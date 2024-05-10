package ca.solostudios.nyx.ext.mc

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.ext.project.ProjectInfoExtension
import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.modrinth
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.dependencies.container.NamedDependencyContainer
import masecla.modrinth4j.model.version.ProjectVersion
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

public class MinotaurExtension(
    override val project: Project,
    private val projectInfo: ProjectInfoExtension,
) : ConfiguresProject, HasProject {
    /**
     * The modrinth project id.
     */
    public val projectId: Property<String> = property()

    /**
     * The changelog for this release.
     */
    public val changelog: Property<String> = property()

    /**
     * The project version type
     */
    public val versionType: Property<ProjectVersion.VersionType> = property()

    public val gameVersions: ListProperty<String> = listProperty()

    public val failSilently: Property<Boolean> = property()

    public val detectLoaders: Property<Boolean> = property()

    public val autoAddDependsOn: Property<Boolean> = property()

    public fun dependencies(dsl: DependenciesDsl.() -> Unit) {
        DependenciesDsl(project).apply(dsl)
    }

    override fun configureProject() {
        val tokenProperty = project.providers.gradleProperty(MODRINTH_TOKEN_GRADLE_PROPERTY)
        if (tokenProperty.isPresent)
            modrinth.token = tokenProperty
        else
            modrinth.token = System.getenv("MODRINTH_TOKEN")

        if (projectId.isPresent)
            modrinth.projectId = projectId

        if (projectInfo.version.isPresent)
            modrinth.versionNumber.convention(projectInfo.version)

        if (versionType.isPresent)
            modrinth.versionType = versionType.map { it.name }

        if (changelog.isPresent)
            modrinth.changelog = changelog

        if (gameVersions.isPresent)
            modrinth.gameVersions = gameVersions

        if (failSilently.isPresent)
            modrinth.failSilently = failSilently

        if (detectLoaders.isPresent)
            modrinth.detectLoaders = detectLoaders

        if (autoAddDependsOn.isPresent)
            modrinth.autoAddDependsOn = autoAddDependsOn

        // currently only support loom
        // TODO: support neoforge (normal forge can suck my balls)
        if (tasks.findByName("remapJar") != null) {
            val remapJar by tasks.named<Jar>("remapJar")

            modrinth.file = remapJar.archiveFile
            tasks.named<TaskModrinthUpload>("modrinth").configure {
                dependsOn(remapJar)
            }
        }

        project.plugins.withType<PublishingPlugin> {
            val modrinth by tasks.named("modrinth")
            val publish by tasks.named("publish")

            publish.dependsOn(modrinth)
        }
    }

    public class DependenciesDsl(override val project: Project) : HasProject {
        public fun incompatible(projectId: String, versionId: String? = null) {
            dependency(modrinth.incompatible, projectId, versionId)
        }

        public fun optional(projectId: String, versionId: String? = null) {
            dependency(modrinth.optional, projectId, versionId)
        }

        public fun required(projectId: String, versionId: String? = null) {
            dependency(modrinth.required, projectId, versionId)
        }

        public fun embedded(projectId: String, versionId: String? = null) {
            dependency(modrinth.embedded, projectId, versionId)
        }

        private fun dependency(dependencyContainer: NamedDependencyContainer, projectId: String, versionId: String?) {
            if (versionId == null)
                dependencyContainer.project(projectId)
            else
                dependencyContainer.version(projectId, versionId)
        }
    }

    public companion object {
        public const val MODRINTH_TOKEN_GRADLE_PROPERTY: String = "modrinth.token"
    }
}
