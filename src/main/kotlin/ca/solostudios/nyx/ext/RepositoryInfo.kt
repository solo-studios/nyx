package ca.solostudios.nyx.ext

import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property

public open class RepositoryInfo(override val project: Project) : HasProject {
    public val projectHost: Property<String> = property()

    public val projectPath: Property<String> = property()

    /**
     * The type of issue management system used
     */
    public val issueManagement: Property<String> = property()

    /**
     * The project's base uri.
     * Used to set information for maven publishing.
     *
     * By default, this is `$host/$path`.
     *
     * **Only change this if you know what you're doing**
     */
    public val projectBaseUri: Property<String> = property<String>()
        .convention(projectHost.flatMap { host -> projectPath.map { path -> "$host/$path" } })

    /**
     * The base uri for cloning.
     * Used to set information for maven publishing.
     *
     * By default, this is `$baseUri.git`.
     *
     * **Only change this if you know what you're doing**
     */
    public val projectCloneBaseUri: Property<String> = property<String>().convention(projectBaseUri.map { "$it.git" })

    /**
     * The uri for cloning from version control, with read-access.
     * Used to set information for maven publishing.
     *
     * By default, this is `scm:git:https://$cloneBaseUri`.
     * Format: `scm:[provider]:[provider_specific]`. See [Maven POM Reference](https://maven.apache.org/pom.html#scm)
     *
     * **Only change this if you know what you're doing**
     */
    public val projectCloneScmUri: Property<String> = property<String>().convention(projectCloneBaseUri.map { "scm:git:https://$it" })

    /**
     * The uri for cloning from version control, with read-access.
     * Used to set information for maven publishing.
     *
     * By default, this is `scm:git:ssh://$cloneBaseUri`.
     * Format: `scm:[provider]:[provider_specific]`. See [Maven POM Reference](https://maven.apache.org/pom.html#scm)
     *
     * **Only change this if you know what you're doing**
     */
    public val projectCloneDeveloperUri: Property<String> = property<String>().convention(projectCloneBaseUri.map { "scm:git:ssh://$it" })

    /**
     * The url for the homepage of the project.
     * Used to set information for maven publishing.
     *
     * By default, this is `https://$baseUri`.
     */
    public val projectUrl: Property<String> = property<String>().convention(projectBaseUri.map { baseUri -> "https://$baseUri" })

    public val projectIssues: Property<String> = property<String>()

    public fun fromGithub(user: String, repo: String): Unit = fromGitHostWithIssues(user, repo, "github.com", "Github")

    public fun fromGitlab(user: String, repo: String): Unit = fromGitHostWithIssues(user, repo, "gitlab.com", "Gitlab")

    public fun fromCodeberg(user: String, repo: String): Unit = fromGitHostWithIssues(user, repo, "codeberg.org", "Codeberg")

    public fun fromGitHostWithIssues(user: String, repo: String, host: String, name: String) {
        fromGitHost(user, repo, host)
        issueManagement.set(name)
        // most major git hosts use /issues
        projectIssues.set(projectUrl.map { url -> "$url/issues" })
    }

    public fun fromGitHost(user: String, repo: String, host: String) {
        projectHost.set(host)
        projectPath.set("$user/$repo")
    }
}
