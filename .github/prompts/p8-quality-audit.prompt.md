---
agent: 'agent'
description: SonarQube-style audit and fixes
---
Audit src/main and the configuration files the way SonarQube would (default
Java profile) and FIX: reliability bugs, blocker and critical
vulnerabilities, plain-text secrets, empty catch blocks, System.out,
commented-out code, unused imports. Do not change behavior or the contract.
Do NOT write new tests.
Run mvn verify with JDK 25. Add the entry "## P8 · <time>" to PROMPTS.md.

OUTPUT (only this):
RESULT P8
FIXED: <file:line → change, or "no findings">
VERIFY: <OK n tests | FAILED + error>
