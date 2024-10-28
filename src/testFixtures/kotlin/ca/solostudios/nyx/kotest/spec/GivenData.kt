/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GivenData.kt is part of nyx
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
@file:OptIn(ExperimentalKotest::class, ExperimentalTypeInference::class)

package ca.solostudios.nyx.kotest.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.getStableIdentifier
import kotlin.experimental.ExperimentalTypeInference

// we need first and second to help the compiler disambiguate
suspend fun <T> NyxGivenScope.givenData(
    first: T,
    second: T,
    vararg rest: T,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = givenData(listOf(first, second) + rest, test)

suspend fun <T> NyxGivenScope.givenData(
    ts: Sequence<T>,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = givenData(ts.toList(), test)

suspend fun <T> NyxGivenScope.givenData(
    ts: Iterable<T>,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = givenData({ stableIdentifier(it) }, ts, test)

suspend fun <T> NyxGivenScope.givenData(
    nameFn: (T) -> String,
    ts: Sequence<T>,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = givenData(nameFn, ts.toList(), test)

// we need first and second to help the compiler disambiguate
suspend fun <T> NyxGivenScope.givenData(
    nameFn: (T) -> String,
    first: T,
    second: T,
    vararg rest: T,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = givenData(nameFn, listOf(first, second) + rest, test)

suspend fun <T> NyxGivenScope.givenData(
    nameFn: (T) -> String,
    @BuilderInference ts: Iterable<T>,
    @BuilderInference test: suspend NyxSpecGivenContainerScope.(T) -> Unit
) = ts.forEach { t ->
    given(nameFn(t)) {
        test(t)
    }
}

@JvmName("withDataMap")
suspend fun <T> NyxGivenScope.givenData(
    data: Map<String, T>,
    test: suspend NyxSpecGivenContainerScope.(T) -> Unit,
) = data.forEach { (name, t) ->
    given(name) {
        test(t)
    }
}

fun <K, V> Map<K, V>.toInputExpected(): List<InputExpected<K, V>> = map { InputExpected(it.key, it.value) }
fun <K, V> inputExpected(input: K, expected: V) = InputExpected(input, expected)

data class InputExpected<K, V>(
    val input: K,
    val expected: V
) : WithDataTestName {
    override fun dataTestName(): String {
        return "the input " + stableIdentifier(this.input)
    }
}

private fun stableIdentifier(input: Any?): String {
    return when (input) {
        null        -> "<null>"
        is Unit     -> "Unit"
        is Array<*> -> input.joinToString(prefix = "[", postfix = "]") { stableIdentifier(it) }
        is List<*>  -> input.joinToString(prefix = "[", postfix = "]") { stableIdentifier(it) }
        else        -> "'%s'".format(getStableIdentifier(input))
    }
}
