'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Checkbox } from 'primereact/checkbox';
import { MultiSelect } from 'primereact/multiselect';
import { Toast } from 'primereact/toast';
import { Toolbar } from 'primereact/toolbar';
import { Tooltip } from 'primereact/tooltip';
import { useUsers, useUserCreate, useUserUpdate, useUserDelete } from '@/hooks/useUsers';
import { useRoles } from '@/hooks/useRoles';
import { AppDialog, ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import type { UserResponse, UserCreateRequest, UserUpdateRequest } from '@/types/user';

export default function UsersPage() {
  const toast = useRef<Toast>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserResponse | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<UserResponse | null>(null);
  const [form, setForm] = useState<Partial<UserCreateRequest> & { active?: boolean }>({
    loginId: '',
    password: '',
    fullName: '',
    emailAddress: '',
    appId: '',
    groupIds: [],
    active: true,
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const { data: users = [], isLoading } = useUsers();
  const { data: roles = [] } = useRoles();
  const createMutation = useUserCreate();
  const updateMutation = useUserUpdate();
  const deleteMutation = useUserDelete();
  const saveLoading = createMutation.isPending || updateMutation.isPending;

  const openCreate = () => {
    setEditingUser(null);
    setForm({
      loginId: '',
      password: '',
      fullName: '',
      emailAddress: '',
      appId: '',
      groupIds: [],
      active: true,
    });
    setDialogOpen(true);
  };

  const openEdit = (row: UserResponse) => {
    setEditingUser(row);
    setForm({
      loginId: row.loginId,
      fullName: row.fullName ?? '',
      emailAddress: row.emailAddress ?? '',
      appId: row.appId ?? '',
      groupIds: row.roleIds ?? [],
      active: row.active ?? true,
    });
    setDialogOpen(true);
  };

  const hideDialog = () => {
    setDialogOpen(false);
    setEditingUser(null);
  };

  const save = async () => {
    if (editingUser) {
      const req: UserUpdateRequest = {
        fullName: form.fullName || undefined,
        password: form.password || undefined,
        active: form.active,
        groupIds: form.groupIds?.length ? form.groupIds : undefined,
      };
      try {
        await updateMutation.mutateAsync({ id: editingUser.id ?? editingUser.loginId, req });
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'User updated', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Update failed', life: 5000 });
      }
    } else {
      const req: UserCreateRequest = {
        loginId: form.loginId!,
        password: form.password || undefined,
        fullName: form.fullName || undefined,
        emailAddress: form.emailAddress || undefined,
        appId: form.appId || undefined,
        groupIds: form.groupIds?.length ? form.groupIds : undefined,
      };
      try {
        await createMutation.mutateAsync(req);
        toast.current?.show({ severity: 'success', summary: 'Success', detail: 'User created', life: 3000 });
        hideDialog();
      } catch (e) {
        toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Create failed', life: 5000 });
      }
    }
  };

  const openDeleteConfirm = (row: UserResponse) => {
    setUserToDelete(row);
    setDeleteDialogOpen(true);
  };

  const confirmDelete = async () => {
    if (!userToDelete) return;
    try {
      await deleteMutation.mutateAsync(userToDelete.id ?? userToDelete.loginId);
      toast.current?.show({ severity: 'success', summary: 'Success', detail: 'User deleted', life: 3000 });
      setDeleteDialogOpen(false);
      setUserToDelete(null);
    } catch (e) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Delete failed', life: 5000 });
    }
  };

  const leftToolbar = () => (
    <div className="flex gap-2">
      <Button label="Add user" icon="pi pi-plus" onClick={openCreate} />
    </div>
  );

  const actionBody = (row: UserResponse) => (
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

  const roleOptions = roles.map((r) => ({ label: r.groupName || r.groupId, value: r.groupId }));

  return (
    <div className="grid">
      <div className="col-12">
        <div className="card">
          <Toast ref={toast} />
          <Tooltip target="[data-pr-tooltip]" />
          <div className="mb-4">
            <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Users</h1>
            <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
              Manage user accounts, profiles, and role assignments.
            </p>
          </div>
          <Toolbar left={leftToolbar} />
          <DataTable value={users} loading={isLoading} paginator rows={10} rowsPerPageOptions={[5, 10, 25]} tableStyle={{ minWidth: '50rem' }}>
            <Column field="loginId" header="Login ID" sortable />
            <Column field="fullName" header="Full Name" sortable />
            <Column field="emailAddress" header="Email" sortable />
            <Column field="appId" header="App ID" sortable />
            <Column field="active" header="Active" body={(row) => (row.active ? 'Yes' : 'No')} sortable />
            <Column header="Roles" body={(row) => (row.roleNames?.length ? row.roleNames.join(', ') : '—')} />
            <Column header="Actions" body={actionBody} style={{ width: '8rem' }} />
          </DataTable>
        </div>
      </div>

      <AppDialog
        visible={dialogOpen}
        onHide={hideDialog}
        title={editingUser ? 'Edit user' : 'Add user'}
        subtitle={editingUser ? 'Update the details below.' : 'Enter the details below.'}
        onPrimary={save}
        onSecondary={hideDialog}
        loading={saveLoading}
        width="32rem"
      >
        <FormSection title="Account" className="pt-0">
          <FormField label="Login ID" htmlFor="loginId" required disabled={!!editingUser} error={fieldErrors.loginId}>
            <InputText
              id="loginId"
              value={form.loginId ?? ''}
              onChange={(e) => {
                setForm((f) => ({ ...f, loginId: e.target.value }));
                if (fieldErrors.loginId) setFieldErrors((prev) => { const next = { ...prev }; delete next.loginId; return next; });
              }}
              disabled={!!editingUser}
              className={`w-full ${fieldErrors.loginId ? 'p-invalid' : ''}`}
            />
          </FormField>
          <FormField
            label={editingUser ? 'Password (leave blank to keep)' : 'Password'}
            htmlFor="password"
            required={!editingUser}
            error={fieldErrors.password}
          >
            <Password
              id="password"
              value={form.password ?? ''}
              onChange={(e) => {
                setForm((f) => ({ ...f, password: e.target.value }));
                if (fieldErrors.password) setFieldErrors((prev) => { const next = { ...prev }; delete next.password; return next; });
              }}
              feedback={!editingUser}
              toggleMask
              className={`w-full ${fieldErrors.password ? 'p-invalid' : ''}`}
            />
          </FormField>
        </FormSection>
        <FormSection title="Profile">
          <FormField label="Full name" htmlFor="fullName">
            <InputText
              id="fullName"
              value={form.fullName ?? ''}
              onChange={(e) => setForm((f) => ({ ...f, fullName: e.target.value }))}
              className="w-full"
            />
          </FormField>
          <FormField label="Email" htmlFor="email">
            <InputText
              id="email"
              value={form.emailAddress ?? ''}
              onChange={(e) => setForm((f) => ({ ...f, emailAddress: e.target.value }))}
              className="w-full"
            />
          </FormField>
          <FormField label="App ID" htmlFor="appId">
            <InputText
              id="appId"
              value={form.appId ?? ''}
              onChange={(e) => setForm((f) => ({ ...f, appId: e.target.value }))}
              className="w-full"
            />
          </FormField>
        </FormSection>
        <FormSection title="Access">
          <FormField label="Roles" htmlFor="roles">
            <MultiSelect
              inputId="roles"
              value={form.groupIds ?? []}
              options={roleOptions}
              onChange={(e) => setForm((f) => ({ ...f, groupIds: e.value ?? [] }))}
              placeholder="Select roles"
              className="w-full"
            />
          </FormField>
          <FormField label="Active" htmlFor="active">
            <Checkbox
              inputId="active"
              checked={form.active ?? false}
              onChange={(e) => setForm((f) => ({ ...f, active: e.checked ?? false }))}
            />
          </FormField>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={deleteDialogOpen}
        onHide={() => { setDeleteDialogOpen(false); setUserToDelete(null); }}
        message={userToDelete ? `Delete user "${userToDelete.loginId}"?` : ''}
        detail="This cannot be undone."
        variant="delete"
        acceptLabel="Delete"
        onAccept={confirmDelete}
        loading={deleteMutation.isPending}
      />
    </div>
  );
}
