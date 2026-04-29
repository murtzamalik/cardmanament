'use client';

import React, { useState } from 'react';
import { Column } from 'primereact/column';
import { DataTable, type DataTableStateEvent } from 'primereact/datatable';
import { InputText } from 'primereact/inputtext';
import { Calendar } from 'primereact/calendar';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { Tag } from 'primereact/tag';
import { useQuery } from '@tanstack/react-query';
import { getApiClient } from '@/services/api/client';
import { FormSection, FormField } from '@/components/ui';

interface AuditLog {
    id: number;
    loginId: string;
    action: string;
    httpMethod: string;
    apiPath: string;
    responseStatus: string;
    errorMessage?: string;
    ipAddress: string;
    createdAt: string;
}

interface AuditLogPage {
    content: AuditLog[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
}

const statusOptions = [
    { label: 'All', value: '' },
    { label: 'Success', value: 'SUCCESS' },
    { label: 'Failed', value: 'FAILED' },
];

const methodOptions = [
    { label: 'All', value: '' },
    { label: 'GET', value: 'GET' },
    { label: 'POST', value: 'POST' },
    { label: 'PUT', value: 'PUT' },
    { label: 'DELETE', value: 'DELETE' },
];

export default function AuditLogsPage() {
    const [loginId, setLoginId] = useState('');
    const [status, setStatus] = useState('');
    const [method, setMethod] = useState('');
    const [page, setPage] = useState(0);
    const [size] = useState(20);
    const [appliedFilters, setAppliedFilters] = useState({ loginId: '', status: '', method: '' });

    const { data, isLoading, refetch } = useQuery({
        queryKey: ['audit-logs', appliedFilters, page, size],
        queryFn: async () => {
            const client = getApiClient();
            const params = new URLSearchParams();
            params.set('page', String(page));
            params.set('size', String(size));
            if (appliedFilters.loginId) params.set('loginId', appliedFilters.loginId);
            const res = await client.get<AuditLogPage>(`/api/audit-logs?${params.toString()}`);
            return res.data;
        },
    });

    const logs = data?.content ?? [];
    const totalRecords = data?.totalElements ?? 0;

    const onPage = (e: DataTableStateEvent) => {
        const p = e.page ?? (e.rows ? Math.floor(e.first / e.rows) : 0);
        setPage(p);
    };

    const search = () => {
        setPage(0);
        setAppliedFilters({ loginId, status, method });
    };

    const reset = () => {
        setLoginId('');
        setStatus('');
        setMethod('');
        setPage(0);
        setAppliedFilters({ loginId: '', status: '', method: '' });
    };

    const statusBody = (row: AuditLog) => (
        <Tag
            value={row.responseStatus}
            severity={row.responseStatus === 'SUCCESS' ? 'success' : 'danger'}
        />
    );

    const methodBody = (row: AuditLog) => (
        <Tag
            value={row.httpMethod}
            severity={row.httpMethod === 'GET' ? 'info' : row.httpMethod === 'DELETE' ? 'danger' : 'warning'}
        />
    );

    const dateBody = (row: AuditLog) =>
        row.createdAt ? new Date(row.createdAt).toLocaleString() : '—';

    const actionBody = (row: AuditLog) => (
        <span style={{ fontSize: '0.8rem', fontFamily: 'monospace' }}>{row.action}</span>
    );

    const pathBody = (row: AuditLog) => (
        <span style={{ fontSize: '0.8rem', fontFamily: 'monospace' }}>{row.apiPath}</span>
    );

    const errorBody = (row: AuditLog) =>
        row.errorMessage ? (
            <span style={{ color: 'var(--red-500)', fontSize: '0.8rem' }}>{row.errorMessage}</span>
        ) : (
            <span className="text-color-secondary">—</span>
        );

    return (
        <div className="card">
            <div className="mb-4">
                <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
                    Audit Logs
                </h1>
                <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
                    Track all API activity — who did what, when, and whether it succeeded.
                </p>
            </div>

            <FormSection title="Filters" className="pb-3">
                <div className="flex flex-wrap align-items-end gap-2">
                    <FormField label="Login ID" htmlFor="al-login" className="mb-0">
                        <InputText
                            id="al-login"
                            value={loginId}
                            onChange={(e) => setLoginId(e.target.value)}
                            placeholder="e.g. admin"
                            className="w-12rem"
                        />
                    </FormField>
                    <FormField label="Status" htmlFor="al-status" className="mb-0">
                        <Dropdown
                            inputId="al-status"
                            value={status}
                            options={statusOptions}
                            onChange={(e) => setStatus(e.value)}
                            className="w-10rem"
                        />
                    </FormField>
                    <FormField label="Method" htmlFor="al-method" className="mb-0">
                        <Dropdown
                            inputId="al-method"
                            value={method}
                            options={methodOptions}
                            onChange={(e) => setMethod(e.value)}
                            className="w-10rem"
                        />
                    </FormField>
                    <div className="flex gap-2 align-items-end" style={{ paddingBottom: '0.25rem' }}>
                        <Button label="Search" icon="pi pi-search" onClick={search} />
                        <Button label="Reset" icon="pi pi-times" outlined onClick={reset} />
                    </div>
                </div>
            </FormSection>

            <DataTable
                value={logs}
                loading={isLoading}
                paginator
                rows={size}
                totalRecords={totalRecords}
                lazy
                first={page * size}
                onPage={onPage}
                rowsPerPageOptions={[10, 20, 50]}
                emptyMessage="No audit logs found."
                rowClassName={(row: AuditLog) =>
                    row.responseStatus === 'FAILED' ? 'bg-red-50' : ''
                }
            >
                <Column field="createdAt" header="Date / Time" body={dateBody} style={{ width: '12rem' }} />
                <Column field="loginId" header="User" style={{ width: '8rem' }} />
                <Column field="httpMethod" header="Method" body={methodBody} style={{ width: '6rem' }} />
                <Column field="apiPath" header="API Path" body={pathBody} />
                <Column field="action" header="Action" body={actionBody} />
                <Column field="responseStatus" header="Status" body={statusBody} style={{ width: '7rem' }} />
                <Column field="ipAddress" header="IP Address" style={{ width: '10rem' }} />
                <Column field="errorMessage" header="Error" body={errorBody} />
            </DataTable>
        </div>
    );
}
