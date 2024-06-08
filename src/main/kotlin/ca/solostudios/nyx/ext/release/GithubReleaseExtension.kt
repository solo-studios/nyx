package ca.solostudios.nyx.ext.release

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.ext.project.ProjectInfoExtension
import ca.solostudios.nyx.util.fileCollection
import ca.solostudios.nyx.util.githubRelease
import ca.solostudios.nyx.util.nyx
import ca.solostudios.nyx.util.property
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

public class GithubReleaseExtension(override val project: Project) : ConfiguresProject, HasProject {
    private val projectInfo: ProjectInfoExtension
        get() = nyx.project

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

    override fun onLoad() {}

    override fun configureProject() {
        val tokenProperty = project.providers.gradleProperty(GITHUB_TOKEN_GRADLE_PROPERTY)
        if (tokenProperty.isPresent)
            githubRelease.token(tokenProperty)
        else
            githubRelease.token(System.getenv("MODRINTH_TOKEN"))

        val version = projectInfo.version

        githubRelease {
            owner(projectInfo.repository.projectOwner)
            repo(projectInfo.repository.projectRepo)
            tagName("v$version")
            targetCommitish(null as String?)
            releaseName("v$version")
        }

        if (!releaseAssets.isEmpty)
            githubRelease.releaseAssets(releaseAssets)

        if (generateReleaseNotes.isPresent)
            githubRelease.generateReleaseNotes(generateReleaseNotes)
    }

    public companion object {
        public const val GITHUB_TOKEN_GRADLE_PROPERTY: String = "github.token"
    }
}
