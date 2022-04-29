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
package net.splitcells.dem.resource;

import net.splitcells.dem.data.set.list.List;
import net.splitcells.dem.lang.annotations.ReturnsThis;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;

/**
 * The name of classes, that provide an aspect, should end with `Aspect`,
 * in order to hint at their main functionality,
 * if the class name does not get too long.
 *
 * @param <T>
 */
public class AspectOrientedConstructor<T> {
    private List<Function<T, T>> aspects = list();

    @ReturnsThis
    public AspectOrientedConstructor<T> withAspect(Function<T, T> aspect) {
        aspects.add(aspect);
        return this;
    }

    public T joinAspects(T arg) {
        T joinedAspects = arg;
        for (final var aspect : aspects) {
            joinedAspects = aspect.apply(joinedAspects);
        }
        return joinedAspects;
    }
}
