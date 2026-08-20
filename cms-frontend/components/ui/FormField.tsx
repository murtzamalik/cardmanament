'use client';

import React from 'react';

interface FormFieldProps {
  label: string;
  htmlFor?: string;
  required?: boolean;
  helperText?: string;
  error?: string;
  disabled?: boolean;
  children: React.ReactNode;
  className?: string;
}

export function FormField({
  label,
  htmlFor,
  required,
  helperText,
  error,
  disabled,
  children,
  className = '',
}: FormFieldProps) {
  return (
    <div className={`field-wrapper ${className}`} style={{ marginBottom: error ? 0 : undefined }}>
      <label
        htmlFor={htmlFor}
        className="block font-medium mb-1"
        style={{
          fontSize: '0.875rem',
          color: disabled ? 'var(--text-color-secondary)' : 'var(--text-color)',
        }}
      >
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <div className="field-control">{children}</div>
      {helperText && !error && (
        <small className="block mt-1 text-color-secondary" style={{ fontSize: '0.8125rem' }}>
          {helperText}
        </small>
      )}
      {error && (
        <small className="block mt-1 text-red-500" style={{ fontSize: '0.8125rem' }}>
          {error}
        </small>
      )}
    </div>
  );
}
