'use client';

import React, { useState, useEffect } from 'react';
import { Terminal, X, Trash2, Cpu, Activity } from 'lucide-react';
import type { Item, Connection } from '@/widgets/sandbox/types';

interface DebugPanelProps {
  items: Item[];
  connections: Connection[];
  selectedId: string | null;
  historyLogs?: string[];
  clearLogs?: () => void;
  engineState?: Record<string, unknown>;
}

export function DebugPanel({
  items,
  connections,
  selectedId,
  historyLogs = [],
  clearLogs,
  engineState = {},
}: DebugPanelProps) {
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.shiftKey && e.key.toLowerCase() === 'd') {
        setIsOpen((prev) => !prev);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  if (!isOpen) return null;

  const selectedItem = items.find((i) => i.id === selectedId);

  return (
    <div className="fixed bottom-4 right-4 z-[200] w-96 max-h-[500px] flex flex-col rounded-2xl border border-cyan-500/30 bg-background/95 p-4 text-white shadow-[0_20px_50px_rgba(0,0,0,0.8)] backdrop-blur-xl animate-fade-in-up font-mono text-xs">
      <div className="flex items-center justify-between border-b border-border pb-2.5">
        <div className="flex items-center gap-2 text-cyan-400 font-bold">
          <Terminal size={16} />
          <span>Sandbox Engine Debugger</span>
        </div>
        <button
          type="button"
          onClick={() => setIsOpen(false)}
          className="rounded-md p-1 text-white/40 hover:bg-white/10 hover:text-white"
          aria-label="Close Debugger"
        >
          <X size={14} />
        </button>
      </div>

      <div className="mt-3 flex-1 overflow-y-auto space-y-3 pr-1 text-[11px]">
        {/* Active Items */}
        <div className="rounded-xl border border-white/5 bg-white/[0.02] p-2.5 space-y-1">
          <div className="flex items-center justify-between text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
            <span className="flex items-center gap-1"><Cpu size={12} /> Objects ({items.length})</span>
            {selectedId && <span className="text-cyan-300">Selected: {selectedId.slice(0, 8)}</span>}
          </div>
          {selectedItem && (
            <div className="mt-1 rounded border border-cyan-500/20 bg-cyan-950/20 p-2 text-[10px] space-y-0.5 text-cyan-200">
              <p>Type: {selectedItem.type}</p>
              <p>Position: ({Math.round(selectedItem.x)}, {Math.round(selectedItem.y)})</p>
              <p>Temp: {selectedItem.temperature?.toFixed(1)} &deg;C</p>
              <p>Volume: {selectedItem.volumeMl?.toFixed(1)} / {selectedItem.capacityMl} mL</p>
              <p>Broken: {selectedItem.broken ? 'YES' : 'NO'}</p>
              <p>Integrity: {selectedItem.integrity ?? 'intact'}</p>
              <p>Thermal: {String((selectedItem as any).thermalState ?? 'tracked in engine')}</p>
            </div>
          )}
        </div>

        {/* Active Connections */}
        <div className="rounded-xl border border-white/5 bg-white/[0.02] p-2.5 space-y-1">
          <div className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground flex items-center gap-1">
            <Activity size={12} /> Connections ({connections.length})
          </div>
          {connections.length === 0 ? (
            <p className="text-[10px] text-white/30 italic">No active connections</p>
          ) : (
            <div className="space-y-1 max-h-24 overflow-y-auto">
              {connections.map((c) => (
                <div key={c.id} className="rounded bg-black/40 p-1.5 text-[10px] text-emerald-300 flex justify-between">
                  <span>{c.from.slice(0, 6)}: {c.fromPort ?? 'port'} &rarr; {c.to.slice(0, 6)}: {c.toPort ?? 'port'}</span>
                  <span className="text-white/40">{c.medium}</span>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Simulation Event Logs */}
        <div className="rounded-xl border border-white/5 bg-white/[0.02] p-2.5 space-y-1">
          <div className="flex items-center justify-between text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
            <span>Event History ({historyLogs.length})</span>
            {clearLogs && (
              <button onClick={clearLogs} className="text-red-400 hover:underline flex items-center gap-1">
                <Trash2 size={10} /> Clear
              </button>
            )}
          </div>
          <div className="max-h-32 overflow-y-auto space-y-1">
            {historyLogs.slice(-10).map((log, idx) => (
              <div key={idx} className="text-[10px] text-muted-foreground border-l border-cyan-400/40 pl-1.5 py-0.5">
                {log}
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="mt-2 border-t border-border pt-2 text-[9px] text-white/40 text-center">
        Press <kbd className="rounded border border-white/20 bg-white/10 px-1 py-0.5 font-sans">Shift + D</kbd> to toggle debug panel
      </div>
    </div>
  );
}
