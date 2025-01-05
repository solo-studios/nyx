/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file FabricModJson.kt is part of nyx
 * Last modified on 05-01-2025 12:09 a.m.
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
    @get:Nested
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
    @get:Nested
    public val authors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * A list of people who contributed to this mod.
     */
    @get:Nested
    public val contributors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * The mixin configs for this mod.
     */
    @get:Nested
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
    @get:Nested
    public val depends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod recommends.
     */
    @get:Nested
    public val recommends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod suggests.
     */
    @get:Nested
    public val suggests: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod conflicts with.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    @get:Nested
    public val conflicts: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod breaks.
     *
     * A breaking mod will cause the game to fail to launch.
     */
    @get:Nested
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
    @get:Nested
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
    @get:Nested
    public val modmenu: ModMenu = ModMenu(project)

    /**
     * A list of custom properties.
     */
    @get:Input
    public val custom: MapProperty<String, Any> = mapProperty()

    /**
     * Adds a mod to the list of other mods that this mod provides.
     *
     * @see provides
     */
    public fun provide(providedId: String) {
        provides.add(providedId)
    }

    // region Entrypoints
    /**
     * Configures the entrypoints for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     main("package.MyJavaMod")
     *     client("package.MyKotlinClientMod", "kotlin")
     * }
     * ```
     */
    public fun entrypoints(action: NamedDomainObjectContainer<EntrypointContainer>.() -> Unit) {
        entrypoints.action()
    }

    /**
     * Adds a named entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     entry("emi", "package.MyJavaEmiPlugin")
     *     entry("emi", "package.MyKotlinEmiPlugin", "kotlin")
     * }
     * ```
     *
     * @see entrypoints
     */
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.entry(name: String, entrypoint: String, adapter: String? = null) {
        maybeRegister(name) {
            entrypoint(entrypoint, adapter)
        }
    }

    /**
     * Adds a named entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     entry("emi") {
     *         entrypoint("package.MyJavaEmiPlugin")
     *         entrypoint("package.MyKotlinEmiPlugin", "kotlin")
     *     }
     * }
     * ```
     *
     * @see entrypoints
     */
    public fun NamedDomainObjectContainer<EntrypointContainer>.entry(name: String, action: EntrypointContainer.() -> Unit) {
        maybeRegister(name, action)
    }

    // region Main Entrypoints
    /**
     * Adds a `main` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     main("package.MyJavaMod")
     *     main("package.MyKotlinMod", "kotlin")
     * }
     * ```
     *
     * @see entrypoints
     */
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.main(entrypoint: String, adapter: String? = null) {
        entry("main") {
            entrypoint(entrypoint, adapter)
        }
    }

    /**
     * Adds a `main` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     main {
     *         entrypoint("package.MyJavaMod")
     *         entrypoint("package.MyKotlinMod", "kotlin")
     *     }
     * }
     * ```
     *
     * @see entrypoints
     */
    public fun NamedDomainObjectContainer<EntrypointContainer>.main(action: EntrypointContainer.() -> Unit) {
        entry("main", action)
    }
    // endregion

    // region Client Entrypoints
    /**
     * Adds a `client` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     client("package.MyJavaMod")
     *     client("package.MyKotlinMod", "kotlin")
     * }
     * ```
     *
     * @see entrypoints
     */
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.client(entrypointClass: String, adapter: String? = null) {
        entry("client") {
            entrypoint(entrypointClass, adapter)
        }
    }

    /**
     * Adds a `client` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     client {
     *         entrypoint("package.MyJavaMod")
     *         entrypoint("package.MyKotlinMod", "kotlin")
     *     }
     * }
     * ```
     *
     * @see entrypoints
     */
    public fun NamedDomainObjectContainer<EntrypointContainer>.client(action: EntrypointContainer.() -> Unit) {
        entry("client", action)
    }
    // endregion

    // region Server Entrypoints
    /**
     * Adds a `server` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     server("package.MyJavaMod")
     *     server("package.MyKotlinMod", "kotlin")
     * }
     * ```
     *
     * @see entrypoints
     */
    @JvmOverloads
    public fun NamedDomainObjectContainer<EntrypointContainer>.server(entrypointClass: String, adapter: String? = null) {
        entry("server") {
            entrypoint(entrypointClass, adapter)
        }
    }

    /**
     * Adds a `server` entrypoint for this mod.
     *
     * For example:
     * ```kotlin
     * entrypoints {
     *     server {
     *         entrypoint("package.MyJavaMod")
     *         entrypoint("package.MyKotlinMod", "kotlin")
     *     }
     * }
     * ```
     *
     * @see entrypoints
     */
    public fun NamedDomainObjectContainer<EntrypointContainer>.server(action: EntrypointContainer.() -> Unit) {
        entry("server", action)
    }
    // endregion
    // endregion

    /**
     * Configures the list of people who authored this mod.
     */
    public fun authors(action: NamedDomainObjectContainer<Person>.() -> Unit) {
        authors.action()
    }

    /**
     * Adds an author for this mod.
     *
     * @see authors
     */
    @JvmOverloads
    public fun author(name: String, contact: Map<String, String>? = null) {
        authors.register(name) {
            if (contact != null)
                this.contact = contact
        }
    }

    /**
     * Configures the list of people who contributed to this mod.
     */
    public fun contributors(action: NamedDomainObjectContainer<Person>.() -> Unit) {
        contributors.action()
    }

    /**
     * Adds a contributor for this mod.
     */
    @JvmOverloads
    public fun contributor(name: String, contact: Map<String, String>? = null) {
        contributors.register(name) {
            if (contact != null)
                this.contact = contact
        }
    }

    /**
     * Configures the mixin configs for this mod.
     */
    public fun mixins(action: NamedDomainObjectContainer<MixinConfig>.() -> Unit) {
        mixins.action()
    }

    /**
     * Adds a mixin config for this mod.
     */
    @JvmOverloads
    public fun mixin(config: String, environment: Environment? = null) {
        mixins.register(config) {
            if (environment != null)
                this.environment = environment
        }
    }

    /**
     * Configures the list of mods that this mod depends on.
     */
    public fun depends(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        depends.action()
    }

    /**
     * Adds a mod that this mod depends on.
     */
    @JvmOverloads
    public fun depends(mod: String, version: String? = null) {
        depends.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    /**
     * Adds a mod that this mod depends on, with multiple compatible versions.
     */
    public fun depends(mod: String, versions: List<String>) {
        depends.register(mod) {
            this.versions = versions
        }
    }

    /**
     * Configures the list of mods that this mod recommends.
     */
    public fun recommends(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        recommends.action()
    }

    /**
     * Adds a mod that this mod recommends.
     */
    @JvmOverloads
    public fun recommends(mod: String, version: String? = null) {
        recommends.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    /**
     * Adds a mod that this mod recommends, with multiple compatible versions.
     */
    public fun recommends(mod: String, versions: List<String>) {
        recommends.register(mod) {
            this.versions = versions
        }
    }

    /**
     * Configures the list of mods that this mod suggests.
     */
    public fun suggests(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        suggests.action()
    }

    /**
     * Adds a mod that this mod suggests.
     */
    @JvmOverloads
    public fun suggests(mod: String, version: String? = null) {
        suggests.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    /**
     * Adds a mod that this mod suggests, with multiple compatible versions.
     */
    public fun suggests(mod: String, versions: List<String>) {
        suggests.register(mod) {
            this.versions = versions
        }
    }

    /**
     * Configures the list of mods that this mod conflicts with.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    public fun conflicts(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        conflicts.action()
    }

    /**
     * Adds a mod that this mod conflicts with.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    @JvmOverloads
    public fun conflicts(mod: String, version: String? = null) {
        conflicts.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    /**
     * Adds a mod that this mod conflicts with, with multiple compatible
     * versions.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    public fun conflicts(mod: String, versions: List<String>) {
        conflicts.register(mod) {
            this.versions = versions
        }
    }

    /**
     * Configures the list of mods that this mod breaks.
     *
     * A breaking mod will cause the game to fail to launch.
     */
    public fun breaks(action: NamedDomainObjectContainer<Dependency>.() -> Unit) {
        breaks.action()
    }

    /**
     * Adds a mod that this mod breaks.
     *
     * A breaking mod will cause the game to fail to launch.
     */
    @JvmOverloads
    public fun breaks(mod: String, version: String? = null) {
        breaks.register(mod) {
            if (version != null)
                this.version = version
        }
    }

    /**
     * Adds a mod that this mod breaks, with multiple compatible versions.
     *
     * A breaking mod will cause the game to fail to launch.
     */
    public fun breaks(mod: String, versions: List<String>) {
        breaks.register(mod) {
            this.versions = versions
        }
    }

    /**
     * Configures the contact information for this mod.
     */
    public fun contact(action: ModContact.() -> Unit) {
        contact.action()
    }

    /**
     * Adds an icon for this mod
     *
     * @see icons
     */
    @JvmOverloads
    public fun icon(file: String, size: Int? = null) {
        icons.add(ModIcon(file, size))
    }

    /**
     * Adds a language adapter that this mod provides
     *
     * @see languageAdapters
     */
    public fun languageAdapter(languageName: String, adapterClass: String) {
        languageAdapters.put(languageName, adapterClass)
    }

    /**
     * Configures Mod Menu for this mod.
     */
    public fun modmenu(action: ModMenu.() -> Unit) {
        modmenu.action()
    }

    /**
     * Adds a custom property for this mod.
     */
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
        @get:Nested
        public val entrypoints: NamedDomainObjectContainer<Entrypoint> = domainObjectContainer { value -> Entrypoint(project, value) }

        /**
         * Adds an entrypoint to this container.
         */
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

    /**
     * A mixin config indicates the location for fabric to look for the
     * `*.mixins.json` file.
     */
    public class MixinConfig(
        @get:IgnoreForTaskInputs
        override val project: Project,
        /**
         * The location of the mixin config json file.
         */
        @get:Input
        public val config: String,
    ) : HasProject, Named {
        /**
         * The environment that this mixin config applies to.
         */
        @get:Input
        @get:Optional
        public val environment: Property<Environment> = property()

        @IgnoreForTaskInputs
        override fun getName(): String = config
    }

    /**
     * A mod dependency.
     */
    public class Dependency(
        @get:IgnoreForTaskInputs
        override val project: Project,
        /**
         * The name of the dependent mod.
         */
        @get:Input
        public val mod: String,
    ) : HasProject, Named {
        /**
         * The version that this mod is compatible with
         *
         * @see versions
         */
        @get:IgnoreForTaskInputs
        public var version: String
            get() = versions.get().single()
            set(value) = versions.set(listOf(value))

        /**
         * The list of versions that this mod is compatible with.
         */
        @get:Input
        public val versions: ListProperty<String> = listProperty()

        @IgnoreForTaskInputs
        override fun getName(): String = mod
    }

    /**
     * A person with optional contact info.
     */
    public class Person(
        @get:IgnoreForTaskInputs
        override val project: Project,
        /**
         * The name of the person.
         */
        @get:Input
        public val person: String,
    ) : HasProject, Named {
        /**
         * The contact information for the person
         *
         * @see
         *         [FabricMC Docs][https://wiki.fabricmc.net/documentation:fabric_mod_json_spec#contactinformation]
         */
        @get:Input
        public val contact: MapProperty<String, String> = mapProperty()

        @IgnoreForTaskInputs
        override fun getName(): String = person
    }

    /**
     * Contact information for a mod.
     */
    public class ModContact(
        @get:IgnoreForTaskInputs
        override val project: Project,
    ) : HasProject {
        /**
         * A link to the mod's homepage.
         *
         * Must be a valid `http` or `https` url.
         */
        @get:Input
        @get:Optional
        public val homepage: Property<String> = property()

        /**
         * A link to the mod's issues page.
         *
         * Must be a valid `http` or `https` url.
         */
        @get:Input
        @get:Optional
        public val issues: Property<String> = property<String>().convention(nyx.info.repository.projectIssues)

        /**
         * A link to the mod's source code.
         *
         * Must be a valid url, but can be a specialized url for (for example) git
         * or mercurial.
         */
        @get:Input
        @get:Optional
        public val source: Property<String> = property<String>().convention(nyx.info.repository.projectUrl)

        /**
         * An email address for the mod.
         *
         * Must be a valid email address.
         */
        @get:Input
        @get:Optional
        public val email: Property<String> = property()

        /**
         * A link to the mod's IRC channel.
         *
         * Must a valid irc url, eg. `irc://irc.esper.net:6667/charset` for
         * `#charset` at EsperNet. The port is optional, and assumed to be `6667`
         * if not present.
         */
        @get:Input
        @get:Optional
        public val irc: Property<String> = property()

        /**
         * Other contact info for a mod.
         *
         * Should be a valid url, although this is not mandatory.
         */
        @get:Input
        public val other: MapProperty<String, String> = mapProperty()
    }

    /**
     * An icon for a mod.
     */
    public data class ModIcon(
        /**
         * The filename for an icon that is included in the jar.
         */
        @get:Input
        public val file: String,

        /**
         * The size of the icon.
         *
         * This is optional, only if there is one icon. If there is more than one
         * icon, then the size must be specified for all icons.
         */
        @get:Input
        @get:Optional
        public val size: Int? = null,
    )

    public class ModMenu(
        override val project: Project,
    ) : HasProject {
        /**
         * A list of badges for this mod.
         *
         * Default supported badges:
         * - `library`
         * - `deprecated`
         */
        @get:Input
        public val badges: ListProperty<String> = listProperty()

        /**
         * A list of additional links for this mod that are shown in Mod Menu.
         */
        @get:Nested
        @get:Optional
        public val links: ModMenuLinks = ModMenuLinks(project)

        /**
         * The parent mod of this mod.
         */
        @get:Nested
        @get:Optional
        public val parent: ModMenuParent = ModMenuParent(project)

        /**
         * If this mod should be checked for updates.
         *
         * By default, update checking is enabled.
         */
        @get:Input
        @get:Optional
        public val updateChecker: Property<Boolean> = property()

        /**
         * Configures additional links for this mod.
         */
        public fun links(action: ModMenuLinks.() -> Unit) {
            links.action()
        }

        /**
         * Configures the parent mod of this mod.
         */
        public fun parent(action: ModMenuParent.() -> Unit) {
            parent.action()
        }

        /**
         * Adds the library badge to this mod.
         *
         * @see badges
         */
        public fun library(): Unit = badges.add("library")

        /**
         * Adds the deprecated badge to this mod.
         *
         * @see badges
         */
        public fun deprecated(): Unit = badges.add("deprecated")

        /**
         * The links associated with a mod that are displayed in Mod Menu.
         */
        public class ModMenuLinks(
            override val project: Project,
        ) : HasProject {
            /**
             * A Buy Me a Coffe link for this mod.
             */
            @get:Input
            @get:Optional
            public val buyMeACoffee: Property<String> = property()

            /**
             * A Coindrop link for this mod.
             */
            @get:Input
            @get:Optional
            public val coindrop: Property<String> = property()

            /**
             * A Crowdin link for this mod.
             */
            @get:Input
            @get:Optional
            public val crowdin: Property<String> = property()

            /**
             * A CurseForge link for this mod.
             */
            @get:Input
            @get:Optional
            public val curseforge: Property<String> = property()

            /**
             * A Discord link for this mod.
             */
            @get:Input
            @get:Optional
            public val discord: Property<String> = property()

            /**
             * A donation link for this mod.
             */
            @get:Input
            @get:Optional
            public val donate: Property<String> = property()

            /**
             * A Flattr link for this mod.
             */
            @get:Input
            @get:Optional
            public val flattr: Property<String> = property()

            /**
             * A GitHub Releases link for this mod.
             */
            @get:Input
            @get:Optional
            public val githubReleases: Property<String> = property()

            /**
             * A GitHub Sponsors link for this mod.
             */
            @get:Input
            @get:Optional
            public val githubSponsors: Property<String> = property()

            /**
             * A Ko-fi link for this mod.
             */
            @get:Input
            @get:Optional
            public val kofi: Property<String> = property()

            /**
             * A Liberapay link for this mod.
             */
            @get:Input
            @get:Optional
            public val liberapay: Property<String> = property()

            /**
             * A Mastodon link for this mod.
             */
            @get:Input
            @get:Optional
            public val mastodon: Property<String> = property()

            /**
             * A Modrinth link for this mod.
             */
            @get:Input
            @get:Optional
            public val modrinth: Property<String> = property()

            /**
             * An Open Collective link for this mod.
             */
            @get:Input
            @get:Optional
            public val openCollective: Property<String> = property()

            /**
             * A Patreon link for this mod.
             */
            @get:Input
            @get:Optional
            public val patreon: Property<String> = property()

            /**
             * A PayPal link for this mod.
             */
            @get:Input
            @get:Optional
            public val paypal: Property<String> = property()

            /**
             * A Reddit link for this mod.
             */
            @get:Input
            @get:Optional
            public val reddit: Property<String> = property()

            /**
             * A Twitch link for this mod.
             */
            @get:Input
            @get:Optional
            public val twitch: Property<String> = property()

            /**
             * A Twitter link for this mod.
             */
            @get:Input
            @get:Optional
            public val twitter: Property<String> = property()

            /**
             * A link to a wiki for this mod.
             */
            @get:Input
            @get:Optional
            public val wiki: Property<String> = property()

            /**
             * A YouTube link for this mod.
             */
            @get:Input
            @get:Optional
            public val youtube: Property<String> = property()

            /**
             * A map of any other links for this mod
             *
             * Note that the links listed above are all of those that are officially
             * supported by Mod Menu, so you must add additional translations for any
             * others specified.
             */
            @get:Input
            @get:Optional
            public val other: MapProperty<String, String> = mapProperty()
        }

        /**
         * A parent mod for Mod Menu
         *
         * If the parent mod is a real mod, then only [id] can be specified and
         * [name], [description], [icon], and [badges] must be omitted.
         */
        public class ModMenuParent(
            override val project: Project,
        ) : HasProject {
            /**
             * The id of the parent mod.
             */
            @get:Input
            @get:Optional
            public val id: Property<String> = property()

            /**
             * The name of the dummy parent mod
             *
             * This can only be specified if the mod is a dummy mod and does not exist.
             */
            @get:Input
            @get:Optional
            public val name: Property<String> = property()

            /**
             * The description of the dummy parent mod
             *
             * This can only be specified if the mod is a dummy mod and does not exist.
             */
            @get:Input
            @get:Optional
            public val description: Property<String> = property()

            /**
             * The icon(s) of the dummy parent mod
             *
             * This can only be specified if the mod is a dummy mod and does not exist.
             */
            @get:Input
            @get:Optional
            public val icon: Property<String> = property()

            /**
             * The badges of the dummy parent mod
             *
             * This can only be specified if the mod is a dummy mod and does not exist.
             */
            @get:Input
            public val badges: ListProperty<String> = listProperty()

            /**
             * Adds the library badge to the dummy parent mod.
             *
             * @see badges
             */
            public fun library(): Unit = badges.add("library")

            /**
             * Adds the deprecated badge to the dummy parent mod.
             *
             * @see badges
             */
            public fun deprecated(): Unit = badges.add("deprecated")
        }
    }
}
