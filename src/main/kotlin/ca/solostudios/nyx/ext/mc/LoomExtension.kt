package ca.solostudios.nyx.ext.mc

import ca.solostudios.nyx.util.loom
import ca.solostudios.nyx.util.property
import ca.solostudios.nyx.util.sourceSets
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.plugin.HasProject

public class LoomExtension(override val project: Project) : HasProject {
    public val accessWidenerName: Property<String> = property()
    public fun accessWidener(name: String) {
        loom {
            // src/main/resources/$name.accesswidener
            accessWidenerPath = sourceSets["main"].resources.srcDirs.map { it.resolve("$name.accesswidener") }
                .first { it.exists() }

            mixin {

            }
        }
    }
}
