/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file FabricModJson.kt is part of nyx
 * Last modified on 15-09-2024 07:58 a.m.
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
import ca.solostudios.nyx.internal.InjectedObjectFactory
import ca.solostudios.nyx.internal.util.domainObjectContainer
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.mapProperty
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested

/**
 * An extension used for generating a `fabric.mod.json` file according to
 * the spec.
 */
public class FabricModJson(override val project: Project) : HasProject {
    /**
     * The id of the mod.
     */
    public val id: Property<String> = property<String>().convention(nyx.info.module)

    /**
     * The human-readable name of the mod.
     */
    public val name: Property<String> = property<String>().convention(nyx.info.name)

    /**
     * The version of the mod.
     */
    public val version: Property<String> = property<String>().convention(nyx.info.version)

    /**
     * The description of this mod.
     */
    public val description: Property<String> = property<String>().convention(nyx.info.description)

    /**
     * A list of other mods that this mod provides.
     */
    public val provides: ListProperty<String> = listProperty()

    /**
     * Which environment type this mod is compatible with. The mod is only
     * loaded in the appropriate environment type.
     */
    public val environment: Property<Environment> = property()

    /**
     * The entrypoints to this mod.
     */
    public val entrypoints: NamedDomainObjectContainer<EntrypointContainer> = domainObjectContainer { target ->
        EntrypointContainer(project, target)
    }

    /**
     * A list of people who authored this mod.
     */
    public val authors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * A list of people who contributed to this mod.
     */
    public val contributors: NamedDomainObjectContainer<Person> = domainObjectContainer { name -> Person(project, name) }

    /**
     * The mixin configs for this mod.
     */
    public val mixins: NamedDomainObjectContainer<MixinConfig> = domainObjectContainer()

    /**
     * The access widener used for this mod.
     */
    public val accessWidener: Property<String> = property()

    /**
     * A list of mods that this mod depends on.
     */
    public val depends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod recommends.
     */
    public val recommends: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod suggests.
     */
    public val suggests: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod conflicts with.
     *
     * A conflicting mod does not cause the game launch to fail, but instead
     * logs a warning at startup.
     */
    public val conflicts: NamedDomainObjectContainer<Dependency> = domainObjectContainer { mod -> Dependency(project, mod) }

    /**
     * A list of mods that this mod breaks.
     *
     * A breaking mod will cause the game to fail to launch.
     */
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
    public val licenses: ListProperty<String> = listProperty<String>().convention(nyx.info.license.name.map { listOf(it) })

    /**
     * A list of icons for this mod.
     */
    public val icons: ListProperty<ModIcon> = listProperty()

    /**
     * A list of language adapters that this mod provides.
     */
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
    public val custom: MapProperty<String, Any> = mapProperty()


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
        override val project: Project,
        /**
         * The name of the target entrypoint.
         */
        public val target: String,
    ) : HasProject, Named {
        /**
         * A list of entrypoints.
         */
        public val entrypoints: ListProperty<Entrypoint> = listProperty()
        override fun getName(): String = target
    }

    /**
     * An entrypoint reference to be loaded.
     */
    public abstract class Entrypoint : InjectedObjectFactory() {
        /**
         * The value of the entrypoint.
         */
        public val value: Property<String> = property()

        /**
         * The entrypoint adapter.
         *
         * Optional.
         */
        public val adapter: Property<String> = property()
    }

    public class MixinConfig(
        override val project: Project,
        public val entrypoint: String,
    ) : HasProject, Named {
        public val environment: Property<Environment> = property()
        override fun getName(): String = entrypoint
    }

    public class Dependency(
        override val project: Project,
        public val mod: String,
    ) : HasProject, Named {
        public var version: String
            get() = versions.get().single()
            set(value) = versions.set(listOf(value))

        public val versions: ListProperty<String> = listProperty()
        override fun getName(): String = mod
    }

    public class Person(
        override val project: Project,
        public val person: String,
    ) : HasProject, Named {
        public val contact: MapProperty<String, String> = mapProperty()
        override fun getName(): String = person
    }

    public class ModContact(
        override val project: Project,
    ) : HasProject {
        public val homepage: Property<String> = property()
        public val issues: Property<String> = property<String>().convention(nyx.info.repository.projectIssues)
        public val source: Property<String> = property<String>().convention(nyx.info.repository.projectUrl)
        public val email: Property<String> = property()
        public val irc: Property<String> = property()
        public val other: MapProperty<String, String> = mapProperty()
    }

    public data class ModIcon(
        public val file: String,
        public val size: Int? = null,
    )

    public class ModMenu(
        override val project: Project,
    ) : HasProject {
        public val badges: ListProperty<String> = listProperty()

        @Nested
        public val links: ModMenuLinks = ModMenuLinks(project)

        @Nested
        public val parent: ModMenuParent = ModMenuParent(project)

        public val updateChecker: Property<Boolean> = property()

        public fun library(): Unit = badges.add("library")
        public fun deprecated(): Unit = badges.add("deprecated")

        public class ModMenuLinks(
            override val project: Project,
        ) : HasProject {
            public val buyMeACoffee: Property<String> = property()
            public val coindrop: Property<String> = property()
            public val crowdin: Property<String> = property()
            public val curseforge: Property<String> = property()
            public val discord: Property<String> = property()
            public val donate: Property<String> = property()
            public val flattr: Property<String> = property()
            public val githubReleases: Property<String> = property()
            public val githubSponsors: Property<String> = property()
            public val kofi: Property<String> = property()
            public val liberapay: Property<String> = property()
            public val mastodon: Property<String> = property()
            public val modrinth: Property<String> = property()
            public val openCollective: Property<String> = property()
            public val patreon: Property<String> = property()
            public val paypal: Property<String> = property()
            public val reddit: Property<String> = property()
            public val twitch: Property<String> = property()
            public val twitter: Property<String> = property()
            public val wiki: Property<String> = property()
            public val youtube: Property<String> = property()
            public val other: MapProperty<String, String> = mapProperty()
        }

        public class ModMenuParent(
            override val project: Project,
        ) : HasProject {
            public val id: Property<String> = property()

            // ommit if real mod
            public val name: Property<String> = property()
            public val description: Property<String> = property()
            public val icon: Property<String> = property()
            public val badges: ListProperty<String> = listProperty()

            public fun library(): Unit = badges.add("library")
            public fun deprecated(): Unit = badges.add("deprecated")
        }
    }
}
