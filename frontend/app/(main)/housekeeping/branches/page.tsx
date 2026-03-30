'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function BranchesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('branches')} entityLabel="Branch" entityLabelPlural="Branches" />;
}
