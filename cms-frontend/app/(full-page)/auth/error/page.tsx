'use client';
import { useRouter } from 'next/navigation';
import React from 'react';
import { Button } from 'primereact/button';

export default function ErrorPage() {
  const router = useRouter();

  return (
    <div className="surface-ground flex align-items-center justify-content-center min-h-screen min-w-screen overflow-hidden">
      <div className="flex flex-column align-items-center justify-content-center">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src="/layout/images/logo-mark-dark.png" alt="LasPay" className="mb-5 flex-shrink-0" style={{ height: '2.75rem', width: 'auto' }} />
        <div className="w-full surface-card py-8 px-5 sm:px-8 flex flex-column align-items-center border-round-xl shadow-2">
          <div
            className="flex justify-content-center align-items-center bg-pink-500 border-circle mb-3"
            style={{ height: '3.2rem', width: '3.2rem' }}
          >
            <i className="pi pi-fw pi-exclamation-circle text-2xl text-white" />
          </div>
          <h1 className="text-900 font-bold text-4xl mb-2">Error Occurred</h1>
          <p className="text-600 mb-5 text-center m-0">Something went wrong.</p>
          <Button icon="pi pi-arrow-left" label="Go to Dashboard" text onClick={() => router.push('/')} />
        </div>
      </div>
    </div>
  );
}
