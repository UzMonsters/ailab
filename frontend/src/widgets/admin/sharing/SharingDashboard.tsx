'use client';

import { useState, useEffect } from 'react';
import { Boxes, Users, Link2, Mail, RefreshCw, Activity } from 'lucide-react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { WorkspacesTable } from './workspaces/WorkspacesTable';
import { MembershipsTable } from './members/MembershipsTable';
import { ShareLinksTable } from './links/ShareLinksTable';
import { InvitationsTable } from './invitations/InvitationsTable';
import { ActivityLog } from './activity/ActivityLog';

type Tab = 'workspaces' | 'members' | 'links' | 'invitations' | 'activity';

export function SharingDashboard() {
  const [activeTab, setActiveTab] = useState<Tab>('workspaces');
  const [stats, setStats] = useState({ workspaces: 0, members: 0, links: 0, invitations: 0 });
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const response = await adminPlatformApi.workspaces.list({ size: 100 });
        const wsList = response.items ?? [];
        const totalMembers = wsList.reduce((sum, workspace) => sum + Number(workspace.memberCount ?? 0), 0);
        const totalLinks = wsList.reduce((sum, workspace) => sum + Number(workspace.activeShareLinkCount ?? 0), 0);
        const totalInvitations = wsList.reduce((sum, workspace) => sum + Number(workspace.pendingInvitationCount ?? 0), 0);
        if (!cancelled) {
          setStats({ workspaces: wsList.length, members: totalMembers, links: totalLinks, invitations: totalInvitations });
          setLoading(false);
        }
      } catch (err) {
        console.error('Failed to load sharing stats', err);
        if (!cancelled) setLoading(false);
      }
    })();
    return () => { cancelled = true; };
  }, []);

  const handleRefresh = async () => {
    setRefreshing(true);
    setLoading(true);
    try {
      const response = await adminPlatformApi.workspaces.list({ size: 100 });
      const wsList = response.items ?? [];
      const totalMembers = wsList.reduce((sum, workspace) => sum + Number(workspace.memberCount ?? 0), 0);
      const totalLinks = wsList.reduce((sum, workspace) => sum + Number(workspace.activeShareLinkCount ?? 0), 0);
      const totalInvitations = wsList.reduce((sum, workspace) => sum + Number(workspace.pendingInvitationCount ?? 0), 0);
      setStats({ workspaces: wsList.length, members: totalMembers, links: totalLinks, invitations: totalInvitations });
    } catch (err) {
      console.error('Failed to refresh sharing stats', err);
    }
    setLoading(false);
    setRefreshing(false);
  };

  const statCards = [
    { label: 'Workspaces', value: stats.workspaces, icon: Boxes, color: 'text-violet-400', tab: 'workspaces' as Tab },
    { label: 'Members', value: stats.members, icon: Users, color: 'text-cyan-400', tab: 'members' as Tab },
    { label: 'Active Links', value: stats.links, icon: Link2, color: 'text-emerald-400', tab: 'links' as Tab },
    { label: 'Invitations', value: stats.invitations, icon: Mail, color: 'text-amber-400', tab: 'invitations' as Tab },
  ];

  const tabs: { id: Tab; label: string }[] = [
    { id: 'workspaces', label: 'Workspaces' },
    { id: 'members', label: 'Members' },
    { id: 'links', label: 'Share Links' },
    { id: 'invitations', label: 'Invitations' },
    { id: 'activity', label: 'Activity' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-start justify-between">
        <div>
          <h1 className="page-title">Collaboration</h1>
          <p className="page-subtitle">Monitor workspaces, members and shared access.</p>
        </div>
        <button onClick={() => void handleRefresh()} disabled={refreshing} className="admin-btn">
          <RefreshCw size={14} className={refreshing ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
        {statCards.map(card => (
          <button
            key={card.label}
            onClick={() => setActiveTab(card.tab)}
            className={`kpi-card text-left transition-all hover:border-violet-500/30 ${
              activeTab === card.tab ? 'border-violet-500/40 ring-1 ring-violet-500/20' : ''
            }`}
          >
            <div className="flex items-center justify-between">
              <span className="kpi-title">{card.label}</span>
              <card.icon size={16} className={card.color} />
            </div>
            <span className="kpi-value">{loading ? '—' : card.value}</span>
          </button>
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
          </button>
        ))}
      </div>

      <div>
        {activeTab === 'workspaces' && <WorkspacesTable />}
        {activeTab === 'members' && <MembershipsTable />}
        {activeTab === 'links' && <ShareLinksTable />}
        {activeTab === 'invitations' && <InvitationsTable />}
        {activeTab === 'activity' && <ActivityLog />}
      </div>
    </div>
  );
}
