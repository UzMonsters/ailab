'use client';
import { useState } from 'react';
import { ChevronDown, ChevronRight } from 'lucide-react';

export function CollapsibleSection({ title, children, defaultOpen = true }: { title: string, children: React.ReactNode, defaultOpen?: boolean }) {
  const [open, setOpen] = useState(defaultOpen);
  return (
    <div className="border-t border-white/10 pt-2">
      <button onClick={() => setOpen(!open)} className="flex w-full items-center justify-between text-[10px] uppercase tracking-wider text-slate-500 hover:text-slate-300">
        {title}
        {open ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
      </button>
      {open && <div className="mt-3 space-y-3">{children}</div>}
    </div>
  );
}
