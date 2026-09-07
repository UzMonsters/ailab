'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function MaterialProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Material</p>
      <div>
        <label className="text-[10px] text-slate-500">Display</label>
        <select value={block.displayMode || 'chip'} onChange={e => patchBlock(block.id, { displayMode: e.target.value })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
          <option value="chip">Material chip</option>
          <option value="card">Material card</option>
          <option value="formula">Formula card</option>
        </select>
      </div>
      <label className="flex items-center gap-2 text-[10px] text-slate-500">
        <input type="checkbox" checked={block.showFormula !== false} onChange={e => patchBlock(block.id, { showFormula: e.target.checked })} className="accent-violet-500" />
        Show formula
      </label>
      <label className="flex items-center gap-2 text-[10px] text-slate-500">
        <input type="checkbox" checked={block.showName !== false} onChange={e => patchBlock(block.id, { showName: e.target.checked })} className="accent-violet-500" />
        Show name
      </label>
    </div>
  );
}
