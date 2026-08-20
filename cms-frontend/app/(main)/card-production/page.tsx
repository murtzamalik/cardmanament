'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ROUTES } from '@/lib/constants';

export default function CardProductionIndexPage() {
  const router = useRouter();
  useEffect(() => {
    router.replace(ROUTES.cardRequests);
  }, [router]);
  return null;
}
