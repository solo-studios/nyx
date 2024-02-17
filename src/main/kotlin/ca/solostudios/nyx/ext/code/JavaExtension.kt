package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.util.tasks
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.HasProject

public open class JavaExtension(override val project: Project) : HasProject {
    internal fun configureProject(
        encoding: Property<String>,
        warningsAsErrors: Provider<Boolean>,
        allWarnings: Provider<Boolean>,
    ) {
        tasks {
            withType<JavaCompile>().configureEach {
                with(options) {
                    if (encoding.isPresent)
                        options.encoding = encoding.get()

                    if (warningsAsErrors.isPresent && warningsAsErrors.get())
                        compilerArgs.add("-Werror")

                    if (allWarnings.isPresent && allWarnings.get())
                        compilerArgs.add("-Xlint:all")
                }
            }

            withType<Javadoc>().configureEach {
                if (encoding.isPresent)
                    options.encoding = encoding.get()
            }
        }
    }
}
