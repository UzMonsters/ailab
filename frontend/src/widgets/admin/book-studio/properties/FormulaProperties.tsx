'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function FormulaProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Formula</p>
      <div>
        <label className="text-[10px] text-slate-500">LaTeX</label>
        <textarea value={block.formula || ''} onChange={e => patchBlock(block.id, { formula: e.target.value }, false)}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1.5 text-xs text-white outline-none font-mono h-20 resize-none" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Size</label>
        <input type="number" value={block.fontSize || 24} onChange={e => patchBlock(block.id, { fontSize: Number(e.target.value) })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
      </div>
    </div>
  );
}
