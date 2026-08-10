import type { Metadata } from 'next';
import './globals.css';
import { UIProvider } from '@/stores/ui.store';

export const metadata: Metadata = {
  title: 'jasScience — The Scientific OS',
  description: 'AI-powered virtual laboratory platform for scientific research and education.',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html>
      <body><UIProvider>{children}</UIProvider></body>
    </html>
  );
}
