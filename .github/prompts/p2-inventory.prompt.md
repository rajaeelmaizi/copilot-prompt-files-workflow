---
agent: 'agent'
description: Project inventory (read only) -> FINDINGS.md
---
Do NOT modify code or configuration. The only things you may write are
FINDINGS.md and your entry in PROMPTS.md.

Decide the MODE:
- CREATE: there is no pom.xml or build.gradle in the project.
- ADAPT: a project exists.

If the mode is ADAPT, review: build and commands; JaCoCo; global error
handling and the contract's error class; what exists and must not be touched;
what a SonarQube-style analysis would flag (System.out, printStackTrace, empty
catch blocks, dead code, possible NPEs); plain-text secrets in code or
properties; security dependencies or configuration that would block calls
without credentials; H2 console or admin endpoints enabled; whether the build
generates code from the contract (openapi-generator) and which interfaces it
produces; which database or external API it is configured against.

Create FINDINGS.md at the root with exactly this content:
MODE: <CREATE | ADAPT>
DO NOT TOUCH: <existing classes and files, or "nothing">
FINDINGS:
- [ ] <problem> in <file:line> → <concrete action>

One line per finding, only infrastructure, quality and security. Exclude
features, DTOs and tests. In CREATE mode write "- none, new project".
Add to PROMPTS.md an entry "## P2 · <time>" with this prompt and the result.

OUTPUT (only this):
RESULT P2
MODE: <CREATE | ADAPT>
BUILD: <test command> · <run command>  (in CREATE: "pending")
FINDINGS: <n> written to FINDINGS.md
