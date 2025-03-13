/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxKotlinExtension.kt is part of nyx
 * Last modified on 13-03-2025 04:19 p.m.
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

@file:Suppress("DEPRECATION")

package ca.solostudios.nyx.plugin.compile

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.addDokkaJavadocJarTask
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.java
import ca.solostudios.nyx.internal.util.kotlin
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.tasks
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapEmbedMode
import org.jetbrains.kotlin.gradle.dsl.JsSourceMapNamesPolicy
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompilerToolOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger

/**
 * An extension to configure the kotlin plugin.
 */
public class NyxKotlinExtension(
    override val project: Project,
    compile: NyxCompileExtension,
) : InternalNyxExtension {
    private val logger by getLogger()

    /**
     * Sets the kotlin api version used.
     *
     * @see KotlinCommonCompilerOptions.apiVersion
     */
    public val apiVersion: Property<String> = property()

    /**
     * Sets the kotlin language version used.
     *
     * @see KotlinCommonCompilerOptions.languageVersion
     */
    public val languageVersion: Property<String> = property()

    /**
     * Enables the following opt-in annotations.
     *
     * @see KotlinCommonCompilerOptions.optIn
     */
    public val optIn: ListProperty<String> = listProperty()

    /**
     * The explicit api mode.
     *
     * @see
     *         org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtensionConfig.explicitApi
     */
    public val explicitApi: Property<ExplicitApiMode> = property()

    /**
     * If the
     * [progressive compiler mode](https://kotlinlang.org/docs/whatsnew13.html#progressive-mode)
     * is enabled.
     *
     * @see KotlinCommonCompilerOptions.progressiveMode
     */
    public val progressiveMode: Property<Boolean> = property()

    /**
     * If compilers outputting warnings as errors is enabled.
     *
     * @see KotlinCommonCompilerToolOptions.allWarningsAsErrors
     * @see NyxCompileExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(compile.warningsAsErrors)

    /**
     * If
     * [additional compiler warnings](https://kotlinlang.org/docs/whatsnew21.html#extra-compiler-checks)
     * are enabled.
     *
     * @see KotlinCommonCompilerToolOptions.extraWarnings
     */
    public val extraWarnings: Property<Boolean> = property()

    /**
     * If all warnings should be suppressed.
     *
     * @see KotlinCommonCompilerToolOptions.suppressWarnings
     * @see NyxCompileExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(compile.suppressWarnings)

    /**
     * List of specific warnings to be suppressed.
     */
    public val suppressedWarnings: ListProperty<String> = listProperty()

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaPluginExtension.toolchain
     * @see NyxCompileExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property<Int>().convention(compile.jvmToolchain)

    /**
     * The jvm target to use.
     *
     * @see KotlinJvmCompilerOptions.jvmTarget
     * @see JavaPluginExtension.setTargetCompatibility
     * @see NyxCompileExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property<Int>().convention(compile.jvmTarget)

    /**
     * If the compiler should generate metadata for Java 1.8 reflection on
     * method parameters.
     *
     * @see KotlinJvmCompilerOptions.javaParameters
     */
    public val javaParameters: Property<Boolean> = property()

    /**
     * The kind of JS module generated by the compiler. ES modules are enabled
     * by default in case of ES2015 target usage.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.moduleKind
     */
    public val jsModuleKind: Property<JsModuleKind> = property()

    /**
     * If source maps are enabled.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.sourceMap
     */
    public val jsSourceMap: Property<Boolean> = property()

    /**
     * If source files should be embedded in the source maps.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.sourceMapEmbedSources
     */
    public val jsSourceMapEmbedSources: Property<JsSourceMapEmbedMode> = property()

    /**
     * Mode for mapping generated names to original names.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.sourceMapNamesPolicy
     */
    public val jsSourceMapNamesPolicy: Property<JsSourceMapNamesPolicy> = property()

    /**
     * The ECMA version to target for generated JavaScript files. Should be one
     * of: `es5`, `es2015`.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.target
     */
    public val jsTarget: Property<String> = property()

    /**
     * If ES2015 classes should be generated by the compiler. Enabled by
     * default if the [target][jsTarget] is set to `es2015`.
     *
     * This only applies to Kotlin/JS targets.
     *
     * @see KotlinJsCompilerOptions.useEsClasses
     */
    public val jsUseEsClasses: Property<Boolean> = property()

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
     * @see
     *         org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtensionConfig.explicitApi
     */
    public fun withExplicitApi() {
        explicitApi = ExplicitApiMode.Strict
    }

    /**
     * Enables the warning explicit api mode.
     *
     * @see
     *         org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtensionConfig.explicitApiWarning
     */
    public fun withExplicitApiWarning() {
        explicitApi = ExplicitApiMode.Warning
    }

    /**
     * Enables the
     * [progressive compiler mode](https://kotlinlang.org/docs/whatsnew13.html#progressive-mode).
     *
     * @see progressiveMode
     */
    public fun withProgressiveMode() {
        progressiveMode = true
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
     * Enables
     * [additional compiler warnings](https://kotlinlang.org/docs/whatsnew21.html#extra-compiler-checks).
     *
     * @see extraWarnings
     */
    public fun withExtraWarnings() {
        extraWarnings = true
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
     * Enables generation of metadata for Java 1.8 reflection on method
     * parameters.
     *
     * @see javaParameters
     */
    public fun withJavaParameters() {
        javaParameters = true
    }

    /**
     * Enables js source maps.
     *
     * @see jsSourceMap
     */
    public fun withJsSourceMap() {
        jsSourceMap = true
    }

    /**
     * Targets ECMA ES2015.
     *
     * @see jsTarget
     */
    public fun withJsEs2015Target() {
        jsTarget = "es2015"
    }

    /**
     * Targets ECMA ES5.
     *
     * @see jsTarget
     */
    public fun withJsEs5Target() {
        jsTarget = "es5"
    }

    /**
     * Generates ES2015 classes.
     */
    public fun withJsEsClasses() {
        jsUseEsClasses = true
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
            if (!project.pluginManager.hasPlugin(DOKKA_PLUGIN_ID)) {
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
                is KotlinJvmProjectExtension    -> {
                    compilerOptions.configureJvmCompilerOptions()
                    compilerOptions.configureCommonCompilerOptions()

                    if (sourcesJar.isTrue) {
                        java.withSourcesJar()
                        // ensure kotlin's sources jar is disabled, because it breaks things
                        tasks.withType<Jar>().named("kotlinSourcesJar").configure { enabled = false }
                    }
                }

                is KotlinMultiplatformExtension -> {
                    compilerOptions.configureCommonCompilerOptions()

                    if (sourcesJar.isTrue)
                        withSourcesJar(publish = true)

                    targets.withType<KotlinJvmTarget>().configureEach {
                        compilerOptions.configureJvmCompilerOptions()
                    }

                    targets.withType<KotlinJsIrTarget>().configureEach {
                        compilerOptions.configureJsCompilerOptions()
                    }
                }

                else                            -> {
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

    private fun KotlinJvmCompilerOptions.configureJvmCompilerOptions() {
        val nyx = this@NyxKotlinExtension
        if (nyx.jvmTarget.isPresent)
            jvmTarget = JvmTarget.fromTarget(nyx.jvmTarget.get().let { target -> if (target == 8) "1.8" else target.toString() })

        if (nyx.javaParameters.isPresent)
            javaParameters = nyx.javaParameters
    }

    private fun KotlinJsCompilerOptions.configureJsCompilerOptions() {
        val nyx = this@NyxKotlinExtension
        if (nyx.jsModuleKind.isPresent)
            moduleKind = nyx.jsModuleKind

        if (nyx.jsSourceMap.isPresent)
            sourceMap = nyx.jsSourceMap

        if (nyx.jsSourceMapEmbedSources.isPresent)
            sourceMapEmbedSources = nyx.jsSourceMapEmbedSources

        if (nyx.jsSourceMapNamesPolicy.isPresent)
            sourceMapNamesPolicy = nyx.jsSourceMapNamesPolicy

        if (nyx.jsTarget.isPresent)
            target = nyx.jsTarget

        if (nyx.jsUseEsClasses.isPresent)
            useEsClasses = nyx.jsUseEsClasses
    }

    private fun KotlinCommonCompilerOptions.configureCommonCompilerOptions() {
        val nyx = this@NyxKotlinExtension
        if (nyx.apiVersion.isPresent)
            apiVersion = nyx.apiVersion.map { KotlinVersion.fromVersion(it) }.get()

        if (nyx.languageVersion.isPresent)
            languageVersion = nyx.languageVersion.map { KotlinVersion.fromVersion(it) }.get()

        if (nyx.warningsAsErrors.isPresent)
            allWarningsAsErrors = nyx.warningsAsErrors.get()

        if (nyx.progressiveMode.isPresent)
            progressiveMode = nyx.progressiveMode

        if (nyx.extraWarnings.isPresent)
            extraWarnings = nyx.extraWarnings

        if (nyx.optIn.isPresent)
            optIn = nyx.optIn

        if (nyx.suppressWarnings.isPresent)
            suppressWarnings = nyx.suppressWarnings.get()

        if (nyx.compilerArgs.isPresent)
            freeCompilerArgs.addAll(nyx.compilerArgs)

        if (nyx.suppressedWarnings.isPresent)
            freeCompilerArgs.addAll(nyx.suppressedWarnings.get().map { "-Xsuppress-warning=$it" })
    }

    public companion object {
        /**
         * The name this extension is added with.
         */
        public const val NAME: String = "kotlin"

        private const val DOKKA_PLUGIN_ID = "org.jetbrains.dokka"
    }
}
