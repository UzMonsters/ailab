'use client';

import { useState } from 'react';
import { X, AlertTriangle } from 'lucide-react';
import { workspacesApi } from '@/entities/workspace/api/workspace.api';

interface Props {
  workspaceId: string;
  workspaceName: string;
  memberCount: number;
  linkCount: number;
  onClose: () => void;
  onClosed: () => void;
}

export function CloseWorkspaceModal({ workspaceId, workspaceName, memberCount, linkCount, onClose, onClosed }: Props) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleClose = async () => {
    setLoading(true);
    setError('');
    try {
      await workspacesApi.delete(workspaceId);
      onClosed();
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to close workspace.');
    }
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Close workspace</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <div className="flex items-start gap-3">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-amber-500/15">
              <AlertTriangle size={16} className="text-amber-400" />
            </div>
            <div>
              <p className="text-sm text-white">Are you sure you want to close this workspace?</p>
              <p className="mt-1 text-xs text-slate-400">This action may affect collaborators.</p>
            </div>
          </div>

          <div className="rounded-lg border border-white/10 bg-[#080c14] p-3 space-y-1">
            <p className="text-xs text-slate-400">Workspace: <span className="text-white">{workspaceName}</span></p>
            <p className="text-xs text-slate-400">Members: <span className="text-white">{memberCount}</span></p>
            <p className="text-xs text-slate-400">Active links: <span className="text-white">{linkCount}</span></p>
          </div>

          {error && <p className="text-xs text-rose-400">{error}</p>}

          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={() => void handleClose()}
              disabled={loading}
              className="rounded-lg bg-rose-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-rose-500 disabled:opacity-40"
            >
              {loading ? 'Closing...' : 'Close workspace'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// Also export a generic ConfirmModal for other destructive actions
interface ConfirmProps {
  title: string;
  message: string;
  confirmLabel?: string;
  confirmColor?: string;
  loading?: boolean;
  error?: string;
  onClose: () => void;
  onConfirm: () => void;
}

export function ConfirmModal({ title, message, confirmLabel = 'Confirm', confirmColor = 'bg-rose-600 hover:bg-rose-500', loading, error, onClose, onConfirm }: ConfirmProps) {
  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">{title}</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <p className="text-sm text-slate-300">{message}</p>
          {error && <p className="text-xs text-rose-400">{error}</p>}
          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={onConfirm}
              disabled={loading}
              className={`rounded-lg px-4 py-1.5 text-xs font-semibold text-white disabled:opacity-40 ${confirmColor}`}
            >
              {loading ? 'Processing...' : confirmLabel}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
