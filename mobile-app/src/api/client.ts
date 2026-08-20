import { API_BASE_URL } from '../config/api';
import type { ResponseWrapper } from '../types/cms';

export class ApiError extends Error {
  code: number;
  constructor(code: number, message: string) {
    super(message);
    this.code = code;
  }
}

export async function apiPost<T>(
  path: string,
  body: unknown,
): Promise<ResponseWrapper<T>> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    let message = `HTTP ${res.status}`;
    try {
      const err = (await res.json()) as ResponseWrapper<unknown>;
      message = err.responseMessage || message;
    } catch {
      /* ignore */
    }
    throw new ApiError(res.status, message);
  }
  return (await res.json()) as ResponseWrapper<T>;
}

export async function apiGet<T>(path: string): Promise<ResponseWrapper<T>> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    method: 'GET',
    headers: { Accept: 'application/json' },
  });
  if (!res.ok) {
    throw new ApiError(res.status, `HTTP ${res.status}`);
  }
  return (await res.json()) as ResponseWrapper<T>;
}

export function unwrap<T>(
  wrapper: ResponseWrapper<T>,
  allowCodes: number[] = [0],
): T | null {
  if (!allowCodes.includes(wrapper.responseCode)) {
    throw new ApiError(
      wrapper.responseCode,
      wrapper.responseMessage || 'Request failed',
    );
  }
  return wrapper.responseBody;
}
