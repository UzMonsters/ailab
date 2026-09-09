'use client';

import { useState, useEffect } from 'react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { Link2, Copy, Check } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';

const statusColors: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/15 text-emerald-400',
  REVOKED: 'bg-rose-500/15 text-rose-400',
  EXPIRED: 'bg-slate-500/15 text-slate-400',
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
  return `${days}d ago`;
}

export function ShareLinksTable() {
  const [links, setLinks] = useState<(JsonObject & { workspaceName?: string })[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [copiedId, setCopiedId] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      setLoading(true);
      setError('');
      try {
        const ws = await adminPlatformApi.workspaces.list({ size: 100 });
        const wsList = ws.items ?? [];
        const allLinks: (JsonObject & { workspaceName?: string })[] = [];
        const details = await Promise.all(wsList.map(workspace => adminPlatformApi.workspaces.get(String(workspace.id))));
        details.forEach(detail => {
          const arr = Array.isArray(detail.shareLinks) ? detail.shareLinks : [];
          arr.forEach(link => allLinks.push({ ...(link as JsonObject), workspaceName: String(detail.name ?? '') }));
        });
        setLinks(allLinks);
      } catch { setError('Could not load share links.'); }
      setLoading(false);
    })();
  }, []);

  const copyLink = async (url: string, id: string) => {
    await navigator.clipboard.writeText(url);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  if (loading) return <div className="space-y-2">{[1, 2, 3].map(i => <div key={i} className="h-12 animate-pulse rounded-lg bg-white/5" />)}</div>;

  if (error) return (
    <div className="rounded-lg border border-rose-500/20 bg-rose-500/5 p-3 text-sm text-rose-300">
      {error}
      <button onClick={() => window.location.reload()} className="ml-2 underline hover:text-rose-200">Retry</button>
    </div>
  );

  if (!links.length) return (
    <div className="py-16 text-center text-sm text-slate-500">
      <Link2 size={32} className="mx-auto mb-3 text-slate-600" />
      <p>No share links.</p>
    </div>
  );

  return (
    <div className="overflow-x-auto rounded-lg border border-white/10">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
            <th className="px-4 py-2.5">Link</th>
            <th className="px-4 py-2.5">Workspace</th>
            <th className="px-4 py-2.5">Role</th>
            <th className="px-4 py-2.5">Uses</th>
            <th className="px-4 py-2.5">Status</th>
            <th className="px-4 py-2.5">Created</th>
            <th className="px-4 py-2.5 w-10"></th>
          </tr>
        </thead>
        <tbody>
          {links.map((link, i) => (
            <tr key={i} className="border-b border-white/5 hover:bg-white/[.02] transition-colors">
              <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                  <Link2 size={12} className="shrink-0 text-slate-500" />
                  <span className="text-xs text-slate-300 truncate max-w-[250px]">{String(link.url || link.linkId || '—')}</span>
                </div>
              </td>
              <td className="px-4 py-3 text-xs text-slate-300">{link.workspaceName || '—'}</td>
              <td className="px-4 py-3">
                <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                  {String(link.role || 'VIEWER')}
                </span>
              </td>
              <td className="px-4 py-3 text-xs text-slate-400">{(link as any).useCount ?? '—'}</td>
              <td className="px-4 py-3">
                <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${statusColors[String(link.status || 'ACTIVE')] || 'bg-slate-500/15 text-slate-400'}`}>
                  {String(link.status || 'ACTIVE')}
                </span>
              </td>
              <td className="px-4 py-3 text-xs text-slate-400">{timeAgo(String(link.createdAt))}</td>
              <td className="px-4 py-3">
                {link.url && (
                  <button
                    onClick={() => copyLink(String(link.url), String(link.id || i))}
                    className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white transition-colors"
                    title="Copy link"
                    aria-label="Copy link"
                  >
                    {copiedId === String(link.id || i) ? <Check size={13} className="text-emerald-400" /> : <Copy size={13} />}
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
