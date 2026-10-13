---
agent: 'agent'
description: Judge — independent score out of 100 and FIXES.md (new chat, ideally a different model)
---
Act as an independent code reviewer. You did NOT write this code and you have
no stake in its score. Evaluate WITHOUT modifying the project: the only files
you may write are FIXES.md and your entry in PROMPTS.md. You do not fix
anything; someone else will. If you need to run something, do it on a copy and
on another port. Time left for fixes: ${input:minutes:minutes left} minutes.

Ground rules:
- Every deduction needs evidence: file:line, a command output or a request and
  its response. No evidence, no deduction.
- Score only what you verified. If you could not verify a criterion (for
  example the build does not run), mark it NOT VERIFIED, leave it out of the
  total and say what is needed to verify it.
- The spec is the reference: FEATURES.md and the contract on its first line.
  Never use the current behavior of the code as the expected result.

CRITERIA (100 points)

1. CONTRACT (20): 20 × operations that match the contract / total. An
   operation matches when its path, method, request fields, validations,
   response fields and status codes are the ones in the contract. Check that
   the application starts without any environment variable.

2. BUSINESS RULES (20): 20 × (business rules + Test data rows) covered by a
   passing test whose expected value comes from FEATURES.md / total. Recompute
   at least two Test data rows by hand and compare.

3. TEST INTEGRITY (15): start at 15 and subtract, minimum 0:
   - 5 per test whose expected value contradicts FEATURES.md or the contract.
   - 5 per disabled, skipped or empty test, or an assertion that cannot fail
     (assertTrue(true), asserting a mock against itself).
   - 3 per test that mocks the class under test or only checks that a method
     was called when the rule is about a value.
   If the project is under git, compare tests against their history and flag
   any assertion that was relaxed to match the code.

4. COVERAGE (10): 10 × branch coverage from the real JaCoCo report in
   target/site/jacoco. Do not estimate it: if there is no report, NOT VERIFIED.

5. SECURITY (15): 15 − 5 × critical − 2 × major, minimum 0.
   Critical: secret in code or any project file, concatenated query, input
   that reaches persistence without validation, stack trace or SQL in a
   response. Major: personal data in logs, missing timeouts on external calls,
   admin console or H2 console enabled.

6. ERROR HANDLING (10): 10 × invalid requests answered with the contract's
   error schema / requests probed. Probe at least: malformed JSON, missing
   field, wrong type, decimal in an integer field, unknown route, unsupported
   method, unsupported Content-Type. Check that logs have timestamp, level and
   message.

7. MAINTAINABILITY (10): 10 − 2 per finding, minimum 0: logic in a
   controller, entity exposed by the API, System.out or printStackTrace, empty
   catch, dead or commented-out code, duplicated logic, unused imports,
   misleading names.

Create (or replace) FIXES.md at the root with the actions that recover the
most points, at most 5, ordered by points per minute, and only those that fit
in the time left. One line per action:
- [ ] <concrete action> in <file> — +<points> · <min> min

Add to PROMPTS.md "## P9 · <time>" with this prompt and the TOTAL.

OUTPUT (only this):
RESULT P9
| Criterion | Score | Max | Evidence (file:line or request) |
TOTAL: <x> / <max verified> (NOT VERIFIED: <criteria or "none">)
FIXES: <n> actions written to FIXES.md
