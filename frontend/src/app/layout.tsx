import type { Metadata } from 'next';
import './globals.css';
import { UIProvider } from '@/stores/ui.store';

export const metadata: Metadata = {
  title: 'jasScience — The Scientific OS',
  description: 'AI-powered virtual laboratory platform for scientific research and education.',
};

import { EB_Garamond, Inter, Caveat } from 'next/font/google';

const bookSerif = EB_Garamond({ subsets: ['cyrillic', 'latin'], variable: '--font-book-serif', weight: ['400', '500', '600', '700'], display: 'swap' });
const techSans = Inter({ subsets: ['cyrillic', 'latin'], variable: '--font-tech-sans', display: 'swap' });
const handwritten = Caveat({ subsets: ['cyrillic', 'latin'], variable: '--font-handwritten', display: 'swap' });

import { ThemeProvider } from '@/shared/ui/ThemeProvider';

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html suppressHydrationWarning>
      <body className={`${bookSerif.variable} ${techSans.variable} ${handwritten.variable}`}>
        <ThemeProvider>
          <UIProvider>{children}</UIProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
