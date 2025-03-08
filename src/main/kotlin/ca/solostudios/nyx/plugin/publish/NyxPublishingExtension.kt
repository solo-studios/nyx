/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxPublishingExtension.kt is part of nyx
 * Last modified on 06-03-2025 08:42 p.m.
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

package ca.solostudios.nyx.plugin.publish

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.orEmpty
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.publishing
import ca.solostudios.nyx.internal.util.signing
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.project.NyxProjectInfoExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningPlugin

public class NyxPublishingExtension(
    override val project: Project,
    private val info: NyxProjectInfoExtension,
) : InternalNyxExtension {

    /**
     * If publishing should be enabled.
     */
    public val publish: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * If the `publish` task should depend on the `sign` task.
     */
    public val publishDependsOnSign: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * If in-memory pgp keys should be enabled.
     *
     * When in-memory pgp keys are used, you can set the following project
     * properties:
     * - `signingKey`
     * - `signingKeyId`
     * - `signingPassword`
     *
     * You can set these globally by adding
     *
     * ```properties
     * signingKey=[key]
     * signingKeyId=[id]
     * signingPassword=[key password]
     * ```
     *
     * in
     * [the `gradle.properties` located in your `$GRADLE_USER_HOME`](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables).
     * By default, this is located at `~/.gradle/gradle.properties` on
     * Linux/MacOS, and `%USERPROFILE%\.gradle\gradle.properties` on Windows.
     */
    public val inMemoryPgpKeys: Property<Boolean> = property<Boolean>().convention(true)

    /**
     * Enables publishing.
     *
     * @see publish
     */
    public fun withPublish() {
        publish = true
    }

    /**
     * Enables the `publish` task depending on the `sign` task.
     *
     * @see publishDependsOnSign
     */
    public fun withPublishDependsOnSign() {
        publishDependsOnSign = true
    }

    /**
     * Enables in-memory pgp keys.
     *
     * @see inMemoryPgpKeys
     */
    public fun withAllowInMemoryPgpKeys() {
        inMemoryPgpKeys = true
    }

    /**
     * - Makes publish tasks depend on the signing task
     * - Enables in-memory pgp keys
     * - Enables publishing
     */
    public fun withSignedPublishing() {
        publish = true
        publishDependsOnSign = true
        inMemoryPgpKeys = true
    }

    /**
     * Configures the repositories used for publishing.
     */
    public fun repositories(action: RepositoryHandler.() -> Unit) {
        publish = true
        project.apply<MavenPublishPlugin>()

        publishing {
            repositories(action)
        }
    }

    override fun configureProject() {
        if (!publish.isTrue)
            return

        project.apply<MavenPublishPlugin>()
        project.apply<SigningPlugin>()

        if (inMemoryPgpKeys.isTrue) {
            signing {
                // Allow specifying the key, key id, and password via environment variables.
                val signingKey: String? by project
                val signingKeyId: String? by project
                val signingPassword: String? by project

                when {
                    signingKey != null && signingKeyId != null && signingPassword != null -> {
                        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
                    }

                    signingKey != null && signingPassword != null -> {
                        useInMemoryPgpKeys(signingKey, signingPassword)
                    }

                    else                                          -> useGpgCmd()
                }
                sign(publishing.publications)
            }
        }

        publishing {
            if (publishDependsOnSign.isTrue) {
                tasks.withType<AbstractPublishToMaven>().configureEach {
                    dependsOn(tasks.withType<Sign>())
                }
            }

            publications {

                when {
                    project.plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") -> {
                        // when using kotlin multiplatform, publications will be created for you
                        withType<MavenPublication>().configureEach {
                            configurePublication(applyArtifactId = false)
                        }
                    }

                    project.plugins.hasPlugin("org.gradle.java-gradle-plugin") -> {
                        withType<MavenPublication>().configureEach {
                            configurePublication(applyArtifactId = false, applyGroupId = false)
                        }
                    }

                    else                                                       -> {
                        if (withType<MavenPublication>().isEmpty()) {
                            create<MavenPublication>(info.module.get()) {
                                from(project.components["java"])

                                configurePublication()
                            }
                        }
                    }
                }
            }
        }
    }

    internal fun MavenPublication.configurePublication(applyArtifactId: Boolean = true, applyGroupId: Boolean = true) {
        if (applyGroupId)
            groupId = info.group

        // Only apply artifact if needed (ie. when not multiplatform)
        if (applyArtifactId)
            artifactId = info.module.orEmpty()

        version = info.version

        pom {
            name = info.name

            description = info.description
            if (info.repository.projectUrl.isPresent)
                url = info.repository.projectUrl

            // inceptionYear = info.inceptionYear
            if (info.organizationName.isPresent || info.organizationUrl.isPresent) {
                organization {
                    if (info.organizationName.isPresent)
                        name = info.organizationName
                    if (info.organizationUrl.isPresent)
                        url = info.organizationUrl
                }
            }

            developers {
                // Add all configured developers
                for (developerCallback in info.developers.get()) {
                    developer(developerCallback)
                }
            }

            // we assume there is only ever a single license
            if (info.license.name.isPresent || info.license.url.isPresent) {
                licenses {
                    license {
                        if (info.license.name.isPresent)
                            name = info.license.name
                        if (info.license.url.isPresent)
                            url = info.license.url
                    }
                }
            }

            issueManagement {
                if (info.repository.issueManagement.isPresent)
                    system = info.repository.issueManagement
                if (info.repository.projectIssues.isPresent)
                    url = info.repository.projectIssues
            }

            scm {
                if (info.repository.projectCloneScmUri.isPresent)
                    connection = info.repository.projectCloneScmUri
                if (info.repository.projectCloneDeveloperUri.isPresent)
                    developerConnection = info.repository.projectCloneDeveloperUri
                if (info.repository.projectRepoUrl.isPresent)
                    url = info.repository.projectRepoUrl
            }
        }
    }

    public companion object {
        /**
         * The name this extension is added with.
         */
        public const val NAME: String = "publishing"
    }
}
