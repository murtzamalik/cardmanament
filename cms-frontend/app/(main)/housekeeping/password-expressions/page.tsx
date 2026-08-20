'use client';

import { ReferenceDataCrudPage } from '@/components/reference-data/ReferenceDataCrudPage';
import { getReferenceDataConfig } from '@/lib/referenceDataConfig';

export default function PasswordExpressionsPage() {
  return <ReferenceDataCrudPage config={getReferenceDataConfig('passwordExpressions')} entityLabel="Password expression" entityLabelPlural="Password expressions" />;
}
