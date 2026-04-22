# Dynamic RBAC Implementation Guide

## What Was Implemented

This project now supports dynamic RBAC-driven navigation and admin assignment flows.

### Backend

- Dynamic menu APIs:
  - `GET /api/my-menus` -> returns current user's menu tree by role.
  - `GET /api/menus` -> returns all menus (admin/security roles only).
  - `POST /api/menus` -> create menu.
  - `PUT /api/menus/{id}` -> update menu.
  - `DELETE /api/menus/{id}` -> delete menu.
- Role-menu APIs:
  - `GET /api/roles/{roleCode}/menus`
  - `POST /api/roles/{roleCode}/menus` (replace all assigned menus)
  - `DELETE /api/roles/{roleCode}/menus/{menuId}`
- User-role APIs:
  - `GET /api/users/{id}/roles`
  - `POST /api/users/{id}/roles`
  - `DELETE /api/users/{id}/roles/{roleCode}`
- Login/refresh now includes `menus` in auth payload.
- API protection for management endpoints:
  - Menu/Role/User/Permission management endpoints use method-level authorization:
    - `ADMIN`, `SECURITY_MANAGER`, `SUPER_ADMIN`

### Frontend

- Login/refresh stores menus in local storage (`cms_menus`) and auth state.
- Sidebar (`AppMenu`) now builds from API-provided menu tree (fallback to static config if missing).
- Route guard added:
  - `hasMenuAccess(path)` checks exact and nested route access.
  - Unauthorized routes redirect to `/unauthorized`.
- New admin screen:
  - `Security > Menus` at `/security/menus`
  - Supports menu create/update/delete and hierarchy controls.
- Extended role screen:
  - Role menu assignment (multi-select and save).

## How RBAC Works End-to-End

1. User logs in -> backend authenticates and returns token + role IDs + menu tree.
2. Frontend stores token and menus.
3. Sidebar renders only allowed menus.
4. Page guard checks `hasMenuAccess(path)` before page render.
5. Backend still enforces API authorization on sensitive admin APIs.

> Important: frontend menu checks are for UX only. Backend authorization is the real security boundary.

## Data Model Assumptions

- `CMS_MENU`: menu catalog (supports hierarchy via `PARENT_MENU_ID`).
- `CMS_ROLE_MENU`: role-to-menu mapping.
- `USM_GROUP`, `USM_USER_GROUP`: role and user-role mappings.

## API Summary

### Menus

- `GET /api/my-menus`
- `GET /api/menus`
- `POST /api/menus`
- `PUT /api/menus/{id}`
- `DELETE /api/menus/{id}`

### Role-Menu

- `GET /api/roles/{roleCode}/menus`
- `POST /api/roles/{roleCode}/menus`
- `DELETE /api/roles/{roleCode}/menus/{menuId}`

### User-Role

- `GET /api/users/{id}/roles`
- `POST /api/users/{id}/roles`
- `DELETE /api/users/{id}/roles/{roleCode}`

## How to Test

## 1) Login + Sidebar

1. Login as admin user.
2. Verify sidebar matches menus assigned to admin role.
3. Check browser storage:
   - `cms_token` exists
   - `cms_menus` exists

## 2) Menu CRUD

1. Open `/security/menus`.
2. Create new menu:
   - Name: `Test Screen`
   - Path: `/test-screen`
   - Order: `99`
3. Edit menu (icon/order/status).
4. Delete menu and verify it disappears.

## 3) Role -> Menu Assignment

1. Open `/security/roles`.
2. Click assign menus action on a role.
3. Select a limited set of menus and save.
4. Login with a user in that role.
5. Verify only assigned menus appear in sidebar.

## 4) User -> Role Assignment

1. Open `/security/users`.
2. Assign/remove roles for a user.
3. Re-login as that user.
4. Confirm menu set changes based on new role mapping.

## 5) Route Guard

1. Login as user with limited menus.
2. Manually browse to an unassigned route.
3. Verify redirect to `/unauthorized`.

## 6) API Authorization

1. Login as non-admin role.
2. Call admin APIs (`/api/menus`, `/api/roles`, `/api/users`, `/api/permissions` management operations).
3. Verify forbidden response where role is not allowed.

## Notes

- If role/menu assignments are changed while user session is active, user should re-login (or refresh auth) to get updated menu payload.
- For full fine-grained authorization, add permission-level checks (`USM_GROUP_PERMISSION`) on business APIs in addition to role checks.
