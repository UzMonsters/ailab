'use client';
import { useCallback, useEffect, useState } from 'react';
import { Loader2, RefreshCw } from 'lucide-react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { errorMessage } from '@/shared/utils/errorMessage';
import { FormSection } from '@/widgets/admin/editor';
import type { ReactionRelationship } from './materialEditor.types';

export function MaterialRelatedReactions({ value }: { value: ReactionRelationship[]; onChange: (value: ReactionRelationship[]) => void }) {
  const [reactions, setReactions] = useState<JsonObject[]>([]); const [loading, setLoading] = useState(true); const [error, setError] = useState('');
  const load = useCallback(async () => { setLoading(true); setError(''); try { const result = await adminPlatformApi.chemistry.reactions.list({ size: 100, sort: 'updatedAt,desc' }); setReactions(result.items ?? result.content ?? []); } catch (reason) { setError(errorMessage(reason, 'Could not load related Reactions.')); } finally { setLoading(false); } }, []);
  useEffect(() => { const timer = window.setTimeout(() => void load(), 0); return () => window.clearTimeout(timer); }, [load]);
  const record = (id: string) => reactions.find((item) => String(item.id ?? item.code) === id);
  return <FormSection title="Related reactions" description="Derived, read-only relationships from canonical Reaction records. Edit the Reaction to change participants.">
    {loading ? <div className="grid min-h-40 place-items-center"><Loader2 className="animate-spin text-violet-400"/></div> : error ? <div role="alert" className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-200"><p>Could not load related Reactions.</p><details className="mt-2"><summary>Show details</summary>{error}</details><button type="button" onClick={() => void load()} className="mt-3 flex items-center gap-2 underline"><RefreshCw size={13}/>Retry</button></div> : <div className="grid gap-4 md:grid-cols-3">{(['REACTANT','PRODUCT','CATALYST'] as const).map((role) => <section key={role} className="rounded-xl border border-white/[.07] bg-black/15 p-4"><h3 className="text-xs font-bold text-slate-400">USED AS {role}</h3><div className="mt-3 space-y-2">{value.filter((item) => item.role === role).map((item) => { const reaction = record(item.reactionId); return <div key={item.reactionId} className="rounded-lg bg-white/[.035] p-3"><div className="flex items-center justify-between gap-2"><strong className="text-sm">{String(reaction?.name ?? reaction?.code ?? item.reactionId)}</strong><span className="rounded-full bg-white/5 px-2 py-1 text-[10px] text-slate-400">{String(reaction?.status ?? 'UNKNOWN')}</span></div>{item.notes && <p className="mt-1 text-xs text-slate-500">{item.notes}</p>}</div>; })}{!value.some((item) => item.role === role) && <p className="text-xs text-slate-600">No related Reactions.</p>}</div></section>)}</div>}
  </FormSection>;
}
