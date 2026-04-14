/*
 * Copyright 2026 廖浩龙
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.aliaohaolong.mcmoddispatcher;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jspecify.annotations.NonNull;

public class McModDispatcher implements Plugin<Project> {

    @Override
    public void apply(@NonNull Project project) {
        // 注册 Extension
        project.getExtensions().create("modrinth", ModrinthExtension.class, project);

        // 注册 Task
        project.afterEvaluate(p -> {

            ModrinthExtension ext = p.getExtensions().getByType(ModrinthExtension.class);

            // 注册默认 modrinth 任务（如果配置了 gameVersions）
            if (!ext.getGameVersions().get().isEmpty()) {
                String taskName = "modrinth";
                // 如果同时配置了多版本 modrinth 任务，为避免混淆，将为默认 modrinth 任务增加后缀
                if (hasVersionBundles(ext)) {
                    taskName = "modrinth-" + ext.getGameVersions().get().getFirst();
                }
                project.getTasks().register(taskName, ModrinthTask.class, task -> {
                    task.setGroup("publishing");
                    task.setDescription("Upload project to Modrinth");
                    task.notCompatibleWithConfigurationCache("idk");
                });
            }

            // 注册多版本 modrinth 任务（如果配置了 versionBundles）
            if (hasVersionBundles(ext)) {
                for (VersionBundle versionBundle : ext.getVersionBundles().getVersionBundles()) {
                    String minecraftVersion = versionBundle.getFirstSupportedVersion();
                    p.getTasks().register("modrinth-" + minecraftVersion, ModrinthTask.class, task -> {
                        task.setGroup("publishing");
                        task.setDescription("Upload project to Modrinth for Minecraft " + minecraftVersion);
                        task.notCompatibleWithConfigurationCache("idk");
                        task.getGameVersions().set(versionBundle.getSupportedVersions()); // 注入到任务配置
                    });
                }
            }

            // 注册 modrinthSyncBody 任务（如果配置了 syncBodyFrom）
            if (ext.getSyncBodyFrom().isPresent()) {
                project.getTasks().register("modrinthSyncBody", ModrinthSyncBodyTask.class, task -> {
                    task.setGroup("publishing");
                    task.setDescription("Upload body to Modrinth");
                    task.notCompatibleWithConfigurationCache("idk");
                });
            }

        });
    }

    private boolean hasVersionBundles(ModrinthExtension ext) {
        return !ext.getVersionBundles().getVersionBundles().isEmpty();
    }

}
