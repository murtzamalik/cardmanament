'use client';

import React, { useMemo, useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Checkbox } from 'primereact/checkbox';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { Tooltip } from 'primereact/tooltip';
import { useQueries } from '@tanstack/react-query';
import { useReferenceDataList, useReferenceDataCreate, useReferenceDataUpdate, useReferenceDataDelete } from '@/hooks/useReferenceData';
import * as refDataService from '@/services/reference-data/refDataService';
import { AppDialog, ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { RefDataConfig, RefDataRecord, RefDataFormFieldConfig } from '@/types/reference-data';

interface ReferenceDataCrudPageProps {
  config: RefDataConfig;
  /** Display name for the entity (e.g. "Branch", "Account status") for buttons and dialog title */
  entityLabel?: string;
  /** Plural for page title (e.g. "Branches", "Account statuses"). If omitted, entityLabel + "s" is used. */
  entityLabelPlural?: string;
}

export function ReferenceDataCrudPage({ config, entityLabel = 'Record', entityLabelPlural }: ReferenceDataCrudPageProps) {
  const toast = useRef<Toast>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<RefDataRecord | null>(null);
  const [form, setForm] = useState<RefDataRecord>({});
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [rowToDelete, setRowToDelete] = useState<RefDataRecord | null>(null);

  const pageTitle = entityLabelPlural ?? `${entityLabel}s`;

  const { data: list = [], isLoading } = useReferenceDataList(config);
  const createMutation = useReferenceDataCreate(config);
  const updateMutation = useReferenceDataUpdate(config);
  const deleteMutation = useReferenceDataDelete(config);

  const idField = config.idField;

  const dropdownApiPaths = useMemo(
    () =>
      Array.from(new Set(config.formFields.filter((f): f is RefDataFormFieldConfig & { optionsApiPath: string } => f.type === 'dropdown' && !!f.optionsApiPath).map((f) => f.optionsApiPath))),
    [config.formFields]
  );

  const dropdownQueries = useQueries({
    queries: dropdownApiPaths.map((apiPath) => ({
      queryKey: ['reference-data-options', apiPath],
      queryFn: () => refDataService.refDataGetAll(apiPath),
      enabled: dialogOpen,
    })),
  });

  const optionsByApiPath = useMemo(() => {
    const map: Record<string, RefDataRecord[]> = {};
    dropdownApiPaths.forEach((path, i) => {
      map[path] = (dropdownQueries[i]?.data as RefDataRecord[] | undefined) ?? [];
    });
    return map;
  }, [dropdownApiPaths, dropdownQueries]);

  const openCreate = () => {
    setEditingRecord(null);
    setFieldErrors({});
    setForm(
      config.formFields.reduce<RefDataRecord>((acc, f) => {
        acc[f.name] = f.type === 'checkbox' ? false : f.type === 'dropdown' ? '' : '';
        return acc;
      }, {})
    );
    setDialogOpen(true);
  };

  const openEdit = (row: RefDataRecord) => {
    setEditingRecord(row);
    setFieldErrors({});
    setForm(
      config.formFields.reduce<RefDataRecord>((acc, f) => {
        const v = row[f.name];
        acc[f.name] = v !== undefined && v !== null ? v : f.type === 'checkbox' ? false : f.type === 'dropdown' ? '' : '';
        return acc;
      }, {})
    );
    setDialogOpen(true);
  };

  const hideDialog = () => {
    setDialogOpen(false);
    setEditingRecord(null);
    setFieldErrors({});
  };

  const getEditId = (): string => {
    const id = editingRecord?.[idField];
    return typeof id === 'string' ? id : String(id ?? '');
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};
    config.formFields.forEach((f) => {
      if (f.required && (f.type === 'text' || f.type === 'dropdown')) {
        const v = form[f.name];
        if (v === undefined || v === null || String(v).trim() === '') {
          errors[f.name] = `${f.label} is required.`;
        }
      }
    });
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const save = async () => {
    if (!validate()) return;
    if (editingRecord) {
      const id = getEditId();
      const body = { ...form };
      delete body[idField];
      // Empty dropdowns → null (so optional Long fields like defaultLimitProfileId clear cleanly)
      config.formFields.forEach((f) => {
        if (f.type === 'dropdown' && (body[f.name] === '' || body[f.name] === undefined)) {
          body[f.name] = null;
        }
      });
      try {
        await updateMutation.mutateAsync({ id, body });
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Updated', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Update failed', life: 5000 });
      }
    } else {
      try {
        const body = { ...form };
        config.formFields.forEach((f) => {
          if (f.type === 'dropdown' && (body[f.name] === '' || body[f.name] === undefined)) {
            body[f.name] = null;
          }
        });
        await createMutation.mutateAsync(body);
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Created', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Create failed', life: 5000 });
      }
    }
  };

  const openDeleteConfirm = (row: RefDataRecord) => {
    setRowToDelete(row);
    setDeleteDialogOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!rowToDelete) return;
    const id = rowToDelete[idField];
    const idStr = typeof id === 'string' ? id : String(id ?? '');
    try {
      await deleteMutation.mutateAsync(idStr);
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Deleted', life: 3000 });
      setDeleteDialogOpen(false);
      setRowToDelete(null);
    } catch (e) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Delete failed', life: 5000 });
    }
  };

  const leftToolbar = () => (
    <div className="flex gap-2">
      <Button label={`Add ${entityLabel}`} icon="pi pi-plus" onClick={openCreate} />
    </div>
  );

  const actionBody = (row: RefDataRecord) => (
    <>
      <Tooltip target={`.edit-btn-${row[idField]}`} content="Edit" position="top" />
      <Button
        icon="pi pi-pencil"
        text
        rounded
        className={`p-button-text edit-btn-${row[idField]}`}
        onClick={() => openEdit(row)}
      />
      <Tooltip target={`.delete-btn-${row[idField]}`} content="Delete" position="top" />
      <Button
        icon="pi pi-trash"
        text
        rounded
        className={`p-button-text p-button-danger delete-btn-${row[idField]}`}
        onClick={() => openDeleteConfirm(row)}
      />
    </>
  );

  const cellValue = (row: RefDataRecord, field: string) => {
    const v = row[field];
    if (typeof v === 'boolean') return v ? 'Yes' : 'No';
    return v != null ? String(v) : '—';
  };

  const deleteConfirmMessage = rowToDelete
    ? `Delete this ${entityLabel.toLowerCase()}? This action cannot be undone.`
    : '';
  const deleteConfirmDetail = rowToDelete ? (rowToDelete[idField] != null ? `Code: ${String(rowToDelete[idField])}` : undefined) : undefined;

  return (
    <div className="grid">
      <div className="col-12">
        <div className="card">
          <Toast ref={toast} />
          <div className="mb-3">
            <h2 className="m-0 font-semibold" style={{ fontSize: '1.25rem' }}>{pageTitle}</h2>
            <p className="text-color-secondary mt-1 mb-0" style={{ fontSize: '0.875rem' }}>
              Manage {entityLabel.toLowerCase()} reference data. Add, edit, or remove records.
            </p>
          </div>
          <div className="flex justify-content-between align-items-center mb-3">
            {leftToolbar()}
          </div>
          <DataTable value={list} loading={isLoading} paginator rows={10} rowsPerPageOptions={[5, 10, 25]} tableStyle={{ minWidth: '50rem' }}>
            {config.columns.map((col) => (
              <Column key={col.field} field={col.field} header={col.header} body={(row) => cellValue(row, col.field)} sortable />
            ))}
            <Column header="Actions" body={actionBody} style={{ width: '8rem' }} />
          </DataTable>
        </div>
      </div>

      <AppDialog
        visible={dialogOpen}
        onHide={hideDialog}
        title={editingRecord ? `Edit ${entityLabel}` : `Add ${entityLabel}`}
        subtitle={editingRecord ? 'Update the details below.' : 'Enter the details below.'}
        width={config.formFields.length > 3 ? '42rem' : '32rem'}
        primaryLabel="Save"
        secondaryLabel="Cancel"
        onPrimary={save}
        onSecondary={hideDialog}
        loading={createMutation.isPending || updateMutation.isPending}
      >
        <FormSection title="Details" className="pt-0">
          <div className="grid p-fluid">
            {config.formFields.map((f) => {
              const disabled = !!editingRecord && f.disabledWhenEdit;
              const error = fieldErrors[f.name];
              let fieldEl: React.ReactNode;
              if (f.type === 'checkbox') {
                fieldEl = (
                  <FormField key={f.name} label={f.label} required={f.required} disabled={disabled} htmlFor={f.name} error={error}>
                    <Checkbox
                      inputId={f.name}
                      checked={!!form[f.name]}
                      onChange={(e) => setForm((prev) => ({ ...prev, [f.name]: e.checked ?? false }))}
                      disabled={disabled}
                    />
                  </FormField>
                );
              } else if (f.type === 'dropdown' && f.optionsApiPath && f.optionValueField && f.optionLabelField) {
                const optionsRaw = optionsByApiPath[f.optionsApiPath] ?? [];
                const options = optionsRaw.map((item) => ({
                  value: item[f.optionValueField!] as string,
                  label: String(item[f.optionLabelField!] ?? item[f.optionValueField!] ?? ''),
                }));
                fieldEl = (
                  <FormField key={f.name} label={f.label} htmlFor={f.name} required={f.required} disabled={disabled} error={error}>
                    <Dropdown
                      id={f.name}
                      value={form[f.name] ?? ''}
                      options={options}
                      optionLabel="label"
                      optionValue="value"
                      placeholder="Select"
                      onChange={(e) => {
                        setForm((prev) => ({ ...prev, [f.name]: e.value ?? '' }));
                        if (fieldErrors[f.name]) setFieldErrors((prev) => { const next = { ...prev }; delete next[f.name]; return next; });
                      }}
                      disabled={disabled}
                      className={`w-full ${error ? 'p-invalid' : ''}`}
                    />
                  </FormField>
                );
              } else {
                fieldEl = (
                  <FormField key={f.name} label={f.label} htmlFor={f.name} required={f.required} disabled={disabled} error={error}>
                    <InputText
                      id={f.name}
                      value={String(form[f.name] ?? '')}
                      onChange={(e) => {
                        setForm((prev) => ({ ...prev, [f.name]: e.target.value }));
                        if (fieldErrors[f.name]) setFieldErrors((prev) => { const next = { ...prev }; delete next[f.name]; return next; });
                      }}
                      disabled={disabled}
                      className={`w-full ${error ? 'p-invalid' : ''}`}
                    />
                  </FormField>
                );
              }
              return (
                <div key={f.name} className="col-12 md:col-6">
                  {fieldEl}
                </div>
              );
            })}
          </div>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={deleteDialogOpen}
        onHide={() => { setDeleteDialogOpen(false); setRowToDelete(null); }}
        message={deleteConfirmMessage}
        detail={deleteConfirmDetail}
        variant="delete"
        acceptLabel="Delete"
        onAccept={handleConfirmDelete}
        loading={deleteMutation.isPending}
      />
    </div>
  );
}
