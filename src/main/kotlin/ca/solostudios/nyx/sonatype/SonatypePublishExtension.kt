package ca.solostudios.nyx.sonatype

import ca.solostudios.nyx.api.HasProject
import ca.solostudios.nyx.util.create
import ca.solostudios.nyx.util.property
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
