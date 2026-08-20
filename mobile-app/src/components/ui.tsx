import type { ReactNode } from 'react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { maskPan } from '../lib/session';

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
  const navigate = useNavigate();
  return (
    <header className="app-header">
      <div className="header-row">
        {back ? (
          <button type="button" className="icon-btn" onClick={() => navigate(-1)} aria-label="Back">
            ←
          </button>
        ) : (
          <div className="brand-mark">CMS</div>
        )}
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
  className,
}: {
  children: ReactNode;
  title: string;
  subtitle?: string;
  back?: boolean;
  action?: ReactNode;
  className?: string;
}) {
  return (
    <div className={['page', className].filter(Boolean).join(' ')}>
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

export function Field({
  label,
  children,
}: {
  label: string;
  children: ReactNode;
}) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
    </label>
  );
}

/**
 * Keeps full PAN digits in state for APIs, but UI shows first6******last4.
 */
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
        <input
          className="pan-masked-input"
          value={maskPan(digits)}
          readOnly
          aria-label="Masked PAN"
        />
        <button
          type="button"
          className="text-btn pan-change-btn"
          onClick={() => setEditing(true)}
        >
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
      {digits.length >= 10 ? (
        <span className="hint pan-preview">Shows as {maskPan(digits)}</span>
      ) : null}
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
