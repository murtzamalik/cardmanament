'use client';

import React, { useCallback, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { useMutation } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { FormSection, FormField } from '@/components/ui';
import type { Card } from '@/types/card';

function formatLocalDate(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

export default function CardsExpirySearchPage() {
  const router = useRouter();
  const toast = React.useRef<Toast>(null);
  const [dateFrom, setDateFrom] = useState<Date | null>(null);
  const [dateTo, setDateTo] = useState<Date | null>(null);
  const [rows, setRows] = useState<Card[]>([]);

  const searchMutation = useMutation({
    mutationFn: () => {
      if (!dateFrom || !dateTo) {
        return Promise.reject(new Error('Please select both date from and date to'));
      }
      if (dateFrom.getTime() > dateTo.getTime()) {
        return Promise.reject(new Error('Date from must be on or before date to'));
      }
      return CardService.searchCardsByExpiry({
        dateFrom: formatLocalDate(dateFrom),
        dateTo: formatLocalDate(dateTo),
      });
    },
    onSuccess: (data) => {
      setRows(data);
      toast.current?.show({
        severity: 'success',
        summary: 'Search complete',
        detail: `${data.length} card(s) found`,
        life: 3000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const actionBody = useCallback(
    (row: Card) => (
      <div className="flex gap-1">
        <Tooltip target={`.expiry-view-${row.cardId}`} content="View details" position="top" />
        <Button
          icon="pi pi-eye"
          label="View"
          text
          rounded
          size="small"
          className={`p-button-text expiry-view-${row.cardId}`}
          onClick={() => router.push(`/operations/cards/${row.cardId}`)}
        />
      </div>
    ),
    [router]
  );

  const emptyTemplate = useMemo(
    () => (
      <div className="text-center py-6 text-color-secondary">
        <p className="m-0">No results yet. Choose a date range and click Search.</p>
      </div>
    ),
    []
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Cards by expiry date
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Find cards whose expiry date falls between the selected dates (inclusive).
        </p>
      </div>

      <FormSection title="Date range" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Date from" htmlFor="expiry-from" className="mb-0">
            <Calendar
              id="expiry-from"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.value as Date | null)}
              dateFormat="dd/mm/yy"
              showIcon
              className="w-12rem"
            />
          </FormField>
          <FormField label="Date to" htmlFor="expiry-to" className="mb-0">
            <Calendar
              id="expiry-to"
              value={dateTo}
              onChange={(e) => setDateTo(e.value as Date | null)}
              dateFormat="dd/mm/yy"
              showIcon
              className="w-12rem"
            />
          </FormField>
          <Button
            label="Search"
            icon="pi pi-search"
            onClick={() => searchMutation.mutate()}
            loading={searchMutation.isPending}
          />
        </div>
      </FormSection>

      <DataTable
        value={rows}
        loading={searchMutation.isPending}
        paginator
        rows={10}
        rowsPerPageOptions={[10, 25, 50]}
        emptyMessage={emptyTemplate}
      >
        <Column field="panMasked" header="PAN" />
        <Column field="relationshipNum" header="Relationship" />
        <Column field="cardTitle" header="Title" />
        <Column field="cardTypeName" header="Type" />
        <Column field="cardStatusName" header="Status" />
        <Column
          field="expiryDate"
          header="Expiry"
          body={(r: Card) =>
            r.expiryDate ? new Date(r.expiryDate).toLocaleDateString() : '—'
          }
        />
        <Column field="branchName" header="Branch" />
        <Column header="Actions" body={actionBody} style={{ width: '6rem' }} />
      </DataTable>
    </div>
  );
}
