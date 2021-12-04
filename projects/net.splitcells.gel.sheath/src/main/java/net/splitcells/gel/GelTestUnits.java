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
package net.splitcells.gel;

import net.splitcells.dem.resource.communication.log.MessageFilter;

import static net.splitcells.dem.resource.Files.writeToFile;
import static net.splitcells.dem.testing.Test.testUnits;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;
import static net.splitcells.gel.GelEnv.standardDeveloperConfigurator;

public final class GelTestUnits {
    private GelTestUnits() {
        throw constructorIllegal();
    }

    public static void main(String... arg) {
        GelEnv.process(() -> {
                    if (!testUnits()) {
                        System.exit(1);
                    }
                }
                , standardDeveloperConfigurator().andThen(env -> {
                    env.config().withConfigValue(MessageFilter.class, a -> false);
                }));
    }
}
