/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file SerializationUtil.kt is part of nyx
 * Last modified on 15-09-2024 06:55 a.m.
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

@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)

package ca.solostudios.nyx.internal.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializerOrNull


internal fun Any?.toJsonElement(): JsonElement {
    val serializer = this?.let { Json.serializersModule.serializerOrNull(this::class.java) }

    return when {
        this == null -> JsonNull
        serializer != null -> Json.encodeToJsonElement(serializer, this)
        this is Map<*, *> -> toJsonElement()
        this is Array<*> -> toJsonElement()
        this is BooleanArray -> toJsonElement()
        this is ByteArray -> toJsonElement()
        this is CharArray -> toJsonElement()
        this is ShortArray -> toJsonElement()
        this is IntArray -> toJsonElement()
        this is LongArray -> toJsonElement()
        this is FloatArray -> toJsonElement()
        this is DoubleArray -> toJsonElement()
        this is UByteArray -> toJsonElement()
        this is UShortArray -> toJsonElement()
        this is UIntArray -> toJsonElement()
        this is ULongArray -> toJsonElement()
        this is Collection<*> -> toJsonElement()
        this is Boolean -> JsonPrimitive(this)
        this is Number -> JsonPrimitive(this)
        this is String -> JsonPrimitive(this)
        this is Enum<*> -> JsonPrimitive(this.name)
        this is Pair<*, *> -> this.toList().toJsonElement()
        this is Triple<*, *, *> -> this.toList().toJsonElement()
        else -> error("Can't serialize '$this' as it is of an unknown type")
    }
}

internal fun Map<*, *>.toJsonElement(): JsonObject {
    return buildJsonObject {
        forEach { (key, value) ->
            if (key !is String)
                error("Only string keys are supported for maps")

            put(key, value.toJsonElement())
        }
    }
}

internal fun Collection<*>.toJsonElement(): JsonArray = buildJsonArray {
    forEach { element ->
        add(element.toJsonElement())
    }
}

internal fun Array<*>.toJsonElement(): JsonArray = buildJsonArray {
    forEach { element ->
        add(element.toJsonElement())
    }
}

internal fun BooleanArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun ByteArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun CharArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it.code)) } }
internal fun ShortArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun IntArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun LongArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun FloatArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun DoubleArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }

internal fun UByteArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun UShortArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun UIntArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
internal fun ULongArray.toJsonElement(): JsonArray = buildJsonArray { forEach { add(JsonPrimitive(it)) } }
