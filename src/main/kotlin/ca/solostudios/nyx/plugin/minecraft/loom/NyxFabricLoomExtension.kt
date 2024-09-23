/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxFabricLoomExtension.kt is part of nyx
 * Last modified on 22-09-2024 11:49 p.m.
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
import ca.solostudios.nyx.internal.util.isTrue
import ca.solostudios.nyx.internal.util.layout
import ca.solostudios.nyx.internal.util.loom
import ca.solostudios.nyx.internal.util.lowerCamelCaseName
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.publishing
import ca.solostudios.nyx.internal.util.sourceSets
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.plugin.minecraft.AbstractMinecraftExtension
import ca.solostudios.nyx.plugin.minecraft.loom.tasks.GenerateFabricModJson
import net.fabricmc.loom.api.InterfaceInjectionExtensionAPI
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.ModSettings
import net.fabricmc.loom.api.decompilers.DecompilerOptions
import net.fabricmc.loom.configuration.FabricApiExtension
import net.fabricmc.loom.configuration.ide.RunConfigSettings
import net.fabricmc.loom.configuration.providers.minecraft.MinecraftSourceSets
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
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
import org.slf4j.kotlin.getLogger
import org.slf4j.kotlin.warn
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
    public val interfaceInjection: Property<Boolean> = property()

    /**
     * If transitive access wideners are enabled.
     *
     * @see LoomGradleExtensionAPI.getEnableTransitiveAccessWideners
     */
    public val transitiveAccessWideners: Property<Boolean> = property()

    /**
     * If mod provided javadocs is enabled.
     *
     * @see LoomGradleExtensionAPI.getEnableModProvidedJavadoc
     */
    public val modProvidedJavadoc: Property<Boolean> = property()

    /**
     * If runtime only log4j is enabled.
     *
     * @see LoomGradleExtensionAPI.getRuntimeOnlyLog4j
     */
    public val runtimeOnlyLog4j: Property<Boolean> = property()

    /**
     * If splitting mod dependencies is enabled.
     *
     * @see LoomGradleExtensionAPI.getSplitModDependencies
     */
    public val splitModDependencies: Property<Boolean> = property()

    /**
     * If split environment sourcesets is enabled.
     *
     * @see LoomGradleExtensionAPI.splitEnvironmentSourceSets
     */
    public val splitEnvironmentalSourceSet: Property<Boolean> = property()

    /**
     * If the generation of only the minecraft server jar is enabled.
     *
     * @see LoomGradleExtensionAPI.serverOnlyMinecraftJar
     */
    public val serverOnlyMinecraftJar: Property<Boolean> = property()

    /**
     * If the generation of only the minecraft client jar is enabled.
     *
     * @see LoomGradleExtensionAPI.clientOnlyMinecraftJar
     */
    public val clientOnlyMinecraftJar: Property<Boolean> = property()

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
     * Configures data generation.
     *
     * @see FabricApiExtension.configureDataGeneration
     */
    public fun configureDataGeneration(action: Action<FabricApiExtension.DataGenerationSettings>) {
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
     * Configures the loom decompilers.
     *
     * @see LoomGradleExtensionAPI.decompilers
     */
    public fun decompilers(action: Action<NamedDomainObjectContainer<DecompilerOptions>>) {
        action.execute(decompilers)
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
     * Configures the loom runs
     *
     * @see LoomGradleExtensionAPI.runs
     */
    public fun runs(action: Action<NamedDomainObjectContainer<RunConfigSettings>>) {
        action.execute(runs)
    }

    /**
     * Configures the mods.
     *
     * @see LoomGradleExtensionAPI.mods
     */
    public fun mods(action: NamedDomainObjectContainer<ModSettings>.() -> Unit) {
        mods.apply(action)
    }

    /**
     * Configures the mods.
     *
     * @see LoomGradleExtensionAPI.mods
     */
    public fun mods(action: Action<NamedDomainObjectContainer<ModSettings>>) {
        action.execute(mods)
    }

    public fun fabricModJson(action: FabricModJson.() -> Unit) {
        generateFabricModJson = true
        fabricModJson.apply(action)
    }

    public fun fabricModJson(action: Action<FabricModJson>) {
        generateFabricModJson = true
        action.execute(fabricModJson)
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
        if (project.plugins.hasPlugin("org.jetbrains.kotlin.jvm")) // kotlin sources jar task breaks the normal sources jar
            tasks.withType<Jar>().named { it == "kotlinSourcesJar" }.configureEach { enabled = false }

        if (interfaceInjection.isPresent)
            loom.interfaceInjection.getIsEnabled().set(interfaceInjection)

        if (transitiveAccessWideners.isPresent)
            loom.enableTransitiveAccessWideners = transitiveAccessWideners

        if (modProvidedJavadoc.isPresent)
            loom.enableModProvidedJavadoc = modProvidedJavadoc

        if (runtimeOnlyLog4j.isPresent)
            loom.runtimeOnlyLog4j = runtimeOnlyLog4j

        if (splitModDependencies.isPresent)
            loom.splitModDependencies = splitModDependencies

        if (splitEnvironmentalSourceSet.isTrue)
            loom.splitEnvironmentSourceSets()

        if (serverOnlyMinecraftJar.isTrue)
            loom.serverOnlyMinecraftJar()

        if (clientOnlyMinecraftJar.isTrue)
            loom.clientOnlyMinecraftJar()

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
