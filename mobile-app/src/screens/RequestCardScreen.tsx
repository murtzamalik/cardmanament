import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { getLovs, lovMap, newCardRequest } from '../api/cms';
import {
  Banner,
  Field,
  PageShell,
  PrimaryButton,
} from '../components/ui';
import { getSession } from '../lib/session';

export function RequestCardScreen() {
  const session = getSession();
  const [products, setProducts] = useState<Record<string, string>>({});
  const [types, setTypes] = useState<Record<string, string>>({});
  const [requestTypes, setRequestTypes] = useState<Record<string, string>>({});
  const [cardTitle, setCardTitle] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [relationshipNumber, setRelationshipNumber] = useState(
    session?.relationshipNum || '',
  );
  const [productCode, setProductCode] = useState('');
  const [cardType, setCardType] = useState('');
  const [requestTypeId, setRequestTypeId] = useState('1');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);
  const [lovLoading, setLovLoading] = useState(true);

  useEffect(() => {
    void (async () => {
      try {
        const lovs = await getLovs();
        const p = lovMap(lovs, 'card-product');
        const t = lovMap(lovs, 'card-types');
        const r = lovMap(lovs, 'card-request-types');
        setProducts(p);
        setTypes(t);
        setRequestTypes(Object.keys(r).length ? r : { '1': 'NEW' });
        setProductCode(Object.keys(p)[0] || '');
        setCardType(Object.keys(t)[0] || '');
        setRequestTypeId(Object.keys(r)[0] || '1');
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load lists');
        setRequestTypes({ '1': 'NEW' });
      } finally {
        setLovLoading(false);
      }
    })();
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (accountNumber.length !== 10) {
      setError('Account number must be 10 digits.');
      return;
    }
    if (relationshipNumber.length !== 13) {
      setError('Relationship number must be 13 digits.');
      return;
    }
    setLoading(true);
    try {
      await newCardRequest({
        cardTitle: cardTitle.trim(),
        accountNumber,
        productCode,
        cardType,
        relationshipNumber,
        requestTypeId,
      });
      setSuccess('Card request submitted. It will show as in process until issued.');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Request failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <PageShell title="Request card" subtitle="New card application" back>
      {lovLoading ? <Banner kind="info">Loading product lists…</Banner> : null}
      {error ? <Banner kind="error">{error}</Banner> : null}
      {success ? <Banner kind="success">{success}</Banner> : null}

      <form className="panel form-stack" onSubmit={onSubmit}>
        <Field label="Card title">
          <input
            value={cardTitle}
            onChange={(e) => setCardTitle(e.target.value)}
            required
            placeholder="Cardholder name"
          />
        </Field>
        <Field label="Account number (10)">
          <input
            inputMode="numeric"
            maxLength={10}
            value={accountNumber}
            onChange={(e) =>
              setAccountNumber(e.target.value.replace(/\D/g, '').slice(0, 10))
            }
            required
          />
        </Field>
        <Field label="Relationship number (13)">
          <input
            inputMode="numeric"
            maxLength={13}
            value={relationshipNumber}
            onChange={(e) =>
              setRelationshipNumber(
                e.target.value.replace(/\D/g, '').slice(0, 13),
              )
            }
            required
          />
        </Field>
        <Field label="Product">
          <select
            value={productCode}
            onChange={(e) => setProductCode(e.target.value)}
            required
          >
            {Object.entries(products).map(([code, name]) => (
              <option key={code} value={code}>
                {name} ({code})
              </option>
            ))}
          </select>
        </Field>
        <Field label="Card type">
          <select
            value={cardType}
            onChange={(e) => setCardType(e.target.value)}
            required
          >
            {Object.entries(types).map(([code, name]) => (
              <option key={code} value={code}>
                {name} ({code})
              </option>
            ))}
          </select>
        </Field>
        <Field label="Request type">
          <select
            value={requestTypeId}
            onChange={(e) => setRequestTypeId(e.target.value)}
            required
          >
            {Object.entries(requestTypes).map(([code, name]) => (
              <option key={code} value={code}>
                {name} ({code})
              </option>
            ))}
          </select>
        </Field>
        <PrimaryButton type="submit" loading={loading}>
          Submit request
        </PrimaryButton>
      </form>
    </PageShell>
  );
}
