package ca.solostudios.nyx.ext

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.ext.project.ProjectInfoExtension
import ca.solostudios.nyx.ext.release.GithubReleaseExtension
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.nyx
import ca.solostudios.nyx.util.orEmpty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.publishing
import ca.solostudios.nyx.util.signing
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

public class PublishingExtension(override val project: Project) : ConfiguresProject, HasProject {
    private val projectInfo: ProjectInfoExtension
        get() = nyx.project

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
     *
     * You can set these globally by adding
     * ```properties
     * signingKey=[key]
     * signingKeyId=[id]
     * signingPassword=[key password]
     * ```
     * in [the `gradle.properties` located in your `$GRADLE_USER_HOME`](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables).
     * By default, this is located at `~/.gradle/gradle.properties` on Linux/MacOS,
     * and `%USERPROFILE%\.gradle\gradle.properties` on Windows.
     */
    public val allowInMemoryPgpKeys: Property<Boolean> = property<Boolean>().convention(true)

    @Nested
    public val github: GithubReleaseExtension = GithubReleaseExtension(project)

    public fun github(action: Action<GithubReleaseExtension>) {
        action.execute(github)
    }

    public fun github(action: (GithubReleaseExtension).() -> Unit) {
        github.apply(action)
    }

    /**
     * - Makes publish tasks depend on the signing task
     * - Enables in-memory pgp keys
     * - Enables publishing
     */
    public fun configurePublications() {
        publish = true
        publishDependsOnSign = true
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

    override fun onLoad() {
        project.plugins.withId("com.github.breadmoirai.github-release") {
            github.onLoad()
        }
    }

    override fun configureProject() {
        project.plugins.withId("com.github.breadmoirai.github-release") {
            github.configureProject()
        }

        if (!publish.isTrue)
            return

        project.apply<MavenPublishPlugin>()
        project.apply<SigningPlugin>()

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
                            configurePublication(applyArtifactId = false)
                        }
                    }

                    project.plugins.hasPlugin(JavaGradlePluginPlugin::class) -> {
                        withType<MavenPublication>().configureEach {
                            configurePublication(applyArtifactId = false)
                        }
                    }

                    else -> {
                        if (withType<MavenPublication>().isEmpty()) {
                            create<MavenPublication>(nyx.project.module.get()) {
                                from(project.components["java"])

                                configurePublication()
                            }
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

            if (projectInfo.description.isPresent)
                description = projectInfo.description
            if (projectInfo.repository.projectUrl.isPresent)
                url = projectInfo.repository.projectUrl

            // inceptionYear = projectInfo.inceptionYear
            if (projectInfo.organizationName.isPresent || projectInfo.organizationUrl.isPresent) {
                organization {
                    if (projectInfo.organizationName.isPresent)
                        name = projectInfo.organizationName
                    if (projectInfo.organizationUrl.isPresent)
                        url = projectInfo.organizationUrl
                }
            }

            developers {
                // Add all configured developers
                for (developerCallback in projectInfo.developers.get()) {
                    developer(developerCallback)
                }
            }

            // we assume there is only ever a single license
            if (projectInfo.license.name.isPresent || projectInfo.license.url.isPresent) {
                licenses {
                    license {
                        if (projectInfo.license.name.isPresent)
                            name = projectInfo.license.name
                        if (projectInfo.license.url.isPresent)
                            url = projectInfo.license.url
                    }
                }
            }

            issueManagement {
                if (projectInfo.repository.issueManagement.isPresent)
                    system = projectInfo.repository.issueManagement
                if (projectInfo.repository.projectIssues.isPresent)
                    url = projectInfo.repository.projectIssues
            }

            scm {
                if (projectInfo.repository.projectCloneScmUri.isPresent)
                    connection = projectInfo.repository.projectCloneScmUri
                if (projectInfo.repository.projectCloneDeveloperUri.isPresent)
                    developerConnection = projectInfo.repository.projectCloneDeveloperUri
                if (projectInfo.repository.projectUrl.isPresent)
                    url = projectInfo.repository.projectUrl
            }
        }
    }
}
