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

import java.util.Optional;

import static java.util.Objects.hash;

public class LinePointerI implements LinePointer {
    public static LinePointer linePointer(Table context, int index) {
        return new LinePointerI(context, index);
    }

    private final Table context;
    private final int index;

    private LinePointerI(Table context, int index) {
        this.context = context;
        this.index = index;
    }

    @Override
    public Table context() {
        return context;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public Optional<Line> interpret(Table context) {
        if (context.rawLinesView().size() <= index) {
            return Optional.empty();
        }
        return Optional.ofNullable(context.getRawLine(index));
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof LinePointer) {
            final var other = (LinePointer) arg;
            return context().equals(other.context()) && index() == other.index();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return hash(index(), context());
    }
}
