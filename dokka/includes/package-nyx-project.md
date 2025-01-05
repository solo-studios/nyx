# Package ca.solostudios.nyx.project

By default, nyx adds a top-level nyx block with an info block. The info extension is used to configure metadata about the project.

For example, you may do:

```kotlin
nyx {
    info {
        name = "Nyx"
        group = "ca.solo-studios"
        module = "nyx"
        version = scmVersion.version
        description = """
            Nyx is a gradle plugin intended to simplify gradle buildscripts using common conventions.
        """.trimIndent()

        organizationUrl = "https://solo-studios.ca/"
        organizationName = "Solo Studios"

        developer {
            id = "solonovamax"
            name = "solonovamax"
            email = "solonovamax@12oclockpoint.com"
            url = "https://solonovamax.gay"
        }

        repository.fromGithub("solo-studios", "nyx")
        license.useMIT()
    }
}
```

See [ca.solostudios.nyx.project.NyxProjectInfoExtension] for details on each of the properties.
