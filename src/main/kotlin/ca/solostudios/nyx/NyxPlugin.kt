/*
 * Copyright (c) 2023-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxPlugin.kt is part of nyx
 * Last modified on 25-12-2024 06:22 p.m.
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

package ca.solostudios.nyx

import ca.solostudios.nyx.internal.InternalNyxPlugin
import ca.solostudios.nyx.internal.util.create
import ca.solostudios.nyx.plugin.compile.NyxCompilePlugin
import ca.solostudios.nyx.plugin.minecraft.NyxMinecraftPlugin
import ca.solostudios.nyx.plugin.publish.NyxPublishingPlugin
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType
import net.neoforged.gradle.common.CommonProjectPlugin as NeoGradleCommonProjectPlugin

internal class NyxPlugin : InternalNyxPlugin {
    override fun apply(project: Project) {
        val nyx = project.create<NyxExtension>(NyxExtension.NAME, project)

        project.plugins.apply(NyxPublishingPlugin::class)

        project.plugins.apply(NyxCompilePlugin::class)

        try {
            project.plugins.withType<LoomGradlePluginBootstrap> {
                project.applyMinecraftPlugin()
            }
        } catch (_: NoClassDefFoundError) {
            // ignore
        }

        try {
            project.plugins.withType<NeoGradleCommonProjectPlugin> {
                project.applyMinecraftPlugin()
            }
        } catch (_: NoClassDefFoundError) {
            // ignore
        }

        project.afterEvaluate {
            nyx.configureProject()
        }
    }

    private fun Project.applyMinecraftPlugin() {
        plugins.apply(NyxMinecraftPlugin::class)
    }
}
