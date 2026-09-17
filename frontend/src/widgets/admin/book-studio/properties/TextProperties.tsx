'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';
import { RichTextEditor } from '../RichTextEditor';

import { CollapsibleSection } from '../inspector/CollapsibleSection';

export function TextProperties({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="space-y-4">
      <CollapsibleSection title="TEXT FORMAT" defaultOpen>
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
      </CollapsibleSection>

      <CollapsibleSection title="COLORS">
        <div className="flex gap-2">
          <div className="flex-1">
            <label className="text-[10px] text-slate-500">Text Color</label>
            <div className="flex rounded border border-white/10 bg-[#080c14] overflow-hidden">
              <input type="color" value={block.color || '#1e293b'} onChange={e => patchBlock(block.id, { color: e.target.value })}
                className="h-6 w-8 bg-transparent" />
              <input type="text" value={block.color || '#1e293b'} onChange={e => patchBlock(block.id, { color: e.target.value })}
                className="w-full bg-transparent px-2 text-xs text-white outline-none uppercase" />
            </div>
          </div>
          <div className="flex-1">
            <label className="text-[10px] text-slate-500">Background</label>
            <div className="flex rounded border border-white/10 bg-[#080c14] overflow-hidden">
              <input type="color" value={block.backgroundColor || '#ffffff'} onChange={e => patchBlock(block.id, { backgroundColor: e.target.value })}
                className="h-6 w-8 bg-transparent" />
              <input type="text" value={block.backgroundColor || '#ffffff'} onChange={e => patchBlock(block.id, { backgroundColor: e.target.value })}
                className="w-full bg-transparent px-2 text-xs text-white outline-none uppercase" />
            </div>
          </div>
        </div>
        <div className="mt-2">
          <label className="flex items-center gap-2 cursor-pointer text-xs text-slate-300">
            <input type="checkbox" checked={block.backgroundColor === 'transparent'} onChange={e => patchBlock(block.id, { backgroundColor: e.target.checked ? 'transparent' : '#ffffff' })}
              className="rounded border-white/10 bg-[#080c14] text-violet-500" />
            Transparent Background
          </label>
        </div>
      </CollapsibleSection>

      <CollapsibleSection title="CONTENT" defaultOpen={false}>
        <RichTextEditor 
          content={block.text || ''} 
          onChange={html => patchBlock(block.id, { text: html })}
        />
      </CollapsibleSection>
    </div>
  );
}
