/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file SonatypePublishPlugin.kt is part of nyx
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

import ca.solostudios.nyx.sonatype.publishing.PublishToSonatypeMavenRepository
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.credentials.Credentials
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.provider.MissingValueException
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.get
import org.gradle.kotlin.dsl.withType
import org.gradle.util.GradleVersion

public open class SonatypePublishPlugin : Plugin<Project> {
    public override fun apply(project: Project) {
        project.pluginManager.apply(PublishingPlugin::class.java)
        project.pluginManager.apply(MavenPublishPlugin::class.java)

        // Configure after PublishingPlugin and MavenPublishPlugin
        project.extensions.configure<PublishingExtension> {
            realizePublishingTasksLater(project, this)
        }
    }

    private fun realizePublishingTasksLater(project: Project, extension: PublishingExtension) {
        val mavenPublications = extension.publications.withType(MavenPublicationInternal::class)
        val tasks = project.tasks

        val publishLifecycleTask = tasks.named(PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME)
        val centralRepository = extension.repositories.maven {
            name = SONATYPE_REPOSITORY_NAME
            url = project.uri(SONATYPE_MAVEN_DEPLOYMENTS_URL)
            credentials(PasswordCredentials::class)
        }

        tasks.register(PUBLISH_ALL_PUBLICATIONS_TASK) {
            description = "Publishes all Maven publications produced by this project to the Sonatype Central Maven repository."
            group = PublishingPlugin.PUBLISH_TASK_GROUP
        }

        // disable default 'all' publish task
        tasks.named(PUBLISH_ALL_MAVEN_PUBLICATIONS_TASK) {
            enabled = false
            group = DISABLED_TASK_GROUP
        }
        project.gradle.startParameter.excludedTaskNames.add(PUBLISH_ALL_MAVEN_PUBLICATIONS_TASK)

        mavenPublications.all {
            createPublishTasksForSonatypeRepo(project, tasks, publishLifecycleTask, this, centralRepository)
        }
    }

    private fun createPublishTasksForSonatypeRepo(
        project: Project,
        tasks: TaskContainer,
        publishLifecycleTask: TaskProvider<Task>,
        internalPublication: MavenPublicationInternal,
        repository: MavenArtifactRepository,
    ) {
        val publicationName = internalPublication.name
        val publishTask = "publish" + StringUtils.capitalize(publicationName) + "PublicationToSonatypeRepository"
        val mavenPublishTask = "publish" + StringUtils.capitalize(publicationName) + "PublicationTo${SONATYPE_REPOSITORY_NAME}Repository"

        tasks.register<PublishToSonatypeMavenRepository>(publishTask) {
            publication = internalPublication
            setRepository(repository)
            group = PublishingPlugin.PUBLISH_TASK_GROUP
            description = "Publishes Maven publication '$publicationName' to Sonatype Central Maven repository."

            val task = this
            project.gradle.taskGraph.whenReady {
                if (hasTask(task))
                    validateCredentialsSetup(project, task)
            }
        }

        publishLifecycleTask.configure {
            dependsOn(publishTask)
        }
        tasks.named(PUBLISH_ALL_PUBLICATIONS_TASK) {
            dependsOn(publishTask)
        }


        // disable default publish task
        tasks.named(mavenPublishTask) {
            enabled = false
            group = DISABLED_TASK_GROUP
        }
        project.gradle.startParameter.excludedTaskNames.add(mavenPublishTask)
    }

    public companion object {
        public const val SONATYPE_REPOSITORY_NAME: String = "SonatypeCentral"
        public const val SONATYPE_API_URL: String = "https://central.sonatype.com/api/v1"
        public const val SONATYPE_API_UPLOAD_URL: String = "$SONATYPE_API_URL/publisher/upload"
        public const val SONATYPE_MAVEN_DEPLOYMENTS_URL: String = "$SONATYPE_API_URL/publisher/deployments/download/"

        public const val PUBLISH_ALL_PUBLICATIONS_TASK: String = "publishAllPublicationsToSonatypeRepository"
        public const val PUBLISH_ALL_MAVEN_PUBLICATIONS_TASK: String = "publishAllPublicationsTo${SONATYPE_REPOSITORY_NAME}Repository"

        public const val DISABLED_TASK_GROUP: String = "disabled"

        public val USER_AGENT: String = "ca.solo-studios.nyx.sonatype.SonatypePublish Gradle/%s (%s;%s;%s) (%s;%s;%s)".format(
            GradleVersion.current().version,
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"),
            System.getProperty("java.vendor"),
            System.getProperty("java.version"),
            System.getProperty("java.vm.version")
        )


        private fun validateCredentialsSetup(project: Project, publishToMavenRepository: PublishToSonatypeMavenRepository) {
            val repository = publishToMavenRepository.getRepository() as DefaultMavenArtifactRepository
            val creds = try {
                repository.configuredCredentials.getOrNull()
            } catch (e: Exception) {
                // In case of exception, we assume compatibility as this will fail later as well
                null
            }
            if (creds != null && !isUsingCredentialsProvider(project as ProjectInternal, repository.name, creds)) {
                publishToMavenRepository.notCompatibleWithConfigurationCache("Publishing to a repository without a credentials provider is not yet supported for the configuration cache")
            }
        }

        private fun isUsingCredentialsProvider(project: ProjectInternal, identity: String, toCheck: Credentials): Boolean {
            val providerFactory = project.services.get<ProviderFactory>()

            val referenceCredentials = try {
                val credentialsProvider = try {
                    providerFactory.credentials(toCheck.javaClass, identity)
                } catch (e: IllegalArgumentException) {
                    // some possibilities are invalid repository names and invalid credential types
                    // either way, this is not the place to validate that
                    return false
                }

                credentialsProvider.get()
            } catch (e: MissingValueException) {
                return false
            }
            return EqualsBuilder.reflectionEquals(toCheck, referenceCredentials)
        }
    }
}

