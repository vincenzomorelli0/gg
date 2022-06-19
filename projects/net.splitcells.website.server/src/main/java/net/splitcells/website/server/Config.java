package net.splitcells.website.server;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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

    private Optional<String> sslKeystorePassword = Optional.of("password");
    private Optional<Path> sslKeystoreFile = Optional.of(Paths.get("target/keystore.p12"));
    private Optional<String> layout = Optional.empty();
    private Optional<String> layoutRelevant = Optional.empty();
    private int openPort = 443;
    private String generationStyle = "standard";
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
    private String rootIndex = "/index.html";
    /**
     * List of paths, that are equivalent to {@link #rootIndex}.
     */
    private List<String> possibleRootIndex = Lists.list//
            (rootIndex//
                    , "index.html" // Browser (Firefox) like to call this path, if no path is provided by the user.
                    , ""//
                    , "/");

    /**
     * States whether {@link net.splitcells.website.server.project.Renderer}s may cache certain parts,
     * like the output and styling information, or not.
     */
    private boolean cacheRenderers = false;

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
        this.cacheRenderers =  cacheRenderers;
        return this;
    }
    
    public boolean cacheRenderers() {
        return cacheRenderers;
    }
}