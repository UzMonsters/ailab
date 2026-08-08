'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

const equipment = [
  { id: 'beaker', name: 'Beaker', icon: '⚗' },
  { id: 'flask', name: 'Flask', icon: '🧪' },
  { id: 'burner', name: 'Bunsen Burner', icon: '🔥' },
  { id: 'condenser', name: 'Condenser', icon: '❄' },
  { id: 'thermometer', name: 'Thermometer', icon: '🌡' },
  { id: 'scale', name: 'Scale', icon: '⚖' },
  { id: 'pipette', name: 'Pipette', icon: '💧' },
  { id: 'funnel', name: 'Funnel', icon: '🔻' },
];

export default function SandboxPage() {
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const [selectedTool, setSelectedTool] = useState<string | null>(null);

  return (
    <div className="relative z-10 h-screen flex flex-col">
      <header className="px-4 py-2 bg-[var(--card)]/80 backdrop-blur-xl border-b border-[var(--border)] flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Link href={`/${locale}/dashboard`} className="text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors no-underline">←</Link>
          <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold">⚗</div>
          <input type="text" defaultValue="Acid Base Experiment" className="bg-transparent text-sm font-medium outline-none border-b border-transparent hover:border-[var(--border)] focus:border-[#8b5cf6] transition-colors text-[var(--foreground)]" />
        </div>
        <div className="flex items-center gap-2">
          <button className="py-1.5 px-3 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-xs text-[var(--foreground)] hover:bg-white/[0.08] transition-all">Save</button>
          <button className="py-1.5 px-3 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-sm)] text-xs font-semibold">Run</button>
        </div>
      </header>
      <div className="flex-1 flex overflow-hidden">
        <aside className="w-[200px] bg-[var(--card)]/50 border-r border-[var(--border)] p-3 overflow-y-auto hidden md:block">
          <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider mb-3">Equipment</div>
          <div className="space-y-1">
            {equipment.map((eq) => (
              <button key={eq.id} onClick={() => setSelectedTool(eq.id)} className={`w-full flex items-center gap-2 px-3 py-2 rounded-lg text-sm transition-all ${selectedTool === eq.id ? 'bg-[#8b5cf6]/10 text-[#8b5cf6] border border-[#8b5cf6]/20' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.03]'}`}>
                <span className="text-base">{eq.icon}</span><span className="text-xs">{eq.name}</span>
              </button>
            ))}
          </div>
        </aside>
        <main className="flex-1 bg-[#08090d] relative overflow-hidden">
          <div className="absolute inset-0 flex items-center justify-center">
            <div className="text-center"><div className="text-6xl mb-4 opacity-20">⚗</div><div className="text-sm text-[var(--muted-foreground)]">Drag equipment from the left panel to start building your experiment</div></div>
          </div>
          <div className="absolute inset-0 opacity-5" style={{ backgroundImage: 'linear-gradient(rgba(255,255,255,0.1) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.1) 1px, transparent 1px)', backgroundSize: '40px 40px' }} />
        </main>
        <aside className="w-[240px] bg-[var(--card)]/50 border-l border-[var(--border)] p-3 overflow-y-auto hidden lg:block">
          <div className="text-xs font-semibold text-[var(--muted-foreground)] uppercase tracking-wider mb-3">Properties</div>
          {selectedTool ? (
            <div className="space-y-4">
              <div><label className="text-xs text-[var(--muted-foreground)] mb-1 block">Name</label><input type="text" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] px-3 py-2 text-sm text-[var(--foreground)] outline-none" defaultValue={equipment.find((e) => e.id === selectedTool)?.name} /></div>
              <button className="w-full py-2 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-xs text-[var(--foreground)] hover:bg-white/[0.08] transition-all">Remove</button>
            </div>
          ) : (
            <div className="text-center py-8"><div className="text-3xl mb-2 opacity-30">🔧</div><div className="text-xs text-[var(--muted-foreground)]">Select equipment to view properties</div></div>
          )}
        </aside>
      </div>
      <div className="h-[60px] bg-[var(--card)]/80 backdrop-blur-xl border-t border-[var(--border)] px-4 flex items-center justify-between">
        <div className="flex items-center gap-4"><button className="text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-colors">▶</button><span className="text-xs text-[var(--muted-foreground)] font-mono">Speed: 1x</span></div>
        <div className="flex items-center gap-4"><span className="text-xs text-[var(--muted-foreground)]">Zoom: 100%</span><span className="text-xs text-[var(--muted-foreground)] font-mono">Temperature: 25°C</span><span className="text-xs text-[var(--muted-foreground)] font-mono">pH: 7.0</span></div>
      </div>
    </div>
  );
}
