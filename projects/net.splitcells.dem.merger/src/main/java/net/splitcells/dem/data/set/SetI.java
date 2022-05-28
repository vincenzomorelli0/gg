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

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;

@JavaLegacyArtifact
public class SetI<T> implements Set<T> {

    public static Set make() {
        return new SetI();
    }

    private final java.util.Set<T> values;

    private SetI() {
        values = new HashSet<>();
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return values.iterator();
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        throw notImplementedYet();
    }

    @Override
    public boolean add(T t) {
        return values.add(t);
    }

    @Override
    public void ensureContains(T e) {
        values.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return values.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return values.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return values.addAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return values.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return values.removeAll(collection);
    }

    @Override
    public void clear() {
        values.clear();
    }
}
