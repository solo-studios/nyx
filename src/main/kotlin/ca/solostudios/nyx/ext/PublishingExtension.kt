package ca.solostudios.nyx.ext

import ca.solostudios.nyx.util.getOrEmpty
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.property
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.plugin.HasProject
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

public open class PublishingExtension(override val project: Project) : HasProject {
    public val publishDependsOnSign: Property<Boolean> = project.objects.property<Boolean>().convention(true)

    public val publish: Property<Boolean> = project.objects.property<Boolean>().convention(false)

    internal fun configureProject(info: ProjectInfo) {
        if (!publish.isPresent || !publish.get())
            return

        project.apply<MavenPublishPlugin>()
        project.apply<SigningPlugin>()

        when {
            project.plugins.hasPlugin(KotlinMultiplatformPlugin::class) -> {
                // handle multiplatform publishing (do not create publication
            }

            else -> {
                // handle default case (create new publication)
            }
        }
    }

    internal fun MavenPublication.configurePublication(info: ProjectInfo) {
        groupId = info.group.getOrEmpty()
        // This will break on kotlin/multiplatform. Fix this.
        artifactId = info.module.getOrEmpty()
        version = info.version.getOrEmpty()

        pom {
            name = info.name
            description = info.description
            url = info.repository.projectUrl

            // inceptionYear = info.inceptionYear

            licenses {
                license {
                    name = info.license.name
                    url = info.license.url
                }
            }

            // developers {
            // }

            issueManagement {
                // system = "GitHub"
                url = info.repository.projectIssues
            }

            scm {
                connection = info.repository.projectCloneBaseUri.map { "scm:git:https://$it" }
                developerConnection = info.repository.projectCloneBaseUri.map { "scm:git:ssh://$it" }
                url = info.repository.projectUrl
            }
        }
    }
}
