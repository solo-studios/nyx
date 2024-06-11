/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file JavaExtension.kt is part of nyx
 * Last modified on 11-06-2024 05:31 p.m.
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

package ca.solostudios.nyx.plugin.compile

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.isFalse
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.java
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.plugin.minecraft.neoforge.NeoGradleExtension
import net.neoforged.gradle.neoform.runtime.tasks.RecompileSourceJar
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType

public class JavaExtension(
    override val project: Project,
    compile: CompileExtension,
) : InternalNyxExtension {
    /**
     * The encoding to be used for all files.
     *
     * Defaults to `UTF-8`.
     *
     * @see CompileExtension.encoding
     */
    public val encoding: Property<String> = property<String>().convention(compile.encoding)

    /**
     * Enables all compilers to output warnings as errors.
     *
     * @see CompileExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(compile.warningsAsErrors)

    /**
     * Enables all warnings
     *
     * @see CompileExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(compile.allWarnings)

    /**
     * Suppresses all warnings
     *
     * @see CompileExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(compile.suppressWarnings)

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaToolchainSpec.getLanguageVersion
     * @see CompileExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property<Int>().convention(compile.jvmToolchain)

    /**
     * The jvm target to use.
     *
     * @see JavaPluginExtension.setTargetCompatibility
     * @see CompileExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property<Int>().convention(compile.jvmTarget)

    /**
     * Enables sources jar
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see CompileExtension.withSourcesJar
     */
    public val withSourcesJar: Property<Boolean> = property<Boolean>().convention(compile.withSourcesJar)

    /**
     * Enables javadoc jar
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see CompileExtension.withJavadocJar
     */
    public val withJavadocJar: Property<Boolean> = property<Boolean>().convention(compile.withJavadocJar)

    public val compilerArgs: ListProperty<String> = listProperty()

    /**
     * Configures the manifest used in the resulting jar file(s)
     *
     * @param action Configuration for the manifest
     */
    public fun manifest(action: Action<Manifest>) {
        java {
            manifest(action)
        }
    }

    /**
     * Configures the manifest used in the resulting jar file(s)
     *
     * @param action Configuration for the manifest
     * @receiver The manifest to be configured
     */
    public fun manifest(action: (Manifest) -> Unit) {
        java {
            manifest(action)
        }
    }

    override fun configureProject() {
        java {
            if (jvmToolchain.isPresent) {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(jvmToolchain.get())
                }
            }

            if (jvmTarget.isPresent) {
                targetCompatibility = JavaVersion.toVersion(jvmTarget.get())
                sourceCompatibility = JavaVersion.toVersion(jvmTarget.get())
            }

            if (withSourcesJar.isTrue)
                withSourcesJar()

            if (withJavadocJar.isTrue)
                withJavadocJar()
        }


        tasks {
            withType<JavaCompile>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()

                if (jvmTarget.isPresent)
                    options.release = jvmTarget.get()

                if (compilerArgs.isPresent)
                    options.compilerArgs.addAll(compilerArgs.get())

                if (shouldSkipWarningConfiguration())
                    return@configureEach

                if (warningsAsErrors.isTrue)
                    options.compilerArgs.add("-Werror")

                if (allWarnings.isTrue)
                    options.compilerArgs.add("-Xlint:all")

                if (suppressWarnings.isFalse)
                    options.isWarnings = false
            }

            withType<Javadoc>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()
            }
        }
    }

    private fun JavaCompile.shouldSkipWarningConfiguration(): Boolean {
        return when {
            NeoGradleExtension.isNotLoaded(project) -> false
            this is RecompileSourceJar -> true
            else -> false
        }
    }

    public companion object {
        public const val NAME: String = "java"
    }
}
