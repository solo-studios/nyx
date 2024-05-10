/*
 * Copyright (c) 2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file build.gradle.kts is part of gradle-conventions-plugin
 * Last modified on 13-11-2023 07:13 p.m.
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

    // `java-gradle-plugin`

    // alias(libs.plugins.gradle.plugin.java)
    alias(libs.plugins.gradle.plugin.development)

    alias(libs.plugins.dokka)
    alias(libs.plugins.axion.release)

    `maven-publish`
}

group = "ca.solo-studios"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

repositories {
    maven("https://maven.solo-studios.ca/releases/")
    maven("https://maven.fabricmc.net/")
    // maven("https://maven.quiltmc.org/repository/release")
    maven("https://maven.architectury.dev/")
    maven("https://maven.minecraftforge.net/")
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    // compileOnly(gradleApi("8.6"))

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
}

kotlin {
    jvmToolchain(8)

    explicitApi()
}

gradlePlugin {
    plugins {
        create("nyx") {
            id = "ca.solo-studios.nyx"
            implementationClass = "ca.solostudios.nyx.NyxPlugin"
        }

        create("sonatype-publish") {
            id = "ca.solo-studios.sonatype-publish"
            implementationClass = "ca.solostudios.nyx.sonatype.SonatypePublishPlugin"
        }
    }
}
