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
package net.splitcells.gel.rating.type;

import static net.splitcells.dem.data.order.Comparators.ASCENDING_BOOLEANS;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.perspective.PerspectiveI.perspective;

import java.util.Optional;

import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.utils.CommonFunctions;
import net.splitcells.gel.rating.framework.Rating;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.data.order.Comparator;
import net.splitcells.dem.data.order.Ordering;

/**
 * Signals with {@link #value} equals true,
 * that the rating subject complies with all constraints.
 * Otherwise, the {@link #value} is equals false.
 */
public class Compliance implements Rating {
    private static final Comparator<Boolean> COMPARATOR = ASCENDING_BOOLEANS;
    private boolean value;

    public static Compliance compliance(boolean value) {
        return new Compliance(value);
    }

    protected Compliance(boolean value) {
        this.value = value;
    }

    @Override
    public Optional<Ordering> compare_partially_to(Rating arg) {
        if (arg instanceof Compliance) {
            return Optional.of(COMPARATOR.compareTo(value, ((Compliance) arg).value));
        }
        throw new IllegalArgumentException(arg.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Compliance combine(Rating... additionalRatings) {
        if (additionalRatings[0] instanceof Compliance) {
            return compliance(value && ((Compliance) additionalRatings[0]).value);
        }
        throw new IllegalArgumentException(list(additionalRatings).toString());
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof Compliance) {
            return this.value == ((Compliance) arg).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return CommonFunctions.hashCode(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R extends Rating> R _clone() {
        return (R) new Compliance(value);
    }

    @Override
    public boolean betterThan(Rating rating) {
        return greaterThan(rating);
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren(this.getClass().getSimpleName());
        dom.appendChild(Xml.textNode("" + value));
        return dom;
    }

    @Override
    public Perspective toPerspective() {
        return perspective(this.getClass().getSimpleName()).withChild(perspective("" + value));
    }
}
