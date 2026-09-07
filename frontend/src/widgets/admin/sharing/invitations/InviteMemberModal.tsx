'use client';

import { useState } from 'react';
import { X, Mail } from 'lucide-react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';

interface Props {
  workspaceId: string;
  workspaceName: string;
  onClose: () => void;
  onInvited: () => void;
}

export function InviteMemberModal({ workspaceId, workspaceName, onClose, onInvited }: Props) {
  const [email, setEmail] = useState('');
  const [role, setRole] = useState<'VIEWER' | 'EDITOR'>('VIEWER');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleInvite = async () => {
    if (!email.trim()) return;
    setLoading(true);
    setError('');
    try {
      await workspaceCollaborationApi.invite(workspaceId, { emailOrUserId: email.trim(), role });
      onInvited();
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to send invitation.');
    }
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Invite member</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <p className="text-xs text-slate-400">Inviting to <span className="text-white font-medium">{workspaceName}</span></p>

          <div>
            <label className="text-[10px] text-slate-500 uppercase tracking-wider">Email</label>
            <div className="mt-1.5 relative">
              <Mail size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-500" />
              <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="user@example.com"
                className="w-full rounded-lg border border-white/10 bg-[#080c14] pl-8 pr-3 py-2 text-sm text-white outline-none focus:border-violet-500"
                autoFocus
              />
            </div>
          </div>

          <div>
            <label className="text-[10px] text-slate-500 uppercase tracking-wider">Role</label>
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
              onClick={() => void handleInvite()}
              disabled={loading || !email.trim()}
              className="rounded-lg bg-violet-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-violet-500 disabled:opacity-40"
            >
              {loading ? 'Sending...' : 'Send invitation'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
