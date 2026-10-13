# Exercise — Pet shop order rewards (1 use case)

> Practice statement for the workflow in this repository. There is no OpenAPI
> contract and no base project: you start from scratch. Suggested time: 60–90 minutes.

## 1. Context and goal

A pet accessories shop wants a microservice that registers customer orders and
automatically calculates their loyalty reward points. The points decide which
customers get the monthly gift box when there are more candidates than boxes.

Build the microservice with AI tools, in **Spring Boot / Java 25**, with
automated unit tests.

## 2. Single use case

**F1 — Register an order and calculate its reward points.**

The system receives the order data, validates it, calculates the points with
the rules in section 3, stores the order with its points and returns it.

- Exposed as one REST operation: `POST /orders`.
- If everything is correct it answers **201** with the registered order.
- A duplicated order (same `orderReference`) is rejected as a conflict.

### 2.1 Input (JSON)

| Field | Type | Rules |
|---|---|---|
| orderReference | text | Required. 8 digits |
| customerName | text | Required. Between 2 and 80 characters |
| storeCode | text | Required. 8 digits |
| loyaltySeason | text | Required. Format YYYY-YYYY; the second year is the first plus 1 |
| loyaltyMember | boolean | Required |
| pickupMethod | text | Required. One of: STORE_PICKUP, PARCEL_LOCKER, HOME_DELIVERY |
| orderTotal | number | Required. Greater than or equal to 0, at most 2 decimals |
| petCount | integer | Required. Between 1 and 15 |
| adoptedPet | boolean | Required |
| subscription | boolean | Required |

### 2.2 Output (JSON)

`id`, `orderReference`, `customerName`, `storeCode`, `loyaltySeason`,
`amountPerPet`, `rewardPoints` (integer), `breakdown` (object with the points
of each criterion: `loyalty`, `pickup`, `basket`, `adoption`, `subscription`)
and `createdAt` (ISO-8601 date and time).

## 3. Business rules

The reward points are the sum of five criteria:

| Criterion | Condition | Points |
|---|---|---|
| Loyalty | `loyaltyMember` is true | 50 |
| Pickup | STORE_PICKUP | 30 |
| Pickup | PARCEL_LOCKER | 20 |
| Pickup | HOME_DELIVERY | 0 |
| Basket | amount per pet greater than or equal to 70.00 | 15 |
| Adoption | `adoptedPet` is true | 10 |
| Subscription | `subscription` is true | 15 |

- Amount per pet = `orderTotal` / `petCount`, rounded to 2 decimals with
  HALF_UP. The comparison with 70.00 uses the already rounded value.
- Amounts are handled with exact decimal precision.
- The maximum possible score is 120.

## 4. Non-functional requirements

- Test coverage measured with JaCoCo.
- Quality and security checked with a SonarQube-style analysis.
- Every invalid request gets a controlled JSON response, with no internal
  traces, with the fields `timestamp`, `status`, `error`, `message` and `path`.
- Activity log with date and time, severity level and message.
- No credentials in the source code.

## 5. Deliverables

README with run instructions, a strategy document (2 pages max), the prompts
used and the full source code.

---

## Annex A — Test data

### A.1 Valid cases (all answer 201)

| Case | loyalty | pickupMethod | orderTotal | pets | adopted | subscription | Amount per pet | Points |
|---|---|---|---|---|---|---|---|---|
| C1 | yes | STORE_PICKUP | 210.00 | 3 | no | no | 70.00 | 95 |
| C2 | no | PARCEL_LOCKER | 209.97 | 3 | yes | no | 69.99 | 30 |
| C3 | no | HOME_DELIVERY | 100.00 | 4 | no | no | 25.00 | 0 |
| C4 | yes | STORE_PICKUP | 1000.00 | 1 | yes | yes | 1000.00 | 120 |
| C5 | no | HOME_DELIVERY | 209.99 | 3 | no | no | 70.00 | 15 |

Note: in C5, 209.99 / 3 = 69.9966…, which rounds to 70.00 and therefore gets
the 15 basket points. In C2, 69.99 does not get them.

### A.2 Invalid cases

| Case | Request | Expected response |
|---|---|---|
| E1 | `orderReference` = "123" | 400 |
| E2 | `petCount` = 0 | 400 |
| E3 | `pickupMethod` = "DRONE" | 400 |
| E4 | `loyaltySeason` = "2026-2028" | 400 |
| E5 | `petCount` = 2.5 | 400 |
| E6 | `loyaltyMember` missing | 400 |
| E7 | Malformed JSON body | 400 |
| E8 | Send C1 twice (same `orderReference`) | 409 |
