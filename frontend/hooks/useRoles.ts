import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { roleService } from '@/services/security/RoleService';
import type { RoleCreateRequest, RoleUpdateRequest } from '@/types/role';

const QUERY_KEY = ['roles'] as const;

export function useRoles() {
  return useQuery({
    queryKey: QUERY_KEY,
    queryFn: () => roleService.getAll(),
  });
}

export function useRoleCreate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (req: RoleCreateRequest) => roleService.create(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useRoleUpdate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, req }: { id: number | string; req: RoleUpdateRequest }) => roleService.update(id, req),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useRoleDelete() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number | string) => roleService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}
