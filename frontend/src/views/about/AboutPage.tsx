'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';

export default function AboutPage() {
  const t = useTranslations('landing');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';

  return (
    <div className="relative z-10 min-h-screen">
      <div className="section-wrap py-6"><Link href={`/${locale}`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)]">← Back to Home</Link></div>
      <main className="mx-auto max-w-4xl px-4 py-16 md:py-24">
        <div className="text-center mb-16 animate-fade-in-up">
          <span className="badge mb-4 inline-flex">{t('platformBadge')}</span>
          <h1 className="text-4xl md:text-6xl font-bold tracking-tight mb-6">About AI Laboratory</h1>
          <p className="text-lg text-[var(--muted-foreground)] max-w-2xl mx-auto">{t('heroDesc')}</p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-16">
          {[{ icon: '🎯', title: t('platformTitle'), desc: t('platformDesc') }, { icon: '🔬', title: t('sandboxTitle'), desc: t('sandboxDesc') }, { icon: '🤖', title: t('aiTitle'), desc: t('aiDesc') }, { icon: '🌍', title: t('sciencesTitle'), desc: t('sciencesDesc') }].map((item, i) => (
            <div key={i} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-8"><div className="text-4xl mb-4">{item.icon}</div><h3 className="text-xl font-semibold mb-3">{item.title}</h3><p className="text-[var(--muted-foreground)] leading-relaxed">{item.desc}</p></div>
          ))}
        </div>
        <div className="text-center"><Link href={`/${locale}/auth`} className="button button-primary text-base py-3 px-8">{t('heroCta')}</Link></div>
      </main>
    </div>
  );
}
