'use client';

import Link from 'next/link';
import type { ReactNode } from 'react';
import { AlertCircle, Check, ChevronRight, CircleAlert, Eye, Loader2, Save, Send } from 'lucide-react';
import type { EntityStatus } from '@/shared/types/catalog';

export type EditorSaveState = 'saved' | 'dirty' | 'saving' | 'failed';

const statusTone: Record<string, string> = {
  DRAFT: 'border-amber-400/25 bg-amber-400/10 text-amber-300',
  IN_REVIEW: 'border-cyan-400/25 bg-cyan-400/10 text-cyan-300',
  PUBLISHED: 'border-emerald-400/25 bg-emerald-400/10 text-emerald-300',
  ARCHIVED: 'border-slate-400/25 bg-slate-400/10 text-slate-300',
};

export function StatusBadge({ status }: { status: EntityStatus | string }) {
  return <span className={`rounded-full border px-2.5 py-1 text-[11px] font-bold tracking-wide ${statusTone[status] ?? statusTone.ARCHIVED}`}>{status.replaceAll('_', ' ')}</span>;
}

export function ValidationBadge({ valid, label }: { valid: boolean; label?: string }) {
  return <span className={`inline-flex items-center gap-1 rounded-full border px-2 py-1 text-xs ${valid ? 'border-emerald-400/25 text-emerald-300' : 'border-amber-400/25 text-amber-300'}`}>{valid ? <Check size={12}/> : <CircleAlert size={12}/>} {label ?? (valid ? 'Valid' : 'Needs attention')}</span>;
}

export function EntityBreadcrumb({ items }: { items: Array<{ label: string; href?: string }> }) {
  return <nav aria-label="Breadcrumb" className="flex flex-wrap items-center gap-1 text-xs text-slate-500">{items.map((item, index) => <span key={`${item.label}-${index}`} className="inline-flex items-center gap-1">{index > 0 && <ChevronRight size={12}/>} {item.href ? <Link href={item.href} className="hover:text-violet-300">{item.label}</Link> : <span>{item.label}</span>}</span>)}</nav>;
}

export function AdminEditorDirtyState({ state }: { state: EditorSaveState }) {
  const copy = { saved: 'Saved', dirty: 'Unsaved changes', saving: 'Saving…', failed: 'Save failed' }[state];
  const tone = state === 'failed' ? 'text-rose-300' : state === 'dirty' ? 'text-amber-300' : state === 'saved' ? 'text-emerald-300' : 'text-slate-300';
  return <span role="status" className={`inline-flex items-center gap-1.5 text-xs ${tone}`}>{state === 'saving' ? <Loader2 size={12} className="animate-spin"/> : state === 'failed' ? <AlertCircle size={12}/> : <span className="h-1.5 w-1.5 rounded-full bg-current"/>}{copy}</span>;
}

export function AdminEditorActions({ busy, canPublish = true, canValidate = true, onPreview, onValidate, onSave, onPublish }: { busy?: boolean; canPublish?: boolean; canValidate?: boolean; onPreview?: () => void; onValidate?: () => void; onSave: () => void; onPublish?: () => void }) {
  const secondary = 'inline-flex items-center gap-2 rounded-lg border border-white/10 bg-white/[.025] px-3 py-2 text-sm font-semibold text-slate-200 transition hover:bg-white/[.06] disabled:opacity-40';
  return <div className="flex flex-wrap items-center justify-end gap-2">{onPreview && <button type="button" className={secondary} onClick={onPreview}><Eye size={15}/>Preview</button>}{canValidate && onValidate && <button type="button" className={secondary} onClick={onValidate}><CircleAlert size={15}/>Validate</button>}<button type="button" disabled={busy} onClick={onSave} className={secondary}>{busy ? <Loader2 size={15} className="animate-spin"/> : <Save size={15}/>}Save draft</button>{canPublish && onPublish && <button type="button" disabled={busy} onClick={onPublish} className="inline-flex items-center gap-2 rounded-lg bg-violet-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-violet-500 disabled:opacity-40"><Send size={15}/>Publish</button>}</div>;
}

export function AdminEditorHeader({ title, code, status = 'DRAFT', revision, dirtyState = 'saved', breadcrumbs, actions }: { title: string; code?: string; status?: EntityStatus | string; revision?: number | string; dirtyState?: EditorSaveState; breadcrumbs?: Array<{ label: string; href?: string }>; actions?: ReactNode }) {
  return <header className="admin-entity-header sticky top-0 z-20 -mx-4 border-b border-white/[.08] bg-[#090d16]/95 px-4 py-4 shadow-xl shadow-black/10 backdrop-blur md:-mx-8 md:px-8">{breadcrumbs && <EntityBreadcrumb items={breadcrumbs}/>}<div className="mt-2 flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between"><div className="min-w-0"><div className="flex flex-wrap items-center gap-2"><h1 className="truncate text-xl font-bold text-white">{title}</h1><StatusBadge status={status}/>{revision != null && <span className="text-xs text-slate-500">Revision {revision}</span>}</div>{code && <p className="mt-1 text-xs text-slate-400">{code}</p>}<div className="mt-2"><AdminEditorDirtyState state={dirtyState}/></div></div>{actions}</div></header>;
}

export type AdminEditorTab = { id: string; label: string; badge?: ReactNode; disabled?: boolean };
export function AdminEditorTabs({ tabs, active, onChange }: { tabs: AdminEditorTab[]; active: string; onChange: (id: string) => void }) {
  return <div role="tablist" aria-label="Editor sections" className="flex gap-1 overflow-x-auto border-b border-white/[.08]">{tabs.map(tab => <button key={tab.id} type="button" role="tab" aria-selected={active === tab.id} disabled={tab.disabled} onClick={() => onChange(tab.id)} className={`whitespace-nowrap border-b-2 px-3 py-3 text-sm font-semibold transition disabled:opacity-40 ${active === tab.id ? 'border-violet-400 text-white' : 'border-transparent text-slate-400 hover:text-white'}`}>{tab.label}{tab.badge}</button>)}</div>;
}

export function FormSection({ title, description, children, actions }: { title: string; description?: string; children: ReactNode; actions?: ReactNode }) {
  return <section className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5"><div className="mb-5 flex items-start justify-between gap-4"><div><h2 className="font-semibold text-white">{title}</h2>{description && <p className="mt-1 max-w-3xl text-xs leading-5 text-slate-500">{description}</p>}</div>{actions}</div>{children}</section>;
}

export function FieldHelp({ children }: { children: ReactNode }) {
  return <p className="mt-1.5 text-xs leading-5 text-slate-500">{children}</p>;
}

export function AdminEditorShell({ header, tabs, sidebar, preview, children }: { header: ReactNode; tabs?: ReactNode; sidebar?: ReactNode; preview?: ReactNode; children: ReactNode }) {
  return <div className="pb-16">{header}{tabs && <div className="mt-4">{tabs}</div>}{sidebar || preview ? <div className="mt-6 grid gap-6 xl:grid-cols-[190px_minmax(0,1fr)_320px]"><aside className="hidden xl:block">{sidebar}</aside><main className="min-w-0 space-y-6">{children}</main><aside className="hidden xl:block">{preview}</aside></div> : <div className="mt-6 space-y-6">{children}</div>}</div>;
}
