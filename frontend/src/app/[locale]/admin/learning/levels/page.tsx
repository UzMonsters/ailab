'use client';
import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { mockLevels } from '@/mocks/admin/learning';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useLocale } from 'next-intl';
import { Plus, Table as TableIcon, Map as MapIcon } from 'lucide-react';
import AdminDataTable from '@/widgets/admin/AdminDataTable';

export default function LevelsPage() {
  const router = useRouter();
  const locale = useLocale();
  const [view, setView] = useState<'table' | 'map'>('table');
  const [subjectFilter, setSubjectFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [difficultyFilter, setDifficultyFilter] = useState('');

  const filteredLevels = mockLevels.filter(l => {
    if (subjectFilter && l.subject !== subjectFilter) return false;
    if (statusFilter && l.status !== statusFilter) return false;
    if (difficultyFilter && l.difficulty !== difficultyFilter) return false;
    return true;
  });

  const columns = [
    { header: '#', accessorKey: 'levelNumber' as const, sortable: true },
    { header: 'Title', accessorKey: 'title' as const, sortable: true },
    { header: 'Subject', accessorKey: 'subject' as const, sortable: true },
    { header: 'Chapter', accessorKey: 'chapter' as const, sortable: true },
    { 
      header: 'Difficulty', 
      accessorKey: 'difficulty' as const, sortable: true,
      cell: (item: any) => {
        const colors: Record<string, string> = {
          'Beginner': 'text-green-400 bg-green-400/10 border-green-400/20',
          'Intermediate': 'text-yellow-400 bg-yellow-400/10 border-yellow-400/20',
          'Advanced': 'text-red-400 bg-red-400/10 border-red-400/20'
        };
        const color = colors[item.difficulty] || 'text-gray-400 bg-gray-400/10 border-gray-400/20';
        return <span className={`px-2 py-1 text-xs border rounded-md ${color}`}>{item.difficulty}</span>;
      }
    },
    { 
      header: 'Status', 
      accessorKey: 'status' as const, sortable: true,
      cell: (item: any) => {
        const colors: Record<string, string> = {
          'Published': 'text-green-400',
          'Draft': 'text-gray-400'
        };
        const color = colors[item.status] || 'text-gray-400';
        return <span className={`flex items-center gap-1 ${color}`}><div className={`w-2 h-2 rounded-full bg-current`}></div>{item.status}</span>;
      }
    },
    { header: 'XP', accessorKey: 'xp' as const, sortable: true }
  ];

  return (
    <div className="p-4 md:p-6 min-h-screen bg-[#070b14] text-white">
      <AdminPageHeader 
        title="Levels" 
        description="Manage your learning modules"
        actions={
          <div className="flex gap-2">
            <div className="flex bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded-lg p-1">
              <button 
                onClick={() => setView('table')}
                className={`p-1.5 rounded-md transition-colors ${view === 'table' ? 'bg-[#8b5cf6] text-white' : 'text-[#8490a3] hover:text-white'}`}
              >
                <TableIcon size={16} />
              </button>
              <button 
                onClick={() => setView('map')}
                className={`p-1.5 rounded-md transition-colors ${view === 'map' ? 'bg-[#8b5cf6] text-white' : 'text-[#8490a3] hover:text-white'}`}
              >
                <MapIcon size={16} />
              </button>
            </div>
            <Link 
              href={`/${locale}/admin/learning/levels/new`}
              className="flex items-center gap-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white px-4 py-2 rounded-lg font-medium transition-colors text-sm"
            >
              <Plus size={16} />
              New Level
            </Link>
          </div>
        }
        filters={
          <>
            <select 
              value={subjectFilter}
              onChange={e => setSubjectFilter(e.target.value)}
              className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-1.5 text-sm text-white focus:outline-none focus:border-[#8b5cf6]"
            >
              <option value="">All Subjects</option>
              <option value="Chemistry">Chemistry</option>
              <option value="Physics">Physics</option>
            </select>
            
            <select 
              value={difficultyFilter}
              onChange={e => setDifficultyFilter(e.target.value)}
              className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-1.5 text-sm text-white focus:outline-none focus:border-[#8b5cf6]"
            >
              <option value="">All Difficulties</option>
              <option value="Beginner">Beginner</option>
              <option value="Intermediate">Intermediate</option>
              <option value="Advanced">Advanced</option>
            </select>

            <select 
              value={statusFilter}
              onChange={e => setStatusFilter(e.target.value)}
              className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-1.5 text-sm text-white focus:outline-none focus:border-[#8b5cf6]"
            >
              <option value="">All Statuses</option>
              <option value="Published">Published</option>
              <option value="Draft">Draft</option>
            </select>
          </>
        }
      />

      {view === 'table' ? (
        <AdminDataTable 
          data={filteredLevels} 
          columns={columns} 
          onRowClick={(item) => router.push(`/${locale}/admin/learning/levels/${item.id}`)}
          searchPlaceholder="Search levels by title, subject, chapter..."
        />
      ) : (
        <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-8 min-h-[500px] overflow-auto">
          <div className="flex flex-col items-center min-w-max space-y-8 py-8">
            {filteredLevels.map((level, i) => (
              <div key={level.id} className="relative flex flex-col items-center group cursor-pointer" onClick={() => router.push(`/${locale}/admin/learning/levels/${level.id}`)}>
                {i > 0 && (
                  <div className="absolute -top-8 left-1/2 w-0.5 h-8 bg-[#8b5cf6]/50 -translate-x-1/2"></div>
                )}
                <div className={`w-16 h-16 rounded-full flex items-center justify-center border-4 z-10 transition-transform group-hover:scale-110 ${
                  level.status === 'Published' 
                    ? 'bg-[#8b5cf6] border-[#0b101a] text-white shadow-[0_0_15px_rgba(139,92,246,0.5)]' 
                    : 'bg-[#141b2a] border-[#8490a3] text-[#8490a3]'
                }`}>
                  <span className="font-bold">{level.levelNumber}</span>
                </div>
                <div className="mt-4 text-center">
                  <h4 className="font-semibold text-white group-hover:text-[#8b5cf6] transition-colors">{level.title}</h4>
                  <p className="text-xs text-[#8490a3]">{level.chapter}</p>
                </div>
              </div>
            ))}
            {filteredLevels.length === 0 && (
              <div className="text-[#8490a3]">No levels found matching criteria.</div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
