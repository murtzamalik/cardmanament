'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ROUTES } from '@/lib/constants';

export default function HousekeepingIndexPage() {
  const router = useRouter();
  useEffect(() => {
    router.replace(ROUTES.branches);
  }, [router]);
  return null;
}
