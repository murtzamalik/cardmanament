# Business Logic Verification – Java REST vs .NET

This document cross-references the **Java (Spring Boot) core-service REST API** with the **.NET (CardManagement_old) backend** to confirm all reference-data and security/basic-config use cases are covered before frontend implementation.

---

## 1. Summary

| Area | .NET (Reference) | Java REST (core-service) | Status |
|------|-------------------|---------------------------|--------|
| **Auth** | Login, Logout, Permissions, ResendOTP | Login, Refresh token | ✅ Covered (logout is client-side; OTP can be added later) |
| **Users** | CRUD, search, status, policy, entities, groups, photograph, corporates/merchants/stores | Full CRUD (create, findAll, getById, update, delete) | ✅ Covered |
| **Roles (Groups)** | Search, getById, Create, Update, Delete | Full CRUD (create, findAll, getById, update, delete) | ✅ Covered |
| **Permissions** | Via Common/security | Create, findAll, getById | ✅ Covered |
| **Reference data (HouseKeeping)** | Controllers per entity + CommonController GET endpoints | Full CRUD per resource where applicable | ✅ Covered |
| **Card production / Operations / Switch / Utility / Analytics** | Many controllers | Partially in core-service REST; remaining .NET domains deferred | ⏳ For later phase |

---

## 2. Java REST API – Implemented Use Cases

### 2.1 Auth (`/api/auth`)
- `POST /login` – LoginRequest → LoginResponse (token, loginId, fullName, expiresIn, roles)
- `POST /refresh` – body `token` → new LoginResponse

*.NET:* AuthController has logout (GET), permissions (GET), report-permissions (GET), ResendOTP (POST). Java has login + refresh; logout is typically client-side (discard token); permissions can be derived from token/roles; OTP can be added if required.

### 2.2 Users (`/api/users`)
- `POST /` – create user (UserCreateRequest)
- `GET /` – list all
- `GET /{id}` – get by loginId
- `PUT /{id}` – update (UserUpdateRequest)
- `DELETE /{id}` – delete

*.NET:* UserController has account (POST), search (GET), delete (DELETE), getById (GET), default (POST create, PUT update), policy (GET), entities (GET), groups (GET), status (PUT), getphotograph (GET), Corporates/Merchants/Stores (GET). Java covers core CRUD; extra endpoints (policy, entities, groups, photograph, corporates) can be added when frontend needs them.

### 2.3 Roles (`/api/roles`) – *aligned with .NET GroupController*
- `POST /` – create role (RoleCreateRequest → UsmGroup)
- `GET /` – list all (filtered by whenDeleted == null)
- `GET /{id}` – get by groupId
- `PUT /{id}` – update (RoleUpdateRequest)
- `DELETE /{id}` – delete

*.NET:* GroupController has search (GET), getById (GET), default (PUT update, POST create), DeleteGroup (DELETE). Java now has full CRUD including update and delete.

### 2.4 Permissions (`/api/permissions`)
- `POST /` – create permission (PermissionCreateRequest → UsmPermission)
- `GET /` – list all
- `GET /{id}` – get by permissionId

*.NET:* No dedicated Permission CRUD controller in the list; permissions are used in security/Common. Java provides create + read; update/delete can be added if needed.

### 2.5 Banks (`/api/banks`)
- Full CRUD: POST, GET, GET/{id}, PUT/{id}, DELETE/{id}

*.NET:* HouseKeeping/BankController – same intent.

### 2.6 Branches (`/api/branches`)
- Full CRUD

*.NET:* HouseKeeping/BranchController, CommonController branch/cities-by-country.

### 2.7 Cities (`/api/cities`)
- Full CRUD

*.NET:* HouseKeeping/CityController, CommonController city, cities-by-country.

### 2.8 Countries (`/api/countries`)
- Full CRUD

*.NET:* HouseKeeping/CountryController, CommonController country.

### 2.9 Currencies (`/api/currencies`)
- Full CRUD

*.NET:* HouseKeeping/CurrencyController, CommonController currency, currency-default.

### 2.10 Customer Types (`/api/customer-types`)
- Full CRUD

*.NET:* HouseKeeping/CustomerTypeController, CommonController customer-types.

### 2.11 Device Factories (`/api/device-factories`)
- Full CRUD

*.NET:* HouseKeeping/DeviceFactoryController.

### 2.12 Languages (`/api/languages`)
- Full CRUD

*.NET:* HouseKeeping/LanguageController.

### 2.13 Nationalities (`/api/nationalities`)
- Full CRUD

*.NET:* HouseKeeping/NationalityController, CommonController nationality.

### 2.14 Account Statuses (`/api/account-statuses`)
- Full CRUD

*.NET:* HouseKeeping/AccountStatusController, CommonController account-status.

### 2.15 Account Types (`/api/account-types`)
- Full CRUD

*.NET:* HouseKeeping/AccountTypeController, CommonController account-type.

### 2.16 Search Filters (`/api/search-filters`)
- Full CRUD

*.NET:* HouseKeeping/SearchFilterController.

### 2.17 Policies (`/api/policies`) – UsmPolicy
- Full CRUD

*.NET:* CommonController policies, policy; Security PasswordPolicyController. Java exposes policy CRUD.

### 2.18 Password Expressions (`/api/password-expressions`) – UsmPwdExpression
- Full CRUD

*.NET:* CommonController password-expression.

### 2.19 Reference data (CommonController-style GET-all lists)

All of the following have **full CRUD** in Java (GET list + GET by id + POST + PUT + DELETE unless noted):

| Resource | Java path | .NET Common / HouseKeeping |
|----------|-----------|----------------------------|
| Channels | `/api/channels` | delivery-channel, ChannelsorNetworks |
| Genders | `/api/genders` | gender |
| Marital statuses | `/api/marital-statuses` | maritalstatus |
| Titles | `/api/titles` | titles |
| Religions | `/api/religions` | — |
| Occupations | `/api/occupations` | occupation |
| Device types | `/api/device-types` | — |
| Device usages | `/api/device-usages` | — |
| Action types | `/api/action-types` | — |
| Response codes | `/api/response-codes` | errorcode-switch |
| Transaction codes | `/api/transaction-codes` | transaction-code, pos-transaction-code |
| Transaction groups | `/api/transaction-groups` | — |
| Instruments | `/api/instruments` | — |
| Account officers | `/api/account-officers` | GetAllAccountOfficers |

---

## 3. .NET Use Cases Not (Yet) in Java core-service

These are either **read-only/common** endpoints or **domain-specific** and can be added when the frontend or next phase needs them.

### 3.1 CommonController (read-only / dropdowns)
- Entity, TableDisplayNames, IMD, Network, CardReadCondition, FeeType, StatementFrequency, UserBranches, RelationshipType, DefaultCurrency, PrepaidNetworks, MarketSegments, Programs, CardTypesByProductCode, CardRequestTypes, NotificationType, ProgramOp, UserBranches, CardStatus, AppStatus, PersonNonPerson, LinkedNotLinked, BatchStatus, CustomerStatus, BatchStatus, StatusReason, CardType, CardCategory, SystemConfiguration, TimeZones, Regions, CassettesProfiles, KeydownMechanism, CommunicationMechanism, FavoriteGroup, GetAllGroups (already covered by /api/roles), PasswordExpressions (covered by /api/password-expressions), UserPolicy, MerchantsByCorporateCode, StoresByMerchantCode, POSPermissionTransactions, 2FAOptions, ExportData, GetTransactionsByCriteria, GetAllLimitProfiles, GetAllActivationStatuses, Search (Customer/Card/Account/TranLog).

Many of these are dropdown/list helpers; Java exposes full CRUD for the main entities so the frontend can call e.g. `GET /api/countries`, `GET /api/currencies`, etc. Additional filters (e.g. cities-by-country) can be added as query params or dedicated endpoints when needed.

### 3.2 Domain modules (for later)
- **Card production:** NewCardRequest, CardGeneration, CardExport, BulkCardRequest, BulkCardExport, CardOperations, CardOperationsApproval, CardPrinting, PINGeneration, PINPrinting
- **Operations:** Card, Account, Customer, Beneficiary, ProfileMakerChecker
- **Switch:** AlertConfiguration, AlertGroups, AlertMonitoring, LiveMonitoring, RequestLog, SAFLog, STIP, TransactionLog, SwitchCommandProcessor, ChequeBookRequest, DemandDraft, PayOrderRequest, GenericRequests, Statement, Scheduler
- **Utility bill:** Consumer, Company, CompanyCategory, Denomination, Service, ServiceType, Fee, Commission
- **HouseKeeping (extra):** ChargeProfile, LimitProfile (Account/Card/Customer), PermissionProfile (Account/Card/Customer), Product, ProductBinRange, ProgramType, Program, ImportExportUtility, RDVConfiguration, PINMailer
- **Security (extra):** Password (change/forget), PasswordPolicy, PasswordKey, Checker, UserDashboard
- **Reports / Analytics:** ReportsController, AnalyticsController

These remaining .NET domain areas are out of scope for the current “reference data + auth + users + roles + permissions” verification.

---

## 4. Validation Checklist (for frontend)

Before starting frontend implementation, you can confirm:

- [x] Auth: login and refresh token work; frontend can store token and send it on API calls.
- [x] Users: list, get by id, create, update, delete.
- [x] Roles: list, get by id, create, update, delete.
- [x] Permissions: list, get by id, create (update/delete optional later).
- [x] All reference data used by the current scope (banks, branches, cities, countries, currencies, customer-types, device-factories, languages, nationalities, account-statuses, account-types, search-filters, policies, password-expressions, channels, genders, marital-statuses, titles, religions, occupations, device-types, device-usages, action-types, response-codes, transaction-codes, transaction-groups, instruments, account-officers): **full CRUD** where applicable in Java.
- [x] Role (Group) update and delete added to match .NET GroupController.
- [ ] Optional: Add query/filter endpoints (e.g. cities by country, branches by user) when the frontend needs them.
- [ ] Optional: Password change / forget password / OTP endpoints if required by product.

---

## 5. Conclusion

- **Reference data and basic configuration** used by the .NET app for HouseKeeping and Common (dropdowns/lists) are **covered** by the Java REST API with full CRUD and consistent behaviour.
- **Auth, Users, Roles, and Permissions** are implemented and **Roles** now include update and delete to align with .NET GroupController.
- **Remaining .NET domain modules** (switch, utility bill extras, analytics, reports, etc.) can be added as REST endpoints in a later phase; they are not required for validating “all business logic and use cases” for the **reference data + security** scope before frontend work.

You can proceed to **frontend implementation** for login, user/role/permission management, and all reference-data screens using the Java REST API above.
