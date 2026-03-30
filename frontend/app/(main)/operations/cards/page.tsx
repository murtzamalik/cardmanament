'use client';

import React, { useCallback, useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable, type DataTableStateEvent } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { AppDialog, FormSection, FormField } from '@/components/ui';
import type { Card, CardSearchRequest } from '@/types/card';

export default function CardsPage() {
  const router = useRouter();
  const toast = React.useRef<Toast>(null);
  const queryClient = useQueryClient();
  const [search, setSearch] = useState<CardSearchRequest>({ page: 0, size: 10, sort: 'createdOn', sortDir: 'desc' });
  const [limitProfileDialogOpen, setLimitProfileDialogOpen] = useState(false);
  const [cardForLimitProfile, setCardForLimitProfile] = useState<Card | null>(null);
  const [selectedLimitProfileId, setSelectedLimitProfileId] = useState<number | null>(null);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const { data: pageData, isLoading, refetch } = useQuery({
    queryKey: ['cards-search', search],
    queryFn: () => CardService.searchCards(search),
  });

  const updateCardMutation = useMutation({
    mutationFn: ({ id, body }: { id: number; body: { limitProfileId?: number | null } }) => CardService.updateCard(id, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Limit profile updated', life: 3000 });
      setLimitProfileDialogOpen(false);
      setCardForLimitProfile(null);
      setSelectedLimitProfileId(null);
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const content = pageData?.content ?? [];
  const totalRecords = pageData?.totalElements ?? 0;

  const onPage = useCallback((e: DataTableStateEvent) => {
    const page = e.page ?? (e.rows ? Math.floor(e.first / e.rows) : 0);
    const rows = e.rows ?? 10;
    setSearch((s) => ({ ...s, page, size: rows }));
  }, []);

  const statusOptions = dropdowns.cardStatuses?.map((s) => ({ label: s.name, value: s.id })) ?? [];
  const branchOptions = dropdowns.branches?.map((b) => ({ label: b.branchName, value: b.id })) ?? [];
  const limitProfileOptions = dropdowns.limitProfiles?.map((p) => ({ label: p.name || p.code, value: p.id })) ?? [];
  const limitProfileOptionsWithClear = [{ label: '(None)', value: null }, ...limitProfileOptions];

  const openSetLimitProfile = (card: Card) => {
    setCardForLimitProfile(card);
    const profileId = card.limitProfile != null ? dropdowns.limitProfiles?.find((p) => p.code === card.limitProfile)?.id ?? null : null;
    setSelectedLimitProfileId(profileId);
    setLimitProfileDialogOpen(true);
  };

  const saveLimitProfile = () => {
    if (!cardForLimitProfile) return;
    updateCardMutation.mutate({
      id: cardForLimitProfile.cardId,
      body: { limitProfileId: selectedLimitProfileId },
    });
  };

  const actionBody = (row: Card) => (
    <div className="flex gap-1">
      <Tooltip target={`.card-view-${row.cardId}`} content="View details" position="top" />
      <Button
        icon="pi pi-eye"
        label="View"
        text
        rounded
        size="small"
        className={`p-button-text card-view-${row.cardId}`}
        onClick={() => router.push(`/operations/cards/${row.cardId}`)}
      />
      <Tooltip target={`.card-lp-${row.cardId}`} content="Set limit profile" position="top" />
      <Button
        icon="pi pi-wallet"
        text
        rounded
        size="small"
        className={`p-button-text card-lp-${row.cardId}`}
        onClick={() => openSetLimitProfile(row)}
      />
    </div>
  );

  const emptyTemplate = () => (
    <div className="text-center py-6 text-color-secondary">
      <p className="m-0">No cards found. Try adjusting your filters.</p>
    </div>
  );

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Cards</h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Search and view issued cards.
        </p>
      </div>
      <FormSection title="Search" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Relationship" htmlFor="cards-rel" className="mb-0">
            <InputText
              id="cards-rel"
              placeholder="Relationship #"
              value={search.relationshipNum ?? ''}
              onChange={(e) => setSearch((s) => ({ ...s, relationshipNum: e.target.value || undefined }))}
              className="w-12rem"
            />
          </FormField>
          <FormField label="PAN (partial)" htmlFor="cards-pan" className="mb-0">
            <InputText
              id="cards-pan"
              placeholder="PAN (partial)"
              value={search.pan ?? ''}
              onChange={(e) => setSearch((s) => ({ ...s, pan: e.target.value || undefined }))}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Status" htmlFor="cards-status" className="mb-0">
            <Dropdown
              inputId="cards-status"
              placeholder="Status"
              value={search.cardStatusId ?? (search.cardStatusCode != null ? dropdowns.cardStatuses?.find((s) => s.code === search.cardStatusCode)?.id ?? null : null)}
              options={statusOptions}
              onChange={(e) => setSearch((s) => ({ ...s, cardStatusId: e.value ?? undefined, cardStatusCode: undefined }))}
              className="w-12rem"
            />
          </FormField>
          <FormField label="Branch" htmlFor="cards-branch" className="mb-0">
            <Dropdown
              inputId="cards-branch"
              placeholder="Branch"
              value={search.branchId ?? (search.branchCode != null ? dropdowns.branches?.find((b) => b.branchCode === search.branchCode)?.id ?? null : null)}
              options={branchOptions}
              onChange={(e) => setSearch((s) => ({ ...s, branchId: e.value ?? undefined, branchCode: undefined }))}
              className="w-12rem"
            />
          </FormField>
          <Button label="Search" icon="pi pi-search" onClick={() => refetch()} />
        </div>
      </FormSection>
      <DataTable
        value={content}
        loading={isLoading}
        paginator
        rows={search.size ?? 10}
        totalRecords={totalRecords}
        lazy
        first={(search.page ?? 0) * (search.size ?? 10)}
        onPage={onPage}
        rowsPerPageOptions={[10, 20, 50]}
        emptyMessage={emptyTemplate()}
      >
        <Column field="panMasked" header="PAN" />
        <Column field="relationshipNum" header="Relationship" />
        <Column field="cardTitle" header="Title" />
        <Column field="cardTypeName" header="Type" />
        <Column field="cardStatusName" header="Status" />
        <Column field="productName" header="Product" />
        <Column field="branchName" header="Branch" />
        <Column field="limitProfile" header="Limit profile" body={(r: Card) => r.limitProfile || '—'} />
        <Column field="createdOn" header="Created" body={(r: Card) => (r.createdOn ? new Date(r.createdOn).toLocaleDateString() : '—')} />
        <Column header="Actions" body={actionBody} style={{ width: '6rem' }} />
      </DataTable>

      <AppDialog
        visible={limitProfileDialogOpen}
        onHide={() => { setLimitProfileDialogOpen(false); setCardForLimitProfile(null); setSelectedLimitProfileId(null); }}
        title="Set limit profile"
        subtitle={cardForLimitProfile ? `Card: ${cardForLimitProfile.panMasked ?? cardForLimitProfile.cardId}` : ''}
        width="28rem"
        primaryLabel="Save"
        secondaryLabel="Cancel"
        onPrimary={saveLimitProfile}
        onSecondary={() => setLimitProfileDialogOpen(false)}
        loading={updateCardMutation.isPending}
      >
        <FormSection title="Profile" className="pt-0">
          <FormField label="Limit profile" htmlFor="cards-lp-select">
            <Dropdown
              inputId="cards-lp-select"
              placeholder="Select profile"
              value={selectedLimitProfileId}
              options={limitProfileOptionsWithClear}
              onChange={(e) => setSelectedLimitProfileId(e.value ?? null)}
              className="w-full"
            />
          </FormField>
        </FormSection>
      </AppDialog>
    </div>
  );
}
