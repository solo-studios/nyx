/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GenerateFabricModJson.kt is part of nyx
 * Last modified on 15-09-2024 07:16 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.loom.tasks

import ca.solostudios.nyx.internal.AllOpen
import ca.solostudios.nyx.internal.SerialFabricModJson
import ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.AdaptedEntrypoint
import ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.StringEntrypoint
import ca.solostudios.nyx.internal.util.directoryProperty
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.toJsonElement
import ca.solostudios.nyx.plugin.minecraft.loom.FabricModJson
import ca.solostudios.nyx.plugin.minecraft.loom.FabricModJson.Dependency
import ca.solostudios.nyx.plugin.minecraft.loom.FabricModJson.Environment
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.json.put
import org.gradle.api.DefaultTask
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import kotlin.io.path.outputStream
import ca.solostudios.nyx.internal.SerialFabricModJson.Environment as SerialEnvironment
import ca.solostudios.nyx.internal.SerialFabricModJson.MixinConfig.EnvironmentMixinConfig as SerialEnvironmentMixinConfig
import ca.solostudios.nyx.internal.SerialFabricModJson.MixinConfig.StringMixinConfig as SerialStringMixinConfig
import ca.solostudios.nyx.internal.SerialFabricModJson.ModIcon.ModIconMap as SerialModIconMap
import ca.solostudios.nyx.internal.SerialFabricModJson.Person.ContactablePerson as SerialContactablePerson
import ca.solostudios.nyx.internal.SerialFabricModJson.Person.NamedPerson as SerialNamedPerson

@AllOpen
@DisableCachingByDefault(because = "Not worth caching")
public class GenerateFabricModJson : DefaultTask() {
    // @Input
    @Nested
    public val fabricModJson: Property<FabricModJson> = property()

    @OutputDirectory
    public val outputDirectory: DirectoryProperty = directoryProperty()

    @TaskAction
    @OptIn(ExperimentalSerializationApi::class)
    private fun generateJson() {
        val serialModJson = fabricModJson.get().toSerial()

        outputPath("fabric.mod.json").outputStream().buffered().use { outputStream ->
            json.encodeToStream(serialModJson, outputStream)
        }
    }

    private fun outputPath(path: String) = outputDirectory.get().asFile.toPath().resolve(path)

    private fun FabricModJson.toSerial(): SerialFabricModJson {
        val entrypoints = this.entrypoints.takeIf { it.isNotEmpty() }?.associate { container ->
            container.target to container.entrypoints.get().map {
                if (it.adapter.isPresent)
                    AdaptedEntrypoint(it.adapter.get(), it.value.get())
                else
                    StringEntrypoint(it.value.get())
            }
        }

        val mixins = this.mixins.takeIf { it.isNotEmpty() }?.map { config ->
            if (config.environment.isPresent)
                SerialEnvironmentMixinConfig(config.entrypoint, config.environment.map { it.toSerial() }.get())
            else
                SerialStringMixinConfig(config.entrypoint)
        }

        val contact = with(this.contact) {
            buildMap {
                if (homepage.isPresent)
                    put("homepage", homepage.get())
                if (issues.isPresent)
                    put("issues", issues.get())
                if (source.isPresent)
                    put("source", source.get())
                if (email.isPresent)
                    put("email", email.get())
                if (irc.isPresent)
                    put("irc", irc.get())

                if (other.isPresent)
                    putAll(other.get())
            }
        }.takeIf { it.isNotEmpty() }

        val icons = this.icons.orNull?.takeIf { it.isNotEmpty() }?.let { icons ->
            if (icons.size == 1) {
                SerialFabricModJson.ModIcon.StringModIcon(icons.single().file)
            } else {
                SerialModIconMap(icons.associate { it.size!! to it.file })
            }
        }

        val modmenu = with(this.modmenu) {
            buildJsonObject {
                badges.orNull?.takeIf { it.isNotEmpty() }?.let { badges -> put("badges", badges.toJsonElement()) }

                buildMap {
                    links.buyMeACoffee.orNull?.let { buyMeACoffee -> put("modmenu.buymeacoffee", buyMeACoffee) }
                    links.coindrop.orNull?.let { coindrop -> put("modmenu.coindrop", coindrop) }
                    links.crowdin.orNull?.let { crowdin -> put("modmenu.crowdin", crowdin) }
                    links.curseforge.orNull?.let { curseforge -> put("modmenu.curseforge", curseforge) }
                    links.discord.orNull?.let { discord -> put("modmenu.discord", discord) }
                    links.donate.orNull?.let { donate -> put("modmenu.donate", donate) }
                    links.flattr.orNull?.let { flattr -> put("modmenu.flattr", flattr) }
                    links.githubReleases.orNull?.let { githubReleases -> put("modmenu.github_releases", githubReleases) }
                    links.githubSponsors.orNull?.let { githubSponsors -> put("modmenu.github_sponsors", githubSponsors) }
                    links.kofi.orNull?.let { kofi -> put("modmenu.kofi", kofi) }
                    links.liberapay.orNull?.let { liberapay -> put("modmenu.liberapay", liberapay) }
                    links.mastodon.orNull?.let { mastodon -> put("modmenu.mastodon", mastodon) }
                    links.modrinth.orNull?.let { modrinth -> put("modmenu.modrinth", modrinth) }
                    links.openCollective.orNull?.let { openCollective -> put("modmenu.opencollective", openCollective) }
                    links.patreon.orNull?.let { patreon -> put("modmenu.patreon", patreon) }
                    links.paypal.orNull?.let { paypal -> put("modmenu.paypal", paypal) }
                    links.reddit.orNull?.let { reddit -> put("modmenu.reddit", reddit) }
                    links.twitch.orNull?.let { twitch -> put("modmenu.twitch", twitch) }
                    links.twitter.orNull?.let { twitter -> put("modmenu.twitter", twitter) }
                    links.wiki.orNull?.let { wiki -> put("modmenu.wiki", wiki) }
                    links.youtube.orNull?.let { youtube -> put("modmenu.youtube", youtube) }

                    links.other.get().takeIf { it.isNotEmpty() }?.let { other ->
                        for ((key, value) in other)
                            put(key, value)
                    }
                }.takeIf { it.isNotEmpty() }?.let { links -> put("links", links.toJsonElement()) }

                buildMap {
                    parent.id.orNull?.let { id -> put("id", id) }
                    parent.name.orNull?.let { name -> put("name", name) }
                    parent.description.orNull?.let { description -> put("description", description) }
                    parent.icon.orNull?.let { icon -> put("icon", icon) }
                    parent.badges.get().takeIf { it.isNotEmpty() }?.let { badges -> put("badges", badges) }
                }.takeIf { it.isNotEmpty() }?.let { parent -> put("parent", parent.toJsonElement()) }

                if (updateChecker.isPresent)
                    put("update_checker", updateChecker.get())
            }
        }

        val custom = buildJsonObject {
            if (modmenu.isNotEmpty())
                put("modmenu", modmenu)

            for ((key, value) in custom.get()) {
                put(key, value.toJsonElement())
            }
        }.takeIf { it.isNotEmpty() }

        return SerialFabricModJson(
            id = this.id.get(),
            name = this.name.orNull,
            version = this.version.get(),
            provides = this.provides.orNull?.takeIf { it.isNotEmpty() },
            environment = this.environment.orNull?.toSerial(),
            entrypoints = entrypoints,
            mixins = mixins,
            accessWidener = this.accessWidener.orNull,
            depends = this.depends.toSerial(),
            recommends = this.recommends.toSerial(),
            suggests = this.suggests.toSerial(),
            conflicts = this.conflicts.toSerial(),
            breaks = this.breaks.toSerial(),
            description = this.description.orNull?.takeIf { it.isNotBlank() },
            authors = this.authors.toSerial(),
            contributors = this.contributors.toSerial(),
            contact = contact,
            license = this.licenses.orNull?.takeIf { it.isNotEmpty() },
            icon = icons,
            languageAdapters = this.languageAdapters.orNull?.takeIf { it.isNotEmpty() },
            custom = custom
        )
    }

    private fun Environment.toSerial(): SerialEnvironment {
        return when (this) {
            Environment.UNIVERSAL -> SerialEnvironment.UNIVERSAL
            Environment.CLIENT -> SerialEnvironment.CLIENT
            Environment.SERVER -> SerialEnvironment.SERVER
        }
    }

    private fun NamedDomainObjectContainer<Dependency>.toSerial(): Map<String, List<String>>? {
        return takeIf { it.isNotEmpty() }?.associate { dependency ->
            dependency.mod to (dependency.versions.orNull ?: listOf("*"))
        }
    }

    private fun NamedDomainObjectContainer<FabricModJson.Person>.toSerial(): List<SerialFabricModJson.Person>? {
        return takeIf { it.isNotEmpty() }?.map { person ->
            if (person.contact.isPresent)
                SerialContactablePerson(person.person, person.contact.get())
            else
                SerialNamedPerson(person.person)
        }
    }

    private companion object {
        val json = Json {
            prettyPrint = true
        }
    }
}
