'use client';

import Link from 'next/link';
import { useLocale } from 'next-intl';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { Loader2, Plus, RefreshCw, Search } from 'lucide-react';
import { adminLearningApi } from '@/entities/learning/api/learning.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';
import { errorMessage } from '@/shared/utils/errorMessage';
import AdminPageHeader from '../AdminPageHeader';

const difficultyColors: Record<string, string> = {
  BEGINNER: 'bg-emerald-500/10 text-emerald-300',
  INTERMEDIATE: 'bg-amber-500/10 text-amber-300',
  ADVANCED: 'bg-rose-500/10 text-rose-300',
};

const statusColors: Record<string, string> = {
  DRAFT: 'bg-amber-500/10 text-amber-300',
  PUBLISHED: 'bg-emerald-500/10 text-emerald-300',
  IN_REVIEW: 'bg-cyan-500/10 text-cyan-300',
  ARCHIVED: 'bg-slate-500/10 text-slate-300',
};

function levelTitle(row: JsonObject, locale: Locale): string {
  const translations = row.translations;
  if (translations && typeof translations === 'object' && !Array.isArray(translations)) {
    const tl = (translations as JsonObject)[locale] ?? (translations as JsonObject).en ?? (translations as JsonObject).ru;
    if (tl && typeof tl === 'object') {
      const title = (tl as JsonObject).title;
      if (typeof title === 'string' && title.trim()) return title;
    }
  }
  return `Level ${row.levelNumber ?? row.order ?? '?'}`;
}

function scenarioName(row: JsonObject): string {
  const scenario = row.scenario;
  if (scenario && typeof scenario === 'object' && !Array.isArray(scenario)) {
    const s = scenario as JsonObject;
    return String(s.name ?? s.title ?? s.code ?? '');
  }
  return String(row.scenarioId ?? '');
}

function localizationStatus(row: JsonObject): { count: number; locales: string[] } {
  const translations = row.translations;
  if (!translations || typeof translations !== 'object' || Array.isArray(translations)) {
    return { count: 0, locales: [] };
  }
  const tl = translations as JsonObject;
  const localized = CONTENT_LOCALES.filter(locale => {
    const t = tl[locale];
    return t && typeof t === 'object' && typeof (t as JsonObject).title === 'string' && ((t as JsonObject).title as string).trim();
  });
  return { count: localized.length, locales: localized };
}

export function LearningLevelList() {
  const locale = useLocale() as Locale;
  const [rows, setRows] = useState<JsonObject[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const page = await adminLearningApi.levels({ size: 100, sort: 'order,asc' });
      setRows(page.items ?? page.content ?? []);
    } catch (reason) {
      setError(errorMessage(reason, 'Unable to load levels'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(() => void load(), 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const filtered = useMemo(() => {
    if (!query.trim()) return rows;
    const q = query.toLowerCase();
    return rows.filter(row => levelTitle(row, locale).toLowerCase().includes(q) || String(row.trackId ?? '').toLowerCase().includes(q));
  }, [rows, query, locale]);

  if (loading) {
    return (
      <div className="grid min-h-[40vh] place-items-center">
        <Loader2 className="animate-spin text-violet-400" />
      </div>
    );
  }

  return (
    <div className="space-y-6 pb-12">
      <AdminPageHeader
        title="Learning Levels"
        description="Versioned levels, validation and publishing from the learning backend."
        counters={[
          { label: 'Total', value: rows.length },
          { label: 'Visible', value: filtered.length },
        ]}
        actions={
          <div className="flex gap-2">
            <Link
              href={`/${locale}/admin/learning/levels/new`}
              className="flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white"
            >
              <Plus size={16} /> Create
            </Link>
            <button
              onClick={() => void load()}
              className="grid h-10 w-10 place-items-center rounded-lg border border-white/10 bg-[#141b2a] text-white"
              aria-label="Refresh"
            >
              <RefreshCw size={16} />
            </button>
          </div>
        }
      />

      {error && (
        <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-200">
          {error}
          <button onClick={() => void load()} className="ml-2 underline">Retry</button>
        </div>
      )}

      <div className="flex items-center gap-3">
        <label className="relative flex-1 max-w-sm">
          <Search className="pointer-events-none absolute left-3 top-3 text-slate-500" size={15} />
          <input
            value={query}
            onChange={e => setQuery(e.target.value)}
            className="w-full rounded-xl border border-white/10 bg-[#0b101a] py-2.5 pl-9 pr-3 text-sm text-white outline-none focus:border-violet-400"
            placeholder="Search levels..."
          />
        </label>
      </div>

      <div className="overflow-x-auto rounded-xl border border-white/10">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-white/10 text-left text-xs text-slate-500">
              <th className="px-4 py-3 font-semibold">Level</th>
              <th className="px-4 py-3 font-semibold">Title</th>
              <th className="px-4 py-3 font-semibold">Track</th>
              <th className="px-4 py-3 font-semibold">Scenario</th>
              <th className="px-4 py-3 font-semibold">Difficulty</th>
              <th className="px-4 py-3 font-semibold">Duration</th>
              <th className="px-4 py-3 font-semibold">Localization</th>
              <th className="px-4 py-3 font-semibold">Status</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(row => {
              const id = String(row.id ?? '');
              const loc = localizationStatus(row);
              const difficulty = String(row.difficulty ?? 'BEGINNER');
              const status = String(row.status ?? 'DRAFT');
              return (
                <tr key={id} className="border-b border-white/5 hover:bg-white/[.02] transition-colors">
                  <td className="px-4 py-3">
                    <Link href={`/${locale}/admin/learning/levels/${id}`} className="font-semibold text-white hover:text-violet-300">
                      {String(row.levelNumber ?? row.order ?? '?')}
                    </Link>
                  </td>
                  <td className="px-4 py-3">
                    <Link href={`/${locale}/admin/learning/levels/${id}`} className="text-slate-300 hover:text-violet-300">
                      {levelTitle(row, locale)}
                    </Link>
                  </td>
                  <td className="px-4 py-3 text-slate-400">{String(row.trackId ?? '')}</td>
                  <td className="px-4 py-3 text-slate-400">{scenarioName(row) || '—'}</td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2 py-0.5 text-[10px] font-bold ${difficultyColors[difficulty] ?? 'bg-slate-500/10 text-slate-300'}`}>
                      {difficulty}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-slate-400">~{String(row.estimatedMinutes ?? '?')} min</td>
                  <td className="px-4 py-3">
                    <span className="text-xs text-slate-400">
                      {loc.count}/{CONTENT_LOCALES.length}
                    </span>
                    <span className="ml-1.5 flex gap-0.5">
                      {loc.locales.map(l => (
                        <span key={l} className="rounded bg-emerald-500/10 px-1 py-0.5 text-[9px] text-emerald-300">{l.toUpperCase()}</span>
                      ))}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`rounded-full px-2 py-0.5 text-[10px] font-bold ${statusColors[status] ?? 'bg-slate-500/10 text-slate-300'}`}>
                      {status}
                    </span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!filtered.length && (
          <div className="p-8 text-center text-sm text-slate-500">
            {rows.length ? 'No levels match your search.' : 'No levels yet.'}
          </div>
        )}
      </div>
    </div>
  );
}
