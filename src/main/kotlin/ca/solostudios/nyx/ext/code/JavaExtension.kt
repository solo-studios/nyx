package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.java
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.toolchain.JavaLanguageVersion
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
) : ConfiguresProject, HasProject {
    public val encoding: Property<String> = property<String>().convention(encoding)
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(warningsAsErrors)
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(allWarnings)
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(suppressWarnings)
    public val jvmToolchain: Property<Int> = property<Int>().convention(jvmToolchain)
    public val jvmTarget: Property<Int> = property<Int>().convention(jvmTarget)

    override fun configureProject() {
        if (jvmToolchain.isPresent) {
            java {
                toolchain {
                    languageVersion = JavaLanguageVersion.of(jvmToolchain.get())
                }
            }
        }

        if (jvmTarget.isPresent) {
            java {
                targetCompatibility = JavaVersion.toVersion(jvmTarget.get())
                sourceCompatibility = JavaVersion.toVersion(jvmTarget.get())
            }
        }

        tasks {
            withType<JavaCompile>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()

                if (warningsAsErrors.isPresent && warningsAsErrors.get())
                    options.compilerArgs.add("-Werror")

                if (allWarnings.isPresent && allWarnings.get())
                    options.compilerArgs.add("-Xlint:all")

                if (suppressWarnings.isPresent && !suppressWarnings.get())
                    options.isWarnings = false

                if (jvmTarget.isPresent)
                    options.release = jvmTarget.get()
            }

            withType<Javadoc>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()
            }
        }
    }
}
