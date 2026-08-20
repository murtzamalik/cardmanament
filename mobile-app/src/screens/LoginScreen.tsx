import { useState } from 'react';
import type { FormEvent } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { login } from '../api/cms';
import { Banner, Field, PrimaryButton } from '../components/ui';
import { getSession, saveSession } from '../lib/session';
import { API_BASE_URL } from '../config/api';

export function LoginScreen() {
  const navigate = useNavigate();
  const existing = getSession();
  const [username, setUsername] = useState('cms');
  const [password, setPassword] = useState('cms');
  const [relationshipNum, setRelationshipNum] = useState(
    existing?.relationshipNum || '',
  );
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (existing?.token) {
    return <Navigate to="/home" replace />;
  }

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError('');
    if (!relationshipNum.trim() || relationshipNum.trim().length !== 13) {
      setError('Relationship number must be 13 digits.');
      return;
    }
    setLoading(true);
    try {
      const body = await login(username.trim(), password);
      saveSession({
        loginId: body.loginId,
        fullName: body.fullName,
        token: body.token,
        relationshipNum: relationshipNum.trim(),
      });
      navigate('/home', { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-screen">
      <div className="login-hero">
        <div className="login-orb" />
        <p className="eyebrow">Card Management</p>
        <h1>CMS</h1>
        <p className="login-tagline">
          Manage cards, limits, PIN and status in one place.
        </p>
      </div>

      <form className="login-card" onSubmit={onSubmit}>
        <h2>Sign in</h2>
        {error ? <Banner kind="error">{error}</Banner> : null}
        <Field label="Username">
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </Field>
        <Field label="Password">
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </Field>
        <Field label="Relationship number (13 digits)">
          <input
            inputMode="numeric"
            maxLength={13}
            value={relationshipNum}
            onChange={(e) =>
              setRelationshipNum(e.target.value.replace(/\D/g, '').slice(0, 13))
            }
            placeholder="0000000000000"
            required
          />
        </Field>
        <PrimaryButton type="submit" loading={loading}>
          Continue
        </PrimaryButton>
        <p className="hint">API: {API_BASE_URL}</p>
      </form>
    </div>
  );
}
