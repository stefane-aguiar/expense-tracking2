# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start PostgreSQL (required before running app)
docker-compose up -d

# Run application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=UserServiceTest

# Run a single test method
./mvnw test -Dtest=UserServiceTest#methodName

# Build jar
./mvnw clean package

# Test coverage report (output: target/site/jacoco/index.html)
./mvnw test jacoco:report
```

## Architecture

Kotlin + Spring Boot 3 REST API with PostgreSQL, JWT authentication, Redis caching, and Flyway migrations.

**Package layout** (`com.aguiar.expense_tracking2`):
- `controller/` — HTTP layer; `AuthController`, `UserController`, `ExpenseController`
- `service/` — Business logic; `AuthService`, `UserService`, `ExpenseService`, `JwtService`
- `repository/` — JPA repositories for `User` and `Expense`
- `model/` — JPA entities
- `dto/` — Request/response DTOs (kept separate from entities)
- `config/` — `SecurityConfig` (CORS + JWT filter chain), `CacheConfig` (Redis TTL), `JwtFilter`, `LoggingFilter`
- `exception/` — `GlobalExceptionHandler` (@RestControllerAdvice) + `ResourceNotFoundException`

## Key Design Decisions

**Authentication:** Stateless JWT (24h expiry). `JwtFilter` validates the Bearer token and sets the Spring Security context with `userId` as principal. `/auth/**` and `/actuator/**` are open; everything else requires a valid token.

**Caching:** Redis with 5-minute TTL configured in `CacheConfig`. `ExpenseService.getAllExpenses(userId)` is `@Cacheable`; create/update/delete use `@CacheEvict` to invalidate by `userId`.

**Database migrations:** Flyway manages all schema changes — `ddl-auto=none` in JPA. Add new migrations as `V{n}__description.sql` in `src/main/resources/db/migration/`.

**Error handling:** `GlobalExceptionHandler` handles validation errors, JSON parse errors (with user-friendly messages for `LocalDate`/`BigDecimal`), 404s, and 500s consistently.

**Logging:** `LoggingFilter` injects a UUID-based `requestId` into MDC. Log pattern includes `[%X{requestId}]`. Logstash encoder outputs structured JSON.

## Configuration Profiles

| Profile | Database | Schema |
|---|---|---|
| *(default)* | PostgreSQL localhost:5432 | Flyway migrations |
| `prod` | `${DATABASE_URL}` env var | Flyway migrations |
| `test` | H2 in-memory | `create-drop` (no Flyway) |

Integration tests use `@ActiveProfiles("test")` with H2. Unit tests use `@ExtendWith(MockitoExtension::class)` and mock repositories directly.

## Kotlin-Specific Notes

The `all-open` and `noarg` Maven plugins are configured for JPA entities and Spring components — entities don't need `open` or explicit no-arg constructors. Null safety uses JSR305 strict mode.
