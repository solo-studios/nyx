/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file TempFiles.kt is part of nyx
 * Last modified on 17-09-2024 01:23 a.m.
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

package ca.solostudios.nyx.kotest

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

const val TMP_WORK_DIR_PROPERTY = "nyx.test.work.tmp"
val TMP_WORK_DIR = Path(System.getProperty(TMP_WORK_DIR_PROPERTY))

fun createTmpDir(prefix: String? = null, delete: Boolean = true): Path {
    return createTempDirectory(TMP_WORK_DIR.also {
        it.createDirectories()
    }, prefix).also {
        if (delete) it.toFile().deleteOnExit()
    }
}

fun createTmpFile(prefix: String? = null, suffix: String? = null, delete: Boolean = true): Path {
    return createTempFile(TMP_WORK_DIR.also { it.parent.createDirectories() }, prefix, suffix).also {
        if (delete) it.toFile().deleteOnExit()
    }
}
