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

import masecla.modrinth4j.endpoints.project.ModifyProject;
import masecla.modrinth4j.main.ModrinthAPI;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Syncs the body of the project.
 */
public abstract class ModrinthSyncBodyTask extends DefaultTask {

    /**
     * Constructor
     */
    public ModrinthSyncBodyTask() {
    }

    /**
     * Get Modrinth token.
     *
     * @return Modrinth token.
     */
    @Input
    public abstract Property<String> getToken();

    /**
     * Get project ID.
     *
     * @return Project ID.
     */
    @Input
    public abstract Property<String> getProjectId();

    /**
     * The body to sync from.
     *
     * @return The body to sync from.
     */
    @Input
    public abstract Property<String> getSyncBodyFrom();

    /**
     * Whether to enable debug mode.
     *
     * @return Whether to enable debug mode.
     */
    @Input
    public abstract Property<Boolean> getDebugMode();

    /**
     * Syncs the body of the project with the given slug.
     */
    @TaskAction
    public void run() {
        // 获取参数
        String token = getToken().get();
        String projectId = getProjectId().get();
        String syncBodyFrom = getSyncBodyFrom().get();
        boolean debugMode = getDebugMode().get();

        // 准备 Modrinth API 客户端
        ModrinthAPI modrinthAPI = ModrinthApiFactory.api(token, projectId);
        // 准备请求数据
        ModifyProject.ProjectModifications request = ModifyProject.ProjectModifications.builder()
                .body(syncBodyFrom.replace("\r\n", "\n"))
                .build();

        // Debug 模式检测
        if (debugMode) {
            // 打印请求数据
            getLogger().lifecycle("Full data to be sent for upload: {}",
                    ModrinthApiFactory.DEBUG_GSON.toJson(request));
            // 打印 Debug 提示
            getLogger().lifecycle("Debug mode is enabled. Not going to upload this version.");
            // 提前返回
            return;
        }

        // 发起项目-修改请求
        modrinthAPI.projects().modify(projectId, request).join();

        // 获取 projectSlug
        String projectSlug = modrinthAPI.projects().get(projectId).join().getSlug();
        // 输出成功信息
        getLogger().lifecycle("Successfully uploaded body to {} ({}).", projectSlug, projectId);

    }

}
