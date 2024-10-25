/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxMinecraftPluginTest.kt is part of nyx
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

package ca.solostudios.nyx.plugin.minecraft

import ca.solostudios.nyx.NyxPlugin
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.kotest.spec.NyxSpecFeatureContainerScope
import ca.solostudios.nyx.util.project
import ca.solostudios.nyx.util.shouldHavePlugin
import io.kotest.matchers.nulls.shouldNotBeNull
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType

class NyxMinecraftPluginTest : NyxSpec({
    feature("the nyx minecraft plugin") {
        givenMinecraftPluginIdShouldApply("fabric-loom")
        givenMinecraftPluginIdShouldApply("dev.architectury.loom")
        givenMinecraftPluginIdShouldApply("org.quiltmc.loom")
        // neogradle breaks testing for some reason
        givenMinecraftPluginIdShouldApply("net.neoforged.gradle.userdev")
    }
})

private suspend fun NyxSpecFeatureContainerScope.givenMinecraftPluginIdShouldApply(id: String) {
    given("a project with the $id plugin applied") {
        val project = project {
            plugins.apply(id)
        }

        upon("applying the nyx plugin") {
            project.apply<NyxPlugin>()

            should("apply it") {
                project.shouldHavePlugin<NyxMinecraftPlugin>()
            }

            should("add the extension") {
                (project.nyx as ExtensionAware).extensions.findByType<AbstractMinecraftExtension>().shouldNotBeNull()
            }
        }
    }
}
