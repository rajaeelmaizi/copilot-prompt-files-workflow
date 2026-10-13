---
agent: 'agent'
description: Tests for one feature (new chat)
---
Write the tests for F${input:N:feature number}. Sources: the contract operation
named on the "Operation" line of F${input:N:feature number} in FEATURES.md, and the
Business rules and Test data of that section.

Tests demand what those sources say, never what the code does today. If a test
fails because the code breaks them, NEVER change the assertion: fix src/main
with the smallest change, wherever the fault is (service, controller, DTO,
entity or handler), until ALL tests in the project pass. Two limits: do not
modify the contract or the content of FEATURES.md (there you may only tick
boxes on the Status line), and do not disable or delete tests. If the contract
and FEATURES.md contradict each other, STOP and tell me: code does not fix
that.

1. Service (Mockito): happy path, one test per business rule and per Test
   data row, and every business error (right exception and no side effects,
   for example nothing saved). If there is an external service: correct
   response, timeout and error, with the client mocked.
2. Controller (@WebMvcTest + MockMvc, with the global handler): success with
   the contract's response fields; one test per failing input validation; one
   test per contract error code; malformed JSON → 400.

Extend the existing test classes. Names should...When... No @SpringBootTest.
In src/main only fixes for broken rules, nothing new.
Run mvn verify with JDK 25. Read the coverage in target/site/jacoco.
Mark "☑ tests" in F${input:N:feature number} and add to PROMPTS.md
"## P5-F${input:N:feature number} · <time>" with this prompt and the result.

OUTPUT (only this):
RESULT P5-F${input:N:feature number}
TESTS: <n added> · total <n>
VERIFY: <OK | FAILED + error>
COVERAGE: instructions <x>% · branches <y>% (read from the report)
FIXED IN src/main: <file:line → what rule it broke and what you changed, or "nothing">
