'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import Tabs from '@/components/ui/Tabs';

export default function ProfilePage() {
  const t = useTranslations('profile');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const [activeTab, setActiveTab] = useState('overview');

  return (
    <div className="relative z-10 min-h-screen">
      <div className="section-wrap py-6"><Link href={`/${locale}/dashboard`} className="text-sm text-[var(--muted-foreground)] hover:text-[var(--foreground)]">← Back to Dashboard</Link></div>
      <main className="mx-auto max-w-[1320px] px-4 py-6">
        <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-hidden mb-6 shadow-[0_20px_40px_rgba(0,0,0,0.4)]">
          <div className="h-40 bg-gradient-to-br from-[#8b5cf6]/25 via-[#14F195]/10 to-[#A855F7]/20 border-b border-[var(--border)] relative"><div className="absolute inset-0 opacity-15" style={{ backgroundImage: 'radial-gradient(#fff 1px, transparent 1px)', backgroundSize: '16px 16px' }} /></div>
          <div className="px-8 pb-7 -mt-15 relative flex flex-col md:flex-row items-start md:items-end gap-5 flex-wrap">
            <div className="w-[110px] h-[110px] rounded-[22px] border-4 border-[#0B0C14] bg-gradient-to-br from-[#1E1B4B] to-[#311042] flex items-center justify-center text-[40px] text-[#C084FC] shadow-[0_10px_25px_rgba(0,0,0,0.5),0_0_30px_rgba(139,92,246,0.35)] relative">J<div className="absolute bottom-1 right-1 w-[18px] h-[18px] bg-[#14F195] border-3 border-[#0B0C14] rounded-full shadow-[0_0_10px_#14F195]" /></div>
            <div className="flex-1 mb-1">
              <h1 className="text-[26px] font-bold flex items-center gap-2.5 tracking-tight">Jasur <span className="text-[#14F195] text-base">✓</span></h1>
              <p className="text-[var(--muted-foreground)] text-sm mt-0.5">Research Scientist · Chemistry Department</p>
              <div className="flex gap-2 mt-2.5 flex-wrap">{['Chemistry', 'AI Research', 'Lab Automation'].map((tag) => (<span key={tag} className="px-3 py-1 bg-[#8b5cf6]/12 border border-[#8b5cf6]/30 rounded-full text-[11px] font-mono text-[#C084FC]">{tag}</span>))}</div>
            </div>
            <button className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] hover:bg-white/[0.08] transition-all">{t('editProfile')}</button>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 px-8 pb-7">
            {[{ label: t('stats.gpu'), value: '4x A100', icon: '🖥', color: 'purple' }, { label: t('stats.simulations'), value: '1,247', icon: '🔬', color: 'teal' }, { label: t('stats.models'), value: '89', icon: '📊', color: 'amber' }, { label: t('stats.accuracy'), value: '97.3%', icon: '🎯', color: 'rose' }].map((stat, i) => (
              <div key={i} className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-md)] p-5 text-center hover:border-[#8b5cf6]/40 hover:-translate-y-[3px] hover:bg-[rgba(22,24,38,0.85)] transition-all"><div className="text-2xl mb-2">{stat.icon}</div><div className="text-xl font-bold text-[#8b5cf6]">{stat.value}</div><div className="text-xs text-[var(--muted-foreground)] mt-1">{stat.label}</div></div>
            ))}
          </div>
        </div>
        <Tabs tabs={[{ key: 'overview', label: t('overview') }, { key: 'simulations', label: t('activeSimulations') }, { key: 'research', label: t('research') }, { key: 'security', label: t('security') }]} activeTab={activeTab} onChange={setActiveTab} className="mb-6" />
        {activeTab === 'overview' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6">
              <h3 className="font-semibold mb-4">Recent Activity</h3>
              <div className="space-y-4">
                {[{ action: 'Created workspace', item: 'Acid Base Experiment', time: '2 hours ago', icon: '⚗' }, { action: 'Updated simulation', item: 'Distillation Setup', time: '5 hours ago', icon: '🧪' }, { action: 'Exported report', item: 'Organic Lab Results', time: '1 day ago', icon: '📊' }].map((a, i) => (
                  <div key={i} className="flex items-start gap-3 p-3 rounded-lg hover:bg-white/[0.02] transition-all"><div className="w-8 h-8 rounded-lg bg-[#8b5cf6]/10 border border-[#8b5cf6]/20 flex items-center justify-center text-sm flex-shrink-0">{a.icon}</div><div className="flex-1 min-w-0"><div className="text-sm">{a.action} <span className="text-[#8b5cf6] font-medium">{a.item}</span></div><div className="text-xs text-[var(--muted-foreground)] mt-1">{a.time}</div></div></div>
                ))}
              </div>
            </div>
            <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] p-6">
              <h3 className="font-semibold mb-4">Quick Actions</h3>
              <div className="space-y-2">
                <Link href={`/${locale}/dashboard`} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.03] transition-all no-underline text-[var(--foreground)]"><span className="text-lg">▦</span><span className="text-sm">My Workspaces</span></Link>
                <button className="w-full flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.03] transition-all text-left text-[var(--foreground)]"><span className="text-lg">⚙</span><span className="text-sm">{t('settings')}</span></button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
