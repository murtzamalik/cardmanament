# JWT Authentication Example

This document shows how to log in and call a protected API using a JWT token.

## 1. Login

Send a POST request to `/api/auth/login` with `loginId` and `password` in the request body.

**curl:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin","password":"test123"}'
```

**Example response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "loginId": "admin",
  "expiresIn": 86400
}
```

- `token`: Use this in the `Authorization` header for protected endpoints.
- `expiresIn`: Token lifetime in seconds (e.g. 86400 = 24 hours).

**Postman:**

1. Method: **POST**
2. URL: `http://localhost:8080/api/auth/login`
3. Body → **raw** → **JSON**:
   ```json
   {"loginId":"admin","password":"test123"}
   ```
4. Send. Copy the `token` from the response.

---

## 2. Call a protected endpoint with the token

Use the token in the `Authorization` header as a Bearer token.

**curl:**

```bash
TOKEN="<paste-token-here>"

curl -X POST "http://localhost:8080/api/process/message?channelId=1&messageType=1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"LoginId":"admin","PermissionId":"","EntityId":"","MachineName":"client"}'
```

**Postman:**

1. Method: **POST**
2. URL: `http://localhost:8080/api/process/message?channelId=1&messageType=1`
3. Headers:
   - `Authorization`: `Bearer <paste-token-here>`
   - `Content-Type`: `application/json`
4. Body → **raw** → **JSON**:
   ```json
   {"LoginId":"admin","PermissionId":"","EntityId":"","MachineName":"client"}
   ```
5. Send.

A successful response will include `success`, `message`, and `cmsResponse` (e.g. code 200). Without a valid token you will get **401 Unauthorized**.

---

## Default test user

After running Flyway migrations with the seed data, one user is available:

| loginId | password |
|---------|----------|
| admin   | test123  |

Configure `app.default-app-id=CMS` (default) so login uses the same app as this user.
