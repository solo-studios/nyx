/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxCompilePlugin.kt is part of nyx
 * Last modified on 13-03-2025 04:19 p.m.
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

package ca.solostudios.nyx.plugin.compile

import ca.solostudios.kspservice.annotation.Service
import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.internal.InternalNyxPlugin
import ca.solostudios.nyx.internal.util.create
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import java.util.concurrent.atomic.AtomicBoolean

@Service(InternalNyxPlugin::class)
internal class NyxCompilePlugin : InternalNyxPlugin {
    override fun apply(project: Project) {
        val nyxExtension = project.the<NyxExtension>()
        val compileExtension = nyxExtension.create<NyxCompileExtension>(NyxCompileExtension.NAME, project)

        project.afterEvaluate {
            compileExtension.configureProject()
        }

        project.pluginManager.withPlugin("java") {
            val javaExtension = compileExtension.create<NyxJavaExtension>(NyxJavaExtension.NAME, project, compileExtension)

            project.afterEvaluate {
                javaExtension.configureProject()
            }
        }

        val appliedKotlinPlugin = AtomicBoolean(false)
        for (agpPluginId in KOTLIN_PLUGIN_IDS) {
            project.pluginManager.withPlugin(agpPluginId) {
                if (!appliedKotlinPlugin.getAndSet(true)) {
                    applyKotlinExtension(project, compileExtension)
                }
            }
        }
    }

    fun applyKotlinExtension(project: Project, compileExtension: NyxCompileExtension) {
        val kotlinExtension = compileExtension.create<NyxKotlinExtension>(NyxKotlinExtension.NAME, project, compileExtension)

        project.afterEvaluate {
            kotlinExtension.configureProject()
        }
    }

    companion object {
        internal val KOTLIN_PLUGIN_IDS = listOf(
            "kotlin",
            "kotlin-android",
            "kotlin-multiplatform",
            "org.jetbrains.kotlin.android",
            "org.jetbrains.kotlin.js",
            "org.jetbrains.kotlin.jvm",
            "org.jetbrains.kotlin.multiplatform",
        )
    }
}
