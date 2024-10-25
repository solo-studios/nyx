/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file FabricModJson.kt is part of nyx
 * Last modified on 25-10-2024 12:01 p.m.
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

import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.util.domainObjectContainer
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.loom
import ca.solostudios.nyx.internal.util.mapProperty
import ca.solostudios.nyx.internal.util.maybeRegister
import ca.solostudios.nyx.internal.util.minecraft
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.provider
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.assign
import org.gradle.api.tasks.Internal as IgnoreForTaskInputs

/**
 * An extension used for generating a `fabric.mod.json` file according to
 * the spec.
 */
public class FabricModJson(override val project: Project) : HasProject {
    /**
     * The id of the mod.
     */
    @get:Input
    public val id: Property<String> = property<String>().convention(nyx.info.module)

    /**
     * The human-readable name of the mod.
     */
    @get:Input
    public val name: Property<String> = property<String>().convention(nyx.info.name)

    /**
     * The version of the mod.
     */
    @get:Input
    public val version: Property<String> = property<String>().convention(provider { nyx.info.version })

    /**
     * The description of this mod.
     */
    @get:Input
    public val description: Property<String> = property<String>().convention(provider { nyx.info.description })

    /**
     * A list of other mods that this mod provides.
     */
    @get:Input
    public val provides: ListProperty<String> = listProperty()

    /**
     * Which environment type this mod is compatible with. The mod is only
     * loaded in the appropriate environment type.
     */
    @get:Input
    @get:Optional
    public val environment: Property<Environment> = property<Environment>().convention(provider {
        val minecraft = nyx.minecraft
        when {
            minecraft !is NyxFabricLoomExtension -> null
            minecraft.clientOnlyMinecraftJar     -> Environment.CLIENT
            minecraft.serverOnlyMinecraftJar     -> Environment.SERVER
            minecraft.splitMinecraftJar || minecraft.mergedMinecraftJar -> Environment.UNIVERSAL
            else                                 -> Environment.UNIVERSAL
        }
    })

    /**
     * The entrypoints to this mod.
     */
    @get:Input
    public val entrypoints: NamedDomainObjectContainer<EntrypointContainer> = domainObjectContainer { target ->
        EntrypointContainer(project, target)
    }.also { entrypoints ->
        entrypoints.register("main")
        entrypoints.register("client")
        entrypoints.register("server")
    }

    /**
     * A list of people who authored this mod.
     */
    @get:Input
    public val authors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * A list of people who contributed to this mod.
     */
    @get:Input
    public val contributors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * The mixin configs for this mod.
     */
    @get:Input
    public val mixins: NamedDomainObjectContainer<MixinConfig> = domainObjectContainer { name -> MixinConfig(project, name) }

    /**
     * The access widener used for this mod.
     */
    @get:Input
    @get:Optional
    public val accessWidener: Property<String> = property<String>().convention(loom.accessWidenerPath.map { it.asFile.name })

    /**
     * A list of mods that this mod depends on.
     */
    @get:Input
    public val depends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod recommends.
     */
    @get:Input
    public val recommends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod suggests.
     */
    @get:Input
    public val suggests: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod conflicts with.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    @get:Input
    public val conflicts: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod breaks.
     *
     * A breaking mod will cause the game to fail to launch.
     */
    @get:Input
    public val breaks: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * Contact information for this mod, including, but not limited to,
     * - the homepage
     * - where to submit issue (typically github issues)
     * - where the source code is located
     * - etc.
     */
    @Nested
    public val contact: ModContact = ModContact(project)

    /**
     * The license of this mod. Use this property when only a single license is
     * being configured. If multiple licenses are in use, then use [licenses]
     * instead.
     *
     * Internally, this delegates to the [licenses] field. However, if only
     * a single license is specified, then it will be serialized as a string
     * rather than as a list.
     *
     * @see licenses
     */
    @get:IgnoreForTaskInputs
    public var license: String
        get() = licenses.get().single()
        set(value) = licenses.set(listOf(value))

    /**
     * The list of licenses for this mod.
     *
     * Typically, a mod only has one license, so consider using [license]
     * instead.
     *
     * @see license
     */
    @get:Input
    public val licenses: ListProperty<String> = listProperty<String>().convention(nyx.info.license.name.map { listOf(it) }.orElse(listOf()))

    /**
     * A list of icons for this mod.
     */
    @get:Input
    public val icons: ListProperty<ModIcon> = listProperty()

    /**
     * A list of language adapters that this mod provides.
     */
    @get:Input
    public val languageAdapters: MapProperty<String, String> = mapProperty()

    /**
     * Mod Menu configuration for this mod.
     *
     * This creates an object under the `custom.modmenu` key.
     */
    @Nested
    public val modmenu: ModMenu = ModMenu(project)

    /**
     * A list of custom properties.
     */
    @get:Input
    public val custom: MapProperty<String, Any> = mapProperty()

    public fun provide(providedId: String) {
        provides.add(providedId)
    }

    // region Entrypoints
    public fun entrypoints(action: NamedDomainObjectContainer<EntrypointContainer>.() -> Unit) {
        entrypoints.action()
    }

    public fun entrypoints(action: Action<NamedDomainObjectContainer<EntrypointContainer>>) {
        action.execute(entrypoints)
    }

    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.entry(name: String, entrypoint: String, adapter: String? = null) {
        maybeRegister(name) {
            entrypoint(entrypoint, adapter)
        }
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.entry(name: String, action: EntrypointContainer.() -> Unit) {
        maybeRegister(name, action)
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.entry(name: String, action: Action<EntrypointContainer>) {
        maybeRegister(name, action)
    }

    // region Main Entrypoints
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.main(entrypoint: String, adapter: String? = null) {
        entry("main") {
            entrypoint(entrypoint, adapter)
        }
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.main(action: EntrypointContainer.() -> Unit) {
        entry("main", action)
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.main(action: Action<EntrypointContainer>) {
        entry("main", action)
    }
    // endregion

    // region Client Entrypoints
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.client(entrypointClass: String, adapter: String? = null) {
        entry("client") {
            entrypoint(entrypointClass, adapter)
        }
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.client(action: EntrypointContainer.() -> Unit) {
        entry("client", action)
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.client(action: Action<EntrypointContainer>): Unit = entry("client", action)
    // endregion

    // region Server Entrypoints
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.server(entrypointClass: String, adapter: String? = null) {
        entry("server") {
            entrypoint(entrypointClass, adapter)
        }
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.server(action: EntrypointContainer.() -> Unit) {
        entry("server", action)
    }

    public fun NamedDomainObjectContainer<EntrypointContainer>.server(action: Action<EntrypointContainer>) {
        entry("server", action)
    }
    // endregion
    // endregion

    public fun authors(action: NamedDomainObjectContainer<Person>.() -> Unit) {
        authors.action()
    }

    public fun authors(action: Action<NamedDomainObjectContainer<Person>>) {
        action.execute(authors)
    }

    @JvmOverloads
    public fun author(name: String, contact: Map<String, String>? = null) {
        authors.register(name) {
            if (contact != null)
                this.contact = contact
        }
    }

    public fun contributors(action: NamedDomainObjectContainer<Person>.() -> Unit) {
        contributors.action()
    }

    public fun contributors(action: Action<NamedDomainObjectContainer<Person>>) {
        action.execute(contributors)
    }

    @JvmOverloads
    public fun contributor(name: String, contact: Map<String, String>? = null) {
        contributors.register(name) {
            if (contact != null)
                this.contact = contact
        }
    }

    public fun mixins(action: NamedDomainObjectContainer<MixinConfig>.() -> Unit) {
        mixins.action()
    }

    public fun mixins(action: Action<NamedDomainObjectContainer<MixinConfig>>) {
        action.execute(mixins)
    }

    @JvmOverloads
    public fun mixin(config: String, environment: Environment? = null) {
        mixins.register(config) {
            if (environment != null)
                this.environment = environment
        }
    }

    public fun depends(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        depends.action()
    }

    public fun depends(action: Action<NamedDomainObjectContainer<Dependency>>) {
        action.execute(depends)
    }

    @JvmOverloads
    public fun depends(mod: String, version: String? = null) {
        depends.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    public fun depends(mod: String, versions: List<String>) {
        depends.register(mod) {
            this.versions = versions
        }
    }

    public fun recommends(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        recommends.action()
    }

    public fun recommends(action: Action<NamedDomainObjectContainer<Dependency>>) {
        action.execute(recommends)
    }

    @JvmOverloads
    public fun recommends(mod: String, version: String? = null) {
        recommends.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    public fun recommends(mod: String, versions: List<String>) {
        recommends.register(mod) {
            this.versions = versions
        }
    }

    public fun suggests(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        suggests.action()
    }

    public fun suggests(action: Action<NamedDomainObjectContainer<Dependency>>) {
        action.execute(suggests)
    }

    @JvmOverloads
    public fun suggests(mod: String, version: String? = null) {
        suggests.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    public fun suggests(mod: String, versions: List<String>) {
        suggests.register(mod) {
            this.versions = versions
        }
    }

    public fun conflicts(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        conflicts.action()
    }

    public fun conflicts(action: Action<NamedDomainObjectContainer<Dependency>>) {
        action.execute(conflicts)
    }

    @JvmOverloads
    public fun conflicts(mod: String, version: String? = null) {
        conflicts.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    public fun conflicts(mod: String, versions: List<String>) {
        conflicts.register(mod) {
            this.versions = versions
        }
    }

    public fun breaks(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        breaks.action()
    }

    public fun breaks(action: Action<NamedDomainObjectContainer<Dependency>>) {
        action.execute(breaks)
    }

    @JvmOverloads
    public fun breaks(mod: String, version: String? = null) {
        breaks.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    public fun breaks(mod: String, versions: List<String>) {
        breaks.register(mod) {
            this.versions = versions
        }
    }

    public fun contact(action: ModContact.() -> Unit) {
        contact.action()
    }

    public fun contact(action: Action<ModContact>) {
        action.execute(contact)
    }

    @JvmOverloads
    public fun icon(file: String, size: Int? = null) {
        icons.add(ModIcon(file, size))
    }

    public fun languageAdapter(languageName: String, adapterClass: String) {
        languageAdapters.put(languageName, adapterClass)
    }

    public fun modmenu(action: ModMenu.() -> Unit) {
        modmenu.action()
    }

    public fun modmenu(action: Action<ModMenu>) {
        action.execute(modmenu)
    }

    public fun custom(key: String, value: Any) {
        custom.put(key, value)
    }

    /**
     * Indicates compatibility with a specific environemnt.
     */
    public enum class Environment {
        /**
         * Indicates compatibility with both server-side and client-side
         * environments.
         */
        UNIVERSAL,

        /**
         * Indicates compatibility with only client-side environments.
         */
        CLIENT,

        /**
         * Indicates compatibility with only server-side environments.
         */
        SERVER
    }

    /**
     * A named entrypoint container.
     *
     * Contains a list of entrypoints.
     *
     * @see Entrypoint
     */
    public data class EntrypointContainer(
        @get:IgnoreForTaskInputs
        override val project: Project,
        /**
         * The name of the target entrypoint.
         */
        @get:Input
        public val target: String,
    ) : HasProject, Named {
        /**
         * A list of entrypoints.
         */
        @get:Input
        public val entrypoints: NamedDomainObjectContainer<Entrypoint> = domainObjectContainer { value -> Entrypoint(project, value) }

        @JvmOverloads
        public fun entrypoint(className: String, adapter: String? = null) {
            entrypoints.register(className) {
                if (adapter != null)
                    this.adapter = adapter
            }
        }

        @IgnoreForTaskInputs
        override fun getName(): String = target
    }

    /**
     * An entrypoint reference to be loaded.
     */
    public class Entrypoint(
        @get:IgnoreForTaskInputs
        override val project: Project,
        /**
         * The value of the entrypoint.
         */
        @get:Input
        public val value: String,
    ) : HasProject, Named {
        /**
         * The entrypoint adapter.
         *
         * Optional.
         */
        @get:Input
        @get:Optional
        public val adapter: Property<String> = property()

        @IgnoreForTaskInputs
        override fun getName(): String = value
    }

    public class MixinConfig(
        @get:IgnoreForTaskInputs
        override val project: Project,
        @get:Input
        public val config: String,
    ) : HasProject, Named {
        @get:Input
        @get:Optional
        public val environment: Property<Environment> = property()

        @IgnoreForTaskInputs
        override fun getName(): String = config
    }

    public class Dependency(
        @get:IgnoreForTaskInputs
        override val project: Project,
        @get:Input
        public val mod: String,
    ) : HasProject, Named {
        @get:IgnoreForTaskInputs
        public var version: String
            get() = versions.get().single()
            set(value) = versions.set(listOf(value))

        @get:Input
        public val versions: ListProperty<String> = listProperty()

        @IgnoreForTaskInputs
        override fun getName(): String = mod
    }

    public class Person(
        @get:IgnoreForTaskInputs
        override val project: Project,
        @get:Input
        public val person: String,
    ) : HasProject, Named {
        @get:Input
        public val contact: MapProperty<String, String> = mapProperty()

        @IgnoreForTaskInputs
        override fun getName(): String = person
    }

    public class ModContact(
        @get:IgnoreForTaskInputs
        override val project: Project,
    ) : HasProject {
        @get:Input
        @get:Optional
        public val homepage: Property<String> = property()

        @get:Input
        @get:Optional
        public val issues: Property<String> = property<String>().convention(nyx.info.repository.projectIssues)

        @get:Input
        @get:Optional
        public val source: Property<String> = property<String>().convention(nyx.info.repository.projectUrl)

        @get:Input
        @get:Optional
        public val email: Property<String> = property()

        @get:Input
        @get:Optional
        public val irc: Property<String> = property()

        @get:Input
        public val other: MapProperty<String, String> = mapProperty()
    }

    public data class ModIcon(
        @get:Input
        public val file: String,
        @get:Input
        public val size: Int? = null,
    )

    public class ModMenu(
        override val project: Project,
    ) : HasProject {
        @get:Input
        public val badges: ListProperty<String> = listProperty()

        @get:Nested
        @get:Optional
        public val links: ModMenuLinks = ModMenuLinks(project)

        @get:Nested
        @get:Optional
        public val parent: ModMenuParent = ModMenuParent(project)

        @get:Input
        @get:Optional
        public val updateChecker: Property<Boolean> = property()

        public fun links(action: ModMenuLinks.() -> Unit) {
            links.action()
        }

        public fun breaks(action: Action<ModMenuLinks>) {
            action.execute(links)
        }

        public fun parent(action: ModMenuParent.() -> Unit) {
            parent.action()
        }

        public fun parent(action: Action<ModMenuParent>) {
            action.execute(parent)
        }

        public fun library(): Unit = badges.add("library")
        public fun deprecated(): Unit = badges.add("deprecated")

        public class ModMenuLinks(
            override val project: Project,
        ) : HasProject {
            @get:Input
            @get:Optional
            public val buyMeACoffee: Property<String> = property()

            @get:Input
            @get:Optional
            public val coindrop: Property<String> = property()

            @get:Input
            @get:Optional
            public val crowdin: Property<String> = property()

            @get:Input
            @get:Optional
            public val curseforge: Property<String> = property()

            @get:Input
            @get:Optional
            public val discord: Property<String> = property()

            @get:Input
            @get:Optional
            public val donate: Property<String> = property()

            @get:Input
            @get:Optional
            public val flattr: Property<String> = property()

            @get:Input
            @get:Optional
            public val githubReleases: Property<String> = property()

            @get:Input
            @get:Optional
            public val githubSponsors: Property<String> = property()

            @get:Input
            @get:Optional
            public val kofi: Property<String> = property()

            @get:Input
            @get:Optional
            public val liberapay: Property<String> = property()

            @get:Input
            @get:Optional
            public val mastodon: Property<String> = property()

            @get:Input
            @get:Optional
            public val modrinth: Property<String> = property()

            @get:Input
            @get:Optional
            public val openCollective: Property<String> = property()

            @get:Input
            @get:Optional
            public val patreon: Property<String> = property()

            @get:Input
            @get:Optional
            public val paypal: Property<String> = property()

            @get:Input
            @get:Optional
            public val reddit: Property<String> = property()

            @get:Input
            @get:Optional
            public val twitch: Property<String> = property()

            @get:Input
            @get:Optional
            public val twitter: Property<String> = property()

            @get:Input
            @get:Optional
            public val wiki: Property<String> = property()

            @get:Input
            @get:Optional
            public val youtube: Property<String> = property()

            @get:Input
            @get:Optional
            public val other: MapProperty<String, String> = mapProperty()
        }

        public class ModMenuParent(
            override val project: Project,
        ) : HasProject {
            @get:Input
            @get:Optional
            public val id: Property<String> = property()

            // ommit if real mod
            @get:Input
            @get:Optional
            public val name: Property<String> = property()

            @get:Input
            @get:Optional
            public val description: Property<String> = property()

            @get:Input
            @get:Optional
            public val icon: Property<String> = property()

            @get:Input
            public val badges: ListProperty<String> = listProperty()

            public fun library(): Unit = badges.add("library")
            public fun deprecated(): Unit = badges.add("deprecated")
        }
    }
}
