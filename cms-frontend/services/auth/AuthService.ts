import axios from 'axios';
import { getApiClient } from '@/services/api/client';
import {
  getStoredToken,
  setStoredToken,
  clearStoredToken,
} from '@/services/api/client';
import { config } from '@/lib/constants';
import type { LoginResponse } from '@/types/auth';
import type { ApiResponse } from '@/types/api';
import type { IAuthService } from '@/types/auth';

const AUTH_LOGIN = '/api/auth/login';
const AUTH_REFRESH = '/api/auth/refresh';
const AUTH_CHANGE_PASSWORD = '/api/auth/change-password';

export class AuthService implements IAuthService {
  async login(loginId: string, password: string): Promise<LoginResponse> {
    const url = `${config.apiBaseUrl.replace(/\/$/, '')}${AUTH_LOGIN}`;
    try {
      const { data: res } = await axios.post<ApiResponse<LoginResponse>>(url, { loginId, password }, {
        headers: { 'Content-Type': 'application/json' },
        withCredentials: true,
      });
      if (!res.success || !res.data?.token) {
        throw new Error(res.message || 'Login failed');
      }
      setStoredToken(res.data.token);
      return res.data;
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const apiMsg = (err.response?.data as ApiResponse<unknown> | undefined)?.message;
        // Keep lock / validation messages from API; normalize generic auth failures
        if (typeof apiMsg === 'string' && apiMsg.trim()) {
          const normalized =
            apiMsg === 'Unauthorized' || apiMsg.toLowerCase() === 'unauthorized'
              ? 'Invalid credentials'
              : apiMsg;
          throw new Error(normalized);
        }
        if (err.response?.status === 401) {
          throw new Error('Invalid credentials');
        }
      }
      throw err instanceof Error ? err : new Error('Login failed');
    }
  }

  async refresh(): Promise<LoginResponse | null> {
    const token = getStoredToken();
    if (!token) return null;
    try {
      const client = getApiClient();
      const res = await client.post<LoginResponse>(AUTH_REFRESH, { token });
      if (res.success && res.data?.token) {
        setStoredToken(res.data.token);
        return res.data;
      }
    } catch {
      // Do not clear token here; 401 from any API will trigger onUnauthorized and clear
      // so we keep session across refresh even if refresh endpoint fails temporarily
    }
    return null;
  }

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    const client = getApiClient();
    const res = await client.post<null>(AUTH_CHANGE_PASSWORD, {
      currentPassword,
      newPassword,
    });
    if (!res.success) {
      throw new Error(res.message || 'Failed to change password');
    }
  }

  logout(): void {
    clearStoredToken();
  }
}
