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
package net.splitcells.dem.utils;

import net.splitcells.dem.data.set.list.Lists;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.MathUtils.sumsForTarget;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MathUtilsTest {

    @Test
    public void testSumsForTargetWithOneResult() {
        assertThat(sumsForTarget(1, list(1))).isEqualTo(list(list(1)));
    }

    @Test
    public void testSumsForTargetWithMultipleResults() {
        assertThat(sumsForTarget(2, list(1))).isEqualTo(list(list(1, 1)));
        assertThat(sumsForTarget(3, list(1, 2, 3)))
                .isEqualTo(list
                        (list(1, 1, 1)
                                , list(1, 2)
                                , list(2, 1)
                                , list(3)));
    }
}
