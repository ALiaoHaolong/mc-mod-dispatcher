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
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.GSON;
import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.userAgent;

public abstract class ModrinthTask extends DefaultTask {

    @Optional
    @Input
    public abstract Property<String> getChangelog();

    @Optional
    @Input
    public abstract ListProperty<String> getGameVersions();

    @Optional
    @Input
    public abstract Property<String> getVersionNumber();

    @Optional
    @Input
    public abstract Property<String> getVersionName();

    @Optional
    @InputFile
    public abstract RegularFileProperty getPrimaryFile();

    @Optional
    @Input
    public abstract Property<AdditionalFilesExtension> getAdditionalFiles();

    @Optional
    @Input
    public abstract Property<String> getVersionType();

    @Optional
    @Input
    public abstract ListProperty<String> getLoaders();

    @Optional
    @Input
    public abstract Property<DependenciesExtension> getDependencies();

    public void additionalFiles(Action<? super AdditionalFilesExtension> action) {
        if (!getAdditionalFiles().isPresent()) {
            getAdditionalFiles().set(getProject().getObjects().newInstance(AdditionalFilesExtension.class));
        }
        action.execute(getAdditionalFiles().get());
    }

    public void dependencies(Action<? super DependenciesExtension> action) {
        if (!getDependencies().isPresent()) {
            getDependencies().set(getProject().getObjects().newInstance(DependenciesExtension.class));
        }
        action.execute(getDependencies().get());
    }

    @TaskAction
    public void run() {
        ModrinthExtension config = getProject().getExtensions().getByType(ModrinthExtension.class);

        ModrinthAPI modrinthAPI = ModrinthAPI.rateLimited(userAgent(config), config.getToken().get());
        String id = config.getProjectId().get();
        String slug = modrinthAPI.projects().get(id).join().getSlug();

        CreateVersion.CreateVersionRequest request = CreateVersion.CreateVersionRequest.builder()
                .name(getVersionName().get())
                .versionNumber(getVersionNumber().get())
                .changelog(getChangelog().get().replace("\r\n", "\n"))
                .dependencies(getDependencies().get().getDependenciesAsList().stream().map(d -> d.toProjectDependency(modrinthAPI)).toList())
                .gameVersions(getGameVersions().get())
                .versionType(ProjectVersion.VersionType.valueOf(getVersionType().get().toUpperCase(Locale.ROOT)))
                .loaders(getLoaders().get())
                .featured(false)
                .projectId(config.getProjectId().get())
                .files(mergeFiles(getPrimaryFile().get().getAsFile(), getAdditionalFiles().get().getAdditionalFilesAsList()))
                .build();

        // Debug
        if (config.getDebugMode().get()) {
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
    }

    private Map<File, String> mergeFiles(File primaryFile, List<AdditionalFile> additionalFiles) {
        Map<File, String> map = new LinkedHashMap<>();
        map.put(primaryFile, "primary");
        additionalFiles.forEach(additionalFile ->
                map.put(additionalFile.file().get().getAsFile(), additionalFile.additionalFileType().toString()));
        return map;
    }

}
