'use client';

import { useState, useEffect } from 'react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';
import { workspacesApi } from '@/entities/workspace/api/workspace.api';
import { Mail } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';

const statusColors: Record<string, string> = {
  PENDING: 'bg-amber-500/15 text-amber-400',
  ACCEPTED: 'bg-emerald-500/15 text-emerald-400',
  EXPIRED: 'bg-slate-500/15 text-slate-400',
  CANCELLED: 'bg-rose-500/15 text-rose-400',
};

export function InvitationsTable() {
  const [invitations, setInvitations] = useState<(JsonObject & { workspaceName?: string })[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      setLoading(true);
      setError('');
      try {
        const ws = await workspacesApi.list({ size: 50 });
        const wsList = Array.isArray(ws) ? ws : (ws as any).items || [];
        const allInv: (JsonObject & { workspaceName?: string })[] = [];
        for (const workspace of wsList) {
          try {
            const res = await workspaceCollaborationApi.invitations(String(workspace.id));
            const arr = Array.isArray(res) ? res : [];
            arr.forEach((inv: any) => allInv.push({ ...inv, workspaceName: workspace.name || workspace.title }));
          } catch {}
        }
        setInvitations(allInv);
      } catch { setError('Could not load invitations.'); }
      setLoading(false);
    })();
  }, []);

  if (loading) return <div className="space-y-2">{[1, 2, 3].map(i => <div key={i} className="h-12 animate-pulse rounded-lg bg-white/5" />)}</div>;

  if (error) return (
    <div className="rounded-lg border border-rose-500/20 bg-rose-500/5 p-3 text-sm text-rose-300">
      {error}
      <button onClick={() => window.location.reload()} className="ml-2 underline hover:text-rose-200">Retry</button>
    </div>
  );

  if (!invitations.length) return (
    <div className="py-16 text-center text-sm text-slate-500">
      <Mail size={32} className="mx-auto mb-3 text-slate-600" />
      <p>No invitations.</p>
    </div>
  );

  return (
    <div className="overflow-x-auto rounded-lg border border-white/10">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
            <th className="px-4 py-2.5">Invitee</th>
            <th className="px-4 py-2.5">Workspace</th>
            <th className="px-4 py-2.5">Role</th>
            <th className="px-4 py-2.5">Sent</th>
            <th className="px-4 py-2.5">Status</th>
          </tr>
        </thead>
        <tbody>
          {invitations.map((inv, i) => (
            <tr key={i} className="border-b border-white/5 hover:bg-white/[.02] transition-colors">
              <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                  <Mail size={12} className="shrink-0 text-slate-500" />
                  <span className="text-sm text-white">{String(inv.invitee || inv.email || '—')}</span>
                </div>
              </td>
              <td className="px-4 py-3 text-xs text-slate-300">{inv.workspaceName || '—'}</td>
              <td className="px-4 py-3">
                <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                  {String(inv.role || 'VIEWER')}
                </span>
              </td>
              <td className="px-4 py-3 text-xs text-slate-400">
                {inv.createdAt ? new Date(String(inv.createdAt)).toLocaleDateString() : '—'}
              </td>
              <td className="px-4 py-3">
                <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${statusColors[String(inv.status || 'PENDING')] || 'bg-slate-500/15 text-slate-400'}`}>
                  {String(inv.status || 'PENDING')}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
