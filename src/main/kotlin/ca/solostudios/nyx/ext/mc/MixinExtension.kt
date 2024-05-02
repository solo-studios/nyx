package ca.solostudios.nyx.ext.mc

import ca.solostudios.nyx.api.ConfiguresProject
import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.configurations
import ca.solostudios.nyx.util.isTrue
import ca.solostudios.nyx.util.loom
import ca.solostudios.nyx.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.annotations.ApiStatus

public class MixinExtension(override val project: Project) : ConfiguresProject, HasProject {
    public val hotswapMixins: Property<Boolean> = property<Boolean>().convention(true)
    public val debug: Property<Boolean> = property<Boolean>().convention(true)
    public val verbose: Property<Boolean> = property<Boolean>().convention(false)
    public val dumpTargetOnFailure: Property<Boolean> = property<Boolean>().convention(true)
    public val checks: Property<Boolean> = property<Boolean>().convention(false)

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

    override fun configureProject() {
        loom {
            runs {
                configureEach {
                    if (debug.isTrue)
                        property("mixin.debug", "true")
                    if (verbose.isTrue)
                        property("mixin.debug.verbose", "true")
                    if (dumpTargetOnFailure.isTrue)
                        property("mixin.dumpTargetOnFailure", "true")
                    // makes silent failures into hard-failures
                    if (checks.isTrue)
                        property("mixin.checks", "true")


                    if (hotswapMixins.isTrue) {
                        property("mixin.hotSwap", "true")

                        val compileClasspath by configurations.named("compileClasspath")

                        val mixinJarFile = compileClasspath.files {
                            it.group == "net.fabricmc" && it.name == "sponge-mixin"
                        }.firstOrNull()
                        if (mixinJarFile != null)
                            vmArg("-javaagent:$mixinJarFile")
                    }
                }
            }
        }

    }
}
