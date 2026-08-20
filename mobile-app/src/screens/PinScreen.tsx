import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { useLocation } from 'react-router-dom';
import { changePin, generatePin } from '../api/cms';
import { Banner, Field, PageShell, PrimaryButton, PanField } from '../components/ui';
import {
  getCardContext,
  getSession,
  markPinSet,
  resolvePinSet,
  saveCardContext,
} from '../lib/session';

type Mode = 'set' | 'forgot' | 'change';

function readPinAlreadySet(panHint?: string): boolean {
  const cardCtx = getCardContext();
  return resolvePinSet(panHint || cardCtx?.pan, cardCtx?.pinSet);
}

export function PinScreen() {
  const location = useLocation();
  const session = getSession();
  const cardCtx = getCardContext();

  const [pinAlreadySet, setPinAlreadySet] = useState(() => readPinAlreadySet());
  const [mode, setMode] = useState<Mode>(() =>
    readPinAlreadySet() ? 'change' : 'set',
  );
  const [pan, setPan] = useState(cardCtx?.pan || '');
  const [relationshipNum, setRelationshipNum] = useState(
    cardCtx?.relationshipNum || session?.relationshipNum || '',
  );
  const [oldPin, setOldPin] = useState('');
  const [pin, setPin] = useState('');
  const [confirmPin, setConfirmPin] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  // Re-sync whenever this screen is shown again or PAN changes.
  useEffect(() => {
    const ctx = getCardContext();
    if (ctx?.pan) setPan(ctx.pan);
    if (ctx?.relationshipNum) setRelationshipNum(ctx.relationshipNum);
    const set = readPinAlreadySet(ctx?.pan || pan);
    setPinAlreadySet(set);
    setMode((current) => (set && current === 'set' ? 'change' : current));
  }, [location.key, pan]);

  const tabs: Array<[Mode, string]> = pinAlreadySet
    ? [
        ['forgot', 'Forgot'],
        ['change', 'Change'],
      ]
    : [['set', 'Set PIN']];

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setSuccess('');
    const already = readPinAlreadySet(pan.trim());
    if (mode === 'set' && already) {
      setPinAlreadySet(true);
      setMode('change');
      setError('PIN already set. Use Change PIN or Forgot PIN.');
      return;
    }
    if (pin.length < 4) {
      setError('PIN must be at least 4 digits.');
      return;
    }
    if (pin !== confirmPin) {
      setError('PIN and confirm PIN do not match.');
      return;
    }
    setLoading(true);
    try {
      if (mode === 'change') {
        await changePin({
          pan: pan.trim(),
          relationshipNum: relationshipNum.trim(),
          oldPin,
          newPin: pin,
          confirmNewPin: confirmPin,
        });
      } else {
        await generatePin({
          pan: pan.trim(),
          relationshipNum: relationshipNum.trim(),
          pin,
          confirmPin,
          flag: mode === 'forgot' ? 'F' : undefined,
        });
      }
      setSuccess(
        mode === 'change'
          ? 'PIN changed successfully.'
          : mode === 'forgot'
            ? 'PIN reset successfully.'
            : 'PIN set successfully.',
      );
      setOldPin('');
      setPin('');
      setConfirmPin('');

      markPinSet(pan.trim());
      const ctx = getCardContext();
      saveCardContext({
        pan: pan.trim(),
        relationshipNum: relationshipNum.trim(),
        cardTitle: ctx?.cardTitle,
        pinSet: true,
        accountNumber: ctx?.accountNumber,
      });
      setPinAlreadySet(true);
      if (mode === 'set') {
        setMode('change');
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : 'PIN update failed';
      if (
        message.toLowerCase().includes('already set') ||
        message.toLowerCase().includes('pin already')
      ) {
        markPinSet(pan.trim());
        setPinAlreadySet(true);
        setMode('change');
      }
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <PageShell title="PIN management" subtitle="Set once, then change or reset" back>
      {pinAlreadySet ? (
        <Banner kind="info">PIN is already set. Use Forgot or Change with your current PIN.</Banner>
      ) : (
        <Banner kind="info">No PIN yet — use Set PIN for the first time.</Banner>
      )}

      <div className={`segmented cols-${tabs.length}`}>
        {tabs.map(([id, label]) => (
          <button
            key={id}
            type="button"
            className={mode === id ? 'active' : ''}
            onClick={() => {
              setMode(id);
              setError('');
              setSuccess('');
            }}
          >
            {label}
          </button>
        ))}
      </div>

      {error ? <Banner kind="error">{error}</Banner> : null}
      {success ? <Banner kind="success">{success}</Banner> : null}

      <form className="panel form-stack" onSubmit={onSubmit}>
        <PanField value={pan} onChange={setPan} required />
        <Field label="Relationship number">
          <input
            value={relationshipNum}
            onChange={(e) =>
              setRelationshipNum(e.target.value.replace(/\D/g, '').slice(0, 13))
            }
            required
            maxLength={13}
            inputMode="numeric"
          />
        </Field>
        {mode === 'change' ? (
          <Field label="Current PIN">
            <input
              type="password"
              inputMode="numeric"
              maxLength={12}
              value={oldPin}
              onChange={(e) => setOldPin(e.target.value.replace(/\D/g, ''))}
              required
            />
          </Field>
        ) : null}
        <Field label={mode === 'change' ? 'New PIN' : 'PIN'}>
          <input
            type="password"
            inputMode="numeric"
            maxLength={12}
            value={pin}
            onChange={(e) => setPin(e.target.value.replace(/\D/g, ''))}
            required
          />
        </Field>
        <Field label="Confirm PIN">
          <input
            type="password"
            inputMode="numeric"
            maxLength={12}
            value={confirmPin}
            onChange={(e) => setConfirmPin(e.target.value.replace(/\D/g, ''))}
            required
          />
        </Field>
        <p className="hint">PIN is AES-encrypted before sending to the API.</p>
        <PrimaryButton type="submit" loading={loading}>
          {mode === 'change'
            ? 'Change PIN'
            : mode === 'forgot'
              ? 'Reset PIN'
              : 'Set PIN'}
        </PrimaryButton>
      </form>
    </PageShell>
  );
}
