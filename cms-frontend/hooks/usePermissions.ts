import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { permissionService } from '@/services/security/PermissionService';
import type { PermissionCreateRequest } from '@/types/permission';

const QUERY_KEY = ['permissions'] as const;

export function usePermissions() {
  return useQuery({
    queryKey: QUERY_KEY,
    queryFn: () => permissionService.getAll(),
  });
}

export function usePermissionCreate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (req: PermissionCreateRequest) => permissionService.create(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}
