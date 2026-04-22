/**
 * Application constants. API_BASE_URL can be overridden via env NEXT_PUBLIC_API_BASE_URL.
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export const config = {
  apiBaseUrl: API_BASE_URL,
} as const;

/** Query param name for card detail “Back” target (internal path only). */
export const CARD_RETURN_FROM_PARAM = 'from';

/** Safe path for router after reading `from` query param (open redirect safe). */
export function safeReturnPath(from: string | null | undefined, fallback = '/operations/cards'): string {
  if (from == null || from === '') return fallback;
  const t = from.trim();
  if (!t.startsWith('/') || t.startsWith('//')) return fallback;
  if (t.includes(':') || t.includes('\\')) return fallback;
  return t;
}

export const ROUTES = {
  login: '/auth/login',
  home: '/',
  unauthorized: '/unauthorized',
  users: '/security/users',
  menus: '/security/menus',
  roles: '/security/roles',
  permissions: '/security/permissions',
  branches: '/housekeeping/branches',
  accountStatuses: '/housekeeping/account-statuses',
  accountTypes: '/housekeeping/account-types',
  policies: '/housekeeping/policies',
  passwordExpressions: '/housekeeping/password-expressions',
  responseCodes: '/housekeeping/response-codes',
  limitProfiles: '/housekeeping/limit-profiles',
  products: '/housekeeping/products',
  cardTypes: '/housekeeping/card-types',
  cards: '/operations/cards',
  cardsExpiry: '/operations/cards/expiry',
  cardsChangeType: '/operations/cards/change-type',
  cardsReplacement: '/operations/cards/replacement-request',
  cardsChangeStatus: '/operations/cards/change-status',
  changeCardStatus: '/operations/cards/change-status',
  cardsExport: '/operations/cards/export',
  newCardRequest: '/card-production/new-request',
  cardRequests: '/card-production/requests',
  cardRequestSearch: '/card-production/requests/search',
  cardGeneration: '/card-production/generation',
} as const;
