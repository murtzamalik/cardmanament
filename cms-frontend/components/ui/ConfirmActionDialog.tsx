'use client';

import React from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';

export type ConfirmActionVariant = 'delete' | 'reject' | 'danger';

interface ConfirmActionDialogProps {
  visible: boolean;
  onHide: () => void;
  message: string;
  detail?: string;
  variant?: ConfirmActionVariant;
  acceptLabel?: string;
  onAccept: () => void;
  onCancel?: () => void;
  loading?: boolean;
}

export function ConfirmActionDialog({
  visible,
  onHide,
  message,
  detail,
  variant = 'delete',
  acceptLabel,
  onAccept,
  onCancel,
  loading = false,
}: ConfirmActionDialogProps) {
  const defaultLabels: Record<ConfirmActionVariant, string> = {
    delete: 'Delete',
    reject: 'Reject',
    danger: 'Confirm',
  };
  const label = acceptLabel ?? defaultLabels[variant];

  const handleCancel = () => {
    if (onCancel) onCancel();
    else onHide();
  };

  return (
    <Dialog
      visible={visible}
      onHide={handleCancel}
      header="Confirm"
      style={{ width: '28rem' }}
      modal
      closable={!loading}
      closeOnEscape={!loading}
      footer={
        <div className="flex justify-content-end gap-2">
          <Button label="Cancel" text onClick={handleCancel} disabled={loading} />
          <Button
            label={label}
            severity="danger"
            onClick={onAccept}
            loading={loading}
            disabled={loading}
          />
        </div>
      }
    >
      <p className="m-0 mb-2">{message}</p>
      {detail && <p className="m-0 text-color-secondary text-sm">{detail}</p>}
    </Dialog>
  );
}
