
# PaySheild

PaySheild is an educational payment reliability backend
built with Java, Spring Boot, Maven, and PostgreSQL.

## Technology stack

- Java 21
- Spring Boot
- Spring Web
- Bean Validation
- Spring Data JPA
- PostgreSQL
- Maven
- Docker Compose

## Prerequisites

Install JDK 21, Maven, and Docker Desktop.

Verify your installation:

```bash
java -version
mvn -version
docker --version
```

## Start the database

```bash
docker compose up -d postgres
```

## Start the application

```bash
mvn spring-boot:run
```

API base URL:

http://localhost:8080

## Create a payment

Send a POST request to `/api/payments`.

Example JSON:

```json
{
  "amount": 12500,
  "currency": "INR",
  "description": "Demo order"
}
```

Include this HTTP header:

`Idempotency-Key: demo-order-001`

## Retrieve a payment

Send a GET request to:

`/api/payments/{id}`

Replace `{id}` with the UUID returned by the create endpoint.

## Learning features

- REST API design
- Request validation
- Database persistence
- Idempotency-key checks
- Error handling
- Basic payment review rules

## Limitations

This project does not process real money.
It has no payment gateway, authentication, or authorization.
Concurrent idempotency conflicts need further hardening.
The review rule is only a demonstration.

Do not use real payment data or production credentials.
