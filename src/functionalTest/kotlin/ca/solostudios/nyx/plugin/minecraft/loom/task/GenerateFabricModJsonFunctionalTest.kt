/*
 * Copyright (c) 2024 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file GenerateFabricModJsonFunctionalTest.kt is part of nyx
 * Last modified on 25-10-2024 07:19 p.m.
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

package ca.solostudios.nyx.plugin.minecraft.loom.task

import ca.solostudios.nyx.kotest.matchers.shouldBeValidJson
import ca.solostudios.nyx.kotest.matchers.shouldEqualJson
import ca.solostudios.nyx.kotest.matchers.shouldHaveSucceeded
import ca.solostudios.nyx.kotest.spec.NyxSpec
import ca.solostudios.nyx.util.addArguments
import ca.solostudios.nyx.util.buildDir
import ca.solostudios.nyx.util.gradleKtsProject
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.paths.shouldContainFile
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.paths.shouldNotBeEmptyDirectory

class GenerateFabricModJsonFunctionalTest : NyxSpec({
    feature("the GenerateFabricModJson task") {
        // this test fails because fuck you gradle
        // givenFabricLoomVersionShouldNotFailGenerateFMJ("1.7.1")
        given("a project with a simple FabricModJson") {
            val project = gradleKtsProject(withPluginClasspath = true) {
                writeSettingsGradleKts(
                    """
                        |rootProject.name = "simple-fmj"
                    """.trimMargin()
                )

                writeBuildGradleKts(
                    """
                        |plugins {
                        |    java
                        |    id("fabric-loom")
                        |    id("ca.solo-studios.nyx")
                        |}
                        |
                        |version = "1.2.3"
                        |group = "my.group"
                        |
                        |nyx {
                        |    minecraft {
                        |        generateFabricModJson = true
                        |    }
                        |}
                        |
                        |dependencies {
                        |    minecraft("com.mojang:minecraft:1.20.1")
                        |    mappings("net.fabricmc:yarn:1.20.1+build.3:v2")
                        |}
                    """.trimMargin()
                )
            }

            upon("executing the GenerateFabricModJson task") {
                val runner = project.gradleRunner {
                    addArguments("generateFabricModJson")
                }

                should("not fail") {
                    val build = shouldNotThrowAny {
                        runner.build()
                    }
                    build.task(":generateMainFabricModJson").shouldHaveSucceeded()
                }
                should("generate the expected fabric.mod.json") {
                    runner.build()

                    val fabricModJson = runner.buildDir.resolve("fabricModJson").resolve("fabric.mod.json")
                    fabricModJson.parent.shouldNotBeEmptyDirectory()
                    fabricModJson.parent shouldContainFile "fabric.mod.json"

                    fabricModJson.shouldBeValidJson()
                    fabricModJson.shouldEqualJson {
                        // language=JSON
                        """
                            |{
                            |    "id": "simple-fmj",
                            |    "name": "Simple Fmj",
                            |    "version": "1.2.3",
                            |    "environment": "*"
                            |}
                        """.trimMargin()
                    }
                }
            }

            upon("executing the build task") {
                val runner = project.gradleRunner {
                    addArguments("build")
                }

                should("not fail") {
                    shouldNotThrowAny {
                        runner.build()
                    }
                }

                should("execute generateMainFabricModJson") {
                    runner.build().task(":generateMainFabricModJson").shouldHaveSucceeded()
                }
            }
        }

        given("a project with a complex FabricModJson") {
            val project = gradleKtsProject(withPluginClasspath = true) {
                writeSettingsGradleKts(
                    """
                        |rootProject.name = "complex-fmj"
                    """.trimMargin()
                )

                writeBuildGradleKts(
                    """
                        |import ca.solostudios.nyx.plugin.minecraft.loom.FabricModJson.Environment
                        |
                        |plugins {
                        |    java
                        |    id("fabric-loom")
                        |    id("ca.solo-studios.nyx")
                        |}
                        |
                        |version = "1.2.3"
                        |group = "my.group"
                        |description = ${"\"\"\""}
                        |    A multiline mod description
                        |    some other content
                        |${"\"\"\""}.trimIndent()
                        |
                        |nyx {
                        |    info {
                        |        name = "Complex fabric.mod.json Example"
                        |        license.useMIT()
                        |    }
                        |    minecraft {
                        |        accessWidener("complex-fmj-accesswidener")
                        |        fabricModJson {
                        |            provide("some-provided-mod")
                        |            environment = Environment.SERVER
                        |            entrypoints {
                        |                entry("custom_entrypoint") {
                        |                    entrypoint("my.group.MyCustomEntrypointClass", "some.adapter")
                        |                    entrypoint("my.group.MyOtherCustomEntrypointClass")
                        |                }
                        |                entry("second_custom_entrypoint") {
                        |                    entrypoint("my.group.MySecondCustomEntrypointClass")
                        |                }
                        |                main("my.group.MyMainClass", "some.adapter")
                        |                main("my.group.MyOtherMainClass")
                        |                client("my.group.MyClientClass", "some.adapter")
                        |                client("my.group.MyOtherClientClass")
                        |                server("my.group.MyServerClass", "some.adapter")
                        |                server("my.group.MyOtherServerClass")
                        |            }
                        |
                        |            author("dummy_author_1", mapOf("discord" to "me"))
                        |            author("dummy_author_2")
                        |
                        |            contributor("dummy_contributor_1", mapOf("discord" to "me"))
                        |            contributor("dummy_contributor_2")
                        |
                        |            mixin("mymod.mixins.json")
                        |            mixin("mymod.client.mixins.json", Environment.CLIENT)
                        |            depends("dependency-1")
                        |            depends("dependency-2", "1.2.3")
                        |            depends("dependency-3", listOf("1.2.3", "4.5.6"))
                        |            recommends("recommended-1")
                        |            recommends("recommended-2", "1.2.3")
                        |            recommends("recommended-3", listOf("1.2.3", "4.5.6"))
                        |            suggests("suggestion-1")
                        |            suggests("suggestion-2", "1.2.3")
                        |            suggests("suggestion-3", listOf("1.2.3", "4.5.6"))
                        |            conflicts("conflict-1")
                        |            conflicts("conflict-2", "1.2.3")
                        |            conflicts("conflict-3", listOf("1.2.3", "4.5.6"))
                        |            breaks("broken-1")
                        |            breaks("broken-2", "1.2.3")
                        |            breaks("broken-3", listOf("1.2.3", "4.5.6"))
                        |
                        |            contact {
                        |                email = "me@example.com"
                        |                homepage = "example.com"
                        |                irc = "#example"
                        |                issues = "example.com/issues"
                        |                source = "example.com/source"
                        |                other = mapOf(
                        |                    "other-link" to "example.com/other-link",
                        |                    "other-link-2" to "example.com/other-link-2",
                        |                )
                        |            }
                        |
                        |            icon("my-icon.png")
                        |            languageAdapter("my-language", "my.group.MyLanguageAdapter")
                        |
                        |            modmenu {
                        |                badges.addAll("foo", "bar")
                        |                library()
                        |                deprecated()
                        |
                        |                links {
                        |                    discord = "example.com/discord"
                        |                    modrinth = "modrinth.com/discord"
                        |                    wiki = "example.com/wiki"
                        |                    other = mapOf(
                        |                        "link1" to "example.com/link1",
                        |                        "link2" to "example.com/link2",
                        |                    )
                        |                }
                        |
                        |                parent {
                        |                    id = "parent-id"
                        |                    name = "Parent Mod"
                        |                    description = ${"\"\"\""}
                        |                        Parent description
                        |                        some other stuff
                        |                    ${"\"\"\""}.trimIndent()
                        |                    icon = "my-icon.png"
                        |                    badges.addAll("foo", "bar", "baz")
                        |                    library()
                        |                    deprecated()
                        |                }
                        |            }
                        |
                        |            custom("custom_int_field", 1)
                        |            custom("custom_string_field", "foo")
                        |            custom("custom_list_field", listOf("foo", "bar"))
                        |            custom("custom_map_field", mapOf("nested_custom_field" to "foo"))
                        |        }
                        |    }
                        |}
                        |
                        |dependencies {
                        |    minecraft("com.mojang:minecraft:1.20.1")
                        |    mappings("net.fabricmc:yarn:1.20.1+build.3:v2")
                        |}
                    """.trimMargin()
                )
            }

            upon("executing the GenerateFabricModJson task") {
                val runner = project.gradleRunner {
                    addArguments("generateMainFabricModJson")
                }

                should("not fail") {
                    val build = shouldNotThrowAny {
                        runner.build()
                    }
                    build.task(":generateMainFabricModJson").shouldHaveSucceeded()
                }
                should("generate the expected fabric.mod.json") {
                    runner.build()

                    val fabricModJson = runner.buildDir.resolve("fabricModJson").resolve("fabric.mod.json")
                    fabricModJson.parent.shouldExist()
                    fabricModJson.parent.shouldNotBeEmptyDirectory()
                    fabricModJson.parent shouldContainFile "fabric.mod.json"

                    fabricModJson.shouldBeValidJson()
                    fabricModJson.shouldEqualJson {
                        // language=JSON
                        """
                            |{
                            |    "id":               "complex-fmj",
                            |    "name":             "Complex fabric.mod.json Example",
                            |    "version":          "1.2.3",
                            |    "provides":         [
                            |        "some-provided-mod"
                            |    ],
                            |    "environment":      "server",
                            |    "entrypoints":      {
                            |        "client":                   [
                            |            {
                            |                "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.AdaptedEntrypoint",
                            |                "adapter": "some.adapter",
                            |                "value":   "my.group.MyClientClass"
                            |            },
                            |            "my.group.MyOtherClientClass"
                            |        ],
                            |        "custom_entrypoint":        [
                            |            {
                            |                "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.AdaptedEntrypoint",
                            |                "adapter": "some.adapter",
                            |                "value":   "my.group.MyCustomEntrypointClass"
                            |            },
                            |            "my.group.MyOtherCustomEntrypointClass"
                            |        ],
                            |        "main":                     [
                            |            {
                            |                "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.AdaptedEntrypoint",
                            |                "adapter": "some.adapter",
                            |                "value":   "my.group.MyMainClass"
                            |            },
                            |            "my.group.MyOtherMainClass"
                            |        ],
                            |        "second_custom_entrypoint": [
                            |            "my.group.MySecondCustomEntrypointClass"
                            |        ],
                            |        "server":                   [
                            |            "my.group.MyOtherServerClass",
                            |            {
                            |                "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Entrypoint.AdaptedEntrypoint",
                            |                "adapter": "some.adapter",
                            |                "value":   "my.group.MyServerClass"
                            |            }
                            |        ]
                            |    },
                            |    "mixins":           [
                            |        {
                            |            "type":        "ca.solostudios.nyx.internal.SerialFabricModJson.MixinConfig.EnvironmentMixinConfig",
                            |            "config":      "mymod.client.mixins.json",
                            |            "environment": "client"
                            |        },
                            |        "mymod.mixins.json"
                            |    ],
                            |    "accessWidener":    "complex-fmj-accesswidener.accesswidener",
                            |    "depends":          {
                            |        "dependency-1": [ ],
                            |        "dependency-2": "1.2.3",
                            |        "dependency-3": [
                            |            "1.2.3",
                            |            "4.5.6"
                            |        ]
                            |    },
                            |    "recommends":       {
                            |        "recommended-1": [ ],
                            |        "recommended-2": "1.2.3",
                            |        "recommended-3": [
                            |            "1.2.3",
                            |            "4.5.6"
                            |        ]
                            |    },
                            |    "suggests":         {
                            |        "suggestion-1": [ ],
                            |        "suggestion-2": "1.2.3",
                            |        "suggestion-3": [
                            |            "1.2.3",
                            |            "4.5.6"
                            |        ]
                            |    },
                            |    "conflicts":        {
                            |        "conflict-1": [ ],
                            |        "conflict-2": "1.2.3",
                            |        "conflict-3": [
                            |            "1.2.3",
                            |            "4.5.6"
                            |        ]
                            |    },
                            |    "breaks":           {
                            |        "broken-1": [ ],
                            |        "broken-2": "1.2.3",
                            |        "broken-3": [
                            |            "1.2.3",
                            |            "4.5.6"
                            |        ]
                            |    },
                            |    "description":      "A multiline mod description\nsome other content",
                            |    "authors":          [
                            |        {
                            |            "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Person.ContactablePerson",
                            |            "name":    "dummy_author_1",
                            |            "contact": {
                            |                "discord": "me"
                            |            }
                            |        },
                            |        {
                            |            "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Person.ContactablePerson",
                            |            "name":    "dummy_author_2",
                            |            "contact": { }
                            |        }
                            |    ],
                            |    "contributors":     [
                            |        {
                            |            "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Person.ContactablePerson",
                            |            "name":    "dummy_contributor_1",
                            |            "contact": {
                            |                "discord": "me"
                            |            }
                            |        },
                            |        {
                            |            "type":    "ca.solostudios.nyx.internal.SerialFabricModJson.Person.ContactablePerson",
                            |            "name":    "dummy_contributor_2",
                            |            "contact": { }
                            |        }
                            |    ],
                            |    "contact":          {
                            |        "homepage":     "example.com",
                            |        "issues":       "example.com/issues",
                            |        "source":       "example.com/source",
                            |        "email":        "me@example.com",
                            |        "irc":          "#example",
                            |        "other-link":   "example.com/other-link",
                            |        "other-link-2": "example.com/other-link-2"
                            |    },
                            |    "license":          "MIT",
                            |    "icon":             "my-icon.png",
                            |    "languageAdapters": {
                            |        "type":        "kotlin.collections.LinkedHashMap",
                            |        "my-language": "my.group.MyLanguageAdapter"
                            |    },
                            |    "custom":           {
                            |        "modmenu":             {
                            |            "badges": [
                            |                "foo",
                            |                "bar",
                            |                "library",
                            |                "deprecated"
                            |            ],
                            |            "links":  {
                            |                "modmenu.discord":  "example.com/discord",
                            |                "modmenu.modrinth": "modrinth.com/discord",
                            |                "modmenu.wiki":     "example.com/wiki",
                            |                "link1":            "example.com/link1",
                            |                "link2":            "example.com/link2"
                            |            },
                            |            "parent": {
                            |                "id":          "parent-id",
                            |                "name":        "Parent Mod",
                            |                "description": "Parent description\nsome other stuff",
                            |                "icon":        "my-icon.png",
                            |                "badges":      [
                            |                    "foo",
                            |                    "bar",
                            |                    "baz",
                            |                    "library",
                            |                    "deprecated"
                            |                ]
                            |            }
                            |        },
                            |        "custom_int_field":    1,
                            |        "custom_string_field": "foo",
                            |        "custom_list_field":   [
                            |            "foo",
                            |            "bar"
                            |        ],
                            |        "custom_map_field":    {
                            |            "nested_custom_field": "foo"
                            |        }
                            |    }
                            |}
                        """.trimMargin()
                    }
                }
            }

            upon("executing the build task") {
                val runner = project.gradleRunner {
                    addArguments("build")
                }

                should("not fail") {
                    shouldNotThrowAny {
                        runner.build()
                    }
                }
                should("execute generateMainFabricModJson") {
                    runner.build().task(":generateMainFabricModJson").shouldHaveSucceeded()
                }
            }
        }

    }
})

