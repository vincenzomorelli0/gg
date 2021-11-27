#!/usr/bin/env sh
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# Register repository for "command.managed.install.project.commands".

configFolder=~/.config/net.splitcells.os.state.interface
mkdir -p $configFolder
repoList=$configFolder/project.repositories
touch $repoList
if grep -q "^$1$" "$repoList"; then
	echo.debug Project repository "'"$1"'" is already registered.
else
	echo "$1" >> $repoList
fi
