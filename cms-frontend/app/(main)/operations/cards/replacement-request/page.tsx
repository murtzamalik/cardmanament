'use client';

import React, { useState } from 'react';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { Card } from '@/types/card';

function toLocalDate(value: Date): string {
  const y = value.getFullYear();
  const m = String(value.getMonth() + 1).padStart(2, '0');
  const d = String(value.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export default function ReplacementRequestPage() {
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
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [selectedCard, setSelectedCard] = useState<Card | null>(null);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({ label: `${t.name ?? t.code} (${t.code})`, value: t.id })) ?? [];

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

  const replacementMutation = useMutation({
    mutationFn: () => {
      if (!selectedCard?.cardId) throw new Error('No card selected');
      return CardService.createReplacementRequest(selectedCard.cardId);
    },
    onSuccess: (requestId) => {
      setConfirmOpen(false);
      setSelectedCard(null);
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      searchMutation.mutate({ page, size: rows });
      toast.current?.show({
        severity: 'success',
        summary: 'Replacement request created',
        detail: `Request #${requestId}`,
        life: 5000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 6000 });
    },
  });

  const onSearchClick = () => {
    setPage(0);
    searchMutation.mutate({ page: 0, size: rows });
  };

  const openConfirm = (card: Card) => {
    setSelectedCard(card);
    setConfirmOpen(true);
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Replacement card request
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Search cards and request replacement for a selected card.
        </p>
      </div>

      <FormSection title="Search filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="PAN" htmlFor="rep-pan" className="mb-0">
            <InputText
              id="rep-pan"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              placeholder="PAN or last 4"
              className="w-12rem"
            />
          </FormField>
          <FormField label="Relationship Number" htmlFor="rep-rel" className="mb-0">
            <InputText
              id="rep-rel"
              value={relationshipNum}
              onChange={(e) => setRelationshipNum(e.target.value)}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Date From" htmlFor="rep-from" className="mb-0">
            <Calendar id="rep-from" value={fromDate} onChange={(e) => setFromDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Date To" htmlFor="rep-to" className="mb-0">
            <Calendar id="rep-to" value={toDate} onChange={(e) => setToDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Card Type" htmlFor="rep-type" className="mb-0">
            <Dropdown
              inputId="rep-type"
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
          setPage(nextPage);
          setRows(nextRows);
          searchMutation.mutate({ page: nextPage, size: nextRows });
        }}
        emptyMessage="No cards found."
      >
        <Column field="panMasked" header="PAN" />
        <Column field="cardTitle" header="Card Title" />
        <Column field="cardTypeName" header="Card Type" body={(r: Card) => r.cardTypeName ?? r.cardTypeCode ?? '—'} />
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
            <Button type="button" label="Request Replacement" size="small" severity="danger" onClick={() => openConfirm(row)} />
          )}
          style={{ width: '14rem' }}
        />
      </DataTable>

      <ConfirmActionDialog
        visible={confirmOpen}
        onHide={() => setConfirmOpen(false)}
        message="Are you sure? Old card will be set to INACTIVE."
        detail={`Card: ${selectedCard?.panMasked ?? '—'}`}
        variant="danger"
        acceptLabel="Create request"
        loading={replacementMutation.isPending}
        onAccept={() => replacementMutation.mutate()}
      />
    </div>
  );
}
