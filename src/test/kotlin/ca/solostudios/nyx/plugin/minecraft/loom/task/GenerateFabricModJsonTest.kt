/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GenerateFabricModJsonTest.kt is part of nyx
 * Last modified on 22-10-2024 02:34 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.loom.task

import ca.solostudios.nyx.NyxPlugin
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.project
import io.kotest.matchers.collections.shouldBeEmpty
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.withType

class GenerateFabricModJsonTest : NyxSpec({
    feature("the nyx GenerateFabricModJson task") {
        given("a project with the fabric-loom and nyx plugins") {
            val project = project {
                plugins.apply("fabric-loom")
                plugins.apply(NyxPlugin::class)
            }

            should("not have a GenerateFabricModJson task by default") {
                project.tasks.withType<GenerateFabricModJson>().shouldBeEmpty()
            }
        }
    }
})
