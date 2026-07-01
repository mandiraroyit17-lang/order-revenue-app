# Order Revenue App

A Spring Boot 3.x / Java 17 REST application for managing customer orders and calculating monthly revenue.

## Tech Stack

* Java 17
* Spring Boot 3.x
* Spring Web
* Spring Data JPA
* Jakarta Bean Validation
* H2 Database (development)
* Maven
* JUnit 5
* Mockito

---

## Prerequisites

* JDK 17+
* Maven 3.8+

---

## Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application starts on:

```
http://localhost:8080
```

---

## Running Tests

```bash
mvn test
```

---

## Database

The application uses an **H2 in-memory database** for development.

Hibernate automatically creates the required tables on startup.

Example configuration:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

# REST APIs

## Create Order

**POST** `/orders`

Request

```json
{
  "customerId": "CUST-001",
  "customerType": "PREMIUM",
  "amount": 150.00,
  "orderDate": "2024-01-15"
}
```

Response

* **201 Created**
* Returns the created order including its generated `id`
* Includes a `Location` response header

---

## Get Order by Id

**GET**

```
/orders/{id}
```

Returns:

* **200 OK** if the order exists
* **404 Not Found** if the order does not exist

---

## Get All Orders

**GET**

```
/orders
```

Returns all stored orders.

---

## Get Orders by Month

**GET**

```
/orders?month=2024-01
```

Returns all orders belonging to the specified month.

---

## Monthly Revenue

**GET**

```
/revenue
```

Returns revenue grouped by calendar month.

Example response

```json
{
    "2024-01": 135.00,
    "2024-02": 540.00
}
```

Optionally, revenue can be calculated for a specific month:

```
GET /revenue?month=2024-01
```

---

# Revenue Calculation Rules

Revenue calculation is implemented in `RevenueService`.

Business rules:

* Orders are grouped by `YearMonth`
* PREMIUM customers receive a **10% discount**
* Discount is applied only during revenue calculation
* Stored order amounts remain unchanged
* Orders with null values or negative amounts are ignored
* `BigDecimal` is used for all monetary calculations
* Revenue is returned in chronological order

Example

| Customer Type | Amount | Revenue Counted |
| ------------- | ------ | --------------- |
| REGULAR       | 100.00 | 100.00          |
| PREMIUM       | 150.00 | 135.00          |

---

# Validation

The application validates incoming requests using Jakarta Bean Validation.

Examples include:

* customerId must not be blank
* amount must be non-negative
* customerType is required
* orderDate is required

Invalid requests return **400 Bad Request**.

---

# Error Handling

Global exception handling is implemented using `@RestControllerAdvice`.

Supported responses include:

* **400 Bad Request**

    * Validation failures
    * Invalid request payload
    * Invalid month format

* **404 Not Found**

    * Order not found

* **500 Internal Server Error**

    * Unexpected server errors

Example response

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": [
    "amount must be greater than or equal to 0"
  ]
}
```

---

# Project Structure

```
controller/
    OrderController
    RevenueController

service/
    OrderService
    RevenueService

repository/
    OrderRepository

model/
    Order
    CustomerType

dto/
    OrderRequest

exception/
    GlobalExceptionHandler
```

---

# Assumptions

1. Orders are persisted using Spring Data JPA.
2. IDs are generated automatically by the database.
3. Revenue calculations never modify stored order amounts.
4. Premium customers receive a fixed 10% discount.
5. Orders with invalid or negative amounts are excluded from revenue calculations.
6. Month filtering uses the `YYYY-MM` format.
7. Revenue is calculated dynamically from the stored orders.

---

# Spring Boot vs OSGi

For this application, **Spring Boot** is the preferred choice.

Spring Boot provides:

* Simple application configuration
* Embedded server support
* Excellent REST API development
* Strong integration with Spring Data JPA
* Large ecosystem and community support

OSGi is better suited for applications requiring runtime module loading and dynamic deployment, but it introduces additional complexity that is unnecessary for this project.
