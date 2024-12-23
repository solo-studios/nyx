/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ContentFilterableUtilTest.kt is part of nyx
 * Last modified on 22-12-2024 11:01 p.m.
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

import ca.solostudios.nyx.kotest.spec.NyxSpec
import io.kotest.assertions.json.shouldBeValidJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotBeValidJson
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import java.io.StringReader

class ContentFilterableUtilTest : NyxSpec({
    feature("JsonMinifyFilterReader") {
        given("simple json") {
            @Language("JSON")
            val json = """
                {
                    "some simple json": true,
                    "foo":              [
                        "bar"
                    ]
                }
            """.trimIndent()
            val reader = JsonMinifyFilterReader(StringReader(json))

            upon("applying the FilterReader") {
                should("not fail") {
                    shouldNotThrowAny {
                        reader.readText()
                    }
                }

                should("be minified") {
                    val result = reader.readText()
                    result.shouldBeValidJson()
                    result shouldEqualJson json
                    // language=JSON
                    result shouldBe """{"some simple json":true,"foo":["bar"]}"""
                }
            }
        }

        given("broken json") {
            @Language("JSON")
            val json = """
                { "broken": "foo
            """.trimIndent()
            val reader = JsonMinifyFilterReader(StringReader(json))
            upon("applying the FilterReader") {
                should("not fail") {
                    shouldNotThrowAny {
                        reader.readText()
                    }
                }

                should("not have changed") {
                    val result = reader.readText()
                    result.shouldNotBeValidJson()
                    // language=JSON
                    result shouldBe """{ "broken": "foo"""
                }
            }
        }

        given("lenient json") {
            @Language("JSON5")
            val json = """
                {
                    "testing":        true,
                    "trailing comma": [ "foo", "bar", ],
                    // comment
                    unquoted:         "value",
                }
            """.trimIndent()
            val reader = JsonMinifyFilterReader(StringReader(json))
            upon("executing the task") {
                should("not fail") {
                    shouldNotThrowAny {
                        reader.readText()
                    }
                }

                should("be minified") {
                    val result = reader.readText()
                    result.shouldBeValidJson()
                    // language=JSON
                    result shouldBe """{"testing":true,"trailing comma":["foo","bar"],"unquoted":"value"}"""
                }
            }
        }
    }
})
