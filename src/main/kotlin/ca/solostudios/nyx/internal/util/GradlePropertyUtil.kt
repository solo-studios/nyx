/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradlePropertyUtil.kt is part of nyx
 * Last modified on 19-06-2024 05:12 p.m.
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

package ca.solostudios.nyx.internal.util

import ca.solostudios.nyx.internal.InternalNyxExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

internal fun <T> Property<T>.convention(project: Project, provider: () -> T): Property<T> = convention(project.provider(provider))

internal inline fun <reified T> Project.property(): Property<T> = objects.property()
internal inline fun <reified T> InternalNyxExtension.property(): Property<T> = project.objects.property()
internal inline fun <reified T> DefaultTask.property(): Property<T> = project.objects.property()

internal inline fun <reified T> Project.setProperty(): SetProperty<T> = objects.setProperty()
internal inline fun <reified T> InternalNyxExtension.setProperty(): SetProperty<T> = project.objects.setProperty()
internal inline fun <reified T> DefaultTask.setProperty(): SetProperty<T> = project.objects.setProperty()

internal inline fun <reified T> Project.listProperty(): ListProperty<T> = objects.listProperty()
internal inline fun <reified T> InternalNyxExtension.listProperty(): ListProperty<T> = project.objects.listProperty()
internal inline fun <reified T> DefaultTask.listProperty(): ListProperty<T> = project.objects.listProperty()

internal inline fun <reified K, reified V> Project.mapProperty(): MapProperty<K, V> = objects.mapProperty()
internal inline fun <reified K, reified V> InternalNyxExtension.mapProperty(): MapProperty<K, V> = project.objects.mapProperty()
internal inline fun <reified K, reified V> DefaultTask.mapProperty(): MapProperty<K, V> = project.objects.mapProperty()

internal fun Project.directoryProperty(): DirectoryProperty = objects.directoryProperty()
internal fun InternalNyxExtension.directoryProperty(): DirectoryProperty = project.objects.directoryProperty()
internal fun DefaultTask.directoryProperty(): DirectoryProperty = project.objects.directoryProperty()

internal fun Project.fileProperty(): RegularFileProperty = objects.fileProperty()
internal fun InternalNyxExtension.fileProperty(): RegularFileProperty = project.objects.fileProperty()
internal fun DefaultTask.fileProperty(): RegularFileProperty = project.objects.fileProperty()

internal fun Project.fileCollection(): ConfigurableFileCollection = objects.fileCollection()
internal fun InternalNyxExtension.fileCollection(): ConfigurableFileCollection = project.objects.fileCollection()
internal fun DefaultTask.fileCollection(): ConfigurableFileCollection = project.objects.fileCollection()

internal fun Project.fileTree(): ConfigurableFileTree = objects.fileTree()
internal fun InternalNyxExtension.fileTree(): ConfigurableFileTree = project.objects.fileTree()
internal fun DefaultTask.fileTree(): ConfigurableFileTree = project.objects.fileTree()

internal fun Property<String>.orEmpty(): String = getOrElse("")

internal val Property<Boolean>.isTrue: Boolean
    get() = isPresent && getOrElse(false)

internal val Property<Boolean>.isFalse: Boolean
    get() = isPresent && getOrElse(true)
