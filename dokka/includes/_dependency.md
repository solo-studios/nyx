[//]: # (@formatter:off)
You can add the ${project.module} gradle plugin to your project with the following:

<div class="tabbed-set tabbed-alternate" data-tabs="1:3">
<input id="__tabbed_1_1" name="__tabbed_1" type="radio"/>
<input checked="checked" id="__tabbed_1_2" name="__tabbed_1" type="radio"/>
<input id="__tabbed_1_3" name="__tabbed_1" type="radio"/>
<div class="tabbed-labels">
<label for="__tabbed_1_1">Gradle Groovy</label>
<label for="__tabbed_1_2">Gradle Kotlin</label>
<label for="__tabbed_1_3">Gradle Version Catalog</label>
</div>
<div class="tabbed-content">
<div class="tabbed-block">

Add this to your `build.gradle`:
```groovy
plugins {
    id '${project.group}.${project.module}' version '${project.version}'
}
```

</div>
<div class="tabbed-block">

Add this to your `build.gradle.kts`:
```kotlin
plugins {
    id("${project.group}.${project.module}") version "${project.version}"
}
```

</div>
<div class="tabbed-block">

Add this to your `gradle/libs.versions.toml`:
```toml
[versions]
${project.module} = "${project.version}"

[plugins]
${project.module} = { id = "${project.group}.${project.module}", version.ref = "${project.module}" }
```
Add this to your `build.gradle`/`build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.nyx)
}
```

</div>
</div>
</div>
<br>

To use a snapshot version, add the solo-studios snapshots repository to your `settings.gradle`/`settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        maven("https://maven.solo-studios.ca/snapshots/") {
            name = "Solo Studios"
        }
    }
}
```
