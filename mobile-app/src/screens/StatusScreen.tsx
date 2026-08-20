import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { getLovs, lovMap, updateStatus } from '../api/cms';
import { Banner, Field, PageShell, PrimaryButton, PanField } from '../components/ui';
import { getCardContext } from '../lib/session';

export function StatusScreen() {
  const cardCtx = getCardContext();
  const [statuses, setStatuses] = useState<Record<string, string>>({});
  const [pan, setPan] = useState(cardCtx?.pan || '');
  const [statusCode, setStatusCode] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    void (async () => {
      try {
        const lovs = await getLovs();
        const s = lovMap(lovs, 'card-status');
        setStatuses(s);
        setStatusCode(Object.keys(s)[0] || '');
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load statuses');
      }
    })();
  }, []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      await updateStatus(pan.trim(), statusCode);
      setSuccess('Card status updated successfully.');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Update failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <PageShell title="Change status" subtitle="Freeze, block or activate" back>
      {error ? <Banner kind="error">{error}</Banner> : null}
      {success ? <Banner kind="success">{success}</Banner> : null}
      <form className="panel form-stack" onSubmit={onSubmit}>
        <PanField value={pan} onChange={setPan} required />
        <Field label="New status">
          <select
            value={statusCode}
            onChange={(e) => setStatusCode(e.target.value)}
            required
          >
            {Object.entries(statuses).map(([code, name]) => (
              <option key={code} value={code}>
                {name} ({code})
              </option>
            ))}
          </select>
        </Field>
        <p className="hint">HOT cards (status 003) cannot be changed via API.</p>
        <PrimaryButton type="submit" loading={loading}>
          Update status
        </PrimaryButton>
      </form>
    </PageShell>
  );
}
