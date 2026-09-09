'use client';

import { useState } from 'react';
import { X, AlertTriangle } from 'lucide-react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';

interface Props {
  workspaceId: string;
  workspaceName: string;
  member: { userId: string; displayName?: string; email?: string };
  onClose: () => void;
  onRemoved: () => void;
}

export function RemoveMemberModal({ workspaceId, workspaceName, member, onClose, onRemoved }: Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleRemove = async () => {
    setLoading(true);
    setError('');
    try {
      await workspaceCollaborationApi.removeMember(workspaceId, member.userId);
      onRemoved();
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to remove member.');
    }
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Remove member</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <div className="flex items-start gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-rose-500/15">
              <AlertTriangle size={16} className="text-rose-400" />
            </div>
            <div>
              <p className="text-sm text-white">
                Remove <span className="font-medium">{String(member.displayName || member.email || member.userId)}</span>?
              </p>
              <p className="mt-1 text-xs text-slate-400">
                They will lose access to <span className="text-white">{workspaceName}</span>.
              </p>
            </div>
          </div>

          {error && <p className="text-xs text-rose-400">{error}</p>}

          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={() => void handleRemove()}
              disabled={loading}
              className="rounded-lg bg-rose-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-rose-500 disabled:opacity-40"
            >
              {loading ? 'Removing...' : 'Remove member'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
