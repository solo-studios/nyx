/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxJavaExtension.kt is part of nyx
 * Last modified on 14-07-2024 08:25 p.m.
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
import ca.solostudios.nyx.plugin.minecraft.neoforge.NyxNeoGradleExtension
import net.neoforged.gradle.neoform.runtime.tasks.RecompileSourceJar
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.external.javadoc.internal.AbstractListJavadocOptionFileOption
import org.gradle.external.javadoc.internal.JavadocOptionFileWriterContext
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.properties
import org.gradle.kotlin.dsl.withType

public class NyxJavaExtension(
    override val project: Project,
    compile: NyxCompileExtension,
) : InternalNyxExtension {
    /**
     * If all warnings are enabled.
     *
     * @see NyxCompileExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(compile.allWarnings)

    /**
     * If compilers outputting warnings as errors is enabled.
     *
     * @see NyxCompileExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(compile.warningsAsErrors)

    /**
     * If all warnings should be suppressed.
     *
     * @see NyxCompileExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(compile.suppressWarnings)

    /**
     * If all javadoc warnings are enabled.
     *
     * Adds the `-Xdoclint:all` flag to the javadoc task.
     */
    public val allJavadocWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If missing element javadoc warnings are enabled.
     *
     * Adds the `-Xdoclint:-missing` flag to the javadoc task.
     */
    public val noMissingJavadocWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If emitting javadoc warnings as errors is enabled.
     *
     * Adds the `-Werror` flag to the javadoc task.
     */
    public val javadocWarningsAsErrors: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If all javadoc warnings should be suppressed.
     *
     * Adds the `-Xdoclint:none` flag to the javadoc task.
     */
    public val suppressJavadocWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * The encoding to be used for all files.
     *
     * Defaults to `UTF-8`.
     *
     * @see NyxCompileExtension.encoding
     */
    public val encoding: Property<String> = property<String>().convention(compile.encoding)

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaToolchainSpec.getLanguageVersion
     * @see NyxCompileExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property<Int>().convention(compile.jvmToolchain)

    /**
     * The jvm target to use.
     *
     * @see JavaPluginExtension.setTargetCompatibility
     * @see NyxCompileExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property<Int>().convention(compile.jvmTarget)

    /**
     * If the sources jar is enabled.
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see NyxCompileExtension.sourcesJar
     */
    public val sourcesJar: Property<Boolean> = property<Boolean>().convention(compile.sourcesJar)

    /**
     * If the javadoc jar is enabled.
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see NyxCompileExtension.javadocJar
     */
    public val javadocJar: Property<Boolean> = property<Boolean>().convention(compile.javadocJar)

    /**
     * A list of arguments to pass to the compiler.
     *
     * @see CompileOptions.compilerArgs
     */
    public val compilerArgs: ListProperty<String> = listProperty()

    /**
     * Enables all warnings
     *
     * @see allWarnings
     */
    public fun withAllWarnings() {
        allWarnings = true
    }

    /**
     * Enables warnings as errors
     *
     * @see warningsAsErrors
     */
    public fun withWarningsAsErrors() {
        warningsAsErrors = true
    }

    /**
     * Enables suppressing warnings
     *
     * @see suppressWarnings
     */
    public fun withSuppressWarnings() {
        suppressWarnings = true
    }

    /**
     * Enables all javadoc warnings.
     *
     * @see allJavadocWarnings
     */
    public fun withAllJavadocWarnings() {
        allJavadocWarnings = true
    }

    /**
     * Enables missing element javadoc warnings.
     *
     * @see noMissingJavadocWarnings
     */
    public fun withNoMissingJavadocWarnings() {
        noMissingJavadocWarnings = true
    }

    /**
     * Enables javadoc warnings as errors.
     *
     * @see javadocWarningsAsErrors
     */
    public fun withJavadocWarningsAsErrors() {
        javadocWarningsAsErrors = true
    }

    /**
     * Enables suppressing javadoc warnings.
     *
     * @see suppressJavadocWarnings
     */
    public fun withSuppressJavadocWarnings() {
        suppressJavadocWarnings = true
    }

    /**
     * Enables the sources jar
     *
     * @see sourcesJar
     */
    public fun withSourcesJar() {
        sourcesJar = true
    }

    /**
     * Enables the javadoc jar
     *
     * @see javadocJar
     */
    public fun withJavadocJar() {
        javadocJar = true
    }

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

            if (sourcesJar.isTrue)
                withSourcesJar()

            if (javadocJar.isTrue)
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

                options {
                    if (this !is StandardJavadocDocletOptions)
                        return@options

                    inputs.properties(
                        "allJavadocWarnings" to allJavadocWarnings,
                        "noMissingJavadocWarnings" to noMissingJavadocWarnings,
                        "suppressJavadocWarnings" to suppressJavadocWarnings,
                        "javadocWarningsAsErrors" to javadocWarningsAsErrors,
                    )

                    val doclintFlags = buildList {
                        if (allJavadocWarnings.isTrue)
                            add("all")

                        if (noMissingJavadocWarnings.isTrue)
                            add("-missing")

                        if (suppressJavadocWarnings.isTrue)
                            add("none")
                    }

                    addOption(DoclintJavadocFileOption(doclintFlags))

                    if (javadocWarningsAsErrors.isTrue)
                        addBooleanOption("-Werror")
                }
            }
        }
    }

    private fun JavaCompile.shouldSkipWarningConfiguration(): Boolean {
        return when {
            NyxNeoGradleExtension.isNotLoaded(project) -> false
            this is RecompileSourceJar -> true
            else -> false
        }
    }

    public companion object {
        public const val NAME: String = "java"
    }

    public class DoclintJavadocFileOption(value: List<String>) : AbstractListJavadocOptionFileOption<List<String>>("Xdoclint", value, ",") {
        override fun duplicate(): DoclintJavadocFileOption = DoclintJavadocFileOption(value.toMutableList())
        override fun writeCollectionValue(writerContext: JavadocOptionFileWriterContext) {
            writerContext.apply {
                write("-")
                write(option)
                write(":")
                write(value.joinToString(separator = ","))
                newLine()
            }
        }
    }
}
