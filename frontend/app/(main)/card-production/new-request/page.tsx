'use client';

import React, { useRef, useState } from 'react';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery } from '@tanstack/react-query';
import * as CardRequestService from '@/services/cards/CardRequestService';
import * as CardService from '@/services/cards/CardService';
import { refDataGetAll } from '@/services/reference-data/refDataService';
import { FormSection, FormField } from '@/components/ui';
import type { NewCardRequestCreate, NewAccountRequest } from '@/types/card';

type AccountMode = 'existing' | 'new';

const defaultNewAccount: NewAccountRequest = {
  accountNum: '',
  accountTitle: '',
  acctTypeCode: '',
  acctStatusCode: 'OPEN',
  branchCode: '',
  accountTypeId: null,
  accountStatusId: null,
  branchId: null,
};

export default function NewCardRequestPage() {
  const toast = useRef<Toast>(null);
  const [accountMode, setAccountMode] = useState<AccountMode>('existing');
  const [form, setForm] = useState<NewCardRequestCreate>({
    relationshipNum: '',
    accountNum: '',
    cardTitle: '',
    cardTypeId: null,
    productId: null,
    branchId: null,
    supplementaryCount: 0,
  });
  const [newAccount, setNewAccount] = useState<NewAccountRequest>(defaultNewAccount);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const { data: accounts = [] } = useQuery({
    queryKey: ['available-accounts', form.relationshipNum],
    queryFn: () => CardService.getAvailableAccounts(form.relationshipNum),
    enabled: !!form.relationshipNum,
  });

  const { data: accountTypesRaw = [] } = useQuery({
    queryKey: ['account-types'],
    queryFn: () => refDataGetAll('/api/account-types'),
    enabled: accountMode === 'new',
  });

  const { data: accountStatusesRaw = [] } = useQuery({
    queryKey: ['account-statuses'],
    queryFn: () => refDataGetAll('/api/account-statuses'),
    enabled: accountMode === 'new',
  });

  const createMutation = useMutation({
    mutationFn: CardRequestService.createCardRequest,
    onSuccess: (data) => {
      toast.current?.show({
        severity: 'success',
        summary: 'Success',
        detail: data.requestId ? `Request #${data.requestId} created` : 'Card request created',
        life: 3000,
      });
      setForm({
        relationshipNum: '',
        accountNum: '',
        cardTitle: '',
        cardTypeId: null,
        productId: null,
        branchId: null,
        supplementaryCount: 0,
      });
      setNewAccount(defaultNewAccount);
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 5000 });
    },
  });

  const submit = () => {
    const hasCardType = form.cardTypeId != null || (form.cardTypeCode != null && form.cardTypeCode !== '');
    const hasProduct = form.productId != null || (form.productCode != null && form.productCode !== '');
    const hasBranch = form.branchId != null || (form.branchCode != null && form.branchCode !== '');
    if (!form.relationshipNum || !hasCardType || !hasProduct || !hasBranch) {
      toast.current?.show({ severity: 'warn', summary: 'Validation', detail: 'Fill required fields', life: 3000 });
      return;
    }
    if (accountMode === 'existing') {
      if (!form.accountNum) {
        toast.current?.show({ severity: 'warn', summary: 'Validation', detail: 'Select an account', life: 3000 });
        return;
      }
      createMutation.mutate({ ...form, accountNum: form.accountNum });
      return;
    }
    const hasAcctType = newAccount.accountTypeId != null || (newAccount.acctTypeCode != null && newAccount.acctTypeCode !== '');
    const hasAcctBranch = newAccount.branchId != null || (newAccount.branchCode != null && newAccount.branchCode !== '');
    if (
      !newAccount.accountNum?.trim() ||
      !newAccount.accountTitle?.trim() ||
      !hasAcctType ||
      !hasAcctBranch
    ) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Validation',
        detail: 'Fill all account fields (number, title, type, branch)',
        life: 3000,
      });
      return;
    }
    createMutation.mutate({
      ...form,
      accountNum: undefined,
      newAccount: {
        accountNum: newAccount.accountNum.trim(),
        accountTitle: newAccount.accountTitle.trim(),
        accountTypeId: newAccount.accountTypeId ?? undefined,
        acctTypeCode: newAccount.acctTypeCode,
        accountStatusId: newAccount.accountStatusId ?? undefined,
        acctStatusCode: newAccount.acctStatusCode || 'OPEN',
        branchId: newAccount.branchId ?? undefined,
        branchCode: newAccount.branchCode,
      },
    });
  };

  const branchOptions = dropdowns.branches?.map((b) => ({ label: b.branchName, value: b.id })) ?? [];
  const productOptions = dropdowns.cardProducts?.map((p) => ({ label: p.name, value: p.id })) ?? [];
  const selectedProductId = form.productId ?? null;
  const typeOptions =
    dropdowns.cardTypes
      ?.filter((t) => selectedProductId == null || t.productCode === dropdowns.cardProducts?.find((p) => p.id === selectedProductId)?.code)
      ?.map((t) => ({ label: t.name, value: t.id })) ?? [];
  const accountOptions = accounts.map((a) => ({
    label: `${a.accountNum} ${a.accountTitle ?? ''}`.trim(),
    value: a.accountNum,
  }));

  const accountTypeOptions = accountTypesRaw.map((x: Record<string, unknown>) => ({
    label: String(x.acctTypeName ?? x.acctTypeCode ?? ''),
    value: (x.id as number) ?? (x.acctTypeCode as string),
  }));
  const accountStatusOptions = accountStatusesRaw.map((x: Record<string, unknown>) => ({
    label: String(x.acctStatusName ?? x.acctStatusCode ?? ''),
    value: (x.id as number) ?? (x.acctStatusCode as string),
  }));

  const selectedAccountInfo =
    accountMode === 'existing' && form.accountNum
      ? accountOptions.find((o) => o.value === form.accountNum)?.label
      : null;

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          New Card Request
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Submit a new card request for an account. Add account details or select an existing account.
        </p>
      </div>
      <FormSection title="Customer & account">
        <div className="grid p-fluid">
          <div className="col-12 md:col-6">
            <FormField label="Relationship number" htmlFor="rel" required>
              <InputText
                id="rel"
                value={form.relationshipNum}
                onChange={(e) => setForm((f) => ({ ...f, relationshipNum: e.target.value }))}
                className="w-full"
                placeholder="Enter relationship number"
              />
            </FormField>
          </div>
          <div className="col-12">
            <FormField label="Account">
              <div className="flex flex-wrap gap-3 align-items-center">
                <label className="flex align-items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="accountMode"
                    checked={accountMode === 'existing'}
                    onChange={() => {
                      setAccountMode('existing');
                      setNewAccount(defaultNewAccount);
                    }}
                  />
                  Existing account
                </label>
                <label className="flex align-items-center gap-2 cursor-pointer">
                  <input
                    type="radio"
                    name="accountMode"
                    checked={accountMode === 'new'}
                    onChange={() => {
                      setAccountMode('new');
                      setForm((f) => ({ ...f, accountNum: '' }));
                    }}
                  />
                  New account
                </label>
              </div>
            </FormField>
          </div>
          {accountMode === 'existing' && (
            <>
              <div className="col-12 md:col-6">
                <FormField label="Select account" htmlFor="acct" required>
                  <Dropdown
                    id="acct"
                    value={form.accountNum}
                    options={accountOptions}
                    onChange={(e) => setForm((f) => ({ ...f, accountNum: e.value }))}
                    placeholder="Select account"
                    className="w-full"
                  />
                </FormField>
              </div>
              {selectedAccountInfo && (
                <div className="col-12 md:col-6">
                  <FormField label="Account info">
                    <span className="text-color-secondary">{selectedAccountInfo}</span>
                  </FormField>
                </div>
              )}
            </>
          )}
          {accountMode === 'new' && (
            <>
              <div className="col-12 md:col-6">
                <FormField label="Account number" htmlFor="new-acct-num" required>
                  <InputText
                    id="new-acct-num"
                    value={newAccount.accountNum}
                    onChange={(e) => setNewAccount((a) => ({ ...a, accountNum: e.target.value }))}
                    className="w-full"
                    placeholder="Account number"
                  />
                </FormField>
              </div>
              <div className="col-12 md:col-6">
                <FormField label="Account title" htmlFor="new-acct-title" required>
                  <InputText
                    id="new-acct-title"
                    value={newAccount.accountTitle}
                    onChange={(e) => setNewAccount((a) => ({ ...a, accountTitle: e.target.value }))}
                    className="w-full"
                    placeholder="Account title"
                  />
                </FormField>
              </div>
              <div className="col-12 md:col-6">
                <FormField label="Account type" htmlFor="new-acct-type" required>
                  <Dropdown
                    id="new-acct-type"
                    value={newAccount.accountTypeId ?? newAccount.acctTypeCode ?? ''}
                    options={accountTypeOptions}
                    onChange={(e) => setNewAccount((a) => ({
                      ...a,
                      accountTypeId: typeof e.value === 'number' ? e.value : null,
                      acctTypeCode: typeof e.value === 'string' ? e.value : '',
                    }))}
                    placeholder="Select account type"
                    className="w-full"
                  />
                </FormField>
              </div>
              <div className="col-12 md:col-6">
                <FormField label="Account status" htmlFor="new-acct-status">
                  <Dropdown
                    id="new-acct-status"
                    value={newAccount.accountStatusId ?? newAccount.acctStatusCode ?? 'OPEN'}
                    options={accountStatusOptions}
                    onChange={(e) => setNewAccount((a) => ({
                      ...a,
                      accountStatusId: typeof e.value === 'number' ? e.value : null,
                      acctStatusCode: typeof e.value === 'string' ? e.value : 'OPEN',
                    }))}
                    placeholder="Select status"
                    className="w-full"
                  />
                </FormField>
              </div>
              <div className="col-12 md:col-6">
                <FormField label="Account branch" htmlFor="new-acct-branch" required>
                  <Dropdown
                    id="new-acct-branch"
                    value={newAccount.branchId ?? newAccount.branchCode ?? ''}
                    options={branchOptions}
                    onChange={(e) => setNewAccount((a) => ({
                      ...a,
                      branchId: typeof e.value === 'number' ? e.value : null,
                      branchCode: typeof e.value === 'string' ? e.value : '',
                    }))}
                    placeholder="Select branch"
                    className="w-full"
                  />
                </FormField>
              </div>
            </>
          )}
        </div>
      </FormSection>
      <FormSection title="Card details">
        <div className="grid p-fluid">
          <div className="col-12 md:col-6">
            <FormField label="Card title" htmlFor="title">
              <InputText
                id="title"
                value={form.cardTitle ?? ''}
                onChange={(e) => setForm((f) => ({ ...f, cardTitle: e.target.value }))}
                className="w-full"
                placeholder="Optional card title"
              />
            </FormField>
          </div>
          <div className="col-12 md:col-6">
            <FormField label="Product" htmlFor="product" required>
              <Dropdown
                id="product"
                value={form.productId}
                options={productOptions}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    productId: e.value ?? null,
                    cardTypeId: null,
                  }))
                }
                placeholder="Select product"
                className="w-full"
              />
            </FormField>
          </div>
          <div className="col-12 md:col-6">
            <FormField label="Card type" htmlFor="type" required>
              <Dropdown
                id="type"
                value={form.cardTypeId}
                options={typeOptions}
                onChange={(e) => setForm((f) => ({ ...f, cardTypeId: e.value ?? null }))}
                placeholder={form.productId != null ? 'Select type' : 'Select product first'}
                className="w-full"
                disabled={form.productId == null}
              />
            </FormField>
          </div>
          <div className="col-12 md:col-6">
            <FormField label="Branch" htmlFor="branch" required>
              <Dropdown
                id="branch"
                value={form.branchId}
                options={branchOptions}
                onChange={(e) => setForm((f) => ({ ...f, branchId: e.value ?? null }))}
                placeholder="Select branch"
                className="w-full"
              />
            </FormField>
          </div>
          <div className="col-12 md:col-6">
            <FormField label="Supplementary count" htmlFor="supp">
              <InputNumber
                inputId="supp"
                value={form.supplementaryCount ?? 0}
                onValueChange={(e) => setForm((f) => ({ ...f, supplementaryCount: e.value ?? 0 }))}
                min={0}
                className="w-full"
              />
            </FormField>
          </div>
        </div>
      </FormSection>
      <div className="pt-3">
        <Button label="Submit request" icon="pi pi-check" onClick={submit} loading={createMutation.isPending} />
      </div>
    </div>
  );
}
