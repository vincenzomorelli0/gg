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
package net.splitcells.gel.constraint;

import static net.splitcells.dem.data.set.map.typed.TypedMapI.typedMap;
import static net.splitcells.dem.lang.Xml.elementWithChildren;
import static net.splitcells.dem.lang.Xml.textNode;

import java.util.Optional;

import net.splitcells.dem.data.set.map.typed.TypedMapView;
import net.splitcells.dem.lang.Xml;
import org.w3c.dom.Element;
import net.splitcells.dem.lang.dom.Domable;

/**
 * <p>IDEA Implement GroupId#name as constraint path like in reasoning system.</p>
 * <p>IDEA Create value based grouping.</p>
 */
public class GroupId implements Domable {

    public static GroupId group() {
        return new GroupId();
    }

    public static GroupId group(String name) {
        return new GroupId(name);
    }

    public static GroupId group(String name, TypedMapView metaData) {
        return new GroupId(name, metaData);
    }

    @Deprecated
    public GroupId() {
        name = Optional.empty();
        metaData = typedMap();
    }

    private GroupId(String name) {
        this.name = Optional.ofNullable(name);
        metaData = typedMap();
    }

    private GroupId(String name, TypedMapView metaData) {
        this.name = Optional.ofNullable(name);
        this.metaData = metaData;
    }

    /**
     * TODO Move name inside {@link #metaData}
     */
    private final Optional<String> name;
    private final TypedMapView metaData;

    @Deprecated
    public String getName() {
        return name.get();
    }

    public Optional<String> name() {
        return name;
    }

    public static GroupId multiply(GroupId a, GroupId b) {
        return new GroupId(a.toString() + " and " + b.toString());
    }

    @Override
    public String toString() {
        return name.orElse(super.toString());
    }

    @Override
    public Element toDom() {
        final var dom = Xml.elementWithChildren("group");
        if (name.isPresent()) {
            dom.appendChild(Xml.elementWithChildren("name", textNode(name.get())));
        }
        dom.appendChild(Xml.elementWithChildren("id", textNode(this.hashCode() + "")));
        return dom;
    }

    public TypedMapView metaData() {
        return metaData;
    }
}
