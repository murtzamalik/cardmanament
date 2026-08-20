/**
 * Application constants. API_BASE_URL can be overridden via env NEXT_PUBLIC_API_BASE_URL.
 * Paths use trailing slash to match next.config.js `trailingSlash: true` (static export).
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';
/** cms-app (mobile API). Override with NEXT_PUBLIC_CMS_APP_URL on the client server. */
const CMS_APP_BASE_URL = process.env.NEXT_PUBLIC_CMS_APP_URL || 'http://localhost:8016';

export const config = {
  apiBaseUrl: API_BASE_URL,
  cmsAppBaseUrl: CMS_APP_BASE_URL,
} as const;

/** Must match cms-app JwtConstants (AES/CBC/PKCS5). */
export const CMS_APP_AES = {
  secretKey: 'ais-567890123456789012345678-256',
  iv: '1234567890123456',
} as const;

/** Ensure internal app paths end with `/` (except bare `/`). Category `#...` paths unchanged. */
export function withTrailingSlash(path?: string | null): string | undefined {
  if (path == null || !String(path).trim()) return undefined;
  const p = String(path).trim();
  if (p.startsWith('#')) return p;
  if (p === '/') return '/';
  return p.endsWith('/') ? p : `${p}/`;
}

/** Query param name for card detail “Back” target (internal path only). */
export const CARD_RETURN_FROM_PARAM = 'from';

/** Safe path for navigation after reading `from` query param (open redirect safe). */
export function safeReturnPath(from: string | null | undefined, fallback = '/operations/cards/'): string {
  if (from == null || from === '') return fallback;
  const t = from.trim();
  if (!t.startsWith('/') || t.startsWith('//')) return fallback;
  if (t.includes(':') || t.includes('\\')) return fallback;
  return withTrailingSlash(t) ?? fallback;
}

export const ROUTES = {
  login: '/auth/login/',
  home: '/',
  unauthorized: '/unauthorized/',
  users: '/security/users/',
  menus: '/security/menus/',
  roles: '/security/roles/',
  permissions: '/security/permissions/',
  branches: '/housekeeping/branches/',
  accountStatuses: '/housekeeping/account-statuses/',
  accountTypes: '/housekeeping/account-types/',
  policies: '/housekeeping/policies/',
  passwordExpressions: '/housekeeping/password-expressions/',
  responseCodes: '/housekeeping/response-codes/',
  limitProfiles: '/housekeeping/limit-profiles/',
  products: '/housekeeping/products/',
  cardTypes: '/housekeeping/card-types/',
  auditLogs: '/security/audit-logs/',
  cards: '/operations/cards/',
  cardDetail: '/operations/cards/detail/',
  cardsExpiry: '/operations/cards/expiry/',
  cardsChangeType: '/operations/cards/change-type/',
  cardsReplacement: '/operations/cards/replacement-request/',
  cardsChangeStatus: '/operations/cards/change-status/',
  changeCardStatus: '/operations/cards/change-status/',
  cardsExport: '/operations/cards/export/',
  newCardRequest: '/card-production/new-request/',
  cardRequests: '/card-production/requests/',
  cardRequestSearch: '/card-production/requests/search/',
  cardGeneration: '/card-production/generation/',
  /** Hidden cms-app demo (not in sidebar). */
  cmsAppLab: '/m-lab-k7x9/',
} as const;

/** Static-export-safe card detail URL (query id, not dynamic path segment). */
export function cardDetailHref(cardId: number | string, from?: string | null): string {
  const base = `${ROUTES.cardDetail}?id=${encodeURIComponent(String(cardId))}`;
  if (from == null || from === '') return base;
  return `${base}&${CARD_RETURN_FROM_PARAM}=${encodeURIComponent(from)}`;
}
