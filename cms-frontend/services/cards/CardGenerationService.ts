import { getApiClient } from '@/services/api/client';
import type { CardRequest, CardGenerationResult } from '@/types/card';

const BASE = '/api/card-generation';

export async function getCardRequestByCode(relationshipNum: string, accountNum: string): Promise<CardRequest[]> {
  const client = getApiClient();
  const res = await client.post<CardRequest[]>(`${BASE}/request-by-code`, { relationshipNum, accountNum });
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function processNewCardGeneration(requestId: number): Promise<CardGenerationResult> {
  const client = getApiClient();
  const res = await client.post<CardGenerationResult>(`${BASE}/process`, { requestId });
  if (!res.success || !res.data) throw new Error(res.message ?? 'Generation failed');
  return res.data;
}

export async function updateCardRequestProgress(requestId: number, progressFlag: number): Promise<void> {
  const client = getApiClient();
  const res = await client.put<unknown>(`${BASE}/request/${requestId}/progress?progressFlag=${progressFlag}`);
  if (!res.success) throw new Error(res.message ?? 'Update failed');
}

export async function approveAndGenerate(requestId: number): Promise<CardGenerationResult> {
  const client = getApiClient();
  const res = await client.post<CardGenerationResult>(`${BASE}/request/${requestId}/approve-and-generate`);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Approve and generate failed');
  return res.data;
}
