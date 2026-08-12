'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { Atom, Menu, X } from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';
import LanguageSwitcher from '@/components/common/LanguageSwitcher';
import ThemeToggle from '@/components/common/ThemeToggle';
import PublicFooter from '@/components/layout/PublicFooter';

export default function PublicLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const tn = useTranslations('nav');
  const tc = useTranslations('common');
  const locale = pathname.split('/')[1] || 'en';
  const [menuOpen, setMenuOpen] = useState(false);
  const isAuthRoute = pathname.endsWith('/auth');

  if (isAuthRoute) {
    return <>{children}</>;
  }

  return (
    <div className="relative min-h-screen" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <header className="relative z-20 border-b border-[var(--border)] bg-[var(--background)]/80 backdrop-blur-xl">
        <div className="max-w-[1380px] mx-auto px-5 h-16 flex items-center justify-between">
          <Link href={`/${locale}`} className="flex items-center gap-2.5 no-underline text-[var(--foreground)] font-bold text-lg">
            <div className="w-9 h-9 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] rounded-[10px] flex items-center justify-center shadow-[0_0_15px_rgba(139,92,246,0.35)]">
              <Atom size={16} className="text-white" />
            </div>
            <span>jas<span className="text-[#8B5CF6]">Science</span></span>
          </Link>

          <nav className={`lg:flex items-center gap-8 ${menuOpen ? 'flex flex-col absolute top-16 left-0 right-0 bg-[var(--background)]/95 border-b border-[var(--border)] p-6 space-y-3' : 'hidden lg:flex'}`}>
            <Link href={`/${locale}`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors no-underline">{tn('home')}</Link>
            <Link href={`/${locale}/about`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors no-underline">{tn('about')}</Link>
            <Link href={`/${locale}/terms`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors no-underline">{tn('terms')}</Link>
          </nav>

          <div className="flex items-center gap-3">
            <ThemeToggle />
            <LanguageSwitcher />
            <Link href={`/${locale}/auth`} className="bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white py-2 px-5 rounded-[var(--radius-sm)] text-sm font-semibold no-underline shadow-[0_5px_15px_rgba(139,92,246,0.3)] transition-all hover:-translate-y-0.5">
              {tc('login')}
            </Link>
            <button onClick={() => setMenuOpen(!menuOpen)} className="lg:hidden w-9 h-9 flex items-center justify-center rounded-lg border border-white/10 text-[var(--muted-foreground)]">
              {menuOpen ? <X size={18} /> : <Menu size={18} />}
            </button>
          </div>
        </div>
      </header>

      <main className="relative z-10">{children}</main>

      <PublicFooter />
    </div>
  );
}
