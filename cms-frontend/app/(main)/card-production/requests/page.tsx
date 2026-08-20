'use client';

import React, { useRef, useState } from 'react';
import Link from 'next/link';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Toast } from 'primereact/toast';
import { Tag } from 'primereact/tag';
import { useMutation, useQuery } from '@tanstack/react-query';
import * as CardRequestService from '@/services/cards/CardRequestService';
import * as CardGenerationService from '@/services/cards/CardGenerationService';
import { ConfirmActionDialog } from '@/components/ui';
import type { CardRequest } from '@/types/card';
import { labelCardRequestSource } from '@/lib/cardRequestLabels';

export default function CardRequestsPage() {
  const toast = useRef<Toast>(null);
  const [rejectDialogOpen, setRejectDialogOpen] = useState(false);
  const [requestToReject, setRequestToReject] = useState<CardRequest | null>(null);

  const { data: checkerList = [], isLoading, refetch } = useQuery({
    queryKey: ['card-requests-checker'],
    queryFn: CardRequestService.getCheckerList,
  });

  const rejectMutation = useMutation({
    mutationFn: (requestId: number) => CardRequestService.rejectCardRequest(requestId),
    onSuccess: () => {
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Request rejected', life: 3000 });
      setRejectDialogOpen(false);
      setRequestToReject(null);
      refetch();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const approveMutation = useMutation({
    mutationFn: (requestId: number) => CardGenerationService.approveAndGenerate(requestId),
    onSuccess: (data) => {
      const detail = data.exportFilePath
        ? `Card generated and export file created. File: ${data.exportFilePath}`
        : data.panMasked
          ? `Card generated: ${data.panMasked}`
          : 'Request approved and card generated';
      toast.current?.show({ severity: 'success', summary: 'Success', detail, life: 5000 });
      refetch();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const openRejectConfirm = (row: CardRequest) => {
    setRequestToReject(row);
    setRejectDialogOpen(true);
  };

  const confirmReject = () => {
    if (requestToReject) rejectMutation.mutate(requestToReject.requestId);
  };

  const progressBody = (r: CardRequest) => {
    const flag = r.progressFlag;
    if (flag == null) return '—';
    const severity: 'danger' | 'success' | 'info' | 'warning' = flag === 1 ? 'success' : flag === 0 ? 'warning' : 'info';
    const value = flag === 1 ? 'Done' : flag === 0 ? 'Pending' : String(flag);
    return <Tag value={value} severity={severity} />;
  };

  const createdBody = (r: CardRequest) => (r.createdOn ? new Date(r.createdOn).toLocaleString() : '—');

  const sourceBody = (r: CardRequest) => {
    const label = labelCardRequestSource(r.requestTypeId);
    const severity: 'info' | 'warning' | 'success' | 'secondary' =
      label === 'Change card type' ? 'info' : label === 'Replacement' ? 'warning' : 'secondary';
    return <Tag value={label} severity={severity} />;
  };

  const actionBody = (row: CardRequest) => (
    <div className="flex gap-1">
      <Button
        label="Approve"
        icon="pi pi-check"
        severity="success"
        size="small"
        onClick={() => approveMutation.mutate(row.requestId)}
        loading={approveMutation.isPending && approveMutation.variables === row.requestId}
      />
      <Button
        label="Reject"
        icon="pi pi-times"
        severity="secondary"
        size="small"
        onClick={() => openRejectConfirm(row)}
      />
    </div>
  );

  const emptyTemplate = () => (
    <div className="text-center py-6 text-color-secondary">
      <p className="m-0 mb-2">No card requests to review.</p>
      <Link href="/card-production/new-request">
        <Button label="New request" icon="pi pi-plus" size="small" text />
      </Link>
    </div>
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4 flex justify-content-between align-items-start flex-wrap gap-2">
        <div>
          <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Card requests (Checker)</h1>
          <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
            Review and approve or reject card requests.
          </p>
        </div>
        <Link href="/card-production/requests/search">
          <Button label="Search requests" icon="pi pi-search" text />
        </Link>
      </div>
      <DataTable
        value={checkerList}
        loading={isLoading}
        paginator
        rows={10}
        emptyMessage={emptyTemplate()}
      >
        <Column field="requestId" header="ID" />
        <Column header="Request source" body={sourceBody} />
        <Column field="relationshipNum" header="Relationship" />
        <Column field="accountNum" header="Account" />
        <Column field="cardTitle" header="Title" />
        <Column header="Type" body={(r: CardRequest) => r.cardTypeName ?? r.cardTypeCode ?? '—'} />
        <Column header="Product" body={(r: CardRequest) => r.productName ?? r.productCode ?? '—'} />
        <Column field="branchName" header="Branch" body={(r: CardRequest) => r.branchName ?? r.branchCode ?? '—'} />
        <Column header="Progress" body={progressBody} />
        <Column header="Created" body={createdBody} />
        <Column header="Actions" body={actionBody} style={{ width: '8rem' }} />
      </DataTable>

      <ConfirmActionDialog
        visible={rejectDialogOpen}
        onHide={() => { setRejectDialogOpen(false); setRequestToReject(null); }}
        message={requestToReject ? `Reject card request #${requestToReject.requestId}?` : ''}
        detail="This action cannot be undone."
        variant="reject"
        acceptLabel="Reject"
        onAccept={confirmReject}
        loading={rejectMutation.isPending}
      />
    </div>
  );
}
