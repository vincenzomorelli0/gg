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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.testing.Assertions;
import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.listOfShallowCopies;
import static net.splitcells.dem.testing.Assertions.requireEquals;

public class ListTest {
    @UnitTest
    public void testWithRemovedUntilExcludedIndex() {
        list(1, 2, 3, 4, 5, 6, 7).withRemovedUntilExcludedIndex(3).requireEqualityTo(list(1, 2));
    }

    @UnitTest
    public void testGetRemovedUntilExcludedIndex() {
        list(1, 2, 3, 4, 5, 6, 7, 8, 9).withRemovedUntilExcludedIndex(6).requireEqualityTo(list(1, 2, 3, 4, 5));
    }

    @UnitTest
    public void testListOfShallowCopies() {
        requireEquals(listOfShallowCopies(list(1, 2), 3), list(list(1, 2), list(1, 2), list(1, 2)));
    }
}
