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
package net.splitcells.gel.data.allocation;

import net.splitcells.dem.data.set.Set;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.ListView;
import net.splitcells.gel.data.database.AfterAdditionSubscriber;
import net.splitcells.gel.data.database.BeforeRemovalSubscriber;
import net.splitcells.gel.data.database.Database;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.LinePointer;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.data.table.column.Column;
import net.splitcells.gel.data.table.column.ColumnView;
import org.w3c.dom.Node;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

public class AllocationsI implements Allocations {

    public static Allocations allocations() {
        return new AllocationsI();
    }
    private AllocationsI() {

    }

    @Override
    public Object identity() {
        throw notImplementedYet();
    }

    @Override
    public Node toDom() {
        throw notImplementedYet();
    }

    @Override
    public List<String> path() {
        throw notImplementedYet();
    }

    @Override
    public Database supplies() {
        throw notImplementedYet();
    }

    @Override
    public Database suppliesUsed() {
        throw notImplementedYet();
    }

    @Override
    public Database suppliesFree() {
        throw notImplementedYet();
    }

    @Override
    public Database demands() {
        throw notImplementedYet();
    }

    @Override
    public Database demandsUsed() {
        throw notImplementedYet();
    }

    @Override
    public Database demandsFree() {
        throw notImplementedYet();
    }

    @Override
    public Line demandOfAssignment(Line assignment) {
        throw notImplementedYet();
    }

    @Override
    public Line supplyOfAssignment(Line assignment) {
        throw notImplementedYet();
    }

    @Override
    public Line anyAssignmentOf(LinePointer demand, LinePointer supply) {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> assignmentsOfSupply(Line supply) {
        throw notImplementedYet();
    }

    @Override
    public Set<Line> assignmentsOfDemand(Line demand) {
        throw notImplementedYet();
    }

    @Override
    public Line addTranslated(List<?> values) {
        throw notImplementedYet();
    }

    @Override
    public Line add(Line line) {
        throw notImplementedYet();
    }

    @Override
    public Line addWithSameHeaderPrefix(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void remove(int lineIndex) {
        throw notImplementedYet();
    }

    @Override
    public void remove(Line line) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterAdditions(AfterAdditionSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToBeforeRemoval(BeforeRemovalSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public void subscribeToAfterRemoval(BeforeRemovalSubscriber subscriber) {
        throw notImplementedYet();
    }

    @Override
    public List<Attribute<Object>> headerView() {
        throw notImplementedYet();
    }

    @Override
    public List<Attribute<? extends Object>> headerView2() {
        throw notImplementedYet();
    }

    @Override
    public <T> ColumnView<T> columnView(Attribute<T> attribute) {
        throw notImplementedYet();
    }

    @Override
    public ListView<Column<Object>> columnsView() {
        throw notImplementedYet();
    }

    @Override
    public ListView<Line> rawLinesView() {
        throw notImplementedYet();
    }

    @Override
    public int size() {
        throw notImplementedYet();
    }

    @Override
    public List<Line> rawLines() {
        throw notImplementedYet();
    }

    @Override
    public Line lookupEquals(Attribute<Line> attribute, Line values) {
        throw notImplementedYet();
    }
}
