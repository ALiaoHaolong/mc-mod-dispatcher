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

import masecla.modrinth4j.endpoints.version.CreateVersion;
import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.version.ProjectVersion;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.*;

import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.GSON;
import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.userAgent;

public abstract class ModrinthTask extends DefaultTask {

    private static final ThreadLocal<ModrinthTask> CURRENT_TASK = new ThreadLocal<>();

    @Optional
    @Input
    public abstract ListProperty<String> getGameVersions();

    @TaskAction
    public void run() {
        CURRENT_TASK.set(this);
        try {

            ModrinthExtension ext = getProject().getExtensions().getByType(ModrinthExtension.class);

            ModrinthAPI modrinthAPI = ModrinthAPI.rateLimited(userAgent(ext), ext.getToken().get());
            String id = ext.getProjectId().get();
            String slug = modrinthAPI.projects().get(id).join().getSlug();

            CreateVersion.CreateVersionRequest request = CreateVersion.CreateVersionRequest.builder()
                    .name(ext.getVersionName().get())
                    .versionNumber(ext.getVersionNumber().get())
                    .changelog(ext.getChangelog().get().replace("\r\n", "\n"))
                    .dependencies(ext.getDependencies().getDependenciesAsList().stream().map(d -> d.toProjectDependency(modrinthAPI)).toList())
                    .gameVersions(getGameVersions().getOrElse(ext.getGameVersions().get())) // 优先使用任务配置
                    .versionType(ProjectVersion.VersionType.valueOf(ext.getVersionType().get().toUpperCase(Locale.ROOT)))
                    .loaders(ext.getLoaders().get())
                    .featured(ext.getFeatured().get())
                    .projectId(ext.getProjectId().get())
                    .files(mergeFiles(ext.getPrimaryFile().get().getAsFile(), ext.getAdditionalFiles().getAdditionalFilesAsList()))
                    .build();

            // Debug
            if (ext.getDebugMode().get()) {
                getLogger().lifecycle("Full data to be sent for upload: {}", GSON.toJson(request));
                getLogger().lifecycle("Debug mode is enabled. Not going to upload this version.");
                return;
            }

            // Request
            ProjectVersion version = modrinthAPI.versions().createProjectVersion(request).join();

            getLogger().lifecycle(
                    "Successfully uploaded version {} to {} ({}) as version ID {}.",
                    version.getVersionNumber(),
                    slug,
                    id,
                    version.getId()
            );

        } finally {
            CURRENT_TASK.remove();
        }
    }

    public static List<String> getCurrentGameVersions(Project project) {
        List<String> gameVersions = CURRENT_TASK.get().getGameVersions().getOrElse(new ArrayList<>());
        if (!gameVersions.isEmpty()) {
            return gameVersions;
        }

        return project.getExtensions().getByType(ModrinthExtension.class).getGameVersions().get();
    }

    private Map<File, String> mergeFiles(File primaryFile, List<AdditionalFile> additionalFiles) {
        Map<File, String> map = new LinkedHashMap<>();
        map.put(primaryFile, "primary");
        additionalFiles.forEach(additionalFile ->
                map.put(additionalFile.file().get().getAsFile(), additionalFile.additionalFileType().toString()));
        return map;
    }

}
