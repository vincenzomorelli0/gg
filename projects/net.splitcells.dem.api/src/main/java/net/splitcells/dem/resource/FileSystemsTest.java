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

import net.splitcells.dem.testing.annotations.IntegrationTest;

import java.io.IOException;
import java.nio.file.Path;

import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.utils.ExecutionException.executionException;

public class FileSystemsTest {
    @IntegrationTest
    public void testInputStream() {
        final var testData = "78t789tb912dfrf";
        Files.processInTemporaryFolder(path -> {
            final var testSubject = fileSystemOnLocalHost(path);
            testSubject.writeToFile(Path.of("testData.txt"), testData);
            try {
                requireEquals(new String(testSubject.inputStream(Path.of("testData.txt")).readAllBytes())
                        , testData);
            } catch (IOException e) {
                throw executionException(e);
            }
        });
    }
}
