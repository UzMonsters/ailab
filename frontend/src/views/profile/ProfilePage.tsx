'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useEffect, useState, useCallback } from 'react';
import {
  Beaker, Cpu, BarChart3, Activity, Grid3X3, Clock,
  Mail, Edit3, CheckCircle, AlertTriangle, Shield, User,
  Settings, MapPin, Loader2, X, Save, Lock,
  FlaskConical,
} from 'lucide-react';
import { userApi } from '@/services/api/user.api';
import { useAuthStore } from '@/stores/auth.store';
import type { UserStatisticsResponse, UserPreferencesResponse } from '@/types';

interface StatCard {
  label: string;
  key: keyof UserStatisticsResponse;
  icon: typeof Beaker;
  color: 'purple' | 'teal' | 'amber' | 'rose';
}

const statCards: StatCard[] = [
  { label: 'statExperiments', key: 'totalExperimentsRun', icon: Beaker, color: 'purple' },
  { label: 'statFormulas', key: 'totalFormulasParsed', icon: Cpu, color: 'teal' },
  { label: 'statEquations', key: 'totalEquationsBalanced', icon: BarChart3, color: 'amber' },
  { label: 'statSafety', key: 'safetyViolationsTriggered', icon: Activity, color: 'rose' },
];

const colorMap = {
  purple: { bg: 'bg-[#8B5CF6]/10', text: 'text-[#8B5CF6]', border: 'border-[#8B5CF6]/30', hover: 'hover:border-[#8B5CF6]/60' },
  teal: { bg: 'bg-[#14F195]/10', text: 'text-[#14F195]', border: 'border-[#14F195]/30', hover: 'hover:border-[#14F195]/60' },
  amber: { bg: 'bg-[#F59E0B]/10', text: 'text-[#F59E0B]', border: 'border-[#F59E0B]/30', hover: 'hover:border-[#F59E0B]/60' },
  rose: { bg: 'bg-[#F43F5E]/10', text: 'text-[#F43F5E]', border: 'border-[#F43F5E]/30', hover: 'hover:border-[#F43F5E]/60' },
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

  useEffect(() => {
    // Basic theme switcher listener implementation
    if (preferences?.theme) {
      if (preferences.theme === 'LIGHT') {
        document.documentElement.setAttribute('data-theme', 'light');
      } else {
        document.documentElement.removeAttribute('data-theme');
      }
    }
  }, [preferences?.theme]);

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
    return new Date(dateStr).toLocaleDateString(locale === 'ru' ? 'ru-RU' : 'en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  };

  if (!user) {
    return (
      <div className="w-full h-[60vh] flex items-center justify-center bg-[var(--background)]">
        <Loader2 size={32} className="animate-spin text-[var(--primary)]" />
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
    <div className="w-full max-w-[1320px] mx-auto px-6 py-8 animate-in fade-in slide-in-from-bottom-4 duration-500">
      
      {/* PROFILE BANNER */}
      <section className="bg-[var(--card)] border border-[var(--border)] rounded-[24px] overflow-hidden mb-8 shadow-sm">
        <div className="h-40 relative border-b border-[var(--border)] bg-gradient-to-br from-[var(--primary)]/20 via-[var(--primary)]/5 to-[var(--accent)]">
          <div className="absolute inset-0 opacity-10 bg-[radial-gradient(var(--foreground)_1px,transparent_1px)] [background-size:16px_16px]" />
        </div>
        <div className="px-8 pb-8 -mt-[60px] relative flex flex-col md:flex-row items-start md:items-end gap-6 flex-wrap">
          <div className="flex items-end gap-6">
            <div className="relative">
              <div className="w-[120px] h-[120px] rounded-[24px] border-4 border-[var(--card)] bg-[var(--background)] flex items-center justify-center text-[48px] text-[var(--primary)] shadow-lg">
                {user.username[0].toUpperCase()}
              </div>
              <div className="absolute bottom-1 right-1 w-[20px] h-[20px] bg-[#34D399] border-[4px] border-[var(--card)] rounded-full" />
            </div>
            <div className="mb-2">
              <h1 className="text-3xl font-bold tracking-tight text-[var(--foreground)] flex items-center gap-3">
                {user.username}
                <CheckCircle size={20} className="text-[var(--primary)]" />
              </h1>
              <p className="text-[var(--muted-foreground)] text-sm mt-1">{t('memberSince', { date: formatDate(user.createdAt) })}</p>
            </div>
          </div>
          <div className="flex gap-3 md:ml-auto mb-2">
            <button onClick={() => { setEditUsername(user.username); setEditModalOpen(true); }} className="bg-[var(--primary)] text-[var(--primary-foreground)] border-none py-2.5 px-6 rounded-xl text-sm font-semibold cursor-pointer flex items-center gap-2 hover:opacity-90 transition-opacity shadow-sm">
              <Edit3 size={16} /> {t('editProfile')}
            </button>
          </div>
        </div>
      </section>

      {/* STATS GRID */}
      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
        {statCards.map((stat) => {
          const cls = colorMap[stat.color];
          const Icon = stat.icon;
          const value = stats?.[stat.key] ?? 0;
          return (
            <div key={stat.key} className={`bg-[var(--card)] border border-[var(--border)] ${cls.hover} rounded-[var(--radius-lg)] p-6 flex items-center gap-5 transition-all shadow-sm`}>
              <div className={`w-14 h-14 rounded-2xl flex items-center justify-center shrink-0 ${cls.bg} ${cls.text}`}>
                <Icon size={24} />
              </div>
              <div className="flex-1">
                {loadingStats ? <div className="h-8 w-16 animate-pulse rounded bg-[var(--muted)]" aria-label="Loading" /> : <div className="text-2xl font-bold text-[var(--foreground)] tracking-tight">{value}</div>}
                <div className="text-sm font-medium text-[var(--muted-foreground)] mt-1">{t(stat.label)}</div>
              </div>
            </div>
          );
        })}
      </section>

      {/* TABS */}
      <nav className="flex gap-4 border-b border-[var(--border)] mb-8 overflow-x-auto">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.key;
          return (
            <button
              key={tab.key}
              onClick={() => setActiveTab(tab.key)}
              className={`pb-4 px-2 bg-transparent border-none border-b-[3px] text-sm font-semibold cursor-pointer transition-all flex items-center gap-2 whitespace-nowrap ${isActive ? 'text-[var(--primary)] border-[var(--primary)]' : 'text-[var(--muted-foreground)] border-transparent hover:text-[var(--foreground)] hover:border-[var(--border)]'}`}
            >
              <Icon size={16} />{tab.label}
            </button>
          );
        })}
      </nav>

      {/* TAB CONTENT */}
      {activeTab === 'overview' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-8">
            <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 shadow-sm">
              <h2 className="text-lg font-bold text-[var(--foreground)] flex items-center gap-3 mb-6">
                <Clock size={20} className="text-[var(--primary)]" />{t('recentActivity')}
              </h2>
              {stats?.lastActiveTimestamp ? (
                <div className="flex flex-col gap-4">
                  <div className="flex items-start gap-4 pb-4 border-b border-[var(--border)] last:border-0">
                    <div className="w-10 h-10 rounded-full bg-[var(--primary)]/10 text-[var(--primary)] flex items-center justify-center shrink-0">
                      <FlaskConical size={18} />
                    </div>
                    <div>
                      <div className="text-sm font-medium text-[var(--foreground)]">Experiment updated</div>
                      <div className="text-xs text-[var(--muted-foreground)] mt-1">
                        {t('lastActive', { date: new Date(stats.lastActiveTimestamp).toLocaleString(locale === 'ru' ? 'ru-RU' : 'en-US', { dateStyle: 'medium', timeStyle: 'short' }) })}
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-4 rounded-xl border border-dashed border-[var(--border)] bg-[var(--background)]/50 p-5">
                  <div className="grid h-10 w-10 shrink-0 place-items-center rounded-full bg-[var(--muted)] text-[var(--muted-foreground)]"><Clock size={18} /></div>
                  <div><p className="text-sm font-semibold text-[var(--foreground)]">{locale === 'ru' ? 'Нет недавней активности' : locale === 'uz' ? 'Yaqinda faollik yo‘q' : 'No recent activity'}</p><p className="mt-1 text-xs text-[var(--muted-foreground)]">{locale === 'ru' ? 'Здесь появятся ваши эксперименты и действия.' : locale === 'uz' ? 'Tajribalar va kimyoviy amallar shu yerda ko‘rinadi.' : 'Your experiments and chemistry actions will appear here.'}</p></div>
                </div>
              )}
            </div>
          </div>

          <div className="space-y-8">
            <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 shadow-sm">
              <h2 className="text-lg font-bold text-[var(--foreground)] flex items-center gap-3 mb-6">
                <User size={20} className="text-[var(--primary)]" />{t('profileInfo')}
              </h2>
              <div className="flex flex-col gap-5">
                {[
                  { icon: Mail, label: t('email'), value: user.email },
                  { icon: Shield, label: t('role'), value: user.role === 'ROLE_ADMIN' ? t('roleAdmin') : t('roleResearcher') },
                  { icon: MapPin, label: t('userId'), value: user.id },
                ].map((row, i) => {
                  const Icon = row.icon;
                  return (
                    <div key={i} className="flex flex-col gap-1.5 pb-4 border-b border-[var(--border)] last:border-0 last:pb-0">
                      <span className="text-[var(--muted-foreground)] text-xs font-semibold uppercase tracking-wider flex items-center gap-2"><Icon size={14} />{row.label}</span>
                      <span className="font-medium text-[var(--foreground)] text-sm truncate">{row.value}</span>
                    </div>
                  );
                })}
              </div>
            </div>

            <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 shadow-sm">
              <h2 className="text-lg font-bold text-[var(--foreground)] mb-4">{t('quickActions')}</h2>
              <div className="space-y-3">
                <Link href={`/${locale}/dashboard`} className="flex items-center gap-3 p-4 rounded-xl border border-[var(--border)] hover:border-[var(--primary)] hover:bg-[var(--primary)]/5 transition-all no-underline text-[var(--foreground)]">
                  <Grid3X3 size={20} className="text-[var(--primary)]" /><span className="text-sm font-medium">{tn('myWorkspaces')}</span>
                </Link>
                <Link href={`/${locale}/workspace/sandbox`} className="flex items-center gap-3 p-4 rounded-xl border border-[var(--border)] hover:border-[var(--primary)] hover:bg-[var(--primary)]/5 transition-all no-underline text-[var(--foreground)]">
                  <FlaskConical size={20} className="text-[var(--primary)]" /><span className="text-sm font-medium">{t('openSandbox')}</span>
                </Link>
                <Link href={`/${locale}/settings`} className="flex items-center gap-3 p-4 rounded-xl border border-[var(--border)] hover:border-[var(--primary)] hover:bg-[var(--primary)]/5 transition-all no-underline text-[var(--foreground)]">
                  <Settings size={20} className="text-[var(--primary)]" /><span className="text-sm font-medium">{tc('settings')}</span>
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {activeTab === 'activity' && (
        <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 shadow-sm max-w-3xl">
          <h2 className="text-lg font-bold text-[var(--foreground)] flex items-center gap-3 mb-8">
            <Clock size={20} className="text-[var(--primary)]" />{t('activitySummary')}
          </h2>
          <div className="space-y-6">
            {statCards.map((stat) => {
              const val = stats?.[stat.key] ?? 0;
              return (
                <div key={stat.key} className="flex items-center justify-between pb-4 border-b border-[var(--border)] last:border-0">
                  <span className="text-sm font-medium text-[var(--muted-foreground)]">{t(stat.label)}</span>
                  <span className="text-lg font-bold text-[var(--foreground)]">{val}</span>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {activeTab === 'preferences' && (
        <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 max-w-2xl shadow-sm">
          <h2 className="text-lg font-bold text-[var(--foreground)] flex items-center gap-3 mb-8">
            <Settings size={20} className="text-[var(--primary)]" />{t('preferences')}
          </h2>
          {loadingPrefs ? (
            <div className="flex items-center gap-3 text-sm font-medium text-[var(--muted-foreground)]"><Loader2 size={18} className="animate-spin" /> {t('loading')}</div>
          ) : preferences ? (
            <div className="space-y-8">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-3">{t('theme')}</label>
                  <select value={preferences.theme} onChange={(e) => setPreferences({ ...preferences, theme: e.target.value as any })} className="w-full bg-[var(--background)] border border-[var(--border)] rounded-xl py-3 px-4 text-sm font-medium text-[var(--foreground)] outline-none focus:border-[var(--primary)] transition-colors">
                    <option value="DARK">{t('dark')}</option>
                    <option value="LIGHT">{t('light')}</option>
                    <option value="SYSTEM">{t('system')}</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-3">{t('temperatureUnit')}</label>
                  <select value={preferences.defaultTemperatureUnit} onChange={(e) => setPreferences({ ...preferences, defaultTemperatureUnit: e.target.value as any })} className="w-full bg-[var(--background)] border border-[var(--border)] rounded-xl py-3 px-4 text-sm font-medium text-[var(--foreground)] outline-none focus:border-[var(--primary)] transition-colors">
                    <option value="CELSIUS">Celsius</option>
                    <option value="KELVIN">Kelvin</option>
                    <option value="FAHRENHEIT">Fahrenheit</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-3">{t('pressureUnit')}</label>
                  <select value={preferences.defaultPressureUnit} onChange={(e) => setPreferences({ ...preferences, defaultPressureUnit: e.target.value as any })} className="w-full bg-[var(--background)] border border-[var(--border)] rounded-xl py-3 px-4 text-sm font-medium text-[var(--foreground)] outline-none focus:border-[var(--primary)] transition-colors">
                    <option value="ATMOSPHERE">Atmosphere</option>
                    <option value="PASCAL">Pascal</option>
                    <option value="BAR">Bar</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-3">{t('volumeUnit')}</label>
                  <select value={preferences.defaultVolumeUnit} onChange={(e) => setPreferences({ ...preferences, defaultVolumeUnit: e.target.value as any })} className="w-full bg-[var(--background)] border border-[var(--border)] rounded-xl py-3 px-4 text-sm font-medium text-[var(--foreground)] outline-none focus:border-[var(--primary)] transition-colors">
                    <option value="LITER">Liter</option>
                    <option value="MILLILITER">Milliliter</option>
                    <option value="CUBIC_METER">Cubic Meter</option>
                  </select>
                </div>
              </div>
              
              <div className="flex items-center justify-between p-4 rounded-xl border border-[var(--border)] bg-[var(--background)]/50">
                <div>
                  <span className="block text-sm font-bold text-[var(--foreground)]">{t('autoSave')}</span>
                  <span className="block text-xs text-[var(--muted-foreground)] mt-1">Automatically save experiment progress</span>
                </div>
                <button
                  onClick={() => setPreferences({ ...preferences, autoSaveEnabled: !preferences.autoSaveEnabled })}
                  className={`w-12 h-6 rounded-full relative cursor-pointer transition-colors ${preferences.autoSaveEnabled ? 'bg-[var(--primary)]' : 'bg-[var(--border)]'}`}
                >
                  <div className={`absolute top-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform ${preferences.autoSaveEnabled ? 'right-0.5 translate-x-0' : 'left-0.5 translate-x-0'}`} />
                </button>
              </div>

              <div className="flex items-center gap-4 pt-4 border-t border-[var(--border)]">
                <button onClick={handleSavePreferences} disabled={saving} className="bg-[var(--primary)] text-[var(--primary-foreground)] border-none py-3 px-8 rounded-xl text-sm font-bold cursor-pointer hover:opacity-90 transition-opacity disabled:opacity-60 flex items-center gap-2 shadow-sm">
                  {saving ? <><Loader2 size={16} className="animate-spin" />{t('saving')}</> : <><Save size={16} />{t('saveChanges')}</>}
                </button>
                {saveStatus && <span className={`text-sm font-medium ${saveStatus === t('saved') ? 'text-[#14F195]' : 'text-[#F43F5E]'}`}>{saveStatus}</span>}
              </div>
            </div>
          ) : (
            <p className="text-sm text-[var(--muted-foreground)]">{t('unableToLoadPreferences')}</p>
          )}
        </div>
      )}

      {activeTab === 'security' && (
        <div className="space-y-8 max-w-2xl">
          <div className="bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-lg)] p-8 shadow-sm">
            <h2 className="text-lg font-bold text-[var(--foreground)] flex items-center gap-3 mb-6">
              <Shield size={20} className="text-[var(--primary)]" />{t('account')}
            </h2>
            <div className="space-y-5">
              <div className="flex items-center justify-between pb-4 border-b border-[var(--border)]">
                <span className="text-sm font-medium text-[var(--muted-foreground)]">{t('email')}</span>
                <span className="text-sm font-bold text-[var(--foreground)]">{user.email}</span>
              </div>
              <div className="flex items-center justify-between pb-4 border-b border-[var(--border)]">
                <span className="text-sm font-medium text-[var(--muted-foreground)]">{t('role')}</span>
                <span className="text-sm font-bold text-[var(--foreground)]">{user.role === 'ROLE_ADMIN' ? t('roleAdmin') : t('roleResearcher')}</span>
              </div>
              <div className="flex items-center justify-between pt-2">
                <span className="text-sm font-medium text-[var(--muted-foreground)]">{t('signOut')}</span>
                <button onClick={handleLogout} className="text-sm font-bold text-[var(--primary)] hover:underline bg-transparent border-none cursor-pointer px-4 py-2 rounded hover:bg-[var(--primary)]/10 transition-colors">{t('signOut')}</button>
              </div>
            </div>
          </div>

          <div className="bg-[var(--card)] border border-[#F43F5E]/30 rounded-[var(--radius-lg)] p-8 shadow-sm relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1 h-full bg-[#F43F5E]" />
            <h2 className="text-lg font-bold flex items-center gap-3 mb-3 text-[#F43F5E]">
              <AlertTriangle size={20} />{t('dangerZone')}
            </h2>
            <p className="text-sm text-[var(--muted-foreground)] mb-6 leading-relaxed">{t('dangerZoneDesc')}</p>
            <button className="bg-[#F43F5E]/10 border border-[#F43F5E]/30 text-[#F43F5E] py-2.5 px-6 rounded-xl text-sm font-bold cursor-pointer hover:bg-[#F43F5E]/20 transition-all">
              {t('deleteAccount')}
            </button>
          </div>
        </div>
      )}

      {/* EDIT PROFILE MODAL */}
      {editModalOpen && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[100] flex items-center justify-center p-6 animate-in fade-in duration-200" onClick={() => setEditModalOpen(false)}>
          <div className="bg-[var(--card)] border border-[var(--border)] rounded-2xl w-full max-w-[480px] p-8 shadow-2xl animate-in zoom-in-95 duration-200" onClick={(e) => e.stopPropagation()}>
            <div className="flex items-center justify-between mb-8">
              <h3 className="text-xl font-bold text-[var(--foreground)]">{t('editProfile')}</h3>
              <button onClick={() => setEditModalOpen(false)} className="p-2 rounded-lg text-[var(--muted-foreground)] hover:bg-[var(--accent)] hover:text-[var(--foreground)] transition-colors"><X size={20} /></button>
            </div>
            <div className="space-y-6">
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-2">{t('username')}</label>
                <input type="text" value={editUsername} onChange={(e) => setEditUsername(e.target.value)} className="w-full py-3 px-4 bg-[var(--background)] border border-[var(--border)] rounded-xl text-sm text-[var(--foreground)] outline-none transition-colors focus:border-[var(--primary)]" />
              </div>
              <div>
                <label className="block text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)] mb-2">{t('email')}</label>
                <input type="text" value={user.email} disabled className="w-full py-3 px-4 bg-[var(--background)] border border-[var(--border)]/50 rounded-xl text-sm text-[var(--muted-foreground)] outline-none cursor-not-allowed opacity-70" />
              </div>
              {saveStatus && <p className="text-sm font-medium text-[#F43F5E]">{saveStatus}</p>}
            </div>
            <div className="flex justify-end gap-3 mt-8 pt-6 border-t border-[var(--border)]">
              <button onClick={() => setEditModalOpen(false)} className="bg-[var(--background)] border border-[var(--border)] text-[var(--foreground)] py-2.5 px-6 rounded-xl text-sm font-bold cursor-pointer hover:bg-[var(--accent)] transition-colors">{tc('cancel')}</button>
              <button onClick={handleSaveProfile} disabled={saving} className="bg-[var(--primary)] text-[var(--primary-foreground)] border-none py-2.5 px-6 rounded-xl text-sm font-bold cursor-pointer hover:opacity-90 transition-opacity disabled:opacity-60 flex items-center gap-2 shadow-sm">
                {saving ? <><Loader2 size={16} className="animate-spin" />{t('saving')}</> : t('saveChanges')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TOAST */}
      {toast && (
        <div className={`fixed bottom-8 right-8 z-[200] px-5 py-4 rounded-xl border shadow-xl flex items-center gap-3 transition-all animate-in slide-in-from-bottom-8 duration-300 ${toast.type === 'error' ? 'border-[#F43F5E]/30 bg-[var(--card)] text-[#F43F5E]' : 'border-[var(--primary)]/30 bg-[var(--card)] text-[var(--primary)]'}`}>
          <CheckCircle size={18} />
          <span className="text-sm font-bold">{toast.message}</span>
          <button onClick={() => setToast(null)} className="ml-2 text-[var(--muted-foreground)] hover:text-inherit transition-colors"><X size={16} /></button>
        </div>
      )}
    </div>
  );
}
