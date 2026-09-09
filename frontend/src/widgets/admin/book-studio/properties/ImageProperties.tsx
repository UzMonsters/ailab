'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function ImageProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Image</p>
      <div>
        <label className="text-[10px] text-slate-500">Fit</label>
        <select value={block.fit || 'contain'} onChange={e => patchBlock(block.id, { fit: e.target.value })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
          <option value="contain">Contain</option>
          <option value="cover">Cover</option>
          <option value="fill">Fill</option>
        </select>
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Border radius</label>
        <input type="number" value={block.borderRadius || 0} onChange={e => patchBlock(block.id, { borderRadius: Number(e.target.value) })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Alt text</label>
        <input value={block.alt || ''} onChange={e => patchBlock(block.id, { alt: e.target.value })}
          placeholder="Describe the image..."
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
      </div>
    </div>
  );
}
