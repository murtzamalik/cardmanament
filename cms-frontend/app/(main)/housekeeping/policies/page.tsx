'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function PoliciesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('policies')} entityLabel="Policy" entityLabelPlural="Policies" />;
}
