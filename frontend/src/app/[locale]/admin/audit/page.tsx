'use client';

import React, { useState } from 'react';
import { ShieldAlert, Activity, Users, Zap, AlertTriangle, UserCheck, Shield, FileText } from 'lucide-react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { mockAuditEvents } from '@/mocks/admin/audit';

type AuditEvent = {
  id: string;
  time: string;
  date?: string;
  actor: string;
  action: string;
  entity: string;
  subject: string;
  source: string;
  result: string;
  severity: string;
};

export default function AuditPage() {
  const [activeTab, setActiveTab] = useState('All');
  const [selectedEvent, setSelectedEvent] = useState<AuditEvent | null>(null);

  const tabs = ['All', 'Admins', 'Users', 'Sandbox', 'Content', 'Safety', 'System'];

  const filteredEvents = mockAuditEvents.filter((evt: any) => {
    if (activeTab === 'All') return true;
    if (activeTab === 'Admins') return evt.actor.includes('Admin');
    if (activeTab === 'Users') return evt.source === 'User' || (!evt.actor.includes('Admin') && !evt.actor.includes('System') && !evt.actor.includes('Engine'));
    if (activeTab === 'Sandbox') return evt.source === 'Sandbox';
    if (activeTab === 'Content') return evt.action.includes('scenario') || evt.subject.includes('Content');
    if (activeTab === 'Safety') return evt.action.includes('safety') || evt.severity === 'Critical';
    if (activeTab === 'System') return evt.source === 'System' || evt.actor === 'System';
    return true;
  });

  const columns: Column<AuditEvent>[] = [
    { header: 'Time', accessorKey: 'time', sortable: true },
    { header: 'Actor', accessorKey: 'actor', sortable: true, cell: row => (
      <span className="font-medium text-white">{row.actor}</span>
    )},
    { header: 'Action', accessorKey: 'action', sortable: true },
    { header: 'Entity', accessorKey: 'entity', sortable: true },
    { header: 'Subject', accessorKey: 'subject', sortable: true },
    { header: 'Source', accessorKey: 'source', sortable: true },
    { header: 'Result', accessorKey: 'result', sortable: true, cell: row => (
      <span className={`px-2 py-1 rounded text-xs font-bold ${
        row.result === 'Success' ? 'bg-emerald-500/10 text-emerald-400' :
        row.result === 'Failed' || row.result === 'Error' ? 'bg-red-500/10 text-red-400' :
        'bg-amber-500/10 text-amber-400'
      }`}>
        {row.result}
      </span>
    )},
    { header: 'Severity', accessorKey: 'severity', sortable: true, cell: row => (
      <span className={`px-2 py-1 rounded text-xs font-bold ${
        row.severity === 'Critical' ? 'bg-red-500/10 text-red-400' :
        row.severity === 'High' ? 'bg-orange-500/10 text-orange-400' :
        row.severity === 'Medium' ? 'bg-amber-500/10 text-amber-400' :
        'bg-blue-500/10 text-blue-400'
      }`}>
        {row.severity}
      </span>
    )}
  ];

  // Mock metadata generation for selected event
  const getMockMetadata = (evt: AuditEvent) => {
    return {
      eventId: evt.id,
      timestamp: `${evt.date}T${evt.time}Z`,
      sessionInfo: {
        locale: "en-US",
        ip: "192.168.1.104",
        browser: "Chrome/114.0.0 (Windows NT 10.0; Win64; x64)",
      },
      changes: {
        before: { status: "inactive", target_id: null },
        after: { status: "active", target_id: evt.entity }
      },
      requestHeaders: {
        "x-forwarded-for": "192.168.1.104",
        "user-agent": "Mozilla/5.0",
        "authorization": "Bearer ***"
      }
    };
  };

  return (
    <div className="p-6 md:p-10 max-w-[1600px] mx-auto">
      <AdminPageHeader 
        title="Audit Log" 
        description="Monitor system events, admin actions, and simulation activity across the platform."
      />

      {/* KPI Cards */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-8">
        <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-5 flex flex-col">
          <div className="flex items-center gap-3 text-[#8490a3] mb-3">
            <Activity className="w-5 h-5 text-[#8b5cf6]" />
            <span className="text-sm font-medium">EVENTS TODAY</span>
          </div>
          <span className="text-2xl font-bold text-white">1,248</span>
        </div>
        <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-5 flex flex-col">
          <div className="flex items-center gap-3 text-[#8490a3] mb-3">
            <UserCheck className="w-5 h-5 text-[#10b981]" />
            <span className="text-sm font-medium">ADMIN ACTIONS</span>
          </div>
          <span className="text-2xl font-bold text-white">42</span>
        </div>
        <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-5 flex flex-col">
          <div className="flex items-center gap-3 text-[#8490a3] mb-3">
            <Zap className="w-5 h-5 text-[#3b82f6]" />
            <span className="text-sm font-medium">SANDBOX EVENTS</span>
          </div>
          <span className="text-2xl font-bold text-white">890</span>
        </div>
        <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-5 flex flex-col">
          <div className="flex items-center gap-3 text-[#8490a3] mb-3">
            <ShieldAlert className="w-5 h-5 text-[#f59e0b]" />
            <span className="text-sm font-medium">SAFETY EVENTS</span>
          </div>
          <span className="text-2xl font-bold text-white">14</span>
        </div>
        <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-5 flex flex-col">
          <div className="flex items-center gap-3 text-[#8490a3] mb-3">
            <AlertTriangle className="w-5 h-5 text-[#ef4444]" />
            <span className="text-sm font-medium">FAILED ACTIONS</span>
          </div>
          <span className="text-2xl font-bold text-white">3</span>
        </div>
      </div>

      <div className="flex items-center gap-2 mb-6 border-b border-[rgba(255,255,255,0.05)] pb-px overflow-x-auto">
        {tabs.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 border-b-2 font-medium text-sm transition-colors whitespace-nowrap ${
              activeTab === tab 
                ? 'border-[#8b5cf6] text-[#8b5cf6]' 
                : 'border-transparent text-[#8490a3] hover:text-white'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      <AdminDataTable 
        data={filteredEvents}
        columns={columns}
        searchPlaceholder="Search audit events..."
        onRowClick={(row) => setSelectedEvent(row as AuditEvent)}
      />

      <AdminDrawer 
        isOpen={!!selectedEvent} 
        onClose={() => setSelectedEvent(null)}
        title="Audit Event Details"
      >
        {selectedEvent && (
          <div className="space-y-6">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <span className="text-[#8490a3] text-xs uppercase">Event ID</span>
                <p className="text-white font-mono mt-1">{selectedEvent.id}</p>
              </div>
              <div>
                <span className="text-[#8490a3] text-xs uppercase">Timestamp</span>
                <p className="text-white mt-1">{selectedEvent.date} {selectedEvent.time}</p>
              </div>
              <div>
                <span className="text-[#8490a3] text-xs uppercase">Actor</span>
                <p className="text-white mt-1">{selectedEvent.actor}</p>
              </div>
              <div>
                <span className="text-[#8490a3] text-xs uppercase">Result</span>
                <p className="text-white mt-1">{selectedEvent.result}</p>
              </div>
            </div>

            <div className="pt-4 border-t border-[rgba(255,255,255,0.05)]">
              <span className="text-[#8490a3] text-xs uppercase mb-2 block">Action Context</span>
              <div className="bg-[#141b2a] rounded-lg p-4 font-mono text-sm text-[#a9a5b8] overflow-x-auto">
                <p><span className="text-[#8b5cf6]">Action:</span> {selectedEvent.action}</p>
                <p><span className="text-[#8b5cf6]">Entity:</span> {selectedEvent.entity}</p>
                <p><span className="text-[#8b5cf6]">Subject:</span> {selectedEvent.subject}</p>
                <p><span className="text-[#8b5cf6]">Source:</span> {selectedEvent.source}</p>
                <p><span className="text-[#8b5cf6]">Severity:</span> {selectedEvent.severity}</p>
              </div>
            </div>

            <div className="pt-4 border-t border-[rgba(255,255,255,0.05)]">
              <span className="text-[#8490a3] text-xs uppercase mb-2 flex items-center gap-2">
                <FileText className="w-3.5 h-3.5" />
                Metadata (JSON)
              </span>
              <div className="bg-[#141b2a] rounded-lg p-4 font-mono text-sm text-[#a9a5b8] overflow-x-auto whitespace-pre border border-[rgba(255,255,255,0.02)]">
                {JSON.stringify(getMockMetadata(selectedEvent), null, 2)}
              </div>
            </div>
          </div>
        )}
      </AdminDrawer>
    </div>
  );
}
