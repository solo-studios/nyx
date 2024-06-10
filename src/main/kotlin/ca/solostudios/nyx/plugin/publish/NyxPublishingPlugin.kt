/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxPublishingPlugin.kt is part of nyx
 * Last modified on 10-06-2024 03:24 p.m.
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

package ca.solostudios.nyx.plugin.publish

import ca.solostudios.nyx.NyxExtension
import ca.solostudios.nyx.internal.InternalNyxPlugin
import ca.solostudios.nyx.internal.util.create
import ca.solostudios.nyx.plugin.publish.release.NyxGithubReleaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

internal class NyxPublishingPlugin : InternalNyxPlugin {
    override fun apply(project: Project) {
        val nyxExtension = project.the<NyxExtension>()
        val publishingExtension = nyxExtension.create<PublishingExtension>(PublishingExtension.NAME, project, nyxExtension.info)

        project.plugins.withId("com.github.breadmoirai.github-release") {
            publishingExtension.create<NyxGithubReleaseExtension>(NyxGithubReleaseExtension.NAME, project, nyxExtension.info)
        }

        afterEvaluate(project) {
            publishingExtension.configureProject()
        }
    }
}
