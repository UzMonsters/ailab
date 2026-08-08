'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';

export default function NotFoundPage() {
  const t = useTranslations('notFound');

  return (
    <div className="relative z-10 min-h-screen flex items-center justify-center p-4">
      <div className="text-center max-w-md animate-fade-in-up">
        <div className="text-[120px] font-bold text-[#8b5cf6]/20 leading-none mb-4">404</div>
        <div className="text-5xl mb-6">🔍</div>
        <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-4">{t('title')}</h1>
        <p className="text-[var(--muted-foreground)] mb-8">{t('desc')}</p>
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link href="/en" className="button button-primary py-3 px-6">{t('backHome')}</Link>
          <button onClick={() => window.history.back()} className="button button-secondary py-3 px-6">{t('goBack')}</button>
        </div>
      </div>
    </div>
  );
}
