import type { Metadata } from 'next';
import type { ReactNode } from 'react';

export const metadata: Metadata = {
  title: 'CMS App lab',
  robots: { index: false, follow: false },
};

export default function CmsAppLabLayout({ children }: { children: ReactNode }) {
  return <>{children}</>;
}
