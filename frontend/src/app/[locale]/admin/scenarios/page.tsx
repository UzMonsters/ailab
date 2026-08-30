'use client';

import { useState } from 'react';
import { useTranslations } from 'next-intl';
import { mockScenarios } from '@/mocks/admin/scenarios';
import { 
  Plus, Search, MoreVertical, Edit3, Trash2, CheckCircle, FileText, 
  BookOpen, Beaker, Zap, Activity, Filter, ArrowUpRight, BarChart3, Users
} from 'lucide-react';
import Link from 'next/link';

export default function AdminScenariosPage() {
  const t = useTranslations('admin');
  
  const [search, setSearch] = useState('');
  const [activeTab, setActiveTab] = useState('All');
  
  const filtered = mockScenarios.filter((s) => {
    const matchSearch = s.title.toLowerCase().includes(search.toLowerCase());
    const matchTab = 
      activeTab === 'All' ? true :
      activeTab === 'Drafts' ? s.status === 'Draft' :
      s.subject === activeTab;
    
    return matchSearch && matchTab;
  });

  const kpis = [
    { title: 'Total Scenarios', value: mockScenarios.length, change: '+12%', icon: BookOpen, color: 'text-blue-500', bg: 'bg-blue-500/10' },
    { title: 'Published', value: mockScenarios.filter(s => s.status === 'Published').length, change: '+5%', icon: CheckCircle, color: 'text-green-500', bg: 'bg-green-500/10' },
    { title: 'Avg. Completion', value: '68%', change: '+2.4%', icon: BarChart3, color: 'text-purple-500', bg: 'bg-purple-500/10' },
    { title: 'Active Players', value: '1,240', change: '+18%', icon: Users, color: 'text-orange-500', bg: 'bg-orange-500/10' },
  ];

  return (
    <div className="p-4 md:p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-[var(--foreground)]">Scenarios</h1>
          <p className="text-sm text-[var(--muted-foreground)] mt-1">Manage laboratory scenarios and experiments.</p>
        </div>
        <Link href="./scenarios/new" className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold flex items-center gap-2 shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:-translate-y-0.5 transition-all">
          <Plus size={16} /> New Scenario
        </Link>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {kpis.map((kpi, i) => (
          <div key={i} className="p-5 rounded-2xl bg-[var(--card)] border border-[var(--border)] flex items-center justify-between">
            <div>
              <p className="text-sm text-[var(--muted-foreground)] mb-1">{kpi.title}</p>
              <h3 className="text-2xl font-bold text-[var(--foreground)]">{kpi.value}</h3>
              <p className="text-xs text-emerald-500 flex items-center gap-1 mt-1">
                <ArrowUpRight size={12} /> {kpi.change}
              </p>
            </div>
            <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${kpi.bg} ${kpi.color}`}>
              <kpi.icon size={24} />
            </div>
          </div>
        ))}
      </div>

      {/* Main Content Area */}
      <div className="bg-[var(--card)] border border-[var(--border)] rounded-2xl overflow-hidden">
        
        {/* Tabs & Toolbar */}
        <div className="border-b border-[var(--border)] p-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
          
          <div className="flex items-center gap-1 overflow-x-auto pb-1 md:pb-0 hide-scrollbar">
            {['All', 'Chemistry', 'Physics', 'Biology', 'Drafts'].map(tab => (
              <button 
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all ${activeTab === tab ? 'bg-white/10 text-[var(--foreground)]' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/5'}`}
              >
                {tab}
              </button>
            ))}
          </div>

          <div className="flex items-center gap-3">
            <div className="relative">
              <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
              <input 
                type="text" 
                placeholder="Search..." 
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-full md:w-64 py-2 pl-9 pr-4 bg-[var(--input)] border border-[var(--border)] rounded-full text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] transition-all"
              />
            </div>
            <button className="p-2 border border-[var(--border)] rounded-full text-[var(--muted-foreground)] hover:bg-white/5 transition-all">
              <Filter size={16} />
            </button>
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="text-[var(--muted-foreground)] bg-black/20">
              <tr>
                <th className="py-4 px-6 font-medium">Scenario Title</th>
                <th className="py-4 px-6 font-medium">Subject</th>
                <th className="py-4 px-6 font-medium hidden md:table-cell">Metrics</th>
                <th className="py-4 px-6 font-medium hidden sm:table-cell">Status</th>
                <th className="py-4 px-6 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[var(--border)]">
              {filtered.map(scenario => (
                <tr key={scenario.id} className="hover:bg-white/[0.02] transition-colors group">
                  <td className="py-4 px-6">
                    <div className="flex items-center gap-4">
                      <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0 ${
                        scenario.subject === 'Chemistry' ? 'bg-purple-500/20 text-purple-400' :
                        scenario.subject === 'Physics' ? 'bg-blue-500/20 text-blue-400' :
                        'bg-green-500/20 text-green-400'
                      }`}>
                        {scenario.subject === 'Chemistry' ? <Beaker size={20} /> :
                         scenario.subject === 'Physics' ? <Zap size={20} /> :
                         <Activity size={20} />}
                      </div>
                      <div>
                        <Link href={`./scenarios/${scenario.id}`} className="font-semibold text-[var(--foreground)] hover:text-blue-400 transition-colors">
                          {scenario.title}
                        </Link>
                        <div className="text-xs text-[var(--muted-foreground)] mt-1 flex items-center gap-2">
                          <span>{scenario.difficulty}</span>
                          <span className="w-1 h-1 rounded-full bg-[var(--muted-foreground)]"></span>
                          <span>{scenario.duration}</span>
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="py-4 px-6">
                    <span className="text-[var(--foreground)] font-medium">{scenario.subject}</span>
                  </td>
                  <td className="py-4 px-6 hidden md:table-cell">
                    <div className="flex flex-col gap-1">
                      <div className="flex items-center justify-between text-xs w-24">
                        <span className="text-[var(--muted-foreground)]">Completion</span>
                        <span className="font-medium">{scenario.completion}</span>
                      </div>
                      <div className="w-24 h-1.5 bg-black/40 rounded-full overflow-hidden">
                        <div className="h-full bg-blue-500 rounded-full" style={{ width: scenario.completion }}></div>
                      </div>
                    </div>
                  </td>
                  <td className="py-4 px-6 hidden sm:table-cell">
                    <span className={`px-2.5 py-1 text-xs font-medium rounded-full inline-flex items-center gap-1.5 border ${
                      scenario.status === 'Published' 
                        ? 'bg-emerald-500/10 text-emerald-500 border-emerald-500/20'
                        : scenario.status === 'Draft'
                        ? 'bg-amber-500/10 text-amber-500 border-amber-500/20'
                        : 'bg-zinc-500/10 text-zinc-400 border-zinc-500/20'
                    }`}>
                      {scenario.status === 'Published' ? <CheckCircle size={12} /> : <FileText size={12} />}
                      {scenario.status}
                    </span>
                  </td>
                  <td className="py-4 px-6 text-right">
                    <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                      <Link href={`./scenarios/${scenario.id}`} className="p-2 text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/10 rounded-lg transition-colors">
                        <Edit3 size={16} />
                      </Link>
                      <button className="p-2 text-[var(--muted-foreground)] hover:text-red-400 hover:bg-red-400/10 rounded-lg transition-colors">
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={5} className="py-12 text-center text-[var(--muted-foreground)]">
                    No scenarios found matching your criteria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
