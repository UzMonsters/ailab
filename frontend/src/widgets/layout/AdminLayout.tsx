'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  Atom, LayoutDashboard, Users, FlaskConical, Settings,
  Bell, Search, Database, ChevronLeft, ChevronRight,
  TestTube2, Zap, Microscope, BookOpen, ShieldAlert,
  List, LogOut
} from 'lucide-react';
import { useAuthStore } from '@/stores/auth.store';
import { useTranslations } from 'next-intl';
import ToastProvider from '@/widgets/admin/ToastProvider';

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname() || '';
  const router = useRouter();
  
  let locale = pathname.split('/')[1];
  if (!['ru', 'en', 'uz'].includes(locale)) {
    locale = 'ru';
  }

  const [collapsed, setCollapsed] = useState(false);
  const { user, logout, fetchUser } = useAuthStore();
  const t = useTranslations('admin');

  useEffect(() => {
    // Admin is a frontend prototype in this phase. User hydration is best-effort
    // and must never block local CRUD screens when the backend is unavailable.
    void fetchUser();
  }, [fetchUser]);

  const handleLogout = async () => {
    await logout();
    router.push(`/${locale}/auth`);
  };

  const navGroups = [
    {
      title: null,
      links: [
        { href: `/${locale}/admin/dashboard`, label: 'Dashboard', icon: LayoutDashboard },
        { href: `/${locale}/admin/users`, label: 'Users', icon: Users },
        { href: `/${locale}/admin/laboratories`, label: 'Laboratories', icon: FlaskConical },
        { href: `/${locale}/admin/learning`, label: 'Learning Content', icon: BookOpen },
        { href: `/${locale}/admin/book`, label: 'Book Studio', icon: BookOpen },
      ]
    },
    {
      title: 'Science Catalog',
      links: [
        { href: `/${locale}/admin/science/chemistry`, label: 'Chemistry', icon: TestTube2 },
        { href: `/${locale}/admin/equipment`, label: 'Equipment', icon: Database },
        { href: `/${locale}/admin/materials`, label: 'Materials & Samples', icon: List },
      ]
    },
    {
      title: 'System',
      links: [
        { href: `/${locale}/admin/scenarios`, label: 'Scenarios', icon: BookOpen },
        { href: `/${locale}/admin/audit`, label: 'Audit Log', icon: List },
        { href: `/${locale}/admin/settings`, label: 'Settings', icon: Settings },
      ]
    }
  ];

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="admin-theme admin-layout">
      {/* Mobile Sidebar Overlay */}
      {mobileMenuOpen && (
        <div 
          className="fixed inset-0 bg-black/60 z-[45] md:hidden backdrop-blur-sm"
          onClick={() => setMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside className={`admin-sidebar ${collapsed ? 'collapsed' : ''} ${mobileMenuOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'} fixed md:relative h-full z-50 transition-transform duration-300`}>
        <div className="sidebar-header">
          <div className="w-8 h-8 bg-gradient-to-br from-[#8B5CF6] to-[#22D3EE] rounded-lg flex items-center justify-center shrink-0">
            <Atom size={16} className="text-white" />
          </div>
          {!collapsed && <span>jasScience Admin</span>}
        </div>
        
        <div className="sidebar-content">
          {navGroups.map((group, idx) => (
            <div key={idx}>
              {group.title && !collapsed && <div className="sidebar-section">{group.title}</div>}
              {group.title && collapsed && <div className="h-4"></div>}
              
              {group.links.map(link => {
                const isActive = pathname.startsWith(link.href);
                return (
                  <Link key={link.href} href={link.href} onClick={() => setMobileMenuOpen(false)} className={`sidebar-link ${isActive ? 'active' : ''}`} title={collapsed ? link.label : undefined}>
                    <link.icon size={18} className="shrink-0" />
                    {!collapsed && <span>{link.label}</span>}
                  </Link>
                );
              })}
            </div>
          ))}
        </div>
        
        <div className="p-3 border-t border-[rgba(255,255,255,0.07)] flex items-center justify-between">
          <button onClick={() => setCollapsed(!collapsed)} className="header-icon p-2 hidden md:block">
            {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
          </button>
          {!collapsed && (
            <button onClick={handleLogout} className="header-icon p-2 hover:text-[#ef4444]" title="Logout">
              <LogOut size={18} />
            </button>
          )}
        </div>
      </aside>

      {/* Main Content */}
      <main className="admin-main">
        <header className="admin-header">
          <div className="flex items-center gap-4">
            <button 
              className="md:hidden header-icon p-2 -ml-2"
              onClick={() => setMobileMenuOpen(true)}
            >
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M3 12h18M3 6h18M3 18h18"/></svg>
            </button>
            <div className="global-search hidden sm:flex">
              <Search size={16} color="var(--admin-secondary)" />
              <input type="text" placeholder="Search users, equipment, scenarios..." />
            </div>
          </div>
          
          <div className="header-actions">
            <span className="text-xs font-bold bg-[var(--admin-panel-2)] px-2 py-1 rounded text-[var(--admin-secondary)]">RU</span>
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
