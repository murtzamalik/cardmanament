'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { ConfirmActionDialog, FormSection, FormField } from '@/components/ui';
import { CARD_RETURN_FROM_PARAM, ROUTES } from '@/lib/constants';

export default function ReplacementRequestPage() {
  const router = useRouter();
  const toast = React.useRef<Toast>(null);
  const queryClient = useQueryClient();
  const [cardIdInput, setCardIdInput] = useState('');
  const [cardId, setCardId] = useState<number | null>(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [lastRequestId, setLastRequestId] = useState<number | null>(null);

  const { data: card, isFetching: cardLoading } = useQuery({
    queryKey: ['card', cardId],
    queryFn: () => CardService.getCardById(cardId!),
    enabled: cardId != null && Number.isFinite(cardId) && cardId > 0,
  });

  const replacementMutation = useMutation({
    mutationFn: () => {
      if (cardId == null || cardId < 1) throw new Error('Enter a valid card ID');
      return CardService.createReplacementRequest(cardId);
    },
    onSuccess: (requestId) => {
      setLastRequestId(requestId);
      setConfirmOpen(false);
      queryClient.invalidateQueries({ queryKey: ['card', cardId] });
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({
        severity: 'success',
        summary: 'Replacement request created',
        detail: `Request #${requestId}`,
        life: 5000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 6000 });
    },
  });

  const parseCardId = (): number | null => {
    const n = parseInt(cardIdInput.trim(), 10);
    if (!Number.isFinite(n) || n < 1) return null;
    return n;
  };

  const loadCard = () => {
    const id = parseCardId();
    if (id == null) {
      toast.current?.show({ severity: 'warn', summary: 'Card ID', detail: 'Enter a positive card ID', life: 3000 });
      return;
    }
    setCardId(id);
    setLastRequestId(null);
    queryClient.invalidateQueries({ queryKey: ['card', id] });
  };

  const openConfirm = () => {
    if (cardId == null || cardId < 1 || !card) {
      toast.current?.show({
        severity: 'warn',
        summary: 'Load card first',
        detail: 'Enter card ID and load the card before requesting replacement.',
        life: 4000,
      });
      return;
    }
    setConfirmOpen(true);
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Replacement card request
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Marks the card inactive and creates a new card request with the same details. Confirm before submitting.
        </p>
      </div>

      <FormSection title="Card" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Card ID" htmlFor="rep-card-id" className="mb-0">
            <InputText
              id="rep-card-id"
              value={cardIdInput}
              onChange={(e) => setCardIdInput(e.target.value)}
              placeholder="e.g. 1"
              inputMode="numeric"
              className="w-12rem"
              onKeyDown={(e) => {
                if (e.key === 'Enter') loadCard();
              }}
            />
          </FormField>
          <Button type="button" label="Load card" icon="pi pi-download" onClick={loadCard} loading={cardLoading} />
          {card && (
            <Button
              type="button"
              label="Open detail"
              icon="pi pi-external-link"
              text
              onClick={() =>
                router.push(
                  `/operations/cards/${card.cardId}?${CARD_RETURN_FROM_PARAM}=${encodeURIComponent(ROUTES.cardsReplacement)}`
                )
              }
            />
          )}
        </div>
        {card && (
          <p className="mt-3 mb-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
            <strong className="text-color">PAN:</strong> {card.panMasked ?? '—'} ·{' '}
            <strong className="text-color">Type:</strong> {card.cardTypeName ?? card.cardTypeCode ?? '—'} ·{' '}
            <strong className="text-color">Status:</strong> {card.cardStatusName ?? card.cardStatusCode ?? '—'}
          </p>
        )}
      </FormSection>

      <FormSection title="Request replacement" className="pb-3">
        <Button
          label="Create replacement request…"
          icon="pi pi-replay"
          severity="danger"
          onClick={openConfirm}
          disabled={!card}
        />
        {lastRequestId != null && (
          <p className="mt-3 mb-0">
            Last created request:{' '}
            <Link href={ROUTES.cardRequests} className="text-primary font-medium">
              #{lastRequestId}
            </Link>{' '}
            (see Card Requests)
          </p>
        )}
      </FormSection>

      <ConfirmActionDialog
        visible={confirmOpen}
        onHide={() => setConfirmOpen(false)}
        message="Create replacement request for this card?"
        detail="The card will be marked inactive and a new card request will be created."
        variant="danger"
        acceptLabel="Create request"
        loading={replacementMutation.isPending}
        onAccept={() => replacementMutation.mutate()}
      />
    </div>
  );
}
