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

import net.splitcells.dem.data.set.Set;
import net.splitcells.website.server.processor.BinaryMessage;

import java.nio.file.Path;
import java.util.Optional;

/**
 * <p>TODO This interface is deprecated.
 * Some of its methods will have to be moved into {@link ProjectRenderer}.</p>
 * <p>Provides a path system, where every object is mapped to a path and the content of the object can be queried
 * by the path.
 * Note that there is no guarantee,
 * that accessing an object does not change it.</p>
 */
@Deprecated
public interface Renderer {

    /**
     * All project paths are returned as relative paths.
     * The context of the paths is {@link #resourceRootPath2()}
     * 
     * Note that from the project's perspective all {@link #projectPaths}
     * can be viewed as absolute,
     * because the project itself does not contain paths,
     * that are not a child of {@link #resourceRootPath2()}.
     *
     * 
     * @return This is list of all path, that can be rendered.
     */
    Set<Path> projectPaths();

    /**
     * All project paths are returned as relative paths.
     * The context of the paths is {@link #resourceRootPath2()}
     *
     * @return This is list of all path,
     * that can be rendered and are relevant for normal users.
     */
    Set<Path> relevantProjectPaths();

    /**
     * TODO Use {@link Path} objects instead of {@link String}s.
     *
     * @param path             Absolute Path To Be Rendered
     * @return This is the rendering result, if the path is supported.
     */
    @Deprecated
    Optional<BinaryMessage> render(String path);

    /**
     * TODO Use {@link Path} object instead of {@link String}.
     * TODO We assume that resource root paths are absolute and therefore start with a slash.
     * <p>
     * This can be considered the address of the renderer,
     * that identifies the renderer.
     *
     * @return Prefix of all paths, supported by the renderer.
     */
    @Deprecated
    String resourceRootPath();

    default Path resourceRootPath2() {
        return Path.of(resourceRootPath());
    }
}
