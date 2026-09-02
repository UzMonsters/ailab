'use client';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';
import { Atom } from 'lucide-react';

export default function TermsPage() {
  const pathname = usePathname();
  const t = useTranslations('terms');
  const locale = pathname.split('/')[1] || 'en';

  return (
    <div className="relative min-h-screen" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative z-10 max-w-[860px] mx-auto px-5 py-20">
        <div className="text-center mb-12">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-[#8b5cf6]/10 border border-[#8b5cf6]/30 rounded-full text-xs font-mono text-[#C084FC] mb-6 tracking-wider uppercase">
            <Atom size={12} />{t('badge')}
          </div>
          <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-2">{t('title')}</h1>
          <p className="text-xs text-[var(--muted-foreground)]">{t('lastUpdated')}</p>
        </div>

        <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-white/5 rounded-[24px] p-8 md:p-10 space-y-8 text-sm leading-relaxed">
          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s1Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s1Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s2Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s2Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s3Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s3Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s4Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s4Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s5Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s5Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s6Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s6Text')}</p>
          </section>

          <section>
            <h2 className="text-lg font-semibold mb-3">{t('s7Title')}</h2>
            <p className="text-[var(--muted-foreground)]">{t('s7Text')}</p>
          </section>
        </div>
      </div>
    </div>
  );
}
