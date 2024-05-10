package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePlugin
import java.io.File

public class CompileExtension(override val project: Project) : ConfiguresProject, HasProject {
    /**
     * Enables all compilers to output warnings as errors.
     *
     * @see JavaExtension.warningsAsErrors
     * @see KotlinExtension.warningsAsErrors
     */
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * Enables all warnings
     *
     * @see JavaExtension.allWarnings
     */
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(false)

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
     * @see JavaExtension.encoding
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
     * @see JavaExtension.suppressWarnings
     * @see KotlinExtension.suppressWarnings
     */
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * The jvm toolchain release to use.
     *
     * @see JavaToolchainSpec.getLanguageVersion
     * @see JavaExtension.jvmToolchain
     * @see KotlinExtension.jvmToolchain
     */
    public val jvmToolchain: Property<Int> = property()

    /**
     * The jvm target to use.
     *
     * @see JavaPluginExtension.setTargetCompatibility
     * @see JavaExtension.jvmTarget
     * @see KotlinExtension.jvmTarget
     */
    public val jvmTarget: Property<Int> = property()

    /**
     * Enables sources jar
     *
     * @see JavaPluginExtension.withSourcesJar
     * @see JavaExtension.withSourcesJar
     * @see KotlinExtension.withSourcesJar
     */
    public val withSourcesJar: Property<Boolean> = property()

    /**
     * Enables javadoc jar
     *
     * @see JavaPluginExtension.withJavadocJar
     * @see JavaExtension.withJavadocJar
     * @see KotlinExtension.withJavadocJar
     */
    public val withJavadocJar: Property<Boolean> = property()

    /**
     * Enables reproducible builds.
     *
     * See: [Gradle Reproducible Builds](https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives)
     */
    public val reproducibleBuilds: Property<Boolean> = property<Boolean>().convention(true)

    @Nested
    public val kotlin: KotlinExtension = KotlinExtension(
        project,
        warningsAsErrors,
        suppressWarnings,
        jvmToolchain,
        jvmTarget,
        withSourcesJar,
        withJavadocJar,
    )

    @Nested
    public val java: JavaExtension = JavaExtension(
        project,
        encoding,
        warningsAsErrors,
        allWarnings,
        suppressWarnings,
        jvmToolchain,
        jvmTarget,
        withSourcesJar,
        withJavadocJar,
    )


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

    /**
     * Configures the kotlin compiler
     */
    public fun kotlin(action: Action<KotlinExtension>) {
        action.execute(kotlin)
    }

    /**
     * Configures the kotlin compiler
     */
    public fun kotlin(action: (KotlinExtension).() -> Unit) {
        kotlin.apply(action)
    }

    /**
     * Configures the java compiler
     */
    public fun java(action: Action<JavaExtension>) {
        action.execute(java)
    }

    /**
     * Configures the java compiler
     */
    public fun java(action: (JavaExtension).() -> Unit) {
        java.apply(action)
    }

    override fun onLoad() {
        java.onLoad()

        project.plugins.withType<KotlinBasePlugin> {
            kotlin.onLoad()
        }
    }

    override fun configureProject() {
        project.plugins.withId("java") {
            java.configureProject()
        }

        project.plugins.withType<KotlinBasePlugin> {
            kotlin.configureProject()
        }

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
}
