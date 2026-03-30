import { getApiClient } from '@/services/api/client';
import type { PermissionResponse, PermissionCreateRequest } from '@/types/permission';

const BASE = '/api/permissions';

export const permissionService = {
  async getAll(): Promise<PermissionResponse[]> {
    const client = getApiClient();
    const res = await client.get<PermissionResponse[]>(BASE);
    if (!res.success || res.data == null) return [];
    return res.data;
  },

  async getById(id: string): Promise<PermissionResponse | null> {
    const client = getApiClient();
    const res = await client.get<PermissionResponse>(`${BASE}/${encodeURIComponent(id)}`);
    return res.success ? res.data ?? null : null;
  },

  async create(req: PermissionCreateRequest): Promise<PermissionResponse> {
    const client = getApiClient();
    const res = await client.post<PermissionResponse>(BASE, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
    return res.data;
  },
};
