'use client';

import { useState, useEffect } from 'react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { Users } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';

export function MembershipsTable() {
  const [members, setMembers] = useState<(JsonObject & { workspaceName?: string })[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      setLoading(true);
      setError('');
      try {
        const ws = await adminPlatformApi.workspaces.list({ size: 100 });
        const wsList = ws.items ?? [];
        const allMembers: (JsonObject & { workspaceName?: string })[] = [];
        const details = await Promise.all(wsList.map(workspace => adminPlatformApi.workspaces.get(String(workspace.id))));
        details.forEach(detail => {
          const arr = Array.isArray(detail.members) ? detail.members : [];
          arr.forEach(member => allMembers.push({ ...(member as JsonObject), workspaceName: String(detail.name ?? '') }));
        });
        setMembers(allMembers);
      } catch { setError('Could not load members.'); }
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

  if (!members.length) return (
    <div className="py-16 text-center text-sm text-slate-500">
      <Users size={32} className="mx-auto mb-3 text-slate-600" />
      <p>No members found.</p>
    </div>
  );

  return (
    <div className="overflow-x-auto rounded-lg border border-white/10">
      <table className="w-full text-sm">
        <thead>
          <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
            <th className="px-4 py-2.5">User</th>
            <th className="px-4 py-2.5">Workspace</th>
            <th className="px-4 py-2.5">Role</th>
            <th className="px-4 py-2.5">Joined</th>
          </tr>
        </thead>
        <tbody>
          {members.map((m, i) => (
            <tr key={i} className="border-b border-white/5 hover:bg-white/[.02] transition-colors">
              <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                  <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-violet-600/20 text-[10px] font-bold text-violet-300">
                    {String(m.displayName || m.userId || 'U').charAt(0).toUpperCase()}
                  </div>
                  <div>
                    <p className="text-sm text-white">{String(m.displayName || m.email || m.userId || 'User')}</p>
                    {m.email && <p className="text-[10px] text-slate-500">{String(m.email)}</p>}
                  </div>
                </div>
              </td>
              <td className="px-4 py-3 text-xs text-slate-300">{m.workspaceName || '—'}</td>
              <td className="px-4 py-3">
                <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                  {String(m.role || 'VIEWER')}
                </span>
              </td>
              <td className="px-4 py-3 text-xs text-slate-400">
                {m.joinedAt ? new Date(String(m.joinedAt)).toLocaleDateString() : '—'}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
