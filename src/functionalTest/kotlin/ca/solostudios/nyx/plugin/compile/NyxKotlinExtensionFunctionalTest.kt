/*
 * Copyright (c) 2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxKotlinExtensionFunctionalTest.kt is part of nyx
 * Last modified on 08-03-2025 06:32 p.m.
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

import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.addArguments
import ca.solostudios.nyx.util.gradleKtsProject

class NyxKotlinExtensionFunctionalTest : NyxSpec({
    feature("the nyx plugin") {
        given("it is loaded with the kotlin plugin") {
            val project = gradleKtsProject(withPluginClasspath = true) {
                writeBuildGradleKts(
                    """
                        |plugins {
                        |    java
                        |    kotlin("jvm")
                        |    id("ca.solo-studios.nyx")
                        |}
                        |
                        |nyx.compile.jvmTarget = 11
                        |nyx.compile.kotlin.apiVersion = "2.0"
                    """.trimMargin()
                )
            }

            upon("loading the project") {
                project.gradleRunner {
                    addArguments("build")

                    should("not fail") {
                        build()
                    }
                }
            }
        }
    }
})
