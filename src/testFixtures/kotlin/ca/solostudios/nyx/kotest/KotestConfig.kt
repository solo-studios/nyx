/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file KotestConfig.kt is part of nyx
 * Last modified on 17-09-2024 11:15 p.m.
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

package ca.solostudios.nyx.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameCase
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.test.TestCaseOrder
import io.kotest.extensions.allure.AllureTestReporter
import io.kotest.extensions.htmlreporter.HtmlReporter
import io.kotest.extensions.junitxml.JunitXmlReporter

object KotestConfig : AbstractProjectConfig() {
    override val parallelism = if (isIntegrationTest)
        (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1).coerceAtMost(4)
    else
        (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1)

    override val testCaseOrder = TestCaseOrder.Sequential
    override val duplicateTestNameMode = DuplicateTestNameMode.Error
    override val testNameCase = TestNameCase.Lowercase
    override val specExecutionOrder = SpecExecutionOrder.Lexicographic

    override val isolationMode = IsolationMode.InstancePerTest

    override fun extensions() = listOf(
        AllureTestReporter(
            includeContainers = false
        ),
        JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
            outputDir = "test-results/$taskName"
        ),
        HtmlReporter(
            outputDir = "reports/tests/$taskName"
        )
    )

    private val taskName: String
        get() = System.getProperty("gradle.task.name")

    private val isIntegrationTest: Boolean
        get() = taskName.contains("functional", ignoreCase = true)
}
