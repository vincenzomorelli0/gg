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
package net.splitcells.dem.resource.communication.log;

import net.splitcells.dem.environment.resource.Console;
import net.splitcells.dem.environment.resource.ResourceI;
import net.splitcells.dem.resource.communication.interaction.Ui;

import static net.splitcells.dem.Dem.environment;
import static net.splitcells.dem.resource.communication.interaction.Pdsui.pdsui;

/**
 * <p>TODO Create compact and standardize log format.
 * Each message should only take up one line, so that filtering messages is easy.</p>
 * <p>TODO In the future, this should be a counter part of the web server.</p>
 * <p>TODO Message filtering and routing should be done by dedicated classes,
 * so that rendering can be separated from the rest.
 * </p>
 */
public class Domsole extends ResourceI<Ui> {
    public Domsole() {
        super(() -> pdsui(environment().config().configValue(Console.class)
                , environment().config().configValue(MessageFilter.class)));
    }

    public static Ui domsole() {
        return environment().config().configValue(Domsole.class);
    }
}