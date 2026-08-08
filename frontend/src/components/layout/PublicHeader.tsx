'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import Logo from '@/components/common/Logo';

const languages = [
  { code: 'en', label: 'EN' },
  { code: 'ru', label: 'RU' },
  { code: 'uz', label: 'UZ' },
];

export default function PublicHeader() {
  const t = useTranslations();
  const pathname = usePathname();
  const [mobileOpen, setMobileOpen] = useState(false);
  const currentLocale = pathname.split('/')[1] || 'en';

  const switchLocale = (locale: string) => {
    const segments = pathname.split('/');
    segments[1] = locale;
    return segments.join('/');
  };

  return (
    <header className="site-nav">
      <div className="nav-inner">
        <Link href={`/${currentLocale}`} className="logo">
          <span className="logo-symbol">⚗</span>
          <span>{t('common.brand')}</span>
        </Link>
        <nav className={mobileOpen ? 'nav-links nav-open' : 'nav-links'} aria-label="Main navigation">
          <Link href={`/${currentLocale}`}>{t('nav.home')}</Link>
          <Link href={`/${currentLocale}/about`}>{t('nav.about')}</Link>
          <Link href={`/${currentLocale}/terms`}>{t('nav.terms')}</Link>
        </nav>
        <div className="nav-actions">
          <div className="flex items-center gap-1 bg-[var(--input)] rounded-lg border border-[var(--border)] p-0.5">
            {languages.map((lang) => (
              <Link
                key={lang.code}
                href={switchLocale(lang.code)}
                className={`px-2 py-1 rounded text-xs font-medium transition-all ${
                  currentLocale === lang.code
                    ? 'bg-[#8b5cf6]/20 text-[#8b5cf6] border border-[#8b5cf6]/30'
                    : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'
                }`}
              >
                {lang.label}
              </Link>
            ))}
          </div>
          <Link href={`/${currentLocale}/auth`} className="text-button login-button">
            {t('common.login')}
          </Link>
          <Link href={`/${currentLocale}/auth`} className="button button-primary nav-cta">
            {t('common.register')}
          </Link>
          <button className="mobile-toggle icon-button" onClick={() => setMobileOpen(!mobileOpen)}>
            {mobileOpen ? '✕' : '☰'}
          </button>
        </div>
      </div>
    </header>
  );
}
