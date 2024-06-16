/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file RepositoryInfo.kt is part of nyx
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

package ca.solostudios.nyx.project

import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign

public open class NyxRepositoryInfo(override val project: Project) : HasProject {
    /**
     * The domain name of the website hosting the git repository.
     */
    public val projectHost: Property<String> = property()

    /**
     * The owner of the repository, if applicable.
     *
     * eg. The github organization, or github user.
     */
    public val projectOwner: Property<String> = property()

    /**
     * The name of the repository, if applicable.
     *
     * eg. The name of the github repository.
     */
    public val projectRepo: Property<String> = property()

    /**
     * The path of the project on the git host.
     *
     * For example: ``[user][projectOwner]`/`[repo][projectRepo]
     */
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

    /**
     * The url for the issue management system.
     */
    public val projectIssues: Property<String> = property<String>()

    /**
     * Configures the repository info to use a github url.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     */
    public fun fromGithub(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "github.com", "Github")

    /**
     * Configures the repository info to use a gitlab url.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     */
    public fun fromGitlab(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "gitlab.com", "Gitlab")

    /**
     * Configures the repository info to use a codeberg url.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     */
    public fun fromCodeberg(owner: String, repo: String): Unit = fromGitHostWithIssues(owner, repo, "codeberg.org", "Codeberg")

    /**
     * Configures the repository info to use a generic git host that supports issue management.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @param host The host/domain of the git repository (eg. `github.com`, `gitlab.com`, `gitlab.my-domain.com`, etc.)
     * @param name The name of the git host (eg. "GitHub", "Gitlab", "Codeberg", etc.)
     */
    public fun fromGitHostWithIssues(owner: String, repo: String, host: String, name: String) {
        fromGitHost(owner, repo, host)
        issueManagement = name
        // most major git hosts use /issues
        projectIssues = projectUrl.map { url -> "$url/issues" }
    }

    /**
     * Configures the repository info to use a generic git host that *does not* support issue management.
     *
     * @param owner The user/organization the repository exists under
     * @param repo The name of the repository
     * @param host The host/domain of the git repository (eg. `github.com`, `gitlab.com`, `gitlab.my-domain.com`, etc.)
     */
    public fun fromGitHost(owner: String, repo: String, host: String) {
        projectHost = host
        projectPath = "$owner/$repo"
        projectOwner = owner
        projectRepo = repo
    }
}
