import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as refDataService from '@/services/reference-data/refDataService';
import type { RefDataConfig, RefDataRecord } from '@/types/reference-data';

export function useReferenceDataList(config: RefDataConfig) {
  return useQuery({
    queryKey: ['reference-data', config.apiPath],
    queryFn: () => refDataService.refDataGetAll(config.apiPath),
  });
}

export function useReferenceDataCreate(config: RefDataConfig) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: RefDataRecord) => refDataService.refDataCreate(config.apiPath, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['reference-data', config.apiPath] }),
  });
}

export function useReferenceDataUpdate(config: RefDataConfig) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: string | number; body: RefDataRecord }) => refDataService.refDataUpdate(config.apiPath, id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['reference-data', config.apiPath] }),
  });
}

export function useReferenceDataDelete(config: RefDataConfig) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string | number) => refDataService.refDataDelete(config.apiPath, id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['reference-data', config.apiPath] }),
  });
}
