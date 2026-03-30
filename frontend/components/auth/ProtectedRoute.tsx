'use client';

import { useEffect } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { useAuth } from '@/services/auth/AuthContext';
import { ROUTES } from '@/lib/constants';

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, initialized } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (typeof window === 'undefined') return;
    if (!initialized) return;
    if (!isAuthenticated) {
      router.replace(ROUTES.login);
    }
  }, [initialized, isAuthenticated, router, pathname]);

  if (!initialized || !isAuthenticated) {
    return (
      <div className="flex align-items-center justify-content-center min-h-screen min-w-screen surface-ground">
        <div className="text-center">
          <i className="pi pi-spin pi-spinner text-4xl text-primary mb-3" />
          <p className="text-600">Checking authentication...</p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}
