/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file SerialFabricModJson.kt is part of nyx
 * Last modified on 14-09-2024 11:35 p.m.
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

package ca.solostudios.nyx.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer


@Serializable
internal data class SerialFabricModJson(
    val id: String,
    val name: String? = null,
    val version: String,
    val provides: List<String>? = null,
    val environment: Environment? = null,
    val entrypoints: Map<String, List<Entrypoint>>? = null,
    val mixins: List<MixinConfig>? = null,
    val accessWidener: String? = null,
    val depends: Map<String, @Serializable(with = SingleListElementSerializer::class) List<String>>? = null,
    val recommends: Map<String, @Serializable(with = SingleListElementSerializer::class) List<String>>? = null,
    val suggests: Map<String, @Serializable(with = SingleListElementSerializer::class) List<String>>? = null,
    val conflicts: Map<String, @Serializable(with = SingleListElementSerializer::class) List<String>>? = null,
    val breaks: Map<String, @Serializable(with = SingleListElementSerializer::class) List<String>>? = null,
    val description: String? = null,
    val authors: List<Person>? = null,
    val contributors: List<Person>? = null,
    val contact: Map<String, String>? = null,
    @Serializable(with = SingleListElementSerializer::class)
    val license: List<String>? = null,
    val icon: ModIcon? = null,
    val languageAdapters: Map<String, String>? = null,
    val custom: JsonObject? = null,
) {
    val schemaVersion: Int = 1

    @Serializable
    enum class Environment {
        @SerialName("*")
        UNIVERSAL,

        @SerialName("client")
        CLIENT,

        @SerialName("server")
        SERVER
    }

    @Serializable
    sealed interface Entrypoint {
        @JvmInline
        @Serializable
        value class StringEntrypoint(
            val entrypoint: String,
        ) : Entrypoint

        @Serializable
        data class AdaptedEntrypoint(
            val adapter: String,
            val value: String,
        ) : Entrypoint
    }

    @Serializable
    sealed interface MixinConfig {
        @JvmInline
        @Serializable
        value class StringMixinConfig(
            val entrypoint: String,
        ) : MixinConfig

        @Serializable
        data class EnvironmentMixinConfig(
            val config: String,
            val environment: Environment? = null,
        ) : MixinConfig
    }

    @Serializable
    sealed interface Person {
        @JvmInline
        @Serializable
        value class NamedPerson(
            val name: String,
        ) : Person

        @Serializable
        data class ContactablePerson(
            val name: String,
            val contact: Map<String, String>,
        ) : Person
    }

    @Serializable
    sealed interface ModIcon {
        @JvmInline
        @Serializable
        value class StringModIcon(
            val icon: String,
        ) : ModIcon

        @JvmInline
        @Serializable
        value class ModIconMap(
            val icons: Map<Int, String>,
        ) : ModIcon
    }

    internal object PersonSerializer : JsonContentPolymorphicSerializer<Person>(Person::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Person> = when (element) {
            is JsonObject -> Person.ContactablePerson.serializer()
            is JsonPrimitive -> Person.NamedPerson.serializer()
            else -> error("Bad input for person")
        }
    }

    internal object SingleListElementSerializer : JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
        override fun transformDeserialize(element: JsonElement): JsonElement {
            return if (element !is JsonArray) JsonArray(listOf(element)) else element
        }

        override fun transformSerialize(element: JsonElement): JsonElement {
            return if (element is JsonArray && element.size == 1) element[0] else element
        }
    }
}
