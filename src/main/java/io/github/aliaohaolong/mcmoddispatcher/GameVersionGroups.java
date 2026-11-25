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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to define the game version groups.
 */
public class GameVersionGroups {

    private final List<List<String>> gameVersionGroups;

    /**
     * Create a new GameVersionGroups.
     */
    public GameVersionGroups() {
        this.gameVersionGroups = new ArrayList<>();
    }

    /**
     * Get the game version groups.
     *
     * @return The game version groups.
     */
    public List<List<String>> getAll() {
        return List.copyOf(gameVersionGroups);
    }

    /**
     * Add a game version group.
     *
     * @param gameVersions The Minecraft versions.
     */
    public void gameVersions(String... gameVersions) {
        gameVersionGroups.add(List.of(gameVersions));
    }

}
