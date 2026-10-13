# Project rules

## Source of truth
1. The API contract (path on the first line of FEATURES.md) rules paths,
   methods, field names, validations and HTTP status codes. Do not change or
   "improve" it.
2. FEATURES.md rules the business rules.
3. If something is in neither, apply the standard HTTP convention and say so
   in your answer.

## Scope
- Do only what the prompt asks. Do not add features, layers, dependencies or
  files nobody asked for.
- Do NOT create or modify tests unless the prompt explicitly asks for it.
- Do not move or rename existing code.

## Architecture
- controller (no logic) → service → repository. Constructor injection.
- Input and output DTOs separate from the entity. The entity is never received
  or returned by the API.
- Money with BigDecimal and explicit rounding.
- If the project already generates code from the contract (openapi-generator):
  implement the generated interfaces and use their models; do not write
  parallel controllers or DTOs. Do not add that plugin if it is not there.

## Security
- No secrets in code or in project files. Users, passwords, tokens, keys or
  URLs with credentials go in application.properties as
  ${ENVIRONMENT_VARIABLE:} (empty default, never a real value), and every
  variable is documented in the README.
- Do not add authentication, Spring Security, CORS or filters the spec does
  not ask for.
- Validate every input (body, path and query) with Bean Validation before
  using it.
- Data access only through JPA repositories or parameterized queries. Never
  concatenate text into a query.
- Error responses without stack traces, class names or SQL.
- Never log passwords, tokens or full personal data (ID documents, card
  numbers). Use internal identifiers.
- H2 console and admin endpoints disabled.
- Calls to external services: URL and credentials from configuration,
  connect and read timeouts, and an external failure turned into a controlled
  error (502 or 504), never an unhandled 500.

## Errors
- All of them go through the global @RestControllerAdvice and return the
  contract's error schema.
- Business errors as custom exceptions.

## Quality (SonarQube-style)
- No possible NullPointerException: validate inputs and use Optional.
- No System.out, printStackTrace, empty catch blocks, commented-out code, dead
  code or unused imports.

## Logging
- SLF4J. INFO for successful operations, WARN for client errors, ERROR for
  server errors.

## Tests (only when the prompt asks for them)
- JUnit 5. Service with Mockito. Controller with @WebMvcTest and MockMvc.
- A test demands what the contract says, not what the code does today.
- No test depends on external services or databases: the external API is
  mocked and the test database is in-memory H2.
