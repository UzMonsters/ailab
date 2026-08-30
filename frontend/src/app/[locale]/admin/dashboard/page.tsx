'use client';
import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import { Activity, BookOpen, FlaskConical, Users, Zap, Download } from 'lucide-react';

const TABS = [
  { id: 'Overview', label: 'Overview', icon: <Activity size={18} /> },
  { id: 'Learning', label: 'Learning', icon: <BookOpen size={18} /> },
  { id: 'Laboratories', label: 'Laboratories', icon: <FlaskConical size={18} /> },
  { id: 'Science', label: 'Science', icon: <Zap size={18} /> },
  { id: 'Activity', label: 'Activity', icon: <Users size={18} /> },
];

const mockActiveLabs = [
  { id: '1', workspace: 'Organic Synthesis', subject: 'Chemistry', owner: 'Jasur', objects: 8, runtime: '24m', status: 'Running' },
  { id: '2', workspace: 'Optics #12', subject: 'Physics', owner: 'Dmitry', objects: 5, runtime: '9m', status: 'Paused' },
  { id: '3', workspace: 'Cell Structure', subject: 'Biology', owner: 'Anna', objects: 12, runtime: '45m', status: 'Running' },
  { id: '4', workspace: 'Acid-Base Titration', subject: 'Chemistry', owner: 'Elena', objects: 6, runtime: '12m', status: 'Running' },
  { id: '5', workspace: 'Kinematics Lab', subject: 'Physics', owner: 'Alex', objects: 3, runtime: '5m', status: 'Running' },
];

const activeLabsCols: Column<typeof mockActiveLabs[0]>[] = [
  { header: 'Workspace', accessorKey: 'workspace', sortable: true },
  { header: 'Subject', accessorKey: 'subject', sortable: true, cell: (item) => (
    <span className={`px-2 py-1 rounded text-xs ${
      item.subject === 'Chemistry' ? 'bg-purple-500/20 text-purple-400' :
      item.subject === 'Physics' ? 'bg-cyan-500/20 text-cyan-400' :
      'bg-emerald-500/20 text-emerald-400'
    }`}>{item.subject}</span>
  )},
  { header: 'Owner', accessorKey: 'owner', sortable: true },
  { header: 'Objects', accessorKey: 'objects', sortable: true },
  { header: 'Runtime', accessorKey: 'runtime', sortable: true },
  { header: 'Status', accessorKey: 'status', sortable: true, cell: (item) => (
    <span className={item.status === 'Running' ? 'text-emerald-500' : 'text-amber-500'}>
      {item.status}
    </span>
  )}
];

const mockLearningActivity = [
  { id: '1', student: 'Mikhail', lesson: 'Boyle\'s Law', score: 85, completedAt: '2h ago' },
  { id: '2', student: 'Sarah', lesson: 'Titration Basics', score: 92, completedAt: '3h ago' },
  { id: '3', student: 'John', lesson: 'Newton\'s Laws', score: 78, completedAt: '5h ago' },
];

const learningCols: Column<typeof mockLearningActivity[0]>[] = [
  { header: 'Student', accessorKey: 'student', sortable: true },
  { header: 'Lesson', accessorKey: 'lesson', sortable: true },
  { header: 'Score', accessorKey: 'score', sortable: true, cell: (item) => (
    <div className="flex items-center gap-2">
      <div className="w-16 h-2 bg-[#141b2a] rounded-full overflow-hidden">
        <div className="h-full bg-indigo-500" style={{ width: `${item.score}%` }}></div>
      </div>
      <span>{item.score}%</span>
    </div>
  )},
  { header: 'Completed', accessorKey: 'completedAt', sortable: true },
];

// Simple Bar Chart Component using CSS
function SimpleBarChart({ data }: { data: { label: string; value: number; color?: string }[] }) {
  const max = Math.max(...data.map(d => d.value));
  return (
    <div className="flex items-end gap-4 h-full pt-4">
      {data.map((item, i) => (
        <div key={i} className="flex flex-col items-center flex-1 gap-2 h-full justify-end group">
          <div className="text-xs text-gray-400 opacity-0 group-hover:opacity-100 transition-opacity">
            {item.value}
          </div>
          <div 
            className="w-full rounded-t-sm transition-all duration-500"
            style={{ 
              height: `${(item.value / max) * 100}%`,
              backgroundColor: item.color || '#8b5cf6' 
            }}
          />
          <div className="text-xs text-gray-500 truncate w-full text-center">
            {item.label}
          </div>
        </div>
      ))}
    </div>
  );
}

export default function AdminDashboardPage() {
  const [activeTab, setActiveTab] = useState('Overview');

  return (
    <div className="pb-12 space-y-6">
      <AdminPageHeader 
        title="Admin Dashboard" 
        description="Comprehensive overview of platform usage and metrics."
        actions={
          <button className="flex items-center gap-2 bg-[#141b2a] hover:bg-[#1a2235] text-white px-4 py-2 rounded-lg border border-[rgba(255,255,255,0.1)] transition-colors">
            <Download size={16} /> Export Report
          </button>
        }
      />

      <div className="flex overflow-x-auto gap-2 pb-2 mb-6 border-b border-[rgba(255,255,255,0.05)] scrollbar-hide">
        {TABS.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-lg whitespace-nowrap transition-colors ${
              activeTab === tab.id 
                ? 'bg-[#8b5cf6] text-white font-medium' 
                : 'text-[#8490a3] hover:text-white hover:bg-[rgba(255,255,255,0.05)]'
            }`}
          >
            {tab.icon}
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'Overview' && (
        <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
            {[
              { label: 'Total Users', value: '12,481', trend: '+8.2%', isUp: true },
              { label: 'Active Labs', value: '1,284', trend: '+142', isUp: true },
              { label: 'Experiments', value: '48,210', trend: '+12.4%', isUp: true },
              { label: 'Avg. Score', value: '74.2%', trend: '+3.1%', isUp: true },
              { label: 'Safety Incidents', value: '38', trend: '-11%', isUp: false },
            ].map((kpi, i) => (
              <div key={i} className="bg-[#0b101a] p-5 rounded-xl border border-[rgba(255,255,255,0.05)]">
                <div className="text-[#8490a3] text-sm mb-2">{kpi.label}</div>
                <div className="text-2xl font-bold text-white mb-2">{kpi.value}</div>
                <div className={`text-xs ${kpi.isUp ? 'text-emerald-500' : 'text-red-500'}`}>
                  {kpi.trend} from last month
                </div>
              </div>
            ))}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl flex flex-col">
              <div className="p-5 border-b border-[rgba(255,255,255,0.05)] flex justify-between items-center">
                <h3 className="font-bold text-white">Platform Activity</h3>
                <div className="flex gap-1 text-xs">
                  {['7D', '30D', '3M', '1Y'].map(tf => (
                    <button key={tf} className={`px-2 py-1 rounded ${tf === '30D' ? 'bg-[#8b5cf6] text-white' : 'bg-[#141b2a] text-[#8490a3] hover:text-white'}`}>
                      {tf}
                    </button>
                  ))}
                </div>
              </div>
              <div className="p-5 h-[300px]">
                <SimpleBarChart data={[
                  { label: 'Mon', value: 450, color: '#6366f1' },
                  { label: 'Tue', value: 520, color: '#6366f1' },
                  { label: 'Wed', value: 380, color: '#6366f1' },
                  { label: 'Thu', value: 610, color: '#6366f1' },
                  { label: 'Fri', value: 590, color: '#6366f1' },
                  { label: 'Sat', value: 240, color: '#6366f1' },
                  { label: 'Sun', value: 290, color: '#6366f1' },
                ]} />
              </div>
            </div>

            <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl flex flex-col">
              <div className="p-5 border-b border-[rgba(255,255,255,0.05)]">
                <h3 className="font-bold text-white">Science Distribution</h3>
              </div>
              <div className="p-6 flex-1 flex flex-col items-center justify-center">
                <div className="relative w-40 h-40 mb-8">
                  {/* CSS Pie Chart Mock */}
                  <div className="absolute inset-0 rounded-full border-[12px] border-[#22d3ee] border-t-[#a78bfa] border-l-[#a78bfa] border-r-[#34d399] rotate-45"></div>
                  <div className="absolute inset-0 flex items-center justify-center text-xl font-bold text-white">
                    Labs
                  </div>
                </div>
                <div className="w-full space-y-3">
                  <div className="flex justify-between text-sm"><div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-[#a78bfa]" /> <span className="text-[#8490a3]">Chemistry</span></div><span className="text-white">58%</span></div>
                  <div className="flex justify-between text-sm"><div className="flex items-center gap-2"><div className="w-3 h-3 rounded-full bg-[#a78bfa]" /> <span className="text-[#8490a3]">Chemistry</span></div><span className="text-white">100%</span></div>
                </div>
              </div>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-bold text-white mb-4">Active Laboratories</h3>
            <AdminDataTable data={mockActiveLabs} columns={activeLabsCols} />
          </div>
        </div>
      )}

      {activeTab === 'Learning' && (
        <div className="space-y-6 animate-in fade-in duration-500">
           <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
              <div className="bg-[#0b101a] p-6 rounded-xl border border-[rgba(255,255,255,0.05)]">
                <h4 className="text-[#8490a3] text-sm mb-1">Total Enrollments</h4>
                <div className="text-3xl font-bold text-white">32,104</div>
              </div>
              <div className="bg-[#0b101a] p-6 rounded-xl border border-[rgba(255,255,255,0.05)]">
                <h4 className="text-[#8490a3] text-sm mb-1">Avg Completion Time</h4>
                <div className="text-3xl font-bold text-white">45 mins</div>
              </div>
              <div className="bg-[#0b101a] p-6 rounded-xl border border-[rgba(255,255,255,0.05)]">
                <h4 className="text-[#8490a3] text-sm mb-1">Success Rate</h4>
                <div className="text-3xl font-bold text-emerald-400">89.4%</div>
              </div>
           </div>
           <div>
             <h3 className="text-lg font-bold text-white mb-4">Recent Learning Activity</h3>
             <AdminDataTable data={mockLearningActivity} columns={learningCols} searchPlaceholder="Search students..." />
           </div>
        </div>
      )}

      {activeTab === 'Laboratories' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 animate-in fade-in duration-500">
          {[['Active now','1,284','+12.4%'],['Chemistry labs','742','58% of total'],['Physics + Biology','542','42% of total']].map(([label,value,meta]) => <div key={label} className="bg-[#0b101a] p-6 rounded-xl border border-white/[.06]"><p className="text-sm text-[#8490a3]">{label}</p><p className="mt-2 text-3xl font-bold text-white">{value}</p><p className="mt-2 text-xs text-emerald-400">{meta}</p></div>)}
          <div className="md:col-span-3"><h3 className="mb-4 text-lg font-bold text-white">Active laboratory sessions</h3><AdminDataTable data={mockActiveLabs} columns={activeLabsCols} /></div>
        </div>
      )}

      {activeTab === 'Science' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 animate-in fade-in duration-500">
          {[['Chemistry','118 elements · 64 compounds']].map(([label,meta]) => <div key={label} className="bg-[#0b101a] p-6 rounded-xl border border-white/[.06]"><div className="mb-4 grid h-10 w-10 place-items-center rounded-xl bg-violet-400/15 text-violet-300"><Zap size={18}/></div><p className="text-lg font-bold text-white">{label}</p><p className="mt-2 text-sm text-[#8490a3]">{meta}</p><p className="mt-5 text-xs text-emerald-400">Engine online · 99.98%</p></div>)}
        </div>
      )}

      {activeTab === 'Activity' && (
        <div className="space-y-6 animate-in fade-in duration-500">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">{[['Online now','248'],['Experiments today','1,842'],['Lessons completed','936'],['Avg. session','28m']].map(([label,value]) => <div key={label} className="bg-[#0b101a] p-5 rounded-xl border border-white/[.06]"><p className="text-sm text-[#8490a3]">{label}</p><p className="mt-2 text-2xl font-bold text-white">{value}</p></div>)}</div>
          <div><h3 className="mb-4 text-lg font-bold text-white">Recent learning activity</h3><AdminDataTable data={mockLearningActivity} columns={learningCols} /></div>
        </div>
      )}
    </div>
  );
}
