package ca.solostudios.nyx.util

import ca.solostudios.nyx.api.HasProject
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

internal fun <T> Property<T>.convention(project: Project, provider: () -> T): Property<T> = convention(project.provider(provider))

internal inline fun <reified T> HasProject.property(): Property<T> = project.objects.property()
internal inline fun <reified T> DefaultTask.property(): Property<T> = project.objects.property()

internal inline fun <reified T> HasProject.setProperty(): SetProperty<T> = project.objects.setProperty()

internal inline fun <reified T> HasProject.listProperty(): ListProperty<T> = project.objects.listProperty()

internal inline fun <reified K, reified V> HasProject.mapProperty(): MapProperty<K, V> = project.objects.mapProperty()

internal fun Property<String>.orEmpty(): String = getOrElse("")

internal val Property<Boolean>.isTrue: Boolean
    get() = isPresent && getOrElse(false)

internal val Property<Boolean>.isFalse: Boolean
    get() = isPresent && getOrElse(true)
