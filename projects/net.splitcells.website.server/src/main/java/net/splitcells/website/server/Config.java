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
package net.splitcells.website.server;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.resource.Trail;
import net.splitcells.website.server.processor.Processor;
import net.splitcells.website.server.processor.ProcessorRegistry;
import net.splitcells.website.server.projects.ProjectsRenderer;
import net.splitcells.website.server.projects.extension.ProjectsRendererExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.setOfUniques;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listWithValuesOf;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;
import static net.splitcells.website.server.processor.ProcessorRegistry.binaryProcessorRegistry;
import static net.splitcells.website.server.processor.BinaryMessage.binaryMessage;

/**
 * TODO IDEA Use string and enum based mapping as a backend,
 * so that clones can easily be created.
 * This would also make serialization easier.
 * Maybe create a serialization mini framework for that?
 */
public class Config {
    public static Config create() {
        return new Config();
    }

    private boolean isSecured = true;

    private Optional<String> sslKeystorePassword = Optional.of("password");
    private Optional<Path> sslKeystoreFile = Optional.of(Paths.get("target/keystore.p12"));

    /**
     * TODO Use variable for storing the contents of the XSD in order to be file system independent,
     * instead of using a path to the XSD.
     */
    @Deprecated
    private Optional<Path> xmlSchema = Optional.of(net.splitcells.dem.resource.Paths.path("src/main/xsd/den.xsd"));

    /**
     * Points to the folder containing projects required for data, like XSL transformations.
     * These are needed in order to render the website.
     */
    private Optional<Path> mainProjectRepositoryPath = Optional.empty();

    /**
     * This is an XML document provided for the XSL based renderer.
     * It contains all paths supported by the renderer.
     */
    @Deprecated
    private Optional<String> layout = Optional.empty();
    /**
     * This is an XML document provided for the XSL based renderer.
     * It contains all paths supported by the renderer, that are relevant to the targeted common user.
     */
    @Deprecated
    private Optional<String> layoutRelevant = Optional.empty();
    /**
     * <p>This is a tree structure containing all paths supported by the renderer.</p>
     * <p>Replaces {@link #layout}.</p>
     */
    private Optional<Perspective> layoutPerspective = Optional.empty();

    /**
     * <p>This is a tree structure containing all paths supported by the renderer,
     * that are relevant to the targeted common user.</p>
     * <p>Replaces {@link #layoutRelevant}.</p>
     */
    private Optional<Perspective> layoutRelevantPerspective = Optional.empty();
    private int openPort = 443;
    private String generationStyle = "standard";
    /**
     * Lists all CSS files, that should be used for the layouts instead of the standard ones, used by the layout.
     */
    private List<String> cssFiles = list();
    /**
     * Lists all JavaScript files, that should be used for the layouts.
     * These JavaScripts will be placed at the end of the layout and therefore loaded last.
     * These files are loaded in the given order.
     */
    private List<String> jsBackgroundFiles = list();
    /**
     * TODO This does not seem to be used actively anymore.
     * It was used for some specific resources,
     * but this way of resource loading should probably not be used anymore,
     * as it is unnecessarily hard to maintain.
     */
    @Deprecated
    private Optional<String> siteFolder;
    /**
     * All calls of {@link net.splitcells.website.server.projects.ProjectsRenderer#render(String)}
     * with a path not starting with {@link #rootPath} will not return anything.
     */
    private String rootPath = "/";
    /**
     * This is the root index file of the website.
     */
    private String rootIndex = "/index.html";
    /**
     * List of paths, that are equivalent to {@link #rootIndex}.
     * Browsers like Firefox like to call <q>[...]/index.html</q> instead of <q>[...]/</q>.
     */
    private List<String> possibleRootIndex = list(rootIndex
            , "index.html"
            , ""
            , "/");

    /**
     * States whether {@link net.splitcells.website.server.project.Renderer}s may cache certain parts,
     * like the output and styling information, or not.
     */
    private boolean cacheRenderers = false;

    /**
     * Determines whether it can be assumed, that {@link ProjectsRenderer#projectsPaths()} changes.
     * Otherwise, {@link ProjectsRenderer#projectsPaths()} can be cached, for increased performance,
     * which is useful, when a static website is rendered.
     */
    private boolean mutableProjectsPath = true;

    /**
     * Contains the content of the detailed menu for each page.
     * The content should be processable by XSL.
     */
    private Optional<String> detailedXslMenu = Optional.of("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<xsl:stylesheet version=\"2.0\" xmlns:s=\"http://splitcells.net/sew.xsd\"\n"
            + "                xmlns:svg=\"http://www.w3.org/2000/svg\"\n"
            + "                xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:x=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:d=\"http://splitcells.net/den.xsd\"\n"
            + "                xmlns:p=\"http://splitcells.net/private.xsd\"\n"
            + "                xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\n"
            + "                xmlns:r=\"http://splitcells.net/raw.xsd\"\n"
            + "                xmlns:n=\"http://splitcells.net/natural.xsd\"\n"
            + "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
            + "                xmlns:xl=\"http://www.w3.org/1999/xlink\"\n"
            + "                xmlns:ns=\"http://splitcells.net/namespace.xsd\">\n"
            + "   <xsl:variable name=\"net-splitcells-website-server-config-menu-detailed\"/>\n"
            + "</xsl:stylesheet>");

    /**
     * Defines the content of the menu for the main tab on the page.
     */
    private Optional<String> xslWindowMenu = Optional.of("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<xsl:stylesheet version=\"2.0\" xmlns:s=\"http://splitcells.net/sew.xsd\"\n"
            + "                xmlns:svg=\"http://www.w3.org/2000/svg\"\n"
            + "                xmlns=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:x=\"http://www.w3.org/1999/xhtml\"\n"
            + "                xmlns:d=\"http://splitcells.net/den.xsd\"\n"
            + "                xmlns:p=\"http://splitcells.net/private.xsd\"\n"
            + "                xmlns:m=\"http://www.w3.org/1998/Math/MathML\"\n"
            + "                xmlns:r=\"http://splitcells.net/raw.xsd\"\n"
            + "                xmlns:n=\"http://splitcells.net/natural.xsd\"\n"
            + "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\n"
            + "                xmlns:xl=\"http://www.w3.org/1999/xlink\"\n"
            + "                xmlns:ns=\"http://splitcells.net/namespace.xsd\">\n"
            + "   <xsl:variable name=\"net-splitcells-website-server-config-window-menu\"/>\n"
            + "</xsl:stylesheet>");

    private List<ProjectConfig> additionalProjects = list();

    private List<ProgramConfig> programConfigs = list();
    private List<ProjectsRendererExtension> projectsRendererExtension = list();

    private ProcessorRegistry<Perspective, Perspective> processor = binaryProcessorRegistry();
    /*new BinaryProcessor() {
        @Override
        public BinaryResponse process(BinaryRequest request) {
            final var response = binaryResponse();
            response.data().put(PRIMARY_TEXT_RESPONSE
                    , binaryMessage(perspective("no-response")
                                    .toJsonString()
                                    .getBytes(StandardCharsets.UTF_8)
                            , JSON));
            return response;
        }
    };*/

    private Config() {
    }

    public Config withLayout(String layout) {
        this.layout = Optional.of(layout);
        return this;
    }

    public Optional<String> layout() {
        return layout;
    }

    public Config withOpenPort(int openPort) {
        this.openPort = openPort;
        return this;
    }

    public int openPort() {
        return openPort;
    }

    public String generationStyle() {
        return generationStyle;
    }

    public Config withGenerationStyle(String arg) {
        generationStyle = arg;
        return this;
    }

    public Optional<String> sslKeystorePassword() {
        return sslKeystorePassword;
    }

    public Optional<Path> sslKeystoreFile() {
        return sslKeystoreFile;
    }

    public Config witSslKeystoreFile(Optional<Path> sslKeystoreFile) {
        this.sslKeystoreFile = sslKeystoreFile;
        return this;
    }

    @Deprecated
    public Optional<String> siteFolder() {
        return siteFolder;
    }

    @Deprecated
    public Config withSiteFolder(Optional<String> siteFolder) {
        this.siteFolder = siteFolder;
        return this;
    }

    public Config withLayoutRelevant(String layoutRelevant) {
        this.layoutRelevant = Optional.of(layoutRelevant);
        return this;
    }

    public Optional<String> layoutRelevant() {
        return layoutRelevant;
    }

    public Config withRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }

    public String rootPath() {
        return rootPath;
    }

    public String rootIndex() {
        return rootIndex;
    }

    public List<String> possibleRootIndex() {
        return possibleRootIndex;
    }

    public Config withCacheRenderers(boolean cacheRenderers) {
        this.cacheRenderers = cacheRenderers;
        return this;
    }

    public boolean cacheRenderers() {
        return cacheRenderers;
    }

    public Optional<Perspective> layoutPerspective() {
        return layoutPerspective;
    }

    public Optional<Perspective> layoutPerspectiveSubtree(List<String> path) {
        return layoutPerspective;
    }

    @ReturnsThis
    public Config withLayoutPerspective(Optional<Perspective> arg) {
        layoutPerspective = arg;
        return this;
    }

    public Optional<Perspective> layoutRelevantPerspective() {
        return layoutRelevantPerspective;
    }

    @ReturnsThis
    public Config withLayoutRelevantPerspective(Optional<Perspective> arg) {
        layoutRelevantPerspective = arg;
        return this;
    }

    public boolean mutableProjectsPath() {
        return mutableProjectsPath;
    }

    @ReturnsThis
    public Config withMutableProjectsPath(boolean arg) {
        mutableProjectsPath = arg;
        return this;
    }

    @ReturnsThis
    public Config withMainProjectRepositoryPath(Path arg) {
        mainProjectRepositoryPath = Optional.of(arg);
        return this;
    }

    public Optional<Path> mainProjectRepositoryPath() {
        return mainProjectRepositoryPath;
    }

    @ReturnsThis
    public Config withDetailedXslMenu(Optional<String> arg) {
        detailedXslMenu = arg;
        return this;
    }

    public Optional<String> detailedXslMenu() {
        return detailedXslMenu;
    }

    @ReturnsThis
    public Config withCssFiles(List<String> arg) {
        cssFiles = arg;
        return this;
    }

    @ReturnsThis
    public Config withAdditionalCssFile(String arg) {
        cssFiles.add(arg);
        return this;
    }

    public List<String> cssFiles() {
        return cssFiles;
    }

    public boolean isSecured() {
        return isSecured;
    }

    @ReturnsThis
    public Config withIsSecured(boolean arg) {
        isSecured = arg;
        return this;
    }

    @ReturnsThis
    public Config withXmlSchema(Optional<Path> arg) {
        xmlSchema = arg;
        return this;
    }

    @Deprecated
    public Optional<Path> xmlSchema() {
        return xmlSchema;
    }

    public List<ProjectConfig> additionalProjects() {
        return additionalProjects;
    }

    public Config withAdditionalProject(ProjectConfig project) {
        additionalProjects.add(project);
        return this;
    }

    public Config withJsBackgroundFiles(List<String> args) {
        jsBackgroundFiles = args;
        return this;
    }

    public Config withAdditionalJsBackgroundFiles(String arg) {
        jsBackgroundFiles.add(arg);
        return this;
    }

    public List<String> jsBackgroundFiles() {
        return jsBackgroundFiles;
    }

    public Config withXslWindowMenu(Optional<String> arg) {
        this.xslWindowMenu = arg;
        return this;
    }

    public Optional<String> xslWindowMenu() {
        return xslWindowMenu;
    }

    public List<ProgramConfig> programConfigs() {
        return programConfigs;
    }

    public Config withAdditionalProgramConfig(ProgramConfig programConfig) {
        programConfigs.add(programConfig);
        return this;
    }

    public Config clearAdditionalProgramConfigs() {
        programConfigs.clear();
        return this;
    }

    public Config withAdditionalProjectsRendererExtension(ProjectsRendererExtension arg) {
        projectsRendererExtensions().add(arg);
        return this;
    }

    public List<ProjectsRendererExtension> projectsRendererExtensions() {
        return projectsRendererExtension;
    }

    public ProcessorRegistry<Perspective, Perspective> processor() {
        return processor;
    }

    public Config withAdditionalProcessor(Trail trail, Processor<Perspective, Perspective> arg) {
        processor.register(trail, arg);
        return this;
    }
}