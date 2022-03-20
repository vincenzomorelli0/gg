package net.splitcells.website.server.projects;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;

import java.nio.file.Path;
import java.util.Optional;

public interface ProjectsRenderer {
    void build();

    void serveTo(Path target);

    void serveToHttpAt();

    void serveAsAuthenticatedHttpsAt();

    Optional<RenderingResult> render(String path);

    Set<Path> projectsPaths();

    Set<Path> relevantProjectsPaths();

    Config config();

    List<ProjectRenderer> projectRenderers();

    Optional<byte[]> renderHtmlBodyContent(String bodyContent
            , Optional<String> title
            , Optional<String> path
            , Config config);
}