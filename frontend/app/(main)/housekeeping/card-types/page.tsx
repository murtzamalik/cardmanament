'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function CardTypesPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('cardTypes')} entityLabel="Card type" entityLabelPlural="Card types" />;
}
