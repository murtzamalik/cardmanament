'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Checkbox } from 'primereact/checkbox';
import { Toast } from 'primereact/toast';
import { Toolbar } from 'primereact/toolbar';
import { Tooltip } from 'primereact/tooltip';
import { useRoles, useRoleCreate, useRoleUpdate, useRoleDelete } from '@/hooks/useRoles';
import { AppDialog, ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { RoleResponse, RoleCreateRequest, RoleUpdateRequest } from '@/types/role';

export default function RolesPage() {
  const toast = useRef<Toast>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleResponse | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [roleToDelete, setRoleToDelete] = useState<RoleResponse | null>(null);
  const [form, setForm] = useState<{ groupId: string; groupName: string; active: boolean }>({
    groupId: '',
    groupName: '',
    active: true,
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: roles = [], isLoading } = useRoles();
  const createMutation = useRoleCreate();
  const updateMutation = useRoleUpdate();
  const deleteMutation = useRoleDelete();
  const saveLoading = createMutation.isPending || updateMutation.isPending;

  const openCreate = () => {
    setEditingRole(null);
    setFieldErrors({});
    setForm({ groupId: '', groupName: '', active: true });
    setDialogOpen(true);
  };

  const openEdit = (row: RoleResponse) => {
    setEditingRole(row);
    setFieldErrors({});
    setForm({
      groupId: row.groupId,
      groupName: row.groupName ?? '',
      active: row.active ?? true,
    });
    setDialogOpen(true);
  };

  const hideDialog = () => {
    setDialogOpen(false);
    setEditingRole(null);
    setFieldErrors({});
  };

  const validateRole = (): boolean => {
    if (editingRole) return true;
    const errors: Record<string, string> = {};
    if (!form.groupId?.trim()) errors.groupId = 'Group ID is required.';
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const save = async () => {
    if (!validateRole()) return;
    if (editingRole) {
      const req: RoleUpdateRequest = { groupName: form.groupName || undefined, active: form.active };
      try {
        await updateMutation.mutateAsync({ id: editingRole.id ?? editingRole.groupId, req });
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Role updated', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Update failed', life: 5000 });
      }
    } else {
      const req: RoleCreateRequest = { groupId: form.groupId, groupName: form.groupName || undefined, active: form.active };
      try {
        await createMutation.mutateAsync(req);
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Role created', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Create failed', life: 5000 });
      }
    }
  };

  const openDeleteConfirm = (row: RoleResponse) => {
    setRoleToDelete(row);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (!roleToDelete) return;
    try {
      await deleteMutation.mutateAsync(roleToDelete.id ?? roleToDelete.groupId);
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Role deleted', life: 3000 });
      setDeleteDialogOpen(false);
      setRoleToDelete(null);
    } catch (e) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Delete failed', life: 5000 });
    }
  };

  const leftToolbar = () => (
    <div className="flex gap-2">
      <Button label="Add role" icon="pi pi-plus" onClick={openCreate} />
    </div>
  );

  const actionBody = (row: RoleResponse) => (
    <>
      <Button
        icon="pi pi-pencil"
        text
        rounded
        className="p-button-text"
        onClick={() => openEdit(row)}
        data-pr-tooltip="Edit"
        data-pr-position="top"
      />
      <Button
        icon="pi pi-trash"
        text
        rounded
        className="p-button-text p-button-danger"
        onClick={() => openDeleteConfirm(row)}
        data-pr-tooltip="Delete"
        data-pr-position="top"
      />
    </>
  );

  return (
    <div className="grid">
      <div className="col-12">
        <div className="card">
          <Toast ref={toast} />
          <Tooltip target="[data-pr-tooltip]" />
          <div className="mb-4">
            <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Roles</h1>
            <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
              Manage roles and access groups.
            </p>
          </div>
          <Toolbar left={leftToolbar} />
          <DataTable value={roles} loading={isLoading} paginator rows={10} rowsPerPageOptions={[5, 10, 25]} tableStyle={{ minWidth: '50rem' }}>
            <Column field="groupId" header="Group ID" sortable />
            <Column field="groupName" header="Group Name" sortable />
            <Column field="active" header="Active" body={(row) => (row.active ? 'Yes' : 'No')} sortable />
            <Column header="Actions" body={actionBody} style={{ width: '8rem' }} />
          </DataTable>
        </div>
      </div>

      <AppDialog
        visible={dialogOpen}
        onHide={hideDialog}
        title={editingRole ? 'Edit role' : 'Add role'}
        subtitle={editingRole ? 'Update the details below.' : 'Enter the details below.'}
        onPrimary={save}
        onSecondary={hideDialog}
        loading={saveLoading}
        width="32rem"
      >
        <FormSection title="Role" className="pt-0">
          <FormField label="Group ID" htmlFor="groupId" required disabled={!!editingRole} error={fieldErrors.groupId}>
            <InputText
              id="groupId"
              value={form.groupId}
              onChange={(e) => {
                setForm((f) => ({ ...f, groupId: e.target.value }));
                if (fieldErrors.groupId) setFieldErrors((prev) => { const next = { ...prev }; delete next.groupId; return next; });
              }}
              disabled={!!editingRole}
              className={`w-full ${fieldErrors.groupId ? 'p-invalid' : ''}`}
            />
          </FormField>
          <FormField label="Group name" htmlFor="groupName">
            <InputText
              id="groupName"
              value={form.groupName}
              onChange={(e) => setForm((f) => ({ ...f, groupName: e.target.value }))}
              className="w-full"
            />
          </FormField>
        </FormSection>
        <FormSection title="Status">
          <FormField label="Active" htmlFor="active">
            <Checkbox
              inputId="active"
              checked={form.active}
              onChange={(e) => setForm((f) => ({ ...f, active: e.checked ?? false }))}
            />
          </FormField>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={deleteDialogOpen}
        onHide={() => { setDeleteDialogOpen(false); setRoleToDelete(null); }}
        message={roleToDelete ? `Delete role "${roleToDelete.groupId}"?` : ''}
        detail="This cannot be undone."
        variant="delete"
        acceptLabel="Delete"
        onAccept={confirmDelete}
        loading={deleteMutation.isPending}
      />
    </div>
  );
}
