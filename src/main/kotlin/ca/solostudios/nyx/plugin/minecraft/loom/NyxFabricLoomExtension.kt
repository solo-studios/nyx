/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxFabricLoomExtension.kt is part of nyx
 * Last modified on 22-12-2024 11:04 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.loom

import ca.solostudios.nyx.internal.util.fabricApi
import ca.solostudios.nyx.internal.util.githubRelease
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.layout
import ca.solostudios.nyx.internal.util.loom
import ca.solostudios.nyx.internal.util.lowerCamelCaseName
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.internal.util.problemReporter
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.publishing
import ca.solostudios.nyx.internal.util.sourceSets
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.plugin.minecraft.AbstractMinecraftExtension
import ca.solostudios.nyx.plugin.minecraft.loom.task.GenerateFabricModJson
import net.fabricmc.loom.api.InterfaceInjectionExtensionAPI
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.ModSettings
import net.fabricmc.loom.api.decompilers.DecompilerOptions
import net.fabricmc.loom.configuration.FabricApiExtension
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import net.fabricmc.loom.configuration.providers.minecraft.MinecraftJarConfiguration
import net.fabricmc.loom.configuration.providers.minecraft.MinecraftSourceSets
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.problems.Severity
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.SourceSet
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.util.GradleVersion
import org.slf4j.kotlin.getLogger
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.writeText
import net.fabricmc.loom.configuration.providers.minecraft.MinecraftSourceSets.Split as SplitMinecraftSourceSet
import net.fabricmc.loom.util.Constants as LoomConstants

public class NyxFabricLoomExtension(
    override val project: Project,
) : AbstractMinecraftExtension(project) {
    private val logger by getLogger()

    /**
     * The configured loom decompilers.
     *
     * @see LoomGradleExtensionAPI.getDecompilerOptions
     */
    public val decompilers: NamedDomainObjectContainer<DecompilerOptions>
        get() = loom.decompilerOptions

    /**
     * The configured loom runs.
     *
     * @see LoomGradleExtensionAPI.getRuns
     */
    public val runs: NamedDomainObjectContainer<RunConfigSettings>
        get() = loom.runs

    /**
     * The configured mods.
     *
     * @see LoomGradleExtensionAPI.getMods
     */
    public val mods: NamedDomainObjectContainer<ModSettings>
        get() = loom.mods

    @Nested
    public val fabricModJson: FabricModJson = FabricModJson(project)

    /**
     * If interface injection is enabled.
     *
     * @see InterfaceInjectionExtensionAPI.getIsEnabled
     */
    public var interfaceInjection: Boolean
        get() = loom.interfaceInjection.getIsEnabled().isTrue
        set(value) = loom.interfaceInjection.getIsEnabled().set(value)

    /**
     * If transitive access wideners are enabled.
     *
     * @see LoomGradleExtensionAPI.getEnableTransitiveAccessWideners
     */
    public var transitiveAccessWideners: Boolean
        get() = loom.enableTransitiveAccessWideners.isTrue
        set(value) = loom.enableTransitiveAccessWideners.set(value)

    /**
     * If mod provided javadocs is enabled.
     *
     * @see LoomGradleExtensionAPI.getEnableModProvidedJavadoc
     */
    public var modProvidedJavadoc: Boolean
        get() = loom.enableModProvidedJavadoc.isTrue
        set(value) = loom.enableModProvidedJavadoc.set(value)

    /**
     * If runtime only log4j is enabled.
     *
     * @see LoomGradleExtensionAPI.getRuntimeOnlyLog4j
     */
    public var runtimeOnlyLog4j: Boolean
        get() = loom.runtimeOnlyLog4j.isTrue
        set(value) = loom.runtimeOnlyLog4j.set(value)

    /**
     * If splitting mod dependencies is enabled.
     *
     * @see LoomGradleExtensionAPI.getSplitModDependencies
     */
    public var splitModDependencies: Boolean
        get() = loom.splitModDependencies.isTrue
        set(value) = loom.splitModDependencies.set(value)

    /**
     * If split environment sourcesets is enabled.
     *
     * @see LoomGradleExtensionAPI.splitEnvironmentSourceSets
     */
    public var splitEnvironmentalSourceSet: Boolean
        get() = loom.areEnvironmentSourceSetsSplit()
        set(value) {
            if (value) loom.splitEnvironmentSourceSets()
        }

    /**
     * If the generation of only the minecraft server jar is enabled.
     *
     * @see LoomGradleExtensionAPI.serverOnlyMinecraftJar
     */
    public var serverOnlyMinecraftJar: Boolean
        get() = loom.minecraftJarConfiguration.orNull == MinecraftJarConfiguration.SERVER_ONLY
        set(value) {
            if (value) loom.serverOnlyMinecraftJar()
        }

    /**
     * If the generation of only the minecraft client jar is enabled.
     *
     * @see LoomGradleExtensionAPI.clientOnlyMinecraftJar
     */
    public var clientOnlyMinecraftJar: Boolean
        get() = loom.minecraftJarConfiguration.orNull == MinecraftJarConfiguration.CLIENT_ONLY
        set(value) {
            if (value) loom.clientOnlyMinecraftJar()
        }

    /**
     * If the generation of the minecraft should be split into client and
     * server jars.
     *
     * @see LoomGradleExtensionAPI.splitMinecraftJar
     */
    public var splitMinecraftJar: Boolean
        get() = loom.minecraftJarConfiguration.orNull == MinecraftJarConfiguration.SPLIT
        set(value) {
            if (value) loom.splitMinecraftJar()
        }

    /**
     * If the generation of the minecraft should be merged into one.
     *
     * @see LoomGradleExtensionAPI.getMinecraftJarConfiguration
     */
    public var mergedMinecraftJar: Boolean
        get() = loom.minecraftJarConfiguration.orNull == MinecraftJarConfiguration.MERGED
        set(value) {
            if (value) loom.minecraftJarConfiguration.set(MinecraftJarConfiguration.MERGED)
        }

    /**
     * If the generation of the `fabric.mod.json` file is enabled.
     */
    public val generateFabricModJson: Property<Boolean> = property()

    /**
     * Enables interface injection.
     *
     * @see interfaceInjection
     */
    public fun withInterfaceInjection() {
        interfaceInjection = true
    }

    /**
     * Enables transitive access wideners.
     *
     * @see transitiveAccessWideners
     */
    public fun withTransitiveAccessWideners() {
        transitiveAccessWideners = true
    }

    /**
     * Enables mod provided javadocs.
     *
     * @see modProvidedJavadoc
     */
    public fun withModProvidedJavadoc() {
        modProvidedJavadoc = true
    }

    /**
     * Enables runtime only log4j.
     *
     * @see runtimeOnlyLog4j
     */
    public fun withRuntimeOnlyLog4j() {
        runtimeOnlyLog4j = true
    }

    /**
     * Enables split mod dependencies.
     *
     * @see splitModDependencies
     */
    public fun withSplitModDependencies() {
        splitModDependencies = true
    }

    /**
     * Enables split environment source sets.
     *
     * @see splitEnvironmentalSourceSet
     */
    public fun withSplitEnvironmentalSourceSet() {
        splitEnvironmentalSourceSet = true
    }

    /**
     * Enables generation of only the minecraft server jar.
     *
     * @see serverOnlyMinecraftJar
     */
    public fun withServerOnlyMinecraftJar() {
        serverOnlyMinecraftJar = true
    }

    /**
     * Enables generation of only the minecraft client jar.
     *
     * @see clientOnlyMinecraftJar
     */
    public fun withClientOnlyMinecraftJar() {
        clientOnlyMinecraftJar = true
    }

    /**
     * Enables the generation of the minecraft to be split into client and
     * server jars.
     *
     * @see splitMinecraftJar
     */
    public fun withSplitMinecraftJar() {
        splitMinecraftJar = true
    }

    /**
     * Enables generation of the `fabric.mod.json` file.
     */
    public fun withGenerateFabricModJson() {
        generateFabricModJson = true
    }

    /**
     * Enables data generation.
     *
     * @see configureDataGeneration
     * @see FabricApiExtension.configureDataGeneration
     */
    public fun withDataGeneration() {
        fabricApi {
            configureDataGeneration()
        }
    }

    /**
     * Configures data generation.
     *
     * @see FabricApiExtension.configureDataGeneration
     */
    public fun configureDataGeneration(action: FabricApiExtension.DataGenerationSettings.() -> Unit) {
        fabricApi {
            configureDataGeneration(action)
        }
    }

    /**
     * Configures the loom decompilers.
     *
     * @see LoomGradleExtensionAPI.decompilers
     */
    public fun decompilers(action: NamedDomainObjectContainer<DecompilerOptions>.() -> Unit) {
        decompilers.apply(action)
    }

    /**
     * Configures the loom runs
     *
     * @see LoomGradleExtensionAPI.runs
     */
    public fun runs(action: NamedDomainObjectContainer<RunConfigSettings>.() -> Unit) {
        runs.apply(action)
    }

    /**
     * Configures the mods.
     *
     * @see LoomGradleExtensionAPI.mods
     */
    public fun mods(action: NamedDomainObjectContainer<ModSettings>.() -> Unit) {
        mods.apply(action)
    }

    public fun fabricModJson(action: FabricModJson.() -> Unit) {
        generateFabricModJson = true
        fabricModJson.apply(action)
    }

    /**
     * Adds an access widener at `src/main/resources/`[name]`.accesswidener`.
     *
     * The file will be created if it does not exist (this is to avoid an error
     * with fabric loom)
     *
     * @param name The name of the access widener, defaulting to
     *         `project.name`.
     */
    public fun accessWidener(name: String = project.name) {
        loom {
            // src/main/resources/$name.accesswidener
            val accessWidenerPaths = sourceSets[SourceSet.MAIN_SOURCE_SET_NAME].resources.srcDirs.map {
                it.resolve("$name.accesswidener").toPath()
            }
            if (accessWidenerPaths.none { it.exists() }) {
                // try creating access widener file
                val firstAccessWidener = accessWidenerPaths.first()

                val warningMessage = """
                    Can't find an access widener in any resource directory named '$name.accesswidener'.
                    Loom will report an error if it cannot find the access widener, so one has been
                    created for you at $firstAccessWidener.

                    If this is not desired, please either remove the file and create it in another
                    resource directory for your main source set, or remove loom.accessWidener()
                    from your buildscript.
                """.trimIndent()

                if (GradleVersion.current() >= GradleVersion.version("8.8")) {
                    @Suppress("UnstableApiUsage")
                    problemReporter.reporting {
                        id("missing-access-widener", "Missing Access Widener")
                        severity(Severity.WARNING)
                        details(warningMessage)
                        solution(
                            """
                                change loom.accessWidener() to use the name of an access widener that is in the
                                src/main/resources/ directory (or the resources directory for your main source set),
                                or use the newly created access widener at $firstAccessWidener.
                            """.trimIndent()
                        )
                        stackLocation()
                    }
                } else {
                    logger.warn(warningMessage)
                }

                firstAccessWidener.parent.createDirectories()
                firstAccessWidener.createFile()
                // language=Access Widener
                firstAccessWidener.writeText(
                    """
                        |accessWidener v2 named
                        |
                        |# Auto-generated access widener
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

        if (generateFabricModJson.isTrue) {
            sourceSets.named("main") {
                configureSourceSet(this)
            }

            if (loom.areEnvironmentSourceSetsSplit()) {
                val minecraftSourceSets = MinecraftSourceSets.get(project)
                val clientSourceSetName = minecraftSourceSets.getSourceSetForEnv(SplitMinecraftSourceSet.CLIENT_ONLY_SOURCE_SET_NAME)

                sourceSets.named { it == clientSourceSetName }.configureEach {
                    configureSourceSet(this)
                }
            }
        }

        project.plugins.withId("com.github.breadmoirai.github-release") {
            val remapJar by tasks.named<Jar>("remapJar")
            nyx.publishing.githubRelease.releaseAssets.from(remapJar)
        }
    }

    private fun configureSourceSet(sourceSet: SourceSet) {
        val sourceSetName = sourceSet.name

        val generateFabricModJson by tasks.register<GenerateFabricModJson>(lowerCamelCaseName("generate", sourceSetName, "FabricModJson")) {
            group = LoomConstants.TaskGroup.FABRIC
            description = "Generate the fabric.mod.json file"

            fabricModJson = this@NyxFabricLoomExtension.fabricModJson
            outputDirectory = layout.buildDirectory.dir("fabricModJson")
        }
        sourceSet.compiledBy(generateFabricModJson)

        tasks.withType<Jar>().named { it == sourceSet.sourcesJarTaskName }.configureEach {
            from(generateFabricModJson)
        }
    }
}
