'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

const mockStats = [
  { label: 'Total Users', value: '12,847', change: '+12%', icon: '👥' },
  { label: 'Active Labs', value: '3,421', change: '+8%', icon: '🔬' },
  { label: 'Experiments', value: '45,892', change: '+23%', icon: '⚗' },
  { label: 'System Health', value: '99.9%', change: 'OK', icon: '💚' },
];

const mockUsers = [
  { name: 'Dr. Sarah Chen', email: 'sarah@lab.com', status: 'active', role: 'Researcher' },
  { name: 'Prof. James Wilson', email: 'james@lab.com', status: 'active', role: 'Admin' },
  { name: 'Dr. Emily Brown', email: 'emily@lab.com', status: 'inactive', role: 'Researcher' },
  { name: 'Alex Kumar', email: 'alex@lab.com', status: 'active', role: 'Student' },
];

const mockLabs = [
  { name: 'Organic Chemistry Lab', status: 'active', experiments: 234, users: 45 },
  { name: 'Physics Simulation Lab', status: 'active', experiments: 189, users: 32 },
  { name: 'Biochemistry Lab', status: 'maintenance', experiments: 156, users: 28 },
  { name: 'Materials Science Lab', status: 'active', experiments: 98, users: 19 },
];

export default function AdminPage() {
  const t = useTranslations('admin');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const [activePage, setActivePage] = useState('dashboard');
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="relative z-10 min-h-screen flex">
      {sidebarOpen && <div className="fixed inset-0 bg-black/50 z-40 lg:hidden" onClick={() => setSidebarOpen(false)} />}
      <aside className={`fixed lg:sticky top-0 left-0 h-screen w-[260px] bg-[var(--card)]/95 backdrop-blur-xl border-r border-[var(--border)] z-50 flex flex-col transition-transform duration-300 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}>
        <div className="p-4 flex items-center gap-3 border-b border-[var(--border)]"><div className="w-9 h-9 rounded-[10px] bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-sm font-bold animate-pulse-glow">⚗</div><div><div className="font-bold text-sm">{tn('brand')}</div><div className="text-[10px] text-[var(--muted-foreground)] font-mono uppercase">Admin Panel</div></div></div>
        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {[{ key: 'dashboard', icon: '📊', label: t('dashboard') }, { key: 'users', icon: '👥', label: t('users') }, { key: 'laboratories', icon: '🔬', label: t('laboratories') }, { key: 'chemicals', icon: '⚗', label: t('chemicals') }, { key: 'elements', icon: '⚛', label: t('elements') }, { key: 'equipment', icon: '🔧', label: t('equipment') }].map((item) => (
            <button key={item.key} onClick={() => setActivePage(item.key)} className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${activePage === item.key ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`}><span className="text-base">{item.icon}</span>{item.label}</button>
          ))}
        </nav>
        <div className="p-3 border-t border-[var(--border)]"><Link href={`/${locale}/dashboard`} className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all no-underline"><span className="text-base">🏠</span>User Dashboard</Link></div>
      </aside>
      <div className="flex-1 min-w-0">
        <header className="sticky top-0 z-30 px-4 py-3 md:px-6 bg-[var(--background)]/80 backdrop-blur-xl border-b border-[var(--border)]">
          <div className="flex items-center justify-between"><div className="flex items-center gap-3"><button onClick={() => setSidebarOpen(true)} className="lg:hidden w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--input)] text-[var(--muted-foreground)]">☰</button><h1 className="text-lg font-bold">{t('dashboard')}</h1></div><div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#14F195] flex items-center justify-center text-white text-xs font-bold">A</div></div>
        </header>
        <div className="p-4 md:p-6">
          {activePage === 'dashboard' && (<>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">{mockStats.map((stat, i) => (<div key={i} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-md)] p-5"><div className="flex items-center justify-between mb-3"><span className="text-2xl">{stat.icon}</span><span className={`text-xs font-medium px-2 py-0.5 rounded-full ${stat.change === 'OK' ? 'bg-[#14F195]/10 text-[#14F195]' : 'bg-[#8b5cf6]/10 text-[#8b5cf6]'}`}>{stat.change}</span></div><div className="text-2xl font-bold">{stat.value}</div><div className="text-xs text-[var(--muted-foreground)] mt-1">{stat.label}</div></div>))}</div>
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6"><h3 className="font-semibold mb-4">{t('recentUsers')}</h3><div className="space-y-3">{mockUsers.map((user, i) => (<div key={i} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.02] transition-all"><div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold flex-shrink-0">{user.name[0]}</div><div className="flex-1 min-w-0"><div className="text-sm font-medium truncate">{user.name}</div><div className="text-xs text-[var(--muted-foreground)]">{user.email}</div></div><span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${user.status === 'active' ? 'bg-[#14F195]/10 text-[#14F195]' : 'bg-[#F43F5E]/10 text-[#F43F5E]'}`}>{user.status}</span></div>))}</div></div>
              <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6"><h3 className="font-semibold mb-4">{t('recentLabs')}</h3><div className="space-y-3">{mockLabs.map((lab, i) => (<div key={i} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.02] transition-all"><div className="w-8 h-8 rounded-lg bg-[#8b5cf6]/10 border border-[#8b5cf6]/20 flex items-center justify-center text-sm flex-shrink-0">🔬</div><div className="flex-1 min-w-0"><div className="text-sm font-medium truncate">{lab.name}</div><div className="text-xs text-[var(--muted-foreground)]">{lab.experiments} experiments • {lab.users} users</div></div><span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${lab.status === 'active' ? 'bg-[#14F195]/10 text-[#14F195]' : 'bg-[#F59E0B]/10 text-[#F59E0B]'}`}>{lab.status}</span></div>))}</div></div>
            </div>
          </>)}
          {activePage === 'users' && (<div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6"><div className="flex items-center justify-between mb-6"><h2 className="text-xl font-bold">{t('users')}</h2><button className="py-2 px-4 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold">+ Add User</button></div><div className="overflow-x-auto"><table className="w-full text-sm"><thead><tr className="border-b border-[var(--border)]"><th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">User</th><th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Role</th><th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Status</th><th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Actions</th></tr></thead><tbody>{mockUsers.map((user, i) => (<tr key={i} className="border-b border-[var(--border)]/50 hover:bg-white/[0.02]"><td className="py-3 px-4"><div className="flex items-center gap-3"><div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold">{user.name[0]}</div><div><div className="font-medium">{user.name}</div><div className="text-xs text-[var(--muted-foreground)]">{user.email}</div></div></div></td><td className="py-3 px-4 text-[var(--muted-foreground)]">{user.role}</td><td className="py-3 px-4"><span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${user.status === 'active' ? 'bg-[#14F195]/10 text-[#14F195]' : 'bg-[#F43F5E]/10 text-[#F43F5E]'}`}>{user.status}</span></td><td className="py-3 px-4"><button className="text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors">⋮</button></td></tr>))}</tbody></table></div></div>)}
          {activePage === 'laboratories' && (<div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6"><div className="flex items-center justify-between mb-6"><h2 className="text-xl font-bold">{t('laboratories')}</h2><button className="py-2 px-4 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold">+ Add Laboratory</button></div><div className="grid grid-cols-1 md:grid-cols-2 gap-4">{mockLabs.map((lab, i) => (<div key={i} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-md)] p-5 hover:border-[#8b5cf6]/30 transition-all cursor-pointer"><div className="flex items-center justify-between mb-3"><div className="w-10 h-10 rounded-lg bg-[#8b5cf6]/10 border border-[#8b5cf6]/20 flex items-center justify-center text-lg">🔬</div><span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${lab.status === 'active' ? 'bg-[#14F195]/10 text-[#14F195]' : 'bg-[#F59E0B]/10 text-[#F59E0B]'}`}>{lab.status}</span></div><h3 className="font-semibold mb-1">{lab.name}</h3><div className="text-xs text-[var(--muted-foreground)]">{lab.experiments} experiments • {lab.users} users</div></div>))}</div></div>)}
          {(activePage === 'chemicals' || activePage === 'elements' || activePage === 'equipment') && (<div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6"><div className="flex items-center justify-between mb-6"><h2 className="text-xl font-bold">{t(activePage)}</h2><button className="py-2 px-4 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold">+ Add Item</button></div><div className="text-center py-16"><div className="text-5xl mb-4">📦</div><p className="text-[var(--muted-foreground)]">Coming soon. Management interface will be available here.</p></div></div>)}
        </div>
      </div>
    </div>
  );
}
