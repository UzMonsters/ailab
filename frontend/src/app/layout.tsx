import type { Metadata } from 'next';
import { Inter, JetBrains_Mono } from 'next/font/google';
import './globals.css';
import { UIProvider } from '@/stores/ui.store';

const inter = Inter({ subsets: ['latin'], variable: '--font-inter' });
const jetbrains = JetBrains_Mono({ subsets: ['latin'], variable: '--font-jetbrains' });

export const metadata: Metadata = {
  title: 'jasScience — The Scientific OS',
  description: 'AI-powered virtual laboratory platform for scientific research and education.',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html className={`${inter.variable} ${jetbrains.variable}`}>
      <body><UIProvider>{children}</UIProvider></body>
    </html>
  );
}
