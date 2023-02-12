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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Node;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.nio.file.Files.createDirectories;
import static net.splitcells.dem.lang.Xml.toPrettyString;
import static net.splitcells.dem.utils.ExecutionException.executionException;

/**
 * Some additional methods for the java.nio.file.Files class.
 */
@JavaLegacyArtifact
public interface Files {

    static void createDirectory(Path directory) {
        try {
            createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void ensureAbsence(Path path) {
        if (isDirectory(path)) {
            deleteDirectory(path);
        }
    }

    static boolean is_file(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }

    static Stream<Path> walkDirectChildren(Path path) {
        try {
            return java.nio.file.Files.walk(path, 1).filter(e -> !path.equals(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Path> walk_recursively(Path path) {
        try {
            return java.nio.file.Files.walk(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isDirectory(Path path) {
        return java.nio.file.Files.isDirectory(path);
    }

    /**
     * IDEA Create method to move path to trash instead of deleting it.
     */
    static void deleteDirectory(Path dirToDelete) {
        try {
            FileUtils.deleteDirectory(dirToDelete.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void copyDirectory(Path sourceDirectory, Path targetDirectory) {
        try {
            FileUtils.copyDirectory(sourceDirectory.toFile(), targetDirectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(Path path, Node node) {
        writeToFile(path, toPrettyString(node));
    }

    static void writeToFile(Path path, byte[] content) {
        try (final var writer = new FileOutputStream(path.toFile())) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    static void writeToFile(Path path, String string) {
        writeToFile(path, string.getBytes());
    }

    /**
     * TODO How do we handle new line symbols on all platforms?
     *
     * @return New Line Symbol
     */
    static String newLine() {
        return "\n";
    }

    static void appendToFile(Path path, byte[] content) {
        try (final var writer = new FileOutputStream(path.toFile(), true)) {
            writer.write(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String readFileAsString(Path path) {
        try {
            return java.nio.file.Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] readFileAsBytes(Path path) {
        try {
            return java.nio.file.Files.readAllBytes(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static InputStream newInputStream(Path path) {
        try {
            return java.nio.file.Files.newInputStream(path);
        } catch (IOException e) {
            throw executionException(e);
        }
    }

    static boolean fileExists(Path path) {
        return java.nio.file.Files.isRegularFile(path);
    }
}
