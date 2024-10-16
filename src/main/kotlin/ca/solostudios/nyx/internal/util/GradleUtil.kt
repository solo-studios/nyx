/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradleUtil.kt is part of nyx
 * Last modified on 15-10-2024 09:17 p.m.
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
import ca.solostudios.nyx.plugin.minecraft.AbstractMinecraftExtension
import ca.solostudios.nyx.plugin.publish.NyxPublishingExtension
import ca.solostudios.nyx.plugin.publish.release.NyxGithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.modrinth.minotaur.ModrinthExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.configuration.FabricApiExtension
import net.neoforged.gradle.dsl.common.extensions.Minecraft
import net.neoforged.gradle.dsl.common.runs.run.RunManager
import net.neoforged.gradle.dsl.mixin.extension.Mixin
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.gradle.kotlin.dsl.ExistingDomainObjectDelegate
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
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

// @formatter:off
internal fun HasProject.plugins(block: PluginContainer.() -> Unit) = project.plugins.apply(block)
internal val HasProject.plugins: PluginContainer
    get() = project.plugins

internal fun HasProject.layout(block: ProjectLayout.() -> Unit) = project.layout.apply(block)
internal val HasProject.layout: ProjectLayout
    get() = project.layout

internal fun HasProject.tasks(block: TaskContainer.() -> Unit) = project.tasks.apply(block)
internal fun Task.tasks(block: TaskContainer.() -> Unit) = project.tasks.apply(block)
internal val HasProject.tasks: TaskContainer
    get() = project.tasks

internal fun HasProject.java(block: JavaPluginExtension.() -> Unit) = project.configure<JavaPluginExtension>(block)
internal val HasProject.java: JavaPluginExtension
    get() = project.the<JavaPluginExtension>()

internal fun HasProject.kotlin(block: KotlinProjectExtension.() -> Unit) = project.configure<KotlinProjectExtension>(block)
internal val HasProject.kotlin: KotlinProjectExtension
    get() = project.the<KotlinProjectExtension>()

internal fun HasProject.publishing(block: PublishingExtension.() -> Unit) = project.configure<PublishingExtension>(block)
internal fun Project.publishing(block: PublishingExtension.() -> Unit) = project.configure<PublishingExtension>(block)
internal val HasProject.publishing: PublishingExtension
    get() = project.the<PublishingExtension>()

internal fun HasProject.signing(block: SigningExtension.() -> Unit) = project.configure<SigningExtension>(block)
internal val HasProject.signing: SigningExtension
    get() = project.the<SigningExtension>()

internal fun HasProject.loom(block: LoomGradleExtensionAPI.() -> Unit) = project.configure<LoomGradleExtensionAPI>(block)
internal val HasProject.loom: LoomGradleExtensionAPI
    get() = project.the<LoomGradleExtensionAPI>()

internal fun HasProject.fabricApi(block: FabricApiExtension.() -> Unit) = project.configure<FabricApiExtension>(block)
internal val HasProject.fabricApi: FabricApiExtension
    get() = project.the<FabricApiExtension>()

internal fun HasProject.neoMinecraft(block: NeoMinecraft.() -> Unit) = project.configure<Minecraft>(block)
internal val HasProject.neoMinecraft: NeoMinecraft
    get() = project.the<NeoMinecraft>()

internal fun HasProject.neoRuns(block: RunManager.() -> Unit) = project.extensions.configure<RunManager>(block)
internal val HasProject.neoRuns: RunManager
    get() = project.the<RunManager>()

internal fun HasProject.neoMixins(block: NeoMixin.() -> Unit) = project.configure<Mixin>(block)
internal val HasProject.neoMixins: NeoMixin
    get() = project.the<NeoMixin>()

internal fun HasProject.artifacts(block: ArtifactHandler.() -> Unit) = project.artifacts(block)
internal val HasProject.artifacts: ArtifactHandler
    get() = project.artifacts

internal fun HasProject.sourceSets(block: SourceSetContainer.() -> Unit) = project.configure<SourceSetContainer>(block)
internal val HasProject.sourceSets: SourceSetContainer
    get() = project.the<SourceSetContainer>()

internal fun HasProject.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)
internal fun Project.configurations(block: ConfigurationContainer.() -> Unit) = project.configurations.apply(block)
internal val HasProject.configurations: ConfigurationContainer
    get() = project.configurations

internal fun HasProject.modrinth(block: ModrinthExtension.() -> Unit) = project.configure<ModrinthExtension>(block)
internal val HasProject.modrinth: ModrinthExtension
    get() = project.the<ModrinthExtension>()

internal fun HasProject.githubRelease(block: GithubReleaseExtension.() -> Unit) = project.configure<GithubReleaseExtension>(block)
internal val HasProject.githubRelease: GithubReleaseExtension
    get() = project.the<GithubReleaseExtension>()

internal fun HasProject.nyx(block: NyxExtension.() -> Unit) = project.configure<NyxExtension>(block)
internal val HasProject.nyx: NyxExtension
    get() = project.the<NyxExtension>()
internal val Project.nyx: NyxExtension
    get() = project.the<NyxExtension>()

internal fun NyxExtension.minecraft(block: AbstractMinecraftExtension.() -> Unit) = project.configure<AbstractMinecraftExtension>(block)
internal val NyxExtension.minecraft: AbstractMinecraftExtension
    get() = (this as ExtensionAware).the<AbstractMinecraftExtension>()

internal fun NyxExtension.publishing(block: NyxPublishingExtension.() -> Unit) = project.configure<NyxPublishingExtension>(block)
internal val NyxExtension.publishing: NyxPublishingExtension
    get() = (this as ExtensionAware).the<NyxPublishingExtension>()

internal fun NyxPublishingExtension.githubRelease(block: NyxGithubReleaseExtension.() -> Unit) = project.configure<NyxGithubReleaseExtension>(block)
internal val NyxPublishingExtension.githubRelease: NyxGithubReleaseExtension
    get() = (this as ExtensionAware).the<NyxGithubReleaseExtension>()

internal fun <T : Any> NamedDomainObjectContainer<T>.maybeRegister(name: String, action: T.() -> Unit = {}) = when {
    findByName(name) != null -> named(name, action)
    else -> register(name, action)
}

internal fun <U : T, T : Any> PolymorphicDomainObjectContainer<T>.maybeRegister(name: String, type: KClass<U>, action: T.() -> Unit = {}) = when {
    findByName(name) != null -> named(name, type, action)
    else -> register(name, type, action)
}

internal operator fun <T> NamedDomainObjectSet<T>.contains(name: String) = findByName(name) != null

internal val <T : Any> NamedDomainObjectContainer<T>.maybeRegistering
    get() = maybeRegistering {  }
internal fun <T : Any> NamedDomainObjectContainer<T>.maybeRegistering(configuration: T.() -> Unit) = MaybeCreatingDomainObjectDelegate.of(this, configuration)
internal fun <T : Any, U : T> PolymorphicDomainObjectContainer<T>.maybeRegistering(type: KClass<U>) = MaybeCreatingDomainObjectDelegateWithType.of(this, type) {  }
internal fun <T : Any, U : T> PolymorphicDomainObjectContainer<T>.maybeRegistering(type: Class<U>) = maybeRegistering(type.kotlin) {  }
internal fun <T : Any, U : T> PolymorphicDomainObjectContainer<T>.maybeRegistering(type: KClass<U>, configuration: U.() -> Unit) = MaybeCreatingDomainObjectDelegateWithType.of(this, type, configuration)
internal fun <T : Any, U : T> PolymorphicDomainObjectContainer<T>.maybeRegistering(type: Class<U>, configuration: U.() -> Unit) = maybeRegistering(type.kotlin, configuration)

// @formatter:on

internal class MaybeCreatingDomainObjectDelegate<T : Any> private constructor(
    val container: NamedDomainObjectContainer<T>,
    val action: T.() -> Unit,
) {
    operator fun provideDelegate(
        receiver: Any?,
        property: KProperty<*>,
    ): ExistingDomainObjectDelegate<NamedDomainObjectProvider<T>> {
        val name = property.name
        val found = container.findByName(name) != null
        return ExistingDomainObjectDelegate.of(
            when {
                found -> container.named(name, action)
                else -> container.register(name, action)
            }
        )
    }

    companion object {
        fun <T : Any> of(
            container: NamedDomainObjectContainer<T>,
            action: T.() -> Unit,
        ) = MaybeCreatingDomainObjectDelegate(container, action)
    }
}


internal class MaybeCreatingDomainObjectDelegateWithType<T : Any, U : T> private constructor(
    val container: PolymorphicDomainObjectContainer<T>,
    val type: KClass<U>,
    val action: U.() -> Unit,
) {
    operator fun provideDelegate(
        receiver: Any?,
        property: KProperty<*>,
    ): ExistingDomainObjectDelegate<NamedDomainObjectProvider<U>> {
        val name = property.name
        val found = container.findByName(name) != null
        return ExistingDomainObjectDelegate.of(
            when {
                found -> container.named(property.name, type, action)
                else -> container.register(property.name, type, action)
            }
        )
    }

    companion object {
        fun <T : Any, U : T> of(
            container: PolymorphicDomainObjectContainer<T>,
            type: KClass<U>,
            action: U.() -> Unit,
        ) = MaybeCreatingDomainObjectDelegateWithType(container, type, action)
    }
}

internal inline fun <reified T> HasProject.newInstance(vararg parameters: Any): T = project.objects.newInstance(*parameters)
