import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { userService } from '@/services/security/UserService';
import type { UserCreateRequest, UserUpdateRequest } from '@/types/user';

const QUERY_KEY = ['users'] as const;

export function useUsers() {
  return useQuery({
    queryKey: QUERY_KEY,
    queryFn: () => userService.getAll(),
  });
}

export function useUser(id: number | string | null) {
  return useQuery({
    queryKey: [...QUERY_KEY, id],
    queryFn: () => (id != null ? userService.getById(id) : Promise.resolve(null)),
    enabled: id != null,
  });
}

export function useUserCreate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (req: UserCreateRequest) => userService.create(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useUserUpdate() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, req }: { id: number | string; req: UserUpdateRequest }) => userService.update(id, req),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useUserDelete() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number | string) => userService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}
