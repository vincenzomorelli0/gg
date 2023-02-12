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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.environment.config.StaticFlags;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * Provides one interface to multiple {@link ProjectRendererExtension}.
 * When a query is requested a matching {@link ProjectRendererExtension} is queried.
 * </p>
 * <p>
 * Only one {@link ProjectRendererExtension} is allowed to matched at a time.
 * </p>
 */
public class ProjectRendererExtensionMerger implements ProjectRendererExtension {
    public static ProjectRendererExtensionMerger rendererMerger() {
        return new ProjectRendererExtensionMerger();
    }

    private final List<ProjectRendererExtension> projectRendererExtensions = list();

    private ProjectRendererExtensionMerger() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        final var rendering = projectRendererExtensions.stream()
                .map(e -> e.renderFile(path, projectRenderer, config))
                .filter(e -> e.isPresent())
                .collect(Lists.toList());
        if (rendering.size() > 1) {
            final var matchedExtensions = projectRendererExtensions.stream()
                    .filter(r -> r.renderFile(path, projectRenderer, config).isPresent())
                    .map(Object::toString)
                    .reduce((a, b) -> a + ", " + b)
                    .orElseThrow();
            throw new RuntimeException("Multiple matches are present: "
                    + projectRenderer.resourceRootPath2().toString()
                    + ": "
                    + matchedExtensions);
        }
        if (rendering.isEmpty()) {
            return Optional.empty();
        } else {
            return rendering.get(0);
        }
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        projectRendererExtensions.forEach(e -> e.extendProjectLayout(layout, projectRenderer));
        return layout;
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> projectPaths = setOfUniques();
        projectRendererExtensions.forEach(e -> {
            final var path = e.projectPaths(projectRenderer);
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                if (path.toString().startsWith("/")) {
                    throw new IllegalStateException("Absolute project paths are not allowed: " + path.toString());
                }
            }
            projectPaths.addAll(path);
        });
        return projectPaths;
    }

    @ReturnsThis
    public ProjectRendererExtensionMerger registerExtension(ProjectRendererExtension extension) {
        projectRendererExtensions.add(extension);
        return this;
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        final Set<Path> relevantProjectPaths = setOfUniques();
        projectRendererExtensions.forEach(e -> {
            final var path = e.relevantProjectPaths(projectRenderer);
            if (StaticFlags.ENFORCING_UNIT_CONSISTENCY) {
                if (path.toString().startsWith("/")) {
                    throw new IllegalStateException("Absolute project paths are not allowed: " + path.toString());
                }
            }
            relevantProjectPaths.addAll(path);
        });
        return relevantProjectPaths;
    }
}