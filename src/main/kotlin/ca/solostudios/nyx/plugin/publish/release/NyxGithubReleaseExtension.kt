/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxGithubReleaseExtension.kt is part of nyx
 * Last modified on 10-06-2024 03:24 p.m.
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

package ca.solostudios.nyx.plugin.publish.release

import ca.solostudios.nyx.internal.ConfiguresProject
import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.util.fileCollection
import ca.solostudios.nyx.internal.util.githubRelease
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.project.NyxProjectInfoExtension
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign

public class NyxGithubReleaseExtension(
    override val project: Project,
    private val info: NyxProjectInfoExtension,
) : ConfiguresProject, HasProject {
    /**
     * Whether to automatically generate the body for this release.
     * If `body` is specified, the body will be pre-pended to the automatically generated notes.
     *
     * See: https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release
     */
    public val generateReleaseNotes: Property<Boolean> = property()

    /**
     * Text describing the contents of the tag.
     *
     * See: https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release
     */
    public val body: Property<String> = property()

    /**
     * `true` to create a draft (unpublished) release, `false` to create a published one.
     *
     * See: https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release
     */
    public val draft: Property<Boolean> = property()

    /**
     * `true` to identify the release as a prerelease.
     * `false` to identify the release as a full release.
     *
     * See: https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#create-a-release
     */
    public val prerelease: Property<Boolean> = property()

    /**
     * If these artifacts can be used to overwrite an existing release (delete, then re-create).
     */
    public val overwrite: Property<Boolean> = property()

    /**
     * If these artifacts can be uploaded to an existing release.
     */
    public val allowUploadToExisting: Property<Boolean> = property()

    /**
     * If this is a dry run.
     *
     * A dry run will not upload anything.
     */
    public val dryRun: Property<Boolean> = property()

    /**
     * A file collection of all the assets published in the release.
     */
    public val releaseAssets: ConfigurableFileCollection = fileCollection()

    override fun configureProject() {
        val tokenProperty = project.providers.gradleProperty(GITHUB_TOKEN_GRADLE_PROPERTY)
        if (tokenProperty.isPresent)
            githubRelease.token(tokenProperty)
        else
            githubRelease.token(System.getenv("GITHUB_TOKEN"))

        val version = info.version

        githubRelease {
            owner = info.repository.projectOwner
            repo = info.repository.projectRepo
            tagName = "v$version"
            targetCommitish = null
            releaseName = "v$version"
        }

        if (!releaseAssets.isEmpty)
            githubRelease.releaseAssets(releaseAssets)

        if (generateReleaseNotes.isPresent)
            githubRelease.generateReleaseNotes = generateReleaseNotes
    }

    public companion object {
        public const val GITHUB_TOKEN_GRADLE_PROPERTY: String = "github.token"
        public const val NAME: String = "github"
    }
}
