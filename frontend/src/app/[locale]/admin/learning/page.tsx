'use client';
import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { mockLevels } from '@/mocks/admin/learning';
import { useParams } from 'next/navigation';
import { LearningPanel } from '@/widgets/admin/AdminCatalogPanels';
import { 
  BookOpen, 
  Layers, 
  Award, 
  CheckCircle2, 
  Globe, 
  TrendingUp,
  BarChart3,
  Beaker,
  Atom,
  Dna
} from 'lucide-react';
import AdminDataTable from '@/widgets/admin/AdminDataTable';

const tabs = [
  { id: 'overview', label: 'Overview', icon: <BarChart3 size={16} /> },
  { id: 'levels', label: 'Levels', icon: <Layers size={16} /> },
  { id: 'chapters', label: 'Chapters', icon: <BookOpen size={16} /> },
  { id: 'tasks', label: 'Tasks', icon: <CheckCircle2 size={16} /> },
  { id: 'rewards', label: 'Rewards', icon: <Award size={16} /> },
  { id: 'progress', label: 'Progress', icon: <TrendingUp size={16} /> },
  { id: 'localization', label: 'Localization', icon: <Globe size={16} /> },
];

export default function AdminLearningDashboard() {
  const params = useParams();
  const locale = typeof params.locale === 'string' ? params.locale : 'ru';
  const [activeTab, setActiveTab] = useState('overview');
  const [timeframe, setTimeframe] = useState('7D');

  const totalLevels = mockLevels.length;
  const publishedCount = mockLevels.filter(l => l.status === 'Published').length;
  const draftCount = mockLevels.filter(l => l.status === 'Draft').length;

  const recentContentColumns = [
    { header: 'Title', accessorKey: 'title' as const },
    { header: 'Subject', accessorKey: 'subject' as const },
    { header: 'Status', accessorKey: 'status' as const },
    { header: 'Last Modified', accessorKey: 'lastModified' as const }
  ];
  
  const recentContent = mockLevels.slice(0, 4).map((l, index) => ({
    ...l,
    lastModified: new Date(1718000000000 - index * 86400000).toLocaleDateString()
  }));

  return (
    <div className="p-4 md:p-6 min-h-screen bg-[#070b14] text-white">
      <AdminPageHeader 
        title="Learning Content" 
        description="Manage curriculum, track engagement, and oversee content creation."
      />

      <div className="flex space-x-1 border-b border-[rgba(255,255,255,0.05)] mb-6 overflow-x-auto pb-px">
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 px-4 py-2.5 text-sm font-medium whitespace-nowrap border-b-2 transition-colors ${
              activeTab === tab.id 
                ? 'border-[#8b5cf6] text-white' 
                : 'border-transparent text-[#8490a3] hover:text-white hover:border-[rgba(255,255,255,0.1)]'
            }`}
          >
            {tab.icon}
            {tab.label}
          </button>
        ))}
      </div>

      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Curriculum Overview Cards */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] p-4 rounded-xl flex items-center gap-4">
              <div className="p-3 bg-cyan-500/10 rounded-lg">
                <Beaker className="text-cyan-500" size={24} />
              </div>
              <div>
                <p className="text-[#8490a3] text-sm">Chemistry</p>
                <div className="flex items-end gap-2">
                  <h3 className="text-2xl font-bold">24</h3>
                  <span className="text-sm text-[#8490a3] mb-1">Levels</span>
                </div>
              </div>
            </div>
            
            <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] p-4 rounded-xl flex items-center gap-4">
              <div className="p-3 bg-purple-500/10 rounded-lg">
                <Atom className="text-purple-500" size={24} />
              </div>
              <div>
                <p className="text-[#8490a3] text-sm">Physics</p>
                <div className="flex items-end gap-2">
                  <h3 className="text-2xl font-bold">18</h3>
                  <span className="text-sm text-[#8490a3] mb-1">Levels</span>
                </div>
              </div>
            </div>
            
            <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] p-4 rounded-xl flex items-center gap-4">
              <div className="p-3 bg-green-500/10 rounded-lg">
                <Dna className="text-green-500" size={24} />
              </div>
              <div>
                <p className="text-[#8490a3] text-sm">Biology</p>
                <div className="flex items-end gap-2">
                  <h3 className="text-2xl font-bold">12</h3>
                  <span className="text-sm text-[#8490a3] mb-1">Levels</span>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-4 min-h-[300px] flex flex-col">
              <div className="flex justify-between items-center mb-4">
                <h3 className="font-semibold text-white">Student Engagement</h3>
                <div className="flex gap-2">
                  {['7D', '30D', '90D'].map(t => (
                    <button 
                      key={t}
                      onClick={() => setTimeframe(t)}
                      className={`px-3 py-1 text-xs rounded-md ${timeframe === t ? 'bg-[#8b5cf6] text-white' : 'bg-[#141b2a] text-[#8490a3]'}`}
                    >
                      {t}
                    </button>
                  ))}
                </div>
              </div>
              <div className="flex-1 flex flex-col justify-end rounded-lg border border-[rgba(255,255,255,0.07)] bg-gradient-to-b from-violet-500/[.04] to-transparent p-4 text-[#8490a3]">
                <div className="flex h-40 items-end gap-2 md:gap-3">{[38,52,47,69,62,84,73,91,78,96,86,100].map((height,index) => <div key={index} className="group relative flex h-full flex-1 items-end"><div className="w-full rounded-t bg-gradient-to-t from-violet-600/70 to-cyan-400 transition-opacity group-hover:opacity-80" style={{height:`${height}%`}}/><span className="absolute -bottom-5 left-1/2 -translate-x-1/2 text-[10px]">{index + 1}</span></div>)}</div>
                <div className="flex flex-wrap gap-4 mt-9 text-xs">
                  <span className="flex items-center gap-1"><div className="w-2 h-2 bg-blue-500 rounded-full"></div> Active Learners</span>
                  <span className="flex items-center gap-1"><div className="w-2 h-2 bg-green-500 rounded-full"></div> Completed Levels</span>
                  <span className="flex items-center gap-1"><div className="w-2 h-2 bg-purple-500 rounded-full"></div> Scenario Attempts</span>
                </div>
              </div>
            </div>

            <div className="lg:col-span-1 bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-4 flex flex-col">
              <h3 className="font-semibold mb-4 text-white">Curriculum Completion</h3>
              <div className="flex-1 flex flex-col gap-6 justify-center">
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-[#8490a3]">Beginner Curriculum</span>
                    <span className="text-white">78%</span>
                  </div>
                  <div className="h-2 bg-[#141b2a] rounded-full overflow-hidden">
                    <div className="h-full bg-cyan-500 rounded-full" style={{ width: '78%' }}></div>
                  </div>
                </div>
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-[#8490a3]">Intermediate Curriculum</span>
                    <span className="text-white">45%</span>
                  </div>
                  <div className="h-2 bg-[#141b2a] rounded-full overflow-hidden">
                    <div className="h-full bg-purple-500 rounded-full" style={{ width: '45%' }}></div>
                  </div>
                </div>
                <div>
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-[#8490a3]">Advanced Curriculum</span>
                    <span className="text-white">12%</span>
                  </div>
                  <div className="h-2 bg-[#141b2a] rounded-full overflow-hidden">
                    <div className="h-full bg-pink-500 rounded-full" style={{ width: '12%' }}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl overflow-hidden">
            <div className="p-4 border-b border-[rgba(255,255,255,0.05)]">
              <h3 className="font-semibold text-white">Recent Content Updates</h3>
            </div>
            <AdminDataTable 
              data={recentContent} 
              columns={recentContentColumns} 
              itemsPerPageOptions={[4]}
              searchPlaceholder="Search recent content..."
            />
          </div>
        </div>
      )}

      {['levels', 'chapters', 'tasks', 'rewards', 'progress', 'localization'].includes(activeTab) && (
        <LearningPanel tab={activeTab} locale={locale} />
      )}

    </div>
  );
}
