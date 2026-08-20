'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { Toolbar } from 'primereact/toolbar';
import { usePermissions, usePermissionCreate } from '@/hooks/usePermissions';
import { AppDialog, FormSection, FormField } from '@/components/ui';
import type { PermissionCreateRequest } from '@/types/permission';

export default function PermissionsPage() {
  const toast = useRef<Toast>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState<PermissionCreateRequest>({
    permissionId: '',
    perParentId: '',
    permissionName: '',
    permissionType: '',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: permissions = [], isLoading } = usePermissions();
  const createMutation = usePermissionCreate();

  const openCreate = () => {
    setFieldErrors({});
    setForm({ permissionId: '', perParentId: '', permissionName: '', permissionType: '' });
    setDialogOpen(true);
  };

  const hideDialog = () => {
    setDialogOpen(false);
    setFieldErrors({});
  };

  const validate = (): boolean => {
    const errors: Record<string, string> = {};
    if (!form.permissionId?.trim()) errors.permissionId = 'Permission ID is required.';
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const save = async () => {
    if (!validate()) return;
    try {
      await createMutation.mutateAsync(form);
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Permission created', life: 3000 });
      hideDialog();
    } catch (e) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Create failed', life: 5000 });
    }
  };

  const leftToolbar = () => (
    <div className="flex gap-2">
      <Button label="Add permission" icon="pi pi-plus" onClick={openCreate} />
    </div>
  );

  return (
    <div className="grid">
      <div className="col-12">
        <div className="card">
          <Toast ref={toast} />
          <div className="mb-4">
            <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Permissions</h1>
            <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
              Manage permission definitions and hierarchy.
            </p>
          </div>
          <Toolbar left={leftToolbar} />
          <DataTable value={permissions} loading={isLoading} paginator rows={10} rowsPerPageOptions={[5, 10, 25]} tableStyle={{ minWidth: '50rem' }}>
            <Column field="permissionId" header="Permission ID" sortable />
            <Column field="perParentId" header="Parent ID" sortable />
            <Column field="permissionName" header="Name" sortable />
            <Column field="permissionType" header="Type" sortable />
          </DataTable>
        </div>
      </div>

      <AppDialog
        visible={dialogOpen}
        onHide={hideDialog}
        title="Add permission"
        subtitle="Enter the details below."
        width="32rem"
        primaryLabel="Save"
        secondaryLabel="Cancel"
        onPrimary={save}
        onSecondary={hideDialog}
        loading={createMutation.isPending}
      >
        <FormSection title="Details" className="pt-0">
          <div className="grid p-fluid">
            <div className="col-12 md:col-6">
              <FormField label="Permission ID" htmlFor="permissionId" required error={fieldErrors.permissionId}>
                <InputText
                  id="permissionId"
                  value={form.permissionId}
                  onChange={(e) => {
                    setForm((f) => ({ ...f, permissionId: e.target.value }));
                    if (fieldErrors.permissionId) setFieldErrors((prev) => { const next = { ...prev }; delete next.permissionId; return next; });
                  }}
                  className={`w-full ${fieldErrors.permissionId ? 'p-invalid' : ''}`}
                />
              </FormField>
            </div>
            <div className="col-12 md:col-6">
              <FormField label="Parent ID" htmlFor="perParentId">
                <InputText
                  id="perParentId"
                  value={form.perParentId ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, perParentId: e.target.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-6">
              <FormField label="Name" htmlFor="permissionName">
                <InputText
                  id="permissionName"
                  value={form.permissionName ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, permissionName: e.target.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
            <div className="col-12 md:col-6">
              <FormField label="Type" htmlFor="permissionType">
                <InputText
                  id="permissionType"
                  value={form.permissionType ?? ''}
                  onChange={(e) => setForm((f) => ({ ...f, permissionType: e.target.value }))}
                  className="w-full"
                />
              </FormField>
            </div>
          </div>
        </FormSection>
      </AppDialog>
    </div>
  );
}
