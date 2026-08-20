'use client';
import React from 'react';
import Link from 'next/link';
import { Button } from 'primereact/button';

export default function NotFoundPage() {
  return (
    <div className="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
      <div className="flex flex-column align-items-center justify-content-center">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src="/layout/images/logo-mark-dark.png" alt="LasPay" className="mb-5 flex-shrink-0" style={{ height: '2.75rem', width: 'auto' }} />
        <div className="w-full surface-card py-8 px-5 sm:px-8 flex flex-column align-items-center border-round-xl shadow-2">
          <span className="text-blue-500 font-bold text-3xl">404</span>
          <h1 className="text-900 font-bold text-4xl mb-2">Not Found</h1>
          <p className="text-600 mb-5 text-center m-0">Requested resource is not available.</p>
          <Link href="/">
            <Button icon="pi pi-home" label="Go to Dashboard" />
          </Link>
        </div>
      </div>
    </div>
  );
}
