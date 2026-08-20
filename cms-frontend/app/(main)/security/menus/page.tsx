'use client';

import React, { useMemo, useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { InputNumber } from 'primereact/inputnumber';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { Toast } from 'primereact/toast';
import { Toolbar } from 'primereact/toolbar';
import { Tooltip } from 'primereact/tooltip';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { menuService } from '@/services/security/MenuService';
import type { MenuCreateRequest, MenuResponse, MenuUpdateRequest } from '@/types/menu';
import { AppDialog, ConfirmActionDialog, FormField, FormSection } from '@/components/ui';

type MenuForm = {
  menuName: string;
  menuPath: string;
  parentMenuId: number | null;
  menuIcon: string;
  sortOrder: number;
  status: 'Y' | 'N';
};

export default function MenusPage() {
  const toast = useRef<Toast>(null);
  const qc = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [editingMenu, setEditingMenu] = useState<MenuResponse | null>(null);
  const [menuToDelete, setMenuToDelete] = useState<MenuResponse | null>(null);
  const [form, setForm] = useState<MenuForm>({
    menuName: '',
    menuPath: '',
    parentMenuId: null,
    menuIcon: '',
    sortOrder: 0,
    status: 'Y',
  });

  const { data: menus = [], isLoading } = useQuery({
    queryKey: ['menus'],
    queryFn: () => menuService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: (req: MenuCreateRequest) => menuService.create(req),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['menus'] }),
  });
  const updateMutation = useMutation({
    mutationFn: ({ id, req }: { id: number; req: MenuUpdateRequest }) => menuService.update(id, req),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['menus'] }),
  });
  const deleteMutation = useMutation({
    mutationFn: (id: number) => menuService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['menus'] }),
  });

  const flattenMenus = (items: MenuResponse[]): MenuResponse[] =>
    items.flatMap((m) => [m, ...(m.children ? flattenMenus(m.children) : [])]);
  const flatMenus = useMemo(() => flattenMenus(menus), [menus, flattenMenus]);
  const parentOptions = useMemo(
    () => [{ label: 'None (root)', value: null as number | null }, ...flatMenus.map((m) => ({ label: `${m.menuName} (${m.menuPath})`, value: m.id }))],
    [flatMenus]
  );

  const openCreate = () => {
    setEditingMenu(null);
    setForm({ menuName: '', menuPath: '', parentMenuId: null, menuIcon: '', sortOrder: 0, status: 'Y' });
    setDialogOpen(true);
  };
  const openEdit = (menu: MenuResponse) => {
    setEditingMenu(menu);
    setForm({
      menuName: menu.menuName ?? '',
      menuPath: menu.menuPath ?? '',
      parentMenuId: menu.parentMenuId ?? null,
      menuIcon: menu.menuIcon ?? '',
      sortOrder: menu.sortOrder ?? 0,
      status: menu.status === 'N' ? 'N' : 'Y',
    });
    setDialogOpen(true);
  };

  const save = async () => {
    try {
      if (editingMenu) {
        await updateMutation.mutateAsync({
          id: editingMenu.id,
          req: { ...form },
        });
      } else {
        await createMutation.mutateAsync(form);
      }
      toast.current?.show({ severity: 'success', summary: 'Success', detail: `Menu ${editingMenu ? 'updated' : 'created'}`, life: 3000 });
      setDialogOpen(false);
      setEditingMenu(null);
    } catch (e) {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Save failed', life: 5000 });
    }
  };

  const renderRows = (items: MenuResponse[], level = 0): MenuResponse[] =>
    items.flatMap((m) => [{ ...m, menuName: `${'  '.repeat(level)}${m.menuName}` }, ...(m.children ? renderRows(m.children, level + 1) : [])]);

  return (
    <div className="card">
      <Toast ref={toast} />
      <Tooltip target="[data-pr-tooltip]" />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>Menus</h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>Manage navigation menus and hierarchy.</p>
      </div>
      <Toolbar left={<Button label="Add menu" icon="pi pi-plus" onClick={openCreate} />} />
      <DataTable value={renderRows(menus)} loading={isLoading} paginator rows={10} rowsPerPageOptions={[10, 25, 50]}>
        <Column field="menuName" header="Menu Name" />
        <Column field="menuPath" header="Path" />
        <Column field="menuIcon" header="Icon" />
        <Column field="sortOrder" header="Order" />
        <Column field="status" header="Status" body={(r: MenuResponse) => (r.status === 'N' ? 'Inactive' : 'Active')} />
        <Column
          header="Actions"
          body={(row: MenuResponse) => (
            <>
              <Button icon="pi pi-pencil" text rounded onClick={() => openEdit(row)} data-pr-tooltip="Edit" data-pr-position="top" />
              <Button
                icon="pi pi-trash"
                text
                rounded
                className="p-button-danger"
                onClick={() => { setMenuToDelete(row); setDeleteDialogOpen(true); }}
                data-pr-tooltip="Delete"
                data-pr-position="top"
              />
            </>
          )}
          style={{ width: '8rem' }}
        />
      </DataTable>

      <AppDialog
        visible={dialogOpen}
        onHide={() => setDialogOpen(false)}
        title={editingMenu ? 'Edit menu' : 'Add menu'}
        subtitle="Configure menu item details."
        onPrimary={save}
        onSecondary={() => setDialogOpen(false)}
        loading={createMutation.isPending || updateMutation.isPending}
        width="36rem"
      >
        <FormSection title="Menu" className="pt-0">
          <FormField label="Menu Name" htmlFor="menuName" required>
            <InputText id="menuName" value={form.menuName} onChange={(e) => setForm((f) => ({ ...f, menuName: e.target.value }))} className="w-full" />
          </FormField>
          <FormField label="Path" htmlFor="menuPath" required>
            <InputText id="menuPath" value={form.menuPath} onChange={(e) => setForm((f) => ({ ...f, menuPath: e.target.value }))} className="w-full" />
          </FormField>
          <FormField label="Parent Menu" htmlFor="parentMenuId">
            <Dropdown
              inputId="parentMenuId"
              value={form.parentMenuId}
              options={parentOptions}
              onChange={(e) => setForm((f) => ({ ...f, parentMenuId: e.value ?? null }))}
              className="w-full"
              showClear
            />
          </FormField>
          <FormField label="Icon" htmlFor="menuIcon">
            <InputText id="menuIcon" value={form.menuIcon} onChange={(e) => setForm((f) => ({ ...f, menuIcon: e.target.value }))} className="w-full" />
          </FormField>
          <FormField label="Sort Order" htmlFor="sortOrder">
            <InputNumber id="sortOrder" value={form.sortOrder} onValueChange={(e) => setForm((f) => ({ ...f, sortOrder: e.value ?? 0 }))} className="w-full" />
          </FormField>
          <FormField label="Status" htmlFor="status">
            <Dropdown
              inputId="status"
              value={form.status}
              options={[{ label: 'Active', value: 'Y' }, { label: 'Inactive', value: 'N' }]}
              onChange={(e) => setForm((f) => ({ ...f, status: e.value ?? 'Y' }))}
              className="w-full"
            />
          </FormField>
        </FormSection>
      </AppDialog>

      <ConfirmActionDialog
        visible={deleteDialogOpen}
        onHide={() => { setDeleteDialogOpen(false); setMenuToDelete(null); }}
        message={menuToDelete ? `Delete menu "${menuToDelete.menuName}"?` : ''}
        detail="This cannot be undone."
        variant="delete"
        acceptLabel="Delete"
        onAccept={async () => {
          if (!menuToDelete) return;
          try {
            await deleteMutation.mutateAsync(menuToDelete.id);
            toast.current?.show({ severity: 'success', summary: 'Success', detail: 'Menu deleted', life: 3000 });
            setDeleteDialogOpen(false);
            setMenuToDelete(null);
          } catch (e) {
            toast.current?.show({ severity: 'error', summary: 'Error', detail: e instanceof Error ? e.message : 'Delete failed', life: 5000 });
          }
        }}
        loading={deleteMutation.isPending}
      />
    </div>
  );
}
