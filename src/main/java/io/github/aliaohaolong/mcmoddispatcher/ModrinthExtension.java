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

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.util.List;

/**
 * Through this extension, you can configure the Modrinth plugin in your build.gradle file.
 */
public class ModrinthExtension {

    private static final String CURRENT_VERSIONS_HINT =
            "modrinth.currentFirstVersion and modrinth.currentGameVersions can only be read while tasks are being generated. Wrap it in a provider, for example: "
                    + "versionName = provider { \"MyMod ${project.version} for Fabric ${modrinth.currentFirstVersion}\" }";

    private final ListProperty<String> currentGameVersions;

    private final Property<String> token;
    private final Property<String> projectId;
    private final Property<CharSequence> versionName;
    private final Property<CharSequence> versionNumber;
    private final Property<String> changelog;
    private final Dependencies dependencies;
    private final GameVersionGroups gameVersionGroups;
    private final Property<String> versionType;
    private final ListProperty<String> loaders;
    private final Property<Boolean> featured;
    private final RegularFileProperty primaryFile;
    private final AdditionalFiles additionalFiles;
    private final Property<String> syncBodyFrom;
    private final Property<Boolean> debugMode;

    /**
     * Create a new ModrinthExtension.
     *
     * @param project The project.
     */
    public ModrinthExtension(Project project) {
        currentGameVersions = project.getObjects().listProperty(String.class).empty();

        token = project.getObjects().property(String.class).convention(project.getProviders().environmentVariable("MODRINTH_TOKEN"));
        projectId = project.getObjects().property(String.class);
        versionName = project.getObjects().property(CharSequence.class);
        versionNumber = project.getObjects().property(CharSequence.class);
        changelog = project.getObjects().property(String.class);
        dependencies = project.getObjects().newInstance(Dependencies.class);
        gameVersionGroups = project.getObjects().newInstance(GameVersionGroups.class);
        versionType = project.getObjects().property(String.class).convention("release");
        loaders = project.getObjects().listProperty(String.class).empty();
        featured = project.getObjects().property(Boolean.class).convention(false);
        primaryFile = project.getObjects().fileProperty();
        additionalFiles = project.getObjects().newInstance(AdditionalFiles.class);
        syncBodyFrom = project.getObjects().property(String.class);
        debugMode = project.getObjects().property(Boolean.class).convention(false);
    }

    /**
     * Set the current game versions.
     */
    void setCurrentGameVersions(List<String> currentGameVersions) {
        this.currentGameVersions.set(currentGameVersions);
    }

    /**
     * Get the first current game version.
     *
     * @return The first current game version.
     */
    public String getCurrentFirstVersion() {
        List<String> currentGameVersionsAsList = currentGameVersions.get();
        if (currentGameVersionsAsList.isEmpty())
            throw new IllegalStateException(CURRENT_VERSIONS_HINT);
        return currentGameVersionsAsList.getFirst();
    }

    /**
     * Get the current game versions.
     *
     * @return The current game versions.
     */
    public List<String> getCurrentGameVersions() {
        List<String> currentGameVersionsAsList = currentGameVersions.get();
        if (currentGameVersionsAsList.isEmpty())
            throw new IllegalStateException(CURRENT_VERSIONS_HINT);
        return currentGameVersionsAsList;
    }

    /**
     * Configure the dependencies.
     *
     * @param action The action to execute.
     */
    public void dependencies(Action<? super Dependencies> action) {
        action.execute(dependencies);
    }

    /**
     * Configure the game version groups.
     *
     * @param action The action to execute.
     */
    public void gameVersionGroups(Action<? super GameVersionGroups> action) {
        action.execute(gameVersionGroups);
    }

    /**
     * Configure the additional files.
     *
     * @param action The action to execute.
     */
    public void additionalFiles(Action<? super AdditionalFiles> action) {
        action.execute(additionalFiles);
    }

    /**
     * Get the token.
     *
     * @return The token.
     */
    public Property<String> getToken() {
        return token;
    }

    /**
     * Get the project ID.
     *
     * @return The project ID.
     */
    public Property<String> getProjectId() {
        return projectId;
    }

    /**
     * Get the version name.
     *
     * @return The version name.
     */
    public Property<CharSequence> getVersionName() {
        return versionName;
    }

    /**
     * Get the version number.
     *
     * @return The version number.
     */
    public Property<CharSequence> getVersionNumber() {
        return versionNumber;
    }

    /**
     * Get the changelog.
     *
     * @return The changelog.
     */
    public Property<String> getChangelog() {
        return changelog;
    }

    /**
     * Get the dependencies.
     *
     * @return The dependencies.
     */
    public Dependencies getDependencies() {
        return dependencies;
    }

    /**
     * Get the game version groups.
     *
     * @return The game version groups.
     */
    public GameVersionGroups getGameVersionGroups() {
        return gameVersionGroups;
    }

    /**
     * Get the version type.
     *
     * @return The version type.
     */
    public Property<String> getVersionType() {
        return versionType;
    }

    /**
     * Get the loaders.
     *
     * @return The loaders.
     */
    public ListProperty<String> getLoaders() {
        return loaders;
    }

    /**
     * Get whether the version is featured.
     *
     * @return Whether the version is featured.
     */
    public Property<Boolean> getFeatured() {
        return featured;
    }

    /**
     * Get the primary file.
     *
     * @return The primary file.
     */
    public RegularFileProperty getPrimaryFile() {
        return primaryFile;
    }

    /**
     * Get the additional files.
     *
     * @return The additional files.
     */
    public AdditionalFiles getAdditionalFiles() {
        return additionalFiles;
    }

    /**
     * Get the file to sync the changelog from.
     *
     * @return The file to sync the changelog from.
     */
    public Property<String> getSyncBodyFrom() {
        return syncBodyFrom;
    }

    /**
     * Get whether to enable debug mode.
     *
     * @return Whether to enable debug mode.
     */
    public Property<Boolean> getDebugMode() {
        return debugMode;
    }

}
