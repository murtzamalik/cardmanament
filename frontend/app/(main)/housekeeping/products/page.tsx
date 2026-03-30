'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function ProductsPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('products')} entityLabel="Product" entityLabelPlural="Products" />;
}
