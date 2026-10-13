---
agent: 'agent'
description: Implement and validate one feature
---
Implement ONLY feature F${input:N:feature number}.
- Technical part (path, method, input fields with their validations, response
  fields, success and error codes): read it in the contract, in the operation
  named on the "Operation" line of F${input:N:feature number} in FEATURES.md.
- Business rules and external dependencies: those in section F${input:N:feature number}.
- If the contract and the section contradict each other, STOP and tell me.

How:
- Bean Validation identical to the contract's validations.
- Every error code in the contract goes out through the global handler.
- If F${input:N:feature number} calls an external service: use the configured
  client, and turn a timeout or external failure into 504 or 502 with the
  error schema.
- INFO log on success, WARN on rejections.
- Do NOT touch other features. Do NOT generate tests (another role does that).
- Do not modify the entity, repository or handler unless F${input:N:feature number}
  requires it; if it does, say so under DEVIATIONS.
- Review your code against the project's Security and Quality rules.
- Run mvn verify with JDK 25 and fix until it passes.
- Validation: start the application, run the Validation requests of
  F${input:N:feature number} one by one and compare the status you get with its
  "# expect". If any does not match, fix the code (never the curl or the
  expected value), restart and repeat until they all match. Stop the
  application when you finish.
- Mark "☑ implemented" and, if everything matches, "☑ validated" in
  F${input:N:feature number}. Add to PROMPTS.md "## P4-F${input:N:feature number} · <time>"
  with this prompt and the result.

OUTPUT (only this):
RESULT P4-F${input:N:feature number}
FILES: <created and modified>
VERIFY: <OK n tests | FAILED + error>
VALIDATION: <n of n match | request → expected / received for those that don't>
DEVIATIONS: <list or "none">
