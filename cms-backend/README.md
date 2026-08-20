# Card Management System (Backend)

Multi-module Spring Boot backend for the Card Management System. Migrated from .NET with preservation of business logic rules (BLRs), state machine, audit/logging, and safe raw SQL/SP usage.

## Tech Stack

- **Backend:** Spring Boot 3.2 (Java 17+)
- **Database:** Oracle 19c (production); H2 for tests
- **Security:** Spring Security + JWT
- **Logging:** SLF4J/Logback (ELK-ready)
- **Design patterns:** Strategy (IBusinessProcess), Template Method (state machine), Repository, DI

## Project Structure

```
card-management-system/
├── dal-service/          # JPA entities, Spring Data JPA repositories
├── common-service/       # Shared security/config utilities (JWT, helpers)
├── core-service/         # Spring Boot app — REST API, services, auth, Flyway
├── cms-frontend/         # Next.js Vision Web UI
└── pom.xml
```

## Architecture Flow

```
REST API (core-service controllers)
  → Application services
  → Repositories / DataHelper → DAL (JPA / Oracle)
  → Audit / ActivityLogger
```

- **Core:** REST controllers, application services, auth, Flyway migrations, OpenAPI.
- **Common:** Shared security/config utilities (JWT, helpers) used by core.
- **DAL:** JPA entities and Spring Data JPA repositories; parameterized SQL/SP via DataHelper where needed.

## Configuration

- **application.properties:** Oracle URL, username, password, pool, JWT secret/expiry, logging. See `core-service/src/main/resources/application.properties`.
- **Tests:** Use `application-test.properties` with H2 in-memory and test JWT secret.

## Oracle database setup

1. **Set your Oracle connection:** Either edit `core-service/src/main/resources/application.properties` or use environment variables:
   - `spring.datasource.url=<your-oracle-jdbc-url>` (e.g. `jdbc:oracle:thin:@host:1521/SERVICE`)
   - `spring.datasource.username=<user>`
   - `spring.datasource.password=<password>`
   - Env overrides: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
2. **Table creation:** On first application start, **Flyway** runs and creates all tables (and seed data) in your Oracle 19c database. Migrations are under `core-service/src/main/resources/db/migration/`. Keep `spring.jpa.hibernate.ddl-auto=none`.
3. **Manual option:** If you prefer to run DDL yourself, execute the SQL in `db/migration/V1__create_schema_oracle.sql` in SQL*Plus/SQLcl, then set `spring.flyway.enabled=false` so Flyway does not run.

## BLRs and Error Handling

- See `migration-spec/BUSINESS_LOGIC_REGISTRY.md` and `migration-spec/ERROR_HANDLING_MATRIX.md`.
- Core: BLR-Core-1 to BLR-Core-7 (startup, null message, biz process not found, state transitions, extension resolve, audit).
- Common: BLR-Common-1 to BLR-Common-13, BLR-Cache-1 to BLR-Cache-6.
- Business: BLR-Biz-1 to BLR-Biz-9 (e.g. duplicate checks, parameterized SQL only for DenominationManager, SAFLogManager).
- DAL: BLR-DAL-1 (whitelist/parameterize view access), BLR-DAL-2 (no Code First).

## Audit and Logging

- **AddForAuditLogs (Core):** Builds `LogRequestAudit` and stores in `AuditContext` (thread-local) for the request.
- **ActivityLogger:** SystemLog/ActivityLog in Common and Business; can be extended to write to DB and ELK.

## Raw SQL and Stored Procedures

- All usage must be **parameterized** (Oracle `:param` / NamedParameterJdbcTemplate). No concatenation of user input.
- See `migration-spec/RAW_SQL_REGISTRY.md` and `migration-spec/STORED_PROCEDURE_DOCUMENTATION.md`.
- DataHelper: `executeUpdate(sql, params)`, `getData(sql, params)`; validate IN-list size (e.g. SAFLogManager).

## Security

- JWT issued at `POST /api/auth/login` with body `{"loginId":"...", "password":"..."}`. Login validates against `USM_USER` and resolves roles from `USM_USER_GROUP` / `USM_GROUP_PERMISSION`. Use `Authorization: Bearer <token>` for `/api/**`.
- **Example:** See [docs/JWT-EXAMPLE.md](docs/JWT-EXAMPLE.md) for curl and Postman steps (login, then calling a protected endpoint).
- Roles mapped from token; PermissionController permissions align with roles for audit and endpoint security.

### PCI DSS and card data encryption

- **CardDataEncryptionService** (core-service) encrypts cardholder data (PAN, CVV, etc.) at rest using AES-256-GCM.
- **Key configuration:** Set `cms.card.encryption-key` (hex 64 chars or base64) or the `CARD_ENCRYPTION_KEY` environment variable. For production, set the env var and use `cms.card.encryption-optional=false` so startup fails if the key is missing.
- **Key generation:** Generate a 256-bit key: `openssl rand -hex 32` (output 64 hex chars). Do not commit keys to source control.
- **Key rotation:** Decrypt existing values with the old key and re-encrypt with the new key (run a migration or one-off job), then rotate the key in config/env.
- **Hash for lookup:** PAN hash (SHA-256 with salt) is used for exact-match lookup without storing plain PAN. Salt: `cms.card.hash-salt`.
- **Card export (approve-and-generate):** When a card request is approved via `POST /api/card-generation/request/{id}/approve-and-generate`, the system generates the card and optionally writes a bureau feed file. Configure `cms.card.export.output-dir` (e.g. a restricted directory path) to enable export; leave blank to disable. Options: `filename-pattern`, `include-pan`, `format` (PAN_LIST or CSV), `external-command`. Files are never served by the API (PCI).

## Build and Run

```bash
mvn clean install
cd core-service && mvn spring-boot:run
```

- **API:** REST endpoints under `/api/auth`, `/api/users`, `/api/roles`, `/api/permissions`, `/api/banks`, etc. See OpenAPI at `/swagger-ui.html` when running.
- **Health:** Use actuator/health if enabled.

## API testing and auth

- **Base URL:** `http://localhost:8080` (or your backend URL when using Docker).
- **Auth flow:** Call `POST /api/auth/login` with JSON body `{"loginId":"admin","password":"test123"}` or `{"username":"admin","password":"test123"}`. Use the returned `data.token` in the `Authorization: Bearer <token>` header for all other `/api/**` requests.
- **Default test credentials:** `admin` / `test123` (ensured by Flyway migration V5; see [docs/ORACLE_DB_VERIFICATION.md](docs/ORACLE_DB_VERIFICATION.md)).
- **CORS:** Frontend origins (e.g. `http://localhost:3000`) are allowed via `app.cors.allowed-origins` (default: `http://localhost:3000,http://127.0.0.1:3000`).

### Run API integration tests (JUnit, H2)

Requires no Oracle; uses in-memory H2 and test profile.

```bash
mvn -pl core-service test -Dtest=ApiIntegrationTest
```

Tests: login with `loginId` and `username`, protected endpoint returns 401/403 without token, GET `/api/users` with token returns 200.

### Run API smoke test (real backend)

Run against a running backend (local or Docker). Logs in, then GETs each API base path with the JWT.

```bash
./scripts/api-smoke-test.sh http://localhost:8080
```

Optional: pass a different base URL as the first argument. Requires `curl`.

### API list (all require JWT except auth and health)

| Area | Base path |
|------|-----------|
| Auth | `POST /api/auth/login`, `POST /api/auth/refresh` |
| Users, Roles, Permissions | `/api/users`, `/api/roles`, `/api/permissions` |
| Banks, Branches, Countries, Cities | `/api/banks`, `/api/branches`, `/api/countries`, `/api/cities` |
| Reference / lookup | `/api/currencies`, `/api/account-types`, `/api/account-statuses`, `/api/genders`, `/api/titles`, `/api/nationalities`, `/api/religions`, `/api/marital-statuses`, `/api/occupations`, `/api/languages`, `/api/response-codes`, `/api/policies`, `/api/password-expressions`, `/api/customer-types`, `/api/channels`, `/api/device-types`, `/api/device-usages`, `/api/device-factories`, `/api/instruments`, `/api/transaction-codes`, `/api/transaction-groups`, `/api/action-types`, `/api/account-officers`, `/api/search-filters` |
| Health | `GET /actuator/health` (no JWT) |

Each resource supports standard CRUD where applicable (POST, GET, GET `/{id}`, PUT `/{id}`, DELETE `/{id}`). See OpenAPI at `/swagger-ui.html`.

## Docker deployment (local)

Backend and frontend can run in Docker. The **backend container is named `CMS`**.

**Prerequisites:** Oracle 19c reachable (local or `host.docker.internal`). Set DB URL via env or `.env` file.

```bash
# From repository root (card-management-system/)

# 1. Create .env (optional) with your Oracle and JWT settings
# SPRING_DATASOURCE_URL=jdbc:oracle:thin:@host.docker.internal:1521/ORCL
# SPRING_DATASOURCE_USERNAME=cms
# SPRING_DATASOURCE_PASSWORD=yourpassword
# NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

# 2. Build and run
docker compose -p cms build
docker compose -p cms up -d

# 3. Backend (container name: CMS): http://localhost:8080
# 4. Frontend: http://localhost:3000
```

- **Backend image:** Built with `Dockerfile.backend` (Maven + Eclipse Temurin 21). Container name: **CMS**.
- **Frontend image:** Built with `frontend/Dockerfile` (Next.js standalone). Container name: **CMS-Frontend**.
- **Oracle:** Not included in compose. Use `SPRING_DATASOURCE_URL` pointing to your Oracle instance (e.g. `host.docker.internal` from inside container for DB on host).
- **Stop:** `docker compose -p cms down`

## Unit Tests

- Skeleton tests in `core-service` and `common-service` under `src/test/java`. Run with `mvn test` (use `-DskipTests` if DB not configured).
- **API integration tests:** `ApiIntegrationTest` in `core-service` (auth and protected endpoints with H2). See **API testing and auth** above.

## Migration parity

- **Full parity plan:** See verification report `migration-spec/VERIFICATION_REPORT_OLD_VS_JAVA.md` for completion status of DAL, Common, Business, raw SQL/SP, and config.
- **BLRs / error handling / SP-SQL safety:** BUSINESS_LOGIC_REGISTRY.md, ERROR_HANDLING_MATRIX.md, RAW_SQL_REGISTRY.md, STORED_PROCEDURE_DOCUMENTATION.md.
- **Config keys used by ported code:** `app.default-app-id`, `app.date-format` (see application.properties). Additional keys from migration-spec/CONFIG_USAGE_CHECK.md can be added as needed.

## References

- `migration-spec/CROSS_LAYER_DEPENDENCY_MAP.md` — layer dependencies and flow.
- `migration-spec/FILE_COVERAGE_TRACKER.md` — extraction status.
- `migration-spec/BIZPROCESS_LAYER_COMPLETED.md` — list of 71 managers.
