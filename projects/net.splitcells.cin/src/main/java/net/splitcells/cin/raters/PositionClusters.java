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
package net.splitcells.cin.raters;

import net.splitcells.dem.data.atom.Integers;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.gel.constraint.Constraint;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.rating.rater.Rater;
import net.splitcells.gel.rating.rater.RatingEvent;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.data.set.map.typed.TypedMapI.typedMap;
import static net.splitcells.dem.utils.MathUtils.absolute;
import static net.splitcells.dem.utils.MathUtils.modulus;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.constraint.GroupId.group;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * <p>Groups all positions of a 2 dimensional space.
 * Every existing (x, y) coordinate has a corresponding position group, that is identified by its center.
 * Every such group has a center position and also contains every neighbouring position.
 * This also applies to neighbouring positions only sharing a corner with the center position.
 * All position clusters are non overlapping.</p>
 * <p>Overlapping position clusters can be implemented via multiple parallel constraint nodes,
 * with different (x,y) offset of the position center.
 * In this case, there is on additional offset for each neighbouring positions of the original center position</p>
 * <p>An (x,y) offset of (0,0), means that the most central position group on the 2D grid is (0,0).
 * Its center is located at (1,1).
 * The neighbouring position group to the right (x direction) is (3,0) and
 * its center position is (4,1).</p>
 * <p>An (x,y) offset of (1,1), means that the most central position group on the 2D grid is (1,1).
 * Its center is located at (2,2).
 * The neighbouring position group to the right (x direction) is (4,1) and
 * its center position is (5,2).</p>
 */
public class PositionClusters implements Rater {

    /**
     * @param x This is the center x coordinate of the group.
     * @param y This is the center y coordinate of the group.
     * @return Creates the group name of the position cluster, given the center position of the cluster.
     */
    public static String groupNameOfPositionCluster(int x, int y) {
        return "(x=" + x + ", y=" + y + ")";
    }

    public static int centerXPositionOf(GroupId group) {
        return Integers.parse(group.name().orElseThrow().split(",")[0].substring(3));
    }

    public static int centerYPositionOf(GroupId group) {
        final String yCoordinate = group.name().orElseThrow().split(",")[1];
        return Integers.parse(yCoordinate.substring(3, yCoordinate.length() - 1));
    }

    /**
     * @param xAttribute This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @return Returns the list of all overlapping position clusters.
     */
    public static List<Rater> positionClustering(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        return list(new PositionClusters(xAttribute, yAttribute, 0, 0),
                new PositionClusters(xAttribute, yAttribute, 0, 1),
                new PositionClusters(xAttribute, yAttribute, 1, 1),
                new PositionClusters(xAttribute, yAttribute, 1, 0),
                new PositionClusters(xAttribute, yAttribute, 1, -1),
                new PositionClusters(xAttribute, yAttribute, 0, -1),
                new PositionClusters(xAttribute, yAttribute, -1, -1),
                new PositionClusters(xAttribute, yAttribute, -1, 0),
                new PositionClusters(xAttribute, yAttribute, -1, 1));
    }

    /**
     * @param xAttribute This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @return Returns the {@link PositionClusters} with offset of 0.
     */
    public static PositionClusters positionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute) {
        return new PositionClusters(xAttribute, yAttribute, 0, 0);
    }

    /**
     * @param xAttribute    This is the x coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param yAttribute    This is the y coordinate in the {@link net.splitcells.gel.data.database.Database}.
     * @param xCenterOffset This is the x-axis offset of the position center.
     * @param yCenterOffset This is the y-axis offset of the position center.
     * @return Returns the {@link PositionClusters} with offset of 0.
     */
    public static PositionClusters positionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute, int xCenterOffset, int yCenterOffset) {
        return new PositionClusters(xAttribute, yAttribute, xCenterOffset, yCenterOffset);
    }

    private final Attribute<Integer> xAttribute;
    private final Attribute<Integer> yAttribute;
    private final int xCenterOffset;
    private final int yCenterOffset;

    /**
     * Maps x and y coordinates to the respective groups.
     * The first key is the x coordinate.
     */
    private final Map<Integer, Map<Integer, GroupId>> positionGroups = map();

    private PositionClusters(Attribute<Integer> xAttribute, Attribute<Integer> yAttribute, int xCenterOffset, int yCenterOffset) {
        this.xAttribute = xAttribute;
        this.yAttribute = yAttribute;
        this.xCenterOffset = xCenterOffset;
        this.yCenterOffset = yCenterOffset;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children, Table lineProcessing) {
        final var xVal = addition.value(LINE).value(xAttribute) + xCenterOffset;
        final var yVal = addition.value(LINE).value(yAttribute) + yCenterOffset;
        final int xCoord;
        final int xCoordCenter;
        final int yCoord;
        final int yCoordCenter;
        final int xAbs = absolute(xVal);
        final int yAbs = absolute(yVal);
        if (xVal < 0) {
            xCoord = xVal + modulus(xAbs - 1, 3);
            xCoordCenter = xCoord - 1;
        } else {
            xCoord = xVal - modulus(xVal, 3);
            xCoordCenter = xCoord + 1;
        }
        if (yVal < 0) {
            yCoord = yVal + modulus(yAbs - 1, 3);
            yCoordCenter = yCoord - 1;
        } else {
            yCoord = yVal - modulus(yVal, 3);
            yCoordCenter = yCoord + 1;
        }
        final RatingEvent rating = ratingEvent();
        final var positionGroup = positionGroups
                .computeIfAbsent(xCoord, x -> map())
                .computeIfAbsent(yCoord, y -> group(groupNameOfPositionCluster(xCoordCenter, yCoordCenter)
                        , typedMap().withAssignment(PositionClustersCenterX.class, xCoordCenter)
                                .withAssignment(PositionClustersCenterY.class, yCoordCenter)));
        rating.additions().put(addition, localRating()
                .withPropagationTo(children)
                .withRating(noCost())
                .withResultingGroupId(positionGroup));
        return rating;
    }

    @Override
    public List<Domable> arguments() {
        throw notImplementedYet();
    }
}
