/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file RepositoryHandlerExtensions.kt is part of nyx
 * Last modified on 19-06-2024 05:27 p.m.
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

package ca.solostudios.nyx.util

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.maven

/**
 * Adds the fabric maven repository.
 */
public fun RepositoryHandler.fabric(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.fabricmc.net/") {
        name = "Fabric"
        action()
    }
}

/**
 * Adds the quilt maven repository.
 */
public fun RepositoryHandler.quilt(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.quiltmc.org/repository/release/") {
        name = "Quilt"
        action()
    }

}

/**
 * Adds the architectury maven repository.
 */
public fun RepositoryHandler.architectury(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.architectury.dev/") {
        name = "Architectury"
        action()
    }
}

/**
 * Adds the neoforged maven repository.
 */
public fun RepositoryHandler.neoforged(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.neoforged.net/releases/") {
        name = "Neoforged"
        action()
    }
}

/**
 * Adds the solo-studios maven repository.
 */
public fun RepositoryHandler.soloStudios(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.solo-studios.ca/releases/") {
        name = "Solo Studios"
        action()
    }
}

/**
 * Adds the solo-studios snapshots maven repository.
 */
public fun RepositoryHandler.soloStudiosSnapshots(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.solo-studios.ca/snapshots/") {
        name = "Solo Studios Snapshots"
        mavenContent {
            snapshotsOnly()
        }
        action()
    }
}

/**
 * Adds masa's maven repository.
 */
public fun RepositoryHandler.masaModding(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://masa.dy.fi/maven/") {
        name = "Masa Modding"
        action()
    }
}

/**
 * Adds Shedaniel's maven repository.
 */
public fun RepositoryHandler.shedaniel(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.shedaniel.me/") {
        name = "Shedaniel"
        action()
    }
}

/**
 * Adds BlameJared's maven repository.
 */
public fun RepositoryHandler.blamejared(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.blamejared.com/") {
        name = "BlameJared"
        action()
    }
}

/**
 * Adds the TerraformersMC maven repository.
 */
public fun RepositoryHandler.terraformersMC(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
        action()
    }
}

/**
 * Adds the CodeMC maven repository.
 */
public fun RepositoryHandler.codeMC(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://repo.codemc.org/repository/maven-public/") {
        name = "CodeMC"
        action()
    }
}

/**
 * Adds the wishforest maven repository.
 */
public fun RepositoryHandler.wispForest(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.wispforest.io/") {
        name = "Wisp Forest"
        action()
    }
}

/**
 * Adds JamiesWhiteShirt's maven repository.
 */
public fun RepositoryHandler.jamiesWhiteShirt(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.jamieswhiteshirt.com/libs-release/") {
        name = "JamiesWhiteShirt"
        action()
    }
}

/**
 * Adds the cursemaven maven repository.
 */
public fun RepositoryHandler.curseMaven(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://www.cursemaven.com/") {
        name = "Curseforge"
        action()
    }
}

/**
 * Adds the sonatype snapshots maven repository.
 */
public fun RepositoryHandler.sonatypeSnapshots(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "Sonatype Snapshots"
        mavenContent {
            snapshotsOnly()
        }
        action()
    }
}

/**
 * Adds the modrinth maven repository.
 */
public fun RepositoryHandler.modrinth(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://api.modrinth.com/maven/") {
        name = "Modrinth"
        action()
    }
}

/**
 * Adds the ladysnake maven repository.
 */
public fun RepositoryHandler.ladysnake(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.ladysnake.org/releases/") {
        name = "Ladysnake"
        action()
    }
}

/**
 * Adds the ParchmentMC maven repository.
 */
public fun RepositoryHandler.parchment(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.parchmentmc.org") {
        name = "ParchmentMC"
        action()
    }
}

/**
 * Adds the devOS maven repository.
 */
public fun RepositoryHandler.devOS(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://mvn.devos.one/releases/") {
        name = "devOS"
        action()
    }
}

/**
 * Adds the devOS snapshots maven repository.
 */
public fun RepositoryHandler.devOSSnapshots(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://mvn.devos.one/snapshots/") {
        name = "devOS Snapshots"
        action()
    }
}

/**
 * Adds the sleeping.town maven repository.
 */
public fun RepositoryHandler.sleepingTown(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://repo.sleeping.town/") {
        name = "Sleeping Town"
        action()
    }
}

/**
 * Adds the modmaven maven repository.
 */
public fun RepositoryHandler.modMaven(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://modmaven.dev/") {
        name = "ModMaven"
        action()
    }
}

/**
 * Adds tterrag's maven repository.
 */
public fun RepositoryHandler.tterrag(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven("https://maven.tterrag.com/") {
        name = "tterrag"
        action()
    }
}
