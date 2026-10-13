---
agent: 'agent'
description: Create or adapt the project according to FINDINGS.md
---
Read FINDINGS.md and get the project ready BEFORE implementing any feature.

If MODE is CREATE: create a Maven project with Spring Boot 3.5 or later and
Java 25: spring-boot-starter-web, validation, test and, if FEATURES.md needs
its own persistence, data-jpa with H2. Packages controller, service,
repository, model, dto, exception, config. Main class and
application.properties. Nothing else.

Fix ONLY the "- [ ]" lines of FINDINGS.md and mark them "- [x]" when done.
Lines marked "DISCARDED" are not touched: what they describe is fine as is.
Do not touch anything in the DO NOT TOUCH list unless a finding requires it.

FIXED RULES (apply each one only if it is missing):
1. Do NOT implement any feature. Do NOT generate tests.
2. Do NOT move or rename anything that exists; keep packages and versions.
3. JaCoCo: jacoco-maven-plugin (prepare-agent + report in verify).
4. Errors: error class with the EXACT fields of the contract, NotFound and
   Conflict exceptions, and a @RestControllerAdvice with: validation→400,
   unreadable body→400, type mismatch→400, not found→404, unknown route→404,
   method not supported→405, accept not supported→406, conflict→409,
   content type not supported→415, generic→500. Messages without class names
   or stack traces. WARN log on 4xx, ERROR on 5xx.
5. application.properties:
   spring.jackson.deserialization.accept-float-as-int=false
   server.error.include-stacktrace=never
   spring.h2.console.enabled=false
   spring.jpa.open-in-view=false
6. Secrets: every user, password, token or key becomes ${VARIABLE:} in
   application.properties and is removed from the code. List the variables
   in README.md with their description, without values.
7. Integrations listed in FEATURES.md, still without business logic:
   - External API: configuration class with URL and credentials read from
     properties, and an HTTP client with connect and read timeouts.
   - External database: its driver as a dependency; URL, user and password
     from environment variables, with defaults pointing to in-memory H2, so
     the application and the tests work without it.
8. System.out, printStackTrace and empty catch blocks → SLF4J.
9. Run mvn verify with JDK 25 and fix until it passes. Start the application
   WITHOUT defining any environment variable and confirm it starts.
10. Add to PROMPTS.md an entry "## P3 · <time>" with this prompt and the result.

OUTPUT (only this):
RESULT P3
FILES: <created and modified>
FINDINGS: <n fixed> · <n discarded, untouched>
VERIFY: <OK n tests | FAILED + error>
START WITHOUT VARIABLES: <OK | FAILED + error>
ENVIRONMENT VARIABLES: <name → what for, or "none">
