/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file NyxProjectInfoExtensionTest.kt is part of nyx
 * Last modified on 18-09-2024 12:10 a.m.
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

package ca.solostudios.nyx.project

import ca.solostudios.nyx.NyxPlugin
import ca.solostudios.nyx.internal.util.formatAsName
import ca.solostudios.nyx.internal.util.nyx
import ca.solostudios.nyx.kotest.matchers.shouldBeEmpty
import ca.solostudios.nyx.kotest.matchers.shouldBePresent
import ca.solostudios.nyx.kotest.matchers.shouldContainExactly
import ca.solostudios.nyx.kotest.matchers.shouldNotBePresent
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.project
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.assign

class NyxProjectInfoExtensionTest : NyxSpec({
    feature("the project info extesion") {
        given("a project with the nyx plugin and default values") {
            val project = project {
                project.apply<NyxPlugin>()

                group = "gay.solonovamax.test-group"
                description = "test description"
                version = "1.2.3"
            }
            val info = project.nyx.info

            upon("initialization") {
                should("have correct default values") {
                    info.name.shouldBePresent()
                    info.name.get() shouldBe project.name.formatAsName()

                    info.group shouldBe project.group

                    info.module.get() shouldBe project.name
                    info.module.shouldBePresent()

                    info.version shouldBe project.version

                    info.description shouldBe project.description

                    info.organizationUrl.shouldNotBePresent()
                    info.organizationName.shouldNotBePresent()

                    info.developers.shouldBePresent()
                    info.developers.shouldBeEmpty()
                }
            }

            upon("updating the nyx project metadata") {
                should("also update the project metadata") {
                    withClue("setting group") {
                        info.group = "gay.solonovamax.new-group"
                        project.group shouldBe info.group
                    }

                    withClue("setting version") {
                        info.version = "4.5.6"
                        project.version shouldBe info.version
                    }

                    withClue("setting description") {
                        info.description = "a new description"
                        project.description shouldBe info.description
                    }
                }
            }

            upon("adding a developer") {
                val developerCallback: MavenPomDeveloper.() -> Unit = {
                    id = "a-developer"
                    name = "A Developer"
                }

                info.developer(developerCallback)

                should("add the developer to the developers list") {
                    info.developers.get().shouldHaveSize(1)
                    info.developers.shouldContainExactly(developerCallback)
                }
            }
        }
    }
})
