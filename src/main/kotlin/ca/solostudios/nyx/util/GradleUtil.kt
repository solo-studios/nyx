package ca.solostudios.nyx.util

import ca.solostudios.nyx.api.HasProject
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

internal inline fun <reified T> Any.create(name: String, vararg args: Any): T {
    return (this as ExtensionAware).extensions.create(name, T::class.java, *args)
}

internal inline fun <reified T : Named> HasProject.named(name: String): T = project.objects.named(name)

internal fun HasProject.tasks(configure: TaskContainer.() -> Unit) = project.tasks.apply(configure)

internal val HasProject.tasks: TaskContainer
    get() = project.tasks

internal fun HasProject.java(configure: JavaPluginExtension.() -> Unit) = project.extensions.configure(configure)

internal fun HasProject.kotlin(configure: KotlinProjectExtension.() -> Unit) = project.kotlinExtension.apply(configure)

internal fun HasProject.publishing(configure: PublishingExtension.() -> Unit): Unit = project.extensions.configure(configure)

internal fun HasProject.loom(configure: LoomGradleExtensionAPI.() -> Unit) = project.configure<LoomGradleExtensionAPI>(configure)

internal fun HasProject.artifacts(configure: ArtifactHandler.() -> Unit) = project.artifacts(configure)

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)

internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)

internal fun Project.findLicenseFile(): File? {
    var project: Project? = this
    while (project != null) {
        val licenseFile = project.file("LICENSE")
        if (licenseFile.exists())
            return licenseFile

        project = project.parent
    }

    return null
}
