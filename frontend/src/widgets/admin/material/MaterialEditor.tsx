'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';
import { AlertCircle, BadgeCheck, Braces, Eye, FlaskConical, Gauge, Languages, Link2, Loader2, Palette, ShieldAlert, Waypoints } from 'lucide-react';
import { useLocale } from 'next-intl';
import { useRouter } from 'next/navigation';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale } from '@/shared/types/catalog';
import { errorMessage } from '@/shared/utils/errorMessage';
import { useToastStore } from '@/stores/toast.store';
import { AdminEditorActions, AdminEditorHeader, AdminEditorShell, AdminEditorTabs, FormSection, type EditorSaveState } from '@/widgets/admin/editor';
import { EquipmentLinks } from '@/widgets/admin/equipment/EquipmentLinks';
import { MaterialAppearance, MaterialChemistry, MaterialDetails, MaterialLocalization, MaterialSafetySection } from './MaterialSections';
import { MaterialRelatedReactions } from './MaterialRelatedReactions';
import { MaterialSandboxPreview } from './MaterialSandboxPreview';
import { createMaterialDraft, materialPayload, validateMaterial } from './materialEditor.model';
import type { MaterialDraft, MaterialValidationIssue } from './materialEditor.types';

const tabs = ['details','localization','chemistry','appearance','safety','reactions','links','preview','validation','advanced'] as const;
type Tab = typeof tabs[number];

export default function MaterialEditor({ id }: { id?: string }) {
  const locale = useLocale() as Locale;
  const router = useRouter();
  const addToast = useToastStore(state => state.addToast);
  const [draft, setDraft] = useState<MaterialDraft>(() => createMaterialDraft());
  const [baseline, setBaseline] = useState(() => JSON.stringify(materialPayload(createMaterialDraft())));
  const [activeTab, setActiveTab] = useState<Tab>('details');
  const [loading, setLoading] = useState(Boolean(id));
  const [saveState, setSaveState] = useState<EditorSaveState>(id ? 'saved' : 'dirty');
  const [error, setError] = useState('');
  const [dirtyLocales, setDirtyLocales] = useState<Locale[]>([]);
  const issues = useMemo(() => validateMaterial(draft), [draft]);
  const dirty = JSON.stringify(materialPayload(draft)) !== baseline;
  const state = saveState === 'saving' || saveState === 'failed' ? saveState : dirty ? 'dirty' : 'saved';

  useEffect(() => {
    if (!id) return;
    let active = true;
    void adminPlatformApi.materials.get(id).then(record => {
      if (!active) return;
      const next = createMaterialDraft(record);
      setDraft(next);
      setBaseline(JSON.stringify(materialPayload(next)));
      setDirtyLocales([]);
    }).catch(reason => active && setError(errorMessage(reason, 'Unable to load material.')))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [id]);

  useEffect(() => {
    const listener = (event: BeforeUnloadEvent) => { if (dirty) { event.preventDefault(); event.returnValue = ''; } };
    addEventListener('beforeunload', listener);
    return () => removeEventListener('beforeunload', listener);
  }, [dirty]);

  const change = useCallback((patch: Partial<MaterialDraft>) => setDraft(current => ({ ...current, ...patch })), []);
  const save = useCallback(async (): Promise<JsonObject | null> => {
    setSaveState('saving');
    setError('');
    try {
      const result = id ? await adminPlatformApi.materials.patch(id, materialPayload(draft)) : await adminPlatformApi.materials.create(materialPayload(draft));
      const next = createMaterialDraft(result);
      setDraft(next);
      setBaseline(JSON.stringify(materialPayload(next)));
      setDirtyLocales([]);
      setSaveState('saved');
      addToast({ type: 'success', title: id ? 'Material saved' : 'Material draft created' });
      if (!id && (result.id ?? result.code)) router.replace(`/${locale}/admin/materials/${String(result.id ?? result.code)}`);
      return result;
    } catch (reason) {
      const message = errorMessage(reason, 'Unable to save material.');
      setError(message);
      setSaveState('failed');
      addToast({ type: 'error', title: 'Save failed', message });
      return null;
    }
  }, [addToast, draft, id, locale, router]);

  const publish = async () => {
    if (issues.some(issue => issue.severity === 'error')) { setActiveTab('validation'); return; }
    const saved = dirty ? await save() : ({ version: draft.version } as JsonObject);
    if (!saved || !id) return;
    try {
      await adminPlatformApi.materials.publish(id, { version: saved.version ?? draft.version, idempotencyKey: crypto.randomUUID() });
      setDraft(current => ({ ...current, status: 'PUBLISHED' }));
      addToast({ type: 'success', title: 'Material published' });
    } catch (reason) { setError(errorMessage(reason, 'Unable to publish material.')); }
  };
  const openIssue = (issue: MaterialValidationIssue) => { setActiveTab(issue.tab as Tab); requestAnimationFrame(() => document.getElementById(issue.id)?.focus()); };

  if (loading) return <div className="grid min-h-[60vh] place-items-center"><Loader2 className="animate-spin text-violet-400"/></div>;

  const header = <AdminEditorHeader title={draft.translations[locale]?.name || draft.internalName || (id ? 'Edit material' : 'New material')} code={draft.code ? `Material / ${draft.code}` : 'Material'} status={draft.status} revision={draft.version} dirtyState={state} breadcrumbs={[{ label: 'Materials', href: `/${locale}/admin/materials` }, { label: draft.code || 'New material' }]} actions={<AdminEditorActions busy={state === 'saving'} canPublish={Boolean(id)} onPreview={() => setActiveTab('preview')} onValidate={() => setActiveTab('validation')} onSave={() => void save()} onPublish={id ? () => void publish() : undefined}/>}/>;
  const icons = { details: FlaskConical, localization: Languages, chemistry: Gauge, appearance: Palette, safety: ShieldAlert, reactions: Waypoints, links: Link2, preview: Eye, validation: BadgeCheck, advanced: Braces } as const;
  const tabItems = tabs.map(tab => ({ id: tab, label: tab === 'reactions' ? 'Related reactions' : tab[0].toUpperCase() + tab.slice(1), badge: tab === 'validation' && issues.length ? <span className="ml-2 rounded-full bg-amber-400/15 px-1.5 text-[10px] text-amber-300">{issues.length}</span> : undefined }));
  const sidebar = <nav aria-label="Material editor sections" className="sticky top-28 space-y-1 rounded-2xl border border-white/[.07] bg-[#0b101a] p-2">{tabItems.map(item => { const Icon = icons[item.id]; return <button key={item.id} type="button" onClick={() => setActiveTab(item.id)} className={`flex w-full items-center gap-2 rounded-xl px-3 py-2.5 text-left text-sm transition ${activeTab === item.id ? 'bg-violet-500/15 text-violet-200 ring-1 ring-violet-400/30' : 'text-slate-400 hover:bg-white/[.04] hover:text-white'}`} title={item.label}><Icon size={16}/><span className="min-w-0 flex-1 truncate">{item.label}</span>{item.badge}</button>; })}</nav>;

  return <AdminEditorShell header={header} tabs={<div className="xl:hidden"><AdminEditorTabs tabs={tabItems} active={activeTab} onChange={value => setActiveTab(value as Tab)}/></div>} sidebar={sidebar} preview={<MaterialSandboxPreview draft={draft}/>}>{error && <div role="alert" className="flex gap-2 rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-200"><AlertCircle size={17}/>{error}</div>}{activeTab === 'details' && <MaterialDetails draft={draft} onChange={change}/>} {activeTab === 'localization' && <MaterialLocalization draft={draft} dirtyLocales={dirtyLocales} onChange={(contentLocale, value) => { setDirtyLocales(current => current.includes(contentLocale) ? current : [...current, contentLocale]); change({ translations: { ...draft.translations, [contentLocale]: value } }); }}/>} {activeTab === 'chemistry' && <MaterialChemistry draft={draft} onChange={change}/>} {activeTab === 'appearance' && <MaterialAppearance draft={draft} onChange={change}/>} {activeTab === 'safety' && <MaterialSafetySection value={draft.safety} onChange={safety => change({ safety })}/>} {activeTab === 'reactions' && <MaterialRelatedReactions value={draft.reactionRelationships} onChange={reactionRelationships => change({ reactionRelationships })}/>} {activeTab === 'links' && <EquipmentLinks value={draft.links} onChange={links => change({ links })}/>} {activeTab === 'preview' && <MaterialSandboxPreview draft={draft}/>} {activeTab === 'validation' && <FormSection title="Validation" description="Local contract checks before publishing."><div className="space-y-3">{issues.length ? issues.map(issue => <button key={issue.id} type="button" onClick={() => openIssue(issue)} className="block w-full rounded-xl border border-white/10 p-4 text-left text-sm text-slate-200">{issue.severity.toUpperCase()} · {issue.message}</button>) : <p className="rounded-xl border border-emerald-400/20 bg-emerald-500/10 p-4 text-emerald-200">Ready to publish.</p>}</div></FormSection>} {activeTab === 'advanced' && <FormSection title="Raw backend record" description="Backend-compatible material payload."><pre className="max-h-[620px] overflow-auto rounded-xl border border-white/[.06] bg-black/30 p-4 text-xs leading-5 text-slate-300">{JSON.stringify(materialPayload(draft), null, 2)}</pre></FormSection>}</AdminEditorShell>;
}
