/**
 * Application constants. API_BASE_URL can be overridden via env NEXT_PUBLIC_API_BASE_URL.
 */
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080';

export const config = {
  apiBaseUrl: API_BASE_URL,
} as const;

export const ROUTES = {
  login: '/auth/login',
  home: '/',
  users: '/security/users',
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
  newCardRequest: '/card-production/new-request',
  cardRequests: '/card-production/requests',
  cardRequestSearch: '/card-production/requests/search',
  cardGeneration: '/card-production/generation',
} as const;
