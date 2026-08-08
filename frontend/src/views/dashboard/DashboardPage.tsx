'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import Modal from '@/components/ui/Modal';

const mockWorkspaces = [
  { id: 1, name: 'Acid Base Experiment', image: '⚗', recent: true },
  { id: 2, name: 'Distillation Setup', image: '🧪', recent: true },
  { id: 3, name: 'My Lab', image: '🔬', recent: true },
  { id: 4, name: 'Workspace 04', image: '⚗', recent: false },
  { id: 5, name: 'Organic Lab', image: '🧬', recent: false },
  { id: 6, name: 'Experiment 07', image: '🧪', recent: false },
];

export default function DashboardPage() {
  const t = useTranslations('dashboard');
  const tn = useTranslations('common');
  const tnNav = useTranslations('nav');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState('home');
  const [hoveredCard, setHoveredCard] = useState<number | null>(null);
  const recentWorkspaces = mockWorkspaces.filter((w) => w.recent);

  return (
    <div className="relative z-10 min-h-screen flex">
      {sidebarOpen && <div className="fixed inset-0 bg-black/50 z-40 lg:hidden" onClick={() => setSidebarOpen(false)} />}

      <aside className={`fixed lg:sticky top-0 left-0 h-screen w-[260px] bg-[var(--card)]/95 backdrop-blur-xl border-r border-[var(--border)] z-50 flex flex-col transition-transform duration-300 ${sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}`}>
        <div className="p-4"><button onClick={() => setCreateModalOpen(true)} className="w-full py-3 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_10px_25px_rgba(139,92,246,0.4)] hover:-translate-y-0.5 transition-all">+ {t('createWorkspace')}</button></div>
        <nav className="flex-1 px-3 space-y-1 overflow-y-auto">
          <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider px-3 py-2">Home</div>
          {[{ key: 'home', icon: '⌂', label: tnNav('home') }, { key: 'workspaces', icon: '▦', label: tnNav('myWorkspaces') }, { key: 'recent', icon: '◷', label: tnNav('recent') }, { key: 'favorites', icon: '☆', label: tnNav('favorites') }].map((item) => (
            <button key={item.key} onClick={() => setActiveTab(item.key)} className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all ${activeTab === item.key ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`}>
              <span className="text-base">{item.icon}</span>{item.label}
            </button>
          ))}
          <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider px-3 py-2 mt-4">Sciences</div>
          <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all"><span className="text-base">⚗</span>{t('chemistry')}</button>
          <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] cursor-not-allowed opacity-50" disabled><span className="text-base">⚛</span>{t('physics')}<span className="ml-auto text-[10px] font-mono uppercase">{tn('soon')}</span></button>
          <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] cursor-not-allowed opacity-50" disabled><span className="text-base">🧬</span>{t('biology')}<span className="ml-auto text-[10px] font-mono uppercase">{tn('soon')}</span></button>
          <div className="mt-4 space-y-1">
            <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all"><span className="text-base">📋</span>{tnNav('templates')}</button>
            <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all"><span className="text-base">🗑</span>{tnNav('trash')}</button>
          </div>
        </nav>
        <div className="p-3 border-t border-[var(--border)] space-y-1">
          <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all"><span className="text-base">⚙</span>{tn('settings')}</button>
          <button className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03] transition-all"><span className="text-base">?</span>{tn('help')}</button>
          <Link href={`/${locale}/profile`} className="flex items-center gap-3 px-3 py-2.5 mt-2 no-underline text-[var(--foreground)]"><div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#14F195] flex items-center justify-center text-white text-xs font-bold">J</div><div className="flex-1 min-w-0"><div className="text-sm font-medium truncate">Jasur</div><div className="text-xs text-[var(--muted-foreground)]">{tn('viewProfile')}</div></div></Link>
        </div>
      </aside>

      <div className="flex-1 min-w-0">
        <header className="sticky top-0 z-30 px-4 py-3 md:px-6 bg-[var(--background)]/80 backdrop-blur-xl border-b border-[var(--border)]">
          <div className="flex items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <button onClick={() => setSidebarOpen(true)} className="lg:hidden w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--input)] text-[var(--muted-foreground)]">☰</button>
              <Link href={`/${locale}`} className="flex items-center gap-2 no-underline text-[var(--foreground)]"><div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold animate-pulse-glow">⚗</div><span className="font-bold text-sm hidden sm:block">{tn('brand')}</span></Link>
            </div>
            <div className="flex items-center gap-2">
              <button className="w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--input)] text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:border-white/20 transition-all relative">🔔<span className="absolute top-1.5 right-1.5 w-2 h-2 bg-[#14F195] rounded-full" /></button>
              <Link href={`/${locale}/profile`} className="w-9 h-9 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#14F195] flex items-center justify-center text-white text-xs font-bold no-underline">J</Link>
            </div>
          </div>
        </header>

        <div className="p-4 md:p-6">
          <div className="flex items-center justify-between mb-6"><h1 className="text-2xl font-bold">{t('myWorkspaces')}</h1></div>
          <div className="mb-8">
            <h2 className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider mb-4">{t('recent')}</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {recentWorkspaces.map((ws) => (
                <div key={ws.id} className="group relative border border-[var(--border)] bg-[#090f18] rounded-[var(--radius-lg)] overflow-hidden cursor-pointer hover:border-[#8b5cf6]/30 transition-all duration-300" onMouseEnter={() => setHoveredCard(ws.id)} onMouseLeave={() => setHoveredCard(null)}>
                  <div className="aspect-[16/10] bg-gradient-to-br from-[#8b5cf6]/10 via-[#A855F7]/5 to-[#14F195]/5 flex items-center justify-center relative"><span className="text-5xl">{ws.image}</span>
                    {hoveredCard === ws.id && <div className="absolute inset-0 bg-black/40 flex items-center justify-center animate-fade-in-up"><span className="text-sm font-medium text-white">{t('openWorkspace')}</span></div>}
                    <button className="absolute top-2 right-2 w-7 h-7 rounded-md bg-black/30 backdrop-blur-sm flex items-center justify-center text-white/70 hover:text-white opacity-0 group-hover:opacity-100 transition-all">⋮</button>
                  </div>
                  <div className="p-3"><div className="text-sm font-medium truncate">{ws.name}</div></div>
                </div>
              ))}
            </div>
          </div>
          <div>
            <h2 className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider mb-4">{t('allWorkspaces')}</h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {mockWorkspaces.map((ws) => (
                <div key={ws.id + 100} className="group relative border border-[var(--border)] bg-[#090f18] rounded-[var(--radius-lg)] overflow-hidden cursor-pointer hover:border-[#8b5cf6]/30 transition-all duration-300" onMouseEnter={() => setHoveredCard(ws.id + 100)} onMouseLeave={() => setHoveredCard(null)}>
                  <div className="aspect-[16/10] bg-gradient-to-br from-[#8b5cf6]/10 via-[#A855F7]/5 to-[#14F195]/5 flex items-center justify-center relative"><span className="text-5xl">{ws.image}</span>
                    {hoveredCard === ws.id + 100 && <div className="absolute inset-0 bg-black/40 flex items-center justify-center animate-fade-in-up"><span className="text-sm font-medium text-white">{t('openWorkspace')}</span></div>}
                    <button className="absolute top-2 right-2 w-7 h-7 rounded-md bg-black/30 backdrop-blur-sm flex items-center justify-center text-white/70 hover:text-white opacity-0 group-hover:opacity-100 transition-all">⋮</button>
                  </div>
                  <div className="p-3"><div className="text-sm font-medium truncate">{ws.name}</div></div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      <Modal open={createModalOpen} onClose={() => setCreateModalOpen(false)} title={t('createWorkspaceTitle')}>
        <div className="space-y-5">
          <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('workspaceName')}</label><input type="text" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder={t('untitledWorkspace')} /></div>
          <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('science')}</label>
            <div className="space-y-2">
              <button className="w-full flex items-center gap-3 p-3 rounded-lg border border-[#8b5cf6]/30 bg-[#8b5cf6]/10 text-left"><span className="text-xl">⚗</span><span className="text-sm font-medium">{t('chemistry')}</span><span className="ml-auto w-2 h-2 rounded-full bg-[#14F195]" /></button>
              <button className="w-full flex items-center gap-3 p-3 rounded-lg border border-[var(--border)] bg-[var(--input)] text-left opacity-50 cursor-not-allowed" disabled><span className="text-xl">⚛</span><span className="text-sm font-medium text-[var(--muted-foreground)]">{t('physics')}</span><span className="ml-auto text-[10px] font-mono text-[var(--muted-foreground)] uppercase">{tn('soon')}</span></button>
              <button className="w-full flex items-center gap-3 p-3 rounded-lg border border-[var(--border)] bg-[var(--input)] text-left opacity-50 cursor-not-allowed" disabled><span className="text-xl">🧬</span><span className="text-sm font-medium text-[var(--muted-foreground)]">{t('biology')}</span><span className="ml-auto text-[10px] font-mono text-[var(--muted-foreground)] uppercase">{tn('soon')}</span></button>
            </div>
          </div>
        </div>
        <div className="flex items-center justify-end gap-3 mt-8">
          <button onClick={() => setCreateModalOpen(false)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] hover:bg-white/[0.08] transition-all">{tn('cancel')}</button>
          <button className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_10px_25px_rgba(139,92,246,0.4)] hover:-translate-y-0.5 transition-all">{t('createWorkspaceButton')}</button>
        </div>
      </Modal>
    </div>
  );
}
