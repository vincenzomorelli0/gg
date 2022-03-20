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
package net.splitcells.dem.testing;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ReportEntryTimeKey extends ReportEntryKey<Long> {

    public static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    protected ReportEntryTimeKey() {
        super(Long.class);
    }

    public String currentValue() {
        return "" + ZonedDateTime.now().format(DATE_TIME_FORMAT);
    }

}
