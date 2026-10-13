---
agent: 'agent'
description: Close coverage gaps
---
Run mvn verify with JDK 25. Read target/site/jacoco and write the tests for the
uncovered branches (ignore getters, setters and configuration without logic),
including those of the global handler. Tests with behavior assertions.
Do NOT touch src/main. Run verify again. Add the entry "## P7 · <time>" to
PROMPTS.md.

OUTPUT (only this):
RESULT P7
COVERAGE: before <x>% → after <y>% (read from the report)
TESTS ADDED: <n>
VERIFY: <OK | FAILED + error>
