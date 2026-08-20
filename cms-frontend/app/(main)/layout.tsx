import { Metadata } from 'next';
import Layout from '../../layout/layout';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
import { ErrorBoundary } from '@/components/ErrorBoundary';

interface AppLayoutProps {
    children: React.ReactNode;
}

export const metadata: Metadata = {
    title: 'LasPay CMS',
    description: 'LasPay Card Management System – Security and Housekeeping.',
    robots: { index: false, follow: false },
    viewport: { initialScale: 1, width: 'device-width' },
    icons: {
        icon: [
            { url: '/favicon.png', type: 'image/png' },
            { url: '/favicon.svg', type: 'image/svg+xml' }
        ]
    }
};

export default function AppLayout({ children }: AppLayoutProps) {
    return (
        <ProtectedRoute>
            <ErrorBoundary>
                <Layout>{children}</Layout>
            </ErrorBoundary>
        </ProtectedRoute>
    );
}
