'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery } from '@tanstack/react-query';
import * as CardGenerationService from '@/services/cards/CardGenerationService';
import { FormSection, FormField } from '@/components/ui';
import type { CardRequest } from '@/types/card';

export default function CardGenerationPage() {
  const toast = useRef<Toast>(null);
  const [relationshipNum, setRelationshipNum] = useState('');
  const [accountNum, setAccountNum] = useState('');

  const { data: requests = [], isLoading, refetch } = useQuery({
    queryKey: ['card-request-by-code', relationshipNum, accountNum],
    queryFn: () => CardGenerationService.getCardRequestByCode(relationshipNum, accountNum),
    enabled: !!relationshipNum && !!accountNum,
  });

  const processMutation = useMutation({
    mutationFn: (requestId: number) => CardGenerationService.processNewCardGeneration(requestId),
    onSuccess: (data) => {
      toast.current?.show({
        severity: 'success',
        summary: 'Card Generated',
        detail: data.panMasked ? `Card ${data.panMasked} created` : data.message,
        life: 5000,
      });
      refetch();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const loadRequests = () => {
    if (relationshipNum && accountNum) refetch();
  };

  const hasCriteria = !!relationshipNum && !!accountNum;
  const loadingRowId = processMutation.isPending && processMutation.variables != null ? processMutation.variables : null;

  const actionBody = (r: CardRequest) =>
    r.isProcessed !== 1 ? (
      <Button
        label="Generate"
        size="small"
        onClick={() => processMutation.mutate(r.requestId)}
        loading={loadingRowId === r.requestId}
      />
    ) : (
      '—'
    );

  const emptyTemplate = () => {
    if (!hasCriteria) {
      return (
        <div className="text-center py-6 text-color-secondary">
          <p className="m-0">Enter relationship and account, then click Load requests.</p>
        </div>
      );
    }
    return (
      <div className="text-center py-6 text-color-secondary">
        <p className="m-0">No requests found for this relationship and account.</p>
      </div>
    );
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Card generation</h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Look up card requests and generate cards.
        </p>
      </div>
      <FormSection title="Lookup request">
        <div className="grid p-fluid">
          <div className="col-12 md:col-4">
            <FormField label="Relationship number" htmlFor="rel-gen">
              <InputText
                id="rel-gen"
                placeholder="Relationship number"
                value={relationshipNum}
                onChange={(e) => setRelationshipNum(e.target.value)}
                className="w-full"
              />
            </FormField>
          </div>
          <div className="col-12 md:col-4">
            <FormField label="Account number" htmlFor="acct-gen">
              <InputText
                id="acct-gen"
                placeholder="Account number"
                value={accountNum}
                onChange={(e) => setAccountNum(e.target.value)}
                className="w-full"
              />
            </FormField>
          </div>
          <div className="col-12 md:col-4 flex align-items-end">
            <Button label="Load requests" icon="pi pi-search" onClick={loadRequests} disabled={!hasCriteria} />
          </div>
        </div>
      </FormSection>
      <DataTable
        value={requests}
        loading={isLoading}
        emptyMessage={emptyTemplate()}
      >
        <Column field="requestId" header="ID" />
        <Column field="relationshipNum" header="Relationship" />
        <Column field="accountNum" header="Account" />
        <Column field="cardTitle" header="Title" />
        <Column field="cardTypeName" header="Type" />
        <Column field="isProcessed" header="Processed" body={(r: CardRequest) => (r.isProcessed ? 'Yes' : 'No')} />
        <Column header="Action" body={actionBody} style={{ width: '8rem' }} />
      </DataTable>
    </div>
  );
}
