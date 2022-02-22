package net.splitcells.website.server.project.renderer.commonmark;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.config.Context;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import net.splitcells.website.server.project.renderer.Renderer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;
import static net.splitcells.website.server.project.renderer.commonmark.CommonMarkIntegration.commonMarkIntegration;

public class CommonMarkRenderer implements Renderer {
    public static CommonMarkRenderer commonMarkExtension() {
        return new CommonMarkRenderer();
    }

    private final CommonMarkIntegration renderer = commonMarkIntegration();

    private CommonMarkRenderer() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Context context) {
        if (path.endsWith(".html")) {
            final var commonMarkFile = projectRenderer.projectFolder().resolve("src/main").resolve("md")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".md");
            if (Files.is_file(commonMarkFile)) {
                final var pathContent = readString(commonMarkFile);
                return Optional.of(renderingResult(renderer.render(pathContent, projectRenderer, path, context)
                        , TEXT_HTML.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("md");
        if (Files.isDirectory(sourceFolder)) {
            try {
                java.nio.file.Files.walk(sourceFolder)
                        .filter(java.nio.file.Files::isRegularFile)
                        .map(file -> sourceFolder.relativize(
                                file.getParent()
                                        .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                                (file.getFileName().toString()) + ".html")))
                        .forEach(projectPaths::addAll);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return projectPaths;
    }
}
