'use client';

import React, { useCallback, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
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

function isHotCard(card: Card): boolean {
  const code = String(card.cardStatusCode ?? '').trim().toUpperCase();
  const name = String(card.cardStatusName ?? '').trim().toUpperCase();
  return code === 'HOT' || code === '003' || name === 'HOT';
}

export default function CardsExpirySearchPage() {
  const router = useRouter();
  const toast = React.useRef<Toast>(null);
  const [dateFrom, setDateFrom] = useState<Date | null>(null);
  const [dateTo, setDateTo] = useState<Date | null>(null);
  const [pan, setPan] = useState('');
  const [rows, setRows] = useState<Card[]>([]);
  const [selectedRows, setSelectedRows] = useState<Card[]>([]);

  const searchMutation = useMutation({
    mutationFn: () => {
      const hasPan = !!pan.trim();
      const hasFrom = !!dateFrom;
      const hasTo = !!dateTo;
      if (!hasPan && !hasFrom && !hasTo) {
        return Promise.reject(new Error('Enter PAN or select both date from and date to'));
      }
      if (hasFrom !== hasTo) {
        return Promise.reject(new Error('Please select both date from and date to'));
      }
      if (hasFrom && hasTo && dateFrom.getTime() > dateTo.getTime()) {
        return Promise.reject(new Error('Date from must be on or before date to'));
      }
      return CardService.searchCardsByExpiry({
        dateFrom: hasFrom ? formatLocalDate(dateFrom) : undefined,
        dateTo: hasTo ? formatLocalDate(dateTo) : undefined,
        pan: pan.trim() || undefined,
      });
    },
    onSuccess: (data) => {
      setRows(data);
      setSelectedRows([]);
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

  const renewMutation = useMutation({
    mutationFn: (cardIds: number[]) => CardService.bulkRenewCards(cardIds),
    onSuccess: (renewedIds) => {
      setSelectedRows([]);
      toast.current?.show({
        severity: 'success',
        summary: 'Renew complete',
        detail: `${renewedIds.length} card(s) renewed successfully`,
        life: 4000,
      });
      searchMutation.mutate();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Renew failed', detail: e.message, life: 5000 });
    },
  });

  const replaceMutation = useMutation({
    mutationFn: async (cardIds: number[]) => {
      const settled = await Promise.allSettled(cardIds.map((id) => CardService.createReplacementRequest(id)));
      const succeeded = settled.filter((r) => r.status === 'fulfilled').length;
      const failed = settled.length - succeeded;
      return { succeeded, failed };
    },
    onSuccess: ({ succeeded, failed }) => {
      setSelectedRows([]);
      if (succeeded > 0 && failed === 0) {
        toast.current?.show({
          severity: 'success',
          summary: 'Replacement complete',
          detail: `${succeeded} card(s) sent for replacement`,
          life: 4000,
        });
      } else if (succeeded > 0) {
        toast.current?.show({
          severity: 'warn',
          summary: 'Partial replacement',
          detail: `${succeeded} succeeded, ${failed} failed`,
          life: 5000,
        });
      } else {
        toast.current?.show({
          severity: 'error',
          summary: 'Replacement failed',
          detail: 'No replacement request could be created',
          life: 5000,
        });
      }
      searchMutation.mutate();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Replacement failed', detail: e.message, life: 5000 });
    },
  });

  const actionBody = useCallback(
    (row: Card) => {
      const hot = isHotCard(row);
      return <div className="flex gap-1">
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
        <Tooltip target={`.expiry-renew-${row.cardId}`} content="Renew card (+5 years expiry)" position="top" />
        <Button
          icon="pi pi-refresh"
          text
          rounded
          size="small"
          severity="success"
          className={`p-button-text expiry-renew-${row.cardId}`}
          onClick={() => renewMutation.mutate([row.cardId])}
          loading={renewMutation.isPending}
          disabled={hot || searchMutation.isPending || replaceMutation.isPending}
        />
        <Tooltip target={`.expiry-replace-${row.cardId}`} content="Replace card (new PAN/card data)" position="top" />
        <Button
          icon="pi pi-sync"
          text
          rounded
          size="small"
          severity="warning"
          className={`p-button-text expiry-replace-${row.cardId}`}
          onClick={() => replaceMutation.mutate([row.cardId])}
          loading={replaceMutation.isPending}
          disabled={hot || searchMutation.isPending || renewMutation.isPending}
        />
      </div>;
    },
    [renewMutation, replaceMutation, router, searchMutation.isPending]
  );

  const emptyTemplate = useMemo(
    () => (
      <div className="text-center py-6 text-color-secondary">
        <p className="m-0">No results yet. Choose a date range and click Search.</p>
      </div>
    ),
    []
  );

  const renewSelected = useCallback(() => {
    const eligible = selectedRows.filter((r) => !isHotCard(r));
    const ids = eligible.map((r) => r.cardId).filter((id): id is number => typeof id === 'number' && id > 0);
    if (ids.length === 0) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Selection required',
        detail: 'Select at least one non-HOT card to renew',
        life: 3000,
      });
      return;
    }
    renewMutation.mutate(ids);
  }, [renewMutation, selectedRows]);

  const replaceSelected = useCallback(() => {
    const eligible = selectedRows.filter((r) => !isHotCard(r));
    const ids = eligible.map((r) => r.cardId).filter((id): id is number => typeof id === 'number' && id > 0);
    if (ids.length === 0) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Selection required',
        detail: 'Select at least one non-HOT card to replace',
        life: 3000,
      });
      return;
    }
    replaceMutation.mutate(ids);
  }, [replaceMutation, selectedRows]);

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
          <FormField label="PAN" htmlFor="expiry-pan" className="mb-0">
            <InputText
              id="expiry-pan"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              placeholder="PAN or last 4"
              className="w-12rem"
            />
          </FormField>
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
          {selectedRows.length > 0 && (
            <>
              <Button
                label="Renew selected"
                icon="pi pi-refresh"
                severity="success"
                onClick={renewSelected}
                loading={renewMutation.isPending}
                disabled={searchMutation.isPending || replaceMutation.isPending}
              />
              <Button
                label="Replace selected"
                icon="pi pi-sync"
                severity="warning"
                onClick={replaceSelected}
                loading={replaceMutation.isPending}
                disabled={searchMutation.isPending || renewMutation.isPending}
              />
            </>
          )}
        </div>
      </FormSection>

      <DataTable

        value={rows}
        dataKey="cardId"
        selection={selectedRows}
        selectionMode="multiple"
        onSelectionChange={(e) => setSelectedRows(e.value as Card[])}
        loading={searchMutation.isPending}
        paginator
        rows={10}
        rowsPerPageOptions={[10, 25, 50]}
        emptyMessage={emptyTemplate}
      >
        <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
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
        <Column header="Actions" body={actionBody} style={{ width: '10rem' }} />
      </DataTable>
    </div>
  );
}
