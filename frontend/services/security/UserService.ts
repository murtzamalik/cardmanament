import { getApiClient } from '@/services/api/client';
import type { UserResponse, UserCreateRequest, UserUpdateRequest } from '@/types/user';

const BASE = '/api/users';

export const userService = {
  async getAll(): Promise<UserResponse[]> {
    const client = getApiClient();
    const res = await client.get<UserResponse[]>(BASE);
    if (!res.success || res.data == null) return [];
    return res.data;
  },

  async getById(id: number | string): Promise<UserResponse | null> {
    const client = getApiClient();
    const res = await client.get<UserResponse>(`${BASE}/${id}`);
    return res.success ? res.data ?? null : null;
  },

  async create(req: UserCreateRequest): Promise<UserResponse> {
    const client = getApiClient();
    const res = await client.post<UserResponse>(BASE, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Create failed');
    return res.data;
  },

  async update(id: number | string, req: UserUpdateRequest): Promise<UserResponse> {
    const client = getApiClient();
    const res = await client.put<UserResponse>(`${BASE}/${id}`, req);
    if (!res.success || !res.data) throw new Error(res.message ?? 'Update failed');
    return res.data;
  },

  async delete(id: number | string): Promise<void> {
    const client = getApiClient();
    const res = await client.delete<unknown>(`${BASE}/${id}`);
    if (!res.success) throw new Error(res.message ?? 'Delete failed');
  },
};
