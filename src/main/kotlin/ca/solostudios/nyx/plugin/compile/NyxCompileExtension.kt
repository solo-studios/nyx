/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file CompileExtension.kt is part of nyx
 * Last modified on 10-06-2024 03:21 p.m.
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
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.tasks
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import java.io.File

public class NyxCompileExtension(override val project: Project) : InternalNyxExtension {
    /**
     * Enables all warnings
     *
     * @see NyxJavaExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * Enables all compilers to output warnings as errors.
     *
     * @see NyxJavaExtension.warningsAsErrors
     * @see NyxKotlinExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * Distributes the `LICENSE` file in the jar.
     *
     * The license is resolved from nearest project that contains a `LICENSE` file.
     */
    public val distributeLicense: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * The encoding to be used for all files.
     *
     * Defaults to `UTF-8`.
     *
     * @see NyxJavaExtension.encoding
     */
    public val encoding: Property<String> = property<String>().convention("UTF-8")

    /**
     * If zip64 support should be enabled for the resulting jar files
     */
    public val zip64: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If the `build` task should depend on all `Jar` tasks.
     */
    public val buildDependsOnJar: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * Suppresses all warnings
     *
     * @see NyxJavaExtension.suppressWarnings
     * @see NyxKotlinExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaToolchainSpec.getLanguageVersion
     * @see NyxJavaExtension.jvmToolchain
     * @see NyxKotlinExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property()

    /**
     * The jvm target to use.
     *
     * @see JavaPluginExtension.setTargetCompatibility
     * @see NyxJavaExtension.jvmTarget
     * @see NyxKotlinExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property()

    /**
     * Enables sources jar
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see NyxJavaExtension.withSourcesJar
     * @see NyxKotlinExtension.withSourcesJar
     */
    public val withSourcesJar: Property<Boolean> = property()

    /**
     * Enables javadoc jar
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see NyxJavaExtension.withJavadocJar
     * @see NyxKotlinExtension.withJavadocJar
     */
    public val withJavadocJar: Property<Boolean> = property()

    /**
     * Enables reproducible builds.
     *
     * See: [Gradle Reproducible Builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives)
     */
    public val reproducibleBuilds: Property<Boolean> = property<Boolean>().convention(true)


    /**
     * Enables warnings as errors
     *
     * @see warningsAsErrors
     */
    public fun warningsAsErrors() {
        warningsAsErrors = true
    }

    /**
     * Enables the sources jar
     *
     * @see withSourcesJar
     */
    public fun withSourcesJar() {
        withSourcesJar = true
    }

    /**
     * Enables the javadoc jar
     *
     * @see withJavadocJar
     */
    public fun withJavadocJar() {
        withJavadocJar = true
    }

    override fun configureProject() {
        tasks {
            if (distributeLicense.isTrue) {
                val license = project.findNearestLicense()

                if (license != null) {
                    withType<Jar>().configureEach {
                        from(license) {
                            rename { "${it}_${project.rootProject.name}" }
                        }
                    }
                }
            }

            if (buildDependsOnJar.isTrue) {
                named<Task>("build") {
                    dependsOn(withType<Jar>())
                }
            }

            if (zip64.isTrue) {
                withType<Zip>().configureEach {
                    isZip64 = true
                }
            }

            if (reproducibleBuilds.isTrue) {
                withType<AbstractArchiveTask>().configureEach {
                    isPreserveFileTimestamps = false
                    isReproducibleFileOrder = true
                }
            }
        }
    }

    internal fun Project.findNearestLicense(): File? {
        var project: Project? = this
        while (project != null) {
            // Sort for consistent ordering (order is not guaranteed)
            val licenseFile = project.projectDir.listFiles()?.sortedBy { it.name }?.firstOrNull {
                it.nameWithoutExtension == "LICENSE" && it.exists()
            }

            if (licenseFile != null)
                return licenseFile

            project = project.parent
        }

        return null
    }

    public companion object {
        public const val NAME: String = "compile"
    }
}
