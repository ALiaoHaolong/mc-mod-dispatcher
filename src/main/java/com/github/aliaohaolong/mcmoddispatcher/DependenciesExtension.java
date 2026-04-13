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

import lombok.Getter;
import masecla.modrinth4j.model.version.ProjectVersion.ProjectDependencyType;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("ClassCanBeRecord")
public class DependenciesExtension {

    private final NamedDomainObjectContainer<Dependency> dependencies;

    @Inject
    public DependenciesExtension(final Project project) {
        this.dependencies = project.getObjects().domainObjectContainer(Dependency.class);
    }

    public List<Dependency> getDependenciesAsList() {
        return new ArrayList<>(this.dependencies);
    }

    public void required(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.REQUIRED, projectIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void optional(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.OPTIONAL, projectIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void incompatible(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.INCOMPATIBLE, projectIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void embedded(final String projectIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.EMBEDDED, projectIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void required(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.REQUIRED, projectIdOrSlug, versionIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void optional(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.OPTIONAL, projectIdOrSlug, versionIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void incompatible(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.INCOMPATIBLE, projectIdOrSlug, versionIdOrSlug));
    }

    @SuppressWarnings("unused")
    public void embedded(final String projectIdOrSlug, final String versionIdOrSlug) {
        dependencies.add(new Dependency(ProjectDependencyType.EMBEDDED, projectIdOrSlug, versionIdOrSlug));
    }

}
