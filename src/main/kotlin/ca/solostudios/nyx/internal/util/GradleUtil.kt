/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradleUtil.kt is part of nyx
 * Last modified on 10-06-2024 03:21 p.m.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * GRADLE-CONVENTIONS-PLUGIN IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.solostudios.nyx.internal.util

import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.plugin.publish.release.NyxGithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.modrinth.minotaur.ModrinthExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.configuration.FabricApiExtension
import net.neoforged.gradle.common.util.constants.RunsConstants
import net.neoforged.gradle.dsl.common.runs.run.Run
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import kotlin.reflect.KClass
import ca.solostudios.nyx.plugin.publish.PublishingExtension as NyxPublishingExtension
import net.neoforged.gradle.dsl.common.extensions.Minecraft as NeoMinecraft
import net.neoforged.gradle.dsl.mixin.extension.Mixin as NeoMixin

internal inline fun <reified T : Plugin<*>, V : Plugin<*>> PluginContainer.applyWithType(pluginType: KClass<V>) {
    withType<T> {
        apply(pluginType)
    }
}

internal inline fun <reified T> Any.create(name: String, vararg args: Any): T {
    return (this as ExtensionAware).extensions.create(name, T::class.java, *args)
}

internal fun <T : Any> Any.create(name: String, kClass: KClass<T>, vararg args: Any): T {
    return (this as ExtensionAware).extensions.create(name, kClass.java, *args)
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

internal fun HasProject.fabricApi(block: FabricApiExtension.() -> Unit) = project.configure(block)

internal fun HasProject.neoMinecraft(block: NeoMinecraft.() -> Unit) = project.configure(block)
internal val HasProject.neoMinecraft: NeoMinecraft
    get() = project.the<NeoMinecraft>()

@Suppress("UNCHECKED_CAST")
internal val HasProject.neoRuns: NamedDomainObjectContainer<Run>
    get() = project.extensions.getByName(RunsConstants.Extensions.RUNS) as NamedDomainObjectContainer<Run>

internal fun HasProject.neoMixins(block: NeoMixin.() -> Unit) = project.configure(block)

internal val HasProject.neoMixins: NeoMixin
    get() = project.the<NeoMixin>()

internal fun HasProject.artifacts(block: ArtifactHandler.() -> Unit) = project.artifacts(block)

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)

internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal fun HasProject.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)

internal fun Project.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)

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

internal val NyxExtension.publishing: NyxPublishingExtension
    get() = (this as ExtensionAware).the<NyxPublishingExtension>()

internal val NyxPublishingExtension.githubRelease: NyxGithubReleaseExtension
    get() = (this as ExtensionAware).the<NyxGithubReleaseExtension>()

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)
