'use client';

import { useState } from 'react';
import { X, Shield } from 'lucide-react';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';

interface Props {
  workspaceId: string;
  member: { userId: string; displayName?: string; email?: string; role: string };
  onClose: () => void;
  onRoleChanged: () => void;
}

export function ChangeRoleModal({ workspaceId, member, onClose, onRoleChanged }: Props) {
  const [role, setRole] = useState<'VIEWER' | 'EDITOR'>(member.role === 'OWNER' ? 'EDITOR' : member.role as 'VIEWER' | 'EDITOR');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = async () => {
    setLoading(true);
    setError('');
    try {
      await workspaceCollaborationApi.updateMember(workspaceId, member.userId, role);
      onRoleChanged();
      onClose();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to change role.');
    }
    setLoading(false);
  };

  return (
    <div className="fixed inset-0 z-[300] flex items-center justify-center bg-slate-950/80 backdrop-blur-sm" onClick={onClose}>
      <div className="w-full max-w-md rounded-xl border border-white/10 bg-[#141b2a] shadow-2xl" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between border-b border-white/10 px-4 py-3">
          <h3 className="text-sm font-semibold text-white">Change role</h3>
          <button onClick={onClose} className="text-slate-400 hover:text-white"><X size={16} /></button>
        </div>
        <div className="p-4 space-y-4">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-violet-600/20 text-xs font-bold text-violet-300">
              {String(member.displayName || member.userId || 'U').charAt(0).toUpperCase()}
            </div>
            <div>
              <p className="text-sm font-medium text-white">{String(member.displayName || member.email || member.userId)}</p>
              <p className="text-[10px] text-slate-500">Current role: {member.role}</p>
            </div>
          </div>

          <div>
            <label className="text-[10px] text-slate-500 uppercase tracking-wider">New role</label>
            <div className="mt-1.5 space-y-1.5">
              {(['VIEWER', 'EDITOR'] as const).map(r => (
                <button
                  key={r}
                  onClick={() => setRole(r)}
                  className={`flex w-full items-center gap-2 rounded-lg border px-3 py-2 text-xs font-medium transition-colors ${
                    role === r
                      ? 'border-violet-500 bg-violet-600/15 text-violet-300'
                      : 'border-white/10 text-slate-400 hover:bg-white/5'
                  }`}
                >
                  <Shield size={12} />
                  {r}
                  {r === member.role && <span className="ml-auto text-[10px] text-slate-500">current</span>}
                </button>
              ))}
            </div>
          </div>

          {error && <p className="text-xs text-rose-400">{error}</p>}

          <div className="flex justify-end gap-2 pt-2">
            <button onClick={onClose} className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-300 hover:bg-white/5">Cancel</button>
            <button
              onClick={() => void handleChange()}
              disabled={loading || role === member.role}
              className="rounded-lg bg-violet-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-violet-500 disabled:opacity-40"
            >
              {loading ? 'Changing...' : 'Change role'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
