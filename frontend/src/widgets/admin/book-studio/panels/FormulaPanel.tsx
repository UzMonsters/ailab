'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { Sigma } from 'lucide-react';

const templates = [
  { label: 'H₂O', formula: 'H_2O' },
  { label: 'CO₂', formula: 'CO_2' },
  { label: 'NaCl', formula: 'NaCl' },
  { label: 'H₂SO₄', formula: 'H_2SO_4' },
  { label: 'Reaction arrow', formula: 'A + B \\rightarrow C' },
];

export function FormulaPanel() {
  const addBlock = useBookStudioStore(s => s.addBlock);

  return (
    <div className="space-y-3">
      <button
        onClick={() => addBlock('FORMULA')}
        className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-violet-500/30 bg-violet-600/5 p-2 text-xs text-violet-300 hover:bg-violet-600/10 transition-colors"
      >
        <Sigma size={14} />
        Add Formula
      </button>
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Common formulas</p>
      <div className="space-y-1">
        {templates.map((t, i) => (
          <button
            key={i}
            onClick={() => addBlock('FORMULA', { formula: t.formula })}
            className="flex w-full items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 text-left hover:border-violet-500/40 transition-colors"
          >
            <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded bg-emerald-600/10">
              <Sigma size={13} className="text-emerald-400" />
            </div>
            <div>
              <p className="text-xs font-medium text-slate-300">{t.label}</p>
              <p className="text-[10px] text-slate-500 font-mono">{t.formula}</p>
            </div>
          </button>
        ))}
      </div>
    </div>
  );
}
