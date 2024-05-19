package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.isFalse
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.java
import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType

public open class JavaExtension(
    override val project: Project,
    encoding: Provider<String>,
    warningsAsErrors: Provider<Boolean>,
    allWarnings: Provider<Boolean>,
    suppressWarnings: Provider<Boolean>,
    jvmToolchain: Provider<Int>,
    jvmTarget: Provider<Int>,
    withSourcesJar: Property<Boolean>,
    withJavadocJar: Property<Boolean>,
) : ConfiguresProject, HasProject {
    /**
     * The encoding to be used for all files.
     *
     * Defaults to `UTF-8`.
     *
     * @see CompileExtension.encoding
     */
    public val encoding: Property<String> = property<String>().convention(encoding)

    /**
     * Enables all compilers to output warnings as errors.
     *
     * @see CompileExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(warningsAsErrors)

    /**
     * Enables all warnings
     *
     * @see CompileExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(allWarnings)

    /**
     * Suppresses all warnings
     *
     * @see CompileExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(suppressWarnings)

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaToolchainSpec.getLanguageVersion
     * @see CompileExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property<Int>().convention(jvmToolchain)

    /**
     * The jvm target to use.
     *
     * @see JavaPluginExtension.setTargetCompatibility
     * @see CompileExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property<Int>().convention(jvmTarget)

    /**
     * Enables sources jar
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see CompileExtension.withSourcesJar
     */
    public val withSourcesJar: Property<Boolean> = property<Boolean>().convention(withSourcesJar)

    /**
     * Enables javadoc jar
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see CompileExtension.withJavadocJar
     */
    public val withJavadocJar: Property<Boolean> = property<Boolean>().convention(withJavadocJar)

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

    override fun onLoad() {}

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

                if (warningsAsErrors.isTrue)
                    options.compilerArgs.add("-Werror")

                if (allWarnings.isTrue)
                    options.compilerArgs.add("-Xlint:all")

                if (suppressWarnings.isFalse)
                    options.isWarnings = false

                if (jvmTarget.isPresent)
                    options.release = jvmTarget.get()

                if (compilerArgs.isPresent)
                    options.compilerArgs.addAll(compilerArgs.get())
            }

            withType<Javadoc>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()
            }
        }
    }
}
