/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file MixinExtension.kt is part of nyx
 * Last modified on 11-06-2024 06:13 p.m.
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

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.configurations
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getValue

public class MixinExtension(
    override val project: Project,
    private val minecraftExtension: AbstractMinecraftExtension,
) : InternalNyxExtension {
    public val hotswapMixins: Property<Boolean> = property<Boolean>().convention(true)
    public val debug: Property<Boolean> = property<Boolean>().convention(true)
    public val verbose: Property<Boolean> = property<Boolean>().convention(false)
    public val dumpTargetOnFailure: Property<Boolean> = property<Boolean>().convention(true)
    public val checks: Property<Boolean> = property<Boolean>().convention(false)
    public val verify: Property<Boolean> = property<Boolean>().convention(false)

    public fun mixinConfig(name: String) {
        minecraftExtension.addMixinConfig(name)
    }

    /**
     * Sets the mixin default refmap name to `mixins/`[name]`/refmap.json`.
     *
     * @param name The directory used for the refmap, defaults to `project.name`.
     */
    public fun mixinRefmapName(name: String = project.name) {
        minecraftExtension.setDefaultMixinRefmapName("mixins/$name/refmap.json")
    }

    override fun configureProject() {
        if (debug.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.debug", debug.get().toString())
        if (verbose.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.debug.verbose", verbose.get().toString())
        if (dumpTargetOnFailure.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.dumpTargetOnFailure", dumpTargetOnFailure.get().toString())
        // makes silent failures into hard-failures
        if (checks.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.checks", checks.get().toString())
        if (verify.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.debug.verify", verify.get().toString())


        if (hotswapMixins.isTrue) {
            minecraftExtension.additionalJvmProperties.put("mixin.hotSwap", "true")

            val runtimeClasspath by configurations.named("runtimeClasspath")
            val mixinJarFile = runtimeClasspath.resolvedConfiguration.resolvedArtifacts.firstOrNull {
                val module = it.moduleVersion.id
                module.group == "net.fabricmc" && module.name == "sponge-mixin"
            }
            if (mixinJarFile != null)
                minecraftExtension.additionalJvmArgs.add("-javaagent:${mixinJarFile.file}")
        }
    }

    public companion object {
        public const val NAME: String = "mixin"
    }
}
