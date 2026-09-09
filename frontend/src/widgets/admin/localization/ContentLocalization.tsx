'use client';

import type { ReactNode } from 'react';
import { AlertTriangle, Check } from 'lucide-react';
import { CONTENT_LOCALES, type Locale, type LocalizedContent } from '@/shared/types/catalog';

export type LocaleCompleteness = Record<Locale, { completed: number; total: number; percent: number; complete: boolean }>;

export function getLocalizationCompleteness<T extends Record<string, unknown>>(translations: LocalizedContent<T>, requiredFields: readonly (keyof T)[]): LocaleCompleteness {
  return Object.fromEntries(CONTENT_LOCALES.map(locale => {
    const content = translations[locale];
    const completed = requiredFields.filter(field => {
      const value = content?.[field];
      return typeof value === 'string' ? value.trim().length > 0 : value != null;
    }).length;
    const total = requiredFields.length;
    return [locale, { completed, total, percent: total === 0 ? 100 : Math.round((completed / total) * 100), complete: completed === total }];
  })) as LocaleCompleteness;
}

export function LocalizationStatusBadge({ locale, status, dirty }: { locale: Locale; status: LocaleCompleteness[Locale]; dirty?: boolean }) {
  return <span className={`inline-flex items-center gap-1 text-[11px] ${status.complete ? 'text-emerald-300' : 'text-amber-300'}`}><strong className="uppercase">{locale}</strong> {status.percent}% {status.complete ? <Check size={11}/> : <AlertTriangle size={11}/>} {dirty && <span aria-label="Unsaved">*</span>}</span>;
}

export function LocalizationCompleteness({ translations, requiredFields, dirtyLocales = [] }: { translations: LocalizedContent<Record<string, unknown>>; requiredFields: readonly string[]; dirtyLocales?: Locale[] }) {
  const completeness = getLocalizationCompleteness(translations, requiredFields);
  return <div className="flex flex-wrap gap-3">{CONTENT_LOCALES.map(locale => <LocalizationStatusBadge key={locale} locale={locale} status={completeness[locale]} dirty={dirtyLocales.includes(locale)}/>)}</div>;
}

export function LocalizedTabs({ value, onChange, translations, requiredFields, dirtyLocales = [] }: { value: Locale; onChange: (locale: Locale) => void; translations: LocalizedContent<Record<string, unknown>>; requiredFields: readonly string[]; dirtyLocales?: Locale[] }) {
  const completeness = getLocalizationCompleteness(translations, requiredFields);
  return <div role="tablist" aria-label="Content language" className="flex flex-wrap gap-2">{CONTENT_LOCALES.map(locale => { const selected = value === locale; return <button type="button" role="tab" aria-selected={selected} key={locale} onClick={() => onChange(locale)} className={`rounded-lg border px-3 py-2 transition ${selected ? 'border-violet-400 bg-violet-500/10' : 'border-white/10 bg-white/[.02] hover:bg-white/[.05]'}`}><LocalizationStatusBadge locale={locale} status={completeness[locale]} dirty={dirtyLocales.includes(locale)}/></button>; })}</div>;
}

export function LocalizedFieldGroup({ locale, children }: { locale: Locale; children: ReactNode }) {
  return <div role="tabpanel" aria-label={`${locale.toUpperCase()} content`} data-content-locale={locale} className="space-y-4">{children}</div>;
}
