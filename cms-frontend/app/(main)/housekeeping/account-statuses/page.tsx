'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function AccountStatusesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('accountStatuses')} entityLabel="Account status" entityLabelPlural="Account statuses" />;
}
