# Module Nyx

Nyx is a gradle plugin aimed at simplifying configuring your gradle projects, with support for

- Java
- Kotlin
- [loom](https://github.com/FabricMC/fabric-loom)-based toolchains
- & more

## Including

{{dependencies}}

## Features

The enabled features depend on which other plugins are loaded.

### Default

By default, nyx adds a top-level `nyx` block with an `info` block.
The `info` extension is used to configure metadata about the project.

For example,

```kotlin
nyx {
    info {
        name = "Nyx"
        group = "ca.solo-studios"
        module = "nyx"
        version = scmVersion.version
        description = """
            Nyx is a gradle plugin intended to simplify gradle buildscripts using common conventions.
        """.trimIndent()

        organizationUrl = "https://solo-studios.ca/"
        organizationName = "Solo Studios"

        developer {
            id = "solonovamax"
            name = "solonovamax"
            email = "solonovamax@12oclockpoint.com"
            url = "https://solonovamax.gay"
        }

        repository.fromGithub("solo-studios", "nyx")
        license.useMIT()
    }
}
```

See [ca.solostudios.nyx.project] for more info on configuring this.

#### Miscellaneous Utilities

Some additional miscellaneous utilities are offered, specifically for adding/configuring repositories.

See [ca.solostudios.nyx.util] for a list of provided functions.


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

            // If all javadoc warnings are enabled.
            // Defaults to false
            allJavadocWarnings = true
            withAllJavadocWarnings() // Alternative that sets it to true

            // If missing element javadoc warnings are enabled.
            // Defaults to false
            noMissingJavadocWarnings = true
            withNoMissingJavadocWarnings() // Alternative that sets it to true

            // If emitting javadoc warnings as errors is enabled
            // Defaults to false
            javadocWarningsAsErrors = true
            withJavadocWarningsAsErrors() // Alternative that sets it to true

            // If all javadoc warnings should be suppressed.
            // Defaults to false
            suppressJavadocWarnings = true
            withSuppressJavadocWarnings() // Alternative that sets it to true

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

