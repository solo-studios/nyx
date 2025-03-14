/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ProjectBuilderUtil.kt is part of nyx
 * Last modified on 25-10-2024 07:40 p.m.
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
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

fun AbstractContainerScope.project(name: String? = null, parent: Project? = null, block: Project.() -> Unit = {}): Project {
    return this.testCase.spec.project(name, parent, block)
}

fun Spec.project(name: String? = null, parent: Project? = null, block: Project.() -> Unit = {}): Project {
    return ProjectBuilder.builder()
        .apply {
            this.withProjectDir(createTmpDir("worker", "gradle-worker-", delete = false).toFile())
            if (name != null)
                withName(name)
            if (parent != null)
                withParent(parent)
        }
        .build()
        .apply(block)
}
