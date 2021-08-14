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
package net.splitcells.dem.environment;

import net.splitcells.dem.environment.config.IsDeterministic;
import net.splitcells.dem.environment.config.ProgramLocalIdentity;
import net.splitcells.dem.environment.config.ProgramRepresentative;
import net.splitcells.dem.environment.config.StartTime;
import net.splitcells.dem.environment.config.framework.Configuration;
import net.splitcells.dem.resource.communication.Closeable;
import net.splitcells.dem.resource.communication.Flushable;
import net.splitcells.dem.resource.host.ProcessPath;

import static net.splitcells.dem.environment.config.framework.ConfigurationI.configuration;

public class EnvironmentI implements Environment {

    private final Configuration config = configuration();

    public static Environment create(Class<?> programRepresentative) {
        return new EnvironmentI(programRepresentative);
    }

    private EnvironmentI(Class<?> programRepresentative) {
        config.configValue(StartTime.class);
        config.withConfigValue(ProgramRepresentative.class, programRepresentative);
    }

    @Override
    public void init() {
        config.configValue(ProgramLocalIdentity.class);
        config.configValue(IsDeterministic.class);
        config.configValue(ProcessPath.class);
    }

    @Override
    public Configuration config() {
        return config;
    }

    @Override
    public void flush() {
        config.process(Flushable.class, r -> {
            r.flush();
            return r;
        });
    }

    @Override
    public void close() {
        config.process
                (Closeable.class
                        , r -> {
                            r.close();
                            return r;
                        });
    }

}
