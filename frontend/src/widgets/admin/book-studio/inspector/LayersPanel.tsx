'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { blockRegistry } from '../registry/BookBlockRegistry';
import { GripVertical, Eye, EyeOff, Lock, Unlock } from 'lucide-react';

function blockLabel(block: ReturnType<typeof useBookStudioStore.getState>['blocks'][number]): string {
  const config = blockRegistry[block.kind];
  const base = config?.label || block.kind;
  if (block.kind === 'RICH_TEXT' && block.text) {
    const stripped = block.text.replace(/<[^>]*>/g, '').trim();
    const preview = stripped.length > 30 ? stripped.slice(0, 30) + '...' : stripped;
    return `${base} — ${preview || 'Text'}`;
  }
  if (block.kind === 'IMAGE') return `Image — ${block.alt || block.assetId || 'image'}`;
  if (block.kind === 'EQUIPMENT_REFERENCE') return `Equipment — ${block.equipmentId || 'not assigned'}`;
  if (block.kind === 'MATERIAL_REFERENCE') return `Material — ${block.materialId || 'not assigned'}`;
  if (block.kind === 'REACTION_REFERENCE') return `Reaction — ${block.reactionId || 'not assigned'}`;
  if (block.kind === 'FORMULA') return `Formula — ${block.formula || 'formula'}`;
  if (block.kind === 'SVG') return `SVG — ${block.assetId || 'vector'}`;
  if (block.kind === 'SHAPE') return `Shape — ${block.shapeType || 'shape'}`;
  if (block.kind === 'DATA_WIDGET') return `Data — ${block.widgetLabel || 'widget'}`;
  return base;
}

export function LayersPanel() {
  const { blocks, selectedBlockId, setSelected, patchBlock } = useBookStudioStore();
  const sorted = [...blocks].sort((a, b) => b.z - a.z);

  return (
    <div className="space-y-1">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Layers</p>
      {sorted.length === 0 && (
        <p className="text-xs text-slate-500 py-4 text-center">No blocks on page</p>
      )}
      {sorted.map(block => {
        const config = blockRegistry[block.kind];
        const Icon = config?.icon;
        return (
          <div
            key={block.id}
            onClick={() => setSelected(block.id)}
            className={`flex items-center gap-1.5 rounded px-2 py-1.5 cursor-pointer transition-colors ${
              selectedBlockId === block.id
                ? 'bg-violet-600/20 text-violet-300'
                : 'text-slate-400 hover:bg-white/5'
            }`}
          >
            <GripVertical size={12} className="shrink-0 text-slate-600" />
            {Icon && <Icon size={12} className="shrink-0" />}
            <span className="flex-1 truncate text-[11px]">{blockLabel(block)}</span>
            <button
              onClick={e => { e.stopPropagation(); patchBlock(block.id, { visible: block.visible === false ? true : false } as any, false); }}
              className="shrink-0 text-slate-500 hover:text-white"
              aria-label={block.visible === false ? 'Show' : 'Hide'}
            >
              {block.visible === false ? <EyeOff size={11} /> : <Eye size={11} />}
            </button>
            <button
              onClick={e => { e.stopPropagation(); patchBlock(block.id, { locked: !block.locked } as any, false); }}
              className="shrink-0 text-slate-500 hover:text-white"
              aria-label={block.locked ? 'Unlock' : 'Lock'}
            >
              {block.locked ? <Lock size={11} /> : <Unlock size={11} />}
            </button>
          </div>
        );
      })}
    </div>
  );
}
