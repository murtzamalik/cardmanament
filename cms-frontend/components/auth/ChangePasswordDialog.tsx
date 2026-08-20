'use client';

import React, { useState } from 'react';
import { Password } from 'primereact/password';
import { AppDialog, FormField } from '@/components/ui';
import { AuthService } from '@/services/auth/AuthService';
import { PASSWORD_RULE_HINT, validatePasswordStrength } from '@/lib/passwordRules';

const authService = new AuthService();

interface ChangePasswordDialogProps {
  visible: boolean;
  onHide: () => void;
  onSuccess?: (message: string) => void;
  onError?: (message: string) => void;
}

export function ChangePasswordDialog({
  visible,
  onHide,
  onSuccess,
  onError,
}: ChangePasswordDialogProps) {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loading, setLoading] = useState(false);

  const reset = () => {
    setCurrentPassword('');
    setNewPassword('');
    setConfirmPassword('');
    setErrors({});
    setLoading(false);
  };

  const handleHide = () => {
    if (loading) return;
    reset();
    onHide();
  };

  const validate = (): boolean => {
    const next: Record<string, string> = {};
    if (!currentPassword) {
      next.currentPassword = 'Current password is required';
    }
    const strengthError = validatePasswordStrength(newPassword);
    if (strengthError) {
      next.newPassword = strengthError;
    }
    if (!confirmPassword) {
      next.confirmPassword = 'Confirm password is required';
    } else if (newPassword !== confirmPassword) {
      next.confirmPassword = 'Passwords do not match';
    }
    if (currentPassword && newPassword && currentPassword === newPassword) {
      next.newPassword = 'New password must be different from current password';
    }
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async () => {
    if (!validate()) return;
    setLoading(true);
    try {
      await authService.changePassword(currentPassword, newPassword);
      reset();
      onHide();
      onSuccess?.('Password changed successfully');
    } catch (e) {
      onError?.(e instanceof Error ? e.message : 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppDialog
      visible={visible}
      onHide={handleHide}
      title="Change password"
      subtitle={PASSWORD_RULE_HINT}
      primaryLabel="Update password"
      onPrimary={handleSubmit}
      loading={loading}
      width="28rem"
    >
      <FormField
        label="Current password"
        htmlFor="currentPassword"
        required
        error={errors.currentPassword}
      >
        <Password
          inputId="currentPassword"
          value={currentPassword}
          onChange={(e) => {
            setCurrentPassword(e.target.value);
            if (errors.currentPassword) {
              setErrors((prev) => {
                const next = { ...prev };
                delete next.currentPassword;
                return next;
              });
            }
          }}
          feedback={false}
          toggleMask
          className="w-full"
          inputClassName={`w-full ${errors.currentPassword ? 'p-invalid' : ''}`}
          autoComplete="current-password"
        />
      </FormField>

      <FormField
        label="New password"
        htmlFor="newPassword"
        required
        error={errors.newPassword}
        helperText={!errors.newPassword ? PASSWORD_RULE_HINT : undefined}
      >
        <Password
          inputId="newPassword"
          value={newPassword}
          onChange={(e) => {
            setNewPassword(e.target.value);
            if (errors.newPassword) {
              setErrors((prev) => {
                const next = { ...prev };
                delete next.newPassword;
                return next;
              });
            }
          }}
          toggleMask
          className="w-full"
          inputClassName={`w-full ${errors.newPassword ? 'p-invalid' : ''}`}
          autoComplete="new-password"
        />
      </FormField>

      <FormField
        label="Confirm new password"
        htmlFor="confirmPassword"
        required
        error={errors.confirmPassword}
      >
        <Password
          inputId="confirmPassword"
          value={confirmPassword}
          onChange={(e) => {
            setConfirmPassword(e.target.value);
            if (errors.confirmPassword) {
              setErrors((prev) => {
                const next = { ...prev };
                delete next.confirmPassword;
                return next;
              });
            }
          }}
          feedback={false}
          toggleMask
          className="w-full"
          inputClassName={`w-full ${errors.confirmPassword ? 'p-invalid' : ''}`}
          autoComplete="new-password"
        />
      </FormField>
    </AppDialog>
  );
}
