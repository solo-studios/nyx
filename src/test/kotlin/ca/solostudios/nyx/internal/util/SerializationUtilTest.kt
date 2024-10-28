/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file SerializationUtilTest.kt is part of nyx
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
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.array
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.booleanArray
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.byteArray
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.charArray
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.doubleArray
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.floatArray
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.longArray
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.shortArray
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.triple
import io.kotest.property.arbitrary.uByte
import io.kotest.property.arbitrary.uByteArray
import io.kotest.property.arbitrary.uInt
import io.kotest.property.arbitrary.uIntArray
import io.kotest.property.arbitrary.uLong
import io.kotest.property.arbitrary.uLongArray
import io.kotest.property.arbitrary.uShort
import io.kotest.property.arbitrary.uShortArray
import io.kotest.property.assume
import io.kotest.property.checkAll
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalUnsignedTypes::class)
class SerializationUtilTest : NyxSpec({
    fun asJsonElementString(input: Any?) = Json.encodeToString(input.toJsonElement())

    feature("toJsonElement function") {
        given("a boolean") {
            should("generate the correct json") {
                // language=JSON
                asJsonElementString(true) shouldEqualJson "true"
                // language=JSON
                asJsonElementString(false) shouldEqualJson "false"
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.boolean()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a byte") {
            should("generate the correct json") {
                // language=JSON
                asJsonElementString(1.toByte()) shouldEqualJson "1"

                checkAll(Arb.byte()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.byte()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a character") {
            should("generate the correct json") {
                // language=JSON
                asJsonElementString('a') shouldEqualJson """ "a" """
                // language=JSON
                asJsonElementString('2') shouldEqualJson """ "2" """
                // language=JSON
                asJsonElementString('\\') shouldEqualJson """ "\\" """
                // language=JSON
                asJsonElementString('"') shouldEqualJson """ "\"" """

                checkAll(Arb.char()) { a ->
                    assume {
                        a shouldNotBe '\\'
                        a shouldNotBe '"'
                    }
                    asJsonElementString(a) shouldEqualJson "\"$a\""
                }
            }

            should("match the output of Json.encodeToString") {
                asJsonElementString('a') shouldEqualJson Json.encodeToString('a')
                asJsonElementString('2') shouldEqualJson Json.encodeToString('2')
                asJsonElementString('\\') shouldEqualJson Json.encodeToString('\\')
                asJsonElementString('"') shouldEqualJson Json.encodeToString('"')

                checkAll(Arb.char()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a short") {
            should("generate the correct json") {
                checkAll(Arb.short()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.short()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an integer") {
            should("generate the correct json") {
                checkAll(Arb.int()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.int()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a long") {
            should("generate the correct json") {
                checkAll(Arb.long()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.long()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a float") {
            should("generate the correct json") {
                checkAll(Arb.float(includeNonFiniteEdgeCases = false)) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.float(includeNonFiniteEdgeCases = false)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a double") {
            should("generate the correct json") {
                checkAll(Arb.double(includeNonFiniteEdgeCases = false)) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.double(includeNonFiniteEdgeCases = false)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a unsigned byte") {
            should("generate the correct json") {
                checkAll(Arb.uByte()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uByte()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a unsigned short") {
            should("generate the correct json") {
                checkAll(Arb.uShort()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uShort()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned integer") {
            should("generate the correct json") {
                checkAll(Arb.uInt()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uInt()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned long") {
            should("generate the correct json") {
                checkAll(Arb.uLong()) { a ->
                    asJsonElementString(a) shouldEqualJson "$a"
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uLong()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an enum") {
            should("generate the correct json") {
                // language=JSON
                asJsonElementString(YesNo.YES) shouldEqualJson "\"YES\""
                // language=JSON
                asJsonElementString(YesNo.NO) shouldEqualJson "\"NO\""
            }

            should("match the output of Json.encodeToString") {
                asJsonElementString(YesNo.YES) shouldEqualJson Json.encodeToString(YesNo.YES)
                asJsonElementString(YesNo.NO) shouldEqualJson Json.encodeToString(YesNo.NO)
            }
        }

        given("a string") {
            should("generate the correct json") {
                asJsonElementString("foobar") shouldEqualJson """ "foobar" """
                asJsonElementString("\\") shouldEqualJson """ "\\" """
                asJsonElementString("\"") shouldEqualJson """ "\"" """
                checkAll(Arb.string(codepoints = Codepoint.az())) { a ->
                    assume {
                        a shouldNotContain "\\"
                        a shouldNotContain "\""
                    }
                    asJsonElementString(a) shouldEqualJson """
                        "$a"
                    """.trimIndent()
                }
            }

            // START HERE
            should("match the output of Json.encodeToString") {
                checkAll(Arb.string()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a pair") {
            should("generate the correct json") {
                val input = ("foo" to "bar")

                @Language("JSON")
                val expected = """
                    {
                        "first": "foo",
                        "second": "bar"
                    }
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                val string = Arb.string(codepoints = Codepoint.az())
                checkAll(Arb.pair(string, string)) { a ->
                    // language=JSON
                    asJsonElementString(a) shouldEqualJson """
                        {
                            "first": "${a.first}",
                            "second": "${a.second}"
                        }
                    """.trimIndent()
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.pair(Arb.string(), Arb.string())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a triple") {
            should("generate the correct json") {
                val input = Triple("foo", "bar", "baz")

                @Language("JSON")
                val expected = """
                    {
                        "first": "foo",
                        "second": "bar",
                        "third": "baz"
                    }
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                val string = Arb.string(codepoints = Codepoint.az())
                checkAll(Arb.triple(string, string, string)) { a ->
                    // language=JSON
                    asJsonElementString(a) shouldEqualJson """
                        {
                            "first": "${a.first}",
                            "second": "${a.second}",
                            "third": "${a.third}"
                        }
                    """.trimIndent()
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.triple(Arb.string(), Arb.string(), Arb.string())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("the unit") {
            should("generate the correct json") {
                val input = Unit

                @Language("JSON")
                val expected = """
                    {}
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected
            }

            should("match the output of Json.encodeToString") {
                asJsonElementString(Unit) shouldEqualJson Json.encodeToString(Unit)
            }
        }

        given("a duration") {
            should("generate the correct json") {
                val input = 1.seconds

                @Language("JSON")
                val expected = """
                    "PT1S"
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.duration()) { a ->
                    asJsonElementString(a) shouldEqualJson "\"${a.toIsoString()}\""
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.duration()) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a boolean array") {
            should("generate the correct json") {
                val input = booleanArrayOf(true, false, true)

                @Language("JSON")
                val expected = """
                    [ true, false, true ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.booleanArray(Arb.int(0, 10), Arb.boolean())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.booleanArray(Arb.int(0, 10), Arb.boolean())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a byte array") {
            should("generate the correct json") {
                val input = byteArrayOf(1, 2, 3)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.byteArray(Arb.int(0, 10), Arb.byte())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.byteArray(Arb.int(0, 10), Arb.byte())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a character array") {
            should("generate the correct json") {
                // language=JSON
                asJsonElementString(charArrayOf('1', '2', '3')) shouldEqualJson """
                    [ "1", "2", "3" ]
                """.trimIndent()
                // language=JSON
                asJsonElementString(charArrayOf('\"')) shouldEqualJson """
                    [ "\"" ]
                """.trimIndent()

                checkAll(Arb.charArray(Arb.int(0, 10), Arb.char('A'..'Z', 'a'..'z'))) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.charArray(Arb.int(0, 10), Arb.char())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a short array") {
            should("generate the correct json") {
                val input = shortArrayOf(1, 2, 3)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.shortArray(Arb.int(0, 10), Arb.short())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.shortArray(Arb.int(0, 10), Arb.short())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an integer array") {
            should("generate the correct json") {
                val input = intArrayOf(1, 2, 3)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.intArray(Arb.int(0, 10), Arb.int())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.intArray(Arb.int(0, 10), Arb.int())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a long array") {
            should("generate the correct json") {
                val input = longArrayOf(1, 2, 3)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.longArray(Arb.int(0, 10), Arb.long())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.longArray(Arb.int(0, 10), Arb.long())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a float array") {
            should("generate the correct json") {
                val input = floatArrayOf(1.0f, 1.1f, 1.2f, 1.3f)

                @Language("JSON")
                val expected = """
                    [ 1.0, 1.1, 1.2, 1.3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.floatArray(Arb.int(0, 10), Arb.float(includeNonFiniteEdgeCases = false))) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.floatArray(Arb.int(0, 10), Arb.float(includeNonFiniteEdgeCases = false))) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a double array") {
            should("generate the correct json") {
                val input = doubleArrayOf(1.0, 1.1, 1.2, 1.3)

                @Language("JSON")
                val expected = """
                    [ 1.0, 1.1, 1.2, 1.3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.doubleArray(Arb.int(0, 10), Arb.double(includeNonFiniteEdgeCases = false))) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.doubleArray(Arb.int(0, 10), Arb.double(includeNonFiniteEdgeCases = false))) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned byte array") {
            should("generate the correct json") {
                val input = ubyteArrayOf(1u, 2u, 3u)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.uByteArray(Arb.int(0, 10), Arb.uByte())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uByteArray(Arb.int(0, 10), Arb.uByte())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned short array") {
            should("generate the correct json") {
                val input = ushortArrayOf(1u, 2u, 3u)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.uShortArray(Arb.int(0, 10), Arb.uShort())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uShortArray(Arb.int(0, 10), Arb.uShort())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned integer array") {
            should("generate the correct json") {
                val input = uintArrayOf(1u, 2u, 3u)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.uIntArray(Arb.int(0, 10), Arb.uInt())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uIntArray(Arb.int(0, 10), Arb.uInt())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an unsigned long array") {
            should("generate the correct json") {
                val input = ulongArrayOf(1u, 2u, 3u)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.uLongArray(Arb.int(0, 10), Arb.uLong())) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.uLongArray(Arb.int(0, 10), Arb.uLong())) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an array of integers") {
            should("generate the correct json") {
                val input = arrayOf(1, 2, 3)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.array(Arb.int(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.array(Arb.int(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an array of strings") {
            should("generate the correct json") {
                val input = arrayOf("foo", "bar")

                @Language("JSON")
                val expected = """
                    [ "foo", "bar" ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.array(Arb.string(codepoints = Codepoint.az()), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.array(Arb.string(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("an array of pairs") {
            should("generate the correct json") {
                val input = arrayOf("foo" to "bar", "baz" to "qux")

                @Language("JSON")
                val expected = """
                    [
                        {
                            "first": "foo",
                            "second": "bar"
                        },
                        {
                            "first": "baz",
                            "second": "qux"
                        }
                    ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.array(Arb.string(codepoints = Codepoint.az()), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.array(Arb.string(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a list of longs") {
            should("generate the correct json") {
                val input = listOf(1L, 2L, 3L)

                @Language("JSON")
                val expected = """
                    [ 1, 2, 3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.list(Arb.long(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.list(Arb.long(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a list of doubles") {
            should("generate the correct json") {
                val input = listOf(1.1, 2.2, 3.3)

                @Language("JSON")
                val expected = """
                    [ 1.1, 2.2, 3.3 ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.list(Arb.double(includeNonFiniteEdgeCases = false), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]")
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.list(Arb.double(includeNonFiniteEdgeCases = false), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a list of strings") {
            should("generate the correct json") {
                val input = listOf("foo", "bar")

                @Language("JSON")
                val expected = """
                    [ "foo", "bar" ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.list(Arb.string(codepoints = Codepoint.az()), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.list(Arb.string(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a set of strings") {
            should("generate the correct json") {
                val input = setOf("foo", "bar", "baz")

                @Language("JSON")
                val expected = """
                    [ "foo", "bar", "baz" ]
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.set(Arb.string(codepoints = Codepoint.az()), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.joinToString(prefix = "[", postfix = "]") { "\"$it\"" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.set(Arb.string(), 0..10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a map of strings to ints") {
            should("generate the correct json") {
                val input = mapOf("1" to 1, "2" to 2)

                @Language("JSON")
                val expected = """
                    { "1": 1, "2": 2 }
                """.trimIndent()
                asJsonElementString(input) shouldEqualJson expected

                checkAll(Arb.map(Arb.string(codepoints = Codepoint.az()), Arb.int(), maxSize = 10)) { a ->
                    asJsonElementString(a) shouldEqualJson a.toList().joinToString(prefix = "{", postfix = "}") { (k, v) -> "\"$k\": $v" }
                }
            }

            should("match the output of Json.encodeToString") {
                checkAll(Arb.map(Arb.string(), Arb.int(), maxSize = 10)) { a ->
                    asJsonElementString(a) shouldEqualJson Json.encodeToString(a)
                }
            }
        }

        given("a complex object") {
            val input = mapOf(
                "bool" to true,
                "byte" to 1.toByte(),
                "char" to '2',
                "short" to 3.toShort(),
                "int" to 4,
                "long" to 5.toLong(),
                "float" to 1.2f,
                "double" to 1.2,
                "ubyte" to 0.toUByte(),
                "ushort" to 1.toUShort(),
                "uint" to 2u,
                "ulong" to 3uL,
                "enum" to YesNo.YES,
                "pair" to ("foo" to "bar"),
                "triple" to Triple("foo", "bar", "baz"),
                "unit" to Unit,
                "duration" to 1.seconds,
                "boolArray" to booleanArrayOf(true, false, true),
                "byteArray" to byteArrayOf(1, 2, 3),
                "charArray" to charArrayOf('1', '2', '3'),
                "shortArray" to shortArrayOf(1, 2, 3),
                "intArray" to intArrayOf(1, 2, 3),
                "longArray" to longArrayOf(1, 2, 3),
                "floatArray" to floatArrayOf(1.0f, 1.1f, 1.2f, 1.3f),
                "doubleArray" to doubleArrayOf(1.0, 1.1, 1.2, 1.3),
                "ubyteArray" to ubyteArrayOf(1u, 2u, 3u),
                "ushortArray" to ushortArrayOf(1u, 2u, 3u),
                "uintArray" to uintArrayOf(1u, 2u, 3u),
                "ulongArray" to ulongArrayOf(1u, 2u, 3u),
                "arrayOfInt" to arrayOf(1, 2, 3),
                "arrayOfString" to arrayOf("foo", "bar"),
                "listOfDouble" to listOf(1.1, 2.2, 3.3),
                "listOfString" to listOf("foo", "bar"),
                "setOfString" to setOf("foo", "bar", "baz"),
                "mapOfStringInt" to mapOf("1" to 1, "2" to 2),
            )

            @Language("JSON")
            val expected = """
                {
                    "bool": true,
                    "byte": 1,
                    "char": "2",
                    "short": 3,
                    "int": 4,
                    "long": 5,
                    "float": 1.2,
                    "double": 1.2,
                    "ubyte": 0,
                    "ushort": 1,
                    "uint": 2,
                    "ulong": 3,
                    "enum": "YES",
                    "pair": {
                        "first": "foo",
                        "second": "bar"
                    },
                    "triple": {
                        "first": "foo",
                        "second": "bar",
                        "third": "baz"
                    },
                    "unit": {},
                    "duration": "PT1S",
                    "boolArray": [ true, false, true ],
                    "byteArray": [ 1, 2, 3 ],
                    "charArray": [ "1", "2", "3" ],
                    "shortArray": [ 1, 2, 3 ],
                    "intArray": [ 1, 2, 3 ],
                    "longArray": [ 1, 2, 3 ],
                    "floatArray": [ 1.0, 1.1, 1.2, 1.3 ],
                    "doubleArray": [ 1.0, 1.1, 1.2, 1.3 ],
                    "ubyteArray": [ 1, 2, 3 ],
                    "ushortArray": [ 1, 2, 3 ],
                    "uintArray": [ 1, 2, 3 ],
                    "ulongArray": [ 1, 2, 3 ],
                    "arrayOfInt": [ 1, 2, 3 ],
                    "arrayOfString": [ "foo", "bar" ],
                    "listOfDouble": [ 1.1, 2.2, 3.3 ],
                    "listOfString": [ "foo", "bar" ],
                    "setOfString": [ "foo", "bar", "baz" ],
                    "mapOfStringInt": {
                        "1": 1,
                        "2": 2
                    }
                }
            """.trimIndent()

            should("generate the correct json") {
                asJsonElementString(input) shouldEqualJson expected
            }
        }
    }
}) {
    enum class YesNo {
        YES, NO
    }
}
