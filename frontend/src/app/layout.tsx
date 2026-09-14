import type { Metadata } from 'next';
import './globals.css';
import { UIProvider } from '@/stores/ui.store';
import { SvgDefs } from '@/entities/equipment/ui/renderers/SvgDefs';

export const metadata: Metadata = {
  title: 'jasScience — The Scientific OS',
  description: 'AI-powered virtual laboratory platform for scientific research and education.',
};

import { ThemeProvider } from '@/shared/ui/ThemeProvider';

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html suppressHydrationWarning>
      <body>
        <svg width="0" height="0" style={{ position: 'fixed', left: '-9999px', top: '-9999px', visibility: 'hidden' }} aria-hidden="true"><SvgDefs /></svg>
        <ThemeProvider>
          <UIProvider>{children}</UIProvider>
        </ThemeProvider>
      </body>
    </html>
  );
}
