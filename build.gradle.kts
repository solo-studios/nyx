/*
 * Copyright (c) 2023-2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file build.gradle.kts is part of nyx
 * Last modified on 11-06-2024 06:34 p.m.
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

plugins {
    `kotlin-dsl`

    `maven-publish`

    `java-gradle-plugin`

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)

    alias(libs.plugins.gradle.plugin.development)

    alias(libs.plugins.dokka)
    alias(libs.plugins.axion.release)

    alias(libs.plugins.nyx)

    alias(libs.plugins.gradle.publish)
}

nyx {
    project {
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
            explicitApi()
            allOpen.annotations("ca.solostudios.nyx.internal.AllOpen")
        }
    }

    publishing {
        configurePublications()

        repositories {
            maven {
                name = "Sonatype"

                val repositoryId: String? by project
                url = when {
                    isSnapshot           -> uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    repositoryId != null -> uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/")
                    else                 -> uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }

                credentials(PasswordCredentials::class)
            }
            maven {
                name = "SoloStudios"

                val releasesUrl = uri("https://maven.solo-studios.ca/releases/")
                val snapshotUrl = uri("https://maven.solo-studios.ca/snapshots/")
                url = if (isSnapshot) snapshotUrl else releasesUrl

                credentials(PasswordCredentials::class)
                authentication { // publishing doesn't work without this for some reason
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}

repositories {
    maven("https://maven.solo-studios.ca/releases/")
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // compileOnly(gradleApi())
    api(libs.kotlin.stdlib)

    compileOnly(gradleApi("8.6"))

    api(libs.apache.commons)

    implementation(libs.slf4k)

    implementation(libs.bundles.maven)

    implementation(libs.fuel)

    // Kotlin stuff
    compileOnly(libs.kotlin.plugin)
    compileOnly(libs.dokka)

    // Minecraft plugins
    compileOnly(libs.fabric.loom)
    compileOnly(libs.quilt.loom)
    compileOnly(libs.architectury)
    compileOnly(libs.architectury.loom)

    compileOnly(libs.github.release)
    compileOnly(libs.modrinth.minotaur)

    compileOnly(libs.neogradle.userdev)
    compileOnly(libs.neogradle.mixin)
}

gradlePlugin {
    website = nyx.project.repository.projectUrl
    vcsUrl = nyx.project.repository.projectUrl

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


val Project.isSnapshot: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")
