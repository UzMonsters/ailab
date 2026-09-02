'use client';
import { useAdminStore } from '@/stores/admin.store';
import { useState } from 'react';
import { Search } from 'lucide-react';

export default function AdminLabsPage() {
  const materials = useAdminStore((state) => state.materials);
  const [search, setSearch] = useState('');

  const filtered = materials.filter(m => m.name.toLowerCase().includes(search.toLowerCase()));

  return (
    <div className="p-4 md:p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Laboratories</h1>
          <p className="text-sm text-[var(--muted-foreground)] mt-1">Manage laboratory materials and resources</p>
        </div>
      </div>
      
      <div className="mb-6 max-w-sm">
        <div className="relative">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
          <input 
            type="text" 
            placeholder="Search materials..." 
            value={search} 
            onChange={(e) => setSearch(e.target.value)}
            className="w-full py-2 pl-9 pr-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] transition-all" 
          />
        </div>
      </div>

      <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-[var(--border)]">
              <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Name</th>
              <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Type</th>
              <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Status</th>
              <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Uses</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={4} className="py-8 text-center text-[var(--muted-foreground)]">No materials found.</td></tr>
            ) : (
              filtered.map(m => (
                <tr key={m.id} className="border-b border-[var(--border)]/50 hover:bg-white/[0.02]">
                  <td className="py-3 px-4 font-medium">{m.name}</td>
                  <td className="py-3 px-4 text-[var(--muted-foreground)]">
                    <span className="px-2 py-1 text-xs rounded-full bg-white/5 border border-white/10">
                      {m.type}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className={`px-2 py-1 text-xs rounded-full border ${m.status === 'Available' ? 'bg-[#14F195]/10 text-[#14F195] border-[#14F195]/30' : 'bg-white/5 border-white/10 text-[var(--muted-foreground)]'}`}>
                      {m.status}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-[var(--muted-foreground)]">{m.uses}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
