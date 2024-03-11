package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.findLicenseFile
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.Zip
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

public class CompileExtension(override val project: Project) : ConfiguresProject, HasProject {
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(false)

    public val allWarnings: Property<Boolean> = property<Boolean>().convention(false)

    public val distributeLicense: Property<Boolean> = property<Boolean>().convention(true)

    public val encoding: Property<String> = property<String>().convention("UTF-8")

    public val zip64: Property<Boolean> = property<Boolean>().convention(false)

    public val buildDependsOnJar: Property<Boolean> = property<Boolean>().convention(true)

    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(false)

    public val jvmToolchain: Property<Int> = property()

    public val jvmTarget: Property<Int> = property()

    @Nested
    public val kotlin: KotlinExtension = KotlinExtension(
        project,
        warningsAsErrors,
        suppressWarnings,
        jvmToolchain,
        jvmTarget
    )

    @Nested
    public val java: JavaExtension = JavaExtension(
        project,
        encoding,
        warningsAsErrors,
        allWarnings,
        suppressWarnings,
        jvmToolchain,
        jvmTarget
    )


    public fun withSourcesJar() {
        project.configure<JavaPluginExtension> {
            withSourcesJar()
        }
    }

    public fun withJavadocJar() {
        // configure dokka or javadocs
    }

    public fun kotlin(action: Action<KotlinExtension>) {
        action.execute(kotlin)
    }

    public fun kotlin(action: (KotlinExtension).() -> Unit) {
        kotlin.apply(action)
    }

    public fun java(action: Action<JavaExtension>) {
        action.execute(java)
    }

    public fun java(action: (JavaExtension).() -> Unit) {
        java.apply(action)
    }

    override fun configureProject() {
        if (project.plugins.hasPlugin("java"))
            java.configureProject()

        if (project.plugins.hasPlugin("org.jetbrains.kotlin.jvm"))
            kotlin.configureProject()

        tasks {
            if (distributeLicense.isPresent && distributeLicense.get())
                withType<Jar>().configureEach {
                    val license = project.findLicenseFile()
                    if (license != null)
                        from(license) {
                            rename { "${it}_${project.rootProject.name}" }
                        }
                }

            if (buildDependsOnJar.isPresent && buildDependsOnJar.get())
                named<Task>("build") {
                    dependsOn(withType<Jar>())
                }

            if (zip64.isPresent && zip64.get()) {
                withType<Zip>().configureEach {
                    isZip64 = zip64.get()
                }
            }
        }

    }
}
