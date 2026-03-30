import { Metadata } from 'next';
import Layout from '../../layout/layout';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';
import { ErrorBoundary } from '@/components/ErrorBoundary';

interface AppLayoutProps {
    children: React.ReactNode;
}

export const metadata: Metadata = {
    title: 'Card Management System',
    description: 'Card Management System – Security and Housekeeping.',
    robots: { index: false, follow: false },
    viewport: { initialScale: 1, width: 'device-width' },
    icons: {
        icon: '/favicon.ico'
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
