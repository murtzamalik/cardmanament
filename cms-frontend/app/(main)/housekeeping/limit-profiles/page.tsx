'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Checkbox } from 'primereact/checkbox';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as LimitProfileService from '@/services/limitProfiles';
import { AppDialog, ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { LimitProfile, LimitProfileCreateRequest, LimitProfileUpdateRequest } from '@/types/card';

function formatAmount(val: number | null | undefined): string {
  if (val == null) return '—';
  return Number(val).toLocaleString(undefined, { maximumFractionDigits: 2 });
}

const emptyForm: LimitProfileCreateRequest & { active?: boolean } = {
  profileCode: '',
  profileName: '',
  currencyCode: '',
  atmDailyAmount: undefined,
  atmMonthlyAmount: undefined,
  atmYearlyAmount: undefined,
  posDailyAmount: undefined,
  posMonthlyAmount: undefined,
  posYearlyAmount: undefined,
  ecommerceDailyAmount: undefined,
  ecommerceMonthlyAmount: undefined,
  ecommerceYearlyAmount: undefined,
  active: true,
};

export default function LimitProfilesPage() {
  const toast = useRef<Toast>(null);
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<LimitProfile | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [rowToDelete, setRowToDelete] = useState<LimitProfile | null>(null);
  const [form, setForm] = useState<LimitProfileCreateRequest & { active?: boolean }>(emptyForm);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: list = [], isLoading } = useQuery({
    queryKey: ['limit-profiles'],
    queryFn: LimitProfileService.getLimitProfiles,
  });

  const createMutation = useMutation({
    mutationFn: LimitProfileService.createLimitProfile,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['limit-profiles'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Limit profile created', life: 3000 });
      hideDialog();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, req }: { id: number; req: LimitProfileUpdateRequest }) =>
      LimitProfileService.updateLimitProfile(id, req),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['limit-profiles'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Limit profile updated', life: 3000 });
      hideDialog();
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: LimitProfileService.deleteLimitProfile,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['limit-profiles'] });
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Limit profile deleted', life: 3000 });
      setDeleteDialogOpen(false);
      setRowToDelete(null);
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const openCreate = () => {
    setEditing(null);
    setFieldErrors({});
    setForm(emptyForm);
    setDialogOpen(true);
  };

  const openEdit = (row: LimitProfile) => {
    setEditing(row);
    setFieldErrors({});
    setForm({
      profileCode: row.profileCode,
      profileName: row.profileName ?? '',
      currencyCode: row.currencyCode ?? '',
      atmDailyAmount: row.atmDailyAmount,
      atmMonthlyAmount: row.atmMonthlyAmount,
      atmYearlyAmount: row.atmYearlyAmount,
      posDailyAmount: row.posDailyAmount,
      posMonthlyAmount: row.posMonthlyAmount,
      posYearlyAmount: row.posYearlyAmount,
      ecommerceDailyAmount: row.ecommerceDailyAmount,
      ecommerceMonthlyAmount: row.ecommerceMonthlyAmount,
      ecommerceYearlyAmount: row.ecommerceYearlyAmount,
      active: row.active ?? true,
    });
    setDialogOpen(true);
  };

  const hideDialog = () => {
    setDialogOpen(false);
    setEditing(null);
    setFieldErrors({});
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};
    if (!form.profileCode?.trim()) errors.profileCode = 'Profile code is required.';
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const save = async () => {
    if (!validate()) return;
    if (editing && editing.id != null) {
      updateMutation.mutate({
        id: editing.id,
        req: {
          profileName: form.profileName || undefined,
          currencyCode: form.currencyCode || undefined,
          atmDailyAmount: form.atmDailyAmount,
          atmMonthlyAmount: form.atmMonthlyAmount,
          atmYearlyAmount: form.atmYearlyAmount,
          posDailyAmount: form.posDailyAmount,
          posMonthlyAmount: form.posMonthlyAmount,
          posYearlyAmount: form.posYearlyAmount,
          ecommerceDailyAmount: form.ecommerceDailyAmount,
          ecommerceMonthlyAmount: form.ecommerceMonthlyAmount,
          ecommerceYearlyAmount: form.ecommerceYearlyAmount,
          active: form.active,
        },
      });
    } else {
      createMutation.mutate({
        profileCode: form.profileCode.trim(),
        profileName: form.profileName || undefined,
        currencyCode: form.currencyCode || undefined,
        atmDailyAmount: form.atmDailyAmount,
        atmMonthlyAmount: form.atmMonthlyAmount,
        atmYearlyAmount: form.atmYearlyAmount,
        posDailyAmount: form.posDailyAmount,
        posMonthlyAmount: form.posMonthlyAmount,
        posYearlyAmount: form.posYearlyAmount,
        ecommerceDailyAmount: form.ecommerceDailyAmount,
        ecommerceMonthlyAmount: form.ecommerceMonthlyAmount,
        ecommerceYearlyAmount: form.ecommerceYearlyAmount,
        active: form.active,
      });
    }
  };

  const openDeleteConfirm = (row: LimitProfile) => {
    setRowToDelete(row);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = () => {
    if (rowToDelete?.id != null) deleteMutation.mutate(rowToDelete.id);
  };

  const actionBody = (row: LimitProfile) => (
    <>
      <Tooltip target={`.lp-edit-${row.id ?? row.profileCode}`} content="Edit" position="top" />
      <Button
        icon="pi pi-pencil"
        text
        rounded
        className={`p-button-text lp-edit-${row.id ?? row.profileCode}`}
        onClick={() => openEdit(row)}
      />
      <Tooltip target={`.lp-del-${row.profileCode}`} content="Delete" position="top" />
      <Button
        icon="pi pi-trash"
        text
        rounded
        className={`p-button-text p-button-danger lp-del-${row.profileCode}`}
        onClick={() => openDeleteConfirm(row)}
      />
    </>
  );

  const saveLoading = createMutation.isPending || updateMutation.isPending;

  return (
    <div className="grid">
      <div className="col-12">
        <div className="card">
          <Toast ref={toast} />
          <div className="mb-3">
            <h2 className="m-0 font-semibold" style={{ fontSize: '1.25rem' }}>Limit profiles</h2>
            <p className="text-color-secondary mt-1 mb-0" style={{ fontSize: '0.875rem' }}>
              Define per-channel (ATM, POS, E-commerce) daily, monthly, and yearly amount limits. Assign profiles to cards in Operations.
            </p>
          </div>
          <div className="flex justify-content-between align-items-center mb-3">
            <Button label="Add limit profile" icon="pi pi-plus" onClick={openCreate} />
          </div>
          <DataTable
            value={list}
            loading={isLoading}
            paginator
            rows={10}
            rowsPerPageOptions={[5, 10, 25]}
            tableStyle={{ minWidth: '60rem' }}
            emptyMessage="No limit profiles. Add one to get started."
          >
            <Column field="profileCode" header="Profile Code" sortable />
            <Column field="profileName" header="Name" sortable />
            <Column field="currencyCode" header="Currency" />
            <Column header="ATM Daily" body={(r: LimitProfile) => formatAmount(r.atmDailyAmount)} />
            <Column header="ATM Monthly" body={(r: LimitProfile) => formatAmount(r.atmMonthlyAmount)} />
            <Column header="ATM Yearly" body={(r: LimitProfile) => formatAmount(r.atmYearlyAmount)} />
            <Column header="POS Daily" body={(r: LimitProfile) => formatAmount(r.posDailyAmount)} />
            <Column header="POS Monthly" body={(r: LimitProfile) => formatAmount(r.posMonthlyAmount)} />
            <Column header="POS Yearly" body={(r: LimitProfile) => formatAmount(r.posYearlyAmount)} />
            <Column header="E-com Daily" body={(r: LimitProfile) => formatAmount(r.ecommerceDailyAmount)} />
            <Column header="E-com Monthly" body={(r: LimitProfile) => formatAmount(r.ecommerceMonthlyAmount)} />
            <Column header="E-com Yearly" body={(r: LimitProfile) => formatAmount(r.ecommerceYearlyAmount)} />
            <Column field="active" header="Active" body={(r: LimitProfile) => (r.active ? 'Yes' : 'No')} />
            <Column header="Actions" body={actionBody} style={{ width: '8rem' }} />
          </DataTable>
        </div>
      </div>

      <AppDialog
        visible={dialogOpen}
        onHide={hideDialog}
        title={editing ? 'Edit limit profile' : 'Add limit profile'}
        subtitle={editing ? 'Update the details below.' : 'Enter the details below.'}
        width="42rem"
        primaryLabel="Save"
        secondaryLabel="Cancel"
        onPrimary={save}
        onSecondary={hideDialog}
        loading={saveLoading}
      >
        <FormSection title="Details" className="pt-0">
          <div className="grid p-fluid">
            <div className="col-12 md:col-6">
              <FormField label="Profile code" htmlFor="lp-code" required error={fieldErrors.profileCode}>
                <InputText
                  id="lp-code"
                  value={form.profileCode}
                  onChange={(e) => {
                    setForm((f) => ({ ...f, profileCode: e.target.value }));
                    if (fieldErrors.profileCode) setFieldErrors((p) => { const next = { ...p }; delete next.profileCode; return next; });
                  }}
                  disabled={!!editing}
                  className={`w-full ${fieldErrors.profileCode ? 'p-invalid' : ''}`}
                />
              </FormField>
            </div>
            <div className="col-12 md:col-6">
              <FormField label="Profile name" htmlFor="lp-name">
                <InputText
                  id="lp-name"
                  value={form.profileName ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, profileName: e.target.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Currency" htmlFor="lp-currency">
                <InputText
                  id="lp-currency"
                  value={form.currencyCode ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, currencyCode: e.target.value }))}
                  className="w-full"
                  placeholder="e.g. USD"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4 flex align-items-end">
              <FormField label="Active" htmlFor="lp-active">
                <Checkbox
                  inputId="lp-active"
                  checked={form.active ?? true}
                  onChange={(e) => setForm((f) => ({ ...f, active: e.checked ?? true }))}
                />
              </FormField>
            </div>
          </div>
        </FormSection>
        <FormSection title="ATM limits">
          <div className="grid p-fluid">
            <div className="col-12 md:col-4">
              <FormField label="Daily amount" htmlFor="lp-atm-daily">
                <InputNumber
                  inputId="lp-atm-daily"
                  value={form.atmDailyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, atmDailyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Monthly amount" htmlFor="lp-atm-monthly">
                <InputNumber
                  inputId="lp-atm-monthly"
                  value={form.atmMonthlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, atmMonthlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Yearly amount" htmlFor="lp-atm-yearly">
                <InputNumber
                  inputId="lp-atm-yearly"
                  value={form.atmYearlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, atmYearlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
          </div>
        </FormSection>
        <FormSection title="POS limits">
          <div className="grid p-fluid">
            <div className="col-12 md:col-4">
              <FormField label="Daily amount" htmlFor="lp-pos-daily">
                <InputNumber
                  inputId="lp-pos-daily"
                  value={form.posDailyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, posDailyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Monthly amount" htmlFor="lp-pos-monthly">
                <InputNumber
                  inputId="lp-pos-monthly"
                  value={form.posMonthlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, posMonthlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Yearly amount" htmlFor="lp-pos-yearly">
                <InputNumber
                  inputId="lp-pos-yearly"
                  value={form.posYearlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, posYearlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
          </div>
        </FormSection>
        <FormSection title="E-commerce limits">
          <div className="grid p-fluid">
            <div className="col-12 md:col-4">
              <FormField label="Daily amount" htmlFor="lp-ecom-daily">
                <InputNumber
                  inputId="lp-ecom-daily"
                  value={form.ecommerceDailyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, ecommerceDailyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Monthly amount" htmlFor="lp-ecom-monthly">
                <InputNumber
                  inputId="lp-ecom-monthly"
                  value={form.ecommerceMonthlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, ecommerceMonthlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-4">
              <FormField label="Yearly amount" htmlFor="lp-ecom-yearly">
                <InputNumber
                  inputId="lp-ecom-yearly"
                  value={form.ecommerceYearlyAmount ?? null}
                  onValueChange={(e) => setForm((f) => ({ ...f, ecommerceYearlyAmount: e.value ?? undefined }))}
                  mode="decimal"
                  minFractionDigits={0}
                  maxFractionDigits={2}
                  className="w-full"
                />
              </FormField>
            </div>
          </div>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={deleteDialogOpen}
        onHide={() => { setDeleteDialogOpen(false); setRowToDelete(null); }}
        message={rowToDelete ? `Delete limit profile "${rowToDelete.profileCode}"? This action cannot be undone.` : ''}
        detail="Cards using this profile will keep the code until you assign a different profile."
        variant="delete"
        acceptLabel="Delete"
        onAccept={confirmDelete}
        loading={deleteMutation.isPending}
      />
    </div>
  );
}
