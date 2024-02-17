package ca.solostudios.nyx.util

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Named
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.plugin.HasProject
import java.io.File

internal fun <T> Property<T>.convention(project: Project, provider: () -> T): Property<T> = convention(project.provider(provider))

internal val HasProject.tasks: TaskContainer
    get() = project.tasks

internal fun HasProject.tasks(block: TaskContainer.() -> Unit) = project.tasks.apply(block)
internal fun HasProject.java(block: TaskContainer.() -> Unit) = project.tasks.apply(block)
internal fun HasProject.kotlin(block: TaskContainer.() -> Unit) = project.tasks.apply(block)

internal inline fun <reified T : Named> HasProject.named(name: String): T = project.objects.named(name)

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)

internal inline fun <reified T> HasProject.property(): Property<T> = project.objects.property()

internal inline fun <reified T> HasProject.setProperty(): SetProperty<T> = project.objects.setProperty()

internal inline fun <reified T> HasProject.listProperty(): ListProperty<T> = project.objects.listProperty()

internal inline fun <reified K, reified V> HasProject.mapProperty(): MapProperty<K, V> = project.objects.mapProperty()

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

internal fun Property<String>.getOrEmpty(): String = getOrElse("")

internal fun HasProject.loom(block: LoomGradleExtensionAPI.() -> Unit) = project.configure<LoomGradleExtensionAPI>(block)

internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)
