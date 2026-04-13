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

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jspecify.annotations.NonNull;

public class ModrinthPlugin implements Plugin<Project> {

    @Override
    public void apply(@NonNull Project project) {
        // 注册 Extension
        project.getExtensions().create("modrinth", ModrinthExtension.class, project);
        project.getExtensions().create("support", SupportExtension.class, project);

        // 注册 Task
        project.getTasks().register("modrinth", ModrinthTask.class, task -> {
            task.setGroup("publish");
            task.setDescription("Upload project to Modrinth");
            task.notCompatibleWithConfigurationCache("idk");
        });
        project.getTasks().register("modrinthBody", ModrinthBodyTask.class, task -> {
            task.setGroup("publish");
            task.setDescription("Upload body to Modrinth");
            task.notCompatibleWithConfigurationCache("idk");
        });
    }

}
