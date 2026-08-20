'use client';

import type { ReactNode } from 'react';
import { useState } from 'react';
import { maskPan } from '@/services/cms-app/session';
import { useLabNav } from './nav-context';

export function AppHeader({
  title,
  subtitle,
  back,
  action,
}: {
  title: string;
  subtitle?: string;
  back?: boolean;
  action?: ReactNode;
}) {
  const { go } = useLabNav();
  return (
    <header className="app-header">
      <div className="header-row">
        {back ? (
          <button type="button" className="icon-btn" onClick={() => go('home')} aria-label="Back">
            ←
          </button>
        ) : null}
        <div className="header-titles">
          <h1>{title}</h1>
          {subtitle ? <p>{subtitle}</p> : null}
        </div>
        {action ?? <div className="header-spacer" />}
      </div>
    </header>
  );
}

export function PageShell({
  children,
  title,
  subtitle,
  back,
  action,
}: {
  children: ReactNode;
  title: string;
  subtitle?: string;
  back?: boolean;
  action?: ReactNode;
}) {
  return (
    <div className="page">
      <AppHeader title={title} subtitle={subtitle} back={back} action={action} />
      <main className="page-body">{children}</main>
    </div>
  );
}

export function Banner({
  kind,
  children,
}: {
  kind: 'info' | 'success' | 'error' | 'warn';
  children: ReactNode;
}) {
  return <div className={`banner banner-${kind}`}>{children}</div>;
}

export function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
    </label>
  );
}

export function PanField({
  value,
  onChange,
  required,
}: {
  value: string;
  onChange: (fullDigits: string) => void;
  required?: boolean;
}) {
  const digits = value.replace(/\D/g, '');
  const [editing, setEditing] = useState(digits.length < 10);

  if (!editing && digits.length >= 10) {
    return (
      <div className="field pan-field">
        <span>PAN</span>
        <input className="pan-masked-input" value={maskPan(digits)} readOnly aria-label="Masked PAN" />
        <button type="button" className="text-btn pan-change-btn" onClick={() => setEditing(true)}>
          Enter different PAN
        </button>
      </div>
    );
  }

  return (
    <label className="field pan-field">
      <span>PAN</span>
      <input
        value={digits}
        onChange={(e) => onChange(e.target.value.replace(/\D/g, ''))}
        onBlur={() => {
          if (digits.length >= 10) setEditing(false);
        }}
        required={required}
        placeholder="Enter full card number"
        inputMode="numeric"
        autoComplete="off"
      />
      {digits.length >= 10 ? <span className="hint pan-preview">Shows as {maskPan(digits)}</span> : null}
    </label>
  );
}

export function PrimaryButton({
  children,
  loading,
  disabled,
  className,
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { loading?: boolean }) {
  return (
    <button
      {...props}
      className={['btn btn-primary', className].filter(Boolean).join(' ')}
      disabled={Boolean(loading || disabled)}
    >
      {loading ? 'Please wait…' : children}
    </button>
  );
}

export function SecondaryButton({
  children,
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  return (
    <button className="btn btn-secondary" {...props}>
      {children}
    </button>
  );
}

const NAV_ITEMS: Array<{ to: 'home' | 'request' | 'status' | 'pin' | 'limits'; label: string; icon: string }> = [
  { to: 'home', label: 'Home', icon: '⌂' },
  { to: 'request', label: 'Request', icon: '+' },
  { to: 'status', label: 'Status', icon: '◎' },
  { to: 'pin', label: 'PIN', icon: '✱' },
  { to: 'limits', label: 'Limits', icon: '▭' },
];

export function AppNav() {
  const { screen, go } = useLabNav();
  return (
    <nav className="app-nav" aria-label="Main">
      {NAV_ITEMS.map((item) => (
        <button
          key={item.to}
          type="button"
          className={`nav-item${screen === item.to ? ' active' : ''}`}
          onClick={() => go(item.to)}
        >
          <span className="nav-icon">{item.icon}</span>
          <span>{item.label}</span>
        </button>
      ))}
    </nav>
  );
}
