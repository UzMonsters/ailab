'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function EquipmentProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Equipment</p>
      <div>
        <label className="text-[10px] text-slate-500">Display</label>
        <select value={block.displayMode || 'illustration'} onChange={e => patchBlock(block.id, { displayMode: e.target.value })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
          <option value="illustration">Illustration</option>
          <option value="name-card">Name card</option>
          <option value="info-card">Info card</option>
        </select>
      </div>
      <label className="flex items-center gap-2 text-[10px] text-slate-500">
        <input type="checkbox" checked={block.showLabel !== false} onChange={e => patchBlock(block.id, { showLabel: e.target.checked })} className="accent-violet-500" />
        Show label
      </label>
    </div>
  );
}
