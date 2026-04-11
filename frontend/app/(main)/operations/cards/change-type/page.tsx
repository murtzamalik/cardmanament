'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { Toast } from 'primereact/toast';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import * as CardService from '@/services/cards/CardService';
import { FormSection, FormField } from '@/components/ui';
import { CARD_RETURN_FROM_PARAM, ROUTES } from '@/lib/constants';

export default function ChangeCardTypePage() {
  const router = useRouter();
  const toast = React.useRef<Toast>(null);
  const queryClient = useQueryClient();
  /** Raw input — avoid InputNumber: it often leaves value uncommitted until blur, so Load saw null. */
  const [cardIdInput, setCardIdInput] = useState('');
  const [cardId, setCardId] = useState<number | null>(null);
  const [cardTypeId, setCardTypeId] = useState<number | null>(null);

  const { data: dropdowns = {} } = useQuery({
    queryKey: ['card-dropdowns'],
    queryFn: CardService.getDropdowns,
  });

  const { data: card, isFetching: cardLoading } = useQuery({
    queryKey: ['card', cardId],
    queryFn: () => CardService.getCardById(cardId!),
    enabled: cardId != null && Number.isFinite(cardId) && cardId > 0,
  });

  const changeMutation = useMutation({
    mutationFn: () => {
      if (cardId == null || cardId < 1) throw new Error('Enter a valid card ID');
      if (cardTypeId == null) throw new Error('Select a card type');
      return CardService.changeCardType(cardId, { cardTypeId });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['card', cardId] });
      queryClient.invalidateQueries({ queryKey: ['cards-search'] });
      toast.current?.show({
        severity: 'success',
        summary: 'Success',
        detail: 'Card type updated',
        life: 3000,
      });
    },
    onError: (e: Error) => {
      toast.current?.show({ severity: 'error', summary: 'Error', detail: e.message, life: 6000 });
    },
  });

  const typeOptions =
    dropdowns.cardTypes?.map((t) => ({ label: `${t.name ?? t.code} (${t.code})`, value: t.id })) ?? [];

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
    queryClient.invalidateQueries({ queryKey: ['card', id] });
  };

  return (
    <div className="card">
      <Toast ref={toast} />
      <div className="mb-4">
        <h1 className="m-0 mb-1" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
          Change card type
        </h1>
        <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
          Enter the card ID, confirm the card, then select the new card type (must be active).
        </p>
      </div>

      <FormSection title="Card" className="pb-3">
        <div className="flex flex-wrap align-items-end gap-2">
          <FormField label="Card ID" htmlFor="ct-card-id" className="mb-0">
            <InputText
              id="ct-card-id"
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
                  `/operations/cards/${card.cardId}?${CARD_RETURN_FROM_PARAM}=${encodeURIComponent(ROUTES.cardsChangeType)}`
                )
              }
            />
          )}
        </div>
        {card && (
          <p className="mt-3 mb-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
            <strong className="text-color">PAN:</strong> {card.panMasked ?? '—'} ·{' '}
            <strong className="text-color">Current type:</strong> {card.cardTypeName ?? card.cardTypeCode ?? '—'} ·{' '}
            <strong className="text-color">Status:</strong> {card.cardStatusName ?? card.cardStatusCode ?? '—'}
          </p>
        )}
      </FormSection>

      <FormSection title="New card type" className="pb-3">
        <FormField label="Card type" htmlFor="ct-type" className="mb-0 max-w-30rem">
          <Dropdown
            inputId="ct-type"
            placeholder="Select card type"
            value={cardTypeId}
            options={typeOptions}
            onChange={(e) => setCardTypeId(e.value ?? null)}
            className="w-full"
            filter
            showClear
          />
        </FormField>
        <div className="mt-3">
          <Button
            label="Change card type"
            icon="pi pi-check"
            onClick={() => changeMutation.mutate()}
            loading={changeMutation.isPending}
            disabled={!card || cardTypeId == null}
          />
        </div>
      </FormSection>
    </div>
  );
}
