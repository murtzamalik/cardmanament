import { getApiClient } from '@/services/api/client';
import type { DashboardSummary } from '@/types/dashboard';

const BASE = '/api/dashboard';

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const client = getApiClient();
  const res = await client.get<DashboardSummary>(`${BASE}/summary`);
  if (!res.success || !res.data) {
    throw new Error(res.message ?? 'Failed to load dashboard');
  }
  return {
    pendingApproval: res.data.pendingApproval ?? 0,
    openRequests: res.data.openRequests ?? 0,
    issuedToday: res.data.issuedToday ?? 0,
    expiringIn30Days: res.data.expiringIn30Days ?? 0,
    hotCards: res.data.hotCards ?? 0,
    requestsByStatus: res.data.requestsByStatus ?? {},
    cardsByStatus: res.data.cardsByStatus ?? {},
    checkerQueue: Array.isArray(res.data.checkerQueue) ? res.data.checkerQueue : [],
    makerQueue: Array.isArray(res.data.makerQueue) ? res.data.makerQueue : [],
    expiringSoon: Array.isArray(res.data.expiringSoon) ? res.data.expiringSoon : [],
  };
}
