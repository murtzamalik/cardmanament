# Card Management System – Frontend

React + TypeScript frontend based on PrimeReact Sakai template. Connects to the Java REST API for auth, users, roles, permissions, and reference data (banks, branches, cities, etc.).

## Run

```bash
npm install
npm run dev
```

Open [http://localhost:3000](http://localhost:3000). Use **Login** to sign in; dashboard and sidebar menu require authentication.

## Environment

| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_BASE_URL` | Backend API base URL | `http://localhost:8080` |

Create `.env.local` to override:

```
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

Ensure the backend allows CORS for `http://localhost:3000`.

## Features

- **Auth**: Login (Login ID + password), token in `localStorage`, 401 → redirect to login.
- **Security**: Users, Roles, Permissions CRUD under **Security** menu.
- **Housekeeping**: Reference data CRUD (Banks, Branches, Cities, Countries, Currencies, and 20+ other resources) under **Housekeeping** menu. All use a generic CRUD page driven by `lib/referenceDataConfig.ts`.

## Menu and routes

- **Home** → `/`
- **Security** → Users (`/security/users`), Roles (`/security/roles`), Permissions (`/security/permissions`)
- **Housekeeping** → One route per resource (e.g. `/housekeeping/banks`). Resource list and config keys are in `lib/menuConfig.ts` and `lib/referenceDataConfig.ts`.

## Build

```bash
npm run build
npm start
```
