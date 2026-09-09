'use client';

import { Palette } from 'lucide-react';

const presets = [
  { name: 'Cream', bg: '#fff9e9' },
  { name: 'White', bg: '#ffffff' },
  { name: 'Graph paper', bg: '#f0f4f8' },
  { name: 'Dark', bg: '#1a1a2e' },
];

export function ThemePanel() {
  return (
    <div className="space-y-4">
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Paper</p>
        <div className="grid grid-cols-2 gap-1.5">
          {presets.map((p, i) => (
            <button
              key={i}
              className="flex flex-col items-center gap-1 rounded-lg border border-white/5 bg-white/[.03] p-2 hover:border-violet-500/40 transition-colors"
            >
              <div className="h-10 w-full rounded border border-white/10" style={{ background: p.bg }} />
              <span className="text-[10px] text-slate-400">{p.name}</span>
            </button>
          ))}
        </div>
      </div>
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Colors</p>
        <div className="space-y-2">
          {['Primary', 'Secondary', 'Accent', 'Text'].map((label, i) => (
            <label key={i} className="flex items-center justify-between text-xs text-slate-400">
              <span>{label}</span>
              <input type="color" className="h-5 w-8 rounded border border-white/10" defaultValue={['#8b5cf6', '#22d3ee', '#f59e0b', '#1e293b'][i]} />
            </label>
          ))}
        </div>
      </div>
    </div>
  );
}
