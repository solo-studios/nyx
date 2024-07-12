/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxCompilePlugin.kt is part of nyx
 * Last modified on 11-07-2024 08:32 p.m.
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

package ca.solostudios.nyx.plugin.compile

import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.internal.InternalNyxPlugin
import ca.solostudios.nyx.internal.util.create
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin

internal class NyxCompilePlugin : InternalNyxPlugin {
    override fun apply(project: Project) {
        val nyxExtension = project.the<NyxExtension>()
        val compileExtension = nyxExtension.create<NyxCompileExtension>(NyxCompileExtension.NAME, project)

        project.afterEvaluate {
            compileExtension.configureProject()
        }

        project.plugins.withId("java") {
            val javaExtension = compileExtension.create<NyxJavaExtension>(NyxJavaExtension.NAME, project, compileExtension)

            project.afterEvaluate {
                javaExtension.configureProject()
            }
        }

        try {
            project.plugins.withType<KotlinBasePlugin> {
                val kotlinExtension = compileExtension.create<NyxKotlinExtension>(NyxKotlinExtension.NAME, project, compileExtension)

                project.afterEvaluate {
                    kotlinExtension.configureProject()
                }
            }
        } catch (_: NoClassDefFoundError) {
            // ignore
        }
    }
}
