import { getApiClient } from '@/services/api/client';
import type { RefDataRecord } from '@/types/reference-data';

export async function refDataGetAll(apiPath: string): Promise<RefDataRecord[]> {
  const client = getApiClient();
  const res = await client.get<RefDataRecord[]>(apiPath);
  if (!res.success || res.data == null) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function refDataGetById(apiPath: string, id: string | number): Promise<RefDataRecord | null> {
  const client = getApiClient();
  const res = await client.get<RefDataRecord>(`${apiPath}/${encodeURIComponent(String(id))}`);
  return res.success ? (res.data ?? null) : null;
}

export async function refDataCreate(apiPath: string, body: RefDataRecord): Promise<RefDataRecord> {
  const client = getApiClient();
  const res = await client.post<RefDataRecord>(apiPath, body);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
  return res.data;
}

export async function refDataUpdate(apiPath: string, id: string | number, body: RefDataRecord): Promise<RefDataRecord> {
  const client = getApiClient();
  const res = await client.put<RefDataRecord>(`${apiPath}/${encodeURIComponent(String(id))}`, body);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
  return res.data;
}

export async function refDataDelete(apiPath: string, id: string | number): Promise<void> {
  const client = getApiClient();
  const res = await client.delete<unknown>(`${apiPath}/${encodeURIComponent(String(id))}`);
  if (!res.success) throw new Error(res.message ?? 'Delete failed');
}
