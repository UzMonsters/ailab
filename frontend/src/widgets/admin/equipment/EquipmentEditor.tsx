'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';
import { BadgeCheck, Braces, Cable, Eye, Gauge, Image, Languages, Link as LinkIcon, Loader2, PanelLeftClose, PanelLeftOpen, SlidersHorizontal, Sparkles } from 'lucide-react';
import { useLocale, useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale } from '@/shared/types/catalog';
import { errorMessage } from '@/shared/utils/errorMessage';
import { useToastStore } from '@/stores/toast.store';
import { AdminEditorActions, AdminEditorHeader, AdminEditorShell, AdminEditorTabs, AdminErrorState, FormSection, type EditorSaveState } from '@/widgets/admin/editor';
import { EquipmentCapabilities } from './EquipmentCapabilities';
import { EquipmentDetails } from './EquipmentDetails';
import { EquipmentLinks } from './EquipmentLinks';
import { EquipmentLocalization } from './EquipmentLocalization';
import { EquipmentMedia } from './EquipmentMedia';
import { EquipmentPorts } from './EquipmentPorts';
import { EquipmentSandboxPreview } from './EquipmentSandboxPreview';
import { EquipmentTechnicalSpecs } from './EquipmentTechnicalSpecs';
import { EquipmentValidation } from './EquipmentValidation';
import { createEquipmentDraft, equipmentPayload, validateEquipment } from './equipmentEditor.model';
import type { EquipmentDraft, EquipmentValidationIssue } from './equipmentEditor.types';

const tabs = ['details','localization','media','capabilities','ports','technicalSpecs','links','preview','validation','advanced'] as const;
type Tab = typeof tabs[number];

export default function EquipmentEditor({ id }: { id?: string }) {
  const t = useTranslations('admin.equipmentEditor');
  const locale = useLocale();
  const router = useRouter();
  const addToast = useToastStore(state => state.addToast);
  const [draft, setDraft] = useState<EquipmentDraft>(() => createEquipmentDraft());
  const [baseline, setBaseline] = useState(() => JSON.stringify(equipmentPayload(createEquipmentDraft())));
  const [activeTab, setActiveTab] = useState<Tab>('details');
  const [loading, setLoading] = useState(Boolean(id));
  const [saveState, setSaveState] = useState<EditorSaveState>(id ? 'saved' : 'dirty');
  const [error, setError] = useState('');
  const [dirtyLocales, setDirtyLocales] = useState<Locale[]>([]);
  const [navCollapsed, setNavCollapsed] = useState(false);
  const issues = useMemo(() => validateEquipment(draft), [draft]);
  const dirty = JSON.stringify(equipmentPayload(draft)) !== baseline;
  const effectiveSaveState: EditorSaveState = saveState === 'saving' || saveState === 'failed' ? saveState : dirty ? 'dirty' : 'saved';

  useEffect(() => { if (!id) return; let active = true; void adminPlatformApi.equipment.get(id).then(record => { if (!active) return; const next = createEquipmentDraft(record); setDraft(next); setBaseline(JSON.stringify(equipmentPayload(next))); setSaveState('saved'); }).catch(reason => { if (active) setError(errorMessage(reason, t('loadFailed'))); }).finally(() => { if (active) setLoading(false); }); return () => { active = false; }; }, [id, t]);
  useEffect(() => { const prevent = (event: BeforeUnloadEvent) => { if (!dirty) return; event.preventDefault(); event.returnValue = ''; }; window.addEventListener('beforeunload', prevent); return () => window.removeEventListener('beforeunload', prevent); }, [dirty]);
  useEffect(() => { const protectNavigation = (event: MouseEvent) => { if (!dirty || event.defaultPrevented || event.button !== 0) return; const anchor = (event.target as Element | null)?.closest('a[href]') as HTMLAnchorElement | null; if (!anchor || anchor.target === '_blank' || anchor.href === window.location.href) return; if (!window.confirm(t('discardChanges'))) event.preventDefault(); }; document.addEventListener('click', protectNavigation, true); return () => document.removeEventListener('click', protectNavigation, true); }, [dirty, t]);

  const change = useCallback((patch: Partial<EquipmentDraft>) => setDraft(current => ({ ...current, ...patch })), []);
  const save = useCallback(async (): Promise<JsonObject | null> => {
    setSaveState('saving'); setError('');
    try {
      const result = id ? await adminPlatformApi.equipment.patch(id, equipmentPayload(draft)) : await adminPlatformApi.equipment.create(equipmentPayload(draft));
      const next = createEquipmentDraft(result);
      setDraft(next); setBaseline(JSON.stringify(equipmentPayload(next))); setDirtyLocales([]); setSaveState('saved');
      addToast({ type: 'success', title: t(id ? 'changesSaved' : 'draftCreated') });
      if (!id) { const createdId = String(result.id ?? result.code ?? ''); if (createdId) router.replace(`/${locale}/admin/equipment/${createdId}`); }
      return result;
    } catch (reason) { const message = errorMessage(reason, t('saveFailed')); setError(message); setSaveState('failed'); addToast({ type: 'error', title: t('saveFailed'), message }); return null; }
  }, [addToast, draft, id, locale, router, t]);
  const validate = () => { setActiveTab('validation'); addToast({ type: issues.some(issue => issue.severity === 'error') ? 'error' : issues.length ? 'warning' : 'success', title: issues.length ? t('validationFound', { count: issues.length }) : t('validationPassed') }); };
  const publish = async () => {
    const blocking = validateEquipment(draft).filter(issue => issue.severity === 'error');
    if (blocking.length) { setActiveTab('validation'); addToast({ type: 'error', title: t('publishBlocked'), message: t('fixErrors', { count: blocking.length }) }); return; }
    const saved = dirty ? await save() : ({ version: draft.version } as JsonObject);
    if (!saved || !id) return;
    setSaveState('saving');
    try { const result = await adminPlatformApi.equipment.publish(id, { version: saved.version ?? draft.version, idempotencyKey: crypto.randomUUID() }); setDraft(current => ({ ...current, status: 'PUBLISHED', publishedVersion: Number(result.publishedVersion ?? current.publishedVersion) })); setSaveState('saved'); addToast({ type: 'success', title: t('published') }); }
    catch (reason) { const message = errorMessage(reason, t('publishFailed')); setSaveState('failed'); setError(message); addToast({ type: 'error', title: t('publishFailed'), message }); }
  };
  const openIssue = (issue: EquipmentValidationIssue) => { setActiveTab(issue.tab as Tab); window.requestAnimationFrame(() => document.getElementById(issue.id)?.focus()); };

  const action = <AdminEditorActions busy={effectiveSaveState === 'saving'} canPublish={Boolean(id)} onPreview={() => setActiveTab('preview')} onValidate={validate} onSave={() => void save()} onPublish={id ? () => void publish() : undefined}/>;
  const tabItems = tabs.map(tab => ({ id: tab, label: t(`tabs.${tab}`), badge: tab === 'validation' && issues.length ? <span className="ml-2 rounded-full bg-amber-400/15 px-1.5 text-[10px] text-amber-300">{issues.length}</span> : undefined }));
  const header = <AdminEditorHeader title={draft.translations[locale as Locale]?.name || draft.internalName || t(id ? 'editEquipment' : 'newEquipment')} code={draft.code ? `Equipment / ${draft.code}` : 'Equipment'} status={draft.status} revision={draft.version} dirtyState={effectiveSaveState} breadcrumbs={[{ label: t('equipment'), href: `/${locale}/admin/equipment` }, { label: draft.code || t('newEquipment') }]} actions={action}/>;
  const navIcons = { details: SlidersHorizontal, localization: Languages, media: Image, capabilities: Sparkles, ports: Cable, technicalSpecs: Gauge, links: LinkIcon, preview: Eye, validation: BadgeCheck, advanced: Braces } as const;
  const sidebar = <nav aria-label="Equipment editor sections" className={`sticky top-28 space-y-1 rounded-2xl border border-white/[.07] bg-[#0b101a] p-2 ${navCollapsed ? 'w-14' : ''}`}><button type="button" onClick={() => setNavCollapsed(value => !value)} className="mb-1 grid h-9 w-full place-items-center rounded-lg text-slate-400 hover:bg-white/[.05]" aria-label={navCollapsed ? 'Expand editor navigation' : 'Collapse editor navigation'}>{navCollapsed ? <PanelLeftOpen size={16}/> : <PanelLeftClose size={16}/>}</button>{tabItems.map(item => { const Icon = navIcons[item.id as keyof typeof navIcons]; return <button key={item.id} type="button" onClick={() => setActiveTab(item.id as Tab)} className={`flex w-full items-center gap-2 rounded-xl px-3 py-2.5 text-left text-sm transition ${activeTab === item.id ? 'bg-violet-500/15 text-violet-200 ring-1 ring-violet-400/30' : 'text-slate-400 hover:bg-white/[.04] hover:text-white'}`} title={item.label}><Icon size={16}/>{!navCollapsed && <span className="min-w-0 flex-1 truncate">{item.label}</span>}{!navCollapsed && item.badge}</button>; })}</nav>;

  if (loading) return <div className="grid min-h-[60vh] place-items-center"><Loader2 className="animate-spin text-violet-400"/></div>;
  return <AdminEditorShell header={header} tabs={<div className="xl:hidden"><AdminEditorTabs tabs={tabItems} active={activeTab} onChange={tab => setActiveTab(tab as Tab)}/></div>} sidebar={sidebar} preview={<EquipmentSandboxPreview draft={draft}/>}> 
    {error && <AdminErrorState details={error} title={saveState === 'failed' ? 'Could not save changes' : 'Could not complete request'} onRetry={() => id ? window.location.reload() : void save()}/>} 
    {activeTab === 'details' && <EquipmentDetails draft={draft} onChange={change}/>} 
    {activeTab === 'localization' && <EquipmentLocalization draft={draft} dirtyLocales={dirtyLocales} onChange={(contentLocale, value) => { setDirtyLocales(current => current.includes(contentLocale) ? current : [...current, contentLocale]); change({ translations: { ...draft.translations, [contentLocale]: value } }); }}/>} 
    {activeTab === 'media' && <EquipmentMedia value={draft.media} onChange={media => change({ media })} onError={message => { setError(message); addToast({ type: 'error', title: t('uploadFailed'), message }); }}/>} 
    {activeTab === 'capabilities' && <EquipmentCapabilities value={draft.capabilities} onChange={capabilities => change({ capabilities })}/>} 
    {activeTab === 'ports' && <EquipmentPorts draft={draft} onChange={ports => change({ ports })}/>} 
    {activeTab === 'technicalSpecs' && <EquipmentTechnicalSpecs draft={draft} onChange={limits => change({ limits })}/>} 
    {activeTab === 'links' && <EquipmentLinks value={draft.links} onChange={links => change({ links })}/>} 
    {activeTab === 'preview' && <EquipmentSandboxPreview draft={draft}/>} 
    {activeTab === 'validation' && <EquipmentValidation issues={issues} onOpenIssue={openIssue}/>} 
    {activeTab === 'advanced' && <FormSection title={t('rawBackendRecord')} description={t('rawBackendHelp')}><pre className="max-h-[620px] overflow-auto rounded-xl border border-white/[.06] bg-black/30 p-4 text-xs leading-5 text-slate-300">{JSON.stringify(equipmentPayload(draft), null, 2)}</pre></FormSection>}
  </AdminEditorShell>;
}
