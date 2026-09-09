'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import { ExternalLink, Plus, Trash2 } from 'lucide-react';
import { useLocale, useTranslations } from 'next-intl';
import { adminBookApi } from '@/entities/book/api/book.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale } from '@/shared/types/catalog';
import { FormSection } from '@/widgets/admin/editor';
import type { EquipmentLinks as EquipmentLinksValue } from './equipmentEditor.types';

const input = 'mt-1.5 w-full rounded-lg border border-white/10 bg-[#070b13] px-3 py-2 text-sm text-white outline-none focus:border-violet-400';
const label = (record: JsonObject) => String(record.title ?? record.name ?? record.slug ?? record.id ?? '—');

export function EquipmentLinks({ value, onChange }: { value: EquipmentLinksValue; onChange: (value: EquipmentLinksValue) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const appLocale = useLocale();
  const [books, setBooks] = useState<JsonObject[]>([]);
  const [book, setBook] = useState<JsonObject | null>(null);
  useEffect(() => { void adminBookApi.list({ size: 100 }).then(response => setBooks(response.items ?? response.content ?? [])).catch(() => setBooks([])); }, []);
  useEffect(() => { if (!value.bookId) return; let active = true; void adminBookApi.get(value.bookId).then(result => { if (active) setBook(result); }).catch(() => { if (active) setBook(null); }); return () => { active = false; }; }, [value.bookId]);
  const activeBook = String(book?.id ?? '') === value.bookId ? book : null;
  const chapters = (activeBook?.chapters as JsonObject[] | undefined) ?? [];
  const pages = ((chapters.find(chapter => String(chapter.id) === value.chapterId)?.pages as JsonObject[] | undefined) ?? []);
  return <div className="space-y-5"><FormSection title={t('bookReference')} description={t('bookReferenceHelp')}><div className="grid gap-4 md:grid-cols-3"><label><span>{t('book')}</span><select className={input} value={value.bookId} onChange={event => onChange({ ...value, bookId: event.target.value, chapterId: '', pageId: '' })}><option value="">{t('notSpecified')}</option>{books.map(item => <option value={String(item.id)} key={String(item.id)}>{label(item)}</option>)}</select></label><label><span>{t('chapter')}</span><select className={input} value={value.chapterId} disabled={!value.bookId} onChange={event => onChange({ ...value, chapterId: event.target.value, pageId: '' })}><option value="">{t('notSpecified')}</option>{chapters.map(item => <option value={String(item.id)} key={String(item.id)}>{label(item)}</option>)}</select></label><label><span>{t('page')}</span><select className={input} value={value.pageId} disabled={!value.chapterId} onChange={event => onChange({ ...value, pageId: event.target.value })}><option value="">{t('notSpecified')}</option>{pages.map(item => <option value={String(item.id)} key={String(item.id)}>{label(item)}</option>)}</select></label></div>{value.bookId && <Link href={`/${appLocale}/admin/book`} className="mt-4 inline-flex items-center gap-2 text-sm font-semibold text-violet-300">{t('openBookStudio')}<ExternalLink size={14}/></Link>}</FormSection><FormSection title={t('externalReferences')}><div className="grid gap-4 md:grid-cols-3">{(['ru','uz','en'] as Locale[]).map(locale => <label key={locale}><span>Wikipedia {locale.toUpperCase()}</span><input className={input} type="url" value={value.wikipedia[locale] ?? ''} onChange={event => onChange({ ...value, wikipedia: { ...value.wikipedia, [locale]: event.target.value } })} placeholder="https://…"/></label>)}</div><div className="mt-6 space-y-3">{value.references.map((reference, index) => <div key={reference.id} className="grid gap-2 rounded-xl border border-white/[.06] p-3 md:grid-cols-[1fr_2fr_100px_36px]"><input aria-label={t('referenceLabel')} className={input} value={reference.label} onChange={event => onChange({ ...value, references: value.references.map((item, current) => current === index ? { ...item, label: event.target.value } : item) })}/><input aria-label={t('url')} type="url" className={input} value={reference.url} onChange={event => onChange({ ...value, references: value.references.map((item, current) => current === index ? { ...item, url: event.target.value } : item) })}/><select aria-label={t('locale')} className={input} value={reference.locale} onChange={event => onChange({ ...value, references: value.references.map((item, current) => current === index ? { ...item, locale: event.target.value as Locale | '' } : item) })}><option value="">ALL</option>{(['ru','uz','en'] as Locale[]).map(locale => <option key={locale}>{locale}</option>)}</select><button type="button" aria-label={t('remove')} onClick={() => onChange({ ...value, references: value.references.filter((_, current) => current !== index) })} className="mt-1.5 grid h-10 place-items-center rounded-lg border border-rose-500/30 text-rose-300"><Trash2 size={14}/></button></div>)}<button type="button" onClick={() => onChange({ ...value, references: [...value.references, { id: crypto.randomUUID(), label: '', url: '', locale: '' }] })} className="inline-flex items-center gap-2 rounded-lg border border-white/10 px-3 py-2 text-sm"><Plus size={14}/>{t('addReference')}</button></div></FormSection></div>;
}
