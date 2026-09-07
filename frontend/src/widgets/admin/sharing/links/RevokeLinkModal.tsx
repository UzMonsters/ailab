'use client';

import { useState } from 'react';
import { X, AlertTriangle, Link2 } from 'lucide-react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';

interface Props {
  workspaceId: string;
  workspaceName: string;
  link: { id: string; linkId?: string; url?: string; role?: string };
  onClose: () => void;
  onRevoked: () => void;
}

export function RevokeLinkModal({ workspaceId, workspaceName, link, onClose, onRevoked }: Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleRevoke = async () => {
    setLoading(true);
    setError('');
    try {
      await workspaceCollaborationApi.revokeShareLink(workspaceId, link.id);
      onRevoked();
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to revoke link.');
    }
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Revoke share link</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <div className="flex items-start gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-amber-500/15">
              <AlertTriangle size={16} className="text-amber-400" />
            </div>
            <div>
              <p className="text-sm text-white">Revoke this share link?</p>
              <p className="mt-1 text-xs text-slate-400">
                Anyone with this link will no longer be able to access <span className="text-white">{workspaceName}</span>.
              </p>
            </div>
          </div>

          <div className="rounded-lg border border-white/10 bg-[#080c14] p-2">
            <div className="flex items-center gap-2">
              <Link2 size={12} className="shrink-0 text-slate-500" />
              <span className="text-xs text-slate-300 truncate">{link.url || link.linkId || '—'}</span>
            </div>
            {link.role && <p className="mt-1 text-[10px] text-slate-500">Role: {link.role}</p>}
          </div>

          {error && <p className="text-xs text-rose-400">{error}</p>}

          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={() => void handleRevoke()}
              disabled={loading}
              className="rounded-lg bg-amber-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-amber-500 disabled:opacity-40"
            >
              {loading ? 'Revoking...' : 'Revoke link'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
