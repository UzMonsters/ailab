'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';
import { Atom } from 'lucide-react';

export default function NotFoundPage() {
  const pathname = usePathname();
  const t = useTranslations('notFound');
  const locale = pathname.split('/')[1] || 'en';

  return (
    <div className="relative min-h-screen flex items-center justify-center" style={{ backgroundColor: '#050508' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative z-10 text-center px-5">
        <div className="relative inline-block mb-8">
          <Atom size={120} className="text-[#8B5CF6]/30 animate-spin" style={{ animationDuration: '12s' }} />
          <div className="absolute inset-0 flex items-center justify-center">
            <span className="text-6xl font-bold text-[var(--foreground)]">404</span>
          </div>
        </div>

        <h1 className="text-2xl font-bold mb-3">{t('title')}</h1>
        <p className="text-[var(--muted-foreground)] text-sm max-w-md mx-auto mb-8">
          {t('desc')}
        </p>

        <div className="flex items-center justify-center gap-4">
          <Link href={`/${locale}`} className="px-6 py-3 border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] no-underline hover:bg-white/[0.05] transition-all">
            {t('backHome')}
          </Link>
          <Link href={`/${locale}/dashboard`} className="px-6 py-3 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold no-underline shadow-[0_8px_20px_rgba(139,92,246,.35)] hover:-translate-y-0.5 transition-all">
            {t('dashboard')}
          </Link>
        </div>
      </div>
    </div>
  );
}
