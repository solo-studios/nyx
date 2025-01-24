/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file OutcomeMatchers.kt is part of nyx
 * Last modified on 23-01-2025 09:36 p.m.
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

@file:Suppress("unused")

package ca.solostudios.nyx.kotest.matchers

import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome

fun BuildTask?.shouldHaveSucceeded() = this shouldBe TaskOutcome.SUCCESS
fun BuildTask?.shouldNotHaveSucceeded() = this shouldNotBe TaskOutcome.SUCCESS
fun BuildTask?.shouldHaveFailed() = this shouldBe TaskOutcome.FAILED
fun BuildTask?.shouldNotHaveFailed() = this shouldNotBe TaskOutcome.FAILED
fun BuildTask?.shouldBeUpToDate() = this shouldBe TaskOutcome.UP_TO_DATE
fun BuildTask?.shouldNotBeUpToDate() = this shouldNotBe TaskOutcome.UP_TO_DATE
fun BuildTask?.shouldBeSkipped() = this shouldBe TaskOutcome.SKIPPED
fun BuildTask?.shouldNotBeSkipped() = this shouldNotBe TaskOutcome.SKIPPED
fun BuildTask?.shouldBeFromCache() = this shouldBe TaskOutcome.FROM_CACHE
fun BuildTask?.shouldNotBeFromCache() = this shouldNotBe TaskOutcome.FROM_CACHE
fun BuildTask?.shouldHaveNoSource() = this shouldBe TaskOutcome.NO_SOURCE
fun BuildTask?.shouldNotHaveNoSource() = this shouldNotBe TaskOutcome.NO_SOURCE

infix fun BuildTask?.shouldBe(outcome: TaskOutcome) = this should beOutcome(outcome)
infix fun BuildTask?.shouldNotBe(outcome: TaskOutcome) = this shouldNot beOutcome(outcome)

fun beOutcome(outcome: TaskOutcome) = neverNullMatcher<BuildTask> { task ->
    MatcherResult(
        task.outcome == outcome,
        { "The task ${task.path} should be $outcome but was ${task.outcome}" },
        { "The task ${task.path} should not be $outcome but was ${task.outcome}" })
}
