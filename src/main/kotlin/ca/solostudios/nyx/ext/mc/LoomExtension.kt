package ca.solostudios.nyx.ext.mc

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.configurations
import ca.solostudios.nyx.util.loom
import ca.solostudios.nyx.util.sourceSets
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.annotations.ApiStatus
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText

public class LoomExtension(override val project: Project) : ConfiguresProject, HasProject {
    private val logger by getLogger()

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

    /**
     * Sets the mixin default refmap name to `mixins/`[name]`/refmap.json`.
     *
     * @param name The directory used for the refmap, defaults to `project.name`.
     */
    @ApiStatus.Experimental
    public fun mixinRefmapName(name: String = project.name) {
        loom {
            @Suppress("UnstableApiUsage")
            mixin {
                defaultRefmapName = "mixins/$name/refmap.json"
            }
        }
    }

    override fun onLoad() {
        configurations {

        }
    }

    override fun configureProject() {

    }
}
