'use client';

import { AlertTriangle } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { FieldHelp, FormSection } from '@/widgets/admin/editor';
import { hasEquipmentRenderer, listEquipmentRenderers } from '@/entities/equipment/ui/EquipmentRendererRegistry';
import type { EquipmentCategory, EquipmentKind } from '@/shared/types/catalog';
import type { EquipmentDraft } from './equipmentEditor.types';

const categories: EquipmentCategory[] = ['CONTAINER', 'HEATER', 'SENSOR', 'APPARATUS', 'TOOL'];
const kinds: EquipmentKind[] = ['ANALYTICAL_BALANCE','LABORATORY_BALANCE','THERMOMETER','TEMPERATURE_PROBE','VOLUMETRIC_FLASK','GRADUATED_CYLINDER','VOLUMETRIC_PIPETTE','BURETTE','PH_METER','MAGNETIC_STIRRER','HOT_PLATE'];
const input = 'mt-1.5 w-full rounded-lg border border-white/10 bg-[#070b13] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400';

export function EquipmentDetails({ draft, onChange }: { draft: EquipmentDraft; onChange: (patch: Partial<EquipmentDraft>) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const rendererSupported = hasEquipmentRenderer(draft.rendererKey);
  return <FormSection title={t('details')} description={t('detailsHelp')}><div className="grid gap-5 md:grid-cols-2">
    <label><span>{t('code')} *</span><input id="equipment-code" className={input} value={draft.code} onChange={event => onChange({ code: event.target.value })}/><FieldHelp>{t('codeHelp')}</FieldHelp></label>
    <label><span>{t('internalName')}</span><input className={input} value={draft.internalName} onChange={event => onChange({ internalName: event.target.value })}/><FieldHelp>{t('internalNameHelp')}</FieldHelp></label>
    <label><span>{t('category')} *</span><select id="equipment-category" className={input} value={draft.category} onChange={event => onChange({ category: event.target.value as EquipmentCategory })}>{categories.map(value => <option key={value}>{value}</option>)}</select></label>
    <label><span>{t('kind')}</span><select className={input} value={draft.kind} onChange={event => onChange({ kind: event.target.value as EquipmentKind | '' })}><option value="">{t('notSpecified')}</option>{kinds.map(value => <option key={value}>{value}</option>)}</select></label>
    <label><span>{t('renderer')} *</span><select id="equipment-renderer" className={input} value={rendererSupported ? draft.rendererKey : ''} onChange={event => onChange({ rendererKey: event.target.value })}><option value="" disabled>{rendererSupported ? t('selectRenderer') : draft.rendererKey}</option>{listEquipmentRenderers().map(renderer => <option key={renderer.key} value={renderer.key}>{renderer.label} · {renderer.key}</option>)}</select>{!rendererSupported && <p role="alert" className="mt-2 flex items-center gap-1.5 text-xs text-amber-300"><AlertTriangle size={13}/>{t('rendererUnavailable', { key: draft.rendererKey })}</p>}</label>
    <label><span>{t('tags')}</span><input className={input} value={draft.tags.join(', ')} onChange={event => onChange({ tags: event.target.value.split(',').map(item => item.trim()).filter(Boolean) })} placeholder={t('tagsPlaceholder')}/></label>
    <div className="rounded-xl border border-white/[.06] bg-white/[.025] p-4"><p className="text-xs uppercase tracking-wide text-slate-500">{t('status')}</p><p className="mt-1 font-semibold text-white">{draft.status}</p></div>
    <div className="rounded-xl border border-white/[.06] bg-white/[.025] p-4"><p className="text-xs uppercase tracking-wide text-slate-500">{t('revision')}</p><p className="mt-1 font-semibold text-white">{draft.version ?? '—'}</p></div>
  </div></FormSection>;
}
