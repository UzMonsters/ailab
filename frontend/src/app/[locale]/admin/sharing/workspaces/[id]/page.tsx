'use client';

import { useState, useEffect, useCallback } from 'react';
import { useParams, usePathname, useRouter } from 'next/navigation';
import Link from 'next/link';
import { ArrowLeft, Users, Link2, Mail, ExternalLink, Copy, Check, MoreHorizontal, Plus, Shield, Trash2 } from 'lucide-react';
import { workspacesApi } from '@/entities/workspace/api/workspace.api';
import { workspaceCollaborationApi } from '@/entities/workspace/api/collaboration.api';
import { CreateShareLinkModal } from '@/widgets/admin/sharing/links/CreateShareLinkModal';
import { RevokeLinkModal } from '@/widgets/admin/sharing/links/RevokeLinkModal';
import { InviteMemberModal } from '@/widgets/admin/sharing/invitations/InviteMemberModal';
import { ChangeRoleModal } from '@/widgets/admin/sharing/members/ChangeRoleModal';
import { RemoveMemberModal } from '@/widgets/admin/sharing/members/RemoveMemberModal';
import { CloseWorkspaceModal } from '@/widgets/admin/sharing/shared/ConfirmModal';
import type { JsonObject } from '@/shared/api/contracts/platform';

type Tab = 'overview' | 'members' | 'links' | 'invitations';

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

const statusColors: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/15 text-emerald-400',
  IDLE: 'bg-amber-500/15 text-amber-400',
  CLOSED: 'bg-slate-500/15 text-slate-400',
};

export default function WorkspaceDetailPage() {
  const router = useRouter();
  const params = useParams();
  const pathname = usePathname();
  const locale = 'en';
  const workspaceId = String(params.id || '');
  const [workspace, setWorkspace] = useState<JsonObject | null>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [links, setLinks] = useState<any[]>([]);
  const [invitations, setInvitations] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<Tab>('overview');
  const [copiedId, setCopiedId] = useState<string | null>(null);
  const [showCreateLink, setShowCreateLink] = useState(false);
  const [showInvite, setShowInvite] = useState(false);
  const [showClose, setShowClose] = useState(false);
  const [changeRoleMember, setChangeRoleMember] = useState<any>(null);
  const [removeMemberTarget, setRemoveMemberTarget] = useState<any>(null);
  const [revokeLinkTarget, setRevokeLinkTarget] = useState<any>(null);

  const loadData = useCallback(async () => {
    if (!workspaceId) return;
    setLoading(true);
    setError('');
    try {
      const [ws, mems, lks, invs] = await Promise.all([
        workspacesApi.get(workspaceId).catch(() => null),
        workspaceCollaborationApi.members(workspaceId).catch(() => []),
        workspaceCollaborationApi.shareLinks(workspaceId).catch(() => []),
        workspaceCollaborationApi.invitations(workspaceId).catch(() => []),
      ]);
      setWorkspace(ws);
      setMembers(Array.isArray(mems) ? mems : []);
      setLinks(Array.isArray(lks) ? lks : []);
      setInvitations(Array.isArray(invs) ? invs : []);
    } catch { setError('Could not load workspace.'); }
    setLoading(false);
  }, [workspaceId]);

  useEffect(() => {
    const timer = window.setTimeout(() => void loadData(), 0);
    return () => window.clearTimeout(timer);
  }, [loadData]);

  const copyLink = async (url: string, id: string) => {
    await navigator.clipboard.writeText(url);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  if (loading) return <div className="space-y-4">{[1, 2, 3].map(i => <div key={i} className="h-20 animate-pulse rounded-lg bg-white/5" />)}</div>;

  if (error) return (
    <div className="py-16 text-center">
      <p className="text-sm text-rose-300">{error}</p>
      <Link href={`/${locale}/admin/sharing`} className="mt-2 text-sm text-violet-400 hover:underline">Back to Collaboration</Link>
    </div>
  );

  if (!workspace) return (
    <div className="py-16 text-center">
      <p className="text-sm text-slate-500">Workspace not found.</p>
      <Link href={`/${locale}/admin/sharing`} className="mt-2 text-sm text-violet-400 hover:underline">Back to Collaboration</Link>
    </div>
  );

  const tabs: { id: Tab; label: string; count?: number }[] = [
    { id: 'overview', label: 'Overview' },
    { id: 'members', label: 'Members', count: members.length },
    { id: 'links', label: 'Share Links', count: links.length },
    { id: 'invitations', label: 'Invitations', count: invitations.length },
  ];

  return (
    <div className="space-y-6">
      <div>
        <Link href={`/${locale}/admin/sharing`} className="inline-flex items-center gap-1 text-xs text-slate-400 hover:text-white transition-colors mb-3">
          <ArrowLeft size={14} /> Collaboration
        </Link>
        <div className="flex items-start justify-between">
          <div>
            <div className="flex items-center gap-3">
              <h1 className="page-title">{String(workspace.name || workspace.title || 'Workspace')}</h1>
              <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${
                String(workspace.status ?? 'ACTIVE') === 'ACTIVE' ? 'bg-emerald-500/15 text-emerald-400' : 'bg-slate-500/15 text-slate-400'
              }`}>
                {String(workspace.status || 'ACTIVE')}
              </span>
            </div>
            <p className="page-subtitle flex items-center gap-2 mt-1">
              <span className="text-xs text-slate-500">{workspaceId.slice(0, 12)}...</span>
              <span className="text-slate-600">·</span>
              <span className="text-xs text-slate-500">Created {workspace.createdAt ? timeAgo(String(workspace.createdAt)) : '—'}</span>
            </p>
          </div>
          <div className="flex items-center gap-2">
            <button onClick={() => setShowInvite(true)} className="admin-btn">
              <Plus size={14} /> Invite
            </button>
            <button onClick={() => setShowCreateLink(true)} className="admin-btn">
              <Link2 size={14} /> Create Link
            </button>
            <Link
              href={`/${locale}/workspace/sandbox`}
              className="admin-btn"
            >
              <ExternalLink size={14} /> Open workspace
            </Link>
            <button onClick={() => setShowClose(true)} className="admin-btn" style={{ borderColor: 'rgba(239,68,68,0.3)' }}>
              Close
            </button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        {[
          { label: 'Members', value: members.length, icon: Users },
          { label: 'Share Links', value: links.length, icon: Link2 },
          { label: 'Pending', value: invitations.filter(i => String(i.status) === 'PENDING').length, icon: Mail },
        ].map(card => (
          <div key={card.label} className="kpi-card">
            <div className="flex items-center justify-between">
              <span className="kpi-title">{card.label}</span>
              <card.icon size={14} className="text-slate-400" />
            </div>
            <span className="kpi-value">{card.value}</span>
          </div>
        ))}
      </div>

      <div className="admin-tabs">
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`admin-tab ${activeTab === tab.id ? 'active' : ''}`}
          >
            {tab.label}
            {tab.count !== undefined && <span className="ml-1 text-[10px] text-slate-500">({tab.count})</span>}
          </button>
        ))}
      </div>

      {activeTab === 'overview' && (
        <div className="space-y-6">
          <div>
            <h3 className="text-sm font-semibold text-white mb-3">Members</h3>
            <div className="space-y-2">
              {members.slice(0, 5).map((m, i) => (
                <div key={i} className="flex items-center gap-3 rounded-lg border border-white/5 bg-white/[.03] p-3">
                  <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-violet-600/20 text-xs font-bold text-violet-300">
                    {String(m.displayName || m.userId || 'U').charAt(0).toUpperCase()}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-white truncate">{String(m.displayName || m.userId || 'User')}</p>
                    <p className="text-[10px] text-slate-500">{String(m.role || 'VIEWER')}</p>
                  </div>
                  {m.lastSeenAt && <span className="text-[10px] text-slate-500 shrink-0">{timeAgo(String(m.lastSeenAt))}</span>}
                </div>
              ))}
              {members.length > 5 && (
                <button onClick={() => setActiveTab('members')} className="text-xs text-violet-400 hover:underline">
                  +{members.length - 5} more members
                </button>
              )}
              {!members.length && <p className="text-xs text-slate-500">No members yet.</p>}
            </div>
          </div>

          <div>
            <h3 className="text-sm font-semibold text-white mb-3">Share Links</h3>
            <div className="space-y-2">
              {links.slice(0, 3).map((l, i) => (
                <div key={i} className="flex items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2">
                  <Link2 size={12} className="shrink-0 text-slate-500" />
                  <span className="flex-1 text-xs text-slate-300 truncate">{String(l.url || l.linkId || '—')}</span>
                  <span className="text-[10px] text-slate-500 shrink-0">{String(l.role || '')}</span>
                  {l.url && (
                    <button onClick={() => copyLink(String(l.url), String(l.id || i))} className="text-slate-400 hover:text-white shrink-0">
                      {copiedId === String(l.id || i) ? <Check size={12} className="text-emerald-400" /> : <Copy size={12} />}
                    </button>
                  )}
                </div>
              ))}
              {links.length > 3 && (
                <button onClick={() => setActiveTab('links')} className="text-xs text-violet-400 hover:underline">
                  +{links.length - 3} more links
                </button>
              )}
              {!links.length && <p className="text-xs text-slate-500">No share links.</p>}
            </div>
          </div>
        </div>
      )}

      {activeTab === 'members' && (
        <div className="overflow-x-auto rounded-lg border border-white/10">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
                <th className="px-4 py-2.5">User</th>
                <th className="px-4 py-2.5">Role</th>
                <th className="px-4 py-2.5">Joined</th>
                <th className="px-4 py-2.5">Last seen</th>
                <th className="px-4 py-2.5 w-20"></th>
              </tr>
            </thead>
            <tbody>
              {members.map((m, i) => (
                <tr key={i} className="group border-b border-white/5 hover:bg-white/[.02]">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-2">
                      <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-violet-600/20 text-[10px] font-bold text-violet-300">
                        {String(m.displayName || m.userId || 'U').charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <p className="text-sm text-white">{String(m.displayName || m.userId || 'User')}</p>
                        {m.email && <p className="text-[10px] text-slate-500">{String(m.email)}</p>}
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                      {String(m.role || 'VIEWER')}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs text-slate-400">
                    {m.joinedAt ? new Date(String(m.joinedAt)).toLocaleDateString() : '—'}
                  </td>
                  <td className="px-4 py-3 text-xs text-slate-400">
                    {m.lastSeenAt ? timeAgo(String(m.lastSeenAt)) : '—'}
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                      <button
                        onClick={() => setChangeRoleMember(m)}
                        className="flex h-6 w-6 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white"
                        title="Change role"
                        aria-label="Change role"
                      >
                        <Shield size={12} />
                      </button>
                      <button
                        onClick={() => setRemoveMemberTarget(m)}
                        className="flex h-6 w-6 items-center justify-center rounded hover:bg-rose-500/10 text-slate-400 hover:text-rose-300"
                        title="Remove member"
                        aria-label="Remove member"
                      >
                        <Trash2 size={12} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'links' && (
        <div className="overflow-x-auto rounded-lg border border-white/10">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
                <th className="px-4 py-2.5">Link</th>
                <th className="px-4 py-2.5">Role</th>
                <th className="px-4 py-2.5">Uses</th>
                <th className="px-4 py-2.5">Status</th>
                <th className="px-4 py-2.5 w-10"></th>
              </tr>
            </thead>
            <tbody>
              {links.map((l, i) => (
                <tr key={i} className="group border-b border-white/5 hover:bg-white/[.02]">
                  <td className="px-4 py-3 text-xs text-slate-300 truncate max-w-[300px]">{String(l.url || l.linkId || '—')}</td>
                  <td className="px-4 py-3">
                    <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                      {String(l.role || 'VIEWER')}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs text-slate-400">{(l as any).useCount ?? '—'}</td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${
                      String(l.status ?? 'ACTIVE') === 'ACTIVE' ? 'bg-emerald-500/15 text-emerald-400' :
                      String(l.status) === 'REVOKED' ? 'bg-rose-500/15 text-rose-400' :
                      'bg-slate-500/15 text-slate-400'
                    }`}>
                      {String(l.status || 'ACTIVE')}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-1">
                      {l.url && (
                        <button onClick={() => copyLink(String(l.url), String(l.id || i))} className="text-slate-400 hover:text-white" title="Copy link" aria-label="Copy link">
                          {copiedId === String(l.id || i) ? <Check size={13} className="text-emerald-400" /> : <Copy size={13} />}
                        </button>
                      )}
                      {String(l.status ?? 'ACTIVE') === 'ACTIVE' && (
                        <button
                          onClick={() => setRevokeLinkTarget(l)}
                          className="opacity-0 group-hover:opacity-100 text-slate-400 hover:text-amber-300 transition-opacity"
                          title="Revoke link"
                          aria-label="Revoke link"
                        >
                          <Trash2 size={13} />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'invitations' && (
        <div className="overflow-x-auto rounded-lg border border-white/10">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-white/10 text-left text-xs text-slate-500 uppercase tracking-wider">
                <th className="px-4 py-2.5">Invitee</th>
                <th className="px-4 py-2.5">Role</th>
                <th className="px-4 py-2.5">Sent</th>
                <th className="px-4 py-2.5">Status</th>
              </tr>
            </thead>
            <tbody>
              {invitations.map((inv, i) => (
                <tr key={i} className="border-b border-white/5 hover:bg-white/[.02]">
                  <td className="px-4 py-3 text-sm text-white">{String(inv.invitee || inv.email || '—')}</td>
                  <td className="px-4 py-3">
                    <span className="inline-flex items-center rounded-full bg-violet-500/15 px-2 py-0.5 text-[10px] font-medium text-violet-400">
                      {String(inv.role || 'VIEWER')}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-xs text-slate-400">
                    {inv.createdAt ? new Date(String(inv.createdAt)).toLocaleDateString() : '—'}
                  </td>
                  <td className="px-4 py-3">
                    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-[10px] font-medium ${
                      String(inv.status) === 'PENDING' ? 'bg-amber-500/15 text-amber-400' :
                      String(inv.status) === 'ACCEPTED' ? 'bg-emerald-500/15 text-emerald-400' :
                      'bg-slate-500/15 text-slate-400'
                    }`}>
                      {String(inv.status || 'PENDING')}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showCreateLink && (
        <CreateShareLinkModal
          workspaceId={workspaceId}
          workspaceName={String(workspace.name || workspace.title || 'Workspace')}
          onClose={() => setShowCreateLink(false)}
          onCreated={() => { void loadData(); }}
        />
      )}

      {showInvite && (
        <InviteMemberModal
          workspaceId={workspaceId}
          workspaceName={String(workspace.name || workspace.title || 'Workspace')}
          onClose={() => setShowInvite(false)}
          onInvited={() => { void loadData(); }}
        />
      )}

      {showClose && (
        <CloseWorkspaceModal
          workspaceId={workspaceId}
          workspaceName={String(workspace.name || workspace.title || 'Workspace')}
          memberCount={members.length}
          linkCount={links.length}
          onClose={() => setShowClose(false)}
          onClosed={() => router.push(`/${locale}/admin/sharing`)}
        />
      )}

      {changeRoleMember && (
        <ChangeRoleModal
          workspaceId={workspaceId}
          member={changeRoleMember}
          onClose={() => setChangeRoleMember(null)}
          onRoleChanged={() => { void loadData(); }}
        />
      )}

      {removeMemberTarget && (
        <RemoveMemberModal
          workspaceId={workspaceId}
          workspaceName={String(workspace.name || workspace.title || 'Workspace')}
          member={removeMemberTarget}
          onClose={() => setRemoveMemberTarget(null)}
          onRemoved={() => { void loadData(); }}
        />
      )}

      {revokeLinkTarget && (
        <RevokeLinkModal
          workspaceId={workspaceId}
          workspaceName={String(workspace.name || workspace.title || 'Workspace')}
          link={revokeLinkTarget}
          onClose={() => setRevokeLinkTarget(null)}
          onRevoked={() => { void loadData(); }}
        />
      )}
    </div>
  );
}
