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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import masecla.modrinth4j.client.agent.UserAgent;
import masecla.modrinth4j.main.ModrinthAPI;

/**
 * This class is used to store some static methods.
 */
final class ModrinthApiFactory {

    static final Gson DEBUG_GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    static ModrinthAPI api(String token, String projectId) {
        return ModrinthAPI.rateLimited(userAgent(projectId), token);
    }

    private static UserAgent userAgent(String projectId) {
        return UserAgent.builder()
                .authorUsername("LiaoHaolong")
                .projectName("mc-mod-dispatcher")
                .projectVersion(ModrinthTask.class.getPackage().getImplementationVersion())
                .contact(projectId)
                .build();
    }

}
