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
package net.splitcells.gel.problem.derived;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.dem.lang.perspective.Perspective;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.solution.Solution;
import net.splitcells.gel.solution.history.History;
import net.splitcells.gel.solution.history.Histories;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.allocation.Allocations;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.rating.framework.MetaRating;
import org.w3c.dom.Node;

import java.util.function.Function;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.type.Derivation.derivation;


public class DerivedSolution implements Solution {

    private Constraint constraint;
    private final Allocations allocations;
    private final History history;
    private final Discoverable contexts;

    public static DerivedSolution derivedSolution(Discoverable context, Allocations allocations
            , Constraint originalConstraint, Function<Rating, Rating> derivation) {
        return new DerivedSolution(context, allocations, originalConstraint
                , derivation(originalConstraint, derivation));
    }

    public static DerivedSolution derivedSolution(Discoverable context, Allocations allocations
            , Constraint constraint, Constraint derivation) {
        return new DerivedSolution(context, allocations, constraint, derivation);
    }

    private DerivedSolution(Discoverable context, Allocations allocations, Constraint originalConstraints
            , Function<Rating, Rating> derivation) {
        this(context, allocations, originalConstraints, derivation(originalConstraints, derivation));
    }

    public static DerivedSolution derivedSolution(Discoverable contexts, Allocations allocations, Function<Solution, Constraint> constraintBuilder) {
        final var derivedSolution = new DerivedSolution(contexts, allocations);
        derivedSolution.constraint = constraintBuilder.apply(derivedSolution);
        return derivedSolution;
    }

    private DerivedSolution(Discoverable context, Allocations allocations, Constraint constraint
            , Constraint derivation) {
        this.allocations = allocations;
        this.constraint = derivation;
        this.history = Histories.history(this);
        this.contexts = context;
    }

    private DerivedSolution(Discoverable contexts, Allocations allocations) {
        this.allocations = allocations;
        history = Histories.history(this);
        this.contexts = contexts;
    }

    @Override
    public Constraint constraint() {
        return constraint;
    }

    @Override
    public Allocations allocations() {
        return allocations;
    }

    @Override
    public Solution asSolution() {
        throw notImplementedYet();
    }

    @Override
    public DerivedSolution derived(Function<Rating, Rating> derivation) {
        throw notImplementedYet();
    }

    @Override
    public Database supplies() {
        return allocations.supplies();
    }

    @Override
    public Database suppliesUsed() {
        return allocations.suppliesUsed();
    }

    @Override
    public Database suppliesFree() {
        return allocations.suppliesFree();
    }

    @Override
    public Database demands() {
        return allocations.demands();
    }

    @Override
    public Database demandsUsed() {
        return allocations.demandsUsed();
    }

    @Override
    public Database demandsFree() {
        return allocations.demandsFree();
    }

    @Override
    public Line allocate(Line demand, Line supply) {
        return allocations.allocate(demand, supply);
    }

    @Override
    public Line allocationOf(LinePointer demand, LinePointer supply) {
        return allocations.allocationOf(demand, supply);
    }

    @Override
    public Line demandOfAllocation(Line allocation) {
        return allocations.demandOfAllocation(allocation);
    }

    @Override
    public Line supplyOfAllocation(Line allocation) {
        return allocations.supplyOfAllocation(allocation);
    }

    @Override
    public Set<Line> allocationsOfSupply(Line supply) {
        return allocations.allocationsOfSupply(supply);
    }

    @Override
    public Set<Line> allocationsOfDemand(Line demand) {
        return allocations.allocationsOfDemand(demand);
    }

    @Override
    public Line addTranslated(List<?> values) {
        return allocations.addTranslated(values);
    }

    @Override
    public Line add(Line line) {
        return allocations.add(line);
    }

    @Override
    public void remove(int lineIndex) {
        allocations.remove(lineIndex);
    }

    @Override
    public void remove(Line line) {
        allocations.remove(line);
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        allocations.subscribeToAfterAdditions(subscriber);
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        allocations.subscribeToBeforeRemoval(subscriber);
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        allocations.subscribeToAfterRemoval(subscriber);
    }

    @Override
    public List<Attribute<Object>> headerView() {
        return allocations.headerView();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        return allocations.headerView2();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        return allocations.columnView(attribute);
    }

    @Override
    public ListView<Column<Object>> columnsView() {
        return allocations.columnsView();
    }

    @Override
    public ListView<Line> rawLinesView() {
        return allocations.rawLinesView();
    }

    @Override
    public int size() {
        return allocations.size();
    }

    @Override
    public List<Line> rawLines() {
        return allocations.rawLines();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line other) {
        return allocations.lookupEquals(attribute, other);
    }

    @Override
    public Node toDom() {
        return allocations.toDom();
    }

    @Override
    public Perspective toPerspective() {
        return allocations.toPerspective();
    }

    @Override
    public List<String> path() {
        return contexts.path().withAppended(DerivedSolution.class.getSimpleName());
    }

    @Override
    public History history() {
        return history;
    }

    @Override
    public Object identity() {
        return this;
    }
}
