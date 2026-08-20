/** Display labels for CARD_REQUEST.REQUEST_TYPE_ID */
export function labelCardRequestSource(requestTypeId?: string | null): string {
  const t = (requestTypeId ?? '').trim().toUpperCase();
  if (t === 'CHANGE_TYPE') return 'Change card type';
  if (t === 'REPLACEMENT') return 'Replacement';
  if (t === 'NEW' || t === '1' || t === 'MOBILE') return 'New card';
  if (t) return requestTypeId!.trim();
  return 'New card';
}
