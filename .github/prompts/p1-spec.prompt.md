---
agent: 'agent'
description: Spec and contract from the exercise statement
---
STATEMENT FILE: ${input:statement:statement file, e.g. EXERCISE.md}
CONTRACT: ${input:contract:path to the openapi file or 'none'}

STEP 0 — Reading check. If you CANNOT extract the real text of the statement
(binary, empty, unsupported format), answer only "I CANNOT READ THE FILE" and
stop. Never guess the content from the file name. If you can, quote literally
the body fields of the first feature and one figure from the business rules,
with the section they come from.

STEP 1 — Contract.
- If CONTRACT is a path: use it as is. Do not modify it.
- If CONTRACT is "none": generate openapi.yaml at the root (OpenAPI 3.0.3)
  with every operation in the statement. Whatever the statement fixes (path,
  field, validation, code) is copied literally. Whatever it does not fix is
  decided with the REST convention: plural, lowercase resources; create → POST
  201; read and list → GET 200; update → PUT 200; delete → DELETE 204; invalid
  input → 400; not found → 404; duplicate or state not allowed → 409; and a
  single error schema {timestamp, status, error, message, path} unless the
  statement defines another one. Everything you decide, and not the statement,
  goes under CONTRACT DECISIONS.

STEP 2 — Create FEATURES.md at the root. First line:
"Contract: <path to the contract file>". Then one section for EACH feature
in the statement, with its numbering, without adding or merging:

## F<n> — <title>
- Operation: <METHOD> <path> (as it is in the contract)
- Business rules: <numbered list, literal from the statement, or "none">
- Test data: <literal table from the statement, without recalculating, or "none">
- External dependencies: <external API or database it uses, or "none">
- Validation requests: <one curl for the happy path and one for each error
  code the contract defines for this operation, each preceded by
  "# expect <status>">
- Status: ☐ implemented ☐ validated ☐ tests ☐ registered

Do NOT copy fields, validations or codes into this file: they are read from
the contract.

STEP 3 — Add to PROMPTS.md an entry "## P1 · <time>" with this prompt
literally and the result in 2 lines. Do NOT generate code or tests.

OUTPUT (only this):
RESULT P1
READING: <literal quote from step 0>
CONTRACT: <path> (<given | generated>)
FEATURES: <n> → F1 <title>, F2 <title>, ...
CONTRACT DECISIONS: <list of what was decided by convention, or "none">
CREDENTIALS AND EXTERNAL SERVICES: <what the statement gives and what for, or "none">
REQUIREMENTS WITHOUT A FEATURE: <list or "none">
QUESTIONS FOR THE REVIEWER: <list or "none">
