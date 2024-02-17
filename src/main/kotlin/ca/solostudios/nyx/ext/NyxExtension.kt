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

package ca.solostudios.nyx.ext

import ca.solostudios.nyx.ext.code.CompileExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance

public open class NyxExtension(project: Project) {

    @Nested
    public val project: ProjectInfo = project.objects.newInstance(project)

    @Nested
    public val publishing: PublishingExtension = project.objects.newInstance(project)

    @Nested
    public val compile: CompileExtension = project.objects.newInstance(project)

    public fun project(action: Action<ProjectInfo>) {
        action.execute(project)
    }

    public fun project(action: (ProjectInfo).() -> Unit) {
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

    internal fun configureProject() {
        project.configureProject()
        compile.configureProject()
        publishing.configureProject(project)
    }

    public companion object {
        public const val NAME: String = "nyx"

        public operator fun get(project: Project): NyxExtension = project.extensions.getByType<NyxExtension>()

        public fun create(project: Project): NyxExtension = project.extensions.create<NyxExtension>(NAME, project)
    }
}
