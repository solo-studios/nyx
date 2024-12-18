/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxNeoGradleExtension.kt is part of nyx
 * Last modified on 18-12-2024 06:57 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.neoforge

import ca.solostudios.nyx.internal.util.neoMinecraft
import ca.solostudios.nyx.internal.util.neoMixins
import ca.solostudios.nyx.internal.util.neoRuns
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.plugin.minecraft.AbstractMinecraftExtension
import net.neoforged.gradle.dsl.common.extensions.AccessTransformers
import net.neoforged.gradle.dsl.common.extensions.Mappings
import net.neoforged.gradle.dsl.common.extensions.Minecraft
import net.neoforged.gradle.dsl.common.runtime.naming.NamingChannel
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property


public class NyxNeoGradleExtension(
    override val project: Project,
) : AbstractMinecraftExtension(project) {
    /**
     * The mod's identifier.
     *
     * @see Minecraft.getModIdentifier
     */
    public val modIdentifier: Property<String>
        get() = neoMinecraft.modIdentifier

    /**
     * The naming channels.
     *
     * @see Minecraft.getNamingChannels
     */
    public val namingChannels: NamedDomainObjectContainer<NamingChannel>
        get() = neoMinecraft.namingChannels

    /**
     * The mappings.
     *
     * @see Minecraft.getMappings
     */
    public val mappings: Mappings
        get() = neoMinecraft.mappings

    /**
     * The access transformers.
     *
     * @see Minecraft.getAccessTransformers
     */
    public val accessTransformers: AccessTransformers
        get() = neoMinecraft.accessTransformers

    /**
     * Configures the naming channels.
     *
     * @see Minecraft.namingChannels
     */
    public fun namingChannels(block: NamedDomainObjectContainer<NamingChannel>.() -> Unit) {
        neoMinecraft.namingChannels.apply(block)
    }

    /**
     * Configures a specific naming channel.
     *
     * @see Minecraft.namingChannel
     */
    public fun namingChannel(name: String, action: NamingChannel.() -> Unit) {
        neoMinecraft.namingChannel(name, action)
    }

    /**
     * Configures the mappings
     *
     * @see Minecraft.mappings
     */
    public fun mappings(block: Mappings.() -> Unit) {
        neoMinecraft.mappings(block)
    }

    /**
     * Configures the access transformers
     *
     * @see Minecraft.accessTransformers
     */
    public fun accessTransformers(block: AccessTransformers.() -> Unit) {
        neoMinecraft.accessTransformers(block)
    }

    override fun setDefaultMixinRefmapName(defaultName: String) {
        // NO-OP
    }

    override fun addMixinConfig(name: String) {
        neoMixins.config(name)
    }

    override fun configureProject() {
        neoMinecraft {
            this.accessTransformers
        }
        neoRuns.configureEach {
            jvmArguments.add("-Xmx${allocatedMemory.get()}G")
            jvmArguments.addAll(additionalJvmArgs)
            systemProperties.putAll(additionalJvmProperties)
        }
    }

    internal companion object {
        internal fun isLoaded(project: Project): Boolean {
            val nyx = project.nyx as ExtensionAware
            return nyx.extensions.findByName(NAME) is NyxNeoGradleExtension
        }

        internal fun isNotLoaded(project: Project): Boolean {
            return !isLoaded(project)
        }
    }
}
