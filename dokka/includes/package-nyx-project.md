# Package ca.solostudios.nyx.project

```kotlin
nyx {
    info {
        // This is the full name of your project.
        // Defaults to `project.name` with the following changes:
        // - each `-` is replaced with a space
        // - the first character of each word capitalized
        // - the word "kt" is replaced with "Kotlin"
        // eg. my-project -> My Project
        name = "My Project"

        // The group of your project. Used for publishing.
        // Defaults to `project.group`
        group = "com.example"

        // The module name. Used for publishing.
        // Defaults to `project.name`
        module = "my-project"

        // The version of the project.
        // Defaults to `project.version`.
        version = "1.2.3"

        // The description of your project. Used for publishing.
        // Defaults to `project.description`.
        description = """
            My project's description
        """.trimIndent()

        // The url of the associated organization. Used for publishing.
        organizationUrl = "https://github.com/solo-studios"

        // The name of the organization associated with this project. Used for publishing.
        organizationName = "Solo Studios"

        // Add a developer to the project (can be invoked multiple times). Used for publishing.
        developer {
            id = "solonovamax"
            name = "solonovamax"
            email = "solonovamax@12oclockpoint.com"
            url = "https://solonovamax.gay"
        }

        // Configure repository information. Used for publishing.
        repository {
            // Set a github repository
            // This sets the url to https://github.com/solo-studios/my-project
            fromGithub(owner = "solo-studios", repo = "my-project")
            // equivalent to
            // fromGitHostWithIssues(owner, repo, "github.com", "Github")

            // Set a repository from gitlab (gitlab.com)
            // This sets the url to https://gitlab.com/solo-studios/my-project
            fromGitlab(owner = "solo-studios", repo = "my-project")
            // equivalent to
            // fromGitHostWithIssues(owner, repo, "gitlab.com", "Gitlab")

            // Set a repository from codeberg (codeberg.org)
            // This sets the url to https://codeberg.org/solo-studios/my-project
            fromCodeberg(owner = "solo-studios", repo = "my-project")
            // equivalent to
            // fromGitHostWithIssues(owner, repo, "codeberg.org", "Codeberg")

            // Set a repository from a git host that has a /issues page
            // This sets the url to https://forgejo.solo-studios.ca/solo-studios/my-project
            fromGitHostWithIssues(
                owner = "solo-studios",
                repo = "my-project",
                host = "https://forgejo.solo-studios.ca",
                name = "Solo Studios Forgejo"
            )

            // Set a repository from a git host that does *not* have a /issues page
            // It is recommended to also set the issue management system and the issues url if you use this
            // This sets the url to https://git.solo-studios.ca/solo-studios/my-project
            fromGitHost(owner = "solo-studios", repo = "my-project", host = "https://git.solo-studios.ca")

            // Set issue management system name
            issueManagement = "Jira"

            // Set issues url
            projectIssues = "https://jira.solo-studios.ca"
        }
        // You can also set the repository by doing
        repository.fromGithub("solo-studios", "my-project")

        // Configures license information. Used for publishing.
        license {
            // Set the name of the license
            // If possible, this should be the SPDX identifier of the license.
            name = "Apache-2.0"

            // Set the url of the license
            url = "https://apache.org/licenses/LICENSE-2.0"

            // It is recommended to instead use these convenience methods instead of setting the name/url directly

            // Uses MIT license
            useMIT()

            // Uses Apache 2.0 license
            useApachev2()

            // Uses BSD-3-Clause license
            useBSD3Clause()

            // Uses GPL 3.0
            useGPLv3()

            // Uses LGPL 3.0
            useLGPLv3()

            // Uses AGPL 3.0
            useAGPLv3()

            // Uses MPL 2.0
            useMPLv2()

            // For a list of all available licenses please see NyxLicenseInfoExtension.kt
        }
    }
}
```
