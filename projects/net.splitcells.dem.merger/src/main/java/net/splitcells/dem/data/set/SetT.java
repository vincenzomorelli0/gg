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
package net.splitcells.dem.data.set;

import net.splitcells.dem.data.atom.Bools;
import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.data.set.list.Lists;
import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.splitcells.dem.utils.ExecutionException.executionException;

@JavaLegacyArtifact
public interface SetT<T> extends Collection<T> {
    default <R> List<R> mapped(Function<T, R> mapper) {
        return Lists.<R>list().withAppended(
                stream().map(mapper).collect(Lists.toList())
        );
    }

    default <R> List<R> flatMapped(Function<? super T, ? extends Stream<? extends R>> mapper) {
        return Lists.<R>list().withAppended(
                stream().flatMap(mapper).collect(Lists.toList())
        );
    }

    default Optional<T> reduced(BinaryOperator<T> accumulator) {
        return stream().reduce(accumulator);
    }

    default boolean hasDuplicates() {
        final java.util.Set<T> uniques = new HashSet<>();
        for (T e : this) {
            if (uniques.contains(e)) {
                return true;
            }
            uniques.add(e);
        }
        return false;
    }

    default void requireUniqueness() {
        Bools.require(!this.hasDuplicates());
    }

    default void requireSetSizeOf(int requiredSize) {
        if (size() != requiredSize) {
            throw executionException("Set needs to have " + requiredSize + " elements, but has " + size() + " elements instead: " + this);
        }
    }

    default void requireEmptySet() {
        if (!isEmpty()) {
            throw executionException("Set should be empty, but has " + size() + " elements instead: " + this);
        }
    }
}
