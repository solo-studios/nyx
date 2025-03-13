/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxMinecraftPlugin.kt is part of nyx
 * Last modified on 13-03-2025 06:59 p.m.
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
 * NYX IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.solostudios.nyx.plugin.minecraft

import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.internal.InternalNyxPlugin
import ca.solostudios.nyx.internal.util.configurations
import ca.solostudios.nyx.internal.util.create
import ca.solostudios.nyx.internal.util.lowerCamelCaseName
import ca.solostudios.nyx.plugin.minecraft.loom.NyxFabricLoomExtension
import ca.solostudios.nyx.plugin.minecraft.neoforge.NyxNeoGradleExtension
import net.fabricmc.loom.LoomGradlePlugin
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the

internal class NyxMinecraftPlugin : InternalNyxPlugin {
    override fun apply(project: Project) {
        val nyxExtension = project.the<NyxExtension>()
        val minecraftExtension = project.createMinecraftExtension()

        project.pluginManager.withPlugin("com.modrinth.minotaur") {
            val minotaurExtension = minecraftExtension.create<NyxMinotaurExtension>(NyxMinotaurExtension.NAME, project, nyxExtension.info)

            project.afterEvaluate {
                minotaurExtension.configureProject()
            }
        }
    }

    fun Project.createMinecraftExtension(): AbstractMinecraftExtension {
        val nyxExtension = project.the<NyxExtension>()

        val isLoom = try {
            plugins.hasPlugin(LoomGradlePluginBootstrap::class)
        } catch (_: NoClassDefFoundError) {
            try {
                plugins.hasPlugin(LoomGradlePlugin::class)
            } catch (_: NoClassDefFoundError) {
                false
            }
        }

        if (isLoom) {
            addLoomIncludeConfigurations()
        }

        val extensionClass = if (isLoom) NyxFabricLoomExtension::class else NyxNeoGradleExtension::class

        val minecraftExtension = nyxExtension.create(AbstractMinecraftExtension.NAME, extensionClass, project)

        // Only add mixin extension if it's applicable (loom or neogradle mixin)
        if (isLoom || pluginManager.hasPlugin("net.neoforged.gradle.mixin")) {
            val mixinExtension = minecraftExtension.create<NyxMixinExtension>(NyxMixinExtension.NAME, project, minecraftExtension)

            afterEvaluate {
                mixinExtension.configureProject()
            }
        }

        afterEvaluate {
            minecraftExtension.configureProject()
        }

        return minecraftExtension
    }

    private fun Project.addLoomIncludeConfigurations() {
        configurations {
            val include by named("include")

            addInclusionConfigurations(include, "include")

            if (findByName("shadow") != null) {
                val shadow by named("shadow")

                addInclusionConfigurations(shadow, "shadow")
            }
        }
    }

    private fun ConfigurationContainer.addInclusionConfigurations(inclusionConfiguration: Configuration, nameAddition: String) {
        // generate configurations such as
        // apiInclude
        // implementationInclude
        // modApiInclude
        // modImplementationInclude
        matching { it.name.contains("implementation", ignoreCase = true) || it.name.contains("api", ignoreCase = true) }.all {
            if (this.name.contains(nameAddition, ignoreCase = true))
                return@all

            val baseConfiguration = this
            val shadowConfigurationName = lowerCamelCaseName(name, nameAddition)
            register(shadowConfigurationName) {
                inclusionConfiguration.extendsFrom(this)
                baseConfiguration.extendsFrom(this)
            }
        }
    }
}
