# Package ca.solostudios.nyx.util

## Utilities for Adding Common Repositories

All of these function can be invoked in the top-level `repositories` block to add a repository. eg.

```kotlin
repositories {
    soloStudios()
}
```

If there are any repositories you would like added, please [open an issue](https://github.com/solo-studios/nyx/issues/new).

Here is a list of the provided functions:

- [`fabric()`][ca.solostudios.nyx.util.fabric]: Adds the [Fabric MC maven repository](https://maven.fabricmc.net/).
- [`quilt()`][ca.solostudios.nyx.util.quilt]: Adds the [Quilt MC maven repository](https://maven.quiltmc.org/repository/release/).
- [`architectury()`][ca.solostudios.nyx.util.architectury]: Adds the [architectury maven repository](https://maven.architectury.dev/).
- [`neoforged()`][ca.solostudios.nyx.util.neoforged]: Adds the [NeoForged maven repository](https://maven.neoforged.net/releases/).
- [`soloStudios()`][ca.solostudios.nyx.util.soloStudios]: Adds the [solo-studios maven repository](https://maven.solo-studios.ca/releases/).
- [`soloStudiosSnapshots()`][ca.solostudios.nyx.util.soloStudiosSnapshots]: Adds
  the [solo-studios snapshots maven repository](https://maven.solo-studios.ca/snapshots/).
- [`masaModding()`][ca.solostudios.nyx.util.masaModding]: Adds [masa's maven repository](https://masa.dy.fi/maven/).
- [`shedaniel()`][ca.solostudios.nyx.util.shedaniel]: Adds [Shedaniel's maven repository](https://maven.shedaniel.me/).
- [`blamejared()`][ca.solostudios.nyx.util.blamejared]: Adds [BlameJared's maven repository](https://maven.blamejared.com/).
- [`terraformersMC()`][ca.solostudios.nyx.util.terraformersMC]: Adds
  the [TerraformersMC maven repository](https://maven.terraformersmc.com/releases/).
- [`codeMC()`][ca.solostudios.nyx.util.codeMC]: Adds the [CodeMC maven repository](https://repo.codemc.org/repository/maven-public/).
- [`wispForest()`][ca.solostudios.nyx.util.wispForest]: Adds the [Wisp Forest maven repository](https://maven.wispforest.io/).
- [`jamiesWhiteShirt()`][ca.solostudios.nyx.util.jamiesWhiteShirt]:
  Adds [JamiesWhiteShirt's maven repository](https://maven.jamieswhiteshirt.com/libs-release/).
- [`curseMaven()`][ca.solostudios.nyx.util.curseMaven]: Adds the [Curse Maven maven repository](https://www.cursemaven.com/).
- [`sonatypeSnapshots()`][ca.solostudios.nyx.util.sonatypeSnapshots]: Adds
  the [sonatype snapshots maven repository](https://oss.sonatype.org/content/repositories/snapshots/).
- [`modrinth()`][ca.solostudios.nyx.util.modrinth]: Adds the [Modrinth maven repository](https://api.modrinth.com/maven/).
- [`ladysnake()`][ca.solostudios.nyx.util.ladysnake]: Adds the [Ladysnake maven repository](https://maven.ladysnake.org/releases/).
- [`parchment()`][ca.solostudios.nyx.util.parchment]: Adds the [ParchmentMC maven repository](https://maven.parchmentmc.org).
- [`devOS()`][ca.solostudios.nyx.util.devOS]: Adds the [devOS maven repository](https://mvn.devos.one/releases/).
- [`devOSSnapshots()`][ca.solostudios.nyx.util.devOSSnapshots]: Adds
  the [devOS snapshots maven repository](https://mvn.devos.one/snapshots/).
- [`sleepingTown()`][ca.solostudios.nyx.util.sleepingTown]: Adds the [sleeping.town maven repository](https://repo.sleeping.town/).
- [`modMaven()`][ca.solostudios.nyx.util.modMaven]: Adds the [ModMaven maven repository](https://modmaven.dev/).
- [`tterrag()`][ca.solostudios.nyx.util.tterrag]: Adds [tterrag's maven repository](https://maven.tterrag.com/).

## Utilities for Publishing to Repositories

If you are publishing to a [reposilite](https://reposilite.com/) repository, it is required that you
use [BasicAuthentication][org.gradle.authentication.http.BasicAuthentication].
The [`reposiliteMaven()`][ca.solostudios.nyx.util.reposiliteMaven] is provided to make that more convenient:

```kotlin
nyx {
    publishing {
        repositories {
            // Configures publication to a reposilite maven repository
            reposiliteMaven("https://maven.solo-studios.ca/releases/") {
                name = "SoloStudios"
                credentials(PasswordCredentials::class)
            }

            // the above is equivalent to
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
