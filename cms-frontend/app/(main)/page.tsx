'use client';

import React, { useMemo } from 'react';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { Button } from 'primereact/button';
import { Chart } from 'primereact/chart';
import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Tag } from 'primereact/tag';
import { Message } from 'primereact/message';
import 'chart.js/auto';
import type { ChartData } from 'chart.js';
import { getDashboardSummary } from '@/services/dashboard/DashboardService';
import { ROUTES } from '@/lib/constants';
import type { CardRequest } from '@/types/card';
import type { DashboardExpiringCard } from '@/types/dashboard';
import { useAuth } from '@/services/auth/AuthContext';

function KpiCard({
  label,
  value,
  icon,
  iconBg,
  iconColor,
  href,
}: {
  label: string;
  value: number;
  icon: string;
  iconBg: string;
  iconColor: string;
  href?: string;
}) {
  const content = (
    <div className="card mb-0 h-full">
      <div className="flex justify-content-between mb-3">
        <div>
          <span className="block text-500 font-medium mb-3">{label}</span>
          <div className="text-900 font-medium text-xl">{value.toLocaleString()}</div>
        </div>
        <div
          className={`flex align-items-center justify-content-center border-round ${iconBg}`}
          style={{ width: '2.5rem', height: '2.5rem' }}
        >
          <i className={`pi ${icon} ${iconColor} text-xl`} />
        </div>
      </div>
      {href ? <span className="text-primary text-sm">View details</span> : null}
    </div>
  );
  return href ? (
    <Link href={href} className="no-underline text-color">
      {content}
    </Link>
  ) : (
    content
  );
}

function progressBody(r: CardRequest) {
  const flag = r.progressFlag;
  if (flag == null) return '—';
  const severity: 'success' | 'warning' | 'info' = flag === 1 ? 'success' : flag === 0 ? 'warning' : 'info';
  const value = flag === 1 ? 'Done' : flag === 0 ? 'Pending' : String(flag);
  return <Tag value={value} severity={severity} />;
}

function createdBody(r: CardRequest) {
  return r.createdOn ? new Date(r.createdOn).toLocaleString() : '—';
}

function expiryBody(c: DashboardExpiringCard) {
  return c.expiryDate ? new Date(c.expiryDate).toLocaleDateString() : '—';
}

export default function DashboardPage() {
  const { user } = useAuth();
  const { data, isLoading, isError, error, refetch, isFetching } = useQuery({
    queryKey: ['dashboard-summary'],
    queryFn: getDashboardSummary,
    refetchOnWindowFocus: true,
  });

  const requestChartData: ChartData = useMemo(() => {
    const map = data?.requestsByStatus ?? {};
    const labels = Object.keys(map);
    return {
      labels,
      datasets: [
        {
          label: 'Requests',
          data: labels.map((k) => map[k] ?? 0),
          backgroundColor: ['#f59e0b', '#22c55e', '#ef4444', '#94a3b8'],
        },
      ],
    };
  }, [data?.requestsByStatus]);

  const cardChartData: ChartData = useMemo(() => {
    const map = data?.cardsByStatus ?? {};
    const labels = Object.keys(map);
    const palette = ['#1e40af', '#0ea5e9', '#6366f1', '#dc2626', '#64748b', '#14b8a6'];
    return {
      labels,
      datasets: [
        {
          label: 'Cards',
          data: labels.map((k) => map[k] ?? 0),
          backgroundColor: labels.map((_, i) => palette[i % palette.length]),
        },
      ],
    };
  }, [data?.cardsByStatus]);

  const chartOptions = {
    plugins: { legend: { position: 'bottom' as const } },
    maintainAspectRatio: false,
  };

  if (isLoading) {
    return (
      <div className="flex align-items-center justify-content-center" style={{ minHeight: '50vh' }}>
        <ProgressSpinner />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="card">
        <Message
          severity="error"
          className="w-full mb-3"
          text={(error as Error)?.message || 'Failed to load dashboard'}
        />
        <Button label="Retry" icon="pi pi-refresh" onClick={() => refetch()} />
      </div>
    );
  }

  const summary = data!;

  return (
    <div className="grid">
      <div className="col-12">
        <div className="flex flex-column md:flex-row md:align-items-center md:justify-content-between gap-2 mb-2">
          <div>
            <h2 className="m-0 text-900">Operations Dashboard</h2>
            <p className="m-0 mt-1 text-600">
              {user?.fullName ? `Welcome, ${user.fullName}` : 'Card operations overview'}
            </p>
          </div>
          <Button
            label="Refresh"
            icon="pi pi-refresh"
            outlined
            loading={isFetching}
            onClick={() => refetch()}
          />
        </div>
      </div>

      <div className="col-12 md:col-6 xl:col-3">
        <KpiCard
          label="Pending approval"
          value={summary.pendingApproval}
          icon="pi-check-circle"
          iconBg="bg-orange-100"
          iconColor="text-orange-500"
          href={ROUTES.cardRequests}
        />
      </div>
      <div className="col-12 md:col-6 xl:col-3">
        <KpiCard
          label="Open requests"
          value={summary.openRequests}
          icon="pi-inbox"
          iconBg="bg-blue-100"
          iconColor="text-blue-500"
          href={ROUTES.cardRequestSearch}
        />
      </div>
      <div className="col-12 md:col-6 xl:col-3">
        <KpiCard
          label="Issued today"
          value={summary.issuedToday}
          icon="pi-credit-card"
          iconBg="bg-green-100"
          iconColor="text-green-500"
          href={ROUTES.cards}
        />
      </div>
      <div className="col-12 md:col-6 xl:col-3">
        <KpiCard
          label="Expiring in 30 days"
          value={summary.expiringIn30Days}
          icon="pi-calendar"
          iconBg="bg-cyan-100"
          iconColor="text-cyan-500"
          href={ROUTES.cardsExpiry}
        />
      </div>
      <div className="col-12 md:col-6 xl:col-3">
        <KpiCard
          label="Hot cards"
          value={summary.hotCards}
          icon="pi-exclamation-triangle"
          iconBg="bg-red-100"
          iconColor="text-red-500"
          href={ROUTES.cards}
        />
      </div>

      <div className="col-12 lg:col-6">
        <div className="card">
          <h5 className="mt-0">Requests by status</h5>
          <div style={{ height: '260px' }}>
            <Chart type="doughnut" data={requestChartData} options={chartOptions} />
          </div>
        </div>
      </div>
      <div className="col-12 lg:col-6">
        <div className="card">
          <h5 className="mt-0">Cards by status</h5>
          <div style={{ height: '260px' }}>
            <Chart type="bar" data={cardChartData} options={chartOptions} />
          </div>
        </div>
      </div>

      <div className="col-12 xl:col-6">
        <div className="card">
          <div className="flex align-items-center justify-content-between mb-3">
            <h5 className="m-0">Checker queue</h5>
            <Link href={ROUTES.cardRequests}>
              <Button label="Open" icon="pi pi-arrow-right" text size="small" />
            </Link>
          </div>
          <DataTable
            value={summary.checkerQueue}
            rows={5}
            paginator={summary.checkerQueue.length > 5}
            emptyMessage="No pending approvals"
            size="small"
            responsiveLayout="scroll"
          >
            <Column field="requestId" header="ID" style={{ width: '5rem' }} />
            <Column field="relationshipNum" header="Relationship" />
            <Column field="cardTitle" header="Title" />
            <Column field="branchCode" header="Branch" />
            <Column header="Status" body={progressBody} />
            <Column header="Created" body={createdBody} />
          </DataTable>
        </div>
      </div>

      <div className="col-12 xl:col-6">
        <div className="card">
          <div className="flex align-items-center justify-content-between mb-3">
            <h5 className="m-0">Maker queue</h5>
            <Link href={ROUTES.cardRequestSearch}>
              <Button label="Open" icon="pi pi-arrow-right" text size="small" />
            </Link>
          </div>
          <DataTable
            value={summary.makerQueue}
            rows={5}
            paginator={summary.makerQueue.length > 5}
            emptyMessage="No open maker items"
            size="small"
            responsiveLayout="scroll"
          >
            <Column field="requestId" header="ID" style={{ width: '5rem' }} />
            <Column field="relationshipNum" header="Relationship" />
            <Column field="cardTitle" header="Title" />
            <Column field="productCode" header="Product" />
            <Column header="Status" body={progressBody} />
            <Column header="Created" body={createdBody} />
          </DataTable>
        </div>
      </div>

      <div className="col-12">
        <div className="card">
          <div className="flex align-items-center justify-content-between mb-3">
            <h5 className="m-0">Expiring soon</h5>
            <Link href={ROUTES.cardsExpiry}>
              <Button label="Card expiry" icon="pi pi-arrow-right" text size="small" />
            </Link>
          </div>
          <DataTable
            value={summary.expiringSoon}
            rows={5}
            paginator={summary.expiringSoon.length > 5}
            emptyMessage="No cards expiring in the next 30 days"
            size="small"
            responsiveLayout="scroll"
          >
            <Column field="cardId" header="Card ID" style={{ width: '6rem' }} />
            <Column
              header="PAN"
              body={(c: DashboardExpiringCard) => (c.panLast4 ? `****${c.panLast4}` : '—')}
            />
            <Column field="relationshipNum" header="Relationship" />
            <Column field="cardTitle" header="Title" />
            <Column field="branchCode" header="Branch" />
            <Column field="cardStatusCode" header="Status" />
            <Column header="Expiry" body={expiryBody} />
          </DataTable>
        </div>
      </div>
    </div>
  );
}
