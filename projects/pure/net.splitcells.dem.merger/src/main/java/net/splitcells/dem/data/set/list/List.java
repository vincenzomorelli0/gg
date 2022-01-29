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
package net.splitcells.dem.data.set.list;

import net.splitcells.dem.data.set.SetT;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static net.splitcells.dem.data.set.list.Lists.list;

public interface List<T> extends java.util.List<T>, ListView<T>, SetT<T> {

    @Deprecated
    default void addAll(T requiredArg, T... args) {
        this.add(requiredArg);
        this.addAll(Arrays.asList(args));
    }

    default List<T> withAppended(T... args) {
        this.addAll(Arrays.asList(args));
        return this;
    }

    default List<T> withAppended(Collection<T> args) {
        this.addAll(args);
        return this;
    }

    default Optional<T> lastValue() {
        if (isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(get(size() - 1));
    }

    default List<T> shallowCopy() {
        final List<T> shallowCopy = Lists.list();
        shallowCopy.addAll(this);
        return shallowCopy;
    }
    
    @ReturnsThis
    default List<T> reverse() {
        Collections.reverse(this);
        return this;
    }
}
