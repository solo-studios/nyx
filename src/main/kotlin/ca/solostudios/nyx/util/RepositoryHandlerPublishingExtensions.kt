/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file RepositoryHandlerPublishingExtensions.kt is part of nyx
 * Last modified on 19-06-2024 05:16 p.m.
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

package ca.solostudios.nyx.util

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.maven

/**
 * Configures a maven repository with basic authentication.
 */
public fun RepositoryHandler.reposiliteMaven(action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven {
        authentication { // publishing doesn't work without this for some reason
            create<BasicAuthentication>("basic")
        }

        action()
    }
}

/**
 * Configures a maven repository with basic authentication.
 */
public fun RepositoryHandler.reposiliteMaven(url: Any, action: MavenArtifactRepository.() -> Unit = {}): MavenArtifactRepository {
    return maven(url) {
        authentication { // publishing doesn't work without this for some reason
            create<BasicAuthentication>("basic")
        }

        action()
    }
}
