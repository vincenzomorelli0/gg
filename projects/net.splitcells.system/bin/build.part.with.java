#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

set -e
chmod +x ./bin/*
export JAVA_VERSION=11 # This is required on FreeBSD, if an older Java version is set as default.
mvn clean install
cd ../net.splitcells.website.server
  # TODO ./bin/serve.to.folder
  cd ..
