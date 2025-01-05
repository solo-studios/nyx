/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxRepositoryInfo.kt is part of nyx
 * Last modified on 25-12-2024 07:39 p.m.
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

package ca.solostudios.nyx.project

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign

public class NyxRepositoryInfo(override val project: Project) : InternalNyxExtension {
    /**
     * The domain name of the website hosting the git repository.
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectHost: Property<String> = property()

    /**
     * The owner of the repository, if applicable.
     *
     * eg. The github organization, or github user.
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectOwner: Property<String> = property()

    /**
     * The name of the repository, if applicable.
     *
     * eg. The name of the github repository.
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectRepo: Property<String> = property()

    /**
     * The path of the project on the git host.
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectPath: Property<String> = property()

    /**
     * The type of issue management system used.
     *
     * For example,
     * ```kotlin
     * issueManagement = "Jira"
     * ```
     *
     * It is recommended to also configure the [issues url][projectIssues]
     * property when configuring this.
     *
     * @see projectIssues
     */
    public val issueManagement: Property<String> = property()

    /**
     * The project's base uri. Used to set information for maven publishing.
     *
     * By default, this is `$host/$path`.
     *
     * **Only change this if you know what you're doing**
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectBaseUri: Property<String> = property<String>()
        .convention(projectHost.flatMap { host -> projectPath.map { path -> "$host/$path" } })

    /**
     * The base uri for cloning. Used to set information for maven publishing.
     *
     * By default, this is `$baseUri.git`.
     *
     * **Only change this if you know what you're doing**
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectCloneBaseUri: Property<String> = property<String>().convention(projectBaseUri.map { "$it.git" })

    /**
     * The uri for cloning from version control, with read-access. Used to set
     * information for maven publishing.
     *
     * By default, this is `scm:git:https://$cloneBaseUri`.
     * Format: `scm:[provider]:[provider_specific]`. See
     * [Maven POM Reference](https://maven.apache.org/pom.html#scm)
     *
     * **Only change this if you know what you're doing**
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectCloneScmUri: Property<String> = property<String>().convention(projectCloneBaseUri.map { "scm:git:https://$it" })

    /**
     * The uri for cloning from version control, with read-access. Used to set
     * information for maven publishing.
     *
     * By default, this is `scm:git:ssh://$cloneBaseUri`.
     * Format: `scm:[provider]:[provider_specific]`. See
     * [Maven POM Reference](https://maven.apache.org/pom.html#scm)
     *
     * **Only change this if you know what you're doing**
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectCloneDeveloperUri: Property<String> = property<String>().convention(projectCloneBaseUri.map { "scm:git:ssh://$it" })

    /**
     * The url for the homepage of the project. Used to set information for
     * maven publishing.
     *
     * By default, this is `https://$baseUri`.
     *
     * Prefer using the `from*` methods over this.
     *
     * @see fromGithub
     * @see fromGitlab
     * @see fromCodeberg
     * @see fromGitHostWithIssues
     * @see fromGitHost
     */
    public val projectUrl: Property<String> = property<String>().convention(projectBaseUri.map { baseUri -> "https://$baseUri" })

    /**
     * The url for the issue management system.
     *
     * For example,
     * ```kotlin
     * projectIssues = "https://jira.solo-studios.ca"
     * ```
     *
     * It is recommended to also configure the
     * [issue management system][issueManagement]
     * property when configuring this.
     *
     * @see issueManagement
     */
    public val projectIssues: Property<String> = property<String>()

    /**
     * Sets the repository url to a github repository.
     *
     * For example
     *
     * ```kotlin
     * fromGithub("solo-studios", "my-project")
     * ```
     *
     * This configures nyx to use the repository located at
     * `https://github.com/solo-studios/my-project`.
     *
     * This is equivalent to
     *
     * ```kotlin
     * fromGitHostWithIssues("solo-studios", "my-project", "github.com", "Github")
     * ```
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @see fromGitHostWithIssues
     */
    public fun fromGithub(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "github.com", "Github")

    /**
     * Sets the repository url to a gitlab repository (gitlab.com).
     *
     * For example
     *
     * ```kotlin
     * fromGitlab("solo-studios", "my-project")
     * ```
     *
     * This configures nyx to use the repository located at
     * `https://gitlab.com/solo-studios/my-project`.
     *
     * This is equivalent to
     *
     * ```kotlin
     * fromGitHostWithIssues("solo-studios", "my-project", "gitlab.com", "Gitlab")
     * ```
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @see fromGitHostWithIssues
     */
    public fun fromGitlab(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "gitlab.com", "Gitlab")

    /**
     * Sets the repository url to a codeberg repository (codeberg.org).
     *
     * For example
     *
     * ```kotlin
     * fromCodeberg("solo-studios", "my-project")
     * ```
     *
     * This configures nyx to use the repository located at
     * `https://gitlab.com/solo-studios/my-project`.
     *
     * This is equivalent to
     *
     * ```kotlin
     * fromGitHostWithIssues("solo-studios", "my-project", "codeberg.org", "Codeberg")
     * ```
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @see fromGitHostWithIssues
     */
    public fun fromCodeberg(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "codeberg.org", "Codeberg")

    /**
     * Sets the repository url to a generic git host that has a /issues page
     * under the same url.
     *
     * Configures the repository info to use a generic git host that supports
     * issue management.
     *
     * For example,
     * ```kotlin
     * fromGitHostWithIssues(
     *     owner = "solo-studios",
     *     repo = "my-project",
     *     host = "https://forgejo.solo-studios.ca",
     *     name = "Solo Studios Forgejo"
     * )
     * ```
     *
     * This configures nyx to use the repository located at
     * `https://forgejo.solo-studios.ca/solo-studios/my-project`
     * named "Solo Studios Forgejo".
     *
     * This is equivalent to
     *
     * ```kotlin
     * fromGitHost(
     *     owner = "solo-studios",
     *     repo = "my-project",
     *     host = "https://forgejo.solo-studios.ca",
     * )
     * issueManagement = "Solo Studios Forgejo"
     * projectIssues = "https://forgejo.solo-studios.ca/solo-studios/my-project/issues"
     * ```
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @param host The host/domain of the git repository (eg. `github.com`,
     *         `gitlab.com`, `gitlab.my-domain.com`, etc.)
     * @param name The name of the git host (eg. "GitHub", "Gitlab",
     *         "Codeberg", "My Company Forgejo", etc.)
     */
    public fun fromGitHostWithIssues(owner: String, repo: String, host: String, name: String) {
        fromGitHost(owner, repo, host)
        issueManagement = name
        // most major git hosts use /issues
        projectIssues = projectUrl.map { url -> "$url/issues" }
    }

    /**
     * Sets the repository url to a generic git host that *does not* have a
     * `/issues` page.
     *
     * It is recommended to also set the
     * [issue management system][issueManagement] and
     * the [issues url][projectIssues] if you use this.
     *
     * For example,
     * ```kotlin
     * fromGitHost(
     *     owner = "solo-studios",
     *     repo = "my-project",
     *     host = "https://git.solo-studios.ca"
     * )
     * ```
     *
     * This configures nyx to use the repository located at
     * `https://git.solo-studios.ca/solo-studios/my-project`.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @param host The host/domain of the git repository (eg. `github.com`,
     *         `gitlab.com`, `gitlab.my-domain.com`, etc.)
     * @see issueManagement
     */
    public fun fromGitHost(owner: String, repo: String, host: String) {
        projectHost = host
        projectPath = "$owner/$repo"
        projectOwner = owner
        projectRepo = repo
    }

    override fun configureProject() {}
}
