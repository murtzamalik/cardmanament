import { getApiClient } from '@/services/api/client';
import type { MenuCreateRequest, MenuResponse, MenuUpdateRequest } from '@/types/menu';

const BASE = '/api/menus';
const MY_MENUS = '/api/my-menus';

export const menuService = {
  async getAll(): Promise<MenuResponse[]> {
    const client = getApiClient();
    const res = await client.get<MenuResponse[]>(BASE);
    if (!res.success || !res.data) return [];
    return Array.isArray(res.data) ? res.data : [];
  },

  async getMyMenus(): Promise<MenuResponse[]> {
    const client = getApiClient();
    const res = await client.get<MenuResponse[]>(MY_MENUS);
    if (!res.success || !res.data) return [];
    return Array.isArray(res.data) ? res.data : [];
  },

  async create(req: MenuCreateRequest): Promise<MenuResponse> {
    const client = getApiClient();
    const res = await client.post<MenuResponse>(BASE, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Create menu failed');
    return res.data;
  },

  async update(id: number, req: MenuUpdateRequest): Promise<MenuResponse> {
    const client = getApiClient();
    const res = await client.put<MenuResponse>(`${BASE}/${id}`, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Update menu failed');
    return res.data;
  },

  async delete(id: number): Promise<void> {
    const client = getApiClient();
    const res = await client.delete<unknown>(`${BASE}/${id}`);
    if (!res.success) throw new Error(res.message ?? 'Delete menu failed');
  },
};
