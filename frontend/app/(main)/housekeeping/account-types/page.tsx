'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function AccountTypesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('accountTypes')} entityLabel="Account type" entityLabelPlural="Account types" />;
}
