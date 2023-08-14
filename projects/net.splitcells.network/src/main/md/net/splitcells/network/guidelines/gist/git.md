# Git Gist Guidelines

These guidelines only show the most relevant parts for this project regarding
Git usage.

## Commit Signing

Commit can be signed-off according to the Developer Certificate of Origin via
`git commit --amend --signoff`.

* Set used key: `git config --global user.signingkey [fingerprint]`
* Sign commit with default key: `git commit -S --amend`

----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects