package net.splitcells.website.server.renderer;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.Router;
import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.host.Files;
import net.splitcells.dem.resource.host.interaction.Domsole;
import net.splitcells.dem.resource.host.interaction.LogLevel;
import net.splitcells.website.server.Server;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Xml.textNode;
import static net.splitcells.dem.lang.namespace.NameSpaces.*;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.Paths.generateFolderPath;
import static net.splitcells.dem.resource.Paths.path;
import static net.splitcells.dem.resource.host.Files.createDirectory;
import static net.splitcells.dem.resource.host.Files.writeToFile;
import static net.splitcells.dem.resource.host.interaction.Domsole.domsole;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectsRenderer {

    public static ProjectsRenderer projectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        return new ProjectsRenderer(name, fallbackRenderer, renderers);
    }

    public void build() {
        final var generatedFiles = Paths.get("target", "generated");
        Files.createDirectory(generatedFiles);
        writeToFile(generatedFiles.resolve("layout." + profile + ".xml"), projectsLayout().toDom());
        generateFolderPath(Paths.get("target", "generated"));
    }

    public void serveTo(Path target) {
        build();
        projectsPaths().stream()
                .map(path -> "/" + path.toString())
                .map(path -> {
                    if (path.endsWith(".xml")) {
                        return path.substring(0, path.length() - 4) + ".html";
                    }
                    return path;
                })
                .forEach(path -> {
                    try {
                        final var targetPath = path(target, path.substring(1));
                        createDirectory(targetPath.getParent());
                        writeToFile(targetPath, render(path).get().getContent());
                    } catch (Exception e) {
                        throw new RuntimeException(target.toString() + path, e);
                    }
                });
    }

    public void serveToHttpAt(int port) {
        build();
        new Server().serveToHttpAt(port, requestPath -> render(requestPath));
    }

    public void serveAsAuthenticatedHttpsAt(int port) {
        build();
        new Server().serveAsAuthenticatedHttpsAt(port, requestPath -> render(requestPath));
    }

    @Deprecated
    private final String profile;
    private final List<ProjectRenderer> renderers;
    private final ProjectRenderer fallbackRenderer;

    private ProjectsRenderer(String name, ProjectRenderer fallbackRenderer, List<ProjectRenderer> renderers) {
        this.profile = name;
        this.fallbackRenderer = fallbackRenderer;
        this.renderers = renderers;
    }

    public Optional<RenderingResult> render(String path) {
        try {
            final var matchingRoots = renderers
                    .stream()
                    .filter(root -> path.startsWith(root.resourceRootPath()))
                    .collect(toList());
            if (matchingRoots.isEmpty()) {
                // System.out.println("No match for: " + path);
                //System.out.println("Patterns: " + renderers.keySet());
                return fallbackRenderer.render(path);
            }
            // System.out.println("Match for: " + path);
            // System.out.println("Match on: " + matchingRoots.get(0));
            // Sort descending.
            matchingRoots.sort((a, b) -> Integer.valueOf(a.resourceRootPath().length()).compareTo(b.resourceRootPath().length()));
            matchingRoots.reverse();
            final var renderingResult = matchingRoots.stream()
                    .map(renderer -> renderer.render(path))
                    .filter(Optional::isPresent)
                    .findFirst();
            if (renderingResult.isEmpty()) {
                domsole().append(textNode("Path could not be found: " + path), LogLevel.ERROR);
                return Optional.empty();
            }
            return renderingResult.get();
        } catch (Exception e) {
            throw new RuntimeException(path, e);
        }
    }

    public Set<Path> projectsPaths() {
        return renderers.stream()
                .map(renderer -> renderer.projectPaths())
                .reduce((a, b) -> a.with(b)).get();
    }

    public Perspective projectsLayout() {
        final var layout = perspective(VAL, NATURAL);
        renderers.forEach(renderer -> {
            var current = layout;
            for (final var element : list(renderer.resourceRootPath().split("/")).stream().filter(e -> !"".contentEquals(e)).collect(toList())) {
                final var children = s(current, element);
                final Perspective child;
                if (children.isEmpty()) {
                    child = perspective(VAL, NATURAL)
                            .withProperty(NAME, NATURAL, element);
                    current.withChild(child);
                } else {
                    child = children.get(0);
                }
                current = child;
            }
            if (!renderer.projectLayout().children().isEmpty()) {
                layout.withPath(renderer.projectLayout().children().get(0), NAME, NATURAL);
            }
        });
        layout.withProperty(NAME, NATURAL, "Path Context");
        return layout;
    }

    private List<Perspective> s(Perspective current, String element) {
        final var children = current.children().stream()
                .filter(child -> child.nameIs(VAL, NATURAL))
                .filter(child -> child.propertyInstances(NAME, NATURAL).stream()
                        .anyMatch(property -> property.value().get().name().equals(element)))
                .collect(toList());
        assertThat(children).hasSizeLessThan(2);
        return children;
    }
}
