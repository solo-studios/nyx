package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.property
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.HasProject
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets

public open class KotlinExtension(override val project: Project) : HasProject {
    public val apiVersion: Property<String> = property()
    public val languageVersion: Property<String> = property()
    public val optIn: ListProperty<String> = listProperty()

    internal fun configureProject(warningsAsErrors: Provider<Boolean>) {
        with(project.kotlinExtension) {
            targets.onEach { target ->

                target.compilations.configureEach {
                    if (apiVersion.isPresent)
                        kotlinOptions.apiVersion = apiVersion.get()

                    if (languageVersion.isPresent)
                        kotlinOptions.languageVersion = languageVersion.get()

                    if (warningsAsErrors.isPresent)
                        kotlinOptions.allWarningsAsErrors = warningsAsErrors.get()

                    if (optIn.isPresent)
                        kotlinOptions.options.optIn = optIn
                }
            }
        }
    }
}
