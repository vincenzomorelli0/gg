# CommonMark/Markdown Guidelines
[CommonMark](https://commonmark.org/) is used as the definitive Markdown syntax.

Only titles consisting of simple text without special symbols are considered
linkable.
## Mathematical Formula Via LaTex
For LaTex math formulas [MathJax](https://www.mathjax.org) is used.
For inline math `\\(` and `\\)` are used in order to indicated Latex formulas.
For math blocks `\\[` and `\\]` are used as delimiters.
Keep in mind that in CommonMark double backslash (`\\`) has to be used,
in order to describe a single backslash.

Use inline math in order to quote math formulas and do not use normal
CommonMark quoting.
## License Information
Every CommonMark file should state its license:
```
As long as not otherwise noted,
this text is licensed under the EPL-2.0 OR MIT (SPDX-License-Identifier).
```

----
* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
* SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects