import { getApiClient } from '@/services/api/client';
import type { CardRequest, NewCardRequestCreate, CustomerInfo, PageResponse } from '@/types/card';

const BASE = '/api/card-requests';

export async function createCardRequest(request: NewCardRequestCreate): Promise<CardRequest> {
  const client = getApiClient();
  const res = await client.post<CardRequest>(BASE, request);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
  return res.data;
}

export async function rejectCardRequest(requestId: number): Promise<void> {
  const client = getApiClient();
  const res = await client.post<unknown>(`${BASE}/reject`, { requestId });
  if (!res.success) throw new Error(res.message ?? 'Reject failed');
}

export async function getCheckerList(): Promise<CardRequest[]> {
  const client = getApiClient();
  const res = await client.get<CardRequest[]>(`${BASE}/checker`);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function getMakerList(): Promise<CardRequest[]> {
  const client = getApiClient();
  const res = await client.get<CardRequest[]>(`${BASE}/maker`);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function updateCardRequest(id: number, request: Partial<NewCardRequestCreate>): Promise<CardRequest> {
  const client = getApiClient();
  const res = await client.put<CardRequest>(`${BASE}/${id}`, request);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
  return res.data;
}

export async function getCustomerInfo(relationshipNum: string): Promise<CustomerInfo> {
  const client = getApiClient();
  const res = await client.get<CustomerInfo>(`${BASE}/customer-info?relationshipNum=${encodeURIComponent(relationshipNum)}`);
  return res.success && res.data ? res.data : {};
}

export async function searchCardRequests(params: {
  relationshipNum?: string;
  branchCode?: string;
  isProcessed?: number;
  requestTypeId?: string;
  page?: number;
  size?: number;
}): Promise<PageResponse<CardRequest>> {
  const client = getApiClient();
  const q = new URLSearchParams();
  if (params.relationshipNum != null) q.set('relationshipNum', params.relationshipNum);
  if (params.branchCode != null) q.set('branchCode', params.branchCode);
  if (params.isProcessed != null) q.set('isProcessed', String(params.isProcessed));
  if (params.requestTypeId != null) q.set('requestTypeId', params.requestTypeId);
  if (params.page != null) q.set('page', String(params.page));
  if (params.size != null) q.set('size', String(params.size));
  const res = await client.get<PageResponse<CardRequest>>(`${BASE}/search?${q.toString()}`);
  if (!res.success || !res.data) return { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 };
  return res.data;
}

export async function getCardRequestById(id: number): Promise<CardRequest | null> {
  const client = getApiClient();
  const res = await client.get<CardRequest>(`${BASE}/${id}`);
  return res.success ? (res.data ?? null) : null;
}
