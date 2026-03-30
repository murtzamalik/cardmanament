import { getApiClient } from '@/services/api/client';
import type { LimitProfile, LimitProfileCreateRequest, LimitProfileUpdateRequest } from '@/types/card';

const BASE = '/api/limit-profiles';

export async function getLimitProfiles(): Promise<LimitProfile[]> {
  const client = getApiClient();
  const res = await client.get<LimitProfile[]>(BASE);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function getLimitProfileById(id: number): Promise<LimitProfile | null> {
  const client = getApiClient();
  const res = await client.get<LimitProfile>(`${BASE}/${id}`);
  return res.success ? (res.data ?? null) : null;
}

export async function createLimitProfile(request: LimitProfileCreateRequest): Promise<LimitProfile> {
  const client = getApiClient();
  const res = await client.post<LimitProfile>(BASE, request);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
  return res.data;
}

export async function updateLimitProfile(id: number, request: LimitProfileUpdateRequest): Promise<LimitProfile> {
  const client = getApiClient();
  const res = await client.put<LimitProfile>(`${BASE}/${id}`, request);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
  return res.data;
}

export async function deleteLimitProfile(id: number): Promise<void> {
  const client = getApiClient();
  const res = await client.delete<unknown>(`${BASE}/${id}`);
  if (!res.success) throw new Error(res.message ?? 'Delete failed');
}
