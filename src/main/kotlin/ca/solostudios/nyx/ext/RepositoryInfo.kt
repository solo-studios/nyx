package ca.solostudios.nyx.ext

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

public open class RepositoryInfo(project: Project) {
    public val projectHost: Property<String> = project.objects.property()

    public val projectPath: Property<String> = project.objects.property()

    /**
     * The project's base uri.
     *
     * By default, this is `$host/$path`.
     *
     * **Only change this if you know what you're doing**
     */
    public val projectBaseUri: Property<String> = project.objects.property<String>()
        .convention(projectHost.flatMap { host -> projectPath.map { path -> "$host/$path" } })

    /**
     * The base uri for cloning.
     *
     * By default, this is `$baseUri.git`.
     *
     * **Only change this if you know what you're doing**
     */
    public val projectCloneBaseUri: Property<String> = project.objects.property<String>()
        .convention(projectBaseUri.map { "$it.git" })

    /**
     * The base project uri.
     *
     * By default, this is `https://$baseUri`.
     *
     * **Only change this if you know what you're doing**
     */
    public val projectUrl: Property<String> = project.objects.property<String>()
        .convention(projectBaseUri.map { baseUri -> "https://$baseUri" })

    public val projectIssues: Property<String> = project.objects.property<String>()
        .convention(projectUrl.map { url -> "$url/issues" })

    public fun fromGithub(user: String, repo: String): Unit = fromGitHost(user, repo, "github.com")

    public fun fromGitlab(user: String, repo: String): Unit = fromGitHost(user, repo, "gitlab.com")

    public fun fromCodeberg(user: String, repo: String): Unit = fromGitHost(user, repo, "codeberg.org")

    public fun fromGitHost(user: String, repo: String, host: String) {
        projectHost.set(host)
        projectPath.set("$user/$repo")
    }
}
