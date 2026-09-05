'use client';

import { useTranslations } from 'next-intl';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { UnitInput } from '@/shared/ui/UnitInput';
import { FormSection } from '@/widgets/admin/editor';
import type { EquipmentDraft } from './equipmentEditor.types';

type Spec = { key: string; units: readonly string[]; defaultUnit: string };
const isVolume = (key: string) => key.endsWith('Ml');
const isKelvinValue = (key: string) => key.endsWith('TempK');
const isCelsiusValue = (key: string) => key.includes('TemperatureC');
const isPower = (key: string) => key.endsWith('Watts');

function fromCanonical(key: string, value: number | null, unit: string) {
  if (value == null) return null;
  if (isVolume(key) && unit === 'L') return value / 1000;
  if (isKelvinValue(key) && unit === '°C') return value - 273.15;
  if (isCelsiusValue(key) && unit === 'K') return value + 273.15;
  if (isPower(key) && unit === 'kW') return value / 1000;
  return value;
}

function toCanonical(key: string, value: number | null, unit: string) {
  if (value == null) return null;
  if (isVolume(key) && unit === 'L') return value * 1000;
  if (isKelvinValue(key) && unit === '°C') return value + 273.15;
  if (isCelsiusValue(key) && unit === 'K') return value - 273.15;
  if (isPower(key) && unit === 'kW') return value * 1000;
  return value;
}
const specs = (draft: EquipmentDraft): Spec[] => {
  if (draft.category === 'CONTAINER') return [
    { key: 'capacityMl', units: ['mL','L'], defaultUnit: 'mL' }, { key: 'graduationMl', units: ['mL','L'], defaultUnit: 'mL' },
    { key: 'minTempK', units: ['K','°C'], defaultUnit: 'K' }, { key: 'maxTempK', units: ['K','°C'], defaultUnit: 'K' },
  ];
  if (draft.kind === 'THERMOMETER' || draft.kind === 'TEMPERATURE_PROBE') return [
    { key: 'minimumTemperatureC', units: ['°C','K'], defaultUnit: '°C' }, { key: 'maximumTemperatureC', units: ['°C','K'], defaultUnit: '°C' },
    { key: 'accuracyC', units: ['°C','K'], defaultUnit: '°C' }, { key: 'resolutionC', units: ['°C','K'], defaultUnit: '°C' },
  ];
  if (draft.category === 'HEATER' || draft.kind === 'HOT_PLATE') return [
    { key: 'maxPowerWatts', units: ['W','kW'], defaultUnit: 'W' }, { key: 'minimumTemperatureC', units: ['°C','K'], defaultUnit: '°C' }, { key: 'maximumTemperatureC', units: ['°C','K'], defaultUnit: '°C' },
  ];
  if (draft.capabilities.includes('CONDENSER')) return [{ key: 'maxVaporTemperatureC', units: ['°C','K'], defaultUnit: '°C' }];
  return [{ key: 'toleranceMl', units: ['mL','L'], defaultUnit: 'mL' }];
};

export function EquipmentTechnicalSpecs({ draft, onChange }: { draft: EquipmentDraft; onChange: (limits: JsonObject) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const update = (key: string, value: number | null, unit: string, previousUnit: string) => onChange(unit !== previousUnit
    ? { ...draft.limits, [`${key}Unit`]: unit }
    : { ...draft.limits, [key]: toCanonical(key, value, unit), [`${key}Unit`]: unit });
  return <FormSection title={t('technicalSpecs')} description={t('technicalSpecsHelp')}><div className="grid gap-4 md:grid-cols-2">{specs(draft).map(spec => { const unit=typeof draft.limits[`${spec.key}Unit`] === 'string' ? draft.limits[`${spec.key}Unit`] as string : spec.defaultUnit; const canonical=typeof draft.limits[spec.key] === 'number' ? draft.limits[spec.key] as number : null; return <UnitInput key={spec.key} label={t(`specLabels.${spec.key}`)} units={spec.units} value={{ value: fromCanonical(spec.key, canonical, unit), unit }} onChange={value => update(spec.key, value.value, value.unit, unit)}/>;})}
    {draft.category === 'CONTAINER' && <label className="flex items-center gap-3 rounded-xl border border-white/[.07] p-4 text-sm text-slate-200"><input type="checkbox" checked={Boolean(draft.limits.directHeatingAllowed)} onChange={event => onChange({ ...draft.limits, directHeatingAllowed: event.target.checked })} className="h-4 w-4 accent-violet-500"/>{t('directHeatingAllowed')}</label>}
    {draft.capabilities.includes('CONDENSER') && <><label><span>{t('coolingMedium')}</span><input value={String(draft.limits.coolingMedium ?? '')} onChange={event => onChange({ ...draft.limits, coolingMedium: event.target.value })} className="mt-1.5 w-full rounded-lg border border-white/10 bg-[#070b13] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400"/></label><label className="flex items-center gap-3 rounded-xl border border-white/[.07] p-4 text-sm text-slate-200"><input type="checkbox" checked={Boolean(draft.limits.requiresCooling)} onChange={event => onChange({ ...draft.limits, requiresCooling: event.target.checked })} className="h-4 w-4 accent-violet-500"/>{t('requiresCooling')}</label></>}
  </div></FormSection>;
}
