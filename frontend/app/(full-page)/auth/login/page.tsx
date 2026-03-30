/* eslint-disable @next/next/no-img-element */
'use client';

import React, { useContext, useState } from 'react';
import { Button } from 'primereact/button';
import { Password } from 'primereact/password';
import { InputText } from 'primereact/inputtext';
import { classNames } from 'primereact/utils';
import { LayoutContext } from '../../../../layout/context/layoutcontext';
import { FormField } from '@/components/ui';
import { useAuth } from '@/services/auth/AuthContext';

const LoginPage = () => {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { layoutConfig } = useContext(LayoutContext);
  const { login, error, clearError } = useAuth();

  const containerClassName = classNames(
    'surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden',
    { 'p-input-filled': layoutConfig.inputStyle === 'filled' }
  );

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    setLoading(true);
    try {
      await login(loginId, password);
    } catch {
      // error shown via context
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={containerClassName}>
      <div className="flex flex-column align-items-center justify-content-center">
        <img
          src={`/layout/images/logo-${layoutConfig.colorScheme === 'light' ? 'dark' : 'white'}.svg`}
          alt="Logo"
          className="mb-5 w-6rem flex-shrink-0"
        />
        <div
          style={{
            borderRadius: '56px',
            padding: '0.3rem',
            background: 'linear-gradient(180deg, var(--primary-color) 10%, rgba(33, 150, 243, 0) 30%)',
          }}
        >
          <div className="w-full surface-card py-8 px-5 sm:px-8" style={{ borderRadius: '53px' }}>
            <div className="text-center mb-5">
              <h1 className="m-0 mb-2" style={{ fontSize: '1.25rem', fontWeight: 600 }}>
                Welcome
              </h1>
              <p className="m-0 text-color-secondary" style={{ fontSize: '0.875rem' }}>
                Sign in to continue
              </p>
            </div>

            <form onSubmit={handleSubmit} className="flex flex-column" style={{ gap: '16px' }}>
              <FormField label="Login ID" htmlFor="loginId" required>
                <InputText
                  id="loginId"
                  type="text"
                  placeholder="Login ID"
                  value={loginId}
                  onChange={(e) => setLoginId(e.target.value)}
                  className="w-full md:w-30rem"
                  style={{ padding: '0.75rem 1rem' }}
                  required
                  autoComplete="username"
                />
              </FormField>

              <FormField label="Password" htmlFor="password1" required>
                <Password
                  inputId="password1"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                  toggleMask
                  className="w-full md:w-30rem"
                  inputClassName="w-full"
                  inputStyle={{ padding: '0.75rem 1rem' }}
                  feedback={false}
                  required
                  autoComplete="current-password"
                />
              </FormField>

              <Button
                type="submit"
                label="Sign in"
                className="w-full"
                style={{ padding: '0.75rem 1rem', marginTop: '8px' }}
                loading={loading}
                disabled={loading}
              />

              {error && (
                <p className="m-0 text-red-500" style={{ fontSize: '0.875rem' }}>
                  {error}
                </p>
              )}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
