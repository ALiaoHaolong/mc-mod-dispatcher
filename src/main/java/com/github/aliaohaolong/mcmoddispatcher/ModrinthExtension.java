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

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public class ModrinthExtension {

    private final Property<String> token;
    private final Property<String> projectId;
    private final Property<String> versionName;
    private final Property<String> versionNumber;
    private final Property<String> changelog;
    private final DependenciesExtension dependencies;
    private final ListProperty<String> gameVersions;
    private final VersionBundlesExtension versionBundles;
    private final Property<String> versionType;
    private final ListProperty<String> loaders;
    private final Property<Boolean> featured;
    private final RegularFileProperty primaryFile;
    private final AdditionalFilesExtension additionalFiles;
    private final Property<String> syncBodyFrom;
    private final Property<Boolean> debugMode;

    public ModrinthExtension(Project project) {
        token = project.getObjects().property(String.class).convention(project.getProviders().environmentVariable("MODRINTH_TOKEN"));
        projectId = project.getObjects().property(String.class);
        versionName = project.getObjects().property(String.class);
        versionNumber = project.getObjects().property(String.class);
        changelog = project.getObjects().property(String.class);
        dependencies = project.getObjects().newInstance(DependenciesExtension.class);
        gameVersions = project.getObjects().listProperty(String.class).empty();
        versionBundles = project.getObjects().newInstance(VersionBundlesExtension.class);
        versionType = project.getObjects().property(String.class).convention("release");
        loaders = project.getObjects().listProperty(String.class).empty();
        featured = project.getObjects().property(Boolean.class).convention(false);
        primaryFile = project.getObjects().fileProperty();
        additionalFiles = project.getObjects().newInstance(AdditionalFilesExtension.class);
        syncBodyFrom = project.getObjects().property(String.class);
        debugMode = project.getObjects().property(Boolean.class).convention(false);
    }

    public void versionBundles(Action<VersionBundlesExtension> action) {
        action.execute(getVersionBundles());
    }

    public void additionalFiles(Action<? super AdditionalFilesExtension> action) {
        action.execute(getAdditionalFiles());
    }

    public void dependencies(Action<? super DependenciesExtension> action) {
        action.execute(getDependencies());
    }

    public Property<String> getToken() {
        return token;
    }

    public Property<String> getProjectId() {
        return projectId;
    }

    public Property<String> getVersionName() {
        return versionName;
    }

    public Property<String> getVersionNumber() {
        return versionNumber;
    }

    public Property<String> getChangelog() {
        return changelog;
    }

    public DependenciesExtension getDependencies() {
        return dependencies;
    }

    public ListProperty<String> getGameVersions() {
        return gameVersions;
    }

    public VersionBundlesExtension getVersionBundles() {
        return versionBundles;
    }

    public Property<String> getVersionType() {
        return versionType;
    }

    public ListProperty<String> getLoaders() {
        return loaders;
    }

    public Property<Boolean> getFeatured() {
        return featured;
    }

    public RegularFileProperty getPrimaryFile() {
        return primaryFile;
    }

    public AdditionalFilesExtension getAdditionalFiles() {
        return additionalFiles;
    }

    public Property<String> getSyncBodyFrom() {
        return syncBodyFrom;
    }

    public Property<Boolean> getDebugMode() {
        return debugMode;
    }

}
