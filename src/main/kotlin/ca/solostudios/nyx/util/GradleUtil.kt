package ca.solostudios.nyx.util

import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.api.HasProject
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
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

internal fun HasProject.tasks(block: TaskContainer.() -> Unit) = project.tasks.apply(block)

internal val HasProject.tasks: TaskContainer
    get() = project.tasks

internal fun HasProject.java(block: JavaPluginExtension.() -> Unit) = project.configure(block)

internal fun HasProject.kotlin(block: KotlinProjectExtension.() -> Unit) = project.configure(block)

internal fun HasProject.publishing(block: PublishingExtension.() -> Unit): Unit = project.configure(block)

internal val HasProject.publishing: PublishingExtension
    get() = project.the<PublishingExtension>()

internal fun HasProject.signing(block: SigningExtension.() -> Unit) = project.configure(block)

internal fun HasProject.loom(block: LoomGradleExtensionAPI.() -> Unit) = project.configure(block)

internal fun HasProject.artifacts(block: ArtifactHandler.() -> Unit) = project.artifacts(block)

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)

internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal fun HasProject.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)

internal val HasProject.configurations: ConfigurationContainer
    get() = project.configurations

internal fun HasProject.modrinth(block: ModrinthExtension.() -> Unit) = project.configure(block)

internal val HasProject.modrinth: ModrinthExtension
    get() = project.the<ModrinthExtension>()

internal fun HasProject.githubRelease(block: GithubReleaseExtension.() -> Unit) = project.configure(block)

internal val HasProject.githubRelease: GithubReleaseExtension
    get() = project.the<GithubReleaseExtension>()

internal val HasProject.nyx: NyxExtension
    get() = project.the<NyxExtension>()

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)
