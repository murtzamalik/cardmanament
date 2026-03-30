'use client';

import React, { useCallback, useState } from 'react';
import Link from 'next/link';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable, type DataTableStateEvent } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { Tag } from 'primereact/tag';
import { useQuery } from '@tanstack/react-query';
import * as CardRequestService from '@/services/cards/CardRequestService';
import { FormSection, FormField } from '@/components/ui';
import type { CardRequest } from '@/types/card';

const isProcessedOptions = [
  { label: 'All', value: undefined },
  { label: 'Processed', value: 1 },
  { label: 'Not processed', value: 0 },
];

export default function CardRequestSearchPage() {
  const toast = React.useRef<Toast>(null);
  const [filters, setFilters] = useState({
    relationshipNum: '',
    branchCode: '',
    isProcessed: undefined as number | undefined,
    page: 0,
    size: 20,
  });

  const { data: pageData, isLoading, refetch } = useQuery({
    queryKey: ['card-requests-search', filters],
    queryFn: () =>
      CardRequestService.searchCardRequests({
        relationshipNum: filters.relationshipNum || undefined,
        branchCode: filters.branchCode || undefined,
        isProcessed: filters.isProcessed,
        page: filters.page,
        size: filters.size,
      }),
  });

  const content = pageData?.content ?? [];
  const totalRecords = pageData?.totalElements ?? 0;

  const onPage = useCallback((e: DataTableStateEvent) => {
    const page = e.page ?? (e.rows ? Math.floor(e.first / e.rows) : 0);
    const rows = e.rows ?? 20;
    setFilters((s) => ({ ...s, page, size: rows }));
  }, []);

  const progressBody = (r: CardRequest) => {
    const flag = r.progressFlag;
    if (flag == null) return '—';
    const severity: 'danger' | 'success' | 'info' | 'warning' = flag === 1 ? 'success' : flag === 0 ? 'warning' : 'info';
    const value = flag === 1 ? 'Done' : flag === 0 ? 'Pending' : String(flag);
    return <Tag value={value} severity={severity} />;
  };

  const emptyTemplate = () => (
    <div className="text-center py-6 text-color-secondary">
      <p className="m-0">No card requests match your filters.</p>
      <Link href="/card-production/new-request" className="mt-2 inline-block">
        <Button label="New request" icon="pi pi-plus" size="small" text />
      </Link>
    </div>
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Search card requests</h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Find card requests by relationship, branch, or processed status.
        </p>
      </div>
      <FormSection title="Filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Relationship" htmlFor="search-rel" className="mb-0">
            <InputText
              id="search-rel"
              placeholder="Relationship #"
              value={filters.relationshipNum}
              onChange={(e) => setFilters((s) => ({ ...s, relationshipNum: e.target.value, page: 0 }))}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Branch code" htmlFor="search-branch" className="mb-0">
            <InputText
              id="search-branch"
              placeholder="Branch code"
              value={filters.branchCode}
              onChange={(e) => setFilters((s) => ({ ...s, branchCode: e.target.value, page: 0 }))}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Processed" htmlFor="search-processed" className="mb-0">
            <Dropdown
              inputId="search-processed"
              placeholder="All"
              value={filters.isProcessed}
              options={isProcessedOptions}
              onChange={(e) => setFilters((s) => ({ ...s, isProcessed: e.value, page: 0 }))}
              className="w-12rem"
            />
          </FormField>
          <Button label="Search" icon="pi pi-search" onClick={() => refetch()} />
          <Link href="/card-production/requests">
            <Button label="Checker queue" icon="pi pi-list" text />
          </Link>
        </div>
      </FormSection>
      <DataTable
        value={content}
        loading={isLoading}
        paginator
        rows={filters.size}
        totalRecords={totalRecords}
        lazy
        first={filters.page * filters.size}
        onPage={onPage}
        rowsPerPageOptions={[10, 20, 50]}
        emptyMessage={emptyTemplate()}
      >
        <Column field="requestId" header="ID" />
        <Column field="relationshipNum" header="Relationship" />
        <Column field="accountNum" header="Account" />
        <Column field="cardTitle" header="Title" />
        <Column field="cardTypeName" header="Type" />
        <Column field="productName" header="Product" />
        <Column field="branchName" header="Branch" body={(r: CardRequest) => r.branchName ?? r.branchCode ?? '—'} />
        <Column header="Processed" body={(r: CardRequest) => (r.isProcessed === 1 ? 'Yes' : 'No')} />
        <Column header="Progress" body={progressBody} />
        <Column header="Created" body={(r: CardRequest) => (r.createdOn ? new Date(r.createdOn).toLocaleString() : '—')} />
      </DataTable>
    </div>
  );
}
