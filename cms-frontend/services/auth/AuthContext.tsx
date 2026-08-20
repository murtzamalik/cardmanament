'use client';

import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { initApiClient, clearStoredToken, getStoredToken } from '@/services/api/client';
import { AuthService } from '@/services/auth/AuthService';
import { ROUTES } from '@/lib/constants';
import type { MenuResponse } from '@/types/menu';
import { clearStoredMenus, getStoredMenus, hasMenuAccess, setStoredMenus } from '@/lib/rbac';

export interface AuthUser {
  loginId: string;
  fullName: string;
  roles: string[];
  menus: MenuResponse[];
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
  hasAccess: (path: string) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

const authService = new AuthService();

/** Idle timeout aligned with backend JWT (15 minutes). */
const IDLE_TIMEOUT_MS = 15 * 60 * 1000;
const IDLE_EVENTS: (keyof WindowEventMap)[] = ['mousedown', 'mousemove', 'keydown', 'scroll', 'touchstart', 'click'];

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [hasToken, setHasToken] = useState(false);
  /** Set true after reading localStorage so we don't redirect to login before token is restored. */
  const [initialized, setInitialized] = useState(false);
  const idleTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const onUnauthorized = useCallback(() => {
    clearStoredToken();
    clearStoredMenus();
    setUser(null);
    setHasToken(false);
    // Full navigation — avoids Next client-router 404 with static export
    window.location.assign(ROUTES.login);
  }, []);

  useEffect(() => {
    initApiClient(onUnauthorized);
  }, [onUnauthorized]);

  useEffect(() => {
    const token = getStoredToken();
    setHasToken(!!token);
    if (token) {
      authService.refresh().then((res) => {
        if (res) {
          const menus = res.menus ?? getStoredMenus();
          setStoredMenus(menus);
          setUser({
            loginId: res.loginId,
            fullName: res.fullName ?? '',
            roles: res.roles ?? [],
            menus,
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
        const menus = res.menus ?? [];
        setStoredMenus(menus);
        setUser({
          loginId: res.loginId,
          fullName: res.fullName ?? '',
          roles: res.roles ?? [],
          menus,
        });
        setHasToken(true);
        window.location.assign(ROUTES.home);
      } catch (e) {
        setError(e instanceof Error ? e.message : 'Login failed');
        throw e;
      }
    },
    []
  );

  const logout = useCallback(() => {
    authService.logout();
    clearStoredMenus();
    setUser(null);
    setHasToken(false);
    setError(null);
    window.location.assign(ROUTES.login);
  }, []);

  // Idle logout: no user activity for 15 minutes → clear session
  useEffect(() => {
    if (!hasToken) {
      if (idleTimerRef.current) {
        clearTimeout(idleTimerRef.current);
        idleTimerRef.current = null;
      }
      return;
    }

    const resetIdleTimer = () => {
      if (idleTimerRef.current) clearTimeout(idleTimerRef.current);
      idleTimerRef.current = setTimeout(() => {
        logout();
      }, IDLE_TIMEOUT_MS);
    };

    resetIdleTimer();
    IDLE_EVENTS.forEach((event) => window.addEventListener(event, resetIdleTimer, { passive: true }));

    return () => {
      if (idleTimerRef.current) clearTimeout(idleTimerRef.current);
      IDLE_EVENTS.forEach((event) => window.removeEventListener(event, resetIdleTimer));
    };
  }, [hasToken, logout]);

  const clearError = useCallback(() => setError(null), []);
  const hasAccess = useCallback((path: string) => hasMenuAccess(path, user?.menus), [user?.menus]);

  const value: AuthContextValue = useMemo(
    () => ({
      isAuthenticated,
      user,
      error,
      initialized,
      login,
      logout,
      clearError,
      hasAccess,
    }),
    [isAuthenticated, user, error, initialized, login, logout, clearError, hasAccess]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
