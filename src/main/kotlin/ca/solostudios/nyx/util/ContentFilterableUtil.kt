/*
 * Copyright (c) 2024-2025 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file ContentFilterableUtil.kt is part of nyx
 * Last modified on 04-01-2025 11:18 p.m.
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

package ca.solostudios.nyx.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.gradle.api.file.ContentFilterable
import org.gradle.kotlin.dsl.filter
import java.io.FilterReader
import java.io.IOException
import java.io.Reader
import kotlin.math.max
import kotlin.math.min

/**
 * Minifies json in each file as it is copied.
 *
 * Should only be used inside of a `filesMatching` block, otherwise it will
 * attempt to minify even non-json files.
 *
 * For example (remove the spaces in the patterns):
 * ```kotlin
 * processResources {
 *     filesMatching(listOf("** / *.json", "** / *.mcmeta")) {
 *         minifyJson(lenient = true)
 *     }
 * }
 * ```
 */
@JvmOverloads
public fun ContentFilterable.minifyJson(lenient: Boolean = false) {
    filter<JsonMinifyFilterReader>(mapOf("lenient" to lenient))
}

/**
 * Minifies json in each file as it is copied.
 *
 * Should only be used inside of a `filesMatching` block, otherwise it will
 * attempt to minify even non-json files.
 *
 * For example (remove the spaces in the patterns):
 * ```kotlin
 * processResources {
 *     filesMatching(listOf("** / *.json", "** / *.mcmeta")) {
 *         minifyJson(lenient = true)
 *     }
 * }
 * ```
 */
public fun ContentFilterable.minifyJson(properties: Map<String, Any?>) {
    filter<JsonMinifyFilterReader>(properties)
}

internal class JsonMinifyFilterReader(reader: Reader) : FilterReader(reader) {
    var lenient = false
    private val minifiedJson by lazy { minifyJson(reader.buffered()) }
    private var position = 0
    private var mark = 0
    private var closed = false
    private val length: Int
        get() = minifiedJson.length

    @OptIn(ExperimentalSerializationApi::class)
    private fun minifyJson(reader: Reader): String {
        val text = reader.readText()
        return try {
            when {
                lenient -> STRICT_JSON.encodeToString(LENIENT_JSON.decodeFromString<JsonElement>(text))
                else    -> STRICT_JSON.encodeToString(STRICT_JSON.decodeFromString<JsonElement>(text))
            }
        } catch (e: SerializationException) {
            text
        }
    }

    @Throws(IOException::class)
    override fun read(): Int {
        synchronized(lock) {
            ensureOpen()
            if (position >= length)
                return EOF

            return minifiedJson[position++].code
        }
    }

    @Throws(IOException::class)
    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        synchronized(lock) {
            ensureOpen()

            if (len == 0)
                return 0

            if (position >= length)
                return EOF

            val count = min(len, (length - position))
            minifiedJson.toCharArray(cbuf, off, position, position + count)
            position += count
            return count
        }
    }

    @Throws(IOException::class)
    override fun skip(count: Long): Long {
        synchronized(lock) {
            ensureOpen()

            if (position >= length)
                return 0

            // Bound skip by beginning and end of the source
            var skip = min((length - position).toLong(), count)
            skip = max(-position.toDouble(), skip.toDouble()).toLong()
            position += skip.toInt()
            return skip
        }
    }

    override fun ready(): Boolean {
        synchronized(lock) {
            ensureOpen()
            return true
        }
    }

    override fun markSupported(): Boolean = true

    override fun mark(readAheadLimit: Int) {
        synchronized(lock) {
            ensureOpen()
            require(readAheadLimit >= 0) { "Read-ahead limit < 0" }
            mark = position
        }
    }

    override fun reset() {
        synchronized(lock) {
            ensureOpen()
            position = mark
        }
    }

    override fun close() {
        closed = true
    }

    private fun ensureOpen() {
        if (closed)
            throw IOException("Stream closed")
    }

    companion object {
        const val EOF = -1

        @ExperimentalSerializationApi
        val LENIENT_JSON = Json {
            allowComments = true
            allowTrailingComma = true
            isLenient = true
            prettyPrint = false
        }

        @ExperimentalSerializationApi
        val STRICT_JSON = Json {
            prettyPrint = false
        }
    }
}
