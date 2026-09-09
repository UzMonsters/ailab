'use client';

import { useState, useEffect } from 'react';
import { useLocale } from 'next-intl';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { ExternalLink, Users, Link2, MoreHorizontal, Search, ChevronLeft, ChevronRight } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';
import Link from 'next/link';

const statusColors: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/15 text-emerald-400',
  IDLE: 'bg-amber-500/15 text-amber-400',
  CLOSED: 'bg-slate-500/15 text-slate-400',
  ARCHIVED: 'bg-slate-500/15 text-slate-500',
};

function timeAgo(date: string | undefined): string {
  if (!date) return '—';
  const diff = Date.now() - new Date(date).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 1) return 'just now';
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(hours / 24);
  if (days < 30) return `${days}d ago`;
  return new Date(date).toLocaleDateString();
}

type WorkspaceRow = JsonObject;

export function WorkspacesTable() {
  const [workspaces, setWorkspaces] = useState<WorkspaceRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [page, setPage] = useState(1);
  const [serverTotal, setServerTotal] = useState(0);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [menuOpen, setMenuOpen] = useState<string | null>(null);
  const [retryKey, setRetryKey] = useState(0);
  const locale = useLocale();
  const pageSize = 25;

  useEffect(() => {
    let cancelled = false;
    (async () => {
      setLoading(true);
      setError('');
      try {
        const params: Record<string, any> = { size: pageSize, page: page - 1 };
        if (search) params.q = search;
        if (statusFilter) params.status = statusFilter;
        const res = await adminPlatformApi.workspaces.list(params);
        const list = res.items ?? [];

        if (!cancelled) {
          setWorkspaces(list);
          setServerTotal(Number((res.page as JsonObject | undefined)?.totalElements ?? 0));
          setLoading(false);
        }
      } catch (err) {
        console.error('Failed to load workspaces', err);
        if (!cancelled) {
          setError('Could not load workspaces.');
          setLoading(false);
        }
      }
    })();
    return () => { cancelled = true; };
  }, [page, search, statusFilter, retryKey]);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (!(e.target as HTMLElement).closest('[data-menu]')) setMenuOpen(null);
    };
    document.addEventListener('click', handler);
    return () => document.removeEventListener('click', handler);
  }, []);

  const totalPages = Math.ceil(serverTotal / pageSize);

  return (
    <div className="space-y-3">
      <div className="flex items-center gap-3">
        <div className="relative flex-1 max-w-xs">
          <Search size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-500" />
          <input
            value={search}
            onChange={e => { setSearch(e.target.value); setPage(1); }}
            placeholder="Search workspaces..."
            className="w-full rounded-lg border border-white/10 bg-[#080c14] pl-8 pr-3 py-1.5 text-sm text-white outline-none focus:border-violet-500"
          />
        </div>
        <select
          value={statusFilter}
          onChange={e => { setStatusFilter(e.target.value); setPage(1); }}
          className="rounded-lg border border-white/10 bg-[#080c14] px-3 py-1.5 text-sm text-white outline-none"
        >
          <option value="">All statuses</option>
          <option value="ACTIVE">Active</option>
          <option value="IDLE">Idle</option>
          <option value="CLOSED">Closed</option>
        </select>
      </div>

      {error && (
        <div className="rounded-lg border border-rose-500/20 bg-rose-500/5 p-3 text-sm text-rose-300">
          {error}
          <button onClick={() => setRetryKey(k => k + 1)} className="ml-2 underline hover:text-rose-200">Retry</button>
        </div>
      )}

      {loading ? (
        <div className="space-y-2">{[1, 2, 3, 4, 5].map(i => <div key={i} className="h-14 animate-pulse rounded-lg bg-white/5" />)}</div>
      ) : !workspaces.length ? (
        <div className="py-16 text-center text-sm text-slate-500">
          <Users size={32} className="mx-auto mb-3 text-slate-600" />
          <p>No collaborative workspaces yet.</p>
        </div>
      ) : (
        <>
          <div className="overflow-x-auto rounded-lg border border-white/10">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
                  <th className="px-4 py-2.5">Workspace</th>
                  <th className="px-4 py-2.5">Members</th>
                  <th className="px-4 py-2.5">Links</th>
                  <th className="px-4 py-2.5">Status</th>
                  <th className="px-4 py-2.5">Created</th>
                  <th className="px-4 py-2.5 w-10"></th>
                </tr>
              </thead>
              <tbody>
                {workspaces.map(ws => (
                  <tr key={String(ws.id)} className="group border-b border-white/5 hover:bg-white/[.02] transition-colors">
                    <td className="px-4 py-3">
                      <Link href={`/${locale}/admin/sharing/workspaces/${ws.id}`} className="block">
                        <p className="font-medium text-white">{String(ws.name || ws.title || 'Untitled')}</p>
                        <p className="text-xs text-slate-500">{String(ws.id).slice(0, 12)}...</p>
                      </Link>
                    </td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-1.5 text-xs text-slate-300">
                        <Users size={12} className="text-slate-500" />
                        {String(ws.memberCount ?? '—')}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className="flex items-center gap-1.5 text-xs text-slate-300">
                        <Link2 size={12} className="text-slate-500" />
                        {String(ws.activeShareLinkCount ?? '—')}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${statusColors[String(ws.status || 'ACTIVE')] || 'bg-slate-500/15 text-slate-400'}`}>
                        {String(ws.status || 'ACTIVE')}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-xs text-slate-400">
                      {ws.updatedAt ? timeAgo(String(ws.updatedAt)) : '—'}
                    </td>
                    <td className="px-4 py-3">
                      <div className="relative" data-menu>
                        <button
                          onClick={() => setMenuOpen(menuOpen === String(ws.id) ? null : String(ws.id))}
                          className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 text-slate-500 hover:text-white opacity-0 group-hover:opacity-100 transition-opacity"
                        >
                          <MoreHorizontal size={14} />
                        </button>
                        {menuOpen === String(ws.id) && (
                          <div className="absolute right-0 top-full z-50 mt-1 w-48 rounded-lg border border-white/10 bg-[#141b2a] shadow-xl py-1">
                            <Link
                              href={`/${locale}/admin/sharing/workspaces/${ws.id}`}
                              className="flex w-full items-center gap-2 px-3 py-2 text-xs text-slate-300 hover:bg-white/5"
                            >
                              <ExternalLink size={12} /> View details
                            </Link>
                            <Link
                              href={`/${locale}/workspace/sandbox?workspace=${encodeURIComponent(String(ws.id))}`}
                              className="flex w-full items-center gap-2 px-3 py-2 text-xs text-slate-300 hover:bg-white/5"
                            >
                              <ExternalLink size={12} /> Open workspace
                            </Link>
                          </div>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="flex items-center justify-between text-xs text-slate-400">
              <span>Showing {((page - 1) * pageSize) + 1}–{Math.min(page * pageSize, serverTotal)} of {serverTotal}</span>
              <div className="flex items-center gap-1">
                <button
                  onClick={() => setPage(p => Math.max(1, p - 1))}
                  disabled={page <= 1}
                  className="flex h-7 items-center gap-1 rounded border border-white/10 px-2 py-1 hover:bg-white/5 disabled:opacity-30"
                >
                  <ChevronLeft size={12} /> Previous
                </button>
                {Array.from({ length: Math.min(totalPages, 5) }, (_, i) => i + 1).map(p => (
                  <button
                    key={p}
                    onClick={() => setPage(p)}
                    className={`h-7 w-7 rounded text-xs ${p === page ? 'bg-violet-600/20 text-violet-300' : 'hover:bg-white/5'}`}
                  >
                    {p}
                  </button>
                ))}
                <button
                  onClick={() => setPage(p => Math.min(totalPages, p + 1))}
                  disabled={page >= totalPages}
                  className="flex h-7 items-center gap-1 rounded border border-white/10 px-2 py-1 hover:bg-white/5 disabled:opacity-30"
                >
                  Next <ChevronRight size={12} />
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
}
