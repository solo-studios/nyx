/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file TempFiles.kt is part of nyx
 * Last modified on 25-10-2024 07:03 p.m.
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

@file:Suppress("unused")

package ca.solostudios.nyx.kotest

import io.kotest.core.spec.Spec
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteExisting

const val TMP_DIR_PROPERTY = "nyx.test.tmp"
val TMP_DIR = Path(System.getProperty(TMP_DIR_PROPERTY))

fun Spec.createTmpDir(directory: String = "", prefix: String? = null, delete: Boolean = true): Path {
    val tempDir = createTempDirectory(TMP_DIR.resolve(directory).createDirectories(), prefix)

    afterSpec {
        if (delete)
            tempDir.deleteExisting()
    }

    return tempDir
}

fun createTmpFile(prefix: String? = null, suffix: String? = null, delete: Boolean = true): Path {
    return createTempFile(TMP_DIR.also { it.parent.createDirectories() }, prefix, suffix).also {
        if (delete) it.toFile().deleteOnExit()
    }
}
