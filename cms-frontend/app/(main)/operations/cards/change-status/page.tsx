'use client';

import React, { useMemo, useState } from 'react';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dialog } from 'primereact/dialog';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { FormSection, FormField } from '@/components/ui';
import type { Card } from '@/types/card';

function toLocalDate(value: Date): string {
  const y = value.getFullYear();
  const m = String(value.getMonth() + 1).padStart(2, '0');
  const d = String(value.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

function isHotCard(card: Card): boolean {
  const code = String(card.cardStatusCode ?? '').trim().toUpperCase();
  const name = String(card.cardStatusName ?? '').trim().toUpperCase();
  return code === 'HOT' || code === '003' || name === 'HOT';
}

export default function ChangeCardStatusPage() {
  const toast = React.useRef<Toast>(null);
  const queryClient = useQueryClient();
  const [pan, setPan] = useState('');
  const [relationshipNum, setRelationshipNum] = useState('');
  const [fromDate, setFromDate] = useState<Date | null>(null);
  const [toDate, setToDate] = useState<Date | null>(null);
  const [cardTypeId, setCardTypeId] = useState<number | null>(null);
  const [page, setPage] = useState(0);
  const [rows, setRows] = useState(10);
  const [results, setResults] = useState<Card[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedCard, setSelectedCard] = useState<Card | null>(null);
  const [cardStatusId, setCardStatusId] = useState<number | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const searchMutation = useMutation({
    mutationFn: (args: { page: number; size: number }) =>
      CardService.searchCards({
        pan: pan.trim() || undefined,
        relationshipNum: relationshipNum.trim() || undefined,
        cardTypeId,
        dateFrom: fromDate ? toLocalDate(fromDate) : undefined,
        dateTo: toDate ? toLocalDate(toDate) : undefined,
        page: args.page,
        size: args.size,
      }),
    onSuccess: (res) => {
      setResults(res.content ?? []);
      setTotalElements(res.totalElements ?? 0);
      setPage(res.page ?? 0);
      setRows(res.size ?? rows);
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Search failed', detail: e.message, life: 5000 });
    },
  });

  const updateMutation = useMutation({
    mutationFn: () => {
      if (!selectedCard?.cardId) throw new Error('No card selected');
      if (cardStatusId == null) throw new Error('Select a card status');
      return CardService.updateCard(selectedCard.cardId, { cardStatusId });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      setDialogOpen(false);
      setSelectedCard(null);
      setCardStatusId(null);
      searchMutation.mutate({ page, size: rows });
      toast.current?.show({
        severity: 'success',
        summary: 'Success',
        detail: 'Card status updated',
        life: 3000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 6000 });
    },
  });

  const statusOptions =
    dropdowns.cardStatuses
      ?.map((s) => {
        const code = String(
          (s as { code?: string; cardStatusCode?: string }).code
            ?? (s as { code?: string; cardStatusCode?: string }).cardStatusCode
            ?? ''
        );
        const name = String(
          (s as { name?: string; cardStatusName?: string }).name
            ?? (s as { name?: string; cardStatusName?: string }).cardStatusName
            ?? ''
        );
        return { id: s.id, code, name };
      })
      .filter((s) => {
        const n = s.name.toUpperCase();
        const c = s.code.toUpperCase();
        return n === 'COLD' || n === 'WARM' || n === 'HOT' || c === '001' || c === '002' || c === '003';
      })
      .map((s) => ({ label: `${s.name} (${s.code})`, value: s.id })) ?? [];
  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({ label: `${t.name ?? t.code} (${t.code})`, value: t.id })) ?? [];

  const onSearchClick = () => {
    setPage(0);
    searchMutation.mutate({ page: 0, size: rows });
  };

  const openDialog = (card: Card) => {
    setSelectedCard(card);
    const current = dropdowns.cardStatuses?.find((s) => {
      const code = String(
        (s as { code?: string; cardStatusCode?: string }).code
          ?? (s as { code?: string; cardStatusCode?: string }).cardStatusCode
          ?? ''
      );
      return code === (card.cardStatusCode ?? '');
    });
    setCardStatusId(current?.id ?? null);
    setDialogOpen(true);
  };

  const currentStatusLabel = useMemo(
    () => selectedCard?.cardStatusName ?? selectedCard?.cardStatusCode ?? '—',
    [selectedCard]
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Change card status
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Search cards, then change status from the result list.
        </p>
      </div>

      <FormSection title="Search filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="PAN" htmlFor="cs-pan" className="mb-0">
            <InputText
              id="cs-pan"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              placeholder="PAN or last 4"
              className="w-12rem"
            />
          </FormField>
          <FormField label="Relationship Number" htmlFor="cs-rel" className="mb-0">
            <InputText
              id="cs-rel"
              value={relationshipNum}
              onChange={(e) => setRelationshipNum(e.target.value)}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Date From" htmlFor="cs-from" className="mb-0">
            <Calendar id="cs-from" value={fromDate} onChange={(e) => setFromDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Date To" htmlFor="cs-to" className="mb-0">
            <Calendar id="cs-to" value={toDate} onChange={(e) => setToDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Card Type" htmlFor="cs-type-filter" className="mb-0">
            <Dropdown
              inputId="cs-type-filter"
              value={cardTypeId}
              options={typeOptions}
              onChange={(e) => setCardTypeId(e.value ?? null)}
              placeholder="All card types"
              showClear
              filter
              className="w-15rem"
            />
          </FormField>
          <Button type="button" label="Search" icon="pi pi-search" onClick={onSearchClick} loading={searchMutation.isPending} />
        </div>
      </FormSection>

      <DataTable
        value={results}
        dataKey="cardId"
        loading={searchMutation.isPending}
        paginator
        lazy
        first={page * rows}
        rows={rows}
        totalRecords={totalElements}
        onPage={(e) => {
          const nextPage = Math.floor((e.first ?? 0) / (e.rows ?? rows));
          const nextRows = e.rows ?? rows;
          setRows(nextRows);
          setPage(nextPage);
          searchMutation.mutate({ page: nextPage, size: nextRows });
        }}
        emptyMessage="No cards found."
      >
        <Column field="panMasked" header="PAN" />
        <Column field="cardTitle" header="Card Title" />
        <Column field="cardTypeName" header="Card Type" body={(r: Card) => r.cardTypeName ?? r.cardTypeCode ?? '—'} />
        <Column field="cardStatusName" header="Current Status" body={(r: Card) => r.cardStatusName ?? r.cardStatusCode ?? '—'} />
        <Column field="branchName" header="Branch" body={(r: Card) => r.branchName ?? r.branchCode ?? '—'} />
        <Column
          field="expiryDate"
          header="Expiry Date"
          body={(r: Card) => (r.expiryDate ? new Date(r.expiryDate).toLocaleDateString() : '—')}
        />
        <Column
          header="Action"
          body={(row: Card) => (
            <Button
              type="button"
              label="Change Status"
              size="small"
              onClick={() => openDialog(row)}
              disabled={isHotCard(row)}
            />
          )}
          style={{ width: '11rem' }}
        />
      </DataTable>

      <Dialog
        visible={dialogOpen}
        onHide={() => setDialogOpen(false)}
        header="Change Card Status"
        style={{ width: '30rem' }}
        modal
      >
        <p className="mb-3">
          <strong>Card:</strong> {selectedCard?.panMasked ?? '—'}<br />
          <strong>Current Status:</strong> {currentStatusLabel}
        </p>
        <FormField label="New Status" htmlFor="cs-dialog-status" className="mb-0">
          <Dropdown
            inputId="cs-dialog-status"
            placeholder="Select status"
            value={cardStatusId}
            options={statusOptions}
            onChange={(e) => setCardStatusId(e.value ?? null)}
            className="w-full"
            filter
          />
        </FormField>
        <div className="flex justify-content-end gap-2 mt-3">
          <Button type="button" label="Cancel" text onClick={() => setDialogOpen(false)} disabled={updateMutation.isPending} />
          <Button
            type="button"
            label="Confirm"
            icon="pi pi-check"
            onClick={() => updateMutation.mutate()}
            loading={updateMutation.isPending}
            disabled={cardStatusId == null}
          />
        </div>
      </Dialog>
    </div>
  );
}
