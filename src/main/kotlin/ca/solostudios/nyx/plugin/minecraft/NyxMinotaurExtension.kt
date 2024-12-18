/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxMinotaurExtension.kt is part of nyx
 * Last modified on 18-12-2024 06:57 p.m.
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

package ca.solostudios.nyx.plugin.minecraft

import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.fileCollection
import ca.solostudios.nyx.internal.util.fileProperty
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.modrinth
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.project.NyxProjectInfoExtension
import com.modrinth.minotaur.TaskModrinthUpload
import com.modrinth.minotaur.dependencies.container.NamedDependencyContainer
import masecla.modrinth4j.model.version.ProjectVersion
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

public class NyxMinotaurExtension(
    override val project: Project,
    private val info: NyxProjectInfoExtension,
) : InternalNyxExtension {
    /**
     * The modrinth project id.
     */
    public val projectId: Property<String> = property()

    /**
     * The changelog for this release.
     */
    public val changelog: Property<String> = property()

    /**
     * The updated body for the mod's page.
     */
    public val syncBody: Property<String> = property()

    /**
     * The project version type.
     */
    public val versionType: Property<VersionType> = property()

    /**
     * The file to upload.
     */
    public val file: RegularFileProperty = fileProperty()

    /**
     * Any additional files to upload.
     */
    public val additionalFiles: ConfigurableFileCollection = fileCollection()

    /**
     * All supported game versions.
     */
    public val gameVersions: ListProperty<String> = listProperty()

    /**
     * All supported loaders.
     */
    public val loaders: ListProperty<String> = listProperty()

    /**
     * If failures should be silent.
     */
    public val failSilently: Property<Boolean> = property()

    /**
     * If minotaur should attempt to detect which loaders a mod supports.
     */
    public val detectLoaders: Property<Boolean> = property()

    /**
     * If minotaur should automatically adding the `dependsOn` information for upload files.
     */
    public val autoAddDependsOn: Property<Boolean> = property()

    /**
     * Configures any additional files.
     *
     * @see additionalFiles
     */
    public fun additionalFiles(action: ConfigurableFileCollection.() -> Unit) {
        additionalFiles.apply(action)
    }

    /**
     * Enables silent failures.
     *
     * @see failSilently
     */
    public fun withFailSilently() {
        failSilently = true
    }

    /**
     * Enables detecting supported loaders.
     *
     * @see detectLoaders
     */
    public fun withDetectLoaders() {
        detectLoaders = true
    }

    /**
     * Enables automatically adding the `dependsOn` information for upload files.
     *
     * @see autoAddDependsOn
     */
    public fun withAutoAddDependsOn() {
        autoAddDependsOn = true
    }

    /**
     * Configures the dependencies for the modrinth release.
     */
    public fun dependencies(dsl: DependenciesDsl.() -> Unit) {
        DependenciesDsl(project).apply(dsl)
    }

    override fun configureProject() {
        val tokenProperty = project.providers.gradleProperty(MODRINTH_TOKEN_GRADLE_PROPERTY)
        if (tokenProperty.isPresent)
            modrinth.token = tokenProperty
        else
            modrinth.token = System.getenv("MODRINTH_TOKEN")

        if (projectId.isPresent)
            modrinth.projectId = projectId

        modrinth.versionNumber.convention(info.version)

        if (versionType.isPresent)
            modrinth.versionType = versionType.map { it.channelName }

        if (changelog.isPresent)
            modrinth.changelog = changelog

        if (syncBody.isPresent)
            modrinth.syncBodyFrom = syncBody

        if (gameVersions.isPresent)
            modrinth.gameVersions = gameVersions

        if (loaders.isPresent)
            modrinth.loaders = loaders

        if (failSilently.isPresent)
            modrinth.failSilently = failSilently

        if (detectLoaders.isPresent)
            modrinth.detectLoaders = detectLoaders

        if (autoAddDependsOn.isPresent)
            modrinth.autoAddDependsOn = autoAddDependsOn

        // currently only support loom
        // TODO: support neoforge
        when {
            file.isPresent -> {
                modrinth.file = file
            }

            tasks.findByName("remapJar") != null -> {
                val remapJar by tasks.named<Jar>("remapJar")

                modrinth.file = remapJar.archiveFile
                tasks.named<TaskModrinthUpload>("modrinth").configure {
                    dependsOn(remapJar)
                }
            }

            tasks.findByName("jar") != null -> {
                val jar by tasks.named<Jar>("jar")

                modrinth.file = jar.archiveFile
                tasks.named<TaskModrinthUpload>("modrinth").configure {
                    dependsOn(jar)
                }
            }
        }

        if (!additionalFiles.isEmpty) {
            modrinth.additionalFiles.addAll(additionalFiles.asFileTree.files)
        }

        project.plugins.withType<PublishingPlugin> {
            val modrinth by tasks.named("modrinth")
            val publish by tasks.named("publish")

            publish.dependsOn(modrinth)
        }
    }

    public class DependenciesDsl(override val project: Project) : HasProject {
        public fun incompatible(projectId: String, versionId: String? = null) {
            dependency(modrinth.incompatible, projectId, versionId)
        }

        public fun optional(projectId: String, versionId: String? = null) {
            dependency(modrinth.optional, projectId, versionId)
        }

        public fun required(projectId: String, versionId: String? = null) {
            dependency(modrinth.required, projectId, versionId)
        }

        public fun embedded(projectId: String, versionId: String? = null) {
            dependency(modrinth.embedded, projectId, versionId)
        }

        private fun dependency(dependencyContainer: NamedDependencyContainer, projectId: String, versionId: String?) {
            if (versionId == null)
                dependencyContainer.project(projectId)
            else
                dependencyContainer.version(projectId, versionId)
        }
    }

    public companion object {
        public const val MODRINTH_TOKEN_GRADLE_PROPERTY: String = "modrinth.token"
        public const val NAME: String = "minotaur"
    }

    /**
     * The name of the version channel.
     *
     * @see ProjectVersion.VersionType
     */
    public enum class VersionType(
        public val channelName: String,
    ) {
        /**
         * If this version was released on the "release" channel
         */
        RELEASE("release"),

        /**
         *  If this version was released on the "beta" channel
         */
        BETA("beta"),

        /**
         * If this version was released on the "alpha" channel
         */
        ALPHA("alpha")
    }
}
