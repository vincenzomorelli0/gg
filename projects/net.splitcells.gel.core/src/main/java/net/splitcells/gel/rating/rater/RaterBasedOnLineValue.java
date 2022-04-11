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
package net.splitcells.gel.rating.rater;

import static java.util.stream.Collectors.toList;
import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.Lambdas.describedFunction;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.RatingEventI.ratingEvent;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;
import static net.splitcells.gel.rating.framework.LocalRatingI.localRating;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.gel.common.Language;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import net.splitcells.dem.lang.Xml;
import net.splitcells.dem.lang.dom.Domable;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.object.Discoverable;
import net.splitcells.gel.rating.framework.Rating;

/**
 * This {@link Rater} makes it easy to rate every {@link Line} of a group independent of each other.
 */
public class RaterBasedOnLineValue implements Rater {
    public static Rater raterBasedOnLineValue(String description, Function<Line, Integer> classifier) {
        return raterBasedOnLineValue(new Function<>() {
            private final Map<Integer, GroupId> lineNumbering = map();

            @Override
            public GroupId apply(Line arg) {
                return lineNumbering.computeIfAbsent
                        (classifier.apply(arg.value(LINE))
                                , classification -> GroupId.group(description + ": " + classification));
            }

            @Override
            public String toString() {
                return description;
            }
        });
    }

    public static Rater lineValueRater(Predicate<Line> classifier) {
        return lineValueRater(classifier, line -> {
            if (classifier.test(line)) {
                return noCost();
            } else {
                return cost(1);
            }
        });
    }

    public static Rater lineValueRater(Predicate<Line> classifier, String description) {
        return lineValueRater(new Predicate<Line>() {
            @Override
            public boolean test(Line line) {
                return classifier.test(line);
            }

            @Override
            public String toString() {
                return description;
            }
        }, line -> {
            if (classifier.test(line)) {
                return noCost();
            } else {
                return cost(1);
            }
        });
    }

    public static Rater lineValueRater(Predicate<Line> classifier, Function<Line, Rating> rater) {
        return new RaterBasedOnLineValue(rater
                , describedFunction
                (addition -> addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)
                        , classifier.toString())
                , (addition, children) -> {
            if (classifier.test(addition.value(LINE))) {
                return children;
            } else {
                return list();
            }
        });
    }

    public static Rater lineValueSelector(Predicate<Line> classifier) {
        return lineValueRater(classifier, line -> noCost());
    }

    public static Rater lineValueBasedOnRater(Function<Line, Rating> raterBasedOnLineValue) {
        return new RaterBasedOnLineValue(raterBasedOnLineValue
                , addition -> addition.value(Constraint.INCOMING_CONSTRAINT_GROUP)
                , (addition, children) -> children);
    }

    public static Rater raterBasedOnLineValue(Function<Line, GroupId> classifierBasedOnLineValue) {
        return new RaterBasedOnLineValue(additionalLine -> noCost(), classifierBasedOnLineValue
                , (addition, children) -> children);
    }

    private final Function<Line, Rating> rater;
    private final Function<Line, GroupId> classifier;
    private final BiFunction<Line, List<Constraint>, List<Constraint>> propagation;
    private final List<Discoverable> contexts = list();

    private RaterBasedOnLineValue(Function<Line, Rating> rater
            , Function<Line, GroupId> classifierBasedOnLineValue
            , BiFunction<Line, List<Constraint>, List<Constraint>> propagationBasedOnLineValue) {
        this.rater = rater;
        this.classifier = classifierBasedOnLineValue;
        this.propagation = propagationBasedOnLineValue;
    }

    @Override
    public RatingEvent ratingAfterAddition(Table lines, Line addition, List<Constraint> children
            , Table ratingsBeforeAddition) {
        final RatingEvent rating = ratingEvent();
        rating.additions().put
                (addition
                        , localRating()
                                .withPropagationTo(propagation.apply(addition, children))
                                .withResultingGroupId(classifier.apply(addition))
                                .withRating(rater.apply(addition.value(LINE))));
        return rating;
    }

    @Override
    public RatingEvent rating_before_removal(Table lines, Line removal, List<Constraint> children
            , Table ratingsBeforeRemoval) {
        return ratingEvent();
    }

    @Override
    public Class<? extends Rater> type() {
        return RaterBasedOnLineValue.class;
    }

    @Override
    public List<Domable> arguments() {
        return list(() -> Xml.elementWithChildren(getClass().getSimpleName()
                , Xml.textNode(classifier.toString()
                        + " "
                        + rater.toString()
                )));
    }

    @Override
    public Node argumentation(GroupId group, Table allocations) {
        final var argumentation = Xml.elementWithChildren(Language.GROUP.value());
        argumentation.appendChild
                (Xml.textNode(group.name().orElse("missing-group-name")));
        return argumentation;
    }

    @Override
    public void addContext(Discoverable contexts) {
        this.contexts.add(contexts);
    }

    @Override
    public Collection<List<String>> paths() {
        return contexts.stream().map(Discoverable::path).collect(toList());
    }

    @Override
    public Element toDom() {
        final Element dom = Xml.elementWithChildren(getClass().getSimpleName());
        dom.appendChild(Xml.elementWithChildren("args", arguments().get(0).toDom()));
        return dom;
    }

    @Override
    public String toSimpleDescription(Line line, Table groupsLineProcessing, GroupId incomingGroup) {
        return classifier.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ", " + rater + ", " + classifier;
    }
}
