/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GradlePropertyUtil.kt is part of nyx
 * Last modified on 15-09-2024 07:05 a.m.
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

import ca.solostudios.nyx.internal.HasObjectFactory
import org.gradle.api.DefaultTask
import org.gradle.api.DomainObjectSet
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.NamedDomainObjectList
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty

internal fun <T> Property<T>.convention(project: Project, provider: () -> T): Property<T> = convention(project.provider(provider))

// @formatter:off
internal inline fun <reified T> Project.property(): Property<T> = objects.property()
internal inline fun <reified T> HasObjectFactory.property(): Property<T> = objects.property()
internal inline fun <reified T> DefaultTask.property(): Property<T> = project.objects.property()

internal inline fun <reified T> Project.setProperty(): SetProperty<T> = objects.setProperty()
internal inline fun <reified T> HasObjectFactory.setProperty(): SetProperty<T> = objects.setProperty()
internal inline fun <reified T> DefaultTask.setProperty(): SetProperty<T> = project.objects.setProperty()

internal inline fun <reified T> Project.listProperty(): ListProperty<T> = objects.listProperty()
internal inline fun <reified T> HasObjectFactory.listProperty(): ListProperty<T> = objects.listProperty()
internal inline fun <reified T> DefaultTask.listProperty(): ListProperty<T> = project.objects.listProperty()

internal inline fun <reified K, reified V> Project.mapProperty(): MapProperty<K, V> = objects.mapProperty()
internal inline fun <reified K, reified V> HasObjectFactory.mapProperty(): MapProperty<K, V> = objects.mapProperty()
internal inline fun <reified K, reified V> DefaultTask.mapProperty(): MapProperty<K, V> = project.objects.mapProperty()

internal inline fun <reified T> Project.domainObjectContainer(): NamedDomainObjectContainer<T> = objects.domainObjectContainer(T::class.java)
internal inline fun <reified T> HasObjectFactory.domainObjectContainer(): NamedDomainObjectContainer<T> = objects.domainObjectContainer(T::class.java)
internal inline fun <reified T> DefaultTask.domainObjectContainer(): NamedDomainObjectContainer<T> = project.objects.domainObjectContainer(T::class.java)

internal inline fun <reified T> Project.domainObjectContainer(factory: NamedDomainObjectFactory<T>): NamedDomainObjectContainer<T> = objects.domainObjectContainer(T::class.java, factory)
internal inline fun <reified T> HasObjectFactory.domainObjectContainer(factory: NamedDomainObjectFactory<T>): NamedDomainObjectContainer<T> = objects.domainObjectContainer(T::class.java, factory)
internal inline fun <reified T> DefaultTask.domainObjectContainer(factory: NamedDomainObjectFactory<T>): NamedDomainObjectContainer<T> = project.objects.domainObjectContainer(T::class.java, factory)

internal inline fun <reified T> Project.domainObjectSet(): DomainObjectSet<T> = objects.domainObjectSet(T::class.java)
internal inline fun <reified T> HasObjectFactory.domainObjectSet(): DomainObjectSet<T> = objects.domainObjectSet(T::class.java)
internal inline fun <reified T> DefaultTask.domainObjectSet(): DomainObjectSet<T> = project.objects.domainObjectSet(T::class.java)

internal inline fun <reified T> Project.namedDomainObjectSet(): NamedDomainObjectSet<T> = objects.namedDomainObjectSet(T::class.java)
internal inline fun <reified T> HasObjectFactory.namedDomainObjectSet(): NamedDomainObjectSet<T> = objects.namedDomainObjectSet(T::class.java)
internal inline fun <reified T> DefaultTask.namedDomainObjectSet(): NamedDomainObjectSet<T> = project.objects.namedDomainObjectSet(T::class.java)

internal inline fun <reified T> Project.namedDomainObjectList(): NamedDomainObjectList<T> = objects.namedDomainObjectList(T::class.java)
internal inline fun <reified T> HasObjectFactory.namedDomainObjectList(): NamedDomainObjectList<T> = objects.namedDomainObjectList(T::class.java)
internal inline fun <reified T> DefaultTask.namedDomainObjectList(): NamedDomainObjectList<T> = project.objects.namedDomainObjectList(T::class.java)

internal fun Project.directoryProperty(): DirectoryProperty = objects.directoryProperty()
internal fun HasObjectFactory.directoryProperty(): DirectoryProperty = objects.directoryProperty()
internal fun DefaultTask.directoryProperty(): DirectoryProperty = project.objects.directoryProperty()

internal fun Project.fileProperty(): RegularFileProperty = objects.fileProperty()
internal fun HasObjectFactory.fileProperty(): RegularFileProperty = objects.fileProperty()
internal fun DefaultTask.fileProperty(): RegularFileProperty = project.objects.fileProperty()

internal fun Project.sourceDirectorySet(name: String, displayName: String): SourceDirectorySet = objects.sourceDirectorySet(name, displayName)
internal fun HasObjectFactory.sourceDirectorySet(name: String, displayName: String): SourceDirectorySet = objects.sourceDirectorySet(name, displayName)

internal fun DefaultTask.sourceDirectorySet(name: String, displayName: String): SourceDirectorySet = project.objects.sourceDirectorySet(name, displayName)

internal fun Project.fileCollection(): ConfigurableFileCollection = objects.fileCollection()
internal fun HasObjectFactory.fileCollection(): ConfigurableFileCollection = objects.fileCollection()
internal fun DefaultTask.fileCollection(): ConfigurableFileCollection = project.objects.fileCollection()

internal fun Project.fileTree(): ConfigurableFileTree = objects.fileTree()
internal fun HasObjectFactory.fileTree(): ConfigurableFileTree = objects.fileTree()
internal fun DefaultTask.fileTree(): ConfigurableFileTree = project.objects.fileTree()

internal fun Property<String>.orEmpty(): String = getOrElse("")

internal operator fun <T> ListProperty<in T>.plusAssign(element: T) = add(element)
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Iterable<T>) = addAll(elements)
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Array<T>) = addAll(*elements)
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Sequence<T>) = addAll(elements.toList())

@JvmName("plusAssignElement")
internal operator fun <T> ListProperty<in T>.plusAssign(element: Provider<T>) = add(element)
@JvmName("plusAssignIterable")
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Provider<Iterable<T>>) = addAll(elements)
@JvmName("plusAssignArray")
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Provider<Array<T>>) = addAll(elements.map { it.toList() })
@JvmName("plusAssignSequence")
internal operator fun <T> ListProperty<in T>.plusAssign(elements: Provider<Sequence<T>>) = addAll(elements.map { it.toList() })
// @formatter:on

internal val Property<Boolean>.isTrue: Boolean
    get() = isPresent && getOrElse(false)

internal val Property<Boolean>.isFalse: Boolean
    get() = isPresent && getOrElse(true)
