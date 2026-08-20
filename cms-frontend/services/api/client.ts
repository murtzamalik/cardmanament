import axios, { AxiosInstance, AxiosError } from 'axios';
import { config } from '@/lib/constants';
import type { ApiResponse } from '@/types/api';

const TOKEN_KEY = 'cms_token';

export function getStoredToken(): string | null {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem(TOKEN_KEY);
}

export function setStoredToken(token: string): void {
  if (typeof window === 'undefined') return;
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearStoredToken(): void {
  if (typeof window === 'undefined') return;
  localStorage.removeItem(TOKEN_KEY);
}

export interface IApiClient {
  get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>>;
  post<T>(url: string, data?: unknown): Promise<ApiResponse<T>>;
  put<T>(url: string, data?: unknown): Promise<ApiResponse<T>>;
  delete<T>(url: string): Promise<ApiResponse<T>>;
}

function createApiClient(onUnauthorized: () => void): IApiClient {
  const instance: AxiosInstance = axios.create({
    baseURL: config.apiBaseUrl,
    headers: { 'Content-Type': 'application/json' },
    withCredentials: true,
  });

  instance.interceptors.request.use((req) => {
    const token = getStoredToken();
    if (token) req.headers.Authorization = `Bearer ${token}`;
    return req;
  });

  instance.interceptors.response.use(
    (res) => {
      const body = res.data as ApiResponse<unknown>;
      return res;
    },
    async (err: AxiosError<ApiResponse<unknown>>) => {
      const status = err.response?.status;
      if (status === 401) {
        onUnauthorized();
      }
      const body = err.response?.data;
      const msg =
        body &&
        typeof body === 'object' &&
        'message' in body &&
        typeof (body as ApiResponse<unknown>).message === 'string'
          ? (body as ApiResponse<unknown>).message
          : null;
      if (msg && msg.length > 0) {
        return Promise.reject(new Error(msg));
      }
      return Promise.reject(err);
    }
  );

  return {
    async get<T>(url: string, params?: Record<string, unknown>): Promise<ApiResponse<T>> {
      const res = await instance.get<ApiResponse<T>>(url, { params });
      return res.data;
    },
    async post<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
      const res = await instance.post<ApiResponse<T>>(url, data);
      return res.data;
    },
    async put<T>(url: string, data?: unknown): Promise<ApiResponse<T>> {
      const res = await instance.put<ApiResponse<T>>(url, data);
      return res.data;
    },
    async delete<T>(url: string): Promise<ApiResponse<T>> {
      const res = await instance.delete<ApiResponse<T>>(url);
      return res.data;
    },
  };
}

let apiClientInstance: IApiClient | null = null;

export function initApiClient(onUnauthorized: () => void): IApiClient {
  apiClientInstance = createApiClient(onUnauthorized);
  return apiClientInstance;
}

export function getApiClient(): IApiClient {
  if (!apiClientInstance) {
    throw new Error('ApiClient not initialized. Call initApiClient from AuthProvider.');
  }
  return apiClientInstance;
}
