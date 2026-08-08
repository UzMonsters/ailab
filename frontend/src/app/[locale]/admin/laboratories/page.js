'use client';

import { useState } from 'react';

const mockLabs = [
  { id: 1, name: 'Organic Chemistry Lab', status: 'active', experiments: 234, users: 45, created: '2024-01-10' },
  { id: 2, name: 'Physics Simulation Lab', status: 'active', experiments: 189, users: 32, created: '2024-02-05' },
  { id: 3, name: 'Biochemistry Lab', status: 'maintenance', experiments: 156, users: 28, created: '2024-01-22' },
  { id: 4, name: 'Materials Science Lab', status: 'active', experiments: 98, users: 19, created: '2024-03-01' },
  { id: 5, name: 'Environmental Chemistry Lab', status: 'active', experiments: 67, users: 14, created: '2024-03-15' },
];

export default function LaboratoriesPage() {
  const [search, setSearch] = useState('');

  const filteredLabs = mockLabs.filter((l) => l.name.toLowerCase().includes(search.toLowerCase()));

  return (
    <div className="relative z-10 min-h-screen p-4 md:p-6">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold">Laboratories Management</h1>
          <button className="btn-primary text-sm py-2 px-4">+ Add Laboratory</button>
        </div>

        <div className="glass-card p-6">
          <div className="mb-4">
            <input
              type="text"
              placeholder="Search laboratories..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input-field max-w-sm"
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredLabs.map((lab) => (
              <div key={lab.id} className="glass-card p-5 hover:border-purple/30 transition-all cursor-pointer">
                <div className="flex items-center justify-between mb-3">
                  <div className="w-10 h-10 rounded-lg bg-purple/10 border border-purple/20 flex items-center justify-center text-lg">🔬</div>
                  <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${
                    lab.status === 'active' ? 'bg-teal/10 text-teal' : 'bg-amber/10 text-amber'
                  }`}>{lab.status}</span>
                </div>
                <h3 className="font-semibold mb-1">{lab.name}</h3>
                <div className="text-xs text-dim mb-3">{lab.experiments} experiments • {lab.users} users</div>
                <div className="text-xs text-dim">Created: {lab.created}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
