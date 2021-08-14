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
package net.splitcells.gel.data.table;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.database.DatabaseI;
import net.splitcells.gel.data.database.DatabaseSF;
import net.splitcells.gel.data.database.Databases;
import net.splitcells.gel.data.table.attribute.Attribute;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

public class TableSF {

    /**
     * TODO Support different table type mixing, in order to create {@link Allocations} instances.
     */
    public static List<Table> tukšaTabulas() {
        return DatabaseSF.emptyDatabase2().mapped(a -> (Table) a);

    }

    public static List<Function<List<Attribute<? extends Object>>, Table>> testTableFactory() {
        return list((a) -> Databases.database(a));
    }
}
