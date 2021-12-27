/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */
package net.splitcells.website;

import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.ProjectsRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Validates results rendered by {@link net.splitcells.website.server.project.Renderer#render}.
 */
@FunctionalInterface
public interface RenderingValidator {
    boolean validate(Optional<RenderingResult> content, ProjectsRenderer projectsRenderer, Path requestedPath);
}