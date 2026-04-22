import { getApiClient } from '@/services/api/client';
import type { RoleResponse, RoleCreateRequest, RoleUpdateRequest } from '@/types/role';
import type { MenuResponse } from '@/types/menu';

const BASE = '/api/roles';

export const roleService = {
  async getAll(): Promise<RoleResponse[]> {
    const client = getApiClient();
    const res = await client.get<RoleResponse[]>(BASE);
    if (!res.success || res.data == null) return [];
    return res.data;
  },

  async getById(id: number | string): Promise<RoleResponse | null> {
    const client = getApiClient();
    const res = await client.get<RoleResponse>(`${BASE}/${id}`);
    return res.success ? res.data ?? null : null;
  },

  async create(req: RoleCreateRequest): Promise<RoleResponse> {
    const client = getApiClient();
    const res = await client.post<RoleResponse>(BASE, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
    return res.data;
  },

  async update(id: number | string, req: RoleUpdateRequest): Promise<RoleResponse> {
    const client = getApiClient();
    const res = await client.put<RoleResponse>(`${BASE}/${id}`, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
    return res.data;
  },

  async delete(id: number | string): Promise<void> {
    const client = getApiClient();
    const res = await client.delete<unknown>(`${BASE}/${id}`);
    if (!res.success) throw new Error(res.message ?? 'Delete failed');
  },

  async getRoleMenus(roleCode: string): Promise<MenuResponse[]> {
    const client = getApiClient();
    const res = await client.get<MenuResponse[]>(`${BASE}/${encodeURIComponent(roleCode)}/menus`);
    if (!res.success || !res.data) return [];
    return Array.isArray(res.data) ? res.data : [];
  },

  async assignRoleMenus(roleCode: string, menuIds: number[]): Promise<void> {
    const client = getApiClient();
    const res = await client.post<unknown>(`${BASE}/${encodeURIComponent(roleCode)}/menus`, { menuIds });
    if (!res.success) throw new Error(res.message ?? 'Role menu assignment failed');
  },

  async removeRoleMenu(roleCode: string, menuId: number): Promise<void> {
    const client = getApiClient();
    const res = await client.delete<unknown>(`${BASE}/${encodeURIComponent(roleCode)}/menus/${menuId}`);
    if (!res.success) throw new Error(res.message ?? 'Role menu remove failed');
  },
};
