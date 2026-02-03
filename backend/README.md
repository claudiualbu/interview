# Repair Shop API

REST API for managing auto repair orders, invoices, and line items.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- H2 Database
- Flyway
- MapStruct

## Run

```bash
mvn spring-boot:run
```

App runs at http://localhost:8080

## API Endpoints

### Repair Orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/repair-orders | Create |
| GET | /api/v1/repair-orders | List |
| GET | /api/v1/repair-orders/{id} | Get |
| PUT | /api/v1/repair-orders/{id} | Update |
| DELETE | /api/v1/repair-orders/{id} | Delete |

### Invoices

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/invoices | Create |
| GET | /api/v1/invoices | List |
| GET | /api/v1/invoices/paginated | List (paginated) |
| GET | /api/v1/invoices/{id}/details | Get with line items |
| PUT | /api/v1/invoices/{id} | Update status |



### Line Items

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/v1/invoices/{id}/line-items | Create |
| GET | /api/v1/invoices/{id}/line-items | List |
| PUT | /api/v1/line-items/{id} | Update |
| DELETE | /api/v1/line-items/{id} | Delete |

## Data Model

```
RepairOrder (1) --- (0..1) Invoice (1) --- (N) InvoiceLineItem
```

## Business Rules

- Each RepairOrder can have one Invoice
- Invoice starts as DRAFT, can be changed to ISSUED
- ISSUED invoices cannot be modified
- Cannot issue invoice without line items
- Cannot delete RepairOrder that has Invoice

## Features

- Optimistic locking with @Version
- Pessimistic locking for concurrent invoice modifications
- RFC 7807 Problem Details for errors
- Correlation ID for request tracing
- EntityGraph for N+1 prevention

## Tests

```bash
mvn test
```

## Postman

Import `repair-shop-api.postman_collection.json` and run Demo Flow folder.

## H2 Console

- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- User: sa
- Password: password
