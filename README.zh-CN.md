# Mc Mod Dispatcher

🌐 [English](README.md) | 中文

Mc Mod Dispatcher 是一个用于将 Minecraft 模组发布到 Modrinth 的 Gradle 插件。一次配置，即可发布到你所支持的每一个 Minecraft 版本 —— 无需模组开发工具链。

## 支持的平台

- [Modrinth](https://modrinth.com)

## 使用指南

要使用本插件，你需要先将其添加到 Gradle 构建脚本中。之后，你就可以使用 `modrinth` 任务将版本上传到 Modrinth。

Mc Mod Dispatcher 需要一个具有以下权限范围的个人访问令牌（personal access token）：

- `CREATE_VERSION`（运行 `modrinth` 或 `modrinth-VERSION` 任务时需要）
- `PROJECT_WRITE`（运行 `modrinthSyncBody` 任务时需要）

你可以在[个人访问令牌页面](https://modrinth.com/settings/pats)生成令牌。如果你提供的令牌不具备所有必需的权限范围，将会收到如下错误：

```text
EndpointException(error=unauthorized, description=Authentication Error: Invalid Authentication Credentials)
```

### Groovy DSL

首先，你需要在 `settings.gradle` 中添加以下内容：

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

接着，在 `build.gradle` 中添加以下内容：

[![](https://img.shields.io/gradle-plugin-portal/v/io.github.aliaohaolong.mc-mod-dispatcher?label=Gradle%20Plugin%20Portal&color=brightgreen)](https://plugins.gradle.org/plugin/io.github.aliaohaolong.mc-mod-dispatcher)

```groovy
plugins {
    id 'io.github.aliaohaolong.mc-mod-dispatcher' version 'VERSION'
}
```

将 VERSION 替换为上方的版本号（不带 `v` 前缀）。

最后，在 `build.gradle` 中添加以下内容：

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

### 可用属性

| 属性 | 说明 | 默认值 |
|------|------|--------|
| token | 用于 Modrinth API 的有效 API 令牌。 | `MODRINTH_TOKEN` 环境变量 |
| projectId | 要上传到的项目 ID。 | |
| versionName | 版本名称。 | |
| versionNumber | 版本号。 | |
| changelog | 该版本的详细更新日志，支持 Markdown 格式。 | |
| dependencies | 上传版本的依赖项。 | |
| gameVersionGroups | 定义映射到独立上传任务的游戏版本分组。每个 `gameVersions` 会生成一个 `modrinth-VERSION` 任务。 | |
| versionType | 版本类型（例如 `release`、`beta`、`alpha`）。 | `release` |
| loaders | 该版本兼容的加载器列表（例如 `fabric`、`forge`）。 | |
| featured | 是否将该版本设为推荐（featured）。 | `false` |
| primaryFile | 该版本要上传的主文件。 | |
| additionalFiles | 该版本要上传的附加文件，其中键为文件类型（例如 sourcesJar、devJar、javadocJar、signatures、other），值为文件提供者（file provider）。 | |
| syncBodyFrom | 项目的详细描述文本。 | |
| debugMode | 不会真正上传版本，而是打印将要上传的数据。 | `false` |

### 可用任务

#### 上传版本

`gameVersionGroups` 的配置语法如下：

```groovy
modrinth {
    // ...
    gameVersionGroups {
        gameVersions '1.18', '1.18.1', '1.18.2' // 这会生成一个 'modrinth-1.18' 任务，上传支持 Minecraft 1.18、1.18.1 和 1.18.2 的版本
        gameVersions '1.19', '1.19.1', '1.19.2' // 这会生成一个 'modrinth-1.19' 任务，上传支持 Minecraft 1.19、1.19.1 和 1.19.2 的版本
    }
    // ...
}
```

每个 `gameVersions` 条目都对应一个 `modrinth-VERSION` 任务。任务名中的 `VERSION` 默认为该 `gameVersions` 中指定的第一个版本号。

本插件允许在 `versionName`、`versionNumber`、`primaryFile` 和 `additionalFiles` 中使用动态版本号。

- `modrinth.currentFirstVersion`：获取当前任务对应的第一个版本号。
- `modrinth.currentGameVersions`：获取当前任务对应的游戏版本列表。

例如：

```groovy
modrinth {
    // ...
    versionName = provider { "${project.mod_name} ${project.mod_version} for Fabric ${modrinth.currentFirstVersion}" }
    versionNumber = provider { "fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}" }
    primaryFile = provider { layout.projectDirectory.file("${project.mod_id}-fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}.jar") }
    additionalFiles {
        // 其他附加文件的处理方式同理
        sourcesJar provider { layout.projectDirectory.file("${project.mod_id}-fabric-${project.mod_version}-mc${modrinth.currentFirstVersion}-sources.jar") }
    }
}
```

> [!IMPORTANT]
> 只有当值中引用了 `currentFirstVersion` 或 `currentGameVersions` 时，才需要用 `provider { }` 包裹。
> 普通值（例如 `versionName = 'MyMod ${project.mod_version}'`）无需包裹。
>
> 如果忘记包裹，构建会失败，报错如下：
>
> ```text
> > modrinth.currentGameVersions and modrinth.currentFirstVersion can only be read while tasks are being generated. Wrap it in a provider, e.g.: versionName = provider { "MyMod ${project.version} for Fabric ${modrinth.currentFirstVersion}" }
> ```
>
> 这是预期行为：版本号取决于*正在生成哪个任务*，而 Gradle 会在任务执行之前就对 task 的输入求值。

#### 同步项目描述

当你配置了 `syncBodyFrom` 时，会生成一个 `modrinthSyncBody` 任务。运行该任务会将 `syncBodyFrom` 指定的文本上传为你的 Modrinth 项目的描述正文。

## 贡献者 ✨

<a href="https://github.com/aliaohaolong/mc-mod-dispatcher/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=aliaohaolong/mc-mod-dispatcher" alt="aliaohaolong"/>
</a>

## 许可证

本项目基于 Apache License 2.0 授权，详情请参阅 [LICENSE](LICENSE) 文件。

## 致谢

- [Minotaur](https://github.com/modrinth/minotaur)
- [Mod Publisher](https://github.com/firstdarkdev/modpublisher)
- [Modrinth4J](https://github.com/masecla22/Modrinth4J)
