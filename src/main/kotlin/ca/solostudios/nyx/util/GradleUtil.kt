package ca.solostudios.nyx.util

import ca.solostudios.nyx.api.HasProject
import com.modrinth.minotaur.ModrinthExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Named
import org.gradle.api.artifacts.ConfigurationContainer
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
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal inline fun <reified T> Any.create(name: String, vararg args: Any): T {
    return (this as ExtensionAware).extensions.create(name, T::class.java, *args)
}

internal inline fun <reified T : Named> HasProject.named(name: String): T = project.objects.named(name)

internal fun HasProject.tasks(configure: TaskContainer.() -> Unit) = project.tasks.apply(configure)

internal val HasProject.tasks: TaskContainer
    get() = project.tasks

internal fun HasProject.java(configure: JavaPluginExtension.() -> Unit) = project.configure(configure)

internal fun HasProject.kotlin(configure: KotlinProjectExtension.() -> Unit) = project.configure(configure)

internal fun HasProject.publishing(configure: PublishingExtension.() -> Unit): Unit = project.configure(configure)

internal val HasProject.publishing: PublishingExtension
    get() = project.the<PublishingExtension>()

internal fun HasProject.signing(configure: SigningExtension.() -> Unit) = project.configure(configure)

internal fun HasProject.loom(configure: LoomGradleExtensionAPI.() -> Unit) = project.configure(configure)

internal fun HasProject.artifacts(configure: ArtifactHandler.() -> Unit) = project.artifacts(configure)

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)

internal fun HasProject.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)

internal fun HasProject.modrinth(configure: ModrinthExtension.() -> Unit) = project.configure(configure)

internal val HasProject.modrinth: ModrinthExtension
    get() = project.the<ModrinthExtension>()

internal val HasProject.configurations: ConfigurationContainer
    get() = project.configurations

internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)
