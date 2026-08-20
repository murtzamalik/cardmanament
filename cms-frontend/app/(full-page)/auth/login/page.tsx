/* eslint-disable @next/next/no-img-element */
'use client';

import React, { useEffect, useState } from 'react';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Checkbox } from 'primereact/checkbox';
import { useAuth } from '@/services/auth/AuthContext';
import styles from './login.module.scss';

const REMEMBER_KEY = 'cms.rememberedLoginId';
const LOGO_MARK = '/layout/images/logo-mark-dark.png';

const LoginPage = () => {
  const { login, error, clearError } = useAuth();

  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [rememberLoginId, setRememberLoginId] = useState(false);
  const [loading, setLoading] = useState(false);
  const [touched, setTouched] = useState<{ loginId?: boolean; password?: boolean }>({});

  const loginIdInvalid = touched.loginId && loginId.trim().length === 0;
  const passwordInvalid = touched.password && password.length === 0;

  useEffect(() => {
    const remembered = window.localStorage.getItem(REMEMBER_KEY);
    if (remembered) {
      setLoginId(remembered);
      setRememberLoginId(true);
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setTouched({ loginId: true, password: true });

    if (!loginId.trim() || !password) return;

    clearError();
    setLoading(true);
    try {
      await login(loginId, password);
      if (rememberLoginId) {
        window.localStorage.setItem(REMEMBER_KEY, loginId);
      } else {
        window.localStorage.removeItem(REMEMBER_KEY);
      }
    } catch {
      // error shown via context
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.brandPanel}>
        <div className={styles.brandPattern} aria-hidden="true" />
        <img src={LOGO_MARK} alt="LasPay" className={styles.brandLogoCorner} />

        <div className={styles.brandContent}>
          <img src={LOGO_MARK} alt="" className={styles.brandMark} aria-hidden="true" />
          <p className={styles.wordmark}>
            <span className={styles.wordmarkLas}>Las</span>
            <span className={styles.wordmarkPay}>Pay</span>
          </p>
          <h1 className={styles.headline}>Card Management System</h1>
          <p className={styles.subline}>Secure access for card operations and administration</p>
        </div>

        <div className={styles.brandFooter}>Authorized personnel only</div>
      </div>

      <div className={styles.formPanel}>
        <div className={styles.formCard}>
          <div className={styles.formHeader}>
            <div className={styles.mobileBrand}>
              <img src={LOGO_MARK} alt="LasPay" className={styles.mobileLogo} />
              <span className={styles.mobileWordmark}>
                <strong>Las</strong>Pay
              </span>
            </div>
            <h2 className={styles.formTitle}>Sign in</h2>
            <div className={styles.secureCue}>
              <i className="pi pi-lock" aria-hidden="true" />
              <span>Encrypted session</span>
            </div>
          </div>

          <form onSubmit={handleSubmit} noValidate className={styles.form}>
            <div className={styles.field}>
              <label htmlFor="loginId" className={styles.label}>
                Login ID
              </label>
              <InputText
                id="loginId"
                value={loginId}
                onChange={(e) => setLoginId(e.target.value)}
                onBlur={() => setTouched((t) => ({ ...t, loginId: true }))}
                className={loginIdInvalid ? 'p-invalid' : ''}
                aria-describedby={loginIdInvalid ? 'loginId-error' : undefined}
                autoComplete="username"
                autoFocus
                required
              />
              {loginIdInvalid && (
                <small id="loginId-error" className={styles.fieldError}>
                  Login ID is required
                </small>
              )}
            </div>

            <div className={styles.field}>
              <label htmlFor="password" className={styles.label}>
                Password
              </label>
              <Password
                inputId="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                onBlur={() => setTouched((t) => ({ ...t, password: true }))}
                toggleMask
                feedback={false}
                className="w-full"
                inputClassName={passwordInvalid ? 'p-invalid w-full' : 'w-full'}
                aria-describedby={passwordInvalid ? 'password-error' : undefined}
                autoComplete="current-password"
                required
              />
              {passwordInvalid && (
                <small id="password-error" className={styles.fieldError}>
                  Password is required
                </small>
              )}
            </div>

            <div className={styles.rememberRow}>
              <Checkbox
                inputId="rememberLoginId"
                checked={rememberLoginId}
                onChange={(e) => setRememberLoginId(!!e.checked)}
              />
              <label htmlFor="rememberLoginId" className={styles.rememberLabel}>
                Remember Login ID
              </label>
            </div>

            {error && (
              <div className={styles.errorBanner} role="alert">
                <i className="pi pi-exclamation-triangle" aria-hidden="true" />
                <span>{error}</span>
              </div>
            )}

            <Button
              type="submit"
              label="Sign in"
              loading={loading}
              disabled={loading}
              className={styles.submitButton}
            />
          </form>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
