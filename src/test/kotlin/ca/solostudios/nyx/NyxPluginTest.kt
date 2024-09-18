/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxPluginTest.kt is part of nyx
 * Last modified on 18-09-2024 12:14 a.m.
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

package ca.solostudios.nyx

import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.project
import ca.solostudios.nyx.util.shouldHavePluginWithId
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class NyxPluginTest : NyxSpec({
    feature("the nyx plugin") {
        given("the class") {
            upon("applying") {
                val project = project {}

                shouldNotThrowAny {
                    project.apply<NyxPlugin>()
                }

                should("apply it") {
                    project.shouldHavePluginWithId<NyxPlugin>("ca.solo-studios.nyx")
                }

                should("add the extension") {
                    project.extensions.findByType<NyxExtension>().shouldNotBeNull()
                }
            }
        }

        given("the id") {
            upon("applying") {
                val project = project {}

                shouldNotThrowAny {
                    project.plugins.apply("ca.solo-studios.nyx")
                }

                should("apply it") {
                    project.shouldHavePluginWithId<NyxPlugin>("ca.solo-studios.nyx")
                }

                should("add the extension") {
                    project.extensions.findByType<NyxExtension>().shouldNotBeNull()
                }
            }
        }
    }
})
