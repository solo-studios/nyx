/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file JsonMatchers.kt is part of nyx
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

@file:Suppress("unused")

package ca.solostudios.nyx.kotest.matchers

import io.kotest.assertions.json.CompareJsonOptions
import io.kotest.assertions.json.shouldBeEmptyJsonArray
import io.kotest.assertions.json.shouldBeEmptyJsonObject
import io.kotest.assertions.json.shouldBeJsonArray
import io.kotest.assertions.json.shouldBeJsonObject
import io.kotest.assertions.json.shouldBeValidJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.assertions.json.shouldEqualSpecifiedJsonIgnoringOrder
import io.kotest.assertions.json.shouldNotBeJsonArray
import io.kotest.assertions.json.shouldNotBeJsonObject
import io.kotest.assertions.json.shouldNotBeValidJson
import io.kotest.assertions.json.shouldNotEqualJson
import io.kotest.assertions.json.shouldNotEqualSpecifiedJson
import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

// @formatter:off
infix fun Path.shouldEqualJson(expected: String): Path = also { readText() shouldEqualJson expected }
infix fun File.shouldEqualJson(expected: String): File = also { readText() shouldEqualJson expected }
infix fun Path.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path = also { readText() shouldEqualJson configureAndProvideExpected }
infix fun File.shouldEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File = also { readText() shouldEqualJson configureAndProvideExpected }

infix fun Path.shouldNotEqualJson(expected: String): Path = also { readText() shouldNotEqualJson expected }
infix fun File.shouldNotEqualJson(expected: String): File = also { readText() shouldNotEqualJson expected }
infix fun Path.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): Path = also { readText() shouldNotEqualJson configureAndProvideExpected }
infix fun File.shouldNotEqualJson(configureAndProvideExpected: CompareJsonOptions.() -> String): File = also { readText() shouldNotEqualJson configureAndProvideExpected }

infix fun Path.shouldEqualSpecifiedJson(expected: String): Path = also { readText() shouldEqualSpecifiedJson expected }
infix fun File.shouldEqualSpecifiedJson(expected: String): File = also { readText() shouldEqualSpecifiedJson expected }

infix fun Path.shouldEqualSpecifiedJsonIgnoringOrder(expected: String): Path = also { readText() shouldEqualSpecifiedJsonIgnoringOrder expected }
infix fun File.shouldEqualSpecifiedJsonIgnoringOrder(expected: String): File = also { readText() shouldEqualSpecifiedJsonIgnoringOrder expected }

infix fun Path.shouldNotEqualSpecifiedJson(expected: String): Path = also { readText() shouldNotEqualSpecifiedJson expected }
infix fun File.shouldNotEqualSpecifiedJson(expected: String): File = also { readText() shouldNotEqualSpecifiedJson expected }

fun Path.shouldBeEmptyJsonArray(): Path = also { readText().shouldBeEmptyJsonArray() }
fun File.shouldBeEmptyJsonArray(): File = also { readText().shouldBeEmptyJsonArray() }

fun Path.shouldBeJsonArray(): Path = also { readText().shouldBeJsonArray() }
fun File.shouldBeJsonArray(): File = also { readText().shouldBeJsonArray() }

fun Path.shouldNotBeJsonArray(): Path = also { readText().shouldNotBeJsonArray() }
fun File.shouldNotBeJsonArray(): File = also { readText().shouldNotBeJsonArray() }

fun Path.shouldBeEmptyJsonObject(): Path = also { readText().shouldBeEmptyJsonObject() }
fun File.shouldBeEmptyJsonObject(): File = also { readText().shouldBeEmptyJsonObject() }

fun Path.shouldBeJsonObject(): Path = also { readText().shouldBeJsonObject() }
fun File.shouldBeJsonObject(): File = also { readText().shouldBeJsonObject() }

fun Path.shouldNotBeJsonObject(): Path = also { readText().shouldNotBeJsonObject() }
fun File.shouldNotBeJsonObject(): File = also { readText().shouldNotBeJsonObject() }

fun Path.shouldBeValidJson(): Path = also { readText().shouldBeValidJson() }
fun File.shouldBeValidJson(): File = also { readText().shouldBeValidJson() }

fun Path.shouldNotBeValidJson(): Path = also { readText().shouldNotBeValidJson() }
fun File.shouldNotBeValidJson(): File = also { readText().shouldNotBeValidJson() }
