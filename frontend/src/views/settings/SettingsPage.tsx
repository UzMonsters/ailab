'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef, useState } from 'react';
import { AlertTriangle, Check, ChevronRight, Globe2, Laptop, Loader2, LogOut, Moon, Palette, Save, Shield, SlidersHorizontal, Sun, UserRound, X } from 'lucide-react';
import { userApi } from '@/services/api/user.api';
import { useAuthStore } from '@/stores/auth.store';
import { useUIStore } from '@/stores/ui.store';
import { useLocaleSwitch } from '@/hooks/useLocaleSwitch';
import type { UserMeResponse, UserPreferencesResponse, UserPreferencesUpdateRequest } from '@/types';

type Section = 'general' | 'appearance' | 'laboratory' | 'account' | 'security';
type Copy = { title: string; subtitle: string; sections: Record<Section, string>; language: string; timezone: string; timezoneValue: string; theme: string; dark: string; light: string; system: string; themeHint: string; temperature: string; pressure: string; volume: string; autoSave: string; autoSaveHint: string; saving: string; saved: string; saveError: string; account: string; username: string; email: string; role: string; security: string; status: string; active: string; logout: string; danger: string; dangerHint: string; delete: string; deleteTitle: string; deleteBody: string; cancel: string; confirm: string; retry: string; loadError: string };

const copy: Record<string, Copy> = {
  en: { title: 'Settings', subtitle: 'Manage your AI Laboratory experience.', sections: { general: 'General', appearance: 'Appearance', laboratory: 'Laboratory', account: 'Account', security: 'Security' }, language: 'Language', timezone: 'Timezone', timezoneValue: 'Asia / Tashkent', theme: 'Theme', dark: 'Dark', light: 'Light', system: 'System', themeHint: 'Choose how the laboratory looks across all pages.', temperature: 'Default temperature unit', pressure: 'Default pressure unit', volume: 'Default volume unit', autoSave: 'Auto Save', autoSaveHint: 'Automatically save experiment progress when supported.', saving: 'Saving…', saved: 'Saved', saveError: 'Could not save your preferences.', account: 'Account details', username: 'Username', email: 'Email', role: 'Role', security: 'Security', status: 'Authentication status', active: 'Active session', logout: 'Log out', danger: 'Danger zone', dangerHint: 'Deleting your account permanently removes access to your laboratory data.', delete: 'Delete account', deleteTitle: 'Delete your account?', deleteBody: 'This action cannot be undone. Type your username to confirm.', cancel: 'Cancel', confirm: 'Delete account', retry: 'Retry', loadError: 'Could not load your preferences.' },
  ru: { title: 'Настройки', subtitle: 'Управляйте рабочей средой AI Laboratory.', sections: { general: 'Общие', appearance: 'Внешний вид', laboratory: 'Лаборатория', account: 'Аккаунт', security: 'Безопасность' }, language: 'Язык', timezone: 'Часовой пояс', timezoneValue: 'Азия / Ташкент', theme: 'Тема', dark: 'Тёмная', light: 'Светлая', system: 'Системная', themeHint: 'Выберите оформление лаборатории на всех страницах.', temperature: 'Единица температуры', pressure: 'Единица давления', volume: 'Единица объёма', autoSave: 'Автосохранение', autoSaveHint: 'Автоматически сохранять ход эксперимента, если поддерживается.', saving: 'Сохранение…', saved: 'Сохранено', saveError: 'Не удалось сохранить настройки.', account: 'Данные аккаунта', username: 'Имя пользователя', email: 'Email', role: 'Роль', security: 'Безопасность', status: 'Статус авторизации', active: 'Сессия активна', logout: 'Выйти', danger: 'Опасная зона', dangerHint: 'Удаление аккаунта навсегда лишит вас доступа к данным лаборатории.', delete: 'Удалить аккаунт', deleteTitle: 'Удалить аккаунт?', deleteBody: 'Это действие нельзя отменить. Введите имя пользователя для подтверждения.', cancel: 'Отмена', confirm: 'Удалить аккаунт', retry: 'Повторить', loadError: 'Не удалось загрузить настройки.' },
  uz: { title: 'Sozlamalar', subtitle: 'AI Laboratory muhitini boshqaring.', sections: { general: 'Umumiy', appearance: 'Ko‘rinish', laboratory: 'Laboratoriya', account: 'Hisob', security: 'Xavfsizlik' }, language: 'Til', timezone: 'Vaqt mintaqasi', timezoneValue: 'Osiyo / Toshkent', theme: 'Mavzu', dark: 'To‘q', light: 'Yorug‘', system: 'Tizim', themeHint: 'Barcha sahifalar uchun laboratoriya ko‘rinishini tanlang.', temperature: 'Harorat birligi', pressure: 'Bosim birligi', volume: 'Hajm birligi', autoSave: 'Avtomatik saqlash', autoSaveHint: 'Qo‘llab-quvvatlanganda tajribani avtomatik saqlang.', saving: 'Saqlanmoqda…', saved: 'Saqlandi', saveError: 'Sozlamalarni saqlab bo‘lmadi.', account: 'Hisob ma’lumotlari', username: 'Foydalanuvchi nomi', email: 'Email', role: 'Rol', security: 'Xavfsizlik', status: 'Avtorizatsiya holati', active: 'Faol sessiya', logout: 'Chiqish', danger: 'Xavfli zona', dangerHint: 'Hisobni o‘chirish laboratoriya ma’lumotlariga kirishni butunlay olib tashlaydi.', delete: 'Hisobni o‘chirish', deleteTitle: 'Hisobni o‘chirish?', deleteBody: 'Bu amalni bekor qilib bo‘lmaydi. Tasdiqlash uchun foydalanuvchi nomini kiriting.', cancel: 'Bekor qilish', confirm: 'Hisobni o‘chirish', retry: 'Qayta urinish', loadError: 'Sozlamalarni yuklab bo‘lmadi.' },
};

const defaults: UserPreferencesResponse = { theme: 'SYSTEM', defaultTemperatureUnit: 'CELSIUS', defaultPressureUnit: 'ATMOSPHERE', defaultVolumeUnit: 'MILLILITER', autoSaveEnabled: true };

export default function SettingsPage() {
  const pathname = usePathname();
  const router = useRouter();
  const locale = pathname.split('/')[1] || 'en';
  const t = copy[locale] || copy.en;
  const { switchLocale } = useLocaleSwitch();
  const { setTheme } = useUIStore();
  const { user, isAuthenticated, isLoading: authLoading, fetchUser, logout } = useAuthStore();
  const [active, setActive] = useState<Section>('general');
  const [preferences, setPreferences] = useState<UserPreferencesResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [saveState, setSaveState] = useState<'idle' | 'saving' | 'saved' | 'error'>('idle');
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [confirmText, setConfirmText] = useState('');
  const saveTimer = useRef<number | null>(null);
  const pendingPatch = useRef<UserPreferencesUpdateRequest>({});
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => { if (!user) void fetchUser(); }, [user, fetchUser]);
  useEffect(() => {
    if (authLoading || !isAuthenticated) return;
    let cancelled = false;
    void userApi.getPreferences().then((data) => {
      if (cancelled) return;
      setPreferences(data);
      setTheme(data.theme.toLowerCase() as 'dark' | 'light' | 'system');
    }).catch(() => {
      if (cancelled) return;
      setPreferences(defaults);
      setTheme('system');
      setError(false);
    }).finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [authLoading, isAuthenticated, setTheme]);

  useEffect(() => {
    const sync = (event: StorageEvent) => { if (event.key === 'ai-lab-preferences-sync') userApi.getPreferences().then(setPreferences).catch(() => undefined); };
    const channel = typeof BroadcastChannel !== 'undefined' ? new BroadcastChannel('ai-lab-preferences') : null;
    const onMessage = () => userApi.getPreferences().then(setPreferences).catch(() => undefined);
    window.addEventListener('storage', sync); channel?.addEventListener('message', onMessage);
    return () => { window.removeEventListener('storage', sync); channel?.removeEventListener('message', onMessage); channel?.close(); if (saveTimer.current) clearTimeout(saveTimer.current); };
  }, []);

  useEffect(() => { if (!confirmOpen) return; const onKey = (event: KeyboardEvent) => { if (event.key === 'Escape') setConfirmOpen(false); if (event.key === 'Tab' && modalRef.current) { const focusable = modalRef.current.querySelectorAll<HTMLElement>('button,input'); const first = focusable[0]; const last = focusable[focusable.length - 1]; if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); } else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); } } }; document.addEventListener('keydown', onKey); return () => document.removeEventListener('keydown', onKey); }, [confirmOpen]);

  const update = (patch: UserPreferencesUpdateRequest) => {
    if (!preferences) return;
    const next = { ...preferences, ...patch };
    setPreferences(next);
    if (patch.theme) setTheme(patch.theme.toLowerCase() as 'dark' | 'light' | 'system');
    setSaveState('saving');
    pendingPatch.current = { ...pendingPatch.current, ...patch };
    if (saveTimer.current) clearTimeout(saveTimer.current);
    saveTimer.current = window.setTimeout(async () => { const patchToSave = pendingPatch.current; pendingPatch.current = {}; try { await userApi.updatePreferences(patchToSave); localStorage.setItem('ai-lab-preferences-sync', String(Date.now())); setSaveState('saved'); const channel = typeof BroadcastChannel !== 'undefined' ? new BroadcastChannel('ai-lab-preferences') : null; channel?.postMessage('updated'); channel?.close(); window.setTimeout(() => setSaveState('idle'), 1800); } catch { setSaveState('error'); } }, 450);
  };

  const sections = useMemo(() => Object.keys(t.sections) as Section[], [t]);
  const onDelete = async () => { if (!user || confirmText.trim() !== user.username) return; await userApi.deleteMe(); await logout(); router.replace('/'); };

  if (loading) return <div className="mx-auto max-w-[1180px] py-10"><div className="h-10 w-56 animate-pulse rounded bg-[var(--muted)]" /><div className="mt-8 h-72 animate-pulse rounded-2xl bg-[var(--card)]" /></div>;
  if (error || !preferences) return <div className="mx-auto max-w-[720px] py-16 text-center"><p className="text-[var(--muted-foreground)]">{t.loadError}</p><button className="mt-4 rounded-xl bg-[var(--primary)] px-5 py-2.5 text-sm font-semibold text-white" onClick={() => window.location.reload()}>{t.retry}</button></div>;

  const themeCards = [{ value: 'DARK' as const, label: t.dark, icon: Moon, bg: 'bg-[#111827]' }, { value: 'LIGHT' as const, label: t.light, icon: Sun, bg: 'bg-[#F1F3F7]' }, { value: 'SYSTEM' as const, label: t.system, icon: Laptop, bg: 'bg-gradient-to-br from-[#111827] 50%, #F1F3F7 50%' }];
  const selectClass = 'mt-2 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] px-3.5 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--primary)]';
  return <div className="mx-auto w-full max-w-[1180px] py-5 md:py-8">
    <div className="mb-7"><h1 className="text-3xl font-bold tracking-tight">{t.title}</h1><p className="mt-1 text-sm text-[var(--muted-foreground)]">{t.subtitle}</p></div>
    <div className="grid gap-6 lg:grid-cols-[220px_minmax(0,1fr)]">
      <nav aria-label="Settings sections" className="flex gap-2 overflow-x-auto rounded-2xl border border-[var(--border)] bg-[var(--card)] p-2 lg:block lg:h-fit lg:space-y-1">
        {sections.map((key) => <button key={key} type="button" onClick={() => setActive(key)} className={`flex min-h-11 shrink-0 items-center gap-3 rounded-xl px-3.5 text-left text-sm font-medium transition ${active === key ? 'bg-[var(--accent)] text-[var(--primary)]' : 'text-[var(--muted-foreground)] hover:bg-[var(--muted)] hover:text-[var(--foreground)]'}`}><ChevronRight size={15} className={active === key ? 'opacity-100' : 'opacity-0'} />{t.sections[key]}</button>)}
      </nav>
      <div className="space-y-5">
        {active === 'general' && <section className="settings-card"><h2><Globe2 />{t.sections.general}</h2><label>{t.language}<select className={selectClass} value={locale} onChange={(e) => switchLocale(e.target.value as 'en' | 'ru' | 'uz')}><option value="en">English</option><option value="ru">Русский</option><option value="uz">O‘zbekcha</option></select></label><label className="mt-5 block">{t.timezone}<input readOnly value={t.timezoneValue} className={selectClass} /></label></section>}
        {active === 'appearance' && <section className="settings-card"><h2><Palette />{t.sections.appearance}</h2><p className="mb-4 text-sm text-[var(--muted-foreground)]">{t.themeHint}</p><div className="grid gap-3 sm:grid-cols-3">{themeCards.map(({ value, label, icon: Icon, bg }) => <button key={value} type="button" onClick={() => update({ theme: value })} className={`rounded-2xl border-2 p-2 text-left transition ${preferences.theme === value ? 'border-[var(--primary)]' : 'border-[var(--border)] hover:border-[var(--primary)]/50'}`}><div className={`h-20 rounded-xl ${bg} p-3`}><div className="h-2 w-12 rounded bg-white/20" /><div className="mt-3 h-7 rounded border border-white/10 bg-black/10" /></div><div className="flex items-center gap-2 px-1 pt-3 text-sm font-semibold"><Icon size={15} />{label}{preferences.theme === value && <Check size={15} className="ml-auto text-[var(--primary)]" />}</div></button>)}</div></section>}
        {active === 'laboratory' && <section className="settings-card"><h2><SlidersHorizontal />{t.sections.laboratory}</h2><div className="grid gap-5 sm:grid-cols-3"><label>{t.temperature}<select className={selectClass} value={preferences.defaultTemperatureUnit} onChange={(e) => update({ defaultTemperatureUnit: e.target.value as UserPreferencesUpdateRequest['defaultTemperatureUnit'] })}><option value="CELSIUS">Celsius</option><option value="KELVIN">Kelvin</option><option value="FAHRENHEIT">Fahrenheit</option></select></label><label>{t.pressure}<select className={selectClass} value={preferences.defaultPressureUnit} onChange={(e) => update({ defaultPressureUnit: e.target.value as UserPreferencesUpdateRequest['defaultPressureUnit'] })}><option value="ATMOSPHERE">Atmosphere</option><option value="BAR">Bar</option><option value="PASCAL">Pascal</option></select></label><label>{t.volume}<select className={selectClass} value={preferences.defaultVolumeUnit} onChange={(e) => update({ defaultVolumeUnit: e.target.value as UserPreferencesUpdateRequest['defaultVolumeUnit'] })}><option value="MILLILITER">mL</option><option value="LITER">L</option><option value="CUBIC_METER">m³</option></select></label></div><div className="mt-6 flex items-center justify-between rounded-xl border border-[var(--border)] bg-[var(--background)] p-4"><div><p className="text-sm font-semibold">{t.autoSave}</p><p className="mt-1 text-xs text-[var(--muted-foreground)]">{t.autoSaveHint}</p></div><button type="button" role="switch" aria-checked={preferences.autoSaveEnabled} onClick={() => update({ autoSaveEnabled: !preferences.autoSaveEnabled })} className={`relative h-7 w-12 rounded-full transition ${preferences.autoSaveEnabled ? 'bg-[var(--primary)]' : 'bg-[var(--border)]'}`}><span className={`absolute top-1 h-5 w-5 rounded-full bg-white transition ${preferences.autoSaveEnabled ? 'left-6' : 'left-1'}`} /></button></div><SaveState state={saveState} t={t} /></section>}
        {active === 'account' && <section className="settings-card"><h2><UserRound />{t.account}</h2><InfoRow label={t.username} value={user?.username || '—'} /><InfoRow label={t.email} value={user?.email || '—'} /><InfoRow label={t.role} value={user?.role === 'ROLE_ADMIN' ? 'Administrator' : 'Researcher'} /></section>}
        {active === 'security' && <><section className="settings-card"><h2><Shield />{t.security}</h2><InfoRow label={t.status} value={t.active} /><button type="button" onClick={async () => { await logout(); router.replace('/'); }} className="mt-5 inline-flex min-h-11 items-center gap-2 rounded-xl border border-[var(--border)] px-4 text-sm font-semibold hover:border-[var(--primary)]"><LogOut size={16} />{t.logout}</button></section><section className="settings-card border-[#F43F5E]/30"><h2 className="!text-[#F43F5E]"><AlertTriangle />{t.danger}</h2><p className="text-sm text-[var(--muted-foreground)]">{t.dangerHint}</p><button type="button" onClick={() => setConfirmOpen(true)} className="mt-5 rounded-xl border border-[#F43F5E]/40 bg-[#F43F5E]/10 px-4 py-2.5 text-sm font-semibold text-[#F43F5E]">{t.delete}</button></section></>}
      </div>
    </div>
    {saveState !== 'idle' && active !== 'laboratory' && <div className="fixed bottom-5 right-5 rounded-xl border border-[var(--border)] bg-[var(--card)] px-4 py-3 text-sm shadow-xl">{saveState === 'saving' ? <><Loader2 size={15} className="mr-2 inline animate-spin" />{t.saving}</> : saveState === 'saved' ? <span className="text-[#14F195]">✓ {t.saved}</span> : <span className="text-[#F43F5E]">{t.saveError}</span>}</div>}
    {confirmOpen && <div className="fixed inset-0 z-[100] grid place-items-center bg-black/60 p-5" role="dialog" aria-modal="true"><div ref={modalRef} className="w-full max-w-md rounded-2xl border border-[var(--border)] bg-[var(--card)] p-6 shadow-2xl"><div className="flex items-start justify-between"><h2 className="text-xl font-bold">{t.deleteTitle}</h2><button aria-label="Close" onClick={() => setConfirmOpen(false)}><X size={18} /></button></div><p className="mt-3 text-sm text-[var(--muted-foreground)]">{t.deleteBody}</p><input autoFocus value={confirmText} onChange={(e) => setConfirmText(e.target.value)} className={selectClass} placeholder={user?.username} /><div className="mt-6 flex justify-end gap-3"><button onClick={() => setConfirmOpen(false)} className="min-h-11 rounded-xl border border-[var(--border)] px-4 py-2.5 text-sm">{t.cancel}</button><button disabled={confirmText !== user?.username} onClick={onDelete} className="min-h-11 rounded-xl bg-[#F43F5E] px-4 py-2.5 text-sm font-semibold text-white disabled:opacity-40">{t.confirm}</button></div></div></div>}
  </div>;
}

function InfoRow({ label, value }: { label: string; value: string }) { return <div className="flex items-center justify-between gap-4 border-b border-[var(--border)] py-4 last:border-0"><span className="text-sm text-[var(--muted-foreground)]">{label}</span><span className="truncate text-right text-sm font-semibold">{value}</span></div>; }
function SaveState({ state, t }: { state: 'idle' | 'saving' | 'saved' | 'error'; t: Copy }) { if (state === 'idle') return null; return <p className={`mt-5 text-sm ${state === 'error' ? 'text-[#F43F5E]' : 'text-[#14F195]'}`}>{state === 'saving' ? <><Loader2 size={15} className="mr-2 inline animate-spin" />{t.saving}</> : state === 'saved' ? `✓ ${t.saved}` : t.saveError}</p>; }
