/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxProjectInfoExtension.kt is part of nyx
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

package ca.solostudios.nyx.project

import ca.solostudios.nyx.internal.InternalNyxExtension
import ca.solostudios.nyx.internal.util.formatAsName
import ca.solostudios.nyx.internal.util.listProperty
import ca.solostudios.nyx.internal.util.property
import ca.solostudios.nyx.internal.util.provider
import ca.solostudios.nyx.internal.util.tasks
import ca.solostudios.nyx.internal.util.toStringOrEmpty
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType

public class NyxProjectInfoExtension(override val project: Project) : InternalNyxExtension {
    /**
     * The name of the project to be used when publishing.
     *
     * Defaults to the `project.name`, formatted in "Title Case" eg.
     * - `my-project` -> "My Project"
     * - `some-project-name` -> "Some Project Name"
     *
     * Note:
     * - `-kt` is replaced with "Kotlin"
     */
    public val name: Property<String> = property<String>().convention(provider { project.name.formatAsName() })

    /**
     * The group id used when publishing.
     *
     * Defaults to `project.group`.
     */
    public var group: String
        get() = project.group.toStringOrEmpty()
        set(value) {
            project.group = value
        }

    /**
     * The module name used when publishing.
     *
     * This is used for the artifact coordinates once it is published.
     *
     * Defaults to `project.name`
     */
    public val module: Property<String> = property<String>().convention(provider { project.name })

    /**
     * The version used when publishing.
     *
     * Defaults to `project.version`.
     */
    public var version: String
        get() = project.version.toStringOrEmpty()
        set(value) {
            project.version = value
        }

    /**
     * The description used when publishing.
     *
     * Defaults to `project.description`.
     */
    public var description: String
        get() = project.description.toStringOrEmpty()
        set(value) {
            project.description = value
        }

    /**
     * The url of the organization used when publishing.
     */
    public val organizationUrl: Property<String> = property()

    /**
     * The name of the organization used when publishing.
     */
    public val organizationName: Property<String> = property()

    /**
     * The list of developers used when publishing.
     */
    public val developers: ListProperty<(MavenPomDeveloper) -> Unit> = listProperty()

    /**
     * Information about the repository used when publishing.
     */
    @Nested
    public val repository: NyxRepositoryInfo = NyxRepositoryInfo(project)

    /**
     * Information about the license used when publishing.
     */
    @Nested
    public val license: NyxLicenseInfoExtension = NyxLicenseInfoExtension(project)

    /**
     * Adds a developer to the publishing information
     *
     * @param developer Configuration for the developer that is added
     * @receiver The developer to be added
     */
    public fun developer(developer: (MavenPomDeveloper).() -> Unit) {
        this.developers.add(developer)
    }

    /**
     * Configures the repository info.
     */
    public fun repository(action: (NyxRepositoryInfo).() -> Unit) {
        repository.apply(action)
    }

    /**
     * Configures the license info.
     */
    public fun license(action: (NyxLicenseInfoExtension).() -> Unit) {
        license.apply(action)
    }

    override fun configureProject() {
        tasks {
            withType<AbstractArchiveTask>().configureEach {
                archiveBaseName = project.name
            }
        }
    }
}
