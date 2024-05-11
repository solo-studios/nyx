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

    public val draft: Property<Boolean> = property()

    public val prerelease: Property<Boolean> = property()

    public val overwrite: Property<Boolean> = property()

    public val allowUploadToExisting: Property<Boolean> = property()

    public val dryRun: Property<Boolean> = property()

    public val releaseAssets: ConfigurableFileCollection = fileCollection()

    override fun onLoad() {}

    override fun configureProject() {
        val version = projectInfo.version.get()

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
}
