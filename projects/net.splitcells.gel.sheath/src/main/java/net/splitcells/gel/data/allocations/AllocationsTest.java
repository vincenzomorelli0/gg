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

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.TestSuiteI;
import net.splitcells.gel.data.database.Databases;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.testing.Assertions.requireEquals;
import static net.splitcells.dem.testing.Assertions.requireNotNull;
import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;
import static net.splitcells.gel.data.allocation.Allocationss.allocations;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AllocationsTest extends TestSuiteI {

    @Tag(UNIT_TEST)
    @Test
    public void test_subscribe_to_afterAdditions() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscribeToAfterAdditions(
                allocation -> requireNotNull(allocations.demandOfAllocation(allocation)));
        allocations.allocate
                (demands.addTranslated(list())
                        , supplies.addTranslated(list()));
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_beforeRemoval() {
        final var demands = Databases.database();
        final var supplies = Databases.database();
        final var allocations = allocations("test", demands, supplies);
        allocations.subscribeToBeforeRemoval
                (allocation -> requireNotNull(allocations.demandOfAllocation(allocation)));
        allocations.remove(
                allocations.allocate
                        (demands.addTranslated(list())
                                , supplies.addTranslated(list()))
        );
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_subscriber_to_afterRemoval() {
        assertThrows(Exception.class, () -> {
            final var demands = Databases.database();
            final var supplies = Databases.database();
            final var allocations = allocations("test", demands, supplies);
            allocations.subscribeToAfterRemoval
                    (allocation -> requireNotNull(allocations.demandOfAllocation(allocation)));
            allocations.remove(
                    allocations.allocate
                            (demands.addTranslated(list())
                                    , supplies.addTranslated(list()))
            );
        });
    }

    @Tag(UNIT_TEST)
    @Test
    public void test_allocate_and_remove() {
        final var demandAttribute = attribute(Double.class);
        final var demands = Databases.database(demandAttribute);
        demands.addTranslated(list(1.0));
        final var supplyAttribute = attribute(Integer.class);
        final var supplies = Databases.database(supplyAttribute);
        supplies.addTranslated(list(2));
        final var testSubject = allocations("test", demands, supplies);
        testSubject.headerView().requireEqualityTo(list(demands.headerView().get(0), supplies.headerView().get(0)));
        testSubject.demands().lines().requireSizeOf(1);
        testSubject.supplies().lines().requireSizeOf(1);
        demands.addTranslated(list(3.0));
        supplies.addTranslated(list(4));
        testSubject.demands().lines().requireSizeOf(2);
        testSubject.supplies().lines().requireSizeOf(2);
        final var firstAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(0)
                        , testSubject.supplies().rawLinesView().get(0));
        requireEquals(firstAllocation.value(demandAttribute), 1.0);
        requireEquals(firstAllocation.value(supplyAttribute), 2);
        final var secondAllocation = testSubject
                .allocate(testSubject.demands().rawLinesView().get(1)
                        , testSubject.supplies().rawLinesView().get(1));
        requireEquals(secondAllocation.value(demandAttribute), 3.0);
        requireEquals(secondAllocation.value(supplyAttribute), 4);
        requireEquals(testSubject.size(), 2);
        testSubject.remove(firstAllocation);
        requireEquals(testSubject.size(), 1);
    }
}
