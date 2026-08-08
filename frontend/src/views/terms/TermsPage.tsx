'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';

export default function TermsPage() {
  const t = useTranslations('terms');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const sections = ['acceptance', 'use', 'accounts', 'content', 'privacy', 'termination', 'disclaimer', 'liability', 'changes', 'contact'] as const;

  return (
    <div className="relative z-10 min-h-screen">
      <div className="section-wrap py-6"><Link href={`/${locale}`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)]">← Back to Home</Link></div>
      <main className="mx-auto max-w-4xl px-4 py-16 md:py-24">
        <div className="mb-12 animate-fade-in-up">
          <h1 className="text-4xl md:text-5xl font-bold tracking-tight mb-4">{t('title')}</h1>
          <p className="text-sm text-[var(--muted-foreground)]">{t('lastUpdated')}</p>
        </div>
        <div className="space-y-8">
          {sections.map((section) => (
            <div key={section} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6 md:p-8">
              <h2 className="text-xl font-semibold mb-3">{t(section)}</h2>
              <p className="text-[var(--muted-foreground)] leading-relaxed">{t(`${section}Text` as any)}</p>
            </div>
          ))}
        </div>
        <div className="mt-12 text-center"><Link href={`/${locale}`} className="button button-secondary text-sm py-2 px-6">← Back</Link></div>
      </main>
    </div>
  );
}
