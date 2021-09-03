#!/usr/bin/env bash
# Copyright (c) 2021 Mārtiņš Avots (Martins Avots) and others
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0, or the MIT License,
# which is available at https://spdx.org/licenses/MIT.html.
#
# SPDX-License-Identifier: EPL-2.0 OR MIT

# TODO FIXME This script is really broken.

# Multiple modes:
# remote-host path commands
# $@: Should only contain managed commands as there may be different repositories

function relevantFolders {
	find . -mindepth 1 -maxdepth 1 -type d -not -name '.*' -prune
}

set -e
	# If repository processing is not aborted during execution by default, the user might notice very late that there was a an error as he waits until the end of the command.
	# Also note that these way the exit code is determined correctly.

case $1 in "ssh://"*)
	echo Connecting to '"'$1'"'.
	if ssh $1 "test -f $2/.repo"; then
		if ssh $1 "grep -q 'repo.type=meta' $2/.repo"; then
			if ssh $1 "grep -q 'repo.structure=flat' $2/.repo"; then
				echo "${@:3}" "$1" "$2"
				eval "${@:3}" "$1" "$2"
				sshChildren=$(ssh $1 "cd $2; find . -mindepth 1 -maxdepth 1 -type d -not -name '.*' -prune")
				for childFolder in $sshChildren
				do
					# Ommiting quotes in for loop header causes it to iterate over the lines of the string.
					echo meta repo found
					subrepo=$childFolder
					echo $subrepo
					echo Processing child "'"$childFolder"'".
					mkdir -p $subrepo
					cd $subrepo
					if ssh $1 "test -f $2/$subrepo/.repo"; then
						echo $(pwd)
						repo.process $1 $2/$subrepo "${@:3}"
					else
						echo "${@:3}" "$1" "$2"/$subrepo
						eval "${@:3}" "$1" "$2"/$subrepo
					fi
					cd ..
				done
			fi
		fi
	else
		echo Processing "'"$(pwd)"'".
		eval "${@:3}" "$1":"$2"/$subrepo
	fi
	exit
esac

optionalRepoConf="./.repo"
subrepo=''
eval $@ # Through the bash command, pipes are supported.
if test -f "./.repo"; then
	if grep -q 'repo.type=meta' "$optionalRepoConf"; then
		if grep -q 'repo.structure=flat' "$optionalRepoConf"; then
			relevantFolders|while read childFolder; do
				subrepo=$childFolder/\$subrepo
				echo Processing "'"$childFolder"'".
				cd $childFolder
				eval repo.process $@
				cd ..
			done
		fi
	fi
else
	echo Processing "'"$(pwd)"'".
	eval "$@" # Through the bash command, pipes are supported.
fi
