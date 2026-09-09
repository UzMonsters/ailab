'use client';

import { useMemo, useState } from 'react';
import Link from 'next/link';
import Image from 'next/image';
import { AlertTriangle, ArrowRight, Clock3, Eye, FlaskConical, KeyRound, Loader2, LockKeyhole } from 'lucide-react';
import { workspaceCollaborationApi, type ResolvedWorkspaceShare } from '@/entities/workspace/api/collaboration.api';
import { getApiBaseUrl } from '@/shared/api/client';

function absoluteAssetUrl(value?: string) {
  if (!value) return undefined;
  return /^https?:\/\//i.test(value) || value.startsWith('data:') ? value : `${getApiBaseUrl()}${value.startsWith('/') ? '' : '/'}${value}`;
}

export default function SharedWorkspaceAccess({ token, locale }: { token: string; locale: string }) {
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<{ message: string; status?: number } | null>(null);
  const [workspace, setWorkspace] = useState<ResolvedWorkspaceShare | null>(null);
  const preview = useMemo(() => absoluteAssetUrl(workspace?.preview?.variants?.dark?.url ?? workspace?.preview?.variants?.light?.url), [workspace]);

  async function openShare() {
    setLoading(true);
    setError(null);
    try {
      const resolved = await workspaceCollaborationApi.resolveShareLink(token, password || undefined);
      setWorkspace(resolved);
      if (resolved.shareSessionToken) sessionStorage.setItem(`workspace-share:${resolved.workspaceId}`, resolved.shareSessionToken);
    } catch (reason) {
      const response = reason as { status?: number; message?: string };
      const message = response.status === 410
        ? 'This share link has expired, was revoked, or reached its usage limit.'
        : response.status === 401
          ? 'This workspace is password protected. Enter its password to continue.'
          : response.status === 403
            ? 'The password is incorrect. Please try again.'
            : response.status === 404
              ? 'This share link does not exist.'
              : response.message || 'The workspace could not be opened. Check your connection and try again.';
      setError({ message, status: response.status });
    } finally {
      setLoading(false);
    }
  }

  return <main className="relative min-h-screen overflow-hidden bg-[#070910] px-5 py-10 text-white">
    <div aria-hidden className="absolute inset-0 bg-[radial-gradient(circle_at_22%_18%,rgba(139,92,246,.2),transparent_34%),radial-gradient(circle_at_82%_72%,rgba(6,182,212,.13),transparent_34%)]" />
    <section className="relative mx-auto grid min-h-[calc(100vh-5rem)] max-w-5xl place-items-center">
      <div className="grid w-full overflow-hidden rounded-3xl border border-white/10 bg-[#0d111c]/95 shadow-2xl lg:grid-cols-[1.1fr_.9fr]">
        <div className="relative min-h-72 bg-[#090d16] p-6">
          {preview ? <Image unoptimized width={960} height={540} src={preview} alt={`Preview of ${workspace?.name ?? 'shared workspace'}`} className="h-full max-h-[540px] w-full rounded-2xl object-cover" /> : <div className="grid h-full min-h-72 place-items-center rounded-2xl border border-dashed border-white/10 bg-[linear-gradient(rgba(148,163,184,.08)_1px,transparent_1px),linear-gradient(90deg,rgba(148,163,184,.08)_1px,transparent_1px)] bg-[size:24px_24px]"><div className="text-center text-slate-400"><FlaskConical className="mx-auto mb-3 text-violet-400" size={46}/><p>{workspace ? 'No generated preview is available yet.' : 'Workspace preview appears after access is verified.'}</p></div></div>}
        </div>
        <div className="flex flex-col justify-center p-7 sm:p-10">
          <span className="mb-5 grid h-12 w-12 place-items-center rounded-2xl bg-violet-500/15 text-violet-300"><LockKeyhole size={23}/></span>
          <p className="text-xs font-semibold uppercase tracking-[.18em] text-violet-300">jasScience shared workspace</p>
          <h1 className="mt-2 text-3xl font-bold">{workspace?.name ?? 'Open shared laboratory'}</h1>
          <p className="mt-3 text-sm leading-6 text-slate-400">{workspace ? `${workspace.science} · ${workspace.role.toLowerCase()} access` : 'Verify this link to see its owner-provided preview and access details.'}</p>
          {!workspace && <form className="mt-8 space-y-3" onSubmit={(event)=>{event.preventDefault();void openShare()}}>
            <label className="block text-sm font-medium text-slate-200" htmlFor="share-password">Password <span className="font-normal text-slate-500">(if required)</span></label>
            <div className="relative"><KeyRound className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" size={17}/><input id="share-password" type="password" autoComplete="current-password" value={password} onChange={(event)=>setPassword(event.target.value)} className="w-full rounded-xl border border-white/10 bg-black/25 py-3 pl-10 pr-4 outline-none focus:border-violet-400 focus:ring-2 focus:ring-violet-500/25" placeholder="Enter password" /></div>
            {error && <div role="alert" className="flex gap-2 rounded-xl border border-red-500/25 bg-red-500/10 p-3 text-sm text-red-200"><AlertTriangle className="mt-0.5 shrink-0" size={17}/><span>{error.message}</span></div>}
            <button disabled={loading} className="flex w-full items-center justify-center gap-2 rounded-xl bg-violet-600 px-4 py-3 font-semibold transition hover:bg-violet-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-violet-300 disabled:opacity-60">{loading?<Loader2 className="animate-spin" size={18}/>:<Eye size={18}/>}Verify access</button>
          </form>}
          {workspace && <div className="mt-7 space-y-4">
            {workspace.expiresAt && <div className="flex items-center gap-2 text-sm text-slate-400"><Clock3 size={16}/><span>Link expires {new Date(workspace.expiresAt).toLocaleString()}</span></div>}
            <div className="flex flex-wrap gap-2">{workspace.capabilities.map((capability)=><span key={capability} className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-xs text-slate-300">{capability.replaceAll('_',' ').toLowerCase()}</span>)}</div>
            <Link href={`/${locale}/workspace/sandbox?workspace=${encodeURIComponent(workspace.workspaceId)}&shared=1`} className="flex w-full items-center justify-center gap-2 rounded-xl bg-violet-600 px-4 py-3 font-semibold transition hover:bg-violet-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-violet-300">Open workspace <ArrowRight size={18}/></Link>
          </div>}
          <Link href={`/${locale}`} className="mt-6 text-center text-xs text-slate-500 hover:text-slate-300">Return to jasScience</Link>
        </div>
      </div>
    </section>
  </main>;
}
