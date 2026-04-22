'use client';

import React, { useState } from 'react';
import { Button } from 'primereact/button';
import { Calendar } from 'primereact/calendar';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { Message } from 'primereact/message';
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

export default function CardExportPage() {
  const toast = React.useRef<Toast>(null);
  const queryClient = useQueryClient();
  const [cardTypeId, setCardTypeId] = useState<number | null>(null);
  const [pan, setPan] = useState('');
  const [relationshipNum, setRelationshipNum] = useState('');
  const [fromDate, setFromDate] = useState<Date | null>(null);
  const [toDate, setToDate] = useState<Date | null>(null);
  const [rows, setRows] = useState<Card[]>([]);
  const [selectedCards, setSelectedCards] = useState<Card[]>([]);
  const [lastExport, setLastExport] = useState<{ path: string; cardIdForDownload: number } | null>(null);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const searchMutation = useMutation({
    mutationFn: () => {
      if (cardTypeId == null) throw new Error('Card Type is required');
      return CardService.getExportReadyCards({
        cardTypeId,
        pan: pan.trim() || undefined,
        relationshipNum: relationshipNum.trim() || undefined,
        dateFrom: fromDate ? toLocalDate(fromDate) : undefined,
        dateTo: toDate ? toLocalDate(toDate) : undefined,
      });
    },
    onSuccess: (data) => {
      setRows(data);
      setSelectedCards([]);
      toast.current?.show({
        severity: 'success',
        summary: 'Search complete',
        detail: `${data.length} card(s) found`,
        life: 3000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Search failed', detail: e.message, life: 6000 });
    },
  });

  const bulkExportMutation = useMutation({
    mutationFn: (cardIds: number[]) => CardService.bulkExportCards(cardIds),
    onSuccess: (exportPath, cardIds) => {
      const firstId = cardIds[0];
      setLastExport(
        firstId != null ? { path: exportPath, cardIdForDownload: firstId } : { path: exportPath, cardIdForDownload: 0 }
      );
      setSelectedCards([]);
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      searchMutation.mutate();
      toast.current?.show({
        severity: 'success',
        summary: 'Export complete',
        detail: 'File generated. Cards moved to exported status.',
        life: 5000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Export failed', detail: e.message, life: 8000 });
    },
  });

  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({ label: `${t.name ?? t.code} (${t.code})`, value: t.id })) ?? [];

  const onCardTypeChange = (id: number | null) => {
    setCardTypeId(id);
    setSelectedCards([]);
    setLastExport(null);
  };

  const searchCards = () => {
    setLastExport(null);
    searchMutation.mutate();
  };

  const exportSelected = () => {
    if (selectedCards.length === 0) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Selection',
        detail: 'Select at least one card',
        life: 3000,
      });
      return;
    }
    const ids = selectedCards.map((c) => c.cardId).filter((id) => id != null && id > 0);
    if (ids.length === 0) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: 'Invalid card IDs', life: 4000 });
      return;
    }
    bulkExportMutation.mutate(ids);
  };

  const downloadLastFile = async () => {
    if (!lastExport || lastExport.cardIdForDownload < 1) return;
    try {
      await CardService.downloadExportFile(lastExport.cardIdForDownload);
    } catch (e) {
      toast.current?.show({
        severity: 'error',
        summary: 'Download',
        detail: e instanceof Error ? e.message : 'Download failed',
        life: 6000,
      });
    }
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Card export
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Choose a card type to list cards ready for export (status 001). Select one or more rows, then export to generate
          the bureau file. Exported cards move to status 002 and drop off this list.
        </p>
      </div>

      <FormSection title="Search filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Card Type" htmlFor="export-card-type" className="mb-0" required>
            <Dropdown
              inputId="export-card-type"
              placeholder="Select card type (required)"
              value={cardTypeId}
              options={typeOptions}
              onChange={(e) => onCardTypeChange(e.value ?? null)}
              className="w-15rem"
              filter
            />
          </FormField>
          <FormField label="PAN" htmlFor="export-pan" className="mb-0">
            <InputText
              id="export-pan"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              placeholder="PAN or last 4"
              className="w-12rem"
            />
          </FormField>
          <FormField label="Relationship Number" htmlFor="export-rel" className="mb-0">
            <InputText id="export-rel" value={relationshipNum} onChange={(e) => setRelationshipNum(e.target.value)} className="w-12rem" />
          </FormField>
          <FormField label="Date From" htmlFor="export-from" className="mb-0">
            <Calendar id="export-from" value={fromDate} onChange={(e) => setFromDate(e.value as Date | null)} showIcon />
          </FormField>
          <FormField label="Date To" htmlFor="export-to" className="mb-0">
            <Calendar id="export-to" value={toDate} onChange={(e) => setToDate(e.value as Date | null)} showIcon />
          </FormField>
          <Button
            type="button"
            label="Search"
            icon="pi pi-search"
            onClick={searchCards}
            loading={searchMutation.isPending}
            disabled={cardTypeId == null}
          />
        </div>
      </FormSection>

      {searchMutation.isError && (
        <Message
          severity="error"
          text={searchMutation.error instanceof Error ? searchMutation.error.message : 'Failed to load export-ready cards'}
          className="mb-3 w-full"
        />
      )}

      {lastExport && (
        <Message
          severity="success"
          className="mb-3 w-full justify-content-start"
          content={
            <div className="flex flex-column gap-2 w-full">
              <span>
                <strong>Export file:</strong> {lastExport.path}
              </span>
              {lastExport.cardIdForDownload > 0 && (
                <div>
                  <Button
                    type="button"
                    label="Download file"
                    icon="pi pi-download"
                    size="small"
                    onClick={downloadLastFile}
                  />
                </div>
              )}
            </div>
          }
        />
      )}

      <FormSection title="Cards ready for export" className="pb-3">
        <div className="flex flex-wrap align-items-center gap-2 mb-3">
          <Button
            type="button"
            label="Export selected"
            icon="pi pi-file-export"
            onClick={exportSelected}
            loading={bulkExportMutation.isPending}
            disabled={selectedCards.length === 0 || searchMutation.isPending}
          />
          {cardTypeId == null && <span className="text-color-secondary text-sm">Select a card type before searching.</span>}
        </div>

        <DataTable
          value={rows}
          dataKey="cardId"
          loading={searchMutation.isPending}
          emptyMessage={cardTypeId == null ? 'Select a card type above.' : 'No cards in export-ready status for this filter.'}
          selection={selectedCards}
          onSelectionChange={(e) => setSelectedCards(e.value as Card[])}
          metaKeySelection={false}
          size="small"
          stripedRows
        >
          <Column selectionMode="multiple" headerStyle={{ width: '3rem' }} />
          <Column field="panMasked" header="PAN (masked)" />
          <Column field="cardTitle" header="Card Title" />
          <Column field="cardTypeName" header="Card Type" body={(row) => row.cardTypeName ?? row.cardTypeCode ?? '—'} />
          <Column field="branchName" header="Branch" body={(row) => row.branchName ?? row.branchCode ?? '—'} />
          <Column field="issuedDate" header="Issued Date" body={(row) => (row.issuedDate ? new Date(row.issuedDate).toLocaleDateString() : '—')} />
        </DataTable>
      </FormSection>
    </div>
  );
}
