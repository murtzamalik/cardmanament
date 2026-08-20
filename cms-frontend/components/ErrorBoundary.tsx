'use client';

import React, { Component, ErrorInfo, ReactNode } from 'react';
import { Button } from 'primereact/button';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary caught:', error, errorInfo);
  }

  render() {
    if (this.state.hasError && this.state.error) {
      if (this.props.fallback) return this.props.fallback;
      return (
        <div className="flex align-items-center justify-content-center min-h-screen surface-ground p-4">
          <div className="surface-card border-round-lg shadow-2 p-6 text-center" style={{ maxWidth: '28rem' }}>
            <i className="pi pi-exclamation-triangle text-6xl text-red-500 mb-4" />
            <h2 className="text-xl font-semibold mb-2">Something went wrong</h2>
            <p className="text-600 mb-4 line-height-3">{this.state.error.message}</p>
            <Button label="Reload page" icon="pi pi-refresh" onClick={() => window.location.reload()} />
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}
