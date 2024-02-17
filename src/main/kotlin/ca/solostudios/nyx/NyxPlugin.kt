/*
 * Copyright (c) 2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ProjectConventionsPlugin.kt is part of gradle-conventions-plugin
 * Last modified on 13-11-2023 07:48 p.m.
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

package ca.solostudios.nyx

import ca.solostudios.nyx.ext.NyxExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

public class NyxPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = NyxExtension.create(project)

        project.pluginManager.withPlugin("fabric-loom") {
            // project.loom {}
            project.configure<LoomGradleExtensionAPI> {

            }
        }
        project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            project.configure<KotlinMultiplatformPluginWrapper> {

            }
        }

        configureBuild(extension, project)
    }

    private fun configureBuild(extension: NyxExtension, project: Project) {
        with(project) {
            afterEvaluate {
                extension.configureProject()
            }
        }
    }
}
