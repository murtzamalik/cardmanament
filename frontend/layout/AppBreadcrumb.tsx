'use client';

import React from 'react';
import { usePathname } from 'next/navigation';
import { BreadCrumb } from 'primereact/breadcrumb';

const SEGMENT_LABELS: Record<string, string> = {
  security: 'Security',
  users: 'Users',
  roles: 'Roles',
  permissions: 'Permissions',
  operations: 'Operations',
  cards: 'Cards',
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
    return {
      label,
      ...(i < segments.length - 1 ? { url: href } : {}),
    };
  });

  return (
    <BreadCrumb
      model={items}
      className="border-none px-0 py-2 bg-transparent"
      style={{ fontSize: '0.8125rem' }}
    />
  );
}
