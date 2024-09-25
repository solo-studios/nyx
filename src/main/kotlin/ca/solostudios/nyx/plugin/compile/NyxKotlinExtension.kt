/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxKotlinExtension.kt is part of nyx
 * Last modified on 25-09-2024 06:17 p.m.
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

@file:Suppress("DEPRECATION")

package ca.solostudios.nyx.plugin.compile

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.addDokkaJavadocJarTask
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.java
import ca.solostudios.nyx.internal.util.kotlin
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger

public class NyxKotlinExtension(
    override val project: Project,
    compile: NyxCompileExtension,
) : InternalNyxExtension {
    private val logger by getLogger()

    /**
     * Sets the kotlin api version used
     *
     * @see KotlinCommonOptions.apiVersion
     */
    public val apiVersion: Property<String> = property()

    /**
     * Sets the kotlin language version used
     *
     * @see KotlinCommonOptions.languageVersion
     */
    public val languageVersion: Property<String> = property()

    /**
     * Enables the following opt-in annotations
     *
     * @see KotlinCommonCompilerOptions.optIn
     */
    public val optIn: ListProperty<String> = listProperty()

    /**
     * The explicit api mode
     *
     * @see KotlinTopLevelExtension.explicitApi
     */
    public val explicitApi: Property<ExplicitApiMode> = property()

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
     * Requires dokka.
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see NyxCompileExtension.javadocJar
     */
    public val javadocJar: Property<Boolean> = property<Boolean>().convention(compile.javadocJar)

    /**
     * A list of arguments to pass to the compiler.
     *
     * @see KotlinCommonCompilerToolOptions.freeCompilerArgs
     */
    public val compilerArgs: ListProperty<String> = listProperty()

    /**
     * Enables the strict explicit api mode.
     *
     * @see KotlinTopLevelExtension.explicitApi
     */
    public fun withExplicitApi() {
        explicitApi = ExplicitApiMode.Strict
    }

    /**
     * Enables the warning explicit api mode.
     *
     * @see KotlinTopLevelExtension.explicitApiWarning
     */
    public fun withExplicitApiWarning() {
        explicitApi = ExplicitApiMode.Warning
    }

    /**
     * Enables warnings as errors.
     *
     * @see warningsAsErrors
     */
    public fun withWarningsAsErrors() {
        warningsAsErrors = true
    }

    /**
     * Enables suppressing warnings.
     *
     * @see suppressWarnings
     */
    public fun withSuppressWarnings() {
        suppressWarnings = true
    }

    /**
     * Enables the sources jar.
     *
     * @see sourcesJar
     */
    public fun withSourcesJar() {
        sourcesJar = true
    }

    /**
     * Enables the javadoc jar.
     *
     * @see javadocJar
     */
    public fun withJavadocJar() {
        javadocJar = true
    }

    override fun configureProject() {
        if (javadocJar.isTrue) {
            if (!project.plugins.hasPlugin("org.jetbrains.dokka")) {
                // Project does not have dokka plugin. do not attempt to configure it.
                logger.error(IllegalStateException()) {
                    """
                        Requested to add a javadoc jar for a kotlin project, however the dokka plugin has not been applied.

                        Please apply the dokka plugin in order to generate the javadoc jar for kotlin source code:
                        plugins {
                            id("org.jetbrains.dokka") version "<version>"
                        }
                    """.trimIndent()
                }
                return
            }

            // Ensure dokka is applied
            // project.apply<DokkaPlugin>()

            addDokkaJavadocJarTask()
        }

        kotlin {
            if (this@NyxKotlinExtension.explicitApi.isPresent)
                explicitApi = this@NyxKotlinExtension.explicitApi.get()

            if (jvmToolchain.isPresent)
                jvmToolchain(jvmToolchain.get())

            when (this) {
                is KotlinJvmProjectExtension -> {
                    configureCommonCompilations(target)
                    if (sourcesJar.isTrue)
                        java.withSourcesJar()

                    target.compilations.configureEach {
                        configureJvmCompilation(this)
                    }
                }

                is KotlinMultiplatformExtension -> {
                    if (sourcesJar.isTrue)
                        withSourcesJar(publish = true)

                    targets.configureEach {
                        configureCommonCompilations(this)

                        if (this is KotlinJvmTarget) {
                            compilations.configureEach {
                                configureJvmCompilation(this)
                            }
                        }
                    }
                }

                else -> {
                    // Throw error for other platforms
                    logger.error(NotImplementedError("Unsupported kotlin platform.")) {
                        """
                            This kotlin platform is not supported. Currently the only supported platforms are kotlin/jvm and kotlin/multiplatform.

                            Please open an issue with Nyx if you would like support for the current kotlin platform.
                        """.trimIndent()
                    }
                }
            }
        }
    }

    private fun configureJvmCompilation(compilation: KotlinCompilation<KotlinJvmOptions>) {
        if (jvmTarget.isPresent)
            compilation.kotlinOptions.jvmTarget = /* evil */ if (jvmTarget.get() == 8) "1.8" else jvmTarget.get().toString()
    }

    @Suppress("DEPRECATION")
    private fun configureCommonCompilations(target: KotlinTarget) {
        target.compilations.configureEach {
            if (apiVersion.isPresent)
                kotlinOptions.apiVersion = apiVersion.get()

            if (languageVersion.isPresent)
                kotlinOptions.languageVersion = languageVersion.get()

            if (warningsAsErrors.isPresent)
                kotlinOptions.allWarningsAsErrors = warningsAsErrors.get()

            if (optIn.isPresent)
                kotlinOptions.options.optIn = optIn

            if (suppressWarnings.isPresent)
                kotlinOptions.suppressWarnings = suppressWarnings.get()

            if (compilerArgs.isPresent)
                kotlinOptions.options.freeCompilerArgs.addAll(compilerArgs)
        }
    }

    public companion object {
        public const val NAME: String = "kotlin"
    }
}
