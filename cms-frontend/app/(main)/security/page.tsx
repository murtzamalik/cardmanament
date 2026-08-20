'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { ROUTES } from '@/lib/constants';

export default function SecurityIndexPage() {
  const router = useRouter();
  useEffect(() => {
    router.replace(ROUTES.users);
  }, [router]);
  return null;
}
