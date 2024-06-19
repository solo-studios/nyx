# Nyx Gradle Plugin

- [Nyx Gradle Plugin](#nyx-gradle-plugin)
    - [Using](#using)
    - [Features](#features)
        - [Default](#default)
            - [Misc. Utilities](#misc-utilities)
        - [JVM plugins](#jvm-plugins)
        - [Java (`java` plugin)](#java-java-plugin)
        - [Kotlin (`org.jetbrains.kotlin.*`/`kotlin("jvm")` plugin)](#kotlin-orgjetbrainskotlinkotlinjvm-plugin)
        - [Publishing Plugins](#publishing-plugins)
            - [Maven Publish (`maven-publish` plugin)](#maven-publish-maven-publish-plugin)
            - [GitHub Release Plugin (`com.github.breadmoirai.github-release` plugin)](#github-release-plugin-comgithubbreadmoiraigithub-release-plugin)
        - [Minecraft Plugins](#minecraft-plugins)
            - [Loom (`fabric-loom`, `org.quiltmc.loom`, and `dev.architectury.loom` plugins)](#loom-fabric-loom-orgquiltmcloom-and-devarchitecturyloom-plugins)
            - [NeoGradle (`net.neoforged.gradle.userdev`, `net.neoforged.gradle.mixin`, and other `net.neoforged.gradle.*` plugins)](#neogradle-netneoforgedgradleuserdev-netneoforgedgradlemixin-and-other-netneoforgedgradle-plugins)
            - [Mixins (any loom-based plugin or with `net.neoforged.gradle.mixin`)](#mixins-any-loom-based-plugin-or-with-netneoforgedgradlemixin)
            - [Minotaur (`com.modrinth.minotaur` plugin)](#minotaur-commodrinthminotaur-plugin)

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

nyx adds a `nyx` block with an `info` extension.
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
            My project's description
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

#### Misc. Utilities

Some additional miscellaneous utilities are offered, specifically for adding/configuring repositories.

They must be imported from the `ca.solostudios.nyx.util` package.

##### Utilities for common repositories

nyx contains several utility functions for adding common repositories:

```kotlin
import ca.solostudios.nyx.util.*

repositories {
    fabric() // Adds the fabric maven repository
    quilt() // Adds the quilt maven repository
    architectury() // Adds the architectury maven repository
    neoforged() // Adds the neoforged maven repository
    soloStudios() // Adds the solo-studios maven repository
    soloStudiosSnapshots() // Adds the solo-studios snapshots maven repository
    masaModding() // Adds masa's maven repository
    shedaniel() // Adds Shedaniel's maven repository
    blamejared() // Adds BlameJared's maven repository
    terraformersMC() // Adds the TerraformersMC maven repository
    codeMC() // Adds the CodeMC maven repository
    wispForest() // Adds the wishforest maven repository
    jamiesWhiteShirt() // Adds JamiesWhiteShirt's maven repository
    curseMaven() // Adds the cursemaven maven repository
    sonatypeSnapshots() // Adds the sonatype snapshots maven repository
    modrinth() // Adds the modrinth maven repository
    ladysnake() // Adds the ladysnake maven repository
    parchment() // Adds the ParchmentMC maven repository
    devOS() // Adds the devOS maven repository
    devOSSnapshots() // Adds the devOS snapshots maven repository
    sleepingTown() // Adds the sleeping.town maven repository
    modMaven() // Adds the modmaven maven repository
    tterrag() // Adds tterrag's maven repository
}
```

##### Utilities for publishing to repositories

nyx contains several utility functions for publishing to repositories:

```kotlin
import ca.solostudios.nyx.util.*

nyx {
    publishing {
        repositories {
            // Configures publication to a reposilite maven repository.
            reposiliteMaven("https://maven.solo-studios.ca/releases/") {
                name = "SoloStudios"
                credentials(PasswordCredentials::class)
            }
            // this is equivalent to the following:
            maven("https://maven.solo-studios.ca/releases/") {
                name = "SoloStudios"
                credentials(PasswordCredentials::class)

                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}
```

Use the `reposiliteMaven` if you are publishing to a reposilite repository, as it is slightly more convenient.


### JVM plugins

When any jvm plugin is present, a `compile` extension will be added to `nyx`:

```kotlin
nyx {
    compile {
        // Enables/disabled the 'all warnings' feature for the java or kotlin compiler
        // Defaults to false
        allWarnings = true
        withAllWarnings() // Alternative that sets it to true

        // Enables/disables the 'warnings as errors' feature for the java/kotlin compiler
        // Defaults to false
        warningsAsErrors = true
        withWarningsAsErrors() // Alternative that sets it to true

        // Include the LICENSE file in any output jar files
        // Defaults to true
        distributeLicense = true
        withDistributeLicense() // Alternative that sets it to true

        // The encoding used for compilation
        // Defaults to UTF-8
        encoding = "UTF-8"

        // Enables zip64 support for any output jars
        // Defaults to false
        zip64 = true
        withZip64() // Alternative that sets it to true

        // If the `build` task should depend on all tasks of type `Jar`
        // Defaults to true
        buildDependsOnJar = true
        withBuildDependsOnJar() // Alternative that sets it to true

        // If all java/kotlin compiler warnings should be suppressed
        // Defaults to false
        suppressWarnings = true
        withSuppressWarnings() // Alternative that sets it to true

        // The jvm toolchain version to use
        // Note: this uses gradle toolchains: https://docs.gradle.org/current/userguide/toolchains.html
        // This will force gradle to use a specific jdk version, installing it if not found
        jvmToolchain = 17

        // The jvm version to target. This is the bytecode version of the resulting class files
        jvmTarget = 8

        // If the sources jar should be enabled
        // Defaults to false
        withSourcesJar = true
        withSourcesJar() // alternative that sets it to true

        // If the javadoc jar should be enabled
        // If the kotlin plugin is loaded, then requires the dokka plugin to be loaded.
        // Defaults to false
        withJavadocJar = true
        withJavadocJar() // alternative that sets it to true

        // If reproducible builds should be enabled
        // This strips timestamps from all files in the resulting jar
        // Defaults to false
        reproducibleBuilds = true
        withReproducibleBuilds() // Alternative that sets it to true
    }
}
```

### Java (`java` plugin)

When the `java` plugin is present, a `java` extension will be added to the `compile` extension:

```kotlin
nyx {
    compile {
        java {
            // Enables/disabled the 'all warnings' feature for the java compiler
            // Inherited from compile.allWarnings
            allWarnings = true
            withAllWarnings() // Alternative that sets it to true

            // Enables/disables the 'warnings as errors' feature for the java compiler
            // Inherited from compile.warningsAsErrors
            warningsAsErrors = true
            withWarningsAsErrors() // Alternative that sets it to true

            // The encoding used for compilation
            // Inherited from compile.encoding
            encoding = "UTF-8"

            // If all java compiler warnings should be suppressed
            // Inherited from compile.suppressWarnings
            suppressWarnings = true
            withSuppressWarnings() // Alternative that sets it to true

            // The jvm toolchain version to use
            // Note: this uses gradle toolchains: https://docs.gradle.org/current/userguide/toolchains.html
            // This will force gradle to use a specific jdk version, installing it if not found
            // Inherited from compile.jvmToolchain
            jvmToolchain = 17

            // The jvm version to target. This is the bytecode version of the resulting class files
            // This will set the following properties
            // - java.targetCompatibility
            // - java.sourceCompatibility
            // - options.release for all tasks of type JavaCompile
            // Inherited from compile.jvmTarget
            jvmTarget = 8

            // If the sources jar should be enabled
            // Inherited from compile.withSourcesJar
            withSourcesJar = true
            withSourcesJar() // alternative that sets it to true

            // If the javadoc jar should be enabled
            // Inherited from compile.withSourcesJar
            withJavadocJar = true
            withJavadocJar() // alternative that sets it to true

            // A list of arguments to add to the compiler args passed to javac
            // Defaults to an empty list
            compilerArgs = listOf(
                "-g", // generate all debug info
                "-verbose"
            )
        }
    }
}
```

### Kotlin (`org.jetbrains.kotlin.*`/`kotlin("jvm")` plugin)

When a `org.jetbrains.kotlin.*` plugin is present (such as when using `kotlin("jvm")`),
a `kotlin` extension will be added to the `compile` extension:

```kotlin
nyx {
    compile {
        kotlin {
            // The kotlin api version to use
            // Allow using declarations only from the specified version of bundled libraries
            apiVersion = "2.0"

            // The kotlin language version to use
            // Provide source compatibility with the specified version of Kotlin
            languageVersion = "2.0"

            // The list of opt-in annotations
            // Enable usages of API that requires opt-in with an opt-in requirement marker with the given fully qualified name
            // Defaults to an empty list
            optIn = listOf("kotlinx.serialization.ExperimentalSerializationApi")

            // The explicit api mode
            // Option that tells the compiler if and how to report issues on all public API declarations without explicit visibility or return type.
            // Defaults to ExplicitApiMode.Disabled
            explicitApi = ExplicitApiMode.Strict
            withExplicitApi() // Alternative that sets the explicit api mode to strict
            withExplicitApiWarning() // Alternative that sets the explicit api mode to warning

            // Enables/disables the 'warnings as errors' feature for the kotlin compiler
            // Inherited from compile.warningsAsErrors
            warningsAsErrors = true
            withWarningsAsErrors() // Alternative that sets it to true

            // If all kotlin compiler warnings should be suppressed
            // Inherited from compile.suppressWarnings
            suppressWarnings = true
            withSuppressWarnings() // Alternative that sets it to true

            // The jvm toolchain version to use
            // Note: this uses gradle toolchains: https://docs.gradle.org/current/userguide/toolchains.html
            // This will force gradle to use a specific jdk version, installing it if not found
            // Inherited from compile.jvmToolchain
            jvmToolchain = 17

            // The jvm version to target. This is the bytecode version of the resulting class files
            // This will set the following properties
            // Inherited from compile.jvmTarget
            jvmTarget = 8

            // If the sources jar should be enabled
            // Inherited from compile.withSourcesJar
            withSourcesJar = true
            withSourcesJar() // alternative that sets it to true

            // If the javadoc jar should be enabled
            // Requires the dokka plugin to be loaded
            // Inherited from compile.withSourcesJar
            withJavadocJar = true
            withJavadocJar() // alternative that sets it to true

            // A list of arguments to add to the compiler args passed to kotlinc
            // Defaults to an empty list
            compilerArgs = listOf("-Xcontext-receivers")
        }
    }
}
```

### Publishing Plugins

nyx has a `publishing` extension which allows you to configure how the project is published.

#### Maven Publish (`maven-publish` plugin)

setting `publishing.publish` to true will enable the `maven-publish` plugin,
which allows you to configure how the project is published to a maven repository.

If `publish` has been set to true, then a publishing configuration will be automatically added and configured with the following
information:

- `info.group` as the group id
- `info.module` as the module id
- `info.version` as the version
- `info.name` as the name
- `info.description` as the description
- `info.repository.projectUrl` as the url
- `info.organizationName`/`info.organizationUrl` as the organization name/url (if present)
- `info.developers` as the developers
- `info.license.name`/`info.license.url` as the license name/url if present
- `info.repository.issueManagement` as the issue management system
- `info.repository.projectIssues` as the issue management url
- `info.repository.projectCloneScmUri` as the scm connection
- `info.repository.projectCloneDeveloperUri` as the scm developer connection
- `info.repository.projectUrl` as the scm url

```kotlin
nyx {
    publishing {
        // Enables/disables publishing
        // Enabling this will apply the `maven-publish` plugin and the `signing` plugins
        // Defaults to false
        publish = true
        withPublish() // Alternative that sets it to true

        // Enables/disables the `publish` task depending on the `sign` task
        // Defaults to true
        publishDependsOnSign = true
        withPublishDependsOnSign() // Alternative that sets it to true

        // Allows in-memory PGP keys to be used. This is useful for CI.
        // Using the gradle properties
        // - signingKey: the ascii-armored pgp key
        // - signingKeyId: the id of the subkey pgp to use
        // - signingPassword: the password of the ascii-armoed pgp key
        //
        // You can either provide these in multiple ways:
        // - on the command line using -PsigningKey="..." -PsigningKeyId="..." -PsigningPassword="..." (not recommended)
        // - by adding them to your `gradle.properties` in either $GRADLE_USER_HOME or the project directory
        // - by adding them as environment variables using the syntax `ORG_GRADLE_PROJECT_${variable name}`
        inMemoryPgpKeys = true
        withAllowInMemoryPgpKeys() // Alternative that sets it to true

        // This will set publish, publishDependsOnSign, and allowInMemoryPgpKeys all to true
        withSignedPublishing()

        // This is where you configure the repositories you're publishing to.
        // It is configured exactly the same as if you were using the normal publishing block
        repositories {
            maven {
                name = "SoloStudios"
                url = uri("https://maven.solo-studios.ca/releases/")

                // Since PasswordCredentials has been used, gradle get the credentials from
                // - the SoloStudiosUsername property
                // - the SoloStudiosPassword property
                //
                // You can set these in multiple ways:
                // - on the command line using -PSoloStudiosUsername="..." -PSoloStudiosPassword="..." (not recommended)
                // - by adding them to your `gradle.properties` in either $GRADLE_USER_HOME or the project directory
                // - by adding them as environment variables using the syntax `ORG_GRADLE_PROJECT_${variable name}`
                //
                // for your personal development machine, I recommend placing them in the `gradle.properties` in your $GRADLE_USER_HOME directory
                // this is located at `~/.gradle/gradle.properties` on linux/macos and `%USERPROFILE%\.gradle\gradle.properties` on windows
                // for a CI server, I recommend providing them using environment variables
                credentials(PasswordCredentials::class)
            }
        }
    }
}
```

#### GitHub Release Plugin (`com.github.breadmoirai.github-release` plugin)

If the `com.github.breadmoirai.github-release` plugin is present, then a `github` extension will be added to the `publishing` extension.

The github release extension will attempt to load the token used for publishing from one of two places:

- the `github.token` gradle property.
  This can be specified in several different ways
    - on the command line using `-Pgithub.token="..."` (not recommended)
    - by adding `github.token` to your `gradle.properties` in either $GRADLE_USER_HOME or the project directory
      (located at `~/.gradle/gradle.properties` on linux/macos and `%USERPROFILE%\.gradle\gradle.properties` on windows)
    - by adding a `ORG_GRADLE_PROJECT_github.token` environment variable (recommended for CI environments)
- the `GITHUB_TOKEN` environment variable

```kotlin
nyx {
    publishing {
        github {
            // Enables/disables the generation of release notes
            generateReleaseNotes = true
            withGenerateReleaseNotes() // Alternative that sets it to true

            // The text describing the tag for the release
            body = file("CHANGELOG").readText()

            // Enables/disables the draft setting for a release
            draft = true
            withDraft() // Alternative that sets it to true

            // Enables/disables the prerelease setting for a release
            prerelease = true
            withPrerelease() // Alternative that sets it to true

            // Enables/disables overwriting an existing release
            overwrite = true
            withOverwrite() // Alternative that sets it to true

            // Enables/disables uploading artifacts to an existing release
            uploadToExisting = true
            withUploadToExisting() // Alternative that sets it to true

            // Enables the dry-run setting, which will not publish the release to github
            dryRun = true
            withDryRun() // Alternative that sets it to true

            // A file collection of all the release assets
            // using the from method to use the outputs of the jar task as the inputs for this
            releaseAssets.from(tasks.jar)
            // you can also use the releaseAssets function instead
            releaseAssets {
                from(tasks["javadocJar"])
            }
        }
    }
}
```

### Minecraft Plugins

If one of several different minecraft gradle plugins is present, then a `minecraft` extension will be added to `nyx`

```kotlin
nyx {
    minecraft {
        // The memory to be allocated to the jvm, in gigabytes
        // Defaults to 2
        allocatedMemory = 4

        // Any additional jvm arguments to be passed to the jvm
        // Defaults to ["-XX:+UseZGC"]
        additionalJvmArgs.add("-XX:+ZGenerational") // -XX:+ZGenerational only works with jdk >= 21

        // Any additional jvm properties to be added to the jvm
        // Equivalent to adding -D[property]=[value]
        additionalJvmProperties.put("fabric.development", "true")
    }
}
```

#### Loom (`fabric-loom`, `org.quiltmc.loom`, and `dev.architectury.loom` plugins)

If any loom-based plugin is present (currently supported: `fabric-loom`, `org.quiltmc.loom`, and `dev.architectury.loom`),
then the `minecraft` extension is extended as follows:

```kotlin
nyx {
    minecraft {
        // Enables/disables interface injection
        // See loom docs for more information
        interfaceInjection = true
        withInterfaceInjection() // Alternative that sets it to true

        // Enables/disables transitive access wideners
        // See loom docs for more information
        transitiveAccessWideners = true
        withTransitiveAccessWideners() // Alternative that sets it to true

        // Enables/disables mod-provided javadoc
        // See loom docs for more information
        modProvidedJavadoc = true
        withModProvidedJavadoc() // Alternative that sets it to true

        // Enables/disables runtime-only log4j
        // See loom docs for more information
        runtimeOnlyLog4j = true
        withRuntimeOnlyLog4j() // Alternative that sets it to true

        // Enables/disables split mod dependencies
        splitModDependencies = true
        withSplitModDependencies() // Alternative that sets it to true

        // Enables/disables split environment sourcesets
        // See loom docs for more information
        splitEnvironmentalSourceSet = true
        withSplitEnvironmentalSourceSet() // Alternative that sets it to true

        // Enables/disables generation of only the minecraft server jar
        // See loom docs for more information
        serverOnlyMinecraftJar = true
        withServerOnlyMinecraftJar() // Alternative that sets it to true

        // Enables/disables generation of only the minecraft client jar
        // See loom docs for more information
        clientOnlyMinecraftJar = true
        withClientOnlyMinecraftJar() // Alternative that sets it to true

        // Enables data generation with default configuration
        withDataGeneration()
        // Configures data generation
        configureDataGeneration {
            // See the loom docs for more information
        }

        // Configures the decompilers
        decompilers {
            // See the loom docs for more information
        }

        // Configures the loom runs
        runs {
            // See the loom docs for more information
        }

        // Configures the defined mods
        mods {
            // See the loom docs for more information
        }

        // Adds an access widener at `src/main/resources/${name}.accesswidener`
        // If the access widener does not exist, it will be created and a warning will be emitted
        accessWidener("my-project") // this adds an access widener at src/main/resources/my-project.accesswidener
    }
}
```

#### NeoGradle (`net.neoforged.gradle.userdev`, `net.neoforged.gradle.mixin`, and other `net.neoforged.gradle.*` plugins)

If a neogradle plugin is present, then the `minecraft` exntesion is extended as follows:

```kotlin
nyx {
    minecraft {
        // Sets the mod identifier
        modIdentifier = "my-mod"

        // Configures the naming channels
        namingChannels {
            // See the neogradle docs for more information
        }

        // Configures the mappings
        mappings {
            // See the neogradle docs for more information
        }

        // Configures the access transformers
        accessTransformers {
            // See the neogradle docs for more information
        }
    }
}
```

#### Mixins (any loom-based plugin or with `net.neoforged.gradle.mixin`)

If mixins are enabled, then a `mixin` extension will be added to the `minecraft` extension.

Mixins are only enabled when either a loom-based plugin is present, or when `net.neoforged.gradle.mixin` is present.

```kotlin
nyx {
    minecraft {
        mixin {
            // Enables/disables hotswapping mixins
            // Note: this will also find the first net.fabricmc:sponge-mixin artifact on the runtime classpath and add it as a java agent
            // If you have just added this plugin and had previously generated run configurations, delete them so they can be re-created
            // Sets the mixin.hotSwap jvm property
            // Defaults to true
            hotswap = true
            withHotswap() // Alternative that sets it to true

            // Enables/disables *ALL* mixin debug features. this includes mixin.debug.verify
            // Note: enabling this can sometimes cause issues with other mods' mixins
            // Sets the mixin.debug jvm property
            // Defaults to false
            debug = true
            withDebug() // Alternative that sets it to true

            // Enables/disables mixin verbose logging
            // Sets the mixin.debug.verbose jvm property
            // Defaults to true
            verbose = true
            withVerbose() // Alternative that sets it to true

            // Enables/disables dumping the target class on failures
            // Sets the mixin.dumpTargetOnFailure jvm property
            // Defaults to true
            dumpTargetOnFailure = true
            withDumpTargetOnFailure() // Alternative that sets it to true

            // Enables/disables mixin checks
            // Note: enabling this can sometimes cause issues with other mods' mixins.
            // Sets the mixin.checks jvm property
            // Defaults to false
            checks = true
            withChecks() // Alternative that sets it to true

            // Enables/disables mixin debug verification
            // Note: enabling this can sometimes cause issues with other mods' mixins
            // Sets the mixin.debug.verify jvm property
            // Defaults to false
            verify = true
            withVerify() // Alternative that sets it to true

            // Enables/disables exporting the mixins after they have been applied
            // This will export the classes in the `.mixin.out` dir under the run directory
            // This sets the mixin.debug.export jvm property
            // Defaults to true
            export = true
            withExports() // Alternative that sets it to true

            // Selects the mixin config file to use
            // This is relative to `src/main/resources/`
            // NeoGradle ONLY
            mixinConfig("my-project.mixins.json")

            // Selects the default mixin refmap name to use
            // This will set the default mixin refmap name to `mixins/${name}/refmap.json`.
            // Loom-based plugins ONLY
            mixinRefmapName("my-project")
        }
    }
}
```

#### Minotaur (`com.modrinth.minotaur` plugin)

If the `com.modrinth.minotaur` plugin is present, then a `minotaur` extension will be added to the `minecraft` extension.
This is used for publishing to modrinth.

The modrinth extension will attempt to load the token used for publishing from one of two places:

- the `modrinth.token` gradle property.
  This can be specified in several different ways
    - on the command line using `-Pmodrinth.token="..."` (not recommended)
    - by adding `modrinth.token` to your `gradle.properties` in either $GRADLE_USER_HOME or the project directory
      (located at `~/.gradle/gradle.properties` on linux/macos and `%USERPROFILE%\.gradle\gradle.properties` on windows)
    - by adding a `ORG_GRADLE_PROJECT_modrinth.token` environment variable (recommended for CI environments)
- the `MODRINTH_TOKEN` environment variable

```kotlin
nyx {
    minecraft {
        minotaur {
            // The project id/slug
            projectId = "my-project"

            // The changelog for this release
            changelog = file("CHANGELOG").readText()

            // The updated body of the mod page
            syncBody = file("README.md").readText()

            // The version type for this release
            versionType = VersionType.BETA

            // The file to upload.
            // This is automatically set to output from remapJar if a loom-based plugin is present
            file = tasks.named<Jar>("remapJar").archiveFile

            // Any additional files to be uploaded
            additionalFiles {
                from(tasks.named("jar"))
            }

            // The list of game versions this release supports
            gameVersions = listOf("1.19.2", "1.19.3", "1.19.4")

            // The list of loaders
            // By default this is detected automatically
            loaders = listOf("fabric", "quilt")

            // If the modrinth task should fail silently
            failSilently = false
            withFailSilently() // Alternative that sets it to true

            // Enables/disables the detection of loaders
            detectLoaders = true
            withDetectLoaders() // Alternative that sets it to true

            // Enables/disables automatically adding the `dependsOn` information for upload files
            autoAddDependsOn = true
            withAutoAddDependsOn() // Alternative that sets it to true

            // Configures the dependencies
            dependencies {
                // Mark a project as incompatible
                incompatible("project-id")

                // Mark a specific version as incompatible
                incompatible("project-id", "1.2.3")

                // Mark a project as optional
                optional("project-id")

                // Mark a specific version as optional
                optional("project-id", "1.2.3")

                // Mark a project as required
                required("project-id")

                // Mark a specific version as required
                required("project-id", "1.2.3")

                // Mark a project as embedded
                embedded("project-id")

                // Mark a specific version as embedded
                embedded("project-id", "1.2.3")
            }
        }
    }
}
```

