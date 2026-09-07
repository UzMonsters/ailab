'use client';

import { useState, useEffect } from 'react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { workspacesApi } from '@/entities/workspace/api/workspace.api';
import { Activity, UserPlus, Link2, Shield, Trash2, LogIn } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';

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

const actionIcons: Record<string, React.ElementType> = {
  workspace_created: LogIn,
  member_invited: UserPlus,
  member_joined: UserPlus,
  role_changed: Shield,
  share_link_created: Link2,
  share_link_revoked: Trash2,
  member_removed: Trash2,
};

const actionColors: Record<string, string> = {
  workspace_created: 'text-violet-400',
  member_invited: 'text-cyan-400',
  member_joined: 'text-emerald-400',
  role_changed: 'text-amber-400',
  share_link_created: 'text-blue-400',
  share_link_revoked: 'text-rose-400',
  member_removed: 'text-rose-400',
};

export function ActivityLog() {
  const [events, setEvents] = useState<JsonObject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      setLoading(true);
      setError('');
      try {
        const auditEvents = await adminPlatformApi.audit.list({ size: 50 }).catch(() => ({ items: [] as JsonObject[] }));
        const items = (auditEvents.items || []).filter((e: any) =>
          e.action?.includes('workspace') ||
          e.action?.includes('member') ||
          e.action?.includes('share_link') ||
          e.action?.includes('invitation') ||
          e.action?.includes('role')
        );
        setEvents(items);
      } catch {
        setError('Could not load activity.');
      }
      setLoading(false);
    })();
  }, []);

  if (loading) return <div className="space-y-2">{[1, 2, 3, 4, 5].map(i => <div key={i} className="h-12 animate-pulse rounded-lg bg-white/5" />)}</div>;

  if (error) return (
    <div className="rounded-lg border border-rose-500/20 bg-rose-500/5 p-3 text-sm text-rose-300">
      {error}
      <button onClick={() => window.location.reload()} className="ml-2 underline hover:text-rose-200">Retry</button>
    </div>
  );

  if (!events.length) return (
    <div className="py-16 text-center text-sm text-slate-500">
      <Activity size={32} className="mx-auto mb-3 text-slate-600" />
      <p>No collaboration activity found.</p>
      <p className="mt-1 text-xs text-slate-600">Activity events will appear here as workspaces are used.</p>
    </div>
  );

  return (
    <div className="rounded-lg border border-white/10">
      <div className="divide-y divide-white/5">
        {events.map((event, i) => {
          const action = String(event.action || '');
          const Icon = actionIcons[action] || Activity;
          const color = actionColors[action] || 'text-slate-400';

          return (
            <div key={i} className="flex items-center gap-3 px-4 py-3 hover:bg-white/[.02] transition-colors">
              <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-white/5 ${color}`}>
                <Icon size={14} />
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm text-slate-200">
                  <span className="font-medium text-white">{String(event.actorName || event.actorId || 'User')}</span>
                  {' '}
                  <span className="text-slate-400">{action.replace(/_/g, ' ')}</span>
                </p>
                {event.details && <p className="text-xs text-slate-500 truncate">{String(event.details)}</p>}
              </div>
              <span className="shrink-0 text-xs text-slate-500">{timeAgo(String(event.timestamp || event.createdAt))}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
