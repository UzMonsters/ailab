'use client';
import { useCallback, useEffect, useState } from 'react';
import { AlertCircle, Loader2, Pause, RefreshCw, Square } from 'lucide-react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { errorMessage } from '@/shared/utils/errorMessage';

export default function Page() {
  const [rows, setRows] = useState<JsonObject[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [pendingAction, setPendingAction] = useState<{ kind: 'pause' | 'terminate'; id: string } | null>(null);
  const [actionReason, setActionReason] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const page = await adminPlatformApi.laboratories.list({ size: 100 });
      setRows(page.items ?? page.content ?? []);
      setError('');
    } catch (reason) {
      setError(errorMessage(reason, 'Unable to load laboratory sessions'));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const timer = window.setTimeout(() => void load(), 0);
    return () => window.clearTimeout(timer);
  }, [load]);

  const act = (kind: 'pause' | 'terminate', row: JsonObject) => {
    const id = String(row.workspaceId ?? row.sessionId);
    setPendingAction({ kind, id });
    setActionReason('');
  };

  const confirmAction = async () => {
    if (!pendingAction || !actionReason.trim()) return;
    const { kind, id } = pendingAction;
    setPendingAction(null);
    setActionReason('');
    try {
      await adminPlatformApi.laboratories[kind](id, { reason: actionReason.trim(), notifyOwner: true });
      await load();
    } catch (e) {
      setError(errorMessage(e, 'Command failed'));
    }
  };

  return (
    <div className="space-y-6 pb-12">
      <AdminPageHeader
        title="Laboratory Sessions"
        description="Monitor and manage active workspaces."
        counters={[{ label: 'Backend sessions', value: rows.length }]}
        actions={
          <button onClick={() => void load()}
            className="grid h-10 w-10 place-items-center rounded-lg border border-white/10 bg-[#141b2a] text-white"
            aria-label="Refresh">
            <RefreshCw size={16} />
          </button>
        }
      />

      {error && (
        <div className="flex gap-2 rounded-xl border border-red-500/20 bg-red-500/10 p-4 text-red-300">
          <AlertCircle size={17} />{error}
          <button onClick={() => void load()} className="ml-auto underline">Retry</button>
        </div>
      )}

      {loading ? (
        <div className="grid min-h-64 place-items-center">
          <Loader2 className="animate-spin text-violet-400" />
        </div>
      ) : rows.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-white/10 p-12 text-center text-[#8490a3]">
          No active sessions.
        </div>
      ) : (
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
          {rows.map((row, index) => (
            <article key={String(row.workspaceId ?? index)} className="rounded-2xl border border-white/[.07] bg-[#0b101a] p-5">
              <div className="flex items-start justify-between">
                <div>
                  <h2 className="font-semibold text-white">{String(row.name ?? 'Laboratory')}</h2>
                  <p className="mt-1 font-mono text-xs text-cyan-300">{String(row.sessionId ?? row.workspaceId)}</p>
                </div>
                <span className="rounded-full bg-emerald-500/10 px-2 py-1 text-xs text-emerald-300">
                  {String(row.status ?? 'ACTIVE')}
                </span>
              </div>
              <div className="mt-5 grid grid-cols-2 gap-3 text-xs text-[#8490a3]">
                <span>Objects <b className="block text-lg text-white">{String(row.objectCount ?? 0)}</b></span>
                <span>Science <b className="block text-sm text-white">{String(row.science ?? 'Chemistry')}</b></span>
              </div>
              <div className="mt-4 flex gap-2">
                <button onClick={() => act('pause', row)}
                  className="flex items-center gap-1.5 rounded-lg border border-amber-400/20 px-3 py-1.5 text-xs text-amber-300 hover:bg-amber-500/10">
                  <Pause size={12} /> Pause
                </button>
                <button onClick={() => act('terminate', row)}
                  className="flex items-center gap-1.5 rounded-lg border border-red-400/20 px-3 py-1.5 text-xs text-red-300 hover:bg-red-500/10">
                  <Square size={12} /> Terminate
                </button>
              </div>
            </article>
          ))}
        </div>
      )}

      {pendingAction && (
        <div className="fixed inset-0 z-[400] flex items-center justify-center bg-slate-950/60 backdrop-blur-sm" role="dialog" aria-modal="true">
          <div className="w-full max-w-md rounded-2xl border border-white/10 bg-[#0b101a] p-6 shadow-2xl">
            <h3 className="text-lg font-semibold text-white">{pendingAction.kind === 'pause' ? 'Pause session' : 'Terminate session'}</h3>
            <p className="mt-1 text-sm text-slate-400">Reason</p>
            <input autoFocus value={actionReason} onChange={e => setActionReason(e.target.value)}
              onKeyDown={e => { if (e.key === 'Enter') void confirmAction(); if (e.key === 'Escape') setPendingAction(null); }}
              className="mt-2 w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400"
              placeholder="Enter reason..." />
            <div className="mt-4 flex justify-end gap-2">
              <button onClick={() => setPendingAction(null)} className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5">Cancel</button>
              <button onClick={() => void confirmAction()} disabled={!actionReason.trim()}
                className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500 disabled:opacity-50">
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
