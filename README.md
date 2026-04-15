# Mc Mod Dispatcher

Mc Mod Dispatcher is a Gradle plugin that allows mod developers to publish their mods to Modrinth, supporting management of multi-version releases from a single configuration — no mod dev environment required.

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

![](https://img.shields.io/github/v/tag/aliaohaolong/mc-mod-dispatcher?label=Mc%20Mod%20Dispatcher)
![](https://img.shields.io/badge/master--SNAPSHOT-blue?label=Mc%20Mod%20Dispatcher%20Snapshot&badgeContent=master)

```groovy
plugins {
    id 'com.github.aliaohaolong.mc-mod-dispatcher' version 'VERSION'
}
```

Replace VERSION with the version above.

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
    gameVersions = ['26.1', '26.1.1', '26.1.2']
    versionBundles {
        versionBundle '1.18', '1.18.1', '1.18.2'
        versionBundle '1.19', '1.19.1', '1.19.2'
    }
    versionType = 'release'
    loaders = ['fabric', 'forge']
    featured = false
    primaryFile = layout.projectDirectory.file('build/libs/your_mod.jar')
    additionalFiles {
        sourcesJar layout.projectDirectory.file('build/libs/your_mod-sources.jar')
        devJar layout.projectDirectory.file('build/libs/your_mod-dev.jar')
        javadocJar layout.projectDirectory.file('build/libs/your_mod-javadoc.jar')
        signatures layout.projectDirectory.file('build/signatures/your_mod-signatures.zip')
        other layout.projectDirectory.file('build/other/your_mod-other.zip')
    }
    syncBodyFrom = providers.fileContents('Modrinth.md')
    debugMode = true
}
```

### Available Properties

| Property        | Description                                                                                                                                                                                                                                                                                           | Default                               |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------|
| token           | A valid API token for the Modrinth API.                                                                                                                                                                                                                                                               | `MODRINTH_TOKEN` environment variable |
| projectId       | The ID of the project to upload to.                                                                                                                                                                                                                                                                   |                                       |
| versionName     | The name of the version.                                                                                                                                                                                                                                                                              |                                       |
| versionNumber   | The version number.                                                                                                                                                                                                                                                                                   |                                       |
| changelog       | A detailed changelog for this version. Allows Markdown formatting.                                                                                                                                                                                                                                    |                                       |
| dependencies    | Dependencies of the uploaded version.                                                                                                                                                                                                                                                                 |                                       |
| gameVersions    | An array of game versions that this version supports.                                                                                                                                                                                                                                                 |                                       |
| versionBundles  | Defines groups of game versions that map to separate upload tasks. Each `versionBundle` generates a `modrinth-VERSION` task, allowing a single configuration to publish multiple artifacts targeting different Minecraft version ranges - ideal for mods maintained across parallel version branches. |                                       |
| versionType     | The type of the version (e.g., `release`, `beta`, `alpha`).                                                                                                                                                                                                                                           | `release`                             |
| loaders         | A list of loaders this version is compatible with (e.g., `fabric`, `forge`).                                                                                                                                                                                                                          |                                       |
| featured        | Whether the version is featured or not.                                                                                                                                                                                                                                                               | `false`                               |
| primaryFile     | The primary file to upload for this version.                                                                                                                                                                                                                                                          |                                       |
| additionalFiles | A map of additional files to upload for this version, where the key is the file type (e.g., sourcesJar, devJar, javadocJar, signatures, other) and the value is a file provider.                                                                                                                      |                                       |
| syncBodyFrom    | A long form description of the project.                                                                                                                                                                                                                                                               |                                       |
| debugMode       | Doesn't actually upload the version, and prints the data to be uploaded.                                                                                                                                                                                                                              | `false`                               |

### Available Tasks

#### Upload Version

When you configure `gameVersions` without configuring `versionBundles`, Mc Mod Dispatcher generates a `modrinth` task for you. Running this task uploads a single version to Modrinth.

If your mod has multiple artifacts, each supporting different Minecraft versions, you can use `versionBundles` to generate a separate publishing task for each artifact. The configuration syntax for `versionBundles` is as follows:

```groovy
modrinth {
    // ...
    versionBundles {
        versionBundle '1.18', '1.18.1', '1.18.2' // This will generate a 'modrinth-1.18' task that uploads a version supporting Minecraft 1.18, 1.18.1, and 1.18.2
        versionBundle '1.19', '1.19.1', '1.19.2' // This will generate a 'modrinth-1.19' task that uploads a version supporting Minecraft 1.19, 1.19.1, and 1.19.2
    }
    // ...
}
```

Each `versionBundle` entry corresponds to a `modrinth-VERSION` task. The `VERSION` in the task name defaults to the first version number specified in the `versionBundle`.

If you want to use dynamic version numbers in other configuration items. For example, setting `versionName` to:

```groovy
modrinth {
    // ...
    versionName = "${project.mod_name} ${project.mod_version} for Fabric ${THE_FIRST_GAME_VERSION_IN_GAMEVERSIONS}".toString()
    // ...
}
```

Then first, replace `${THE_FIRST_GAME_VERSION_IN_GAMEVERSIONS}` with `${ModrinthTask.getCurrentGameVersions(project).getFirst()}`.

Next, wrap the entire parameter with `providers { "THE VERSION NAME".toString() }` so that it is resolved dynamically at execution time.

For example:

```groovy
modrinth {
    // ...
    versionName = providers { "${project.mod_name} ${project.mod_version} for Fabric ${ModrinthTask.getCurrentGameVersions(project).getFirst()}".toString() }
}
```

Configuration items that support this dynamic versioning include: `versionName`, `versionNumber`, `primaryFile`, and the file parameters within `additionalFiles`.

Notably, the value for `additionalFiles` requires an additional `as Provider<RegularFile>` at the end. For example:

```groovy
modrinth {
    // ...
    additionalFiles {
        sourcesJar provider { layout.projectDirectory.file("build/libs/${project.mod_id}-fabric-${project.mod_version}-mc${ModrinthTask.getCurrentGameVersions(project).getFirst()}-sources.jar") } as Provider<RegularFile>
    }
    // ...
}
```

When you configure both `gameVersions` and `versionBundles`, the original `modrinth` task name from `gameVersions` will change to `modrinth-VERSION`, where `VERSION` defaults to the first version number specified in `gameVersions`. The tasks generated from `versionBundles` will still be created as usual.

This naming convention helps identify which version the task corresponds to when multiple upload tasks exist.

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
