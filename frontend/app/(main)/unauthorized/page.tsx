'use client';

import React from 'react';
import { Button } from 'primereact/button';
import { useRouter } from 'next/navigation';
import { ROUTES } from '@/lib/constants';

export default function UnauthorizedPage() {
  const router = useRouter();
  return (
    <div className="card text-center">
      <h1 className="mb-2">Unauthorized</h1>
      <p className="text-color-secondary mb-4">You do not have access to this page.</p>
      <Button label="Go to Dashboard" icon="pi pi-home" onClick={() => router.push(ROUTES.home)} />
    </div>
  );
}
