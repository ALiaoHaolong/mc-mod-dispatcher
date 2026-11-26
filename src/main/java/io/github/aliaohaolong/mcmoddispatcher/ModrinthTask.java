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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import masecla.modrinth4j.endpoints.version.CreateVersion;
import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.version.ProjectVersion;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modrinth Task
 */
public abstract class ModrinthTask extends DefaultTask {

    /**
     * Constructor
     */
    public ModrinthTask() {
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
     * Get version name.
     *
     * @return Version name.
     */
    @Input
    public abstract Property<String> getVersionName();

    /**
     * Get version number.
     *
     * @return Version number.
     */
    @Input
    public abstract Property<String> getVersionNumber();

    /**
     * Get changelog.
     *
     * @return Changelog.
     */
    @Input
    public abstract Property<String> getChangelog();

    /**
     * Get dependencies.
     *
     * @return Dependencies.
     */
    @Input
    public abstract ListProperty<Dependency> getDependencies();

    /**
     * Get game versions.
     *
     * @return Game versions.
     */
    @Input
    public abstract ListProperty<String> getGameVersions();

    /**
     * Get version type.
     *
     * @return Version type.
     */
    @Input
    public abstract Property<ProjectVersion.VersionType> getVersionType();

    /**
     * Get loaders.
     *
     * @return Loaders.
     */
    @Input
    public abstract ListProperty<String> getLoaders();

    /**
     * Get featured.
     *
     * @return Featured.
     */
    @Input
    public abstract Property<Boolean> getFeatured();

    /**
     * Get primary file.
     *
     * @return Primary file.
     */
    @InputFile
    public abstract RegularFileProperty getPrimaryFile();

    /**
     * Get additional files.
     *
     * @return Additional files.
     */
    @Nested
    public abstract ListProperty<AdditionalFile> getAdditionalFiles();

    /**
     * Get debug mode.
     *
     * @return Debug mode.
     */
    @Input
    public abstract Property<Boolean> getDebugMode();

    /**
     * Run task.
     */
    @TaskAction
    public void run() {
        // 获取参数
        String token = getToken().get();
        String projectId = getProjectId().get();
        String versionName = getVersionName().get();
        String versionNumber = getVersionNumber().get();
        String changelog = getChangelog().get().replace("\r\n", "\n");
        List<Dependency> dependencies = getDependencies().get();
        List<String> gameVersions = getGameVersions().get();
        ProjectVersion.VersionType versionType = getVersionType().get();
        List<String> loaders = getLoaders().get();
        boolean featured = getFeatured().get();
        File primaryFile = getPrimaryFile().get().getAsFile();
        List<AdditionalFile> additionalFiles = getAdditionalFiles().get();
        boolean debugMode = getDebugMode().get();

        // 准备 Modrinth API 客户端
        ModrinthAPI modrinthAPI = ModrinthApiFactory.api(token);
        // 准备请求数据
        CreateVersion.CreateVersionRequest request = CreateVersion.CreateVersionRequest.builder()
                .name(versionName)
                .versionNumber(versionNumber)
                .changelog(changelog)
                .dependencies(dependencies.stream().map(d -> d.toProjectDependency(modrinthAPI)).toList())
                .gameVersions(gameVersions)
                .versionType(versionType)
                .loaders(loaders)
                .featured(featured)
                .projectId(projectId)
                .files(mergeFiles(primaryFile, additionalFiles))
                .build();

        // Debug 模式检测
        if (debugMode) {
            // 打印请求数据
            getLogger().lifecycle("Full data to be sent for upload: {}", toJson(request));
            // 打印 Debug 提示
            getLogger().lifecycle("Debug mode is enabled. Not going to upload this version.");
            // 提前返回
            return;
        }

        // 发起版本-创建项目版本请求
        ProjectVersion version = modrinthAPI.versions().createProjectVersion(request).join();

        // 获取 projectSlug
        String projectSlug = modrinthAPI.projects().get(projectId).join().getSlug();
        // 输出成功信息
        getLogger().lifecycle("Successfully uploaded version {} to {} ({}) as version ID {}.",
                version.getVersionNumber(), projectSlug, projectId, version.getId());
    }

    private Map<File, String> mergeFiles(File primaryFile, List<AdditionalFile> additionalFiles) {
        Map<File, String> map = new LinkedHashMap<>();
        map.put(primaryFile, "primary");
        additionalFiles.forEach(additionalFile ->
                map.put(additionalFile.file().getAsFile(), additionalFile.type().toString())
        );
        return map;
    }

    private String toJson(CreateVersion.CreateVersionRequest request) {
        // 将请求转换为 JSON 对象
        JsonObject jsonObject = ModrinthApiFactory.DEBUG_GSON.toJsonTree(request).getAsJsonObject();
        // 补充 primary_file 参数（受 CreateVersion 实现影响，primary_file 参数不会自动序列化到 JSON 中）
        // copy from masecla.modrinth4j.endpoints.version.CreateVersion#sendRequest
        if (request.getFileNames().size() > 1) {
            String primaryFile = request.getPrimaryFile();
            if (primaryFile == null || primaryFile.isEmpty()) {
                primaryFile = request.getFileNames().getFirst();
            }
            jsonObject.addProperty("primary_file", primaryFile);
        }
        // 补充 file_parts 参数（受 CreateVersion 实现影响，file_parts 参数不会自动序列化到 JSON 中）
        // copy from masecla.modrinth4j.endpoints.version.CreateVersion#sendRequest
        JsonArray array = new JsonArray();
        for (String filename : request.getFileNames()) {
            array.add(filename);
        }
        jsonObject.add("file_parts", array);
        // 返回 JSON 字符串
        return ModrinthApiFactory.DEBUG_GSON.toJson(jsonObject);
    }

}
