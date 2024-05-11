/*
 * Copyright (c) 2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ProjectConventionExtension.kt is part of gradle-conventions-plugin
 * Last modified on 13-11-2023 07:51 p.m.
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

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.ext.PublishingExtension
import ca.solostudios.nyx.ext.code.CompileExtension
import ca.solostudios.nyx.ext.mc.MinecraftExtension
import ca.solostudios.nyx.ext.project.ProjectInfoExtension
import ca.solostudios.nyx.util.create
import net.fabricmc.loom.bootstrap.LoomGradlePluginBootstrap
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.withType

public open class NyxExtension(private val gradleProject: Project) : ConfiguresProject {

    @Nested
    public val project: ProjectInfoExtension = ProjectInfoExtension(gradleProject)

    @Nested
    public val publishing: PublishingExtension = PublishingExtension(gradleProject)

    @Nested
    public val compile: CompileExtension = CompileExtension(gradleProject)

    @Nested
    public val minecraft: MinecraftExtension = MinecraftExtension(gradleProject)

    public fun project(action: Action<ProjectInfoExtension>) {
        action.execute(project)
    }

    public fun project(action: (ProjectInfoExtension).() -> Unit) {
        project.apply(action)
    }

    public fun publishing(action: Action<PublishingExtension>) {
        action.execute(publishing)
    }

    public fun publishing(action: (PublishingExtension).() -> Unit) {
        publishing.apply(action)
    }

    public fun compile(action: Action<CompileExtension>) {
        action.execute(compile)
    }

    public fun compile(action: (CompileExtension).() -> Unit) {
        compile.apply(action)
    }

    public fun minecraft(action: Action<MinecraftExtension>) {
        action.execute(minecraft)
    }

    public fun minecraft(action: (MinecraftExtension).() -> Unit) {
        minecraft.apply(action)
    }

    override fun onLoad() {
        project.onLoad()
        compile.onLoad()
        publishing.onLoad()

        try {
            gradleProject.plugins.withType(LoomGradlePluginBootstrap::class) {
                minecraft.onLoad()
            }
        } catch (_: NoClassDefFoundError) {
            // ignore
        }
    }

    override fun configureProject() {
        project.configureProject()
        compile.configureProject()
        publishing.configureProject()

        try {
            // can't do withId, because loom could have several different ids (fabric-loom, quilt-loom, architectury-loom, etc.)
            gradleProject.plugins.withType(LoomGradlePluginBootstrap::class) {
                minecraft.configureProject()
            }
        } catch (_: NoClassDefFoundError) {
            // ignore
        }
    }

    public companion object {
        public const val NAME: String = "nyx"

        public fun create(project: Project): NyxExtension = project.create<NyxExtension>(NAME, project)
    }
}
