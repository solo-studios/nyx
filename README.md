# Nyx Gradle Plugin

## Using

Add the following to your `build.gradle`/`build.gradle.kts`

```kotlin
plugins {
    id("ca.solo-studios.nyx") version "[nyx version]"
}
```

If you want to use a snapshot version, add the solo-studios snapshots repo to your `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        maven("https://maven.solo-studios.ca/snapshots/")
    }
}
```

## Features

The enabled features depends on which other plugins are loaded.

### Default

By default, Nyx adds a `nyx` block with an `info` extension.

The `info` extension is used to configure metadata about the project:

```kotlin
nyx {
    info {
        // This is the full name of your project.
        // Defaults to `project.name`, with `-` replaced with spaces, and the first character of each word capitalized
        name = "My Project"

        // The group of your project. Used for publishing, among other things.
        // Defaults to project.group
        group = "com.example"

        // The module name. Used for publishing/etc.
        // Defaults to `project.name`
        module = "my-project"

        // The version of the project.
        // Defaults to project.version.
        version = "1.2.3"

        // The description of your project. Used for publishing.
        // Defaults to project.description, if set.
        description = """
            My project
        """.trimIndent()

        // The url of the associated organization. Used for publishing.
        organizationUrl = "https://github.com/solo-studios"

        // The name of the organization associated with this project. Used for publishing.
        organizationName = "Solo Studios"

        // Add a developer to the project. Used for publishing.
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
            name = "My Custom License"

            // Set the url of the license
            url = "https://license.solo-studios.ca"

            // It is recommended to instead use these convenience methods instead of setting the name/url directly

            // Uses MIT license
            useMIT()

            // Uses Apache 1.1 license
            useApachev11()

            // Uses Apache 2.0 license
            useApachev2()

            // Uses ISC license
            useISC()

            // Uses BSD-0 license (has no url)
            use0BSD()

            // Uses BSD-1-Clause license
            useBSD1Clause()

            // Uses BSD-2-Clause license
            useBSD2Clause()

            // Uses BSD-3-Clause license
            useBSD3Clause()

            // Uses GPL 2.0
            useGPLv2()

            // Uses GPL 3.0
            useGPLv3()

            // Uses LGPL 2.0
            useLGPLv2()

            // Uses LGPL 2.1
            useLGPLv21()

            // Uses LGPL 3.0
            useLGPLv3()

            // Uses AGPL 3.0
            useAGPLv3()

            // Uses MPL 1.0
            useMPLv1()

            // Uses MPL 1.1
            useMPLv11()

            // Uses MPL 2.0
            useMPLv2()

            // Uses EPL 1.0
            useEPLv1()

            // Uses EPL 2.0
            useEPLv2()

            // Uses Unlicense
            useUnlicense()

            // Uses CC0
            useCC0()

            // Uses WTFPL
            useWTFPL()
        }
    }
}
```

### JVM plugins

When any jvm plugins are in use, a `compile` extension is added to `nyx`:

```kotlin
nyx {
    compile {
        // Enables/disabled the 'all warnings' feature for the java or kotlin compiler
        // Defaults to false
        allWarnings = true

        // Enables/disables the 'warnings as errors' feature for the java/kotlin compiler
        // Defaults to false
        warningsAsErrors = true

        // Include the LICENSE file in any output jar files
        // Defaults to true
        distributeLicense = true

        // The encoding used for compilation
        // Defaults to UTF-8
        encoding = "UTF-8"

        // Enables zip64 support for any output jars
        // Defaults to false
        zip64 = true

        // If the `build` task should depend on all tasks of type `Jar`
        // Defaults to true
        buildDependsOnJar = true

        // If all compiler warnings should be suppressed
        // Defaults to false
        suppressWarnings = true

        // The jvm toolchain version to use
        // Defaults to unset
        jvmToolchain = 17

        // The jvm version to target. This is the bytecode version of the resulting class files
        // Defaults to unset
        jvmTarget = 8

        // If the sources jar should be enabled
        // Defaults to false
        withSourcesJar = true
        withSourcesJar() // alternative

        // If the javadoc jar should be enabled
        // If the kotlin plugin is loaded, then the dokka plugin most alos be loaded to use this.
        // Defaults to false
        withJavadocJar = true
        withJavadocJar() // alternative

        // If reproducible builds should be enabled
        // This strips timestamps from all files in the resulting jar
        // Defaults to false
        reproducibleBuilds = true
    }
}
```

### Java (`java` plugin)

### Kotlin (`org.jetbrains.kotlin.*`/`kotlin("jvm")` plugin)

### Maven Publish (`maven-publish` plugin)

### Loom (`fabric-loom`, `org.quiltmc.loom`, and `dev.architectury.loom` plugins)

### NeoGradle (`net.neoforged.gradle.userdev`, `net.neoforged.gradle.mixin`, and other `net.neoforged.gradle.*` plugins)

### Minotaur (`com.modrinth.minotaur` plugin)

### GitHub Release Plugin (`com.github.breadmoirai.github-release` plugin)
