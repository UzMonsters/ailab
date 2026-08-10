'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  Atom, LayoutDashboard, Users, FlaskConical, TestTube, Settings,
  Bell, Menu, X, LogOut, Database,
} from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';
import { useAuthStore } from '@/stores/auth.store';
import { authApi } from '@/services/api/auth.api';
import { useTranslations } from 'next-intl';
import { Loader2 } from 'lucide-react';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const locale = pathname.split('/')[1] || 'en';
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, logout, fetchUser } = useAuthStore();
  const [checkingAccess, setCheckingAccess] = useState(true);
  const t = useTranslations('admin');

  useEffect(() => {
    let active = true;
    const checkAccess = async () => {
      try {
        await authApi.refresh();
      } catch {
        // An existing in-memory access token can still be valid.
      }
      await fetchUser();
      if (!active) return;
      const currentUser = useAuthStore.getState().user;
      if (currentUser?.role !== 'ROLE_ADMIN') {
        router.replace(currentUser ? `/${locale}/dashboard` : `/${locale}/auth`);
        return;
      }
      setCheckingAccess(false);
    };
    void checkAccess();
    return () => { active = false; };
  }, [fetchUser, locale, router]);

  const handleLogout = async () => {
    await logout();
    router.push(`/${locale}/auth`);
  };

  const sidebarLinks = [
    { key: 'dashboard', icon: LayoutDashboard, label: t('dashboard'), href: `/${locale}/admin` },
    { key: 'users', icon: Users, label: t('users'), href: `/${locale}/admin/users` },
    { key: 'laboratories', icon: FlaskConical, label: t('laboratories'), href: `/${locale}/admin/laboratories` },
    { key: 'chemicals', icon: TestTube, label: t('chemistryData'), href: `/${locale}/admin/chemicals` },
    { key: 'settings', icon: Settings, label: t('settings'), href: `/${locale}/admin` },
  ];

  const displayName = user?.username || t('admin');

  if (checkingAccess || user?.role !== 'ROLE_ADMIN') {
    return <div className="grid min-h-screen place-items-center bg-[var(--background)]"><Loader2 className="animate-spin text-[var(--primary)]" aria-label="Checking access" /></div>;
  }

  return (
    <div className="relative min-h-screen flex" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      {sidebarOpen && <div className="fixed inset-0 bg-black/60 z-40 lg:hidden" onClick={() => setSidebarOpen(false)} />}

      <aside className={`fixed lg:sticky top-0 left-0 h-screen w-[260px] bg-[#0A0B14]/95 backdrop-blur-xl border-r border-white/5 z-50 flex flex-col transition-transform duration-300 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}>
        <div className="p-4">
          <Link href={`/${locale}/admin`} className="flex items-center gap-2.5 no-underline text-[var(--foreground)] font-bold text-lg">
            <div className="w-9 h-9 bg-gradient-to-br from-[#8B5CF6] to-[#F43F5E] rounded-[10px] flex items-center justify-center shadow-[0_0_15px_rgba(139,92,246,0.35)]">
              <Atom size={16} className="text-white" />
            </div>
            <span>AI<span className="text-[#8B5CF6]">Lab</span></span>
          </Link>
          <div className="mt-3 px-2 py-1.5 bg-[#8B5CF6]/10 border border-[#8B5CF6]/20 rounded-lg text-xs text-[#8B5CF6] font-mono inline-block">{t('panel').toUpperCase()}</div>
        </div>

        <nav className="flex-1 px-3 space-y-1 overflow-y-auto mt-4">
          {sidebarLinks.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.key}
                href={item.href}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all no-underline ${isActive ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`}
                onClick={() => setSidebarOpen(false)}
              >
                <item.icon size={16} />{item.label}
              </Link>
            );
          })}
        </nav>

        <div className="p-3 border-t border-white/5">
          <button onClick={handleLogout} className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[#F43F5E] hover:bg-[#F43F5E]/5 transition-all">
            <LogOut size={16} />{t('logout')}
          </button>
        </div>
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <header className="sticky top-0 z-30 px-4 py-3 md:px-6 bg-[var(--background)]/80 backdrop-blur-xl border-b border-[var(--border)]">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <button onClick={() => setSidebarOpen(true)} className="lg:hidden w-9 h-9 flex items-center justify-center rounded-lg border border-white/10 bg-[#0A0B14] text-[var(--muted-foreground)]">
                <Menu size={18} />
              </button>
              <h1 className="text-lg font-bold">{t('title')}</h1>
            </div>
            <div className="flex items-center gap-2">
              <button className="w-9 h-9 flex items-center justify-center rounded-lg border border-white/10 bg-[#0A0B14] text-[var(--muted-foreground)] transition-all relative">
                <Bell size={16} />
                <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-[#14F195] rounded-full" />
              </button>
              <div className="w-9 h-9 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#F43F5E] flex items-center justify-center text-white text-xs font-bold">{displayName[0].toUpperCase()}</div>
            </div>
          </div>
        </header>

        <main className="flex-1 p-4 md:p-6 relative z-10">
          {children}
        </main>
      </div>
    </div>
  );
}
