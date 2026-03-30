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

export class AuthService implements IAuthService {
  async login(loginId: string, password: string): Promise<LoginResponse> {
    const url = `${config.apiBaseUrl.replace(/\/$/, '')}${AUTH_LOGIN}`;
    const { data: res } = await axios.post<ApiResponse<LoginResponse>>(url, { loginId, password }, {
      headers: { 'Content-Type': 'application/json' },
      withCredentials: true,
    });
    if (!res.success || !res.data?.token) {
      throw new Error(res.message || 'Login failed');
    }
    setStoredToken(res.data.token);
    return res.data;
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

  logout(): void {
    clearStoredToken();
  }
}
