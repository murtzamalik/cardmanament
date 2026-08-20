'use client';

import React, { useCallback, useMemo, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { Card } from '@/types/card';
import { ROUTES, cardDetailHref } from '@/lib/constants';

export default function CardExportPage() {
  const toast = React.useRef<Toast>(null);
  const [cardTypeId, setCardTypeId] = useState<number | null>(null);
  const [rows, setRows] = useState<Card[]>([]);
  const [selectedRows, setSelectedRows] = useState<Card[]>([]);
  const [confirmOpen, setConfirmOpen] = useState(false);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({
      label: `${t.name ?? t.code} (${t.code})`,
      value: t.id,
    })) ?? [];

  const searchMutation = useMutation({
    mutationFn: () => {
      if (cardTypeId == null) {
        return Promise.reject(new Error('Select a card type'));
      }
      return CardService.getExportReadyCards({ cardTypeId });
    },
    onSuccess: (data) => {
      setRows(data);
      setSelectedRows([]);
      toast.current?.show({
        severity: 'success',
        summary: 'Search complete',
        detail: `${data.length} export-ready card(s) found`,
        life: 3000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Search failed', detail: e.message, life: 5000 });
    },
  });

  const exportMutation = useMutation({
    mutationFn: (cardIds: number[]) => CardService.bulkExportCards(cardIds),
    onSuccess: (exportPath) => {
      setConfirmOpen(false);
      setSelectedRows([]);
      toast.current?.show({
        severity: 'success',
        summary: 'Export complete',
        detail: exportPath
          ? `Export file generated: ${exportPath}`
          : 'Cards exported successfully',
        life: 6000,
      });
      if (cardTypeId != null) searchMutation.mutate();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Export failed', detail: e.message, life: 5000 });
    },
  });

  const selectedIds = useMemo(
    () =>
      selectedRows
        .map((r) => r.cardId)
        .filter((id): id is number => typeof id === 'number' && id > 0),
    [selectedRows]
  );

  const openExportConfirm = useCallback(() => {
    if (selectedIds.length === 0) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Selection required',
        detail: 'Select at least one card to export',
        life: 3000,
      });
      return;
    }
    setConfirmOpen(true);
  }, [selectedIds.length]);

  const confirmExport = useCallback(() => {
    if (selectedIds.length === 0) return;
    exportMutation.mutate(selectedIds);
  }, [exportMutation, selectedIds]);

  const emptyTemplate = useMemo(
    () => (
      <div className="text-center py-6 text-color-secondary">
        <p className="m-0">No results yet. Select a card type and click Search.</p>
      </div>
    ),
    []
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Card export
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Find cards ready for bureau export (production status Issued), then export selected cards to a file.
        </p>
      </div>

      <FormSection title="Filters" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Card Type" htmlFor="export-card-type" className="mb-0" required>
            <Dropdown
              inputId="export-card-type"
              value={cardTypeId}
              options={typeOptions}
              onChange={(e) => setCardTypeId(e.value ?? null)}
              placeholder="Select card type"
              filter
              className="w-15rem"
            />
          </FormField>
          <Button
            type="button"
            label="Search"
            icon="pi pi-search"
            onClick={() => searchMutation.mutate()}
            loading={searchMutation.isPending}
            disabled={cardTypeId == null}
          />
          {selectedRows.length > 0 && (
            <Button
              type="button"
              label={`Export selected (${selectedRows.length})`}
              icon="pi pi-download"
              severity="success"
              onClick={openExportConfirm}
              loading={exportMutation.isPending}
              disabled={searchMutation.isPending}
            />
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
        <Column
          field="cardTypeName"
          header="Type"
          body={(r: Card) => r.cardTypeName ?? r.cardTypeCode ?? '—'}
        />
        <Column
          field="cardStatusName"
          header="Status"
          body={(r: Card) => r.cardStatusName ?? r.cardStatusCode ?? '—'}
        />
        <Column
          field="issuedDate"
          header="Issued"
          body={(r: Card) =>
            r.issuedDate ? new Date(r.issuedDate).toLocaleDateString() : '—'
          }
        />
        <Column
          field="expiryDate"
          header="Expiry"
          body={(r: Card) =>
            r.expiryDate ? new Date(r.expiryDate).toLocaleDateString() : '—'
          }
        />
        <Column
          field="branchName"
          header="Branch"
          body={(r: Card) => r.branchName ?? r.branchCode ?? '—'}
        />
        <Column
          header="Actions"
          body={(row: Card) => (
            <Button
              icon="pi pi-eye"
              label="View"
              text
              rounded
              size="small"
              className="p-button-text"
              onClick={() => window.location.assign(cardDetailHref(row.cardId, ROUTES.cardsExport))}
            />
          )}
          style={{ width: '7rem' }}
        />
      </DataTable>

      <ConfirmActionDialog
        visible={confirmOpen}
        onHide={() => setConfirmOpen(false)}
        onAccept={confirmExport}
        loading={exportMutation.isPending}
        message={`Generate a bureau export file for ${selectedIds.length} selected card(s)? Cards will be marked as exported.`}
        acceptLabel="Export"
        variant="danger"
      />
    </div>
  );
}
