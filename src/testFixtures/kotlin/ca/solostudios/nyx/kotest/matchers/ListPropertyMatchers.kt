/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ListPropertyMatchers.kt is part of nyx
 * Last modified on 18-09-2024 12:10 a.m.
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

package ca.solostudios.nyx.kotest.matchers

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.collections.shouldExistInOrder
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.collections.shouldMatchInOrder
import io.kotest.matchers.collections.shouldMatchInOrderSubset
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.collections.shouldNotContainAnyOf
import io.kotest.matchers.collections.shouldNotContainExactly
import io.kotest.matchers.collections.shouldNotExistInOrder
import io.kotest.matchers.collections.shouldNotHaveElementAt
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.collections.shouldNotMatchEach
import io.kotest.matchers.collections.shouldNotMatchInOrder
import io.kotest.matchers.collections.shouldNotMatchInOrderSubset
import org.gradle.api.provider.ListProperty

fun <T> ListProperty<T>.shouldHaveElementAt(index: Int, element: T) = this.get().shouldHaveElementAt(index, element)

fun <T> ListProperty<T>.shouldNotHaveElementAt(index: Int, element: T) = this.get().shouldNotHaveElementAt(index, element)

infix fun <T> ListProperty<T>.shouldExist(p: (T) -> Boolean) = this.get() shouldExist p

fun <T> ListProperty<T>.shouldMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldMatchInOrder(assertions.toList())
infix fun <T> ListProperty<T>.shouldMatchInOrder(assertions: List<(T) -> Unit>) = this.get() shouldMatchInOrder assertions
fun <T> ListProperty<T>.shouldNotMatchInOrder(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrder(assertions.toList())
infix fun <T> ListProperty<T>.shouldNotMatchInOrder(assertions: List<(T) -> Unit>) = this.get() shouldNotMatchInOrder assertions

fun <T> ListProperty<T>.shouldMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldMatchInOrderSubset(assertions.toList())
infix fun <T> ListProperty<T>.shouldMatchInOrderSubset(assertions: List<(T) -> Unit>) = this.get() shouldMatchInOrderSubset assertions
fun <T> ListProperty<T>.shouldNotMatchInOrderSubset(vararg assertions: (T) -> Unit) = this.shouldNotMatchInOrderSubset(assertions.toList())
infix fun <T> ListProperty<T>.shouldNotMatchInOrderSubset(assertions: List<(T) -> Unit>) = this.get() shouldNotMatchInOrderSubset assertions

fun <T> ListProperty<T>.shouldMatchEach(vararg assertions: (T) -> Unit) = this.shouldMatchEach(assertions.toList())
infix fun <T> ListProperty<T>.shouldMatchEach(assertions: List<(T) -> Unit>) = this.get() shouldMatchEach assertions
fun <T> ListProperty<T>.shouldMatchEach(expected: List<T>, asserter: (T, T) -> Unit) = this.get().shouldMatchEach(expected, asserter)
fun <T> ListProperty<T>.shouldNotMatchEach(vararg assertions: (T) -> Unit) = this.shouldNotMatchEach(assertions.toList())
infix fun <T> ListProperty<T>.shouldNotMatchEach(assertions: List<(T) -> Unit>) = this.get() shouldNotMatchEach assertions

fun <T> ListProperty<T>.shouldExistInOrder(vararg ps: (T) -> Boolean) = this.shouldExistInOrder(ps.toList())
infix fun <T> ListProperty<T>.shouldExistInOrder(expected: List<(T) -> Boolean>) = this.get() shouldExistInOrder expected
infix fun <T> ListProperty<T>.shouldNotExistInOrder(expected: List<(T) -> Boolean>) = this.get() shouldNotExistInOrder expected


fun <T> ListProperty<T>.shouldContainAnyOf(vararg ts: T) = this.get().shouldContainAnyOf(*ts)
fun <T> ListProperty<T>.shouldNotContainAnyOf(vararg ts: T) = this.get().shouldContainAnyOf(*ts)
infix fun <T> ListProperty<T>.shouldContainAnyOf(ts: List<T>) = this.get() shouldContainAnyOf ts
infix fun <T> ListProperty<T>.shouldNotContainAnyOf(ts: List<T>) = this.get() shouldNotContainAnyOf ts

fun <T> ListProperty<T>.shouldBeEmpty(): List<T> = this.get().shouldBeEmpty().toList()
fun <T> ListProperty<T>.shouldNotBeEmpty(): List<T> = this.get().shouldNotBeEmpty().toList()


infix fun <T> ListProperty<T>.shouldHaveSize(size: Int): List<T> = (this.get() shouldHaveSize size).toList()
infix fun <T> ListProperty<T>.shouldNotHaveSize(size: Int): List<T> = (this.get() shouldNotHaveSize size).toList()

fun <T> ListProperty<T>.shouldContainAll(vararg ts: T) = this.get().shouldContainAll(*ts)
infix fun <T> ListProperty<T>.shouldContainAll(ts: Collection<T>) = this.get() shouldContainAll ts

fun <T> ListProperty<T>.shouldNotContainAll(vararg ts: T) = this.get().shouldNotContainAll(*ts)
infix fun <T> ListProperty<T>.shouldNotContainAll(ts: Collection<T>) = this.get() shouldNotContainAll ts

fun <T> ListProperty<T>.shouldContainExactly(vararg ts: T) = this.get().shouldContainExactly(*ts)
infix fun <T> ListProperty<T>.shouldContainExactly(ts: Collection<T>) = this.get() shouldContainExactly ts

fun <T> ListProperty<T>.shouldNotContainExactly(vararg ts: T) = this.get().shouldNotContainExactly(*ts)
infix fun <T> ListProperty<T>.shouldNotContainExactly(ts: Collection<T>) = this.get() shouldNotContainExactly ts
