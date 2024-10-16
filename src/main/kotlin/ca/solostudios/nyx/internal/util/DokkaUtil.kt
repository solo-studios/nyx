/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file DokkaUtil.kt is part of nyx
 * Last modified on 15-10-2024 09:17 p.m.
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
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.artifacts.type.ArtifactTypeDefinition
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.DocsType
import org.gradle.api.attributes.Usage
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask

private val UNPUBLISHABLE_VARIANT_ARTIFACTS = listOf(
    ArtifactTypeDefinition.JVM_CLASS_DIRECTORY,
    ArtifactTypeDefinition.JVM_RESOURCES_DIRECTORY,
    ArtifactTypeDefinition.DIRECTORY_TYPE,
)

internal fun HasProject.addDokkaJavadocJarTask() {
    tasks {
        val dokkaHtml by when {
            "dokkaGenerateHtml" in tasks -> named<DokkaGenerateTask>("dokkaGenerateHtml")
            "dokkaHtml" in tasks -> named<DokkaTask>("dokkaHtml")
            else -> error("Could not find dokka task. Both 'dokkaGenerateHtml' and 'dokkaHtml' are null.")
        }

        val javadocJar by maybeRegistering(Jar::class) {
            when (val dokkaHtmlTask = dokkaHtml) {
                is DokkaGenerateTask -> configureJavadocJar(dokkaHtmlTask)
                is DokkaTask -> configureJavadocJarLegacyDokka(dokkaHtmlTask)
            }
        }

        val javadocArtifact = project.artifacts.add("archives", javadocJar)

        // magic shit
        if (sourceSets.names.contains("main")) {
            sourceSets.named("main") {
                val javadocConfiguration by configurations.maybeRegister(javadocElementsConfigurationName) {
                    configureConfiguration(project, javadocArtifact)
                }

                project.components.withType<AdhocComponentWithVariants> {
                    addVariantsFromConfiguration(javadocConfiguration) {
                        mapToMavenScope("runtime")
                        mapToOptional()

                        if (configurationVariant.artifacts.any { it.type in UNPUBLISHABLE_VARIANT_ARTIFACTS })
                            skip()
                    }
                }
            }
        }

        if (names.contains("assemble")) {
            named("assemble") {
                dependsOn(javadocJar)
            }
        }
    }
}

private fun Configuration.configureConfiguration(project: Project, javadocArtifact: PublishArtifact) {
    val docsType = "dokka javadoc"
    isVisible = false
    description = "${DocsType.JAVADOC} elements for" +
            if (name == SourceSet.MAIN_SOURCE_SET_NAME) "main $docsType." else "$docsType of the '$name' feature."
    attributes.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named<Usage>(Usage.JAVA_RUNTIME))
    attributes.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named<Category>(Category.DOCUMENTATION))
    attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named<Bundling>(Bundling.EXTERNAL))
    attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE, project.objects.named<DocsType>(DocsType.JAVADOC))

    outgoing {
        artifact(javadocArtifact)
    }
}

private fun Jar.configureJavadocJar(dokkaHtml: DokkaGenerateTask) {
    dependsOn(dokkaHtml)
    from(dokkaHtml.outputDirectory)
    archiveClassifier = "javadoc"
    description = "Assembles a jar archive containing the main dokka javadocs."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}

private fun Jar.configureJavadocJarLegacyDokka(dokkaHtml: DokkaTask) {
    dependsOn(dokkaHtml)
    from(dokkaHtml.outputDirectory)
    archiveClassifier = "javadoc"
    description = "Assembles a jar archive containing the main dokka javadocs."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
}
