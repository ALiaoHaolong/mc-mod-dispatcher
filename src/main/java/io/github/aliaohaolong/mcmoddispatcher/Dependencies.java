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

import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependencyType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to define dependencies for the mod.
 */
public class Dependencies {

    private final List<Dependency> dependencies;

    /**
     * Constructor
     */
    public Dependencies() {
        this.dependencies = new ArrayList<>();
    }

    /**
     * Gets the dependencies as a list.
     *
     * @return The dependencies as a list.
     */
    public List<Dependency> getAll() {
        return List.copyOf(dependencies);
    }

    /**
     * Adds a required dependency.
     *
     * @param projectIdOrSlug The project ID or slug.
     */
    public void required(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.REQUIRED, projectIdOrSlug));
    }

    /**
     * Adds an optional dependency.
     *
     * @param projectIdOrSlug The project ID or slug.
     */
    public void optional(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.OPTIONAL, projectIdOrSlug));
    }

    /**
     * Adds an incompatible dependency.
     *
     * @param projectIdOrSlug The project ID or slug.
     */
    public void incompatible(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.INCOMPATIBLE, projectIdOrSlug));
    }

    /**
     * Adds an embedded dependency.
     *
     * @param projectIdOrSlug The project ID or slug.
     */
    public void embedded(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.EMBEDDED, projectIdOrSlug));
    }

    /**
     * Adds a required dependency with a specific version.
     *
     * @param projectIdOrSlug The project ID or slug.
     * @param versionIdOrSlug The version ID or slug.
     */
    public void required(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.REQUIRED, projectIdOrSlug, versionIdOrSlug));
    }

    /**
     * Adds an optional dependency with a specific version.
     *
     * @param projectIdOrSlug The project ID or slug.
     * @param versionIdOrSlug The version ID or slug.
     */
    public void optional(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.OPTIONAL, projectIdOrSlug, versionIdOrSlug));
    }

    /**
     * Adds an incompatible dependency with a specific version.
     *
     * @param projectIdOrSlug The project ID or slug.
     * @param versionIdOrSlug The version ID or slug.
     */
    public void incompatible(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.INCOMPATIBLE, projectIdOrSlug, versionIdOrSlug));
    }

    /**
     * Adds an embedded dependency with a specific version.
     *
     * @param projectIdOrSlug The project ID or slug.
     * @param versionIdOrSlug The version ID or slug.
     */
    public void embedded(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.EMBEDDED, projectIdOrSlug, versionIdOrSlug));
    }

}
