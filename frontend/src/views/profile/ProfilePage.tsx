'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useEffect, useState, useCallback } from 'react';
import {
  Beaker, Cpu, BarChart3, Activity, FlaskConical, Grid3X3, Clock,
  Mail, ChevronRight, Edit3, Key, CheckCircle, AlertTriangle, Shield, User, Globe,
  Settings, MapPin, Loader2, X, Save, Lock,
} from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';
import { userApi } from '@/services/api/user.api';
import { authApi } from '@/services/api/auth.api';
import { useAuthStore } from '@/stores/auth.store';
import type { UserMeResponse, UserStatisticsResponse, UserPreferencesResponse } from '@/types';

interface StatCard {
  label: string;
  key: keyof UserStatisticsResponse;
  icon: typeof Beaker;
  color: string;
}

const statCards: StatCard[] = [
  { label: 'statExperiments', key: 'totalExperimentsRun', icon: Beaker, color: 'purple' },
  { label: 'statFormulas', key: 'totalFormulasParsed', icon: Cpu, color: 'teal' },
  { label: 'statEquations', key: 'totalEquationsBalanced', icon: BarChart3, color: 'amber' },
  { label: 'statSafety', key: 'safetyViolationsTriggered', icon: Activity, color: 'rose' },
];

const colorMap: Record<string, { bg: string; text: string; fill: string }> = {
  purple: { bg: 'rgba(139,92,246,0.15)', text: '#C084FC', fill: '#8B5CF6' },
  teal: { bg: 'rgba(20,241,149,0.15)', text: '#14F195', fill: '#14F195' },
  amber: { bg: 'rgba(245,158,11,0.15)', text: '#F59E0B', fill: '#F59E0B' },
  rose: { bg: 'rgba(244,63,94,0.15)', text: '#F43F5E', fill: '#F43F5E' },
};

export default function ProfilePage() {
  const pathname = usePathname();
  const t = useTranslations('profile');
  const tc = useTranslations('common');
  const tn = useTranslations('nav');
  const locale = pathname.split('/')[1] || 'en';
  const { user, fetchUser, logout } = useAuthStore();

  const [activeTab, setActiveTab] = useState('overview');
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editUsername, setEditUsername] = useState('');
  const [stats, setStats] = useState<UserStatisticsResponse | null>(null);
  const [preferences, setPreferences] = useState<UserPreferencesResponse | null>(null);
  const [loadingStats, setLoadingStats] = useState(true);
  const [loadingPrefs, setLoadingPrefs] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveStatus, setSaveStatus] = useState<string | null>(null);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);
  const [error, setError] = useState<string | null>(null);

  const showToast = (message: string, type: 'success' | 'error' = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 2500);
  };

  const loadStats = useCallback(async () => {
    setLoadingStats(true);
    try {
      const data = await userApi.getStatistics();
      setStats(data);
    } catch {
      // Stats may fail silently
    } finally {
      setLoadingStats(false);
    }
  }, []);

  const loadPreferences = useCallback(async () => {
    setLoadingPrefs(true);
    try {
      const data = await userApi.getPreferences();
      setPreferences(data);
    } catch {
      // Use defaults
    } finally {
      setLoadingPrefs(false);
    }
  }, []);

  useEffect(() => {
    if (!user) fetchUser();
    loadStats();
    loadPreferences();
  }, [user, fetchUser, loadStats, loadPreferences]);

  useEffect(() => {
    if (user) setEditUsername(user.username);
  }, [user]);

  const handleSaveProfile = async () => {
    if (!editUsername.trim()) return;
    setSaving(true);
    setSaveStatus(null);
    try {
      await userApi.updateMe({ username: editUsername.trim() });
      await fetchUser();
      setEditModalOpen(false);
      showToast(t('profileUpdated'));
    } catch (err: any) {
      setSaveStatus(err.message || t('saveFailedMsg'));
    } finally {
      setSaving(false);
    }
  };

  const handleSavePreferences = async () => {
    if (!preferences) return;
    setSaving(true);
    setSaveStatus(null);
    try {
      await userApi.updatePreferences({
        theme: preferences.theme,
        defaultTemperatureUnit: preferences.defaultTemperatureUnit,
        defaultPressureUnit: preferences.defaultPressureUnit,
        defaultVolumeUnit: preferences.defaultVolumeUnit,
        autoSaveEnabled: preferences.autoSaveEnabled,
      });
      setSaveStatus(t('saved'));
      setTimeout(() => setSaveStatus(null), 2000);
    } catch (err: any) {
      setSaveStatus(t('saveFailed'));
    } finally {
      setSaving(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    window.location.href = `/${locale}/auth`;
  };

  const formatDate = (dateStr?: string) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  if (!user) {
    return (
      <div className="relative min-h-screen flex items-center justify-center" style={{ backgroundColor: '#050508' }}>
        <BackgroundGlow />
        <ScienceBackground />
        <Loader2 size={32} className="animate-spin text-[#8B5CF6]" />
      </div>
    );
  }

  const tabs = [
    { key: 'overview', label: t('tabOverview'), icon: Grid3X3 },
    { key: 'activity', label: t('tabActivity'), icon: Clock },
    { key: 'preferences', label: t('tabPreferences'), icon: Settings },
    { key: 'security', label: t('tabSecurity'), icon: Lock },
  ];

  return (
    <div className="relative min-h-screen" style={{ backgroundColor: '#050508' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative w-full max-w-[1320px] mx-auto px-5 py-6" style={{ zIndex: 2 }}>

        {/* PROFILE BANNER */}
        <section className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[24px] overflow-hidden mb-6 shadow-[0_20px_40px_rgba(0,0,0,0.4)]">
          <div className="h-40 relative border-b border-[rgba(255,255,255,0.08)]" style={{ background: 'linear-gradient(135deg, rgba(139,92,246,0.25) 0%, rgba(20,241,149,0.1) 50%, rgba(168,85,247,0.2) 100%), radial-gradient(circle at 70% 30%, rgba(139,92,246,0.4), transparent 60%)' }}>
            <div className="absolute inset-0 opacity-15" style={{ backgroundImage: 'radial-gradient(#fff 1px, transparent 1px)', backgroundSize: '16px 16px' }} />
          </div>
          <div className="px-8 pb-7 -mt-[60px] relative flex flex-col md:flex-row items-start md:items-end gap-5 flex-wrap">
            <div className="flex items-end gap-5">
              <div className="relative">
                <div className="w-[110px] h-[110px] rounded-[22px] border-4 border-[#0B0C14] bg-gradient-to-br from-[#1E1B4B] to-[#311042] flex items-center justify-center text-[40px] text-[#C084FC] shadow-[0_10px_25px_rgba(0,0,0,.5),0_0_30px_rgba(139,92,246,.35)]">
                  {user.username[0].toUpperCase()}
                </div>
                <div className="absolute bottom-1 right-1 w-[18px] h-[18px] bg-[#14F195] border-[3px] border-[#0B0C14] rounded-full shadow-[0_0_10px_#14F195]" />
              </div>
              <div className="mb-1.5">
                <h1 className="text-[26px] font-bold tracking-tight flex items-center gap-2.5">
                  {user.username}
                  <CheckCircle size={16} className="text-[#14F195]" />
                </h1>
                <p className="text-[var(--muted-foreground)] text-sm mt-1">{t('memberSince', { date: formatDate(user.createdAt) })}</p>
              </div>
            </div>
            <div className="flex gap-3 self-end mb-1.5">
              <button onClick={() => { setEditUsername(user.username); setEditModalOpen(true); }} className="bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white border-none py-2.5 px-5 rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer flex items-center gap-2 shadow-[0_8px_20px_rgba(139,92,246,0.35)] transition-all hover:-translate-y-0.5 hover:shadow-[0_12px_28px_rgba(139,92,246,0.5)]">
                <Edit3 size={14} /> {t('editProfile')}
              </button>
            </div>
          </div>
        </section>

        {/* STATS GRID */}
        <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-[18px] mb-6">
          {statCards.map((stat) => {
            const colors = colorMap[stat.color];
            const Icon = stat.icon;
            const value = loadingStats ? '...' : (stats?.[stat.key] ?? 0);
            return (
              <div key={stat.key} className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-5 flex items-center gap-4 transition-all hover:border-[rgba(139,92,246,0.4)] hover:-translate-y-[3px] hover:bg-[rgba(22,24,38,0.85)]">
                <div className="w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0" style={{ background: colors.bg, color: colors.text }}>
                  <Icon size={20} />
                </div>
                <div className="flex-1">
                  <div className="text-xl font-bold tracking-tight">{value}</div>
                  <div className="text-xs text-[var(--muted-foreground)] mt-0.5">{t(stat.label)}</div>
                </div>
              </div>
            );
          })}
        </section>

        {/* TABS */}
        <nav className="flex gap-2.5 border-b border-[rgba(255,255,255,0.08)] mb-6 overflow-x-auto pb-0.5">
          {tabs.map((tab) => {
            const Icon = tab.icon;
            return (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                className={`py-3 px-5 bg-transparent border-none border-b-2 border-b-transparent text-sm font-semibold cursor-pointer transition-all flex items-center gap-2 whitespace-nowrap ${activeTab === tab.key ? 'text-[#C084FC] border-b-[#8B5CF6]' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}
              >
                <Icon size={14} />{tab.label}
              </button>
            );
          })}
        </nav>

        {/* TAB CONTENT */}
        {activeTab === 'overview' && (
          <div className="grid grid-cols-1 lg:grid-cols-[2fr_1fr] gap-6">
            <div>
              <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6 mb-6">
                <div className="flex items-center justify-between mb-5">
                  <h2 className="text-base font-bold flex items-center gap-2.5">
                    <Clock size={16} className="text-[#8B5CF6]" />{t('recentActivity')}
                  </h2>
                </div>
                {stats?.lastActiveTimestamp ? (
                  <div className="text-sm text-[var(--muted-foreground)]">
                    {t('lastActive', { date: new Date(stats.lastActiveTimestamp).toLocaleString('en-US', { dateStyle: 'medium', timeStyle: 'short' }) })}
                  </div>
                ) : (
                  <p className="text-sm text-[var(--muted-foreground)]">{t('noRecentActivity')}</p>
                )}
              </div>
            </div>

            <div>
              <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6 mb-6">
                <h2 className="text-base font-bold flex items-center gap-2.5 mb-5">
                  <User size={16} className="text-[#8B5CF6]" />{t('profileInfo')}
                </h2>
                <div className="flex flex-col gap-3.5">
                  {[
                    { icon: Mail, label: t('email'), value: user.email },
                    { icon: Shield, label: t('role'), value: user.role === 'ROLE_ADMIN' ? t('roleAdmin') : t('roleResearcher') },
                    { icon: MapPin, label: t('userId'), value: user.id },
                  ].map((row, i) => {
                    const Icon = row.icon;
                    return (
                      <div key={i} className="flex items-center justify-between text-[13px] pb-3 border-b border-white/[0.04] last:border-0 last:pb-0">
                        <span className="text-[var(--muted-foreground)] flex items-center gap-2.5"><Icon size={14} />{row.label}</span>
                        <span className="font-medium text-[var(--foreground)] font-mono text-xs truncate max-w-[160px]">{row.value}</span>
                      </div>
                    );
                  })}
                </div>
              </div>

              <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6">
                <h2 className="text-base font-bold mb-4">{t('quickActions')}</h2>
                <div className="space-y-2">
                  <Link href={`/${locale}/dashboard`} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.03] transition-all no-underline text-[var(--foreground)]">
                    <Grid3X3 size={18} className="text-[var(--muted-foreground)]" /><span className="text-sm">{tn('myWorkspaces')}</span>
                  </Link>
                  <Link href={`/${locale}/workspace/sandbox`} className="flex items-center gap-3 p-3 rounded-lg hover:bg-white/[0.03] transition-all no-underline text-[var(--foreground)]">
                    <FlaskConical size={18} className="text-[var(--muted-foreground)]" /><span className="text-sm">{t('openSandbox')}</span>
                  </Link>
                </div>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'activity' && (
          <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6">
            <h2 className="text-base font-bold flex items-center gap-2.5 mb-5">
              <Clock size={16} className="text-[#8B5CF6]" />{t('activitySummary')}
            </h2>
            <div className="space-y-4">
              {statCards.map((stat) => {
                const val = stats?.[stat.key] ?? 0;
                return (
                  <div key={stat.key} className="flex items-center justify-between">
                    <span className="text-sm text-[var(--muted-foreground)]">{t(stat.label)}</span>
                    <span className="text-sm font-mono text-[var(--foreground)]">{val}</span>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {activeTab === 'preferences' && (
          <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6 max-w-lg">
            <h2 className="text-base font-bold flex items-center gap-2.5 mb-6">
              <Settings size={16} className="text-[#8B5CF6]" />{t('preferences')}
            </h2>
            {loadingPrefs ? (
              <div className="flex items-center gap-2 text-sm text-[var(--muted-foreground)]"><Loader2 size={14} className="animate-spin" /> {t('loading')}</div>
            ) : preferences ? (
              <div className="space-y-5">
                <div>
                  <label className="block text-xs text-[var(--muted-foreground)] mb-2">{t('theme')}</label>
                  <select value={preferences.theme} onChange={(e) => setPreferences({ ...preferences, theme: e.target.value as any })} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] py-2.5 px-3 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6]">
                    <option value="DARK">{t('dark')}</option>
                    <option value="LIGHT">{t('light')}</option>
                    <option value="SYSTEM">{t('system')}</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs text-[var(--muted-foreground)] mb-2">{t('temperatureUnit')}</label>
                  <select value={preferences.defaultTemperatureUnit} onChange={(e) => setPreferences({ ...preferences, defaultTemperatureUnit: e.target.value as any })} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] py-2.5 px-3 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6]">
                    <option value="CELSIUS">Celsius</option>
                    <option value="KELVIN">Kelvin</option>
                    <option value="FAHRENHEIT">Fahrenheit</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs text-[var(--muted-foreground)] mb-2">{t('pressureUnit')}</label>
                  <select value={preferences.defaultPressureUnit} onChange={(e) => setPreferences({ ...preferences, defaultPressureUnit: e.target.value as any })} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] py-2.5 px-3 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6]">
                    <option value="ATMOSPHERE">Atmosphere</option>
                    <option value="PASCAL">Pascal</option>
                    <option value="BAR">Bar</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs text-[var(--muted-foreground)] mb-2">{t('volumeUnit')}</label>
                  <select value={preferences.defaultVolumeUnit} onChange={(e) => setPreferences({ ...preferences, defaultVolumeUnit: e.target.value as any })} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] py-2.5 px-3 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6]">
                    <option value="LITER">Liter</option>
                    <option value="MILLILITER">Milliliter</option>
                    <option value="CUBIC_METER">Cubic Meter</option>
                  </select>
                </div>
                <div className="flex items-center justify-between pt-2">
                  <span className="text-sm text-[var(--foreground)]">{t('autoSave')}</span>
                  <button
                    onClick={() => setPreferences({ ...preferences, autoSaveEnabled: !preferences.autoSaveEnabled })}
                    className={`w-11 h-6 rounded-full relative cursor-pointer transition-colors ${preferences.autoSaveEnabled ? 'bg-[#8B5CF6]' : 'bg-white/10'}`}
                  >
                    <div className={`absolute top-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${preferences.autoSaveEnabled ? 'right-0.5 translate-x-0' : 'left-0.5 translate-x-0'}`} />
                  </button>
                </div>
                <div className="flex items-center gap-3">
                  <button onClick={handleSavePreferences} disabled={saving} className="bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white border-none py-2.5 px-5 rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] transition-all hover:-translate-y-0.5 disabled:opacity-60 flex items-center gap-2">
                    {saving ? <><Loader2 size={14} className="animate-spin" />{t('saving')}</> : <><Save size={14} />{t('saveChanges')}</>}
                  </button>
                  {saveStatus && <span className={`text-xs ${saveStatus === t('saved') ? 'text-[#14F195]' : 'text-[#F43F5E]'}`}>{saveStatus}</span>}
                </div>
              </div>
            ) : (
              <p className="text-sm text-[var(--muted-foreground)]">{t('unableToLoadPreferences')}</p>
            )}
          </div>
        )}

        {activeTab === 'security' && (
          <div className="space-y-6 max-w-lg">
            <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(255,255,255,0.08)] rounded-[var(--radius-md)] p-6">
              <h2 className="text-base font-bold flex items-center gap-2.5 mb-5">
                <Shield size={16} className="text-[#8B5CF6]" />{t('account')}
              </h2>
              <div className="space-y-3.5">
                <div className="flex items-center justify-between text-[13px] pb-3 border-b border-white/[0.04]">
                  <span className="text-[var(--muted-foreground)]">{t('email')}</span>
                  <span className="font-mono text-[var(--foreground)]">{user.email}</span>
                </div>
                <div className="flex items-center justify-between text-[13px] pb-3 border-b border-white/[0.04]">
                  <span className="text-[var(--muted-foreground)]">{t('role')}</span>
                  <span className="font-mono text-[var(--foreground)]">{user.role === 'ROLE_ADMIN' ? t('roleAdmin') : t('roleResearcher')}</span>
                </div>
                <div className="flex items-center justify-between text-[13px]">
                  <span className="text-[var(--muted-foreground)]">{t('signOut')}</span>
                  <button onClick={handleLogout} className="text-sm text-[#8B5CF6] hover:underline bg-transparent border-none cursor-pointer">{t('signOut')}</button>
                </div>
              </div>
            </div>
            <div className="bg-[rgba(15,16,26,0.75)] backdrop-blur-2xl border border-[rgba(244,63,94,0.2)] rounded-[var(--radius-md)] p-6">
              <h2 className="text-base font-bold flex items-center gap-2.5 mb-3 text-[#F43F5E]">
                <AlertTriangle size={16} />{t('dangerZone')}
              </h2>
              <p className="text-xs text-[var(--muted-foreground)] mb-4">{t('dangerZoneDesc')}</p>
              <button className="bg-[#F43F5E]/10 border border-[#F43F5E]/30 text-[#F43F5E] py-2 px-4 rounded-[var(--radius-sm)] text-sm font-medium cursor-pointer hover:bg-[#F43F5E]/20 transition-all">
                {t('deleteAccount')}
              </button>
            </div>
          </div>
        )}
      </div>

      {/* EDIT PROFILE MODAL */}
      {editModalOpen && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[100] flex items-center justify-center p-5" onClick={() => setEditModalOpen(false)}>
          <div className="bg-[#0F101A] border border-[rgba(255,255,255,0.08)] rounded-[24px] w-full max-w-[520px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold">{t('editProfile')}</h3>
              <button onClick={() => setEditModalOpen(false)} className="bg-transparent border-none text-[var(--dim)] text-lg cursor-pointer hover:text-[var(--foreground)]">&times;</button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('username')}</label>
                <input type="text" value={editUsername} onChange={(e) => setEditUsername(e.target.value)} className="w-full py-3 px-3.5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none transition-all focus:border-[#8B5CF6]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('email')}</label>
                <input type="text" value={user.email} disabled className="w-full py-3 px-3.5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--dim)] outline-none cursor-not-allowed" />
              </div>
              {saveStatus && <p className="text-xs text-[#F43F5E]">{saveStatus}</p>}
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setEditModalOpen(false)} className="bg-[var(--input)] border border-[var(--border)] text-[var(--foreground)] py-2.5 px-5 rounded-[var(--radius-md)] text-sm font-medium cursor-pointer hover:bg-white/[0.08] transition-all">{tc('cancel')}</button>
              <button onClick={handleSaveProfile} disabled={saving} className="bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white border-none py-2.5 px-5 rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] hover:-translate-y-0.5 transition-all disabled:opacity-60 flex items-center gap-2">
                {saving ? <><Loader2 size={14} className="animate-spin" />{t('saving')}</> : t('saveChanges')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TOAST */}
      {toast && (
        <div className={`fixed bottom-6 right-6 z-[200] px-4 py-3 rounded-[var(--radius-md)] border shadow-lg flex items-center gap-3 transition-all ${toast.type === 'error' ? 'border-[#F43F5E]/30 bg-[#F43F5E]/10 text-[#F43F5E]' : 'border-[#14F195]/30 bg-[#14F195]/10 text-[#14F195]'}`}>
          <CheckCircle size={14} />
          <span className="text-sm font-medium">{toast.message}</span>
          <button onClick={() => setToast(null)} className="opacity-60 hover:opacity-100"><X size={14} /></button>
        </div>
      )}
    </div>
  );
}
