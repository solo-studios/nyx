/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file DokkaUtil.kt is part of nyx
 * Last modified on 10-06-2024 03:21 p.m.
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

package ca.solostudios.nyx.internal.util

import ca.solostudios.nyx.internal.HasProject
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask

internal fun HasProject.addDokkaJavadocJarTask() {
    tasks {
        val dokkaHtml by named<DokkaTask>("dokkaHtml")
        // configure the javadoc jar task without creating it (either get an existing task or create a new task)
        val hasJavadocJarTask = findByName("javadocJar") != null
        val javadocJar by if (hasJavadocJarTask) named<Jar>("javadocJar") {
            configureJavadocJar(dokkaHtml)
        } else register<Jar>("javadocJar") {
            configureJavadocJar(dokkaHtml)
        }

        artifacts {
            add("archives", javadocJar)
        }

        if (project.plugins.hasPlugin("publishing") && !hasJavadocJarTask) {
            // Add javadoc jar to maven publications (is this the best way to do it?)
            publishing {
                publications.withType<MavenPublication>().configureEach {
                    artifact(javadocJar)
                }
            }
        }
    }

}

private fun Jar.configureJavadocJar(dokkaHtml: DokkaTask) {
    dependsOn(dokkaHtml)
    from(dokkaHtml.outputDirectory)
    archiveClassifier = "javadoc"
    description = "Assembles a jar archive containing the main dokka kdoc"
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}
