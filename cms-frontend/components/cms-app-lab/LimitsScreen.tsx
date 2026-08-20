'use client';

import { useCallback, useEffect, useState } from 'react';
import { availableLimit, authorizeTransaction, spendingSummary, validateLimit } from '@/services/cms-app/api';
import { formatMoney, getCardContext, getCustomLimit, setCustomLimit } from '@/services/cms-app/session';
import type { CardLimitValidateResponse } from '@/services/cms-app/types';
import { Banner, Field, PageShell, PrimaryButton, SecondaryButton, PanField } from './ui';

const CHANNELS = [
  { code: 1, label: 'ATM', hint: 'Daily cash withdrawal cap after today’s transactions.', icon: 'atm' },
  { code: 2, label: 'POS', hint: 'In-store spending limit for purchases at terminals.', icon: 'pos' },
  { code: 3, label: 'Ecommerce', hint: 'Online transactions limit for web and app payments.', icon: 'ecom' },
] as const;

type ChannelLimit = {
  code: number;
  label: string;
  profileMax: number | null;
  serverAvailable: number | null;
  error?: string;
};

function ChannelIcon({ kind }: { kind: (typeof CHANNELS)[number]['icon'] }) {
  if (kind === 'atm') {
    return (
      <svg viewBox="0 0 24 24" aria-hidden>
        <rect x="3" y="4" width="18" height="14" rx="2" fill="none" stroke="currentColor" strokeWidth="1.8" />
        <path d="M7 18v2M17 18v2M8 10h8M8 13h5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
      </svg>
    );
  }
  if (kind === 'pos') {
    return (
      <svg viewBox="0 0 24 24" aria-hidden>
        <path d="M4 8h16v11H4zM7 8V6a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v2" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round" />
        <path d="M8 12h3M8 15h5" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
      </svg>
    );
  }
  return (
    <svg viewBox="0 0 24 24" aria-hidden>
      <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" strokeWidth="1.8" />
      <path d="M3 12h18M12 3c2.5 3 3.8 6 3.8 9s-1.3 6-3.8 9c-2.5-3-3.8-6-3.8-9s1.3-6 3.8-9z" fill="none" stroke="currentColor" strokeWidth="1.8" />
    </svg>
  );
}

async function loadChannelLimit(
  pan: string,
  accountNumber: string,
  channel: (typeof CHANNELS)[number]
): Promise<ChannelLimit> {
  try {
    let avail: CardLimitValidateResponse | null = null;
    if (pan) {
      avail = await availableLimit(pan, channel.code).catch(() => null);
    }
    if (avail) {
      return {
        code: channel.code,
        label: channel.label,
        profileMax: avail.maxLimit ?? null,
        serverAvailable: avail.availableLimit ?? null,
      };
    }

    const rows = await spendingSummary({
      pan: pan || undefined,
      accountNumber: accountNumber || undefined,
      channelCode: String(channel.code),
    }).catch(() => []);

    const row = rows[0];
    if (row) {
      return {
        code: channel.code,
        label: channel.label,
        profileMax: row.maxLimit ?? null,
        serverAvailable: row.dailyAvailableSpending ?? null,
      };
    }

    return { code: channel.code, label: channel.label, profileMax: null, serverAvailable: null, error: 'No limit data' };
  } catch (err) {
    return {
      code: channel.code,
      label: channel.label,
      profileMax: null,
      serverAvailable: null,
      error: err instanceof Error ? err.message : 'Failed to load',
    };
  }
}

function resolveDisplay(pan: string, channelCode: number, profileMax: number | null, serverAvailable: number | null) {
  const spent = profileMax != null && serverAvailable != null ? Math.max(0, profileMax - serverAvailable) : 0;
  const stored = pan ? getCustomLimit(pan, channelCode) : null;
  const customized =
    stored != null && profileMax != null ? Math.min(Math.max(0, stored), profileMax) : profileMax;
  const available = customized != null ? Math.max(0, customized - spent) : serverAvailable;
  const usedPct = customized != null && customized > 0 ? Math.min(100, Math.round((spent / customized) * 100)) : 0;
  return { customized, available, spent, usedPct, profileMax };
}

export function LimitsScreen() {
  const cardCtx = getCardContext();
  const [pan, setPan] = useState(cardCtx?.pan || '');
  const [accountNumber, setAccountNumber] = useState(cardCtx?.accountNumber || '');
  const [channels, setChannels] = useState<ChannelLimit[]>([]);
  const [drafts, setDrafts] = useState<Record<number, string>>({});
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [savingCode, setSavingCode] = useState<number | null>(null);
  const [tick, setTick] = useState(0);
  const [payChannel, setPayChannel] = useState(1);
  const [payAmount, setPayAmount] = useState('');
  const [payPin, setPayPin] = useState('');
  const [payLoading, setPayLoading] = useState(false);

  const loadDashboard = useCallback(async () => {
    const panValue = pan.trim();
    const acct = accountNumber.trim();
    if (!panValue && !acct) {
      setInfo('Enter PAN or account number to view limits.');
      setChannels([]);
      return;
    }
    setError('');
    setInfo('');
    setLoading(true);
    try {
      const rows = await Promise.all(CHANNELS.map((ch) => loadChannelLimit(panValue, acct, ch)));
      setChannels(rows);
      const nextDrafts: Record<number, string> = {};
      for (const row of rows) {
        const display = resolveDisplay(panValue, row.code, row.profileMax, row.serverAvailable);
        nextDrafts[row.code] = display.customized != null ? String(display.customized) : '';
      }
      setDrafts(nextDrafts);
      if (rows.every((r) => r.profileMax == null && r.serverAvailable == null)) {
        setInfo('No limit data found for this card.');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load limits');
      setChannels([]);
    } finally {
      setLoading(false);
    }
  }, [accountNumber, pan]);

  useEffect(() => {
    void loadDashboard();
  }, [loadDashboard]);

  function onDraftChange(code: number, value: string, profileMax: number | null) {
    const cleaned = value.replace(/[^\d.]/g, '');
    if (profileMax != null && cleaned !== '' && Number(cleaned) > profileMax) {
      setDrafts((d) => ({ ...d, [code]: String(profileMax) }));
      return;
    }
    setDrafts((d) => ({ ...d, [code]: cleaned }));
  }

  function saveChannelCustom(code: number, profileMax: number | null) {
    setError('');
    setSuccess('');
    const panValue = pan.trim();
    if (!panValue) {
      setError('PAN is required to save a customized limit.');
      return;
    }
    if (profileMax == null) {
      setError('No profile max available for this channel.');
      return;
    }
    const amount = Number(drafts[code]);
    if (!Number.isFinite(amount) || amount < 0) {
      setError('Enter a valid customized limit.');
      return;
    }
    if (amount > profileMax) {
      setError(`Customized limit cannot exceed max (${formatMoney(profileMax)}).`);
      return;
    }
    setSavingCode(code);
    setCustomLimit(panValue, code, amount);
    setSuccess(`${CHANNELS.find((c) => c.code === code)?.label || 'Channel'} customized limit saved (on this device).`);
    setTick((t) => t + 1);
    setSavingCode(null);
  }

  async function onAuthorize() {
    setError('');
    setSuccess('');
    const panValue = pan.trim();
    if (!panValue) {
      setError('PAN is required to authorize a transaction.');
      return;
    }
    if (!payAmount || Number(payAmount) <= 0) {
      setError('Enter a valid amount.');
      return;
    }
    if (!payPin) {
      setError('PIN is required to authorize.');
      return;
    }
    setPayLoading(true);
    try {
      const check = await validateLimit(panValue, payChannel, payAmount);
      if (check.exceeded) {
        setError(check.message || 'Limit exceeded');
        return;
      }
      const result = await authorizeTransaction({
        pan: panValue,
        pin: payPin,
        channelCode: payChannel,
        amount: payAmount,
      });
      if (result.exceeded) {
        setError(result.message || 'Limit exceeded');
        return;
      }
      setSuccess(
        `Authorized ${formatMoney(Number(payAmount))}. Remaining ${formatMoney(result.data?.availableLimit)}.`
      );
      setPayPin('');
      await loadDashboard();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Authorize failed');
    } finally {
      setPayLoading(false);
    }
  }

  return (
    <PageShell title="Spending limits" subtitle="Available daily · customize under max" back>
      {error ? <Banner kind="error">{error}</Banner> : null}
      {success ? <Banner kind="success">{success}</Banner> : null}
      {info ? <Banner kind="info">{info}</Banner> : null}

      <section className="limits-controls form-stack">
        <PanField value={pan} onChange={setPan} />
        <Field label="Account number (optional)">
          <input
            value={accountNumber}
            onChange={(e) => setAccountNumber(e.target.value.replace(/\D/g, '').slice(0, 10))}
            inputMode="numeric"
            maxLength={10}
          />
        </Field>
        <SecondaryButton type="button" onClick={() => void loadDashboard()} disabled={loading}>
          {loading ? 'Loading…' : 'Refresh limits'}
        </SecondaryButton>
      </section>

      <section className="panel form-stack">
        <h3>Test spend (authorize)</h3>
        <p className="hint">Calls /card/limit/validate then /card/transaction/authorize — same as a live ATM/POS/ecom debit.</p>
        <div className="form-grid">
          <Field label="Channel">
            <select value={payChannel} onChange={(e) => setPayChannel(Number(e.target.value))}>
              {CHANNELS.map((ch) => (
                <option key={ch.code} value={ch.code}>
                  {ch.label}
                </option>
              ))}
            </select>
          </Field>
          <Field label="Amount">
            <input
              inputMode="decimal"
              value={payAmount}
              onChange={(e) => setPayAmount(e.target.value.replace(/[^\d.]/g, ''))}
              placeholder="0.00"
            />
          </Field>
          <Field label="PIN">
            <input
              type="password"
              inputMode="numeric"
              maxLength={12}
              value={payPin}
              onChange={(e) => setPayPin(e.target.value.replace(/\D/g, ''))}
            />
          </Field>
        </div>
        <PrimaryButton type="button" loading={payLoading} onClick={() => void onAuthorize()}>
          Authorize transaction
        </PrimaryButton>
      </section>

      <div className="limits-sections" key={tick}>
        {CHANNELS.map((ch) => {
          const row = channels.find((c) => c.code === ch.code);
          const display = resolveDisplay(pan.trim(), ch.code, row?.profileMax ?? null, row?.serverAvailable ?? null);
          const hasData = display.customized != null || display.available != null;
          const profileMax = row?.profileMax ?? null;
          const draftValue = drafts[ch.code] ?? '';

          return (
            <article key={ch.code} className="limits-section">
              <div className="limits-section-head">
                <span className="limits-icon">
                  <ChannelIcon kind={ch.icon} />
                </span>
                <div>
                  <h3>{ch.label}</h3>
                  <p>{ch.hint}</p>
                </div>
              </div>

              {hasData ? (
                <>
                  <div className="limits-amount-row">
                    <strong className="limits-amount">{formatMoney(display.available)}</strong>
                    <span className="limits-amount-meta">available today</span>
                  </div>
                  <p className="limits-cap">
                    of <strong>{formatMoney(display.customized)}</strong> customized
                    {profileMax != null && display.customized !== profileMax ? <> · max {formatMoney(profileMax)}</> : null}
                  </p>

                  <div className="limits-bar" aria-hidden>
                    <div className="limits-bar-track">
                      <div
                        className="limits-bar-fill"
                        style={{ width: `${Math.max(display.usedPct, display.usedPct > 0 ? 4 : 0)}%` }}
                      />
                    </div>
                    <div className="limits-bar-scale">
                      <span>0</span>
                      <span>Used {formatMoney(display.spent)}</span>
                      <span>{formatMoney(display.customized)}</span>
                    </div>
                  </div>

                  {profileMax != null ? (
                    <div className="limits-edit">
                      <span className="limits-edit-label">Set customized limit</span>
                      <div className="limits-edit-row">
                        <input
                          type="range"
                          min={0}
                          max={profileMax}
                          step={Math.max(1, Math.round(profileMax / 100))}
                          value={Number(draftValue) || 0}
                          onChange={(e) => onDraftChange(ch.code, e.target.value, profileMax)}
                        />
                        <input
                          type="number"
                          min={0}
                          max={profileMax}
                          value={draftValue}
                          onChange={(e) => onDraftChange(ch.code, e.target.value, profileMax)}
                        />
                      </div>
                      <p className="limits-edit-hint">
                        Must be less than or equal to max {formatMoney(profileMax)}. Saved on this device only.
                      </p>
                      <PrimaryButton type="button" loading={savingCode === ch.code} onClick={() => saveChannelCustom(ch.code, profileMax)}>
                        Save {ch.label} limit
                      </PrimaryButton>
                    </div>
                  ) : null}
                </>
              ) : (
                <p className="limits-empty">{loading ? 'Loading…' : row?.error || 'No data for this channel'}</p>
              )}
            </article>
          );
        })}
      </div>
    </PageShell>
  );
}
