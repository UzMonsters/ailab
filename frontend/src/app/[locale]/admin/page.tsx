'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Users, FlaskConical, Activity, Heart, ShieldCheck, AlertTriangle, Loader2 } from 'lucide-react';
import { adminApi } from '@/services/api/admin.api';
import type { AdminUserResponse } from '@/types';
import { useTranslations } from 'next-intl';
import { normalizeError } from '@/lib/errors';

export default function AdminDashboardPage() {
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const t = useTranslations('admin');
  const [users, setUsers] = useState<AdminUserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const data = await adminApi.getUsers();
        setUsers(data);
      } catch (err: unknown) {
        setError(normalizeError(err, t('loadFailed')).message);
      } finally {
        setLoading(false);
      }
    };
    load();
    }, [t]);

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 size={24} className="animate-spin text-[#8B5CF6]" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center py-20">
        <AlertTriangle size={40} className="text-[#F43F5E]/50 mb-4" />
        <p className="text-sm text-[var(--muted-foreground)]">{error}</p>
      </div>
    );
  }

  const activeUsers = users.filter(u => u.role !== 'ROLE_BANNED').length;
  const admins = users.filter(u => u.role === 'ROLE_ADMIN').length;

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">{t('dashboard')}</h1>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {[
          { label: t('totalUsers'), value: users.length, change: `${activeUsers} ${t('active')}`, icon: Users, color: 'purple' },
          { label: t('administrators'), value: admins, change: t('staff'), icon: ShieldCheck, color: 'teal' },
          { label: t('systemStatus'), value: t('online'), change: t('healthy'), icon: Activity, color: 'amber' },
          { label: t('apiEndpoints'), value: '60', change: t('allExposed'), icon: Heart, color: 'rose' },
        ].map((stat, i) => {
          const colorMap: Record<string, string> = { purple: '#8B5CF6', teal: '#14F195', amber: '#F59E0B', rose: '#F43F5E' };
          const Icon = stat.icon;
          return (
            <div key={i} className="border border-white/5 bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl rounded-[var(--radius-md)] p-5">
              <div className="flex items-center justify-between mb-3">
                <Icon size={20} style={{ color: colorMap[stat.color] }} />
                <span className="text-xs font-medium px-2 py-0.5 rounded-full bg-[#14F195]/10 text-[#14F195]">{stat.change}</span>
              </div>
              <div className="text-2xl font-bold">{stat.value}</div>
              <div className="text-xs text-[var(--muted-foreground)] mt-1">{stat.label}</div>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="border border-white/5 bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl rounded-[var(--radius-lg)] p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold">{t('recentUsers')}</h3>
            <Link href={`/${locale}/admin/users`} className="text-xs text-[#8B5CF6] hover:underline no-underline">{t('viewAll')}</Link>
          </div>
          <div className="space-y-3">
            {users.slice(0, 5).map((user) => (
              <div key={user.id} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.02]">
                <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold flex-shrink-0">{user.username[0].toUpperCase()}</div>
                <div className="flex-1 min-w-0"><div className="text-sm font-medium truncate">{user.username}</div><div className="text-xs text-[var(--muted-foreground)]">{user.email}</div></div>
                <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${user.role === 'ROLE_ADMIN' ? 'bg-[#8b5cf6]/10 text-[#C084FC]' : 'bg-[#14F195]/10 text-[#14F195]'}`}>{user.role === 'ROLE_ADMIN' ? t('admin') : t('user')}</span>
              </div>
            ))}
            {users.length === 0 && <p className="text-sm text-[var(--muted-foreground)] py-4 text-center">{t('noUsers')}</p>}
          </div>
        </div>

        <div className="border border-white/5 bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl rounded-[var(--radius-lg)] p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold">{t('quickLinks')}</h3>
          </div>
          <div className="space-y-3">
            {[
              { icon: Users, label: t('manageUsers'), href: `/${locale}/admin/users`, desc: t('manageUsersDesc') },
              { icon: FlaskConical, label: t('laboratories'), href: `/${locale}/admin/laboratories`, desc: t('labsDesc') },
              { icon: Activity, label: t('chemistryData'), href: `/${locale}/admin/chemicals`, desc: t('chemistryDataDesc') },
              { icon: ShieldCheck, label: t('systemSettings'), href: `/${locale}/admin`, desc: t('systemSettingsDesc') },
            ].map((item, i) => {
              const Icon = item.icon;
              return (
                <Link key={i} href={item.href} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.02] no-underline text-[var(--foreground)]">
                  <div className="w-9 h-9 rounded-lg bg-[#8B5CF6]/10 border border-[#8B5CF6]/20 flex items-center justify-center flex-shrink-0"><Icon size={16} className="text-[#8B5CF6]" /></div>
                  <div><div className="text-sm font-medium">{item.label}</div><div className="text-xs text-[var(--muted-foreground)]">{item.desc}</div></div>
                </Link>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
