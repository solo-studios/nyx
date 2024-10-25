/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file OutcomeMatchers.kt is part of nyx
 * Last modified on 25-10-2024 07:36 p.m.
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

import io.kotest.matchers.MatcherResult
import io.kotest.matchers.neverNullMatcher
import io.kotest.matchers.should
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome

infix fun BuildTask?.shouldBe(outcome: TaskOutcome) = this should beOutcome(outcome)

fun BuildTask?.shouldHaveSucceeded() = this should beOutcome(TaskOutcome.SUCCESS)
fun BuildTask?.shouldHaveFailed() = this should beOutcome(TaskOutcome.FAILED)
fun BuildTask?.shouldBeUpToDate() = this should beOutcome(TaskOutcome.UP_TO_DATE)
fun BuildTask?.shouldBeSkipped() = this should beOutcome(TaskOutcome.SKIPPED)
fun BuildTask?.shouldBeFromCache() = this should beOutcome(TaskOutcome.FROM_CACHE)
fun BuildTask?.shouldHaveNoSource() = this should beOutcome(TaskOutcome.NO_SOURCE)

fun beOutcome(outcome: TaskOutcome) = neverNullMatcher<BuildTask> { task ->
    MatcherResult(
        task.outcome == outcome,
        { "The task ${task.path} should be $outcome but was ${task.outcome}" },
        { "The task ${task.path} should not be $outcome but was ${task.outcome}" })
}
