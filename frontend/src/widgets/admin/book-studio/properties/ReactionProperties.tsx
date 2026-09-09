'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function ReactionProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Reaction</p>
      <div>
        <label className="text-[10px] text-slate-500">Display</label>
        <select value={block.displayMode || 'equation'} onChange={e => patchBlock(block.id, { displayMode: e.target.value })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
          <option value="equation">Equation only</option>
          <option value="card">Reaction card</option>
          <option value="explanation">Explanation</option>
        </select>
      </div>
    </div>
  );
}
