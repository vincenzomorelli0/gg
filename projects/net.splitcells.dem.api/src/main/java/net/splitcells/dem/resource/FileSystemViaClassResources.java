/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import net.splitcells.dem.utils.StringUtils;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Files.readAsString;
import static net.splitcells.dem.resource.Files.walk_recursively;
import static net.splitcells.dem.utils.ExecutionException.executionException;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.dem.utils.StringUtils.removePrefix;

/**
 * Provides an {@link FileSystem} API for {@link Class#getResource(String)}.
 */
@JavaLegacyArtifact
public class FileSystemViaClassResources implements FileSystemView {
    public static FileSystemView fileSystemViaClassResources(Class<?> clazz) {
        return new FileSystemViaClassResources(clazz, "");
    }

    private final String prefix;
    private final Class<?> clazz;

    private FileSystemViaClassResources(Class<?> clazz, String prefix) {
        this.clazz = clazz;
        this.prefix = prefix;
    }

    @Override
    public InputStream inputStream(Path path) {
        return clazz.getResourceAsStream("/" + prefix + path.toString());
    }

    @Override
    public String readString(Path path) {
        return readAsString(clazz.getResourceAsStream("/" + prefix + path.toString()));
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isFile(Path path) {
        return clazz.getResourceAsStream("/" + prefix + path.toString()) != null;
    }

    @Override
    public boolean isDirectory(Path path) {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively() {
        throw notImplementedYet();
    }

    @Override
    public Stream<Path> walkRecursively(Path path) {
        try {
            final var resourcePath = clazz.getClassLoader().getResource(prefix + path + "/");
            if (resourcePath == null) {
                return Stream.empty();
            }
            if ("jar".equals(resourcePath.getProtocol())) {
                try {
                    final var pathStr = prefix + path.toString();
                    final var dirURL = FileSystemViaClassResources.class.getResource("/net/");
                    final var jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
                    final var jarEntries = new JarFile(URLDecoder.decode(jarPath, UTF_8))
                            .entries()
                            .asIterator();
                    final List<Path> walk = list();
                    while (jarEntries.hasNext()) {
                        walk.withAppended(Path.of((jarEntries.next().getRealName())));
                    }
                    /*
                     * If the resources are loaded from a jar, the `META-INF` folder is actively filtered afterwards,
                     * in order to avoid walking through it, even it is not request.
                     * For example, without this hack requesting `net/splitcells/` would result in getting
                     * `META-INF` and `META-INF/MANIFEST.MF` as well, even though it was not requested.
                     */
                    return walk.stream().filter(w -> w.startsWith(pathStr))
                            .map(w -> Path.of(w.toString().substring(prefix.length())));
                } catch (Throwable e) {
                    throw executionException(e);
                }
            } else {
                final var rootPathStr = Path.of(clazz.getClassLoader().getResource(prefix + "./").toURI())
                        .toString()
                        .replace("test-classes", "classes")
                        + "/";
                final var startPath = Path.of(clazz.getClassLoader().getResource(prefix + path + "/").toURI());
                return walk_recursively(startPath).map(p -> Path.of(removePrefix(rootPathStr, p.toString())));
            }
        } catch (Throwable e) {
            throw executionException(e);
        }
    }

    @Override
    public byte[] readFileAsBytes(Path path) {
        throw notImplementedYet();
    }

    public FileSystemView subFileSystemView(String path) {
        return new FileSystemViaClassResources(clazz, (prefix + path + "/").replaceAll("//", "/"));
    }
}
