'use client';

import React from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { ProgressSpinner } from 'primereact/progressspinner';

interface AppDialogProps {
  visible: boolean;
  onHide: () => void;
  title: string;
  subtitle?: string;
  children: React.ReactNode;
  footer?: React.ReactNode;
  primaryLabel?: string;
  secondaryLabel?: string;
  onPrimary?: () => void;
  onSecondary?: () => void;
  loading?: boolean;
  width?: string;
  primaryDisabled?: boolean;
}

export function AppDialog({
  visible,
  onHide,
  title,
  subtitle,
  children,
  footer,
  primaryLabel = 'Save',
  secondaryLabel = 'Cancel',
  onPrimary,
  onSecondary,
  loading = false,
  width = '32rem',
  primaryDisabled,
}: AppDialogProps) {
  const defaultFooter = (
    <>
      <Button
        label={secondaryLabel}
        text
        onClick={onSecondary ?? onHide}
        disabled={loading}
      />
      <Button
        label={primaryLabel}
        onClick={onPrimary}
        loading={loading}
        disabled={primaryDisabled}
      />
    </>
  );

  return (
    <Dialog
      visible={visible}
      onHide={onHide}
      header={
        <div>
          <div className="font-semibold" style={{ fontSize: '1.125rem' }}>{title}</div>
          {subtitle && (
            <div className="text-color-secondary mt-1" style={{ fontSize: '0.875rem', fontWeight: 400 }}>
              {subtitle}
            </div>
          )}
        </div>
      }
      style={{ width, maxWidth: '95vw' }}
      modal
      closable={!loading}
      closeOnEscape={!loading}
      footer={footer ?? (onPrimary ? defaultFooter : null)}
      className="app-dialog"
    >
      <div className="dialog-body position-relative" style={{ padding: '24px', minHeight: '60px' }}>
        {loading && (
          <div
            className="flex align-items-center justify-content-center border-round position-absolute w-full h-full"
            style={{
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              backgroundColor: 'var(--surface-overlay)',
              opacity: 0.9,
              zIndex: 10,
            }}
          >
            <ProgressSpinner style={{ width: '40px', height: '40px' }} />
          </div>
        )}
        {children}
      </div>
    </Dialog>
  );
}
