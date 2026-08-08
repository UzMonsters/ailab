'use client';

import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';

export default function NotFoundPage() {
  const t = useTranslations('notFound');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';

  return (
    <div className="relative z-10 min-h-screen flex items-center justify-center p-4">
      <div className="text-center max-w-md animate-fade-in-up">
        <div className="text-[120px] font-bold text-purple/20 leading-none mb-4">404</div>
        <div className="text-5xl mb-6">🔍</div>
        <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-4">{t('title')}</h1>
        <p className="text-muted mb-8">{t('desc')}</p>
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link href={`/${locale}`} className="btn-primary py-3 px-6 no-underline">
            {t('backHome')}
          </Link>
          <button onClick={() => window.history.back()} className="btn-secondary py-3 px-6">
            {t('goBack')}
          </button>
        </div>
      </div>
    </div>
  );
}
