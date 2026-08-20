import { formatExpiryMonthYear, maskPan } from '../lib/session';
import type { CardInquiryResponse } from '../types/cms';

export function PlasticCard({
  card,
  pending,
  emptyMessage,
}: {
  card?: CardInquiryResponse | null;
  pending?: boolean;
  emptyMessage?: string;
}) {
  if (pending) {
    return (
      <div className="plastic-card plastic-pending">
        <div className="plastic-chip" />
        <p className="plastic-label">Card request</p>
        <h2>In process</h2>
        <p className="plastic-meta">Your new card is being prepared.</p>
      </div>
    );
  }

  if (!card) {
    return (
      <div className="plastic-card plastic-empty">
        <div className="plastic-chip muted" />
        <p className="plastic-label">No card yet</p>
        <h2>Request a card</h2>
        <p className="plastic-meta">{emptyMessage || 'Start from the Request tab.'}</p>
      </div>
    );
  }

  return (
    <div className="plastic-card">
      <div className="plastic-top">
        <span className="plastic-brand">CMS Card</span>
        <span className="plastic-type">{card.cardTypeName || 'Card'}</span>
      </div>
      <div className="plastic-chip" />
      <p className="plastic-pan">{maskPan(card.pan)}</p>
      <div className="plastic-bottom">
        <div>
          <span className="plastic-label">Cardholder</span>
          <strong>{card.cardTitle || '—'}</strong>
        </div>
        <div>
          <span className="plastic-label">Expiry</span>
          <strong>{formatExpiryMonthYear(card.expiryDate)}</strong>
        </div>
      </div>
    </div>
  );
}
