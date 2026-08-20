import { getApiClient, getStoredToken } from '@/services/api/client';
import { config } from '@/lib/constants';
import type {
  Card,
  CardSearchRequest,
  CardUpdateRequest,
  CardDropdownsResponse,
  PageResponse,
  AccountOption,
  CardAccountLink,
  CustomerInfo,
  LinkCardAccountRequest,
  ExpirySearchRequest,
  ChangeCardTypeRequest,
} from '@/types/card';

const BASE = '/api/cards';

/** Download export file for card (requires auth). Throws if not found or request fails. */
export async function downloadExportFile(cardId: number, filename?: string): Promise<void> {
  const url = `${config.apiBaseUrl}${BASE}/${cardId}/export-file`;
  const token = getStoredToken();
  const res = await fetch(url, {
    method: 'GET',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (!res.ok) throw new Error(res.status === 404 ? 'Export file not found' : 'Download failed');
  const blob = await res.blob();
  const name = filename || res.headers.get('Content-Disposition')?.match(/filename="?([^";]+)"?/)?.[1] || `card_export_${cardId}.txt`;
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = name;
  a.click();
  URL.revokeObjectURL(a.href);
}

export async function getAllCards(): Promise<Card[]> {
  const client = getApiClient();
  const res = await client.get<Card[]>(BASE);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function searchCards(request: CardSearchRequest): Promise<PageResponse<Card>> {
  const client = getApiClient();
  const res = await client.post<PageResponse<Card>>(`${BASE}/search`, request);
  if (!res.success || !res.data) return { content: [], page: 0, size: 20, totalElements: 0, totalPages: 0 };
  return res.data;
}

/** Cards whose expiry falls within the given date range (inclusive). */
export async function searchCardsByExpiry(request: ExpirySearchRequest): Promise<Card[]> {
  const client = getApiClient();
  const res = await client.post<Card[]>(`${BASE}/expiry-search`, request);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

/** Bulk renew selected cards; returns renewed card IDs. */
export async function bulkRenewCards(cardIds: number[]): Promise<number[]> {
  const client = getApiClient();
  const res = await client.post<number[]>(`${BASE}/bulk-renew`, { cardIds });
  if (!res.success || !res.data) throw new Error(res.message ?? 'Bulk renew failed');
  return Array.isArray(res.data) ? res.data : [];
}

export async function changeCardType(cardId: number, body: ChangeCardTypeRequest): Promise<number> {
  const client = getApiClient();
  const res = await client.post<number>(`${BASE}/${cardId}/change-card-type`, body);
  if (!res.success || res.data == null) throw new Error(res.message ?? 'Change card type failed');
  return typeof res.data === 'number' ? res.data : Number(res.data);
}

/** Marks card inactive and creates a replacement card request; returns new request id. */
export async function createReplacementRequest(cardId: number): Promise<number> {
  const client = getApiClient();
  const res = await client.post<number>(`${BASE}/${cardId}/replacement-request`);
  if (!res.success || res.data == null) throw new Error(res.message ?? 'Replacement request failed');
  return typeof res.data === 'number' ? res.data : Number(res.data);
}

export async function getCardById(id: number): Promise<Card | null> {
  const client = getApiClient();
  const res = await client.get<Card>(`${BASE}/${id}`);
  return res.success ? (res.data ?? null) : null;
}

export async function getDropdowns(): Promise<CardDropdownsResponse> {
  const client = getApiClient();
  const res = await client.get<CardDropdownsResponse>(`${BASE}/dropdowns`);
  return res.success && res.data ? res.data : {};
}

/** Cards with production status 001 for the given card type (export-ready). */
export async function getExportReadyCards(request: {
  cardTypeId: number;
  pan?: string;
  relationshipNum?: string;
  dateFrom?: string;
  dateTo?: string;
}): Promise<Card[]> {
  const client = getApiClient();
  const res = await client.post<Card[]>(`${BASE}/export-ready`, request);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

/** Bulk export; returns server file path. Updates cards to status 002. */
export async function bulkExportCards(cardIds: number[]): Promise<string> {
  const client = getApiClient();
  const res = await client.post<string>(`${BASE}/bulk-export`, { cardIds });
  if (!res.success || res.data == null) throw new Error(res.message ?? 'Bulk export failed');
  return typeof res.data === 'string' ? res.data : String(res.data);
}

export async function getAvailableAccounts(relationshipNum?: string): Promise<AccountOption[]> {
  const client = getApiClient();
  const url = relationshipNum
    ? `${BASE}/available-accounts?relationshipNum=${encodeURIComponent(relationshipNum)}`
    : `${BASE}/available-accounts`;
  const res = await client.get<AccountOption[]>(url);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function linkCardAccount(request: LinkCardAccountRequest): Promise<void> {
  const client = getApiClient();
  const res = await client.post<unknown>(`${BASE}/link-account`, request);
  if (!res.success) throw new Error(res.message ?? 'Link failed');
}

export async function linkCardAccountByCardId(
  cardId: number,
  request: { accountNum: string; relationshipNum?: string; isOverallDefault?: boolean; isAcctTypeDefault?: boolean }
): Promise<void> {
  const client = getApiClient();
  const res = await client.post<unknown>(`${BASE}/${cardId}/link-account`, request);
  if (!res.success) throw new Error(res.message ?? 'Link failed');
}

export async function delinkCardAccountByCardId(cardId: number, accountNum: string): Promise<void> {
  const client = getApiClient();
  const res = await client.post<unknown>(`${BASE}/${cardId}/delink-account?accountNum=${encodeURIComponent(accountNum)}`);
  if (!res.success) throw new Error(res.message ?? 'Delink failed');
}

export async function updateCard(id: number, body: CardUpdateRequest): Promise<Card> {
  const client = getApiClient();
  const payload = { ...body, limitProfile: body.limitProfile === null ? '' : body.limitProfile };
  const res = await client.put<Card>(`${BASE}/${id}`, payload);
  if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
  return res.data;
}

/** Set or clear limit profile. Prefer limitProfileId when provided. */
export async function linkLimitProfile(id: number, limitProfileId?: number | null, limitProfile?: string): Promise<void> {
  const client = getApiClient();
  const params = new URLSearchParams();
  if (limitProfileId != null) params.set('limitProfileId', String(limitProfileId));
  else if (limitProfile != null && limitProfile !== '') params.set('limitProfile', limitProfile);
  const qs = params.toString();
  const url = qs ? `${BASE}/${id}/limit-profile?${qs}` : `${BASE}/${id}/limit-profile`;
  const res = await client.put<unknown>(url);
  if (!res.success) throw new Error(res.message ?? 'Update failed');
}

export async function getLinkedAccountsByCardId(id: number): Promise<CardAccountLink[]> {
  const client = getApiClient();
  const res = await client.get<CardAccountLink[]>(`${BASE}/${id}/linked-account`);
  if (!res.success || !res.data) return [];
  return Array.isArray(res.data) ? res.data : [];
}

export async function closeCard(id: number): Promise<void> {
  const client = getApiClient();
  const res = await client.post<unknown>(`${BASE}/${id}/close`);
  if (!res.success) throw new Error(res.message ?? 'Close failed');
}

export async function getCustomerByRelation(rel: string, linked?: string, cardId?: number): Promise<CustomerInfo> {
  const client = getApiClient();
  const params: Record<string, string | number> = { rel };
  if (linked != null) params.linked = linked;
  if (cardId != null) params.cardId = cardId;
  const q = new URLSearchParams(params as Record<string, string>).toString();
  const res = await client.get<CustomerInfo>(`${BASE}/customer-by-relation?${q}`);
  return res.success && res.data ? res.data : {};
}
