'use client';

import { useEffect } from 'react';
import { usePathname } from 'next/navigation';
import { useAuth } from '@/services/auth/AuthContext';
import { ROUTES } from '@/lib/constants';

export function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, initialized, hasAccess } = useAuth();
  const pathname = usePathname();

  useEffect(() => {
    if (typeof window === 'undefined') return;
    if (!initialized) return;
    if (!isAuthenticated) {
      // Full navigation — Next client router breaks with output:'export' + trailingSlash
      window.location.replace(ROUTES.login);
      return;
    }
    // Normalize trailing slash (next.config trailingSlash: true) so home "/" still bypasses RBAC.
    const normalizedPath = pathname === '/' ? '/' : (pathname || '/').replace(/\/+$/, '');
    const bypassPaths = [ROUTES.home, ROUTES.unauthorized, '/unauthorized', '/unauthorized/'];
    if (!bypassPaths.includes(normalizedPath as any) && !hasAccess(normalizedPath)) {
      window.location.replace(ROUTES.unauthorized);
    }
  }, [initialized, isAuthenticated, pathname, hasAccess]);

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
