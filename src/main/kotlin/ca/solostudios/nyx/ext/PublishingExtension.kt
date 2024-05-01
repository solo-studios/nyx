package ca.solostudios.nyx.ext

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.ext.project.ProjectInfoExtension
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.orEmpty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.publishing
import ca.solostudios.nyx.util.signing
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

public open class PublishingExtension(
    override val project: Project,
    private val projectInfo: ProjectInfoExtension,
) : ConfiguresProject, HasProject {
    /**
     * Enables publishing
     */
    public val publish: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If the `publish` task should depend on the `sign` task.
     */
    public val publishDependsOnSign: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * Allows for in-memory pgp keys to be used, via setting the following project properties:
     * - `signingKey`
     * - `signingKeyId`
     * - `signingPassword`
     */
    public val allowInMemoryPgpKeys: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * - Makes publish tasks depend on the signing task
     * - Enables in-memory pgp keys
     * - Enables publishing
     */
    public fun configurePublications() {
        publishDependsOnSign = true
        publish = true
        allowInMemoryPgpKeys = true
    }

    /**
     * Configures the repositories used for publishing
     */
    public fun repositories(action: RepositoryHandler.() -> Unit) {
        publishing {
            repositories(action)
        }
    }

    override fun configureProject() {
        if (!publish.isTrue)
            return

        if (allowInMemoryPgpKeys.isTrue) {
            signing {
                // Allow specifying the key, key id, and password via environment variables.
                val signingKey: String? by project
                val signingKeyId: String? by project
                val signingPassword: String? by project

                when {
                    signingKey != null && signingKeyId != null && signingPassword != null -> {
                        useInMemoryPgpKeys(signingKey, signingKeyId, signingPassword)
                    }

                    signingKey != null && signingPassword != null -> {
                        useInMemoryPgpKeys(signingKey, signingPassword)
                    }

                    else -> useGpgCmd()
                }
                sign(publishing.publications)
            }

        }

        project.apply<MavenPublishPlugin>()
        project.apply<SigningPlugin>()

        publishing {
            if (publishDependsOnSign.isTrue) {
                tasks.withType<AbstractPublishToMaven>().configureEach {
                    dependsOn(tasks.withType<Sign>())
                }
            }

            publications {
                when {
                    project.plugins.hasPlugin(KotlinMultiplatformPlugin::class) -> {
                        // when using kotlin multiplatform, publications will be created for you
                        withType<MavenPublication>().configureEach {
                            configurePublication(false)
                        }
                    }

                    else -> {
                        create<MavenPublication>("test") {
                            from(project.components["java"])

                            configurePublication()
                        }
                    }
                }
            }
        }
    }

    internal fun MavenPublication.configurePublication(applyArtifactId: Boolean = true) {
        groupId = projectInfo.group.orEmpty()

        // Only apply artifact if needed (ie. when not multiplatform)
        if (applyArtifactId)
            artifactId = projectInfo.module.orEmpty()

        version = projectInfo.version.orEmpty()

        pom {
            name = projectInfo.name
            description = projectInfo.description
            url = projectInfo.repository.projectUrl

            // inceptionYear = projectInfo.inceptionYear
            organization {
                name = projectInfo.organizationName
                url = projectInfo.organizationUrl
            }

            developers {
                // Add all configured developers
                for (developerCallback in projectInfo.developers.get()) {
                    developer(developerCallback)
                }
            }

            // we assume there is only ever a single license
            licenses {
                license {
                    name = projectInfo.license.name
                    url = projectInfo.license.url
                }
            }

            issueManagement {
                system = projectInfo.repository.issueManagement
                url = projectInfo.repository.projectIssues
            }

            scm {
                connection = projectInfo.repository.projectCloneBaseUri.map { "scm:git:https://$it" }
                developerConnection = projectInfo.repository.projectCloneBaseUri.map { "scm:git:ssh://$it" }
                url = projectInfo.repository.projectUrl
            }
        }
    }
}
