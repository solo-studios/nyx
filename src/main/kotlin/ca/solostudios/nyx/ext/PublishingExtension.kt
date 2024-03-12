package ca.solostudios.nyx.ext

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.orEmpty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.publishing
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

public open class PublishingExtension(
    override val project: Project,
    private val projectInfo: ProjectInfoExtension,
) : ConfiguresProject, HasProject {
    public val publishDependsOnSign: Property<Boolean> = property<Boolean>().convention(true)

    public val publish: Property<Boolean> = property<Boolean>().convention(false)

    override fun configureProject() {
        if (!publish.isPresent || !publish.get())
            return

        project.apply<MavenPublishPlugin>()
        project.apply<SigningPlugin>()

        publishing {
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
