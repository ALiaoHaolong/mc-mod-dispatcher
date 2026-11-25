# Mc Mod Dispatcher

🌐 English | [中文](README.zh-CN.md)

A Gradle plugin for publishing Minecraft mods to Modrinth. Configure once, publish to every Minecraft version you support — no mod dev toolchain required.

## Supported Platforms

- [Modrinth](https://modrinth.com)

## Usage Guide

To use this plugin you must add it to your Gradle build script. After that, you can use the `modrinth` task to upload the version to Modrinth.

Mc Mod Dispatcher requires a personal access token with the following scopes:

- `CREATE_VERSION` (if running the `modrinth` or `modrinth-VERSION` task)
- `PROJECT_WRITE` (if running the `modrinthSyncBody` task)

You can generate a token on [the personal access tokens page](https://modrinth.com/settings/pats). If your provided token does not have all required scopes, you will get an error saying Invalid Authentication Credentials.

```text
EndpointException(error=unauthorized, description=Authentication Error: Invalid Authentication Credentials)
```

### Groovy DSL

To use this plugin, first you have to add the following to your `settings.gradle`:

```groovy
pluginManagement {
	repositories {
		gradlePluginPortal()
		maven {
			url 'https://jitpack.io'
		}
	}
}
```

Next, add the following to your `build.gradle`:

[![](https://img.shields.io/gradle-plugin-portal/v/io.github.aliaohaolong.mc-mod-dispatcher?label=Gradle%20Plugin%20Portal&color=brightgreen)](https://plugins.gradle.org/plugin/io.github.aliaohaolong.mc-mod-dispatcher)

```groovy
plugins {
    id 'io.github.aliaohaolong.mc-mod-dispatcher' version 'VERSION'
}
```

Replace VERSION with the version above (without the `v` prefix).

Finally, add the following to `build.gradle` file:

```groovy
modrinth {
    token = 'your_modrinth_api_token'
    projectId = 'your_modrinth_project_id'
    versionName = 'a_fancy_version_name'
    versionNumber = '1.0.0'
    changelog = 'A detailed changelog for this version.'
    dependencies {
        required 'fabric-api'
        optional 'some-optional-dependency'
        incompatible 'some-incompatible-dependency'
        embedded 'some-embedded-dependency'

        required 'fabric-api', '0.44.0+1.18'
        optional 'some-optional-dependency', '1.2.3'
        incompatible 'some-incompatible-dependency', '2.0.0'
        embedded 'some-embedded-dependency', '3.4.5'
    }
    gameVersionGroups {
        gameVersions '1.18', '1.18.1', '1.18.2'
        gameVersions '1.19', '1.19.1', '1.19.2'
    }
    versionType = 'release'
    loaders = ['fabric', 'forge']
    featured = false
    primaryFile = layout.projectDirectory.file('build/libs/your_mod.jar')
    additionalFiles {
        sourcesJar layout.projectDirectory.file('build/libs/your_mod-sources.jar')
        devJar layout.projectDirectory.file('build/libs/your_mod-dev.jar')
        javadocJar layout.projectDirectory.file('build/libs/your_mod-javadoc.jar')
        signature layout.projectDirectory.file('build/signatures/your_mod-signatures.zip')
        other layout.projectDirectory.file('build/other/your_mod-other.zip')
    }
    syncBodyFrom = providers.fileContents('Modrinth.md')
    debugMode = true
}
```

### Available Properties

| Property| Description| Default|
|---|---|---|
| token| A valid API token for the Modrinth API.| `MODRINTH_TOKEN` environment variable |
| projectId| The ID of the project to upload to.| |
| versionName| The name of the version.| |
| versionNumber| The version number.||
| changelog| A detailed changelog for this version. Allows Markdown formatting.||
| dependencies| Dependencies of the uploaded version.||
| gameVersionGroups | Defines groups of game versions that map to separate upload tasks. Each `gameVersions` generates a `modrinth-VERSION` task.||
| versionType| The type of the version (e.g., `release`, `beta`, `alpha`).| `release`|
| loaders| A list of loaders this version is compatible with (e.g., `fabric`, `forge`).||
| featured| Whether the version is featured or not.| `false`|
| primaryFile| The primary file to upload for this version.||
| additionalFiles| The additional files to upload for this version, where the key is the file type (e.g., sourcesJar, devJar, javadocJar, signatures, other) and the value is a file provider.||
| syncBodyFrom| A long form description of the project.||
| debugMode| Doesn't actually upload the version, and prints the data to be uploaded.| `false`|

### Available Tasks

#### Upload Version

The configuration syntax for `gameVersionGroups` is as follows:

```groovy
modrinth {
    // ...
    gameVersionGroups {
        gameVersions '1.18', '1.18.1', '1.18.2' // This will generate a 'modrinth-1.18' task that uploads a version supporting Minecraft 1.18, 1.18.1, and 1.18.2
        gameVersions '1.19', '1.19.1', '1.19.2' // This will generate a 'modrinth-1.19' task that uploads a version supporting Minecraft 1.19, 1.19.1, and 1.19.2
    }
    // ...
}
```

Each `gameVersions` entry corresponds to a `modrinth-VERSION` task. The `VERSION` in the task name defaults to the first version number specified in the `gameVersions`.

This plugin allows you to use dynamic version numbers in `versionName`, `versionNumber`, `primaryFile`, and `additionalFiles`.

- `modrinth.currentFirstVersion`：Get the first version number corresponding to the current task.
- `modrinth.currentGameVersions`：Get the list of game versions corresponding to the current task.

For example:

```groovy
modrinth {
    // ...
    versionName = provider { "${project.mod_name} ${project.mod_version} for Fabric ${modrinth.currentFirstVersion}" }
    versionNumber = provider { "fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}" }
    primaryFile = provider { layout.projectDirectory.file("${project.mod_id}-fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}.jar") }
    additionalFiles {
        // Other additional files are handled in a similar manner
        sourcesJar provider { layout.projectDirectory.file("${project.mod_id}-fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}-sources.jar") }
    }
}
```

> [!IMPORTANT]
> `provider { }` is only required when the value references `currentFirstVersion` or
> `currentGameVersions`. A plain value such as `versionName = 'MyMod ${project.mod_version}'` needs no wrapper.
>
> If you forget it, the build fails with:
>
> ```text
> > modrinth.currentGameVersions and modrinth.currentFirstVersion can only be read while tasks are being generated. Wrap it in a provider, e.g.: versionName = provider { "MyMod ${project.version} for Fabric ${modrinth.currentFirstVersion}" }
> ```
>
> This is expected: the version depends on *which* task is being generated, and Gradle
> evaluates task inputs before the task runs.

#### Sync Project Body

When you configure `syncBodyFrom`, a `modrinthSyncBody` task is generated. Running this task uploads the text specified by `syncBodyFrom` to the body of your Modrinth project.

## Contributors ✨

<a href="https://github.com/aliaohaolong/mc-mod-dispatcher/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=aliaohaolong/mc-mod-dispatcher" alt="aliaohaolong"/>
</a>

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Minotaur](https://github.com/modrinth/minotaur)
- [Mod Publisher](https://github.com/firstdarkdev/modpublisher)
- [Modrinth4J](https://github.com/masecla22/Modrinth4J)
