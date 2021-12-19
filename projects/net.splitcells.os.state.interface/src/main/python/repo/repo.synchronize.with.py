#!/usr/bin/env python3
"""
This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0, which is available at
http://www.eclipse.org/legal/epl-2.0, or the MIT License,
which is available at https://spdx.org/licenses/MIT.html.
"""

__author__ = "Mārtiņš Avots"
__authors__ = ["and other"]
__copyright__ = "Copyright 2021"
__license__ = "EPL-2.0 OR MIT"

import argparse
import logging
from os import environ
import subprocess
if __name__ == '__main__':
	if environ.get('log_level') == 'debug':
		logging.basicConfig(level=logging.DEBUG)
	parser = argparse.ArgumentParser()
	parser.add_argument('--remote-host', dest='remoteHost')
	parser.add_argument('--remote-repo', dest='remoteRepo')
	parsedArgs = parser.parse_args()
	# TODO Do not create shell script, but call directly.
	# TODO Log errors to error stream.
	synchronizationScript = """system.network.peer.ssh.reachable {0} \\
	&& repo.is.clean \\
	&& repo.remote.set {1} \\
	&& repo.repair --remote-repo={1} \\
	&& repo.pull \\
	|| echo.error Could not synchronize with {0}.""".format(parsedArgs.remoteHost, parsedArgs.remoteRepo, "\n")
	logging.debug('Executing: ' + synchronizationScript)
	returnCode = subprocess.call(synchronizationScript, shell='True')
	if returnCode != 0:
		exit(1)