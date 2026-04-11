'use client';

import React, { useRef, useState } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { AppDialog, ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import { CardVisual } from '@/components/cards/CardVisual';
import type { Card, CardAccountLink } from '@/types/card';
import { CARD_RETURN_FROM_PARAM, safeReturnPath } from '@/lib/constants';

export default function CardDetailPage() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const backPath = safeReturnPath(searchParams.get(CARD_RETURN_FROM_PARAM));
  const toast = useRef<Toast>(null);
  const queryClient = useQueryClient();
  const cardId = params?.id != null ? Number(params.id) : NaN;

  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [linkAccountDialogOpen, setLinkAccountDialogOpen] = useState(false);
  const [closeDialogOpen, setCloseDialogOpen] = useState(false);
  const [delinkDialogOpen, setDelinkDialogOpen] = useState(false);
  const [accountToDelink, setAccountToDelink] = useState<CardAccountLink | null>(null);
  const [editForm, setEditForm] = useState<{ cardStatusCode?: string; cardTitle?: string; limitProfile?: string | null }>({});
  const [linkForm, setLinkForm] = useState<{ accountNum: string; relationshipNum?: string }>({ accountNum: '' });

  const { data: card, isLoading: cardLoading } = useQuery({
    queryKey: ['card', cardId],
    queryFn: () => CardService.getCardById(cardId),
    enabled: Number.isFinite(cardId),
  });

  const { data: linkedAccounts = [], refetch: refetchLinked } = useQuery({
    queryKey: ['card-linked-accounts', cardId],
    queryFn: () => CardService.getLinkedAccountsByCardId(cardId),
    enabled: Number.isFinite(cardId),
  });

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const updateCardMutation = useMutation({
    mutationFn: (body: { cardStatusCode?: string; cardTitle?: string; limitProfile?: string | null }) =>
      CardService.updateCard(cardId, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['card', cardId] });
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Card updated', life: 3000 });
      setEditDialogOpen(false);
    },
    onError: (e: Error) => toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 }),
  });

  const linkAccountMutation = useMutation({
    mutationFn: (body: { accountNum: string; relationshipNum?: string }) =>
      CardService.linkCardAccountByCardId(cardId, body),
    onSuccess: () => {
      refetchLinked();
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Account linked', life: 3000 });
      setLinkAccountDialogOpen(false);
      setLinkForm({ accountNum: '' });
    },
    onError: (e: Error) => toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 }),
  });

  const delinkMutation = useMutation({
    mutationFn: (accountNum: string) => CardService.delinkCardAccountByCardId(cardId, accountNum),
    onSuccess: () => {
      refetchLinked();
      setDelinkDialogOpen(false);
      setAccountToDelink(null);
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Account delinked', life: 3000 });
    },
    onError: (e: Error) => toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 }),
  });

  const closeCardMutation = useMutation({
    mutationFn: () => CardService.closeCard(cardId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['card', cardId] });
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Card closed', life: 3000 });
      setCloseDialogOpen(false);
      router.push(backPath);
    },
    onError: (e: Error) => toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 }),
  });

  const downloadExportMutation = useMutation({
    mutationFn: () => CardService.downloadExportFile(cardId),
    onSuccess: () => {
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Export file downloaded', life: 3000 });
    },
    onError: (e: Error) => toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 }),
  });

  const statusOptions = dropdowns.cardStatuses?.map((s) => ({ label: s.name, value: s.code })) ?? [];
  const limitProfileOptions = [{ label: '(None)', value: null }, ...(dropdowns.limitProfiles?.map((p) => ({ label: p.name || p.code, value: p.code })) ?? [])];
  const { data: accountOptions = [] } = useQuery({
    queryKey: ['available-accounts', linkAccountDialogOpen ? card?.relationshipNum : null],
    queryFn: () => CardService.getAvailableAccounts(card?.relationshipNum),
    enabled: linkAccountDialogOpen && !!card,
  });

  const openEdit = () => {
    setEditForm({
      cardStatusCode: card?.cardStatusCode ?? undefined,
      cardTitle: card?.cardTitle ?? undefined,
      limitProfile: card?.limitProfile ?? null,
    });
    setEditDialogOpen(true);
  };

  const saveEdit = () =>
    updateCardMutation.mutate({
      ...editForm,
      limitProfile: editForm.limitProfile === null ? '' : editForm.limitProfile,
    });

  const openLinkAccount = () => {
    setLinkForm({ accountNum: '', relationshipNum: card?.relationshipNum });
    setLinkAccountDialogOpen(true);
  };

  const saveLinkAccount = () => {
    if (!linkForm.accountNum?.trim()) return;
    linkAccountMutation.mutate({ accountNum: linkForm.accountNum.trim(), relationshipNum: linkForm.relationshipNum });
  };

  const openDelinkConfirm = (row: CardAccountLink) => {
    setAccountToDelink(row);
    setDelinkDialogOpen(true);
  };

  const confirmDelink = () => {
    if (accountToDelink) delinkMutation.mutate(accountToDelink.accountNum);
  };

  const confirmClose = () => closeCardMutation.mutate();

  if (!Number.isFinite(cardId) || cardLoading) {
    return (
      <div className="card">
        <p className="text-color-secondary">{!Number.isFinite(cardId) ? 'Invalid card ID' : 'Loading...'}</p>
      </div>
    );
  }

  if (!card) {
    return (
      <div className="card">
        <Toast ref={toast} />
        <p className="text-color-secondary">Card not found.</p>
        <Button label="Back" icon="pi pi-arrow-left" text onClick={() => router.push(backPath)} />
      </div>
    );
  }

  const isClosed = card.cardStatusCode === 'CLOSED';

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="flex justify-content-between align-items-start flex-wrap gap-2 mb-4">
        <div>
          <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Card details</h1>
          <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
            {card.panMasked} · {card.cardTitle || '—'}
          </p>
        </div>
        <div className="flex gap-2">
          <Button label="Back" icon="pi pi-arrow-left" text onClick={() => router.push(backPath)} />
          {!isClosed && (
            <>
              <Button label="Edit" icon="pi pi-pencil" onClick={openEdit} />
              <Button label="Link account" icon="pi pi-link" onClick={openLinkAccount} />
              <Button label="Close card" icon="pi pi-times" severity="danger" onClick={() => setCloseDialogOpen(true)} />
            </>
          )}
        </div>
      </div>

      <div className="mb-4">
        <CardVisual
          cardTitle={card.cardTitle}
          panMasked={card.panMasked}
          expiryDate={card.expiryDate}
          productName={card.productName}
          cardTypeName={card.cardTypeName}
        />
      </div>

      <FormSection title="Card information" className="pt-0">
        <div className="grid">
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">PAN</p>
            <p className="mt-1 mb-0">{card.panMasked ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Relationship</p>
            <p className="mt-1 mb-0">{card.relationshipNum ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Title</p>
            <p className="mt-1 mb-0">{card.cardTitle ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Status</p>
            <p className="mt-1 mb-0">{card.cardStatusName ?? card.cardStatusCode ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Type</p>
            <p className="mt-1 mb-0">{card.cardTypeName ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Product</p>
            <p className="mt-1 mb-0">{card.productName ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Branch</p>
            <p className="mt-1 mb-0">{card.branchName ?? card.branchCode ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Limit profile</p>
            <p className="mt-1 mb-0">{card.limitProfile ?? '—'}</p>
          </div>
          <div className="col-12 md:col-6">
            <p className="font-medium text-color-secondary mb-0">Created</p>
            <p className="mt-1 mb-0">{card.createdOn ? new Date(card.createdOn).toLocaleString() : '—'}</p>
          </div>
          {card.exportFilePath && (
            <div className="col-12">
              <p className="font-medium text-color-secondary mb-1">Export file</p>
              <Button
                label="Download export file"
                icon="pi pi-download"
                onClick={() => downloadExportMutation.mutate()}
                loading={downloadExportMutation.isPending}
                className="p-button-outlined"
              />
            </div>
          )}
        </div>
      </FormSection>

      <FormSection title="Linked accounts">
        <DataTable value={linkedAccounts} emptyMessage="No linked accounts.">
          <Column field="accountNum" header="Account" />
          <Column field="accountTitle" header="Title" />
          <Column header="Effective from" body={(r: CardAccountLink) => (r.effectiveFrom ? new Date(r.effectiveFrom).toLocaleDateString() : '—')} />
          <Column header="Effective to" body={(r: CardAccountLink) => (r.effectiveTo ? new Date(r.effectiveTo).toLocaleDateString() : '—')} />
          {!isClosed && (
            <Column
              header="Actions"
              body={(r: CardAccountLink) => (
                <Button
                  icon="pi pi-unlink"
                  label="Delink"
                  text
                  size="small"
                  severity="danger"
                  onClick={() => openDelinkConfirm(r)}
                />
              )}
            />
          )}
        </DataTable>
        {!isClosed && (
          <Button label="Link account" icon="pi pi-plus" className="mt-2" onClick={openLinkAccount} />
        )}
      </FormSection>

      <AppDialog
        visible={editDialogOpen}
        onHide={() => setEditDialogOpen(false)}
        title="Edit card"
        subtitle="Update status, title, or limit profile."
        width="28rem"
        primaryLabel="Save"
        secondaryLabel="Cancel"
        onPrimary={saveEdit}
        onSecondary={() => setEditDialogOpen(false)}
        loading={updateCardMutation.isPending}
      >
        <FormSection title="Details" className="pt-0">
          <div className="grid p-fluid">
            <div className="col-12">
              <FormField label="Status" htmlFor="edit-status">
                <Dropdown
                  inputId="edit-status"
                  value={editForm.cardStatusCode}
                  options={statusOptions}
                  onChange={(e) => setEditForm((f) => ({ ...f, cardStatusCode: e.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12">
              <FormField label="Title" htmlFor="edit-title">
                <InputText
                  id="edit-title"
                  value={editForm.cardTitle ?? ''}
                  onChange={(e) => setEditForm((f) => ({ ...f, cardTitle: e.target.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12">
              <FormField label="Limit profile" htmlFor="edit-lp">
                <Dropdown
                  inputId="edit-lp"
                  value={editForm.limitProfile}
                  options={limitProfileOptions}
                  onChange={(e) => setEditForm((f) => ({ ...f, limitProfile: e.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
          </div>
        </FormSection>
      </AppDialog>

      <AppDialog
        visible={linkAccountDialogOpen}
        onHide={() => { setLinkAccountDialogOpen(false); setLinkForm({ accountNum: '' }); }}
        title="Link account"
        subtitle="Select an account to link to this card."
        width="28rem"
        primaryLabel="Link"
        secondaryLabel="Cancel"
        onPrimary={saveLinkAccount}
        onSecondary={() => setLinkAccountDialogOpen(false)}
        loading={linkAccountMutation.isPending}
      >
        <FormSection title="Account" className="pt-0">
          <FormField label="Relationship (optional)" htmlFor="link-rel">
            <InputText
              id="link-rel"
              value={linkForm.relationshipNum ?? ''}
              onChange={(e) => setLinkForm((f) => ({ ...f, relationshipNum: e.target.value || undefined }))}
              placeholder={card.relationshipNum}
              className="w-full"
            />
          </FormField>
          <FormField label="Account" htmlFor="link-account" required>
            <Dropdown
              inputId="link-account"
              placeholder="Select account"
              value={linkForm.accountNum || null}
              options={accountOptions.map((a) => ({ label: `${a.accountNum}${a.accountTitle ? ` - ${a.accountTitle}` : ''}`, value: a.accountNum }))}
              onChange={(e) => setLinkForm((f) => ({ ...f, accountNum: e.value ?? '' }))}
              className="w-full"
              filter
            />
          </FormField>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={closeDialogOpen}
        onHide={() => setCloseDialogOpen(false)}
        message="Close this card? The card will be marked as closed."
        detail="This action cannot be undone."
        variant="delete"
        acceptLabel="Close card"
        onAccept={confirmClose}
        loading={closeCardMutation.isPending}
      />

      <ConfirmActionDialog
        visible={delinkDialogOpen}
        onHide={() => { setDelinkDialogOpen(false); setAccountToDelink(null); }}
        message={accountToDelink ? `Delink account ${accountToDelink.accountNum} from this card?` : ''}
        detail="The account will no longer be linked to this card."
        variant="reject"
        acceptLabel="Delink"
        onAccept={confirmDelink}
        loading={delinkMutation.isPending}
      />
    </div>
  );
}
