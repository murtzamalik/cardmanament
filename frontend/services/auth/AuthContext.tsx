'use client';

import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { initApiClient, clearStoredToken, getStoredToken } from '@/services/api/client';
import { AuthService } from '@/services/auth/AuthService';
import type { LoginResponse } from '@/types/auth';
import { ROUTES } from '@/lib/constants';

export interface AuthUser {
  loginId: string;
  fullName: string;
  roles: string[];
}

interface AuthState {
  isAuthenticated: boolean;
  user: AuthUser | null;
  error: string | null;
  /** False until we have read token from localStorage (avoids redirect on refresh). */
  initialized: boolean;
}

interface AuthContextValue extends AuthState {
  login: (loginId: string, password: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

const authService = new AuthService();

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const [user, setUser] = useState<AuthUser | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [hasToken, setHasToken] = useState(false);
  /** Set true after reading localStorage so we don't redirect to login before token is restored. */
  const [initialized, setInitialized] = useState(false);

  const onUnauthorized = useCallback(() => {
    clearStoredToken();
    setUser(null);
    setHasToken(false);
    router.push(ROUTES.login);
  }, [router]);

  useEffect(() => {
    initApiClient(onUnauthorized);
  }, [onUnauthorized]);

  useEffect(() => {
    const token = getStoredToken();
    setHasToken(!!token);
    if (token) {
      authService.refresh().then((res) => {
        if (res) {
          setUser({
            loginId: res.loginId,
            fullName: res.fullName ?? '',
            roles: res.roles ?? [],
          });
        }
      }).catch(() => { /* keep token, user may stay null */ });
    }
    setInitialized(true);
  }, []);

  const isAuthenticated = hasToken;

  const login = useCallback(
    async (loginId: string, password: string) => {
      setError(null);
      try {
        const res = await authService.login(loginId, password);
        setUser({
          loginId: res.loginId,
          fullName: res.fullName ?? '',
          roles: res.roles ?? [],
        });
        setHasToken(true);
        router.push(ROUTES.home);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Login failed');
        throw e;
      }
    },
    [router]
  );

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    setHasToken(false);
    setError(null);
    router.push(ROUTES.login);
  }, [router]);

  const clearError = useCallback(() => setError(null), []);

  const value: AuthContextValue = useMemo(
    () => ({
      isAuthenticated,
      user,
      error,
      initialized,
      login,
      logout,
      clearError,
    }),
    [isAuthenticated, user, error, initialized, login, logout, clearError]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
