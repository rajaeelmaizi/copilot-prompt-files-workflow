Contract: openapi.yaml

## F1 — Register an order and calculate its reward points
- Operation: POST /orders
- Business rules:
  1. The reward points are the sum of five criteria.
  2. Loyalty: `loyaltyMember` is true → 50 points.
  3. Pickup: STORE_PICKUP → 30 points.
  4. Pickup: PARCEL_LOCKER → 20 points.
  5. Pickup: HOME_DELIVERY → 0 points.
  6. Basket: amount per pet greater than or equal to 70.00 → 15 points.
  7. Adoption: `adoptedPet` is true → 10 points.
  8. Subscription: `subscription` is true → 15 points.
  9. Amount per pet = `orderTotal` / `petCount`, rounded to 2 decimals with HALF_UP.
  10. The comparison with 70.00 uses the already rounded value.
  11. Amounts are handled with exact decimal precision.
  12. The maximum possible score is 120.
  13. A duplicated order (same `orderReference`) is rejected as a conflict.
- Test data:

  | Case | loyalty | pickupMethod | orderTotal | pets | adopted | subscription | Amount per pet | Points |
  |---|---|---|---|---|---|---|---|---|
  | C1 | yes | STORE_PICKUP | 210.00 | 3 | no | no | 70.00 | 95 |
  | C2 | no | PARCEL_LOCKER | 209.97 | 3 | yes | no | 69.99 | 30 |
  | C3 | no | HOME_DELIVERY | 100.00 | 4 | no | no | 25.00 | 0 |
  | C4 | yes | STORE_PICKUP | 1000.00 | 1 | yes | yes | 1000.00 | 120 |
  | C5 | no | HOME_DELIVERY | 209.99 | 3 | no | no | 70.00 | 15 |

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
- External dependencies: none
- Validation requests:

  ```sh
  # expect 201
  curl -i -X POST http://localhost:8080/orders \
    -H 'Content-Type: application/json' \
    -d '{"orderReference":"10000001","customerName":"Test Customer","storeCode":"20000002","loyaltySeason":"2026-2027","loyaltyMember":true,"pickupMethod":"STORE_PICKUP","orderTotal":210.00,"petCount":3,"adoptedPet":false,"subscription":false}'

  # expect 400
  curl -i -X POST http://localhost:8080/orders \
    -H 'Content-Type: application/json' \
    -d '{"orderReference":"123","customerName":"Test Customer","storeCode":"20000002","loyaltySeason":"2026-2027","loyaltyMember":true,"pickupMethod":"STORE_PICKUP","orderTotal":210.00,"petCount":3,"adoptedPet":false,"subscription":false}'

  # expect 409
  curl -i -X POST http://localhost:8080/orders \
    -H 'Content-Type: application/json' \
    -d '{"orderReference":"10000001","customerName":"Test Customer","storeCode":"20000002","loyaltySeason":"2026-2027","loyaltyMember":true,"pickupMethod":"STORE_PICKUP","orderTotal":210.00,"petCount":3,"adoptedPet":false,"subscription":false}'
  ```
- Status: ☑ implemented ☐ validated ☑ tests ☐ registered
