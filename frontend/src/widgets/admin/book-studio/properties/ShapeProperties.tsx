'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function ShapeProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Shape</p>
      <div>
        <label className="text-[10px] text-slate-500">Fill</label>
        <input type="color" value={block.fillColor || '#e2e8f0'} onChange={e => patchBlock(block.id, { fillColor: e.target.value })}
          className="h-6 w-full rounded border border-white/10" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Stroke</label>
        <input type="color" value={block.strokeColor || '#94a3b8'} onChange={e => patchBlock(block.id, { strokeColor: e.target.value })}
          className="h-6 w-full rounded border border-white/10" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Border width</label>
        <input type="number" value={block.strokeWidth || 1} onChange={e => patchBlock(block.id, { strokeWidth: Number(e.target.value) })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Border radius</label>
        <input type="number" value={block.borderRadius || 0} onChange={e => patchBlock(block.id, { borderRadius: Number(e.target.value) })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
      </div>
    </div>
  );
}
