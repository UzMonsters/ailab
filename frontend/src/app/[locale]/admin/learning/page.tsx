'use client';

import Link from 'next/link';
import { useLocale } from 'next-intl';
import { useCallback, useEffect, useState, type ComponentType } from 'react';
import { AlertCircle, BookOpen, ChevronLeft, ChevronRight, Languages, Loader2, RefreshCw, Route, Search, Users } from 'lucide-react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { adminLearningApi } from '@/entities/learning/api/learning.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { errorMessage } from '@/shared/utils/errorMessage';

const records = (value: unknown): JsonObject[] => {
  if (Array.isArray(value)) return value.filter((item): item is JsonObject => Boolean(item) && typeof item === 'object' && !Array.isArray(item));
  if (value && typeof value === 'object') {
    const source = value as JsonObject;
    return records(source.items ?? source.content ?? []);
  }
  return [];
};
const label = (item: JsonObject, key: string, fallback = '—') => String(item[key] ?? fallback);

function Kpi({ icon: Icon, label, value }: { icon: ComponentType<{ className?: string; size?: number }>; label: string; value: unknown }) {
  return (
    <article className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5">
      <Icon className="text-violet-300" size={19} />
      <p className="mt-4 text-xs text-[#8490a3]">{label}</p>
      <p className="mt-2 text-3xl font-bold text-white">{String(value)}</p>
    </article>
  );
}

export default function Page() {
  const locale = useLocale();
  const [data, setData] = useState<JsonObject>({});
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const [overview, tracks, progress, localization] = await Promise.all([
      adminLearningApi.overview(),
      adminLearningApi.tracks({ size: 100 }),
      adminLearningApi.progress({ size: 10, page, q: query || undefined }),
      adminLearningApi.localization({ locale, size: 10 }),
      ]);
      setData({ overview, tracks, progress, localization });
    } catch (reason) { setError(errorMessage(reason, 'Learning data could not be loaded.')); }
    finally { setLoading(false); }
  }, [locale, page, query]);
  useEffect(() => { const timer=window.setTimeout(()=>void load(),250); return()=>window.clearTimeout(timer); }, [load]);

  const overview = data.overview as JsonObject | undefined;
  const levels = overview?.levels as JsonObject | undefined;
  const tracks = records(data.tracks);
  const localization = records(data.localization);
  const progress = records(data.progress);
  const progressEnvelope = data.progress as JsonObject | undefined;
  const progressPage = progressEnvelope?.page;
  const nestedTotalPages = progressPage && typeof progressPage === 'object' && !Array.isArray(progressPage) ? (progressPage as JsonObject).totalPages : undefined;
  const totalPages = Number(progressEnvelope?.totalPages ?? nestedTotalPages ?? 1);

  return (
    <div className="space-y-6 pb-12">
      <AdminPageHeader
        title="Learning"
        description="Tracks, attempts, progress and localization from the learning backend."
        actions={<Link href={`/${locale}/admin/learning/levels`} className="flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white"><Route size={16} />Manage levels</Link>}
      />
      {error ? (
        <div className="flex items-center gap-3 rounded-xl border border-red-500/20 bg-red-500/10 p-4 text-red-300"><AlertCircle size={17} /><span className="flex-1">{error}</span><button onClick={()=>void load()} className="flex items-center gap-1 rounded-lg border border-red-300/20 px-3 py-1.5 text-xs"><RefreshCw size={14}/>Retry</button></div>
      ) : loading && !overview ? (
        <div className="grid min-h-64 place-items-center"><Loader2 className="animate-spin text-violet-400" /></div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-6">
            <Kpi icon={BookOpen} label="Published levels" value={levels?.published ?? levels?.PUBLISHED ?? 0} />
            <Kpi icon={BookOpen} label="Draft levels" value={levels?.draft ?? levels?.DRAFT ?? 0} />
            <Kpi icon={Users} label="Attempts" value={overview.attempts ?? 0} />
            <Kpi icon={Route} label="Completed" value={`${Number(overview.completionRate ?? 0).toFixed(1)}%`} />
            <Kpi icon={Languages} label="Hints used" value={overview.hintUsage ?? 0} />
            <Kpi icon={Route} label="Average duration" value={`${Number(overview.averageDurationSeconds ?? 0).toFixed(0)} s`} />
          </div>
          <section className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5"><div className="flex items-center justify-between"><h2 className="font-semibold text-white">Localization completeness</h2><span className="text-xs uppercase text-[#8490a3]">{locale}</span></div><div className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-3">{localization.map(item=><article key={label(item,'entityId')} className="rounded-xl border border-white/[.06] bg-white/[.02] p-3"><div className="flex items-center justify-between gap-3"><span className="font-mono text-xs text-white">{label(item,'entityId')}</span><strong className={`text-sm ${Number(item.completeness)>=100?'text-emerald-300':'text-amber-300'}`}>{Number(item.completeness??0).toFixed(0)}%</strong></div><p className="mt-2 text-xs text-[#8490a3]">{Array.isArray(item.missingKeys)&&item.missingKeys.length?`Missing: ${item.missingKeys.join(', ')}`:'Complete'}</p></article>)}</div></section>
          <div className="grid gap-6 lg:grid-cols-2">
            <section className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5"><div className="flex items-center justify-between"><h2 className="font-semibold text-white">Tracks</h2><span className="text-xs text-[#8490a3]">{tracks.length} loaded</span></div><div className="mt-4 space-y-2">{tracks.length ? tracks.map(track=><article key={label(track,'id')} className="rounded-xl border border-white/[.06] bg-white/[.02] p-3"><div className="flex justify-between gap-3"><strong className="text-sm text-white">{label(track,'title',label(track,'code'))}</strong><span className="text-xs text-violet-300">{label(track,'status')}</span></div><p className="mt-1 text-xs text-[#8490a3]">{label(track,'description','No description')}</p><p className="mt-2 font-mono text-[11px] text-cyan-300">{label(track,'code')}</p></article>):<p className="py-10 text-center text-sm text-[#8490a3]">No tracks returned by the backend.</p>}</div></section>
            <section className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5"><div className="flex flex-wrap items-center justify-between gap-3"><h2 className="font-semibold text-white">Learner progress</h2><div className="relative min-w-52"><Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[#8490a3]" size={14}/><input aria-label="Search learners" value={query} onChange={event=>{setQuery(event.target.value);setPage(0)}} placeholder="Search user…" className="w-full rounded-lg border border-white/10 bg-black/20 py-2 pl-9 pr-3 text-xs text-white outline-none focus:border-violet-400"/></div></div>{progress.length?<div className="mt-4 overflow-x-auto"><table className="w-full text-left text-xs"><thead className="border-b border-white/[.08] text-[#8490a3]"><tr><th className="pb-2">Learner</th><th className="pb-2">Track</th><th className="pb-2">Level</th><th className="pb-2">Attempt</th><th className="pb-2">Updated</th></tr></thead><tbody>{progress.map((entry,index)=><tr key={label(entry,'id',String(index))} className="border-b border-white/[.05] text-[#d7deeb]"><td className="py-3">{label(entry,'displayName',label(entry,'userId'))}</td><td className="py-3">{label(entry,'trackId')}</td><td className="py-3"><Link className="text-violet-300 hover:underline" href={`/${locale}/admin/learning/levels?level=${encodeURIComponent(label(entry,'currentLevelId',''))}`}>{label(entry,'currentLevelId')}</Link></td><td className="py-3"><span className="text-cyan-300">{label(entry,'latestAttemptId')}</span></td><td className="py-3">{label(entry,'updatedAt')}</td></tr>)}</tbody></table></div>:<p className="py-10 text-center text-sm text-[#8490a3]">No progress records match these filters.</p>}<div className="mt-4 flex items-center justify-between border-t border-white/[.06] pt-3 text-xs text-[#8490a3]"><span>Page {page+1} of {Math.max(1,totalPages)}</span><div className="flex gap-2"><button aria-label="Previous page" disabled={page===0||loading} onClick={()=>setPage(value=>Math.max(0,value-1))} className="grid h-8 w-8 place-items-center rounded-lg border border-white/10 disabled:opacity-30"><ChevronLeft size={15}/></button><button aria-label="Next page" disabled={page+1>=totalPages||loading} onClick={()=>setPage(value=>value+1)} className="grid h-8 w-8 place-items-center rounded-lg border border-white/10 disabled:opacity-30"><ChevronRight size={15}/></button></div></div></section>
          </div>
        </>
      )}
    </div>
  );
}
