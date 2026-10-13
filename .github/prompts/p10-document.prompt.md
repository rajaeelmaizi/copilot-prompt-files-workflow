---
agent: 'agent'
description: README and strategy document
---
The code is FROZEN: do not touch src/ or pom.xml. Generate:
1. README.md: description, requirements (Java 25, Maven), build, tests with
   the JaCoCo report, run, a table of environment variables (name, what for,
   whether it is required) without values, a table of endpoints with
   "☑ implemented", and one example of request, response and error.
2. STRATEGY.md (2 pages max): method (spec → implementation against the
   contract → validation), use of AI with human verification, roles,
   technical decisions (including the CONTRACT DECISIONS, if any).
   Use only facts recorded in PROMPTS.md and in git log.
Add the entry "## P10 · <time>" to PROMPTS.md.

OUTPUT (only this):
RESULT P10
FILES: README.md, STRATEGY.md
FEATURES DOCUMENTED: <list>
PENDING FOR A HUMAN: try the README commands · trim STRATEGY to 2 pages
