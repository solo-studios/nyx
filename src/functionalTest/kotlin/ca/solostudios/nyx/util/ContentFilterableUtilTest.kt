/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ContentFilterableUtilTest.kt is part of nyx
 * Last modified on 21-12-2024 02:33 p.m.
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

package ca.solostudios.nyx.util

import ca.solostudios.nyx.kotest.matchers.shouldBeValidJson
import ca.solostudios.nyx.kotest.matchers.shouldEqualJson
import ca.solostudios.nyx.kotest.matchers.shouldHaveSucceeded
import ca.solostudios.nyx.kotest.spec.NyxSpec
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.paths.shouldContainFile
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.readText
import kotlin.io.path.writeText

class ContentFilterableUtilTest : NyxSpec({
    feature("minify json") {
        given("a simple json file") {
            @Language("JSON")
            val json = """
                {
                    "some simple json": true,
                    "foo": [
                        "bar"
                    ]
                }
            """.trimIndent()
            val project = gradleKtsProject(withPluginClasspath = true) {
                writeBuildGradleKts(
                    """
                        |import ca.solostudios.nyx.util.minifyJson
                        |
                        |plugins {
                        |    java
                        |    id("ca.solo-studios.nyx")
                        |}
                        |
                        |tasks {
                        |    processResources {
                        |        filesMatching("*.json") {
                        |            minifyJson()
                        |        }
                        |    }
                        |}
                    """.trimMargin()
                )

                resolve("src/main/resources").createDirectories().resolve("test.json").createFile().writeText(json)
            }
            upon("executing the task") {
                val runner = project.gradleRunner {
                    addArguments("processResources")
                }

                should("not fail") {
                    val build = shouldNotThrowAny {
                        runner.build()
                    }
                    build.task(":processResources").shouldHaveSucceeded()
                }

                should("generate the expected output") {
                    runner.build()

                    val jsonFile = runner.buildDir.resolve("resources/main/test.json")
                    jsonFile.parent.shouldExist()
                    jsonFile.parent.shouldNotBeEmptyDirectory()
                    jsonFile.parent shouldContainFile "test.json"

                    jsonFile.shouldBeValidJson()
                    jsonFile shouldEqualJson json
                    // language=JSON
                    jsonFile.readText() shouldBe """{"some simple json":true,"foo":["bar"]}"""
                }
            }
        }
    }
})
