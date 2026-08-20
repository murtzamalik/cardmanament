# Requires: Java 21, Maven 3.9+, Node 20 (for frontend build on target)

## Backend (JAR)

1. Install Java 21 JRE/JDK.
2. Configure env from `config/env.example` (set real DB / JWT / encryption values securely).
3. Run:

```text
java -jar backend/jar/core-service-*.jar
```

API default: `http://HOST:8015`  
Swagger (if enabled): `http://HOST:8015/swagger-ui.html`

## Frontend

See `frontend/FRONTEND-BUILD.txt`.

## Database

See `database/DATABASE.txt`.  
Put your manual Oracle dump in `database/dump/` before zipping if you want it in the same pack.

## Smoke checks

1. API responds (health or login).
2. `POST /api/auth/login` with seed user (only on seed/demo DB).
3. Open UI login page; confirm CORS / API URL.

## Not included in this pack

- WAR
- Docker images / Dockerfiles
- Automated DB dump (add manually)
