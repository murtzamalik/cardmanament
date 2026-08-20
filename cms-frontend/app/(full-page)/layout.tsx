import { Metadata } from 'next';
import React from 'react';

interface SimpleLayoutProps {
    children: React.ReactNode;
}

export const metadata: Metadata = {
    title: 'LasPay — Sign in',
    description: 'LasPay Card Management System',
    icons: {
        icon: [
            { url: '/favicon.png', type: 'image/png' },
            { url: '/favicon.svg', type: 'image/svg+xml' }
        ]
    }
};

export default function SimpleLayout({ children }: SimpleLayoutProps) {
    return <React.Fragment>{children}</React.Fragment>;
}
