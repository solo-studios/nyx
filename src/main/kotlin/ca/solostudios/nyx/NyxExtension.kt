/*
 * Copyright (c) 2023-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxExtension.kt is part of nyx
 * Last modified on 05-01-2025 12:06 a.m.
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

package ca.solostudios.nyx

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.plugin.compile.NyxCompileExtension
import ca.solostudios.nyx.plugin.publish.NyxPublishingExtension
import ca.solostudios.nyx.project.NyxProjectInfoExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Nested

/**
 * Base extension for Nyx.
 *
 * The available sub-extensions depend on which plugins are loaded.
 *
 * @see NyxProjectInfoExtension
 * @see NyxCompileExtension
 * @see NyxPublishingExtension
 */
public class NyxExtension(override val project: Project) : InternalNyxExtension {
    /**
     * The project's metadata.
     */
    @Nested
    public val info: NyxProjectInfoExtension = NyxProjectInfoExtension(project)

    /**
     * Configures the project info.
     *
     * @see info
     */
    public fun info(action: NyxProjectInfoExtension.() -> Unit) {
        info.apply(action)
    }

    override fun configureProject() {
        info.configureProject()
    }

    public companion object {
        /**
         * The name this extension is added with.
         */
        public const val NAME: String = "nyx"
    }
}
