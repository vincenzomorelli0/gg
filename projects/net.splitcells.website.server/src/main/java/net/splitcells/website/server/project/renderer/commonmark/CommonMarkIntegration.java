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
package net.splitcells.website.server.project.renderer.commonmark;

import net.splitcells.website.server.config.Context;
import net.splitcells.website.server.project.ProjectRenderer;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Optional;

import static net.splitcells.website.server.project.renderer.commonmark.LinkTranslator.linkTranslator;

public class CommonMarkIntegration {
    public static CommonMarkIntegration commonMarkIntegration() {
        return new CommonMarkIntegration();
    }

    final Parser parser = Parser.builder().build();
    final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private CommonMarkIntegration() {
    }

    public byte[] render(String arg, ProjectRenderer projectRenderer, String path, Context context) {
        final Optional<String> title;
        final String contentToRender;
        if (arg.startsWith("#")) {
            final var titleLine = arg.split("[\r\n]+")[0];
            title = Optional.of(titleLine.replaceAll("#", "").trim());
            contentToRender = arg.substring(titleLine.length());
        } else {
            title = Optional.empty();
            contentToRender = arg;
        }
        final var parsed = parser.parse(contentToRender);
        parsed.accept(linkTranslator());
        return projectRenderer
                .renderHtmlBodyContent(renderer.render(parsed)
                        , title
                        , Optional.of(path)
                        , context)
                .get();
    }
}
