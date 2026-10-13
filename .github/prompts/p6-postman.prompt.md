---
agent: 'agent'
description: Postman regression collection, with report
---
Generate regression.postman_collection.json (Postman v2.1) at the root, from
the contract and FEATURES.md. Only features marked "☑ implemented".
- Variable baseUrl = http://localhost:8080.
- One folder per feature, in runnable order; chain ids with collection
  variables.
- One request for the happy path and one for each contract error code, with a
  status test and a structure test (response fields or error schema).
- One request per Test data row, with a test of the expected result.
- "Cross-cutting robustness" folder: malformed JSON→400, unknown route→404,
  unsupported method→405, Content-Type text/plain→415, decimal in an integer
  field→400.
Every test demands the status in the contract. Never accept "whatever it
returns now". Each request name says what it sends and which status it
expects.

Then run it:
1. Start the application from scratch with JDK 25 (if another instance is
   using port 8080, stop it first).
2. Run: newman run regression.postman_collection.json | tee newman-report.txt
3. Stop the application.

Do NOT modify src/ or change a test so that it passes: a failure is reported,
not fixed, here.
Add to PROMPTS.md "## P6 · <time>" with this prompt and the result.

OUTPUT (only this):
RESULT P6
REQUESTS: <n> · ASSERTIONS: <n> · FAILURES: <n>
REPORT: newman-report.txt

| Folder | Request | Expected | Received | OK / total |
<one row for EACH request, without summarizing or grouping>

FAILURES: <request → expected / received, one line per failure, or "none">
REPEAT: newman run regression.postman_collection.json | tee newman-report.txt
