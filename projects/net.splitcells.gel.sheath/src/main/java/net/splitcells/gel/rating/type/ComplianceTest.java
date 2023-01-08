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
package net.splitcells.gel.rating.type;

import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.order.Ordering.*;
import static net.splitcells.gel.rating.type.Compliance.compliance;
import static org.assertj.core.api.Assertions.assertThat;

public class ComplianceTest {

    @Test
    public void testEquality() {
        assertThat(compliance(false)).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(false))).isEqualTo(compliance(false));
        assertThat(compliance(false).combine(compliance(true))).isEqualTo(compliance(false));
        assertThat(compliance(true).combine(compliance(false))).isNotEqualTo(compliance(true));
    }

    @Test
    public void testCombinationsAndOrderConsistency() {
        assertThat(compliance(false).combine(compliance(false)).compare_partially_to(compliance(false)).orElseThrow())
                .isEqualTo(EQUAL);
        assertThat(compliance(false).combine(compliance(true)).compare_partially_to(compliance(false)).orElseThrow())
                .isEqualTo(EQUAL);
        assertThat(compliance(true).combine(compliance(false)).compare_partially_to(compliance(true)).orElseThrow())
                .isNotEqualTo(EQUAL);
    }

    @Test
    public void testOrderConsistency() {
        assertThat(compliance(true).compare_partially_to(compliance(true)).orElseThrow()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(false)).orElseThrow()).isEqualTo(EQUAL);
        assertThat(compliance(false).compare_partially_to(compliance(true)).orElseThrow()).isEqualTo(LESSER_THAN);
        assertThat(compliance(true).compare_partially_to(compliance(false)).orElseThrow()).isEqualTo(GREATER_THAN);
    }

}
