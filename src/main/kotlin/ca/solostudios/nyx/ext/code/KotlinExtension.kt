package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.kotlin
import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.property
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleJavaTargetExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

public open class KotlinExtension(
    override val project: Project,
    warningsAsErrors: Provider<Boolean>,
    suppressWarnings: Provider<Boolean>,
    jvmToolchain: Provider<Int>,
    jvmTarget: Provider<Int>,
) : ConfiguresProject, HasProject {
    public val apiVersion: Property<String> = property()
    public val languageVersion: Property<String> = property()
    public val optIn: ListProperty<String> = listProperty()
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(warningsAsErrors)
    public val explicitApi: Property<ExplicitApiMode> = property()
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(suppressWarnings)
    public val jvmToolchain: Property<Int> = property<Int>().convention(jvmToolchain)
    public val jvmTarget: Property<Int> = property<Int>().convention(jvmTarget)

    public fun explicitApi() {
        explicitApi = ExplicitApiMode.Strict
    }

    public fun explicitApiWarning() {
        explicitApi = ExplicitApiMode.Warning
    }

    override fun configureProject() {
        kotlin {
            if (jvmToolchain.isPresent)
                jvmToolchain(jvmToolchain.get())

            when (this) {
                is KotlinSingleJavaTargetExtension -> {
                    configureCommonCompilations(target)

                    if (this is KotlinJvmProjectExtension) {
                        target.compilations.configureEach {
                            configureJvmCompilation(this)
                        }
                    }
                }

                is KotlinMultiplatformExtension -> {
                    targets.configureEach {
                        configureCommonCompilations(this)

                        if (this is KotlinJvmTarget) {
                            compilations.configureEach {
                                configureJvmCompilation(this)
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun configureJvmCompilation(compilation: KotlinCompilation<KotlinJvmOptions>) {
        if (jvmTarget.isPresent)
            compilation.kotlinOptions.jvmTarget = /* evil */ if (jvmTarget.get() == 8) "1.8" else jvmTarget.get().toString()
    }

    internal fun configureCommonCompilations(target: KotlinTarget) {
        target.compilations.configureEach {
            kotlinOptions

            if (apiVersion.isPresent)
                kotlinOptions.apiVersion = apiVersion.get()

            if (languageVersion.isPresent)
                kotlinOptions.languageVersion = languageVersion.get()

            if (warningsAsErrors.isPresent)
                kotlinOptions.allWarningsAsErrors = warningsAsErrors.get()

            if (optIn.isPresent)
                kotlinOptions.options.optIn = optIn

            if (suppressWarnings.isPresent)
                kotlinOptions.suppressWarnings = suppressWarnings.get()
        }

    }
}
