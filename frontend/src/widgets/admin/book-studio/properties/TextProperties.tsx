'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';
import { RichTextEditor } from '../RichTextEditor';

export function TextProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Typography</p>
      <div className="grid grid-cols-2 gap-2">
        <div>
          <label className="text-[10px] text-slate-500">Font</label>
          <select value={block.fontFamily || ''} onChange={e => patchBlock(block.id, { fontFamily: e.target.value })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
            <option value="">Default</option>
            <option>Georgia</option><option>Arial</option><option>Times New Roman</option><option>Courier New</option>
          </select>
        </div>
        <div>
          <label className="text-[10px] text-slate-500">Size</label>
          <input type="number" value={block.fontSize || 16} onChange={e => patchBlock(block.id, { fontSize: Number(e.target.value) })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none" />
        </div>
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Content (Rich Text)</label>
        <RichTextEditor 
          content={block.text || ''} 
          onChange={html => patchBlock(block.id, { text: html })}
        />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Color</label>
        <input type="color" value={block.color || '#1e293b'} onChange={e => patchBlock(block.id, { color: e.target.value })}
          className="h-6 w-full rounded border border-white/10" />
      </div>
      <div>
        <label className="text-[10px] text-slate-500">Background</label>
        <input type="color" value={block.backgroundColor || '#ffffff'} onChange={e => patchBlock(block.id, { backgroundColor: e.target.value })}
          className="h-6 w-full rounded border border-white/10" />
      </div>
    </div>
  );
}
