'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useSearchParams, useRouter } from 'next/navigation';
import { useTranslations } from 'next-intl';
import {
  Atom, LayoutDashboard, FlaskConical, Clock, Star, FileText, Trash2,
  Settings, HelpCircle, Bell, Plus, Menu, X, LogOut, User, ChevronLeft,
} from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';
import LanguageSwitcher from '@/components/common/LanguageSwitcher';
import ThemeToggle from '@/components/common/ThemeToggle';
import { useAuthStore } from '@/stores/auth.store';

interface SidebarItem {
  key: string;
  icon: typeof LayoutDashboard;
  label: string;
  href: string;
  exact?: boolean;
  badge?: string;
  disabled?: boolean;
}

export default function UserLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const tn = useTranslations('nav');
  const tc = useTranslations('common');
  const td = useTranslations('dashboard');
  const locale = pathname.split('/')[1] || 'en';
  const [drawerOpen, setDrawerOpen] = useState(false);
  const { user, logout } = useAuthStore();

  useEffect(() => {
    if (!drawerOpen) return;
    document.body.style.overflow = 'hidden';
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') setDrawerOpen(false); };
    document.addEventListener('keydown', onKey);
    return () => {
      document.body.style.overflow = '';
      document.removeEventListener('keydown', onKey);
    };
  }, [drawerOpen]);

  const handleLogout = async () => {
    await logout();
    router.push(`/${locale}/auth`);
  };

  const sections: { title: string; items: SidebarItem[] }[] = [
    {
      title: tn('home'),
      items: [
        { key: 'home', icon: LayoutDashboard, label: tn('home'), href: `/${locale}/dashboard`, exact: true },
        { key: 'recent', icon: Clock, label: tn('recent'), href: `/${locale}/dashboard?view=recent`, exact: true },
        { key: 'favorites', icon: Star, label: tn('favorites'), href: `/${locale}/dashboard?view=favorites`, exact: true },
      ],
    },
    {
      title: tn('sciences'),
      items: [
        { key: 'chemistry', icon: FlaskConical, label: td('chemistry'), href: `/${locale}/dashboard`, exact: true },
        { key: 'physics', icon: Atom, label: td('physics'), href: `/${locale}/dashboard`, badge: tc('soon'), disabled: true },
        { key: 'biology', icon: Atom, label: td('biology'), href: `/${locale}/dashboard`, badge: tc('soon'), disabled: true },
      ],
    },
    {
      title: '',
      items: [
        { key: 'templates', icon: FileText, label: tn('templates'), href: `/${locale}/dashboard`, exact: true },
        { key: 'trash', icon: Trash2, label: tn('trash'), href: `/${locale}/dashboard`, exact: true },
      ],
    },
  ];

  const displayName = user?.username || 'User';

  const SidebarContent = ({ onNavigate }: { onNavigate?: () => void }) => (
    <>
      <div className="p-4">
        <Link href={`/${locale}/dashboard`} className="flex items-center gap-2.5 no-underline text-[var(--foreground)] font-bold text-lg mb-4" onClick={onNavigate}>
          <div className="w-9 h-9 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] rounded-[10px] flex items-center justify-center shadow-[0_0_15px_rgba(139,92,246,0.35)]">
            <Atom size={16} className="text-white" />
          </div>
          <span>jas<span className="text-[#8B5CF6]">Core</span></span>
        </Link>
      </div>

      <nav className="flex-1 px-3 space-y-1 overflow-y-auto min-h-0">
        {sections.map((section) => (
          <div key={section.title || section.items[0].key}>
            {section.title && (
              <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider px-3 py-2 mt-3 first:mt-0">{section.title}</div>
            )}
            {section.items.map((item) => {
              const [itemPath, itemQuery = ''] = item.href.split('?');
              const pathMatches = item.exact ? pathname === itemPath : pathname.startsWith(itemPath);
              const queryMatches = itemQuery ? searchParams.toString() === itemQuery : !searchParams.toString();
              const isActive = pathMatches && queryMatches;              return (
                <Link
                  key={item.key}
                  href={item.href}
                  aria-disabled={item.disabled}
                  onClick={(e) => { if (item.disabled) e.preventDefault(); else onNavigate?.(); }}
                  className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-150 no-underline ${item.disabled ? 'opacity-50 cursor-not-allowed' : ''} ${isActive && !item.disabled ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20 border-l-2 border-l-[#8B5CF6]' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`}
                >
                  <item.icon size={16} className={isActive && !item.disabled ? 'text-[#8b5cf6]' : ''} />
                  <span className="flex-1">{item.label}</span>
                  {item.badge && <span className="text-[10px] font-mono uppercase bg-[#8B5CF6]/10 px-2 py-0.5 rounded">{item.badge}</span>}
                </Link>
              );
            })}
          </div>
        ))}
      </nav>

      <div className="p-3 border-t border-white/5 space-y-1 mt-auto">
        <Link href={`/${locale}/settings`} className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all no-underline ${pathname === `/${locale}/settings` ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`} onClick={onNavigate}>
          <Settings size={16} /> {tc('settings')}
        </Link>
        <button type="button" disabled className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)]/60 cursor-not-allowed bg-transparent border-0 text-left">
          <HelpCircle size={16} /> {tc('help')}
        </button>
        <button onClick={handleLogout} className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[#F43F5E] hover:bg-[#F43F5E]/5 transition-all">
          <LogOut size={16} /> {tc('logout')}
        </button>
        <Link href={`/${locale}/profile`} className="flex items-center gap-3 px-3 py-2.5 mt-2 no-underline text-[var(--foreground)] rounded-lg hover:bg-white/[0.03] transition-all" onClick={onNavigate}>
          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#14F195] flex items-center justify-center text-white text-xs font-bold">{displayName[0].toUpperCase()}</div>
          <div className="flex-1 min-w-0"><div className="text-sm font-medium truncate">{displayName}</div><div className="text-xs text-[var(--muted-foreground)]">{tc('viewProfile')}</div></div>
        </Link>
      </div>
    </>
  );

  return (
    <div className="relative min-h-screen flex" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      {/* Mobile drawer overlay */}
      {drawerOpen && (
        <div className="fixed inset-0 bg-black/60 z-40 lg:hidden" onClick={() => setDrawerOpen(false)} aria-hidden="true" />
      )}

      {/* Desktop sidebar — fixed, 100dvh */}
      <aside className="hidden lg:flex fixed top-0 left-0 z-50 h-dvh w-[230px] bg-[var(--card)]/95 backdrop-blur-xl border-r border-white/5 flex-col min-h-0">
        <SidebarContent />
      </aside>

      {/* Mobile drawer */}
      <div className={`lg:hidden fixed top-0 left-0 z-50 h-dvh w-[280px] max-w-[85vw] bg-[var(--card)]/98 backdrop-blur-xl border-r border-white/5 flex flex-col min-h-0 transition-transform duration-200 ${drawerOpen ? 'translate-x-0' : '-translate-x-full'}`}>
        <button
          onClick={() => setDrawerOpen(false)}
          aria-label="Close menu"
          className="absolute top-3 right-3 w-8 h-8 grid place-items-center rounded-lg border border-white/10 text-[var(--muted-foreground)]"
        >
          <X size={16} />
        </button>
        <SidebarContent onNavigate={() => setDrawerOpen(false)} />
      </div>

      <div className="flex-1 min-w-0 flex flex-col lg:pl-[230px]">
        <header className="sticky top-0 z-30 px-4 py-3 md:px-6 bg-[var(--background)]/80 backdrop-blur-xl border-b border-white/5">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <button onClick={() => setDrawerOpen(true)} className="lg:hidden w-9 h-9 flex items-center justify-center rounded-lg border border-white/10 bg-[#0A0B14] text-[var(--muted-foreground)]" aria-label="Open menu">
                <Menu size={18} />
              </button>
              <span className="lg:hidden font-bold text-lg">jas<span className="text-[#8B5CF6]">Core</span></span>
            </div>
            <div className="flex items-center gap-2">
              <ThemeToggle />
              <LanguageSwitcher />
              <button className="w-9 h-9 flex items-center justify-center rounded-lg border border-white/10 bg-[#0A0B14] text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all relative" aria-label="Notifications">
                <Bell size={16} />
                <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-[#14F195] rounded-full" />
              </button>
              <Link href={`/${locale}/profile`} className="w-9 h-9 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#14F195] flex items-center justify-center text-white text-xs font-bold no-underline" aria-label="Profile">{displayName[0].toUpperCase()}</Link>
            </div>
          </div>
        </header>

        <main className="flex-1 p-4 md:p-6 relative z-10 min-w-0">
          {children}
        </main>
      </div>
    </div>
  );
}
