/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxCompileExtension.kt is part of nyx
 * Last modified on 26-01-2025 09:48 p.m.
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

/**
 * An extension to configure all compilation plugins. (java, kotlin, etc.)
 */
public class NyxCompileExtension(override val project: Project) : InternalNyxExtension {
    /**
     * If all warnings are enabled.
     *
     * @see NyxJavaExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If compilers outputting warnings as errors is enabled.
     *
     * @see NyxJavaExtension.warningsAsErrors
     * @see NyxKotlinExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If all warnings should be suppressed.
     *
     * @see NyxJavaExtension.suppressWarnings
     * @see NyxKotlinExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If the `LICENSE` file should be distributed in the jar.
     *
     * The license is resolved from nearest project that contains a `LICENSE`
     * file (ignoring any file extensions).
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
     * If zip64 support should be enabled for the resulting jar files.
     */
    public val zip64: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If the `build` task should depend on all [Jar] tasks.
     */
    public val buildDependsOnJar: Property<Boolean> = property<Boolean>().convention(true)

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
     * If the sources jar is enabled.
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see NyxJavaExtension.sourcesJar
     * @see NyxKotlinExtension.sourcesJar
     */
    public val sourcesJar: Property<Boolean> = property()

    /**
     * If the javadoc jar is enabled.
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see NyxJavaExtension.javadocJar
     * @see NyxKotlinExtension.javadocJar
     */
    public val javadocJar: Property<Boolean> = property()

    /**
     * Enables reproducible builds.
     *
     * See:
     * [Gradle Reproducible Builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives)
     */
    public val reproducibleBuilds: Property<Boolean> = property<Boolean>().convention(true)

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
     * Enables distributing the LICENSE file in all jar files
     *
     * @see distributeLicense
     */
    public fun withDistributeLicense() {
        distributeLicense = true
    }

    /**
     * Enables zip64 support
     *
     * @see zip64
     */
    public fun withZip64() {
        zip64 = true
    }

    /**
     * Enables the build task depending on all tasks of type [Jar]
     *
     * @see buildDependsOnJar
     */
    public fun withBuildDependsOnJar() {
        buildDependsOnJar = true
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
     * Enables reproducible builds
     *
     * @see reproducibleBuilds
     */
    public fun withReproducibleBuilds() {
        reproducibleBuilds = true
    }

    override fun configureProject() {
        tasks {
            if (distributeLicense.isTrue) {
                val licenseFile = project.firstFileMatchingInParents { it.nameWithoutExtension.contains("LICENSE", ignoreCase = true) }
                val copyingFile = project.firstFileMatchingInParents {
                    it.nameWithoutExtension.equals("COPYING", ignoreCase = true) && !it.name.contains("LESSER", ignoreCase = true)
                }
                val lesserCopyingFile = project.firstFileMatchingInParents {
                    it.name.contains("COPYING", ignoreCase = true) && it.name.contains("LESSER", ignoreCase = true)
                }
                val noticeFile = project.firstFileMatchingInParents { it.nameWithoutExtension.equals("NOTICE", ignoreCase = true) }

                withType<Jar>().configureEach {
                    metaInf {
                        if (licenseFile != null)
                            from(licenseFile)
                        if (copyingFile != null)
                            from(copyingFile)
                        if (lesserCopyingFile != null)
                            from(lesserCopyingFile)
                        if (noticeFile != null)
                            from(noticeFile)
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

    private fun Project.firstFileMatchingInParents(filter: (File) -> Boolean): File? {
        val projectIterable = Iterable {
            object : Iterator<Project> {
                var currentProject: Project? = this@firstFileMatchingInParents.project

                override fun hasNext(): Boolean = currentProject != null
                override fun next(): Project {
                    val project = currentProject
                    if (project == null)
                        throw NoSuchElementException("No further elements")

                    currentProject = project.parent
                    return project
                }
            }
        }
        return projectIterable.firstNotNullOfOrNull { project ->
            project.projectDir.listFiles().asSequence().sorted().firstOrNull(filter)
        }
    }

    public companion object {
        /**
         * The name this extension is added with.
         */
        public const val NAME: String = "compile"
    }
}
