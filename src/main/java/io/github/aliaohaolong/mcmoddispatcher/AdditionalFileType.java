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

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

/**
 * This enum represents the additional file types of a mod.
 */
public enum AdditionalFileType {

    /**
     * Other additional file type.
     */
    @SerializedName("")
    OTHER,

    /**
     * Sources jar file type.
     */
    @SerializedName("sources-jar")
    SOURCES_JAR,

    /**
     * Development jar file type.
     */
    @SerializedName("dev-jar")
    DEV_JAR,

    /**
     * Javadoc jar file type.
     */
    @SerializedName("javadoc-jar")
    JAVADOC_JAR,

    /**
     * Signature file type.
     */
    @SerializedName("signature")
    SIGNATURE;

    /**
     * Returns the string representation of this enum constant.
     *
     * @return the string representation of this enum constant
     */
    @Override
    public String toString() {
        if (this == OTHER) {
            return null;
        } else {
            return this.name().toLowerCase(Locale.ROOT).replace('_', '-');
        }
    }
}
