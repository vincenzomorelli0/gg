package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.FileStructureTransformer;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static java.nio.charset.StandardCharsets.UTF_8;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/xml/" of the project's folder.
 * The projected path's replaces the "xml" file suffix with "html".
 * All files need to end with ".xml".
 */
public class XmlProjectRendererExtension implements ProjectRendererExtension {
    public static XmlProjectRendererExtension xmlRenderer(FileStructureTransformer renderer) {
        return new XmlProjectRendererExtension(renderer);
    }

    private final FileStructureTransformer renderer;

    private XmlProjectRendererExtension(FileStructureTransformer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith(".html")) {
            final var xmlFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/xml")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
            if (Files.is_file(xmlFile)) {
                final var document = Xml.parse(readString(xmlFile));
                if (NameSpaces.SEW.uri().equals(document.getDocumentElement().getNamespaceURI())) {
                    final var metaElement = optionalDirectChildElementsByName(document.getDocumentElement(), "meta", NameSpaces.SEW)
                            .orElseGet(() -> {
                                final var newMeta = document.createElementNS(NameSpaces.SEW.uri(), "meta");
                                document.getDocumentElement().appendChild(newMeta);
                                return newMeta;
                            });
                    if (optionalDirectChildElementsByName(document.getDocumentElement(), "path", NameSpaces.SEW).isEmpty()) {
                        final var pathElement = document.createElementNS(NameSpaces.SEW.uri(), "path");
                        pathElement.appendChild(document.createTextNode(path));
                        metaElement.appendChild(pathElement);
                    }
                }
                return Optional.of(renderingResult(renderer
                                .transform(Xml.toDocumentString(document)).getBytes(UTF_8)
                        , TEXT_HTML.toString()));
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("xml");
        // TODO Move this code block into a function, in order to avoid
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

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}