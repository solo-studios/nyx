/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file MinecraftLoomExtension.kt is part of nyx
 * Last modified on 10-06-2024 03:24 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.loom

import ca.solostudios.nyx.internal.util.fabricApi
import ca.solostudios.nyx.internal.util.githubRelease
import ca.solostudios.nyx.internal.util.loom
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.internal.util.publishing
import ca.solostudios.nyx.internal.util.sourceSets
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.plugin.minecraft.AbstractMinecraftExtension
import net.fabricmc.loom.configuration.FabricApiExtension
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText

public class MinecraftLoomExtension(
    override val project: Project,
) : AbstractMinecraftExtension(project) {
    private val logger by getLogger()

    public fun configureDataGeneration() {
        fabricApi {
            configureDataGeneration()
        }
    }

    public fun configureDataGeneration(block: FabricApiExtension.DataGenerationSettings.() -> Unit) {
        fabricApi {
            configureDataGeneration(block)
        }
    }

    /**
     * Adds an access widener at `src/main/resources/`[name]`.accesswidener`.
     *
     * The file will be created if it does not exist (this is to avoid an error with fabric loom)
     *
     * @param name The name of the access widener, defaulting to `project.name`.
     */
    public fun accessWidener(name: String = project.name) {
        loom {
            // src/main/resources/$name.accesswidener
            val accessWidenerPaths = sourceSets["main"].resources.srcDirs.map { it.resolve("$name.accesswidener").toPath() }
            if (accessWidenerPaths.none { it.exists() }) {
                // try creating access widener file
                val firstAccessWidener = accessWidenerPaths.first()

                logger.warn {
                    """
                        Can't find an access widener in any resource directory named '$name.accesswidener'.
                        Created one at $firstAccessWidener for you.

                        If this is not desired, please either remove the file and create it in another resource directory, or remove loom.accessWidener() from your buildscript.
                    """.trimIndent()
                }

                firstAccessWidener.parent.createDirectories()
                firstAccessWidener.createFile()
                firstAccessWidener.writeText(
                    """
                        |# Auto-generated access widener
                        |accessWidener v2 named
                        |
                        |
                    """.trimMargin("|")
                )
            }
            accessWidenerPath = accessWidenerPaths.first { it.exists() }.toFile()
        }
    }

    @Suppress("UnstableApiUsage")
    override fun setDefaultMixinRefmapName(defaultName: String) {
        loom {
            mixin {
                defaultRefmapName = defaultName
            }
        }
    }

    override fun addMixinConfig(name: String) {
        // NO-OP
    }

    override fun configureProject() {
        loom {
            runs.configureEach {
                vmArg("-Xmx${allocatedMemory.get()}G")
                vmArgs(additionalJvmArgs.get())
                properties(additionalJvmProperties.get())
            }
        }

        project.plugins.withId("com.github.breadmoirai.github-release") {
            val remapJar by tasks.named<Jar>("remapJar")
            nyx.publishing.githubRelease.releaseAssets.from(remapJar)
        }
    }
}
