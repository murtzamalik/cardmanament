'use client';

import { createContext, useContext } from 'react';
import type { LabScreen } from '@/services/cms-app/types';

interface LabNavValue {
  screen: LabScreen;
  go: (screen: LabScreen) => void;
}

export const LabNavContext = createContext<LabNavValue | null>(null);

export function useLabNav(): LabNavValue {
  const ctx = useContext(LabNavContext);
  if (!ctx) throw new Error('useLabNav must be used inside CmsAppLab');
  return ctx;
}
