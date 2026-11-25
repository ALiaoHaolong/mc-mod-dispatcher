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

package io.github.aliaohaolong.mcmoddispatcher;

import masecla.modrinth4j.model.version.ProjectVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;

/**
 * This class is the main class of the plugin.
 */
public class McModDispatcherPlugin implements Plugin<Project> {

    /**
     * Constructor
     */
    public McModDispatcherPlugin() {
    }

    @Override
    public void apply(@NonNull Project project) {
        // 注册 Extension (扩展)
        project.getExtensions().create("modrinth", ModrinthExtension.class, project);

        // 注册 Task (任务)
        project.afterEvaluate(p -> {

            ModrinthExtension ext = p.getExtensions().getByType(ModrinthExtension.class);

            // 注册 modrinth 任务
            if (canRegisterModrinthTask(ext)) {
                for (List<String> gameVersions : ext.getGameVersionGroups().getAll()) {
                    String firstVersion = gameVersions.getFirst();
                    p.getTasks().register("modrinth-" + firstVersion, ModrinthTask.class, task -> {
                        task.setGroup("publishing");
                        task.setDescription("Upload project to Modrinth for Minecraft " + firstVersion);
                        // 反向注入，用于 Extension 中其他属性可以获取当前任务的 gameVersions
                        ext.setCurrentGameVersions(gameVersions);
                        // 注入配置
                        task.getToken().set(ext.getToken());
                        task.getProjectId().set(ext.getProjectId());
                        task.getVersionName().set(ext.getVersionName().get().toString());
                        task.getVersionNumber().set(ext.getVersionNumber().get().toString());
                        task.getChangelog().set(ext.getChangelog());
                        task.getDependencies().set(ext.getDependencies().getAll());
                        task.getGameVersions().set(gameVersions);
                        task.getVersionType().set(ProjectVersion.VersionType.valueOf(ext.getVersionType().get().toUpperCase(Locale.ROOT)));
                        task.getLoaders().set(ext.getLoaders());
                        task.getFeatured().set(ext.getFeatured());
                        task.getPrimaryFile().set(ext.getPrimaryFile().get());
                        task.getAdditionalFiles().set(ext.getAdditionalFiles().resolveAll());
                        task.getDebugMode().set(ext.getDebugMode());
                        // 复位
                        ext.setCurrentGameVersions(List.of());
                    });
                }
            }

            // 注册 modrinthSyncBody 任务
            if (canRegisterModrinthSyncBodyTask(ext)) {
                project.getTasks().register("modrinthSyncBody", ModrinthSyncBodyTask.class, task -> {
                    task.setGroup("publishing");
                    task.setDescription("Upload body to Modrinth");
                    // 注入配置
                    task.getToken().set(ext.getToken());
                    task.getProjectId().set(ext.getProjectId());
                    task.getSyncBodyFrom().set(ext.getSyncBodyFrom());
                    task.getDebugMode().set(ext.getDebugMode());
                });
            }

        });
    }

    private boolean canRegisterModrinthTask(ModrinthExtension ext) {
        return !ext.getGameVersionGroups().getAll().isEmpty();
    }

    private boolean canRegisterModrinthSyncBodyTask(ModrinthExtension ext) {
        return ext.getSyncBodyFrom().isPresent();
    }

}
