package ca.solostudios.nyx.sonatype.publishing

import ca.solostudios.nyx.sonatype.SonatypePublishExtension
import ca.solostudios.nyx.sonatype.SonatypePublishPlugin
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.util.encodeBase64ToString
import com.github.kittinunf.result.getOrElse
import org.apache.maven.artifact.repository.metadata.Metadata
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader
import org.gradle.api.UncheckedIOException
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver
import org.gradle.api.publish.maven.internal.publisher.MavenNormalizedPublication
import org.gradle.api.publish.maven.internal.publisher.MavenPublisher
import org.gradle.internal.Factory
import org.gradle.internal.UncheckedException
import org.gradle.internal.hash.HashFunction
import org.gradle.internal.hash.Hashing
import org.gradle.internal.resource.ExternalResourceName
import org.gradle.internal.resource.ExternalResourceReadResult
import org.gradle.internal.resource.ExternalResourceRepository
import org.gradle.internal.resource.ReadableContent
import org.gradle.internal.resource.local.ByteArrayReadableContent
import org.gradle.internal.resource.local.FileReadableContent
import org.gradle.util.internal.BuildCommencedTimeProvider
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


internal class SonatypeMavenRemotePublisher(
    private val tempDirFactory: Factory<File>,
    private val timeProvider: BuildCommencedTimeProvider,
    private val publishExtension: SonatypePublishExtension,
) : MavenPublisher {
    private val logger by getLogger()

    override fun publish(publication: MavenNormalizedPublication, artifactRepository: MavenArtifactRepository?) {
        val repositoryUrl = artifactRepository!!.url

        artifactRepository as DefaultMavenArtifactRepository

        val protocol = repositoryUrl.scheme.lowercase()
        val transport = artifactRepository.getTransport(protocol)
        val repository = transport.repository

        val credentials = when {
            publishExtension.username.isPresent && publishExtension.password.isPresent -> {
                SonatypeCredentials(publishExtension.username.get(), publishExtension.password.get())
            }

            // If username or password is present, but not both, fail.
            publishExtension.username.isPresent || publishExtension.password.isPresent -> {
                // Redact username & passwords, but still show first few characters of password to help debugging.
                val username = if (publishExtension.username.isPresent) publishExtension.username.get().take(3) + "****" else null
                val password = if (publishExtension.password.isPresent) "*******" else null

                error(
                    """
                        Both username and password must be set for the Sonatype publishing extension.
                        Username: $username
                        Password: $password
                    """.trimIndent()
                )
            }

            else -> {
                val passwordCredentials = artifactRepository.configuredCredentials.orNull as? PasswordCredentials
                    ?: error("Only PasswordCredentials are supported for uploading to Sonatype. Please use PasswordCredentials.")
                val username = passwordCredentials.username ?: error("The username must be set for publishing to Sonatype Central.")
                val password = passwordCredentials.password ?: error("The password must be set for publishing to Sonatype Central.")

                SonatypeCredentials(username, password)
            }
        }

        publish(publication, repository, repositoryUrl, credentials)
    }

    private fun publish(
        publication: MavenNormalizedPublication,
        repository: ExternalResourceRepository,
        rootUri: URI,
        credentials: SonatypeCredentials,
    ) {
        val groupId = publication.groupId
        val artifactId = publication.artifactId
        val version = publication.version

        val publisher = ModuleArtifactPublisher(tempDirFactory, rootUri, groupId, artifactId, version, credentials, publishExtension)

        // Use the timestamped version for all published artifacts:
        if (version.isSnapshot)
            publisher.artifactVersion = snapshotVersion(version, repository, publisher.snapshotMetadataLocation)

        publishArtifactsAndMetadata(publication, publisher)

        publisher.finalize()
    }

    private fun publishArtifactsAndMetadata(publication: MavenNormalizedPublication, artifactPublisher: ModuleArtifactPublisher) {
        if (publication.mainArtifact != null)
            artifactPublisher.publish(publication.mainArtifact.file, publication.mainArtifact.extension)

        artifactPublisher.publish(publication.pomArtifact.file, "pom")

        for (artifact in publication.additionalArtifacts)
            artifactPublisher.publish(artifact.file, artifact.extension, artifact.classifier)
    }

    private val String.isSnapshot: Boolean
        get() = if (endsWith(SNAPSHOT_VERSION, ignoreCase = true)) true else VERSION_FILE_PATTERN.matcher(this).matches()

    private fun snapshotVersion(
        version: String,
        repository: ExternalResourceRepository,
        metadataResource: ExternalResourceName,
    ): String {
        val timestamp = timeProvider.formatTimestamp()
        val buildNumber = nextBuildNumber(repository, metadataResource)

        return version.replace(SNAPSHOT_VERSION, "${timestamp}-${buildNumber}")
    }

    private fun nextBuildNumber(repository: ExternalResourceRepository, metadataResource: ExternalResourceName): Int {
        return readExistingMetadata(repository, metadataResource)
            ?.result?.versioning?.snapshot?.buildNumber
            ?.takeIf { it > 0 }
            ?: return 1
    }

    fun readExistingMetadata(
        repository: ExternalResourceRepository,
        metadataResource: ExternalResourceName,
    ): ExternalResourceReadResult<Metadata>? {
        return repository.resource(metadataResource).withContentIfPresent { inputStream: InputStream ->
            try {
                return@withContentIfPresent MetadataXpp3Reader().read(inputStream, false)
            } catch (e: Exception) {
                throw UncheckedException.throwAsUncheckedException(e)
            }
        }
    }

    private class ModuleArtifactPublisher(
        temporaryDirFactory: Factory<File>,
        rootUri: URI,
        groupId: String,
        private val artifactId: String,
        private val moduleVersion: String,
        credentials: SonatypeCredentials,
        private val publishExtension: SonatypePublishExtension,
    ) {
        private val logger by getLogger()

        private val fuel = FuelManager()

        init {
            val username = credentials.username
            val password = credentials.password

            val authorizationToken = "$username:$password".encodeBase64ToString()
            fuel.baseHeaders = mapOf(
                Headers.AUTHORIZATION to "Bearer $authorizationToken",
                Headers.USER_AGENT to SonatypePublishPlugin.USER_AGENT,
            )
        }

        private val groupPath = groupId.replace('.', '/')
        var artifactVersion = moduleVersion

        val outputFile = File(temporaryDirFactory.create(), "central-bundle.zip")
        val outputStream = ZipOutputStream(outputFile.outputStream().buffered()).apply {
            setMethod(ZipOutputStream.DEFLATED)
            setLevel(9)
        }

        private val uploadName = "$groupId:$artifactId:$artifactVersion"

        val snapshotMetadataPath = "$groupPath/$artifactId/$moduleVersion/$METADATA_FILENAME"

        /**
         * Return the location of the snapshot `maven-metadata.xml`, which contains details of the latest published snapshot for a Maven module.
         */
        val snapshotMetadataLocation = ExternalResourceName(rootUri, snapshotMetadataPath)

        /**
         * Publishes a single module artifact, based on classifier and extension.
         */
        fun publish(content: File, extension: String, classifier: String? = null) {
            val path = buildString(128) {
                append(groupPath).append('/')
                append(artifactId).append('/')
                append(moduleVersion).append('/')
                append(artifactId).append('-').append(artifactVersion)

                if (classifier != null)
                    append('-').append(classifier)

                if (extension.isNotEmpty())
                    append('.').append(extension)
            }

            publish(path, content)
        }

        fun publish(resourcePath: String, content: File) {
            putResource(resourcePath, FileReadableContent(content))
            publishChecksums(resourcePath, content)
        }

        private fun publishChecksums(destinationPath: String, content: File) {
            publishChecksum(destinationPath, content, Hashing.sha1())
            publishChecksum(destinationPath, content, Hashing.md5())

            if (!ExternalResourceResolver.disableExtraChecksums()) {
                publishPossiblyUnsupportedChecksum(destinationPath, content, Hashing.sha256())
                publishPossiblyUnsupportedChecksum(destinationPath, content, Hashing.sha512())
            }
        }

        private fun publishPossiblyUnsupportedChecksum(destinationPath: String, content: File, hashFunction: HashFunction) {
            try {
                publishChecksum(destinationPath, content, hashFunction)
            } catch (e: Exception) {
                if (logger.isDebugEnabled) {
                    logger.warn(e) {
                        "Cannot upload checksum for ${content.name} because the remote repository doesn't support $hashFunction. This will not fail the build."
                    }
                } else {
                    logger.warn {
                        "Cannot upload checksum for ${content.name} because the remote repository doesn't support $hashFunction. This will not fail the build."
                    }
                }
            }
        }

        private fun publishChecksum(destinationPath: String, content: File, hashFunction: HashFunction) {
            val checksum = createChecksumFile(content, hashFunction)
            val hashExtension = hashFunction.algorithm.lowercase().replace("-", "")
            putResource(
                "$destinationPath.$hashExtension",
                ByteArrayReadableContent(checksum)
            )
        }

        private fun createChecksumFile(src: File, hashFunction: HashFunction): ByteArray {
            val hash = try {
                hashFunction.hashFile(src)
            } catch (e: IOException) {
                throw UncheckedIOException(e)
            }
            val formattedHashString = hash.toZeroPaddedString(hashFunction.hexDigits)
            return formattedHashString.toByteArray(StandardCharsets.US_ASCII)
        }

        private fun putResource(path: String, readableContent: ReadableContent) {
            val zipEntry = ZipEntry(path)
            outputStream.putNextEntry(zipEntry)

            readableContent.open().use {
                it.copyTo(outputStream)
            }
        }

        fun finalize() {
            outputStream.flush()
            outputStream.close()

            uploadSonatypeZip(outputFile)
        }

        private fun uploadSonatypeZip(zipFile: File) {
            val uploadParameters = listOf(
                "publishingType" to publishExtension.publishingType.get().name,
                "name" to uploadName,
            )

            val (request, response, result) = fuel.upload(SonatypePublishPlugin.SONATYPE_API_UPLOAD_URL, parameters = uploadParameters)
                .add(FileDataPart(zipFile, name = "bundle", contentType = "application/octet-stream"))
                .responseString()

            val resultString = result.getOrElse { e ->
                throw e
            }
        }
    }

    companion object {
        private const val METADATA_FILENAME = "maven-metadata.xml"
        private const val SNAPSHOT_VERSION = "SNAPSHOT"
        private val VERSION_FILE_PATTERN: Pattern = Pattern.compile("^(.*)-([0-9]{8}.[0-9]{6})-([0-9]+)$")
        val utcDateFormatter = SimpleDateFormat("yyyyMMdd.HHmmss").apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        private fun BuildCommencedTimeProvider.formatTimestamp(): String {
            return utcDateFormatter.format(Date(currentTime))
        }
    }

    private data class SonatypeCredentials(
        val username: String,
        val password: String,
    )
}
