'use client';

import React from 'react';

export interface CardVisualProps {
  cardTitle?: string | null;
  panMasked?: string | null;
  expiryDate?: string | null;
  productName?: string | null;
  cardTypeName?: string | null;
}

/** Format masked PAN as 4 groups, preserving first 6 and last 4 when available. */
function formatPanGroups(panMasked: string | null | undefined): string {
  if (!panMasked || !panMasked.trim()) return '**** **** **** ****';
  const raw = panMasked.trim().replace(/\s+/g, '');
  if (raw.length < 16) {
    const last4 = raw.length >= 4 ? raw.slice(-4) : raw;
    return '**** **** **** ' + last4;
  }
  return `${raw.slice(0, 4)} ${raw.slice(4, 8)} ${raw.slice(8, 12)} ${raw.slice(12, 16)}`;
}

/** Format expiry as MM/YY */
function formatExpiry(expiryDate: string | null | undefined): string {
  if (!expiryDate) return '—';
  try {
    const d = new Date(expiryDate);
    if (isNaN(d.getTime())) return '—';
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = String(d.getFullYear()).slice(-2);
    return `${month}/${year}`;
  } catch {
    return '—';
  }
}

export function CardVisual({ cardTitle, panMasked, expiryDate, productName, cardTypeName }: CardVisualProps) {
  const panDisplay = formatPanGroups(panMasked ?? undefined);
  const expiryDisplay = formatExpiry(expiryDate ?? undefined);
  const productOrType = [productName, cardTypeName].filter(Boolean).join(' · ') || 'Card';

  return (
    <div
      className="rounded-lg overflow-hidden shadow-md border border-surface-200"
      style={{
        aspectRatio: '1.586',
        maxWidth: '22rem',
        background: 'linear-gradient(135deg, #1a1f36 0%, #2d3548 50%, #1a1f36 100%)',
        color: 'rgba(255,255,255,0.95)',
      }}
    >
      <div className="p-4 h-full flex flex-column justify-content-between">
        <div className="flex justify-content-between align-items-start">
          <div className="flex align-items-center gap-2">
            <div
              className="rounded"
              style={{ width: '2.25rem', height: '1.75rem', background: 'rgba(255,255,255,0.2)' }}
              aria-hidden
            />
            <span className="text-sm font-medium opacity-90">{productOrType}</span>
          </div>
          <div className="text-right text-sm">
            <div className="opacity-80">Expires</div>
            <div className="font-medium">{expiryDisplay}</div>
          </div>
        </div>
        <div className="text-xl font-mono tracking-widest letter-spacing-2" style={{ letterSpacing: '0.2em' }}>
          {panDisplay}
        </div>
        <div className="flex justify-content-between align-items-end">
          <div className="truncate font-medium" style={{ maxWidth: '14rem' }} title={cardTitle ?? undefined}>
            {cardTitle && cardTitle.trim() ? cardTitle.trim() : 'CARDHOLDER'}
          </div>
          <div className="text-sm">
            <span className="opacity-70">CVV</span> <span className="font-mono">***</span>
          </div>
        </div>
      </div>
    </div>
  );
}
