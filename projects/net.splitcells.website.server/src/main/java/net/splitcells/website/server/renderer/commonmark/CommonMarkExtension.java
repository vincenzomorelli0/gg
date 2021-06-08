package net.splitcells.website.server.renderer.commonmark;

import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.website.server.renderer.ProjectRenderer;
import net.splitcells.website.server.renderer.ProjectRendererExtension;
import net.splitcells.website.server.renderer.RenderingResult;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.nio.file.Path;
import java.util.Optional;

import static io.vertx.core.http.HttpHeaders.TEXT_HTML;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.resource.host.Files.is_file;
import static net.splitcells.website.server.renderer.RenderingResult.renderingResult;
import static net.splitcells.website.server.renderer.commonmark.CommonMarkRenderer.commonMarkRenderer;

public class CommonMarkExtension implements ProjectRendererExtension {

    public static CommonMarkExtension commonMarkExtension() {
        return new CommonMarkExtension();
    }

    private final CommonMarkRenderer renderer = commonMarkRenderer();

    private CommonMarkExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer) {
        if (path.endsWith("README.html") && is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            final var pathContent = readString(projectRenderer.projectFolder().resolve("README.md"));
            final Optional<String> title;
            final String contentToRender;
            if (pathContent.startsWith("#")) {
                final var titleLine = pathContent.split("[\r\n]+")[0];
                title = Optional.of(titleLine.replaceAll("#", "").trim());
                contentToRender = pathContent.substring(titleLine.length());
            } else {
                title = Optional.empty();
                contentToRender = pathContent;
            }
            return projectRenderer.renderHtmlBodyContent(renderer.render(contentToRender), title)
                    .map(result -> renderingResult
                            (result
                                    , TEXT_HTML.toString()));
        }
        return Optional.empty();
    }

    @Override
    public Perspective extendProjectLayout(Perspective layout, ProjectRenderer projectRenderer) {
        if (is_file(projectRenderer.projectFolder().resolve("README.md"))) {
            projectRenderer.extendPerspectiveWithPath(layout
                    , Path.of(projectRenderer.resourceRootPath().substring(1)).resolve("README.html"));
        }
        return layout;
    }
}
