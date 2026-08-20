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

export default function ChangeCardTypePage() {
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
  const [newCardTypeId, setNewCardTypeId] = useState<number | null>(null);
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
      setPage(res.page ?? 0);
      setRows(res.size ?? rows);
      setTotalElements(res.totalElements ?? 0);
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Search failed', detail: e.message, life: 5000 });
    },
  });

  const changeMutation = useMutation({
    mutationFn: () => {
      if (!selectedCard?.cardId) throw new Error('No card selected');
      if (newCardTypeId == null) throw new Error('Select new card type');
      return CardService.changeCardType(selectedCard.cardId, { cardTypeId: newCardTypeId });
    },
    onSuccess: (requestId) => {
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      setDialogOpen(false);
      setSelectedCard(null);
      setNewCardTypeId(null);
      searchMutation.mutate({ page, size: rows });
      toast.current?.show({
        severity: 'success',
        summary: 'Request created',
        detail: `Card type change request #${requestId} created`,
        life: 4000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 6000 });
    },
  });

  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({ label: `${t.name ?? t.code} (${t.code})`, value: t.id })) ?? [];

  const openDialog = (card: Card) => {
    setSelectedCard(card);
    setNewCardTypeId(null);
    setDialogOpen(true);
  };

  const onSearchClick = () => {
    setPage(0);
    searchMutation.mutate({ page: 0, size: rows });
  };

  const currentTypeLabel = useMemo(
    () => selectedCard?.cardTypeName ?? selectedCard?.cardTypeCode ?? '—',
    [selectedCard]
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Change card type
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Search cards, then change card type from the result list.
        </p>
      </div>

      <FormSection title="Search filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="PAN" htmlFor="ct-pan" className="mb-0">
            <InputText
              id="ct-pan"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              placeholder="PAN or last 4"
              className="w-12rem"
            />
          </FormField>
          <FormField label="Relationship Number" htmlFor="ct-rel" className="mb-0">
            <InputText
              id="ct-rel"
              value={relationshipNum}
              onChange={(e) => setRelationshipNum(e.target.value)}
              placeholder="Relationship Number"
              className="w-12rem"
            />
          </FormField>
          <FormField label="Date From" htmlFor="ct-from" className="mb-0">
            <Calendar id="ct-from" value={fromDate} onChange={(e) => setFromDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Date To" htmlFor="ct-to" className="mb-0">
            <Calendar id="ct-to" value={toDate} onChange={(e) => setToDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Card Type" htmlFor="ct-filter-type" className="mb-0">
            <Dropdown
              inputId="ct-filter-type"
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
        <Column field="cardTypeName" header="Current Card Type" body={(r: Card) => r.cardTypeName ?? r.cardTypeCode ?? '—'} />
        <Column field="branchName" header="Branch" body={(r: Card) => r.branchName ?? r.branchCode ?? '—'} />
        <Column field="cardStatusName" header="Status" body={(r: Card) => r.cardStatusName ?? r.cardStatusCode ?? '—'} />
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
              label="Change Type"
              size="small"
              onClick={() => openDialog(row)}
              disabled={isHotCard(row)}
            />
          )}
          style={{ width: '10rem' }}
        />
      </DataTable>

      <Dialog
        visible={dialogOpen}
        onHide={() => setDialogOpen(false)}
        header="Change Card Type"
        style={{ width: '30rem' }}
        modal
      >
        <p className="mb-3">
          <strong>Card:</strong> {selectedCard?.panMasked ?? '—'}<br />
          <strong>Current Type:</strong> {currentTypeLabel}
        </p>
        <FormField label="New Card Type" htmlFor="ct-dialog-type">
          <Dropdown
            inputId="ct-dialog-type"
            value={newCardTypeId}
            options={typeOptions}
            onChange={(e) => setNewCardTypeId(e.value ?? null)}
            placeholder="Select new card type"
            className="w-full"
            filter
          />
        </FormField>
        <div className="flex justify-content-end gap-2 mt-3">
          <Button type="button" label="Cancel" text onClick={() => setDialogOpen(false)} disabled={changeMutation.isPending} />
          <Button
            type="button"
            label="Confirm"
            icon="pi pi-check"
            onClick={() => changeMutation.mutate()}
            loading={changeMutation.isPending}
            disabled={newCardTypeId == null}
          />
        </div>
      </Dialog>
    </div>
  );
}
