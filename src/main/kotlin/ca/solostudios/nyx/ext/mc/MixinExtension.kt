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

public class MixinExtension(override val project: Project) : ConfiguresProject, HasProject {
    public val hotswapMixins: Property<Boolean> = property<Boolean>().convention(true)
    public val debug: Property<Boolean> = property<Boolean>().convention(true)
    public val verbose: Property<Boolean> = property<Boolean>().convention(false)
    public val dumpTargetOnFailure: Property<Boolean> = property<Boolean>().convention(true)
    public val checks: Property<Boolean> = property<Boolean>().convention(false)
    public val verify: Property<Boolean> = property<Boolean>().convention(false)

    /**
     * Sets the mixin default refmap name to `mixins/`[name]`/refmap.json`.
     *
     * @param name The directory used for the refmap, defaults to `project.name`.
     */
    @Suppress("UnstableApiUsage")
    public fun mixinRefmapName(name: String = project.name) {
        loom {
            mixin {
                defaultRefmapName = "mixins/$name/refmap.json"
            }
        }
    }

    override fun onLoad() {}

    override fun configureProject() {
        loom {
            runs {
                configureEach {
                    if (debug.isPresent)
                        this.property("mixin.debug", debug.get().toString())
                    if (verbose.isPresent)
                        this.property("mixin.debug.verbose", verbose.get().toString())
                    if (dumpTargetOnFailure.isPresent)
                        this.property("mixin.dumpTargetOnFailure", dumpTargetOnFailure.get().toString())
                    // makes silent failures into hard-failures
                    if (checks.isPresent)
                        this.property("mixin.checks", checks.get().toString())
                    if (verify.isPresent)
                        this.property("mixin.debug.verify", verify.get().toString())


                    if (hotswapMixins.isTrue) {
                        this.property("mixin.hotSwap", "true")

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
