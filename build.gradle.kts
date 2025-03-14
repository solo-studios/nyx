/*
 * Copyright (c) 2023-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file build.gradle.kts is part of nyx
 * Last modified on 08-03-2025 06:31 p.m.
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

@file:Suppress("UnstableApiUsage")

import ca.solostudios.nyx.util.soloStudios
import ca.solostudios.nyx.util.soloStudiosSnapshots
import com.sass_lang.embedded_protocol.OutputStyle
import groovy.text.SimpleTemplateEngine
import io.freefair.gradle.plugins.sass.SassCompile
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.internal.component.external.model.TestFixturesSupport
import org.gradle.jvm.component.internal.DefaultJvmSoftwareComponent
import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
import java.io.StringWriter
import java.time.Year

plugins {
    `kotlin-dsl`

    `maven-publish`

    `java-gradle-plugin`

    `jvm-test-suite`
    `java-test-fixtures`

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.dokka)

    alias(libs.plugins.ksp)

    alias(libs.plugins.axion.release)

    alias(libs.plugins.gradle.plugin.development)
    alias(libs.plugins.gradle.plugin.functional.test)
    alias(libs.plugins.gradle.plugin.unit.test)

    alias(libs.plugins.nyx)

    alias(libs.plugins.gradle.publish)

    alias(libs.plugins.allure.gradle)

    alias(libs.plugins.sass.base)
}

nyx {
    info {
        name = "Nyx"
        group = "ca.solo-studios"
        module = "nyx"
        version = scmVersion.version
        description = """
            Nyx is a gradle plugin intended to simplify gradle buildscripts using common conventions.
        """.trimIndent()

        organizationUrl = "https://solo-studios.ca/"
        organizationName = "Solo Studios"

        developer {
            id = "solonovamax"
            name = "solonovamax"
            email = "solonovamax@12oclockpoint.com"
            url = "https://solonovamax.gay"
        }

        repository.fromGithub("solo-studios", "nyx")
        license.useMIT()
    }

    compile {
        withJavadocJar()
        withSourcesJar()

        allWarnings = true
        warningsAsErrors = true
        distributeLicense = true
        buildDependsOnJar = true
        reproducibleBuilds = true
        jvmToolchain = 17

        kotlin {
            apiVersion = "1.8"
            languageVersion = "1.8"

            withExplicitApi()
            allOpen.annotations("ca.solostudios.nyx.internal.AllOpen")
        }
    }

    publishing {
        withSignedPublishing()

        repositories {
            maven {
                name = "Sonatype"

                val repositoryId: String? by project
                url = when {
                    repositoryId != null -> uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/")
                    else                 -> uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }

                credentials(PasswordCredentials::class)
            }
            maven {
                name = "SoloStudiosReleases"

                url = uri("https://maven.solo-studios.ca/releases/")

                credentials(PasswordCredentials::class)
                authentication { // publishing doesn't work without this for some reason
                    create<BasicAuthentication>("basic")
                }
            }
            maven {
                name = "SoloStudiosSnapshots"

                url = uri("https://maven.solo-studios.ca/snapshots/")

                credentials(PasswordCredentials::class)
                authentication { // publishing doesn't work without this for some reason
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}

repositories {
    soloStudios()
    soloStudiosSnapshots()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    api(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlinx.serialization)
    testFixturesApi(libs.bundles.kotlinx.serialization)
    testFixturesApi(libs.kotlinx.serialization.properties)

    api(libs.ksp.service.annotation)
    ksp(libs.ksp.service.annotation)

    compileOnly(gradleApi("8.8"))

    api(libs.apache.commons)

    implementation(libs.slf4k)

    implementation(libs.bundles.maven)

    implementation(libs.fuel)

    // Kotlin stuff
    compileOnly(libs.kotlin.plugin)
    compileOnly(libs.dokka.plugin)

    // Minecraft plugins
    compileOnly(libs.bundles.loom)

    compileOnly(libs.github.release)
    compileOnly(libs.modrinth.minotaur)

    compileOnly(libs.bundles.neogradle)

    testFixturesApi(libs.bundles.junit)
    testFixturesApi(libs.bundles.kotest)
    testFixturesApi(gradleKotlinDsl())

    testFixturesCompileOnly(gradleApi("8.8"))
    testFixturesCompileOnly(gradleTestKit("8.8"))
}

gradlePlugin {
    website = nyx.info.repository.projectUrl
    vcsUrl = nyx.info.repository.projectUrl

    plugins {
        create("Nyx") {
            displayName = "Nyx"
            id = "ca.solo-studios.nyx"
            implementationClass = "ca.solostudios.nyx.NyxPlugin"
            description = nyx.project.description

            tags = listOf("kotlin", "publishing", "convention")
        }

        create("SonatypePublish") {
            displayName = "Sonatype Publish"
            id = "ca.solo-studios.sonatype-publish"
            implementationClass = "ca.solostudios.nyx.sonatype.SonatypePublishPlugin"
            description = """
                Sonatype Publish is a gradle plugin for publishing to the new Sonatype Central repository.
            """.trimIndent()
            tags = listOf("publishing", "sonatype", "maven", "maven-central")
        }
    }
}

allure {
    version = "2.29.0"
    adapter.autoconfigure = false
    adapter.autoconfigureListeners = false
    adapter.frameworks {
        junit5.enabled = false
    }
}

functionalTest {
    // currently they all just execute using gradle 8.10
    // TODO fix this
    testingStrategies = buildSet {
        add(strategies.coverageForGradleVersion("8.8"))
    }
    // testingStrategies = buildSet {
    //     add(strategies.coverageForGradleVersion("8.6"))
    //     add(strategies.coverageForGradleVersion("8.7"))
    //     add(strategies.coverageForGradleVersion("8.10"))
    //     add(strategies.coverageForLatestNightlyVersion)
    // }
    dependencies {
        implementation(testFixtures.modify(project))
        implementation(gradleTestKit())

        pluginUnderTestMetadata(libs.kotlin.plugin)
        pluginUnderTestMetadata(libs.fabric.loom)
    }
}

sass {
    omitSourceMapUrl = true
    outputStyle = OutputStyle.COMPRESSED
    sourceMapContents = false
    sourceMapEmbed = false
    sourceMapEnabled = false
}

val dokkaDir = project.layout.projectDirectory.dir("dokka")
val dokkaBuildDir = dokka.basePublicationsDirectory
val dokkaTemplates = dokkaDir.dir("templates")

val processDokkaIncludes by tasks.registering(ProcessResources::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP

    val includes = dokkaDir.dir("includes")
    val projectInfo = mapOf(
        "group" to nyx.info.group,
        "module" to nyx.info.module.get(),
        "version" to nyx.info.version
    )

    into(dokkaBuildDir.dir("includes"))
    inputs.properties(projectInfo)
    inputs.files(includes)

    from(includes) {
        exclude { it.name.startsWith("_") }

        val dependencyInformation = processTemplate(includes.file("_dependency.md").asFile, mapOf("project" to projectInfo))
        filter<ReplaceTokens>(
            "tokens" to mapOf("dependencies" to dependencyInformation),
            "beginToken" to "{{",
            "endToken" to "}}"
        )
    }
}

val compileDokkaSass by tasks.registering(SassCompile::class) {
    group = BasePlugin.BUILD_GROUP
    source = fileTree(dokkaDir.dir("styles"))
    destinationDir = dokkaBuildDir.dir("styles")
}

dokka {
    moduleName = nyx.info.name
    moduleVersion = nyx.info.version
    dokkaSourceSets.configureEach {
        includes.from(fileTree(processDokkaIncludes.map { it.destinationDir }))
        reportUndocumented = true
        documentedVisibilities = setOf(VisibilityModifier.Public, VisibilityModifier.Protected)

        @Suppress("unused")
        externalDocumentationLinks {
            val gradle by registering {
                url("https://docs.gradle.org/${gradle.gradleVersion}/kotlin-dsl/")
                packageListUrl("https://docs.gradle.org/${gradle.gradleVersion}/kotlin-dsl/gradle/package-list")
            }
            val kotlinPlugin by registering {
                url("https://kotlinlang.org/api/kotlin-gradle-plugin/")
                packageListUrl("https://kotlinlang.org/api/kotlin-gradle-plugin/package-list")
            }
        }

        sourceLink {
            localDirectory = projectDir.resolve("src")
            remoteUrl = nyx.info.repository.projectUrl.map { uri("$it/blob/${scmVersion.scmPosition.revision}/src") }
            remoteLineSuffix = "#L"
        }
    }

    pluginsConfiguration {
        html {
            homepageLink = nyx.info.repository.projectUrl
            footerMessage = "© ${Year.now()} Copyright solo-studios"

            separateInheritedMembers = false

            templatesDir = dokkaTemplates

            customStyleSheets.from(fileTree(compileDokkaSass.flatMap { it.destinationDir }))
        }
    }
}

testing.suites {
    withType<JvmTestSuite>().configureEach {
        useJUnitJupiter()

        dependencies {
            // implementation(gradleTestKit())

            implementation(testFixtures(project()))

            implementation(libs.kotlin.plugin)
            implementation(libs.dokka.plugin)
            implementation(libs.fabric.loom)
            // implementation(libs.quilt.loom)
            // implementation(libs.architectury.loom)
            implementation(libs.github.release)
            implementation(libs.modrinth.minotaur)

            // the version of org.codehause.groovy included by neogradle breaks tests
            implementation(libs.neogradle.mixin) {
                exclude(group = "org.codehaus.groovy")
            }
            implementation(libs.neogradle.userdev) {
                exclude(group = "org.codehaus.groovy")
            }
        }

        targets.configureEach {
            testTask.configure {
                useJUnitPlatform()

                failFast = false
                finalizedBy(tasks.allureReport)

                reports {
                    html.required = false
                    junitXml.required = false
                }
            }
        }
    }

    val test by getting(JvmTestSuite::class)

    // val integrationTest by registering(JvmTestSuite::class) {
    //     gradlePlugin.testSourceSet(this.sources)
    //     testType = TestSuiteType.INTEGRATION_TEST
    //
    //     targets.configureEach {
    //         testTask.configure {
    //             shouldRunAfter(test)
    //         }
    //     }
    // }
}

tasks {
    withType<DokkaGenerateTask>().configureEach {
        inputs.files(dokkaBuildDir.dir("styles"), dokkaTemplates)

        dependsOn(compileDokkaSass, processDokkaIncludes)
    }

    // val functionalTest by registering {
    //     group = JavaBasePlugin.VERIFICATION_GROUP
    //     description = "Runs the functional test suite."
    //     dependsOn(functionalTest.testTasks.elements)
    // }

    withType<Test>().configureEach {
        finalizedBy(allureReport)

        useJUnitPlatform()

        reports {
            html.required = false
            junitXml.required = false
        }

        systemProperty("nyx.test.tmp", temporaryDir)
        systemProperty("gradle.build.dir", layout.buildDirectory.get().asFile)
        systemProperty("gradle.task.name", name)
        systemProperty("kotest.framework.config.fqn", "ca.solostudios.nyx.kotest.KotestConfig")
        systemProperty("kotest.framework.classpath.scanning.config.disable", true)
        systemProperty("kotest.framework.classpath.scanning.autoscan.disable", true)
    }

    validatePlugins {
        enableStricterValidation = true
    }

    allureReport {
        clean = true
    }

    check {
        dependsOn(withType<Test>(), functionalTest.testTasks.elements)
    }
}

fun processTemplate(templateFile: File, templateProperties: Map<String, Any?>): String {
    val engine = SimpleTemplateEngine()
    val template = engine.createTemplate(templateFile)
    val writer = StringWriter()

    // SimpleTemplateEngine expects to be able to mutate the map internally.
    template.make(templateProperties.toMutableMap())
        .writeTo(writer)
    return writer.toString()
}

val Project.isSnapshot: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")


project.components.withType<DefaultJvmSoftwareComponent> {
    val testFixturesFeature by features.named(TestFixturesSupport.TEST_FIXTURES_FEATURE_NAME)
    withVariantsFromConfiguration(testFixturesFeature.apiElementsConfiguration) {
        skip()
    }
    withVariantsFromConfiguration(testFixturesFeature.runtimeElementsConfiguration) {
        skip()
    }
}
