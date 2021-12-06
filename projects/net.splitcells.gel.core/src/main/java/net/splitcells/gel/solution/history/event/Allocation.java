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
package net.splitcells.gel.solution.history.event;

import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.lang.Xml;
import net.splitcells.gel.data.table.Line;
import org.w3c.dom.Node;

import static net.splitcells.gel.common.Language.*;

public class Allocation implements Domable {
    private final AllocationChangeType tips;
    private final Line prasība;
    private final Line piedāvājums;

    public static Allocation allocations(AllocationChangeType tips, Line prasība, Line piedāvājums) {
        return new Allocation(tips, prasība, piedāvājums);
    }

    private Allocation(AllocationChangeType tips, Line prasība, Line piedāvājums) {
        this.tips = tips;
        this.prasība = prasība;
        this.piedāvājums = piedāvājums;

    }

    public AllocationChangeType type() {
        return tips;
    }

    public Line demand() {
        return prasība;
    }

    public Line supply() {
        return piedāvājums;
    }

    @Override
    public Node toDom() {
        final var piešķiršana = Xml.elementWithChildren(ALLOCATION.value());
        piešķiršana.appendChild
                (Xml.elementWithChildren(TYPE.value()).appendChild(Xml.textNode(tips.name())));
        piešķiršana.appendChild
                (Xml.elementWithChildren(DEMAND2.value()).appendChild(prasība.toDom()));
        piešķiršana.appendChild
                (Xml.elementWithChildren(SUPPLY.value()).appendChild(piedāvājums.toDom()));
        return piešķiršana;
    }

    @Override
    public String toString() {
        return Xml.toPrettyString(toDom());
    }
}
