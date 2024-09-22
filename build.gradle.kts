/*
 * Copyright (c) 2023-2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file build.gradle.kts is part of nyx
 * Last modified on 22-09-2024 05:05 p.m.
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
 * GRADLE-CONVENTIONS-PLUGIN IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import ca.solostudios.nyx.util.soloStudios

plugins {
    `kotlin-dsl`

    `maven-publish`

    `java-gradle-plugin`

    `jvm-test-suite`
    `java-test-fixtures`

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.serialization)

    alias(libs.plugins.gradle.plugin.development)
    alias(libs.plugins.gradle.plugin.functional.test)
    alias(libs.plugins.gradle.plugin.unit.test)

    alias(libs.plugins.dokka)
    alias(libs.plugins.axion.release)

    alias(libs.plugins.gradle.publish)

    alias(libs.plugins.allure.gradle)

    alias(libs.plugins.nyx)
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
        jvmTarget = 17

        kotlin {
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
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // compileOnly(gradleApi())
    api(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlinx.serialization)

    compileOnly(gradleApi("8.6"))

    api(libs.apache.commons)

    implementation(libs.slf4k)

    implementation(libs.bundles.maven)

    implementation(libs.fuel)

    implementation(libs.bundles.arrow)

    // Kotlin stuff
    compileOnly(libs.kotlin.plugin)
    testApi(libs.kotlin.plugin)
    compileOnly(libs.dokka.plugin)
    testApi(libs.dokka.plugin)

    // Minecraft plugins
    compileOnly(libs.bundles.loom)
    testApi(libs.bundles.loom)

    compileOnly(libs.github.release)
    testApi(libs.github.release)
    compileOnly(libs.modrinth.minotaur)
    testApi(libs.modrinth.minotaur)

    compileOnly(libs.bundles.neogradle)

    testFixturesApi(libs.bundles.junit)
    testFixturesApi(libs.bundles.kotest)

    testFixturesCompileOnly(gradleTestKit())
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
    testingStrategies = buildSet {
        add(strategies.coverageForGradleVersion("8.6"))
        add(strategies.coverageForGradleVersion("8.7"))
        add(strategies.coverageForGradleVersion("8.10"))
    }
    dependencies {
        implementation(gradleTestKit())
        implementation(testFixtures(project()))
    }
}

tasks {
    val functionalTest by registering {
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Runs the functional test suite."
        dependsOn(functionalTest.testTasks.elements)
    }

    withType<Test>().configureEach {
        finalizedBy(allureReport)

        useJUnitPlatform()

        reports {
            html.required.set(false)
            junitXml.required.set(false)
        }

        systemProperty("nyx.test.work.tmp", temporaryDir.resolve("work"))
        systemProperty("gradle.build.dir", layout.buildDirectory.get().asFile)
        systemProperty("gradle.task.name", name)
        systemProperty("kotest.framework.classpath.scanning.config.disable", true)
        systemProperty("kotest.framework.config.fqn", "ca.solostudios.nyx.kotest.KotestConfig")
        systemProperty("kotest.framework.classpath.scanning.autoscan.disable", true)
    }

    allureReport {
        clean = true
    }

    check {
        dependsOn(withType<Test>(), functionalTest)
    }
}

val Project.isSnapshot: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")
