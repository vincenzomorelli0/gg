/*
 * Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the MIT License,
 * which is available at https://spdx.org/licenses/MIT.html.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * or any later versions with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT OR GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package net.splitcells.gel.solution.optimization.primitive;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.solution.optimization.OptimizationEvent;
import org.junit.jupiter.api.Test;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.gel.constraint.type.ForAlls.forAllWithValue;
import static net.splitcells.gel.constraint.type.ForAlls.for_all;
import static net.splitcells.gel.constraint.type.Then.then;
import static net.splitcells.gel.data.table.attribute.AttributeI.attribute;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.solution.SolutionBuilder.defineProblem;
import static net.splitcells.gel.solution.optimization.OptimizationEvent.optimizationEvent;
import static net.splitcells.gel.solution.optimization.StepType.ADDITION;
import static net.splitcells.gel.solution.optimization.primitive.ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair;
import static net.splitcells.gel.solution.optimization.primitive.LinearInitialization.linearInitialization;
import static org.assertj.core.api.Assertions.assertThat;

public class ConstraintGroupbasedrepairTest {

    @Test
    public void test_repair_of_defying_group() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var invalidValueA = 1;
        final var invalidValueB = 3;
        final var validValue = 5;
        final var defyingGroupA = then(cost(1));
        final var defyingGroupB = then(cost(1));
        @SuppressWarnings("unchecked") final var solution
                = defineProblem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(invalidValueA, 1)
                                , list(invalidValueA, 1)
                                , list(invalidValueA, 2)
                                , list(invalidValueA, 2)
                                , list(2, invalidValueB)
                                , list(2, invalidValueB)
                                , list(validValue, validValue))
                .withSupplyAttributes()
                .withSupplies
                        (list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                        )
                .withConstraint
                /**
                 * Needless constraints are added, in order to check, if the correct {@link Constraint} is selected.
                 */
                        (for_all().withChildren
                                (forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(a, invalidValueA).withChildren(defyingGroupA)
                                        , forAllWithValue(b, invalidValueB).withChildren(defyingGroupB)
                                        , forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        final var testSubject = ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(
                constraintGroup -> list(constraintGroup.get(6)) // Select the first defying group.
                , (freeDemandGroups, freedSupplies) -> currentSolution -> {
                    final List<OptimizationEvent> repairs = list();
                    final int i[] = {0};
                    freeDemandGroups.entrySet().forEach(freeGroup -> {
                        freeGroup.getValue().forEach(freeDemand -> {
                            repairs.add(
                                    optimizationEvent(
                                            ADDITION
                                            , freeDemand.toLinePointer()
                                            , currentSolution.suppliesFree().getLines().get(i[0]++).toLinePointer()
                                    ));
                        });
                    });
                    return repairs;
                }
        );
        final var groupsOfConstraintGroup = testSubject.groupOfConstraintGroup(solution);
        final var demandClassifications = groupsOfConstraintGroup
                .stream()
                .map(e -> e
                        .lastValue()
                        .map(f -> testSubject.demandGrouping(f, solution))
                        .orElseGet(() -> map()))
                .collect(toList());
        final var testProduct = testSubject.repair(solution, demandClassifications.get(0), list());
        assertThat(testProduct).hasSize(4);
        final var freeSupplyIndexes = testProduct.stream()
                .map(optimizationEvent -> optimizationEvent.supply().index())
                .collect(toList());
        assertThat(freeSupplyIndexes).contains(7, 8, 9, 10);
        final var demandIndexes = testProduct.stream()
                .map(optimizationEvent -> optimizationEvent.demand().index())
                .collect(toList());
        assertThat(demandIndexes).contains(0, 1, 2, 3);
    }

    @Test
    public void test_removal_of_defying_group() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var invalidValueA = 1;
        final var invalidValueB = 3;
        final var validValue = 5;
        final var defyingConstraintA = then(cost(1));
        final var defyingConstraintB = then(cost(1));
        @SuppressWarnings("unchecked") final var solution
                = defineProblem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(invalidValueA, 1)
                                , list(invalidValueA, 1)
                                , list(invalidValueA, 2)
                                , list(invalidValueA, 2)
                                , list(2, invalidValueB)
                                , list(2, invalidValueB)
                                , list(validValue, validValue))
                .withSupplyAttributes()
                .withSupplies
                        (list(), list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list())
                .withConstraint
                        (for_all().withChildren
                                (forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(a, invalidValueA).withChildren(defyingConstraintA)
                                        , forAllWithValue(b, invalidValueB).withChildren(defyingConstraintB)
                                        , forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        assertThat(solution.getLines()).hasSize(7);

        final var testSubject = ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(0);
        solution.optimize(testSubject.freeDefyingGroupOfConstraintGroup(solution, defyingConstraintA));
        assertThat(solution.getLines()).hasSize(3);
        solution.optimize(testSubject.freeDefyingGroupOfConstraintGroup(solution, defyingConstraintB));
        assertThat(solution.getLines()).hasSize(1);
    }

    @Test
    public void test_demandGrouping() {
        final var a = attribute(Integer.class, "a");
        final var b = attribute(Integer.class, "b");
        final var invalidValueA = 1;
        final var invalidValueB = 3;
        final var validValue = 5;
        final var defyingConstraintA = then(cost(1));
        final var defyingConstraintB = then(cost(1));
        @SuppressWarnings("unchecked") final var solution
                = defineProblem()
                .withDemandAttributes(a, b)
                .withDemands
                        (list(invalidValueA, validValue)
                                , list(invalidValueA, validValue)
                                , list(invalidValueA, validValue)
                                , list(invalidValueA, validValue)
                                , list(validValue, invalidValueB)
                                , list(validValue, invalidValueB)
                                , list(validValue, validValue))
                .withSupplyAttributes()
                .withSupplies
                        (list(), list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list()
                                , list())
                .withConstraint
                        (for_all().withChildren
                                (forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(a, invalidValueA).withChildren(defyingConstraintA)
                                        , forAllWithValue(b, invalidValueB).withChildren(defyingConstraintB)
                                        , forAllWithValue(a, validValue).withChildren(then(noCost()))
                                        , forAllWithValue(b, validValue).withChildren(then(noCost()))))
                .toProblem()
                .asSolution();
        solution.optimize(linearInitialization());
        assertThat(solution.getLines()).hasSize(7);

        final var testSubject = ConstraintGroupBasedRepair.simpleConstraintGroupBasedRepair(0);
        final var testProduct = testSubject.demandGrouping
                (solution.constraint().childrenView().get(3).childrenView().get(0)
                        , solution);
        assertThat(testProduct).hasSize(1);
        assertThat(testProduct.values().iterator().next()).hasSize(2);
        testProduct.values().iterator().next()
                .forEach(line -> assertThat(line.value(b)).isEqualTo(invalidValueB));
    }
}
