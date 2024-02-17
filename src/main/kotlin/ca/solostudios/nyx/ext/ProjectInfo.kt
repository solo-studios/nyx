package ca.solostudios.nyx.ext

import ca.solostudios.nyx.util.convention
import ca.solostudios.nyx.util.formatAsName
import ca.solostudios.nyx.util.tasks
import ca.solostudios.nyx.util.toStringOrEmpty
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.HasProject

public open class ProjectInfo(override val project: Project) : HasProject {
    public val name: Property<String> = project.objects.property<String>().convention(project) {
        project.name.formatAsName()
    }

    public val group: Property<String> = project.objects.property<String>().convention(project) {
        project.group.toStringOrEmpty()
    }

    public val module: Property<String> = project.objects.property<String>().convention(project) {
        project.name
    }

    public val version: Property<String> = project.objects.property<String>().convention(project) {
        project.version.toStringOrEmpty()
    }

    public val description: Property<String> = project.objects.property<String>().convention(project) {
        project.description.toStringOrEmpty()
    }

    @Nested
    public val repository: RepositoryInfo = project.objects.newInstance(project)

    @Nested
    public val license: LicenseInfo = project.objects.newInstance(project)

    // fun applyAxionRelease() {
    //     scmVersion {
    //         tag {
    //             prefix = project.name
    //             versionSeparator = "-"
    //         }
    //     }
    //
    //     project.version = scmVersion.version
    // }

    public fun repository(action: Action<RepositoryInfo>) {
        action.execute(repository)
    }

    public fun repository(action: (RepositoryInfo).() -> Unit) {
        repository.apply(action)
    }

    public fun license(action: Action<LicenseInfo>) {
        action.execute(license)
    }

    public fun license(action: (LicenseInfo).() -> Unit) {
        license.apply(action)
    }

    internal fun configureProject() {
        tasks {
            withType<AbstractArchiveTask>().configureEach {
                archiveBaseName = project.name
            }
        }

        project.version = version.get()
        project.group = group.get()
        project.description = description.get()
    }
}
