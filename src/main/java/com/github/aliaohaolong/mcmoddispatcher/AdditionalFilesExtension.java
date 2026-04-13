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
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AdditionalFilesExtension {

    private final List<AdditionalFile> additionalFiles;

    @Inject
    public AdditionalFilesExtension() {
        this.additionalFiles = new  ArrayList<>();
    }

    public List<AdditionalFile> getAdditionalFilesAsList() {
        return new ArrayList<>(this.additionalFiles);
    }

    public void sourcesJar(final Provider<RegularFile> file) {
        additionalFiles.add(new AdditionalFile(AdditionalFileType.SOURCES_JAR, file));
    }

    @SuppressWarnings("unused")
    public void devJar(final Provider<RegularFile> file) {
        additionalFiles.add(new AdditionalFile(AdditionalFileType.DEV_JAR, file));
    }

    @SuppressWarnings("unused")
    public void javadocJar(final Provider<RegularFile> file) {
        additionalFiles.add(new AdditionalFile(AdditionalFileType.JAVADOC_JAR, file));
    }

    @SuppressWarnings("unused")
    public void signature(final Provider<RegularFile> file) {
        additionalFiles.add(new AdditionalFile(AdditionalFileType.SIGNATURE, file));
    }

    @SuppressWarnings("unused")
    public void other(final Provider<RegularFile> file) {
        additionalFiles.add(new AdditionalFile(AdditionalFileType.OTHER, file));
    }

}
