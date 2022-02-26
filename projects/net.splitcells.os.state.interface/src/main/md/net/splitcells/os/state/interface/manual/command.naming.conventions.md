# Command Naming Convention

At the heart of this project is a convention determining how commands are named.
Without such a convention it would be harder to find new commands and remember the names of every day commands.

The gist of the convention is:
The name of a command is a dot-separated list of words.
This list describes a path inside a concept hierarchy from a general starting point to the desired destination.

Example:
* Natural description: I want to see how much communication there is between my computer and the network.
* Command: system.network.usage

## Grammar

Grammar can be viewed via [Railroad Diagram Generator](https://www.bottlecaps.de/rr/ui).

```
Grammar ::= object*viaTool?identifier?
object ::= [0-9a-fA-F]+
viaTool ::= '.via.'object
identifier ::= .[0-9]+
```

As long as not otherwise noted,
this text is licensed under the EPL-2.0 OR MIT (SPDX-License-Identifier).