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

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public enum AdditionalFileType {

    @SerializedName("")
    OTHER,

    @SerializedName("sources-jar")
    SOURCES_JAR,

    @SerializedName("dev-jar")
    DEV_JAR,

    @SerializedName("javadoc-jar")
    JAVADOC_JAR,

    @SerializedName("signature")
    SIGNATURE;

    @Override
    public String toString() {
        if (this == OTHER) {
            return null;
        } else {
            return this.name().toLowerCase(Locale.ROOT).replace('_', '-');
        }
    }
}
