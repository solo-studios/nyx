/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradleRunnerBuilder.kt is part of nyx
 * Last modified on 17-09-2024 12:52 a.m.
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

import ca.solostudios.nyx.kotest.createTmpDir
import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.appendText
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import kotlin.time.Duration.Companion.seconds

fun gradleKtsProject(
    baseDir: Path = createTmpDir("gradle-project-template-", delete = false),
    build: GradleRunnerBuilder.() -> Unit,
): GradleRunnerBuilder {
    return GradleRunnerBuilder(baseDir).apply(build)
}

class GradleRunnerBuilder(
    val projectDir: Path,
) {
    fun writeBuildGradleKts(@Language("kts") contents: String) {
        resolve("build.gradle.kts").writeText(contents)
    }

    fun writeSettingsGradleKts(@Language("kts") contents: String) {
        resolve("settings.gradle.kts").writeText(contents)
    }

    fun resolve(path: String): Path = projectDir.resolve(path)

    @OptIn(ExperimentalPathApi::class)
    suspend fun gradleRunner(action: suspend GradleRunner.() -> Unit): GradleRunner {
        val gradleRunnerDir = createTmpDir("gradle-runner", delete = false)
        projectDir.copyToRecursively(gradleRunnerDir, followLinks = false, overwrite = true)

        val gradleProperties = gradleRunnerDir.resolve("gradle.properties")

        if (!gradleProperties.exists())
            gradleProperties.createFile()

        // fix for https://github.com/gradle/gradle/issues/29747
        val settingsGradleKts = gradleRunnerDir.resolve("settings.gradle.kts")
        if (settingsGradleKts.notExists())
            settingsGradleKts.createFile()

        // language=Properties
        gradleProperties.appendText(
            """
                |
                |org.gradle.daemon.idletimeout=${30.seconds.inWholeMilliseconds}
            """.trimMargin()
        )

        val runner = GradleRunner.create()
            .withProjectDir(gradleRunnerDir.toFile())

        action(runner)

        return runner
    }
}

