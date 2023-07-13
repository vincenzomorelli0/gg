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
package net.splitcells.website.server.project;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.resource.FileSystem;
import net.splitcells.dem.resource.FileSystems;
import net.splitcells.dem.resource.Paths;
import net.splitcells.dem.resource.communication.log.LogLevel;
import net.splitcells.website.server.project.validator.SourceValidator;
import net.splitcells.website.server.translation.to.html.XslTransformer;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static net.splitcells.dem.lang.namespace.NameSpaces.STRING;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.FileSystems.fileSystemOnLocalHost;
import static net.splitcells.dem.resource.Files.newInputStream;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.communication.log.Domsole.domsole;
import static net.splitcells.dem.utils.CommonFunctions.appendToFile;
import static net.splitcells.website.server.translation.to.html.PathBasedUriResolver.pathBasedUriResolver;

public class FileStructureTransformer {

    public static FileStructureTransformer fileStructureTransformer(Path fileStructureRoot
            , Path xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        return new FileStructureTransformer(fileStructureRoot, xslLibs, transformerXsl, sourceValidator, config);
    }

    private final Path fileStructureRoot;
    private final Path loggingProject = Paths.path(System.getProperty("user.home")
            + "/connections/tmp.storage/net.splitcells.dem");
    private final SourceValidator sourceValidator;
    private final Path xslLibs;
    private final String transformerXsl;
    private final Function<String, Optional<String>> config;
    private final FileSystem xslLibsFileSystem;

    private FileStructureTransformer(Path fileStructureRoot
            , Path xslLibs
            , String transformerXsl
            , SourceValidator sourceValidator
            , Function<String, Optional<String>> config) {
        this.fileStructureRoot = fileStructureRoot;
        this.sourceValidator = sourceValidator;
        this.xslLibs = xslLibs;
        this.transformerXsl = transformerXsl;
        this.config = config;
        xslLibsFileSystem = fileSystemOnLocalHost(xslLibs);
    }

    public String transform(List<String> path) {
        return transform(Paths.path(fileStructureRoot, path));
    }

    public String transform(Path file) {

        sourceValidator.validate(file).ifPresent(error -> {
            final var loggingFolder = loggingProject.resolve("src/main/txt")
                    .resolve(fileStructureRoot.relativize(file).getParent());
            generateFolderPath(loggingFolder);
            appendToFile(loggingFolder.resolve(file.getFileName() + ".errors.txt"), error);
            domsole().append(perspective(error, STRING), LogLevel.ERROR);
        });
        return new String(transformer().transform(file));
    }

    /**
     * TODO Create an option, to enable or disable this, for better performance.
     * <p>
     * Creates a new XslTransformer, so that the XSL-files are reloaded
     * during each rendering.
     *
     * @return
     */
    private XslTransformer transformer() {
        try {
            return new XslTransformer
                    (xslLibsFileSystem.inputStream(Paths.path(transformerXsl))
                            , pathBasedUriResolver(xslLibsFileSystem, config::apply));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String transform(String content) {
        return transformer().transform(content);
    }
}
