'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ROUTES } from '@/lib/constants';

/** /operations has no page — send users back to the dashboard. */
export default function OperationsIndexPage() {
  const router = useRouter();
  useEffect(() => {
    router.replace(ROUTES.home);
  }, [router]);
  return null;
}
