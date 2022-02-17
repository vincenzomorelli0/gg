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
package net.splitcells.gel.common;

public enum Language {
    ALLOCATE("allocate"),
    ALLOCATION("allocation"),
    ALLOCATIONS("allocations"),
    ARGUMENTATION("argumentation"),
    DATA("data"),
    DEMAND("demand"),
    @Deprecated
    DEMAND2("demand"),
    DEMANDS("demands"),
    EMPTY_STRING(""),
    EVENT("event"),
    GROUP("group"),
    HISTORY("history"),
    INDEX("index"),
    KEY("key"),
    LINE("line"),
    META_DATA("metaData"),
    NAME("name"),
    OPTIMIZATION("optimization"),
    PATH_ACCESS_SYMBOL("."),
    PROBLEM("problem"),
    PROPAGTION("propagation"),
    RATING("rating"),
    REMOVE("remove"),
    RESULT("result"),
    STEP_TYPE("stepType"),
    SUPPLIES("supplies"),
    SUPPLY("supply"),
    TEST("test"),
    TYPE("type"),
    VALUE("value"),
    WORDS("words");
    private final String value;

    Language(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
