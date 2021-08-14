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
package net.splitcells.gel.data.allocations;

import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.allocation.AllocationsFactory;
import net.splitcells.gel.data.database.Database;

public class AllocationsIRefFactory implements AllocationsFactory {
    @Override
    public Allocations allocations(String name, Database demand, Database supply) {
        return new AllocationsIRef(name, demand, supply);    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
