'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function ResponseCodesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('responseCodes')} entityLabel="Response code" entityLabelPlural="Response codes" />;
}
