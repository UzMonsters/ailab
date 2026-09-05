'use client';

import { useState } from 'react';
import { useTranslations } from 'next-intl';
import type { Locale } from '@/shared/types/catalog';
import { FormSection } from '@/widgets/admin/editor';
import { LocalizedFieldGroup, LocalizedTabs } from '@/widgets/admin/localization';
import type { EquipmentDraft, EquipmentTranslation } from './equipmentEditor.types';

const input = 'mt-1.5 w-full rounded-lg border border-white/10 bg-[#070b13] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400';
const required = ['name', 'shortDescription'] as const;

export function EquipmentLocalization({ draft, dirtyLocales, onChange }: { draft: EquipmentDraft; dirtyLocales: Locale[]; onChange: (locale: Locale, value: EquipmentTranslation) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const [locale, setLocale] = useState<Locale>('ru');
  const value = draft.translations[locale];
  const patch = (next: Partial<EquipmentTranslation>) => onChange(locale, { ...value, ...next });
  return <FormSection title={t('localization')} description={t('localizationHelp')}><LocalizedTabs value={locale} onChange={setLocale} translations={draft.translations} requiredFields={required} dirtyLocales={dirtyLocales}/><div className="mt-6"><LocalizedFieldGroup locale={locale}>
    <label><span>{t('name')} *</span><input id={`translation-${locale}-name`} className={input} value={value.name} onChange={event => patch({ name: event.target.value })}/></label>
    <label><span>{t('shortDescription')} *</span><textarea id={`translation-${locale}-short`} className={`${input} min-h-24`} value={value.shortDescription} onChange={event => patch({ shortDescription: event.target.value })}/></label>
    <label><span>{t('detailedDescription')}</span><textarea className={`${input} min-h-40`} value={value.detailedDescription} onChange={event => patch({ detailedDescription: event.target.value })}/></label>
    <div className="grid gap-4 lg:grid-cols-2"><label><span>{t('usageDescription')}</span><textarea className={`${input} min-h-32`} value={value.usageDescription} onChange={event => patch({ usageDescription: event.target.value })}/></label><label><span>{t('safetyInformation')}</span><textarea className={`${input} min-h-32`} value={value.safetyInformation} onChange={event => patch({ safetyInformation: event.target.value })}/></label></div>
    <label><span>{t('educationalNotes')}</span><textarea className={`${input} min-h-28`} value={value.educationalNotes} onChange={event => patch({ educationalNotes: event.target.value })}/></label>
  </LocalizedFieldGroup></div></FormSection>;
}
