'use client';

import { useState } from 'react';
import { X, Link2, Copy, Check } from 'lucide-react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';

interface Props {
  workspaceId: string;
  workspaceName: string;
  onClose: () => void;
  onCreated: () => void;
}

export function CreateShareLinkModal({ workspaceId, workspaceName, onClose, onCreated }: Props) {
  const [role, setRole] = useState<'VIEWER' | 'EDITOR'>('VIEWER');
  const [loading, setLoading] = useState(false);
  const [created, setCreated] = useState<any>(null);
  const [copied, setCopied] = useState(false);
  const [error, setError] = useState('');

  const handleCreate = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await workspaceCollaborationApi.createShareLink(workspaceId, { role });
      setCreated(result);
      onCreated();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to create share link.');
    }
    setLoading(false);
  };

  const copyLink = async () => {
    const url = created?.url || '';
    if (url) {
      await navigator.clipboard.writeText(url);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  if (created) {
    return (
      <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
        <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
          <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
            <h3 className="text-sm font-semibold text-white">Share link created</h3>
            <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
          </div>
          <div className="p-4 space-y-3">
            <p className="text-xs text-slate-400">Share link for <span className="text-white font-medium">{workspaceName}</span></p>
            <div className="flex items-center gap-2 rounded-lg border border-white/10 bg-[#080c14] p-2">
              <Link2 size={14} className="shrink-0 text-slate-500" />
              <span className="flex-1 text-xs text-slate-300 truncate">{created.url || created.linkId || '—'}</span>
              <button onClick={() => void copyLink()} className="shrink-0 text-slate-400 hover:text-white">
                {copied ? <Check size={14} className="text-emerald-400" /> : <Copy size={14} />}
              </button>
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Done</button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Create share link</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <p className="text-xs text-slate-400">Creating link for <span className="text-white font-medium">{workspaceName}</span></p>

          <div>
            <label className="text-[10px] text-slate-500 uppercase tracking-wider">Access level</label>
            <div className="mt-1.5 flex gap-2">
              {(['VIEWER', 'EDITOR'] as const).map(r => (
                <button
                  key={r}
                  onClick={() => setRole(r)}
                  className={`flex-1 rounded-lg border px-3 py-2 text-xs font-medium transition-colors ${
                    role === r
                      ? 'border-violet-500 bg-violet-600/15 text-violet-300'
                      : 'border-white/10 text-slate-400 hover:bg-white/5'
                  }`}
                >
                  {r}
                </button>
              ))}
            </div>
          </div>

          {error && <p className="text-xs text-rose-400">{error}</p>}

          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={() => void handleCreate()}
              disabled={loading}
              className="rounded-lg bg-violet-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-violet-500 disabled:opacity-40"
            >
              {loading ? 'Creating...' : 'Create link'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
