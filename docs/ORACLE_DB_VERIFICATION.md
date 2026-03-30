# Oracle DB Connection and Verification

This document supports the plan: **Connect CMS to Oracle DB and Verify Tables/Procedures**.

## 1. Configuration applied

- **application.properties** (core-service):  
  `spring.datasource.url=jdbc:oracle:thin:@//58.65.160.27:1521/ladb`,  
  `spring.datasource.username=CMSJ`, `spring.datasource.password=CMSJ` (or set via .env; do not commit real credentials in production)
- **.env** (Docker): `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` set for the same Oracle instance. `.env` is in `.gitignore` to avoid committing credentials.

## 2. Connection check

- The application was started with the above config. The Oracle server at `58.65.160.27:1521/ladb` was **reachable** (no network error).
- If you see **ORA-01017: invalid username/password**, the URL and driver are correct; verify with your DBA that the CMS user exists and the password is correct (and that the account is not locked).
- If you see **ORA-28000: The account is locked**, the credentials are correct but the Oracle user is locked. A DBA must unlock it (connect as a user with `ALTER USER` privilege):
  ```sql
  ALTER USER CMSJ ACCOUNT UNLOCK;
  ```
- **Success** = app starts and Flyway runs (or completes with baseline) with no connection or ORA- errors.

### Default test credentials (login API)

- **Flyway migration V5** (`V5__ensure_admin_user_oracle.sql`) ensures a default admin user exists for testing.
- **Default credentials:** `loginId` / `username`: **admin**, **password**: **test123**.
- Use these to call `POST /api/auth/login` with body `{"loginId":"admin","password":"test123"}` or `{"username":"admin","password":"test123"}`.

## 3. Tables verification

Connect as schema owner (e.g. CMS) and run:

```sql
SELECT TABLE_NAME FROM USER_TABLES ORDER BY TABLE_NAME;
```

Compare the result with the **expected tables** from Flyway migrations (V1–V4):

| Migration | Tables |
|-----------|--------|
| **V1** | BIZ_PROCESS, BIZ_PROCESS_STATES, BIZ_PROCESS_ASSEMBLY, USM_PERMISSION, USM_USER, ACCOUNT_STATUS, ACCOUNT_TYPE, COUNTRY, RESPONSE_CODE |
| **V2** | USM_APPLICATION, USM_GROUP, USM_USER_GROUP, USM_GROUP_PERMISSION, BANK, CURRENCY, CITY, BRANCH |
| **V3** | ACCOUNT, ACCOUNT_OFFICER, ACTION_TYPE, APPLICATION_SEQUENCE, BANK_ENTITY, BANK_SYSTEM, CHANNEL, CACHE_ITEM_INFO, COUNTER_VALUE, CUSTOMER_TYPE, DEVICE_FACTORY, DEVICE_TYPE, DEVICE_USAGE, GENDER, INSTRUMENT, LANGUAGE, META_COUNTER, MARITAL_STATUS, NATIONALITY, OCCUPATION, RELIGION, TITLE, TRANSACTION_GROUP, TRANSACTION_CODE, AUDIT_LOG_BATCH, AUDIT_LOG_ENTITY, AUDIT_LOG_FIELD, SCHEMA_METADATA, SCHEMA_METADATA_TABLE, SEARCH_FILTER, SEARCH_FILTER_BINDING, SYSTEM_CONFIGURATION, REPORT_CONNECTION_STRING, USM_PWD_EXPRESSION, USM_POLICY, USM_PWD_HISTORY |
| **V4** | (seed data only; no new tables) |

- If all expected tables exist: DB is in sync with migrations.
- If some are missing: run the app with Flyway enabled (no baseline, or baseline-on-migrate as configured) so migrations run, or apply the missing DDL manually.
- If the DB was already migrated and you do not want Flyway to re-apply: use `spring.flyway.baseline-on-migrate=true` (already set) so only migrations with version higher than the baseline run.

## 4. Stored procedures verification

### List procedures and functions (in DB)

```sql
SELECT OBJECT_NAME, OBJECT_TYPE, STATUS
FROM USER_OBJECTS
WHERE OBJECT_TYPE IN ('PROCEDURE','FUNCTION')
ORDER BY OBJECT_NAME;
```

### List arguments for a procedure (replace `<procedure_name>`)

```sql
SELECT OBJECT_NAME, ARGUMENT_NAME, DATA_TYPE, IN_OUT
FROM USER_ARGUMENTS
WHERE PACKAGE_NAME IS NULL AND OBJECT_NAME = '<procedure_name>'
ORDER BY POSITION;
```

### App whitelist and alignment

- The app calls stored procedures only via **DataHelperService** (`common-service`). Only names in **ALLOWED_PROCEDURES** can be passed to `executeStoredProcedure(...)`.
- Current whitelist: **CHECKER_REQUEST**.
- **Alignment:** For each procedure in the DB that the app must call:
  1. Add its name (exact case as used in the call; Oracle typically uppercase) to `ALLOWED_PROCEDURES` in `DataHelperService.java`.
  2. Ensure call sites use the same name and a parameter map matching the procedure’s signature (order/types/IN-OUT per `USER_ARGUMENTS`).

### Deliverable: procedure checklist

| Procedure name | Purpose (if known) | Used by app | In whitelist |
|----------------|--------------------|-------------|---------------|
| (fill from USER_OBJECTS) | | yes/no | yes/no |

After running the SQL above, fill the table. For any procedure that should be callable by the app but is not in the whitelist, add it to `ALLOWED_PROCEDURES` and document here.
