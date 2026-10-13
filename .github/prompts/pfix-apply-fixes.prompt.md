---
agent: 'agent'
description: Apply the fixes listed in FIXES.md
---
Read FIXES.md. Fix ONLY the "- [ ]" lines, in the order they appear, and mark
each one "- [x]" when it is done. Do not touch anything else.
For each action add or adjust its test. Do not change the contract.
Never relax an existing assertion to make a fix pass.
Run mvn verify with JDK 25 and fix until it passes.
Add to PROMPTS.md "## PFIX · <time>" with this prompt and the result.

OUTPUT (only this):
RESULT PFIX
DONE: <n> · NOT DONE: <line and reason, or "none">
VERIFY: <OK n tests | FAILED + error>
