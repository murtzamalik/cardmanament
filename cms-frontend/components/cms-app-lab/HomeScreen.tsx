'use client';

import { useCallback, useEffect, useState } from 'react';
import { cardInquiry } from '@/services/cms-app/api';
import {
  clearSession,
  formatExpiryMonthYear,
  getSession,
  markPinSet,
  maskPan,
  resolvePinSet,
  saveCardContext,
  updateRelationship,
} from '@/services/cms-app/session';
import type { CardInquiryResponse } from '@/services/cms-app/types';
import { PlasticCard } from './PlasticCard';
import { Banner, PageShell, PrimaryButton, SecondaryButton } from './ui';
import { useLabNav } from './nav-context';

export function HomeScreen() {
  const { go } = useLabNav();
  const session = getSession();
  const [rel, setRel] = useState(session?.relationshipNum || '');
  const [card, setCard] = useState<CardInquiryResponse | null>(null);
  const [pending, setPending] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const load = useCallback(async (relationshipNum: string) => {
    setLoading(true);
    setError('');
    setMessage('');
    setPending(false);
    setCard(null);
    try {
      const result = await cardInquiry(relationshipNum);
      if (result.code === 13) {
        setPending(true);
        setMessage(result.message);
        saveCardContext(null);
      } else if (result.code === 3) {
        setMessage(result.message || 'No card found for this relationship.');
        saveCardContext(null);
      } else if (result.card) {
        const pinSet = resolvePinSet(result.card.pan, result.card.pinSet);
        if (pinSet) markPinSet(result.card.pan);
        setCard({ ...result.card, pinSet });
        saveCardContext({
          pan: result.card.pan,
          relationshipNum,
          cardTitle: result.card.cardTitle,
          pinSet,
        });
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Inquiry failed');
      saveCardContext(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (session?.relationshipNum) void load(session.relationshipNum);
  }, [load, session?.relationshipNum]);

  function refresh() {
    const value = rel.trim();
    if (value.length !== 13) {
      setError('Relationship number must be 13 digits.');
      return;
    }
    updateRelationship(value);
    void load(value);
  }

  function logout() {
    clearSession();
    go('login');
  }

  return (
    <PageShell
      title="My card"
      subtitle={session?.fullName || 'Welcome'}
      action={
        <button type="button" className="text-btn" onClick={logout}>
          Logout
        </button>
      }
    >
      {error ? <Banner kind="error">{error}</Banner> : null}
      {!error && message && !pending && !card ? <Banner kind="info">{message}</Banner> : null}

      <div className="home-grid">
        <PlasticCard card={card} pending={pending} emptyMessage={message} />

        {card ? (
          <section className="panel details">
            <h3>Card details</h3>
            <dl>
              <div>
                <dt>PAN</dt>
                <dd>{maskPan(card.pan)}</dd>
              </div>
              <div>
                <dt>Status</dt>
                <dd>
                  <span className="status-chip">{card.cardStatusCode || '—'}</span>
                </dd>
              </div>
              <div>
                <dt>PIN</dt>
                <dd>{resolvePinSet(card.pan, card.pinSet) ? 'Set' : 'Not set'}</dd>
              </div>
              <div>
                <dt>Production</dt>
                <dd>{card.cardProdStatus || '—'}</dd>
              </div>
              <div>
                <dt>Expiry</dt>
                <dd>{formatExpiryMonthYear(card.expiryDate)}</dd>
              </div>
              <div>
                <dt>Type</dt>
                <dd>{card.cardTypeName || '—'}</dd>
              </div>
            </dl>
          </section>
        ) : (
          <section className="panel">
            <h3>Next step</h3>
            <p className="hint">No issued card yet. Submit a request or refresh after production.</p>
          </section>
        )}
      </div>

      <section className="panel">
        <label className="field">
          <span>Relationship number</span>
          <div className="row-input">
            <input
              inputMode="numeric"
              maxLength={13}
              value={rel}
              onChange={(e) => setRel(e.target.value.replace(/\D/g, '').slice(0, 13))}
            />
            <SecondaryButton type="button" onClick={refresh} disabled={loading}>
              {loading ? '…' : 'Refresh'}
            </SecondaryButton>
          </div>
        </label>
      </section>

      <div className="action-grid">
        <PrimaryButton type="button" onClick={() => go('request')}>
          Request card
        </PrimaryButton>
        <SecondaryButton type="button" onClick={() => go('limits')}>
          View limits
        </SecondaryButton>
        {card ? (
          <>
            <SecondaryButton type="button" onClick={() => go('pin')}>
              Manage PIN
            </SecondaryButton>
            <SecondaryButton type="button" onClick={() => go('status')}>
              Change status
            </SecondaryButton>
          </>
        ) : null}
      </div>
    </PageShell>
  );
}
