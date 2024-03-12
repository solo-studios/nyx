package ca.solostudios.nyx.ext

import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.convention
import ca.solostudios.nyx.util.formatAsName
import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import ca.solostudios.nyx.util.toStringOrEmpty
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType

public class ProjectInfoExtension(override val project: Project) : HasProject {
    public val name: Property<String> = property<String>().convention(project) {
        project.name.formatAsName()
    }

    public val group: Property<String> = property<String>().convention(project) {
        project.group.toStringOrEmpty()
    }

    public val module: Property<String> = property<String>().convention(project) {
        project.name
    }

    public val version: Property<String> = property<String>().convention(project) {
        project.version.toStringOrEmpty()
    }

    public val description: Property<String> = property<String>().convention(project) {
        project.description.toStringOrEmpty()
    }

    public val organizationUrl: Property<String> = property()

    public val organizationName: Property<String> = property()

    public val developers: ListProperty<(MavenPomDeveloper) -> Unit> = listProperty()

    @Nested
    public val repository: RepositoryInfo = RepositoryInfo(project)

    @Nested
    public val license: LicenseInfoExtension = LicenseInfoExtension(project)

    public fun name(name: String) {
        this.name = name
    }

    public fun group(group: String) {
        this.group = group
    }

    public fun module(module: String) {
        this.module = module
    }

    public fun description(description: String) {
        this.description = description
    }

    public fun organizationUrl(organizationUrl: String) {
        this.organizationUrl = organizationUrl
    }

    public fun organizationName(organizationName: String) {
        this.organizationName = organizationName
    }

    public fun developer(developer: (MavenPomDeveloper) -> Unit) {
        this.developers.add(developer)
    }

    public fun repository(action: Action<RepositoryInfo>) {
        action.execute(repository)
    }

    public fun repository(action: (RepositoryInfo).() -> Unit) {
        repository.apply(action)
    }

    public fun license(action: Action<LicenseInfoExtension>) {
        action.execute(license)
    }

    public fun license(action: (LicenseInfoExtension).() -> Unit) {
        license.apply(action)
    }

    internal fun configureProject() {
        tasks {
            withType<AbstractArchiveTask>().configureEach {
                archiveBaseName = project.name
            }
        }

        // update the version/group/description according to the properties
        // if they have not been set, it will just be updating the value with its existing contents.
        project.version = version.get()
        project.group = group.get()
        project.description = description.get()
    }
}
