package ca.solostudios.nyx.sonatype.publishing

import ca.solostudios.nyx.sonatype.SonatypePublishExtension
import ca.solostudios.nyx.util.property
import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.credentials.Credentials
import org.gradle.api.internal.GeneratedSubclasses
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.publish.internal.PublishOperation
import org.gradle.api.publish.maven.internal.publisher.MavenNormalizedPublication
import org.gradle.api.publish.maven.internal.publisher.MavenPublisher
import org.gradle.api.publish.maven.internal.publisher.ValidatingMavenPublisher
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.authentication.Authentication
import org.gradle.internal.event.ListenerManager
import org.gradle.internal.serialization.Cached
import org.gradle.internal.serialization.Transient
import org.gradle.internal.service.ServiceRegistry
import org.gradle.kotlin.dsl.support.get
import org.gradle.kotlin.dsl.the
import org.gradle.util.internal.BuildCommencedTimeProvider
import org.gradle.work.DisableCachingByDefault
import java.io.Serializable
import java.net.URI
import javax.inject.Inject


/**
 * Publishes a [org.gradle.api.publish.maven.MavenPublication] to a [MavenArtifactRepository].
 *
 * @since 1.4
 */
@DisableCachingByDefault(because = "Not worth caching")
public abstract class PublishToSonatypeMavenRepository : AbstractPublishToMaven() {
    private val repository: Transient.Var<DefaultMavenArtifactRepository> = Transient.varOf()
    private val spec: Cached<PublishSpec> = Cached.of { this.computeSpec() }

    @get:Nested
    @get:Optional
    public val credentials: Property<Credentials> = property<Credentials>()

    /**
     * The repository to publish to.
     *
     * @return The repository to publish to
     */
    @Internal
    public fun getRepository(): MavenArtifactRepository? = repository.get()

    @get:Inject
    protected open val listenerManager: ListenerManager
        get() = throw UnsupportedOperationException()

    /**
     * Sets the repository to publish to.
     *
     * @param repository The repository to publish to
     */
    public fun setRepository(repository: MavenArtifactRepository) {
        this.repository.set(repository as DefaultMavenArtifactRepository)
        credentials.set(repository.configuredCredentials)
    }

    @TaskAction
    public fun publish() {
        val spec = spec.get()
        val publication = spec.publication
        val repository = spec.repository.get(services)
        duplicatePublicationTracker.checkCanPublish(publication, repository.url, repository.name)
        doPublish(publication, repository)
    }

    private fun computeSpec(): PublishSpec {
        val publicationInternal = publicationInternal ?: throw InvalidUserDataException("The 'publication' property is required")

        val repository = repository.get() ?: throw InvalidUserDataException("The 'repository' property is required")
        val normalizedPublication = publicationInternal.asNormalisedPublication()
        return PublishSpec(RepositorySpec.of(repository), normalizedPublication)
    }

    private fun doPublish(normalizedPublication: MavenNormalizedPublication, repository: MavenArtifactRepository) {
        object : PublishOperation(normalizedPublication.name, repository.name) {
            override fun publish() {
                validatingMavenPublisher().publish(normalizedPublication, repository)
            }
        }.run()
    }

    private fun validatingMavenPublisher(): MavenPublisher {
        // evil hack
        val timeProvider = BuildCommencedTimeProvider(project.gradle.startParameter)
        return ValidatingMavenPublisher(
            SonatypeMavenRemotePublisher(temporaryDirFactory, timeProvider, project.the<SonatypePublishExtension>())
        )
    }

    internal class PublishSpec(
        val repository: RepositorySpec,
        internal val publication: MavenNormalizedPublication,
    )

    internal sealed interface RepositorySpec {
        fun get(services: ServiceRegistry): MavenArtifactRepository

        class Configured(val repository: DefaultMavenArtifactRepository) : RepositorySpec, Serializable {
            override fun get(services: ServiceRegistry): MavenArtifactRepository {
                return repository
            }

            private fun writeReplace(): Any {
                val credentialsSpec = repository.configuredCredentials.map { it: Credentials ->
                    CredentialsSpec.of(repository.name, it)
                }.getOrNull()
                return DefaultRepositorySpec(
                    repository.name,
                    repository.url,
                    repository.isAllowInsecureProtocol,
                    credentialsSpec,
                    repository.configuredAuthentication
                )
            }
        }

        class DefaultRepositorySpec(
            private val name: String,
            private val repositoryUrl: URI,
            private val allowInsecureProtocol: Boolean,
            private val credentials: CredentialsSpec?,
            private val authentications: Collection<Authentication>,
        ) : RepositorySpec {
            override fun get(services: ServiceRegistry): MavenArtifactRepository {
                val repository = services.get(
                    BaseRepositoryFactory::class.java
                ).createMavenRepository() as DefaultMavenArtifactRepository

                repository.name = name
                repository.url = repositoryUrl
                repository.isAllowInsecureProtocol = allowInsecureProtocol

                if (credentials != null) {
                    val provider = services.get<ProviderFactory>().credentials(credentials.type, name)
                    repository.setConfiguredCredentials(provider.get())
                }
                repository.authentication {
                    addAll(authentications)
                }
                return repository
            }
        }

        class CredentialsSpec private constructor(val identity: String, val type: Class<out Credentials>) {
            companion object {
                fun of(identity: String, credentials: Credentials?): CredentialsSpec {
                    return CredentialsSpec(
                        identity, GeneratedSubclasses.unpackType(
                            credentials!!
                        ) as Class<out Credentials>
                    )
                }
            }
        }

        companion object {
            fun of(repository: DefaultMavenArtifactRepository): RepositorySpec {
                return Configured(repository)
            }
        }
    }
}

