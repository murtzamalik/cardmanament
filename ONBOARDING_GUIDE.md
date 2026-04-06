# Card Management System — Onboarding Guide

## What is this project?

This is a **Card Management System (CMS)** — a full-stack banking application that manages the full lifecycle of payment cards. 
It was originally built in .NET and has been migrated to Java (Spring Boot). 
It lets bank staff create card requests, generate cards, manage users/roles/permissions,
and configure reference data like branches, products, and limit profiles.

---

## Tech Stack at a Glance

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Backend   | Java 17, Spring Boot 3.2            |
| Database  | Oracle 19c (H2 for tests)           |
| Security  | Spring Security + JWT               |
| Frontend  | Next.js 13, TypeScript, PrimeReact  |
| Build     | Maven (multi-module), npm           |
| Container | Docker + Docker Compose             |
| DB Migrations | Flyway (15 migration scripts)   |

---

## Project Structure

```
card-management-system/
├── dal-service/        # Database layer — JPA entities + Spring Data repositories
├── common-service/     # Shared utilities — JWT, security, logging, BizProcess engine
├── business-service/   # 65+ Manager classes — all business logic lives here
├── core-service/       # REST API, controllers, services, app entry point
├── frontend/           # Next.js UI
└── docker-compose.yml  # Run everything with Docker
```


---

## Module Breakdown

### 1. dal-service — Data Access Layer
This is the lowest layer. It has no business logic — just database mapping.

- **Entities** — Java classes that map to Oracle DB tables (e.g. `Card`, `CardRequest`, `UsmUser`, `Account`)
- **Repositories** — Spring Data JPA interfaces for querying the DB (e.g. `CardRepository`, `UsmUserRepository`)

Key entities you will work with:

| Entity          | Table           | Purpose                              |
|-----------------|-----------------|--------------------------------------|
| `Card`          | CARD            | A physical/virtual payment card      |
| `CardRequest`   | CARD_REQUEST    | Request to issue a new card          |
| `UsmUser`       | USM_USER        | System users (login, password, role) |
| `Account`       | ACCOUNT         | Bank account linked to a card        |
| `CardProduct`   | CARD_PRODUCT    | Card product definition (Visa, etc.) |
| `CardType`      | CARD_TYPE       | Type under a product (Debit, Credit) |
| `LimitProfile`  | LIMIT_PROFILE   | ATM/POS/eCommerce spend limits       |
| `BizProcess`    | BIZ_PROCESS     | Process config loaded at startup     |

---

### 2. common-service — Shared Infrastructure
Everything that is reused across modules lives here.

- **SecurityConfig** — Configures Spring Security. Public routes: `/api/auth/**`, `/swagger-ui/**`. Everything else requires a JWT token.
- **JwtService** — Generates and validates JWT tokens using HMAC-SHA256. Token carries `loginId` and `roles`.
- **JwtAuthenticationFilter** — Intercepts every request, reads the `Authorization: Bearer <token>` header, validates it, and sets the security context.
- **BizProcessConfigService** — On app startup (`@PostConstruct`), loads all rows from `BIZ_PROCESS` and `BIZ_PROCESS_STATES` tables into memory. This is the engine that knows which Manager class to call for which operation.
- **ActivityLoggerService** — Central logging for all system and user activity. Can be extended to write to ELK or a DB audit table.
- **DataHelperService** — Safe raw SQL execution. Only allows parameterized queries (Oracle `:param` style). Never concatenates user input into SQL.
- **AuditContext** — Thread-local storage that holds audit info (`LogRequestAudit`) for the current request lifecycle.


---

### 3. business-service — Business Logic (65+ Managers)
This is the heart of the application. Every business operation is handled by a Manager class.

All managers implement the `IBusinessProcess` interface:

```java
boolean execute(String methodName, IProcessMessage request, IProcessMessage response);
```

The core engine calls `execute("CreateAccountStatus", req, res)` and the manager dispatches to the right method internally.
Every manager extends `AbstractManagerStub` which handles error catching and response setup automatically.

Key managers you will encounter:

| Manager                  | What it does                                      |
|--------------------------|---------------------------------------------------|
| `UserManager`            | Authenticate users, search users, resolve roles   |
| `CardGenerationManager`  | Handles card generation business logic            |
| `CardManager`            | Card operations (status update, close, link)      |
| `AccountStatusManager`   | Create/update account statuses                    |
| `BranchManager`          | Branch search and management                      |
| `PermissionManager`      | Fetch all permissions from USM tables             |
| `PolicyManager`          | Password and security policy management           |
| `LimitProfileCardManager`| Card spend limit profile operations               |
| `SAFLogManager`          | Store-and-forward log queries (uses DataHelper)   |
| `DenominationManager`    | Denomination data (uses parameterized raw SQL)    |

---

### 4. core-service — REST API & Orchestration
This is the entry point of the backend. It contains:

- **`CardManagementApplication.java`** — The `main()` class that boots the Spring app
- **Controllers** — REST endpoints (e.g. `CardController`, `AuthController`, `UserController`)
- **Services** — Business orchestration (e.g. `CardGenerationServiceImpl`, `UserServiceImpl`)
- **DTOs** — Request/Response objects that travel between frontend and backend
- **Mappers** — Convert JPA entities to DTOs and back (using MapStruct)
- **Exception Handlers** — `GlobalExceptionHandler` catches all exceptions and returns clean JSON errors
- **Flyway Migrations** — 15 SQL scripts under `db/migration/` that create and seed the Oracle schema


---

## How a Request Flows Through the System

This is the most important thing to understand. Here is what happens when the frontend makes an API call:

```
Browser (Next.js)
  → POST /api/auth/login          ← get JWT token first
  → GET  /api/cards               ← all protected routes need Bearer token

Backend flow:
  HTTP Request
    → JwtAuthenticationFilter     (validates token, sets security context)
    → Controller                  (e.g. CardController)
    → Service                     (e.g. CardServiceImpl)
    → Repository / DataHelper     (queries Oracle DB)
    → Response mapped to DTO
    → JSON back to frontend
```

For operations that go through the BizProcess engine (legacy flow):

```
Controller
  → BizProcessConfigService.getBizProcess(channelId, messageType)
  → Looks up BIZ_PROCESS_STATES table → finds ClassName + MethodName
  → BizProcessRegistry.resolve("AccountStatusManager")
  → AccountStatusManager.execute("CreateAccountStatus", req, res)
  → Manager method runs business logic
  → Saves to DB via Repository
  → AuditContext logs the action
```

---

## Authentication Flow

1. Frontend sends `POST /api/auth/login` with `{ "loginId": "admin", "password": "test123" }`
2. `AuthController` calls `AuthService.login()`
3. `AuthService` calls `UserService.authenticate()` → `UserManager.authenticate()`
4. `UserManager` looks up `USM_USER` table, checks password, resolves roles from `USM_USER_GROUP` and `USM_GROUP_PERMISSION`
5. If valid, `JwtService.generateToken()` creates a signed JWT with `loginId` and `roles` embedded
6. Token returned to frontend — stored in browser, sent as `Authorization: Bearer <token>` on every subsequent request

Default test credentials (seeded by Flyway V5):

| loginId | password |
|---------|----------|
| admin   | test123  |

---

## Card Lifecycle

Understanding how a card goes from request to physical card is key to this project:

```
1. New Card Request   → Staff fills form → POST /api/card-requests
2. Request stored     → CARD_REQUEST table, IS_PROCESSED = 0
3. Approve & Generate → POST /api/card-generation/request/{id}/approve-and-generate
4. Card created       → CARD table, PAN generated, CVV computed, Track1/Track2 formatted
5. PAN encrypted      → AES-256-GCM stored in PAN_ENCRYPTED, plain PAN never exposed
6. Account linked     → CARD_ACCOUNT table links card PAN to bank account number
7. Export file        → Optional bureau feed file written to /app/export directory
```

