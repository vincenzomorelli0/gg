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
package net.splitcells.dem.lang.annotations;

public @interface JavaLegacy {
	/**
	 * Marks code that is present because of insufficiencies of the Java programming
	 * language, This is important in order to support the porting of this library.
	 * This annotation should not be used on code related to abstract side effect
	 * management.
	 * 
	 * TODO When to use {@link JavaLegacy}.
	 */
}
