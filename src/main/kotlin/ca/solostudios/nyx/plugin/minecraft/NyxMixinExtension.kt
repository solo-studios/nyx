/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxMixinExtension.kt is part of nyx
 * Last modified on 19-06-2024 04:58 p.m.
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
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue

public class NyxMixinExtension(
    override val project: Project,
    private val minecraftExtension: AbstractMinecraftExtension,
) : InternalNyxExtension {
    /**
     * If hotswapping mixins should be enabled.
     *
     * Note: this will also find the first net.fabricmc:sponge-mixin artifact on the runtime classpath and add it as a java agent
     *
     * If you have just added this plugin and had previously generated run configurations, delete them so they can be re-created.
     *
     * This sets the `mixin.hotSwap` jvm property.
     */
    public val hotswap: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * If *ALL* mixin debug features should be enabled.
     * This includes `mixin.debug.verify`.
     *
     * Note: enabling this can sometimes cause issues with other mods' mixins.
     *
     * This sets the `mixin.debug` jvm property.
     */
    public val debug: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If mixin verbose logging should be enabled.
     *
     * This sets the `mixin.debug.verbose` jvm property.
     */
    public val verbose: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * If dumping the target class on failures should be enabled.
     *
     * This sets the `mixin.dumpTargetOnFailure` jvm property
     */
    public val dumpTargetOnFailure: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * If mixin checks should be enabled.
     *
     * Note: enabling this can sometimes cause issues with other mods' mixins.
     *
     * This sets the `mixin.checks` jvm property.
     */
    public val checks: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If mixin debug verification should be enabled.
     *
     * Note: enabling this can sometimes cause issues with other mods' mixins.
     *
     * This sets the `mixin.debug.verify` jvm property
     */
    public val verify: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If exporting the mixins after they have been applied should be enabled.
     *
     * This will export the classes in the `.mixin.out` dir under the run directory
     *
     * This sets the `mixin.debug.export` jvm property.
     */
    public val export: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * Enables mixin hotswapping.
     *
     * @see hotswap
     */
    public fun withHotswap() {
        hotswap = true
    }

    /**
     * Enables mixin debugging.
     *
     * @see debug
     */
    public fun withDebug() {
        debug = true
    }

    /**
     * Enables verbose mixins.
     *
     * @see verbose
     */
    public fun withVerbose() {
        verbose = true
    }

    /**
     * Enables dumping target classes on failure.
     *
     * @see dumpTargetOnFailure
     */
    public fun withDumpTargetOnFailure() {
        dumpTargetOnFailure = true
    }

    /**
     * Enables mixin checks.
     *
     * @see checks
     */
    public fun withChecks() {
        checks = true
    }

    /**
     * Enables mixin debug verification.
     *
     * @see verify
     */
    public fun withVerify() {
        verify = true
    }

    /**
     * Enables mixin exporting.
     *
     * @see export
     */
    public fun withExport() {
        export = true
    }

    /**
     * Selects the file name for the mixin config.
     *
     * This file is relative to `src/main/resources/`.
     *
     * This ONLY applies to forge.
     */
    public fun mixinConfig(name: String) {
        minecraftExtension.addMixinConfig(name)
    }

    /**
     * Sets the mixin default refmap name to `mixins/`[name]`/refmap.json`.
     *
     * This ONLY applies to fabric.
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
        if (export.isPresent)
            minecraftExtension.additionalJvmProperties.put("mixin.debug.export", export.get().toString())

        if (hotswap.isTrue) {
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
