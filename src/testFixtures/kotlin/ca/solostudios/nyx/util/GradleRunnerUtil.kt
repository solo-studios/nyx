/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradleRunnerUtil.kt is part of nyx
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

package ca.solostudios.nyx.util

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Path


fun GradleRunner.addArguments(
    vararg arguments: String,
): GradleRunner = addArguments(arguments.toList())

fun GradleRunner.addArguments(
    arguments: List<String>,
): GradleRunner = withArguments(this.arguments + arguments)

val BuildTask.name: String
    get() = path.substringAfterLast(':')

fun GradleRunner.build(action: BuildResult.() -> Unit): BuildResult = build().also(action)

@Suppress("UnstableApiUsage")
fun GradleRunner.run(action: BuildResult.() -> Unit): BuildResult = run().also(action)

val GradleRunner.buildDir: Path
    get() = projectDir.toPath().resolve("build")
