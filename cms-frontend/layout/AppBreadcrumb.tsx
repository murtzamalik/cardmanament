'use client';

import React from 'react';
import { usePathname } from 'next/navigation';
import { BreadCrumb } from 'primereact/breadcrumb';
import { ROUTES, withTrailingSlash } from '@/lib/constants';

const SEGMENT_LABELS: Record<string, string> = {
  security: 'Security',
  users: 'Users',
  roles: 'Roles',
  permissions: 'Permissions',
  menus: 'Menus',
  'audit-logs': 'Audit logs',
  operations: 'Operations',
  cards: 'Cards',
  detail: 'Card details',
  export: 'Card export',
  expiry: 'Expiry search',
  'change-type': 'Change card type',
  'replacement-request': 'Replacement request',
  'change-status': 'Change card status',
  'card-production': 'Card Production',
  'new-request': 'New request',
  requests: 'Card requests',
  search: 'Search requests',
  generation: 'Card generation',
  housekeeping: 'Housekeeping',
  branches: 'Branches',
  'account-statuses': 'Account statuses',
  'account-types': 'Account types',
  policies: 'Policies',
  'password-expressions': 'Password expressions',
  'response-codes': 'Response codes',
  'limit-profiles': 'Limit profiles',
  products: 'Products',
  'card-types': 'Card types',
};

/** Paths that have a real page (section roots like /operations do not). */
const PAGE_PATHS = new Set(
  Object.values(ROUTES)
    .filter((p) => p !== ROUTES.login && p !== ROUTES.unauthorized)
    .map((p) => normalizePath(p))
);

function normalizePath(path: string): string {
  if (!path || path === '/') return '/';
  return path.replace(/\/+$/, '');
}

function segmentToLabel(segment: string): string {
  return SEGMENT_LABELS[segment] || segment.replace(/-/g, ' ');
}

export default function AppBreadcrumb() {
  const pathname = usePathname();
  const segments = pathname?.split('/').filter(Boolean) ?? [];
  if (segments.length === 0) return null;

  const items = segments.map((seg, i) => {
    const href = '/' + segments.slice(0, i + 1).join('/');
    const label = segmentToLabel(seg);
    const isLast = i === segments.length - 1;
    // Operations breadcrumb goes to dashboard (home), not /operations
    if (!isLast && seg === 'operations') {
      return { label, url: ROUTES.home };
    }
    const isRealPage = PAGE_PATHS.has(normalizePath(href));
    return {
      label,
      ...(!isLast && isRealPage ? { url: withTrailingSlash(href) } : {}),
    };
  });

  return (
    <BreadCrumb
      model={items}
      home={{ icon: 'pi pi-home', url: '/' }}
      className="border-none px-0 py-2 bg-transparent"
      style={{ fontSize: '0.8125rem' }}
    />
  );
}
