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

import masecla.modrinth4j.endpoints.project.ModifyProject;
import masecla.modrinth4j.main.ModrinthAPI;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.GSON;
import static com.github.aliaohaolong.mcmoddispatcher.ModrinthPublisher.userAgent;

public class ModrinthSyncBodyTask extends DefaultTask {

    @TaskAction
    public void run() {
        ModrinthExtension ext = getProject().getExtensions().getByType(ModrinthExtension.class);

        ModrinthAPI modrinthAPI = ModrinthAPI.rateLimited(userAgent(ext), ext.getToken().get());
        String id = ext.getProjectId().get();
        String slug = modrinthAPI.projects().get(id).join().getSlug();

        ModifyProject.ProjectModifications request = ModifyProject.ProjectModifications.builder()
                .body(ext.getSyncBodyFrom().get().replace("\r\n", "\n"))
                .build();

        // Debug
        if (ext.getDebugMode().get()) {
            getLogger().lifecycle("Full data to be sent for upload: {}", GSON.toJson(request));
            getLogger().lifecycle("Debug mode is enabled. Not going to upload this version.");
            return;
        }

        // Request
        modrinthAPI.projects().modify(id, request).join();

        getLogger().lifecycle(
                "Successfully uploaded body to {} ({}).",
                slug,
                id
        );

    }

}
