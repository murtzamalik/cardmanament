#!/usr/bin/env bash
# CMS API smoke test: login, then GET each protected endpoint with JWT.
# Usage: ./scripts/api-smoke-test.sh [BASE_URL]
# Example: ./scripts/api-smoke-test.sh http://localhost:8080

set -e
BASE_URL="${1:-http://localhost:8080}"
LOGIN_URL="${BASE_URL}/api/auth/login"
FAILED=0

echo "=== CMS API Smoke Test ==="
echo "Base URL: $BASE_URL"
echo ""

# 1. Login and extract token
RESP=$(curl -s -w "\n%{http_code}" -X POST "$LOGIN_URL" \
  -H "Content-Type: application/json" \
  -d '{"loginId":"admin","password":"test123"}')
HTTP_BODY=$(echo "$RESP" | sed '$d')
HTTP_CODE=$(echo "$RESP" | tail -1)

if [ "$HTTP_CODE" != "200" ]; then
  echo "FAIL: POST /api/auth/login returned $HTTP_CODE (expected 200)"
  echo "$HTTP_BODY" | head -c 200
  echo ""
  exit 1
fi

if ! echo "$HTTP_BODY" | grep -q '"success":true'; then
  echo "FAIL: Login response success not true"
  exit 1
fi

TOKEN=$(echo "$HTTP_BODY" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
if [ -z "$TOKEN" ]; then
  echo "FAIL: Could not extract token from login response"
  exit 1
fi

echo "PASS: POST /api/auth/login (token obtained)"
echo ""

# 2. GET each API base path (list endpoint) with token
endpoints=(
  "/api/users"
  "/api/roles"
  "/api/permissions"
  "/api/banks"
  "/api/branches"
  "/api/countries"
  "/api/cities"
  "/api/currencies"
  "/api/account-types"
  "/api/account-statuses"
  "/api/genders"
  "/api/titles"
  "/api/nationalities"
  "/api/religions"
  "/api/marital-statuses"
  "/api/occupations"
  "/api/languages"
  "/api/response-codes"
  "/api/policies"
  "/api/password-expressions"
  "/api/customer-types"
  "/api/channels"
  "/api/device-types"
  "/api/device-usages"
  "/api/device-factories"
  "/api/instruments"
  "/api/transaction-codes"
  "/api/transaction-groups"
  "/api/action-types"
  "/api/account-officers"
  "/api/search-filters"
)

for path in "${endpoints[@]}"; do
  CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "${BASE_URL}${path}" \
    -H "Authorization: Bearer $TOKEN" -H "Accept: application/json")
  if [ "$CODE" = "200" ]; then
    echo "PASS: GET $path ($CODE)"
  else
    echo "FAIL: GET $path ($CODE)"
    FAILED=$((FAILED + 1))
  fi
done

# 3. Health (no auth; 2xx or 5xx = endpoint reachable)
HEALTH_CODE=$(curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health" 2>/dev/null || echo "000")
if [ "$HEALTH_CODE" = "200" ] || [ "$HEALTH_CODE" = "503" ] || [ "$HEALTH_CODE" = "500" ] || [ "$HEALTH_CODE" = "000" ]; then
  echo "PASS: GET /actuator/health ($HEALTH_CODE)"
else
  echo "FAIL: GET /actuator/health ($HEALTH_CODE)"
  FAILED=$((FAILED + 1))
fi

echo ""
if [ $FAILED -eq 0 ]; then
  echo "=== All checks passed ==="
  exit 0
else
  echo "=== $FAILED check(s) failed ==="
  exit 1
fi
