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
package net.splitcells.website.server.project.renderer.extension;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.Sets;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.namespace.NameSpaces;
import net.splitcells.dem.resource.Files;
import net.splitcells.dem.utils.StreamUtils;
import net.splitcells.website.server.Config;
import net.splitcells.website.server.project.LayoutConfig;
import net.splitcells.website.server.project.ProjectRenderer;
import net.splitcells.website.server.project.RenderingResult;
import org.w3c.dom.Document;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.environment.resource.Console.CONSOLE_FILE_NAME;
import static net.splitcells.dem.lang.Xml.optionalDirectChildElementsByName;
import static net.splitcells.dem.lang.perspective.Den.subtree;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.dem.resource.ContentType.HTML_TEXT;
import static net.splitcells.dem.resource.Files.is_file;
import static net.splitcells.dem.resource.Paths.readString;
import static net.splitcells.dem.utils.StreamUtils.emptyStream;
import static net.splitcells.website.server.project.LayoutConfig.layoutConfig;
import static net.splitcells.website.server.project.RenderingResult.renderingResult;

/**
 * Projects the file tree located "src/main/xml/" of the project's folder.
 * The projected path's replaces the "xml" file suffix with "html".
 * All files need to end with ".xml".
 */
public class XmlProjectRendererExtension implements ProjectRendererExtension {

    private static final String MARKER = "834ZT09345ZHGF09H3457G90H34";

    public static XmlProjectRendererExtension xmlRenderer() {
        return new XmlProjectRendererExtension();
    }

    private XmlProjectRendererExtension() {

    }

    @Override
    public Optional<RenderingResult> renderFile(String path, ProjectRenderer projectRenderer, Config config) {
        if (path.endsWith(".html")) {
            final var xmlFile = projectRenderer
                    .projectFolder()
                    .resolve("src/main/xml")
                    .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
            final var pathFolder = StreamUtils.stream(path.split("/"))
                    .filter(s -> !s.isEmpty())
                    .collect(toList())
                    .withRemovedFromBehind(0);
            final var layoutConfig = layoutConfig(path)
                    .withLocalPathContext(config.layoutPerspective()
                            .map(l -> subtree(l, pathFolder)));
            if (is_file(xmlFile)) {
                return renderFile(path, readString(xmlFile), projectRenderer, config, layoutConfig);
            } else {
                final var sumXmlFile = projectRenderer
                        .projectFolder()
                        .resolve("src/main/sum.xml")
                        .resolve(path.substring(0, path.lastIndexOf(".html")) + ".xml");
                if (is_file(sumXmlFile)) {
                    return renderFile(path, "<start xmlns=\"http://splitcells.net/den.xsd\">" + readString(sumXmlFile) + "</start>", projectRenderer, config, layoutConfig);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<RenderingResult> renderFile(String path
            , String xmlContent
            , ProjectRenderer projectRenderer
            , Config config
            , LayoutConfig layoutConfig) {
        final var document = Xml.parse(xmlContent);
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
            metaElement.appendChild(document.createTextNode(MARKER));
            final String documentString;
            final var localPathContext = layoutConfig.localPathContext();
            if (localPathContext.isPresent()) {
                documentString = Xml.toDocumentString(document)
                        .replace(MARKER, "<path.context " +
                                "xmlns:d=\"http://splitcells.net/den.xsd\"" +
                                ">" +
                                localPathContext.get().toXmlStringWithPrefixes() +
                                "</path.context>");
            } else {
                documentString = Xml.toDocumentString(document);
            }
            return Optional.of(renderingResult(projectRenderer.renderRawXml(documentString, config).orElseThrow()
                    , HTML_TEXT.codeName()));
        } else {
            return Optional.of(renderingResult(projectRenderer.renderXml(xmlContent, layoutConfig, config).orElseThrow()
                    , HTML_TEXT.codeName()));
        }
    }

    @Override
    public Set<Path> projectPaths(ProjectRenderer projectRenderer) {
        final var projectPaths = Sets.<Path>setOfUniques();
        final var sourceFolder = projectRenderer.projectFolder().resolve("src/main").resolve("xml");
        // TODO Move this code block into a function, in order to avoid
        if (Files.isDirectory(sourceFolder)) {
            Files.walk_recursively(sourceFolder)
                    .filter(Files::fileExists)
                    .map(file -> sourceFolder.relativize(
                            file.getParent()
                                    .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                            (file.getFileName().toString()) + ".html")))
                    .forEach(projectPaths::addAll);
        }
        projectSumXlPaths(projectRenderer).forEach(projectPaths::addAll);
        return projectPaths;
    }

    private Stream<Path> projectSumXlPaths(ProjectRenderer projectRenderer) {
        final var sumXmlFolder = projectRenderer.projectFolder().resolve("src/main").resolve("sum.xml");
        if (Files.isDirectory(sumXmlFolder)) {
            return Files.walk_recursively(sumXmlFolder)
                    .filter(Files::fileExists)
                    .map(file -> sumXmlFolder.relativize(
                            file.getParent()
                                    .resolve(net.splitcells.dem.resource.Paths.removeFileSuffix
                                            (file.getFileName().toString()) + ".html")));
        }
        return emptyStream();
    }

    @Override
    public Set<Path> relevantProjectPaths(ProjectRenderer projectRenderer) {
        return projectPaths(projectRenderer);
    }
}
