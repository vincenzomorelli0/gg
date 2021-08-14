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
package net.splitcells.dem.data.atom;

import org.assertj.core.api.Assert;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Bools {
    private Bools() {
        throw constructorIllegal();
    }
    public static void require(boolean arg) {
        if (!arg) {
            throw new AssertionError();
        }
    }

    public static Bool bool(boolean arg) {
        return new BoolI(arg);
    }

    public static Bool truthful() {
        return new BoolI(true);
    }

    public static Bool untrue() {
        return new BoolI(false);
    }
}
