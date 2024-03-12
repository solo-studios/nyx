package ca.solostudios.nyx.ext.code

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.artifacts
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.kotlin
import ca.solostudios.nyx.util.listProperty
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.publishing
import ca.solostudios.nyx.util.tasks
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.slf4j.kotlin.error
import org.slf4j.kotlin.getLogger

public open class KotlinExtension(
    override val project: Project,
    warningsAsErrors: Provider<Boolean>,
    suppressWarnings: Provider<Boolean>,
    jvmToolchain: Provider<Int>,
    jvmTarget: Provider<Int>,
    withSourcesJar: Provider<Boolean>,
    withJavadocJar: Provider<Boolean>,
) : ConfiguresProject, HasProject {
    private val logger by getLogger()

    public val apiVersion: Property<String> = property()
    public val languageVersion: Property<String> = property()
    public val optIn: ListProperty<String> = listProperty()
    public val warningsAsErrors: Property<Boolean> = property<Boolean>().convention(warningsAsErrors)
    public val explicitApi: Property<ExplicitApiMode> = property()
    public val suppressWarnings: Property<Boolean> = property<Boolean>().convention(suppressWarnings)
    public val jvmToolchain: Property<Int> = property<Int>().convention(jvmToolchain)
    public val jvmTarget: Property<Int> = property<Int>().convention(jvmTarget)
    public val withSourcesJar: Property<Boolean> = property<Boolean>().convention(withSourcesJar)
    public val withJavadocJar: Property<Boolean> = property<Boolean>().convention(withJavadocJar)

    public fun explicitApi() {
        explicitApi = ExplicitApiMode.Strict
    }

    public fun explicitApiWarning() {
        explicitApi = ExplicitApiMode.Warning
    }

    override fun configureProject() {
        if (withJavadocJar.isTrue)
            addDokkaJavadocJarTask()

        kotlin {
            if (jvmToolchain.isPresent)
                jvmToolchain(jvmToolchain.get())

            when (this) {
                is KotlinJvmProjectExtension -> {
                    configureCommonCompilations(target)

                    target.compilations.configureEach {
                        configureJvmCompilation(this)
                    }
                }

                is KotlinMultiplatformExtension -> {
                    if (withSourcesJar.isTrue)
                        withSourcesJar(publish = true)

                    targets.configureEach {
                        configureCommonCompilations(this)

                        if (this is KotlinJvmTarget) {
                            compilations.configureEach {
                                configureJvmCompilation(this)
                            }
                        }
                    }
                }

                else -> {
                    // Throw error for other platforms
                    logger.error(NotImplementedError("Unsupported kotlin platform.")) {
                        """
                            This kotlin platform is not supported. Currently the only supported platforms are kotlin/jvm and kotlin/multiplatform.

                            Please open an issue with Nyx if you would like support for the current kotlin platform.
                        """.trimIndent()
                    }
                }
            }
        }
    }

    internal fun addDokkaJavadocJarTask() {
        if (!project.plugins.hasPlugin("org.jetbrains.dokka")) {
            // Project does not have dokka plugin. do not attempt to configure it.
            logger.error(IllegalStateException()) {
                """
                    Requested to add a javadoc jar for a kotlin project, however the dokka plugin has not been applied.

                    Please apply the dokka plugin in order to generate the javadoc jar for kotlin source code:
                    plugins {
                        id("org.jetbrains.dokka") version "<version>"
                    }
                """.trimIndent()
            }
            return
        }

        // Ensure dokka is applied
        project.apply<DokkaPlugin>()

        tasks {
            val dokkaHtml by named<DokkaTask>("dokkaHtml")
            // configure the javadoc jar task without creating it (either get an existing task or create a new task)
            val javadocJar by if (findByName("javadocJar") != null) named<Jar>("javadocJar") {
                configureJavadocJar(dokkaHtml)
            } else register<Jar>("javadocJar") {
                configureJavadocJar(dokkaHtml)
            }

            artifacts {
                add("archives", javadocJar)
            }

            if (project.plugins.hasPlugin("publishing")) {
                // Add javadoc jar to maven publications (is this the best way to do it?)
                publishing {
                    publications.withType<MavenPublication>().configureEach {
                        artifact(javadocJar)
                    }
                }
            }
        }
    }

    internal fun Jar.configureJavadocJar(dokkaHtml: DokkaTask) {
        dependsOn(dokkaHtml)
        from(dokkaHtml.outputDirectory)
        archiveClassifier = "javadoc"
        description = "Assembles a jar archive containing the main dokka kdoc"
        group = JavaBasePlugin.DOCUMENTATION_GROUP
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
