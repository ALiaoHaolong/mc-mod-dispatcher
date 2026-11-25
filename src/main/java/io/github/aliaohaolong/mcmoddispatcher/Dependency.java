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

import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.version.ProjectVersion;
import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependency;
import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependencyType;

/**
 * A dependency for a mod.
 *
 * @param dependencyType  The type of the dependency.
 * @param projectIdOrSlug The project id or slug of the dependency.
 * @param versionIdOrSlug The version id or slug of the dependency.
 */
public record Dependency(
        ProjectDependencyType dependencyType,
        String projectIdOrSlug,
        String versionIdOrSlug
) {

    /**
     * Creates a new dependency.
     *
     * @param dependencyType  The type of the dependency.
     * @param projectIdOrSlug The project id or slug of the dependency.
     */
    public Dependency(ProjectDependencyType dependencyType, String projectIdOrSlug) {
        this(dependencyType, projectIdOrSlug, null);
    }

    /**
     * Converts this dependency to a project dependency.
     *
     * @param modrinthAPI The Modrinth API client to use.
     * @return The project dependency.
     */
    public ProjectDependency toProjectDependency(ModrinthAPI modrinthAPI) {
        String projectId;
        String versionId = null;
        if (versionIdOrSlug == null) {
            projectId = modrinthAPI.projects().getProjectIdBySlug(this.projectIdOrSlug).join();
        } else {
            ProjectVersion version = modrinthAPI.versions().getVersionByNumber(this.projectIdOrSlug, this.versionIdOrSlug).join();
            projectId = version.getProjectId();
            versionId = version.getId();
        }
        if (projectId == null) {
            throw new NullPointerException("Failed to resolve project id for " + this.projectIdOrSlug);
        }
        if (versionIdOrSlug != null && versionId == null) {
            throw new NullPointerException("Failed to resolve version id for " + this.versionIdOrSlug + " (" + this.projectIdOrSlug + ")");
        }
        return new ProjectDependency(versionId, projectId, null, this.dependencyType);
    }

}
