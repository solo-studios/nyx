/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file StringUtilTest.kt is part of nyx
 * Last modified on 28-10-2024 01:58 a.m.
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

import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.kotest.spec.givenData
import ca.solostudios.nyx.kotest.spec.toInputExpected
import io.kotest.matchers.equals.shouldBeEqual

class StringUtilTest : NyxSpec({
    feature("capitalizeWord function") {
        val testValues = mapOf(
            "test" to "Test",
            "Test" to "Test",
            "1test" to "1test",
            "_test" to "_test",
        ).toInputExpected()

        givenData(testValues) { (input, expected) ->
            should("be equal to $expected") {
                input.capitalizeWord() shouldBeEqual expected
            }
        }
    }

    feature("toStringOrEmpty function") {
        val testValues = mapOf(
            null to "",
            ("foo" to "bar") to "(foo, bar)",
            Unit to "kotlin.Unit",
            "" to "",
        ).toInputExpected()

        givenData(testValues) { (input, expected) ->
            should("be equal to $expected") {
                input.toStringOrEmpty() shouldBeEqual expected
            }
        }
    }

    feature("formatAsName function") {
        val testValues = mapOf(
            "foo" to "Foo",
            "this-is-a-test" to "This Is A Test",
            "test-kt" to "Test Kotlin",
            "snake_case" to "Snake Case",
            "foo_bar-test" to "Foo Bar Test",
            "" to "",
            "Example" to "Example"
        ).toInputExpected()

        givenData(testValues) { (input, expected) ->
            should("be equal to '$expected'") {
                input.formatAsName() shouldBeEqual expected
            }
        }
    }

    feature("lowerCamelCaseName function") {
        val testValues = mapOf(
            arrayOf("foo") to "foo",
            arrayOf("foo", "bar") to "fooBar",
            arrayOf("foo", "bar", "baz") to "fooBarBaz",
            arrayOf("fooBar", "baz") to "fooBarBaz",
            arrayOf("foo", "barBaz") to "fooBarBaz",
        ).toInputExpected()

        givenData(testValues) { (input, expected) ->
            should("be equal to '$expected'") {
                lowerCamelCaseName(*input) shouldBeEqual expected
            }
        }
    }
})
