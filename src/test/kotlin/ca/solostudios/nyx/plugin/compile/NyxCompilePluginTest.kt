/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxCompilePluginTest.kt is part of nyx
 * Last modified on 20-09-2024 03:32 p.m.
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

import ca.solostudios.nyx.NyxPlugin
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.project
import ca.solostudios.nyx.util.shouldHavePlugin
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType

class NyxCompilePluginTest : NyxSpec({
    feature("the nyx compile plugin") {
        given("a project") {
            val project = project {}

            upon("applying the nyx plugin") {
                project.apply<NyxPlugin>()

                should("apply it") {
                    project.shouldHavePlugin<NyxCompilePlugin>()
                }

                should("add the extension") {
                    (project.nyx as ExtensionAware).extensions.findByType<NyxCompileExtension>().shouldNotBeNull()
                }
            }
        }

        given("a project with the java plugin") {
            val project = project {
                plugins.apply("java")
            }

            upon("applying the nyx plugin") {
                project.apply<NyxPlugin>()

                val compile = (project.nyx as ExtensionAware).extensions.getByType<NyxCompileExtension>()

                should("add the java extension") {
                    (compile as ExtensionAware).extensions.findByType<NyxJavaExtension>().shouldNotBeNull()
                }
            }
        }

        given("a project with the kotlin plugin") {
            val project = project {
                plugins.apply("org.jetbrains.kotlin.jvm")
            }

            upon("applying the nyx plugin") {
                project.apply<NyxPlugin>()

                val compile = (project.nyx as ExtensionAware).extensions.getByType<NyxCompileExtension>()

                should("add the kotlin extension") {
                    (compile as ExtensionAware).extensions.findByType<NyxKotlinExtension>().shouldNotBeNull()
                }
            }
        }
    }
})
