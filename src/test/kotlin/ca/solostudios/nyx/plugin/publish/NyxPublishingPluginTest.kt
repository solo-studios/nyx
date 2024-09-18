/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxPublishingPluginTest.kt is part of nyx
 * Last modified on 18-09-2024 12:15 a.m.
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

import ca.solostudios.nyx.NyxPlugin
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.project
import ca.solostudios.nyx.util.shouldHavePlugin
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class NyxPublishingPluginTest : NyxSpec({
    feature("the nyx publishing plugin") {
        upon("applying the nyx plugin") {
            val project = project {}

            project.apply<NyxPlugin>()

            should("apply it") {
                project.shouldHavePlugin<NyxPublishingPlugin>()
            }

            should("add the extension") {
                (project.nyx as ExtensionAware).extensions.findByType<NyxPublishingExtension>().shouldNotBeNull()
            }
        }
    }
})
