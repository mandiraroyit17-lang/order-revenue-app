# Order Revenue App

A small Spring Boot 3.x / Java 17 application that manages orders and calculates monthly revenue.

## Tech Stack
- Java 17
- Spring Boot 3.3.x (Web, Validation)
- Maven
- JUnit 5

## How to Run

### Prerequisites
- JDK 17+
- Maven 3.8+

### Build and run
```bash
mvn clean install
mvn spring-boot:run
```

The app starts on **http://localhost:8080**.

### Run tests only
```bash
mvn test
```

## API Endpoints

### Create an order
```
POST /orders
Content-Type: application/json

{
  "customerId": "CUST-001",
  "customerType": "PREMIUM",
  "amount": 150.00,
  "orderDate": "2024-01-15"
}
```
Returns `201 Created` with the saved order (including generated `id`) and a `Location` header.

### Get an order by id
```
GET /orders/{id}
```
Returns `200 OK` with the order, or `404 Not Found` if it doesn't exist.

### List orders, optionally filtered by month
```
GET /orders
GET /orders?month=2024-01
```
Returns `200 OK` with a list of orders. `month` must be in `YYYY-MM` format; an invalid format returns `400 Bad Request`.

## Part 1 — Monthly Revenue Calculation

Implemented in `RevenueService#calculateMonthlyRevenue(List<Order>)`.

Rules implemented:
- Groups orders by `YearMonth` (derived from `orderDate`)
- Applies a 10% discount to `PREMIUM` customer order amounts before summing
- Ignores orders with a `null` or negative `amount` (zero-amount orders are kept, since zero is valid revenue, just contributes nothing)
- Implemented using Java 8+ Streams (`Collectors.groupingBy` + `Collectors.reducing`), no manual loops
- `BigDecimal` is used throughout (not `double`) to avoid floating-point rounding errors with currency
- Result map is returned sorted chronologically by month

Covered by unit tests in `RevenueServiceTest`.

## Error Handling

Centralized via `@RestControllerAdvice` (`GlobalExceptionHandler`), covering:
- `404` — order not found
- `400` — Bean Validation failures (missing/invalid fields), malformed JSON, invalid `month` query param format
- `500` — fallback for any unhandled exception

All errors return a consistent JSON shape:
```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": ["amount: amount must not be negative"]
}
```

## Assumptions

1. **Storage**: in-memory only (`ConcurrentHashMap`), as specified — no persistence across restarts.
2. **ID generation**: server-assigned, auto-incrementing `Long`. Any `id` in the POST request body is ignored.
3. **PREMIUM discount**: applied only when calculating revenue (`RevenueService`); the stored `Order.amount` itself is never mutated — i.e. the discount is a reporting-time calculation, not a change to the original order record.
4. **Negative amounts**: orders with a negative or null amount are excluded entirely from revenue totals, but are still retrievable via `GET /orders` and `GET /orders/{id}` (the exclusion only applies to the revenue calculation, not to order storage/retrieval) — validation on the `POST /orders` request body separately prevents negative amounts from being created in the first place via Bean Validation (`@DecimalMin`).
5. **Zero-amount orders**: treated as valid and included in revenue (contributing `0.00`), since the spec only said to ignore "null or negative," not zero.
6. **Month filtering**: `GET /orders?month=YYYY-MM` with no `month` param returns all orders, rather than erroring.
7. **OSGi (Part 3)**: not implemented (optional bonus, out of scope for this submission). See discussion in Part 4 notes below if included separately.

## Part 4 — Spring Boot vs. OSGi (short answer)

**Prefer Spring Boot** for the vast majority of modern applications — standalone microservices, REST APIs, typical enterprise backends. It has a much shallower learning curve, a single classloader (simpler debugging), a huge ecosystem, and is the default expectation for cloud-native deployments (containers, Kubernetes) where the whole point is immutable, redeployable units rather than live in-place patching.

**Consider OSGi** when the core requirement is genuinely **dynamic modularity at runtime** — installing, updating, or removing modules in a running JVM without downtime, and/or needing strict classloader isolation between modules that may depend on conflicting versions of the same library. This shows up in plugin-based platforms (e.g. Eclipse), some telecom/embedded systems, and legacy enterprise application servers.

**OSGi would be a bad choice** for most new projects: it adds substantial complexity (manifest configuration, bundle lifecycle management, classloader debugging), the tooling/ecosystem is far smaller than Spring Boot's, hiring/onboarding is harder since OSGi expertise is rare, and in containerized/cloud-native environments the "redeploy a fresh immutable container" pattern already solves the problem OSGi was designed for — making OSGi's dynamic hot-swap capability largely redundant.
