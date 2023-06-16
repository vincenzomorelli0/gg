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
package net.splitcells.gel.constraint;

import net.splitcells.dem.testing.annotations.UnitTest;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.gel.Gel.defineProblem;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.rater.lib.classification.ForAllAttributeValues.forAllAttributeValues;

public class QueryTest {
    @UnitTest
    public void testConstraintPath() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testData = defineProblem("testConstraintPath")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(d).forAll(s).forAll(d);
                    return r;
                }).toProblem()
                .asSolution();
        final var testProduct = testData.constraint().query().forAll(d).forAll(s).forAll(d).constraintPath();
        testProduct.assertEquals(list(testData.constraint()
                , testData.constraint().childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0)
                , testData.constraint().childrenView().get(0).childrenView().get(0).childrenView().get(0)));
    }

    @UnitTest
    public void testForWithMultipleClassifiers() {
        final var d = attribute(Integer.class, "d");
        final var s = attribute(Integer.class, "s");
        final var testSubject = defineProblem("testForWithMultipleClassifiers")
                .withDemandAttributes(d)
                .withNoDemands()
                .withSupplyAttributes(s)
                .withNoSupplies()
                .withConstraint(r -> {
                    r.forAll(list(forAllAttributeValues(d), forAllAttributeValues(s))).forAll(forAllAttributeValues(d));
                    return r;
                }).toProblem()
                .asSolution();
        final var testResult = testSubject.constraint();
        testResult.childrenView().requireSizeOf(2);
        testResult.childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(1).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(1);
        testResult.childrenView().get(0).childrenView().get(0).childrenView().get(0).childrenView().requireSizeOf(0);
    }

}
