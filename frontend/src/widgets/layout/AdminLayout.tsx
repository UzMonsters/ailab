'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  Atom, LayoutDashboard, FlaskConical, Settings,
  Bell, Database, ChevronLeft, ChevronRight,
  TestTube2, Zap, Microscope, BookOpen,
  List, LogOut, Users
} from 'lucide-react';
import { useAuthStore } from '@/stores/auth.store';
import ToastProvider from '@/widgets/admin/ToastProvider';
import AdminGlobalSearch from '@/widgets/admin/AdminGlobalSearch';
import { useTranslations } from 'next-intl';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const t = useTranslations('admin.navigation');
  const pathname = usePathname() || '';
  const router = useRouter();
  
  const locale = pathname.split('/')[1] || 'en';

  const isBookEditor = pathname.includes('/admin/book');
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout, fetchUser } = useAuthStore();

  useEffect(() => {
    fetchUser().then(() => {
      const currentUser = useAuthStore.getState().user;
      if (!currentUser) {
        router.replace(`/${locale}/auth`);
      }
    });
  }, [fetchUser, router, locale]);

  if (!user) {
    return null;
  }

  const sidebarCollapsed = isBookEditor || collapsed;

  const handleLogout = async () => {
    await logout();
    router.push(`/${locale}/auth`);
  };

  const navGroups = [
    {
      title: null,
      links: [
        { href: `/${locale}/admin/dashboard`, label: t('dashboard'), icon: LayoutDashboard },
      ]
    },
    {
      title: t('learningGroup'),
      links: [
        { href: `/${locale}/admin/learning`, label: t('learningContent'), icon: BookOpen },
        { href: `/${locale}/admin/learning/levels`, label: t('levels'), icon: Zap },
        { href: `/${locale}/admin/scenarios`, label: t('scenarios'), icon: Microscope },
      ]
    },
    {
      title: t('scienceCatalogGroup'),
      links: [
        { href: `/${locale}/admin/science/chemistry`, label: t('chemistry'), icon: TestTube2 },
        { href: `/${locale}/admin/equipment`, label: t('equipment'), icon: Database },
        { href: `/${locale}/admin/materials`, label: t('materials'), icon: FlaskConical },
      ]
    },
    {
      title: t('contentGroup'),
      links: [
        { href: `/${locale}/admin/book`, label: t('bookStudio'), icon: BookOpen },
      ]
    },
    {
      title: 'Collaboration',
      links: [
        { href: `/${locale}/admin/sharing`, label: 'Collaboration', icon: Users },
      ]
    },
    {
      title: t('systemGroup'),
      links: [
        { href: `/${locale}/admin/audit`, label: t('auditLog'), icon: List },
        { href: `/${locale}/admin/settings`, label: t('settings'), icon: Settings },
      ]
    }
  ];
  const allLinks = navGroups.flatMap(group => group.links);
  const activeHref = allLinks.filter(link => pathname === link.href || pathname.startsWith(`${link.href}/`)).sort((a, b) => b.href.length - a.href.length)[0]?.href;

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="admin-theme admin-layout">
      {mobileMenuOpen && (
        <div 
          className="fixed inset-0 bg-black/60 z-[45] md:hidden backdrop-blur-sm"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}

      <aside className={`admin-sidebar ${sidebarCollapsed ? 'collapsed' : ''} ${mobileMenuOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'} fixed md:relative h-full z-50 transition-transform duration-300`}>
        <div className="sidebar-header">
          <div className="w-8 h-8 bg-gradient-to-br from-[#8B5CF6] to-[#22D3EE] rounded-lg flex items-center justify-center shrink-0">
            <Atom size={16} className="text-white" />
          </div>
          {!sidebarCollapsed && <span>jasScience Admin</span>}
        </div>
        
        <div className="sidebar-content">
          {navGroups.map((group, idx) => (
            <div key={idx}>
              {group.title && !sidebarCollapsed && <div className="sidebar-section">{group.title}</div>}
              {group.title && sidebarCollapsed && <div className="h-4"></div>}
              
              {group.links.map(link => {
                const isActive = activeHref === link.href;
                return (
                  <Link key={link.href} href={link.href} onClick={() => setMobileMenuOpen(false)} className={`sidebar-link ${isActive ? 'active' : ''}`} title={sidebarCollapsed ? link.label : undefined}>
                    <link.icon size={18} className="shrink-0" />
                    {!sidebarCollapsed && <span>{link.label}</span>}
                  </Link>
                );
              })}
            </div>
          ))}
        </div>
        
        <div className="p-3 border-t border-[rgba(255,255,255,0.07)] flex items-center justify-between">
          <button onClick={() => setCollapsed(!sidebarCollapsed)} className="header-icon p-2 hidden md:block">
            {sidebarCollapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
          </button>
          {!sidebarCollapsed && (
            <button onClick={handleLogout} className="header-icon p-2 hover:text-[#ef4444]" title={t('logout')} aria-label={t('logout')}>
              <LogOut size={18} />
            </button>
          )}
        </div>
      </aside>

      <main className="admin-main">
        <header className="admin-header">
          <div className="flex items-center gap-4">
            <button 
              className="md:hidden header-icon p-2 -ml-2"
              onClick={() => setMobileMenuOpen(true)}
            >
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M3 12h18M3 6h18M3 18h18"/></svg>
            </button>
            <AdminGlobalSearch locale={locale}/>
          </div>
          
          <div className="header-actions">
            <Bell size={18} className="header-icon" />
            <div className="w-8 h-8 rounded-full bg-[#8b5cf6] flex items-center justify-center font-bold text-xs text-white">
              {user?.username?.charAt(0).toUpperCase() || 'A'}
            </div>
          </div>
        </header>
        
        <div className="admin-content">
          {children}
        </div>
        <ToastProvider />
      </main>
    </div>
  );
}
