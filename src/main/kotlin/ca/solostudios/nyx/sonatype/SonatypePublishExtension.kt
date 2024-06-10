/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file SonatypePublishExtension.kt is part of nyx
 * Last modified on 10-06-2024 03:21 p.m.
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

package ca.solostudios.nyx.sonatype

import ca.solostudios.nyx.internal.HasProject
import ca.solostudios.nyx.internal.util.create
import ca.solostudios.nyx.internal.util.property
import org.gradle.api.Project
import org.gradle.api.provider.Property

public open class SonatypePublishExtension(override val project: Project) : HasProject {
    /**
     * The publishing type to use when uploading the artifact.
     *
     * By default, this is set to `USER_MANAGED`.
     * This means that you are required to manually log in to Sonatype Central to approve all publications.
     */
    public val publishingType: Property<PublishingType> = property<PublishingType>().convention(PublishingType.USER_MANAGED)

    /**
     * The username used to authenticate to sonatype.
     *
     * If unset, it will default to the username provided via the `PasswordCredentials` for the `SonatypeCentral` repository.
     *
     * You can set the password globally by adding
     * ```properties
     * SonatypeCentralUsername=[username]
     * SonatypeCentralPassword=[password]
     * ```
     * in [the `gradle.properties` located in your `$GRADLE_USER_HOME`](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables).
     * By default, this is located at `~/.gradle/gradle.properties` on Linux/MacOS,
     * and `%USERPROFILE%\.gradle\gradle.properties` on Windows.
     *
     * It is preferred to use that method over setting this property.
     */
    public val username: Property<String> = property()

    /**
     * The password used to authenticate to sonatype.
     *
     * If unset, it will default to the password provided via the `PasswordCredentials` for the `SonatypeCentral` repository.
     *
     * You can set the password globally by adding
     * ```properties
     * SonatypeCentralUsername=[username]
     * SonatypeCentralPassword=[password]
     * ```
     * in [the `gradle.properties` located in your `$GRADLE_USER_HOME`](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_environment_variables).
     * By default, this is located at `~/.gradle/gradle.properties` on Linux/MacOS,
     * and `%USERPROFILE%\.gradle\gradle.properties` on Windows.
     *
     * It is preferred to use that method over setting this property.
     */
    public val password: Property<String> = property()

    public companion object {
        public const val NAME: String = "sonatype"

        public fun create(project: Project): SonatypePublishExtension = project.create<SonatypePublishExtension>(NAME, project)
    }
}

/**
 * Whether to have the deployment stop in the `VALIDATED` state
 * and require a user to log in and manually approve its progression,
 * or to automatically go directly to `PUBLISHING` when validation has passed.
 *
 * See: [https://central.sonatype.com/api-doc](https://central.sonatype.com/api-doc)
 */
public enum class PublishingType {
    /**
     * Will automatically publish a publication once it has been validated.
     */
    AUTOMATIC,

    /**
     * Requires you to log in to manually approve all publications.
     */
    USER_MANAGED
}
