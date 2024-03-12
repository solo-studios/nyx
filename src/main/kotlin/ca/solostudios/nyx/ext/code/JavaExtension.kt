package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.isFalse
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.java
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.tasks
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
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
    withSourcesJar: Property<Boolean>,
    withJavadocJar: Property<Boolean>,
) : ConfiguresProject, HasProject {
    public val encoding: Property<String> = property<String>().convention(encoding)
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(warningsAsErrors)
    public val allWarnings: Property<Boolean> = property<Boolean>().convention(allWarnings)
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(suppressWarnings)
    public val jvmToolchain: Property<Int> = property<Int>().convention(jvmToolchain)
    public val jvmTarget: Property<Int> = property<Int>().convention(jvmTarget)
    public val withSourcesJar: Property<Boolean> = property<Boolean>().convention(withSourcesJar)
    public val withJavadocJar: Property<Boolean> = property<Boolean>().convention(withJavadocJar)

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

                if (warningsAsErrors.isTrue)
                    options.compilerArgs.add("-Werror")

                if (allWarnings.isTrue)
                    options.compilerArgs.add("-Xlint:all")

                if (suppressWarnings.isFalse)
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
