'use client';

import { useTranslations } from 'next-intl';
import type { EquipmentCapability } from '@/shared/types/catalog';
import { FormSection } from '@/widgets/admin/editor';

const groups: Array<{ key: string; values: EquipmentCapability[] }> = [
  { key: 'handling', values: ['CONTAINER','LIQUID_HOLDING','TRANSFER','FILTRATION','SWIRLABLE'] },
  { key: 'measurement', values: ['TEMPERATURE_MEASURING','PH_MEASURING','MEASURING_DISPENSE','PRECISION_DISPENSE','TITRATION','SENSOR'] },
  { key: 'thermal', values: ['HEATING_RECEPTIVE','HEAT_SOURCE','FLAME_GENERATOR','COOLING','CONDENSER'] },
  { key: 'process', values: ['MAGNETIC_STIRRER'] },
];

export function EquipmentCapabilities({ value, onChange }: { value: EquipmentCapability[]; onChange: (value: EquipmentCapability[]) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const toggle = (capability: EquipmentCapability) => onChange(value.includes(capability) ? value.filter(item => item !== capability) : [...value, capability]);
  return <FormSection title={t('capabilities')} description={t('capabilitiesHelp')}><div className="grid gap-4 lg:grid-cols-2">{groups.map(group => <fieldset key={group.key} className="rounded-xl border border-white/[.07] bg-white/[.02] p-4"><legend className="px-1 text-xs font-bold uppercase tracking-wider text-violet-300">{t(`capabilityGroups.${group.key}`)}</legend><div className="mt-2 space-y-2">{group.values.map(capability => <label key={capability} className="flex cursor-pointer items-center gap-3 rounded-lg px-2 py-2 text-sm text-slate-200 hover:bg-white/[.04]"><input type="checkbox" checked={value.includes(capability)} onChange={() => toggle(capability)} className="h-4 w-4 accent-violet-500"/><span>{t(`capabilityLabels.${capability}`)}</span><code className="ml-auto text-[10px] text-slate-600">{capability}</code></label>)}</div></fieldset>)}</div></FormSection>;
}
