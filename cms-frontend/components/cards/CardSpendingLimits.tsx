'use client';

import { useCallback, useEffect, useState } from 'react';
import { Button } from 'primereact/button';
import { availableLimit } from '@/services/cms-app/api';
import { FormSection } from '@/components/ui';
import styles from './CardSpendingLimits.module.css';

/** Local portal test: loads ATM / POS / Ecommerce available limits from cms-app. */
const CHANNELS = [
  {
    code: 1,
    label: 'ATM',
    hint: 'Daily cash withdrawal cap after today’s transactions.',
    icon: 'atm' as const,
  },
  {
    code: 2,
    label: 'POS',
    hint: 'In-store spending limit for purchases at terminals.',
    icon: 'pos' as const,
  },
  {
    code: 3,
    label: 'Ecommerce',
    hint: 'Online transactions limit for web and app payments.',
    icon: 'ecom' as const,
  },
];

type ChannelRow = {
  code: number;
  label: string;
  hint: string;
  icon: (typeof CHANNELS)[number]['icon'];
  maxLimit: number | null;
  availableLimit: number | null;
  error?: string;
};

function formatMoney(n?: number | null): string {
  if (n == null || Number.isNaN(n)) return '—';
  return new Intl.NumberFormat('en-US', {
    maximumFractionDigits: 2,
    minimumFractionDigits: 0,
  }).format(n);
}

function ChannelIcon({ kind }: { kind: ChannelRow['icon'] }) {
  if (kind === 'atm') {
    return (
      <svg viewBox="0 0 24 24" aria-hidden className={styles.iconSvg}>
        <rect x="3" y="4" width="18" height="14" rx="2" fill="none" stroke="currentColor" strokeWidth="1.8" />
        <path
          d="M7 18v2M17 18v2M8 10h8M8 13h5"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.8"
          strokeLinecap="round"
        />
      </svg>
    );
  }
  if (kind === 'pos') {
    return (
      <svg viewBox="0 0 24 24" aria-hidden className={styles.iconSvg}>
        <path
          d="M4 8h16v11H4zM7 8V6a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v2"
          fill="none"
          stroke="currentColor"
          strokeWidth="1.8"
          strokeLinejoin="round"
        />
        <path d="M8 12h3M8 15h5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
      </svg>
    );
  }
  return (
    <svg viewBox="0 0 24 24" aria-hidden className={styles.iconSvg}>
      <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" strokeWidth="1.8" />
      <path
        d="M3 12h18M12 3c2.5 3 3.8 6 3.8 9s-1.3 6-3.8 9c-2.5-3-3.8-6-3.8-9s1.3-6 3.8-9z"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
      />
    </svg>
  );
}

async function loadChannel(pan: string, channel: (typeof CHANNELS)[number]): Promise<ChannelRow> {
  try {
    const avail = await availableLimit(pan, channel.code);
    return {
      code: channel.code,
      label: channel.label,
      hint: channel.hint,
      icon: channel.icon,
      maxLimit: avail.maxLimit ?? null,
      availableLimit: avail.availableLimit ?? null,
    };
  } catch (err) {
    return {
      code: channel.code,
      label: channel.label,
      hint: channel.hint,
      icon: channel.icon,
      maxLimit: null,
      availableLimit: null,
      error: err instanceof Error ? err.message : 'Failed to load',
    };
  }
}

export function CardSpendingLimits({ pan }: { pan?: string | null }) {
  const [rows, setRows] = useState<ChannelRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const load = useCallback(async () => {
    const panValue = (pan || '').trim();
    if (!panValue) {
      setRows([]);
      setMessage('No PAN on this card — limits cannot be loaded.');
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      const next = await Promise.all(CHANNELS.map((ch) => loadChannel(panValue, ch)));
      setRows(next);
      if (next.every((r) => r.maxLimit == null && r.availableLimit == null)) {
        setMessage('No limit data returned. Is cms-app running on the configured URL?');
      }
    } catch (err) {
      setRows([]);
      setMessage(err instanceof Error ? err.message : 'Failed to load spending limits');
    } finally {
      setLoading(false);
    }
  }, [pan]);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <FormSection
      title="Spending limits"
      description="Available daily after transactions · ATM / POS / Ecommerce (cms-app local test)"
      className="pt-0"
    >
      <div className={styles.toolbar}>
        <Button
          type="button"
          label={loading ? 'Loading…' : 'Refresh'}
          icon="pi pi-refresh"
          outlined
          size="small"
          disabled={loading || !pan}
          onClick={() => void load()}
        />
      </div>

      {message ? <p className={styles.message}>{message}</p> : null}

      <div className={styles.grid}>
        {CHANNELS.map((ch) => {
          const row = rows.find((r) => r.code === ch.code);
          const max = row?.maxLimit ?? null;
          const available = row?.availableLimit ?? null;
          const spent = max != null && available != null ? Math.max(0, max - available) : 0;
          const usedPct = max != null && max > 0 ? Math.min(100, Math.round((spent / max) * 100)) : 0;
          const hasData = max != null || available != null;

          return (
            <article key={ch.code} className={styles.card}>
              <div className={styles.head}>
                <span className={styles.iconWrap}>
                  <ChannelIcon kind={ch.icon} />
                </span>
                <div>
                  <h3 className={styles.title}>{ch.label}</h3>
                  <p className={styles.hint}>{ch.hint}</p>
                </div>
              </div>

              {hasData ? (
                <>
                  <div className={styles.amountRow}>
                    <strong className={styles.amount}>{formatMoney(available)}</strong>
                    <span className={styles.amountMeta}>available today</span>
                  </div>
                  <p className={styles.cap}>
                    of <strong>{formatMoney(max)}</strong> customized daily limit
                  </p>
                  <div className={styles.barTrack} aria-hidden>
                    <div
                      className={styles.barFill}
                      style={{ width: `${Math.max(usedPct, usedPct > 0 ? 4 : 0)}%` }}
                    />
                  </div>
                  <div className={styles.barScale}>
                    <span>0</span>
                    <span>Used {formatMoney(spent)}</span>
                    <span>{formatMoney(max)}</span>
                  </div>
                </>
              ) : (
                <p className={styles.empty}>
                  {loading ? 'Loading…' : row?.error || 'No data for this channel'}
                </p>
              )}
            </article>
          );
        })}
      </div>
    </FormSection>
  );
}
