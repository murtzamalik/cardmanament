'use client';

import React from 'react';

interface FormSectionProps {
  title: string;
  description?: string;
  children: React.ReactNode;
  className?: string;
}

export function FormSection({ title, description, children, className = '' }: FormSectionProps) {
  return (
    <div className={`form-section ${className}`} style={{ padding: '24px 0', borderBottom: '1px solid var(--surface-border)' }}>
      <div className="mb-3">
        <h3 className="form-section-title m-0" style={{ fontSize: '0.95rem', fontWeight: 600, color: 'var(--text-color)' }}>
          {title}
        </h3>
        {description && (
          <p className="form-section-desc mt-1 mb-0 text-color-secondary" style={{ fontSize: '0.8125rem' }}>
            {description}
          </p>
        )}
      </div>
      <div className="flex flex-col gap-3" style={{ gap: '16px' }}>
        {children}
      </div>
    </div>
  );
}
