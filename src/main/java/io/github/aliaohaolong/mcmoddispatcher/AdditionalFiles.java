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

import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to store additional files information.
 */
public class AdditionalFiles {

    private record InternalAdditionalFile(AdditionalFileType type, Provider<RegularFile> file) {
    }

    private final List<InternalAdditionalFile> internalAdditionalFiles;
    private final ProviderFactory providerFactory;

    /**
     * Constructor
     *
     * @param providerFactory The provider factory.
     */
    @Inject
    public AdditionalFiles(ProviderFactory providerFactory) {
        this.internalAdditionalFiles = new ArrayList<>();
        this.providerFactory = providerFactory;
    }

    /**
     * Get additional files as a list.
     *
     * @return A list of additional files.
     */
    public List<AdditionalFile> resolveAll() {
        return internalAdditionalFiles.stream()
                .map(i -> new AdditionalFile(i.type, i.file.get()))
                .toList();
    }

    /**
     * Add a sources jar file.
     *
     * @param file The file.
     */
    public void sourcesJar(final RegularFile file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.SOURCES_JAR, providerFactory.provider(() -> file)));
    }

    /**
     * Add a sources jar file.
     *
     * @param file The file.
     */
    public void sourcesJar(final Provider<RegularFile> file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.SOURCES_JAR, file));
    }

    /**
     * Add a dev jar file.
     *
     * @param file The file.
     */
    public void devJar(final RegularFile file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.DEV_JAR, providerFactory.provider(() -> file)));
    }

    /**
     * Add a dev jar file.
     *
     * @param file The file.
     */
    public void devJar(final Provider<RegularFile> file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.DEV_JAR, file));
    }

    /**
     * Add a Javadoc jar file.
     *
     * @param file The file.
     */
    public void javadocJar(final RegularFile file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.JAVADOC_JAR, providerFactory.provider(() -> file)));
    }

    /**
     * Add a Javadoc jar file.
     *
     * @param file The file.
     */
    public void javadocJar(final Provider<RegularFile> file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.JAVADOC_JAR, file));
    }

    /**
     * Add a signature file.
     *
     * @param file The file.
     */
    public void signature(final RegularFile file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.SIGNATURE, providerFactory.provider(() -> file)));
    }

    /**
     * Add a signature file.
     *
     * @param file The file.
     */
    public void signature(final Provider<RegularFile> file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.SIGNATURE, file));
    }

    /**
     * Add another file.
     *
     * @param file The file.
     */
    public void other(final RegularFile file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.OTHER, providerFactory.provider(() -> file)));
    }

    /**
     * Add another file.
     *
     * @param file The file.
     */
    public void other(final Provider<RegularFile> file) {
        internalAdditionalFiles.add(new InternalAdditionalFile(AdditionalFileType.OTHER, file));
    }

}
