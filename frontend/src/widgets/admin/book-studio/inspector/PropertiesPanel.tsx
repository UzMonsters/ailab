'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { blockRegistry } from '../registry/BookBlockRegistry';
import { TextProperties } from '../properties/TextProperties';
import { ImageProperties } from '../properties/ImageProperties';
import { ShapeProperties } from '../properties/ShapeProperties';
import { EquipmentProperties } from '../properties/EquipmentProperties';
import { MaterialProperties } from '../properties/MaterialProperties';
import { ReactionProperties } from '../properties/ReactionProperties';
import { FormulaProperties } from '../properties/FormulaProperties';
import { ExperimentProperties } from '../properties/ExperimentProperties';

const propertiesByKind: Record<string, React.FC<{ block: any }>> = {
  RICH_TEXT: TextProperties,
  PARAGRAPH: TextProperties,
  HEADING: TextProperties,
  IMAGE: ImageProperties,
  SVG: ShapeProperties,
  SHAPE: ShapeProperties,
  EQUIPMENT_REFERENCE: EquipmentProperties,
  MATERIAL_REFERENCE: MaterialProperties,
  REACTION_REFERENCE: ReactionProperties,
  FORMULA: FormulaProperties,
  INTERACTIVE_EXPERIMENT_LINK: ExperimentProperties,
  DATA_WIDGET: ShapeProperties,
  TABLE: ShapeProperties,
};

export function PropertiesPanel() {
  const { blocks, selectedBlockId, patchBlock } = useBookStudioStore();
  const block = blocks.find(b => b.id === selectedBlockId);

  if (!block) {
    return <p className="text-xs text-slate-500">Select a block on the page.</p>;
  }

  const config = blockRegistry[block.kind];
  const Props = propertiesByKind[block.kind];

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2">
        {config && <config.icon size={14} className="text-violet-400" />}
        <span className="text-xs font-semibold text-slate-300">{config?.label || block.kind}</span>
      </div>

      <div className="grid grid-cols-2 gap-2">
        <div>
          <label className="text-[10px] text-slate-500 uppercase">X</label>
          <input type="number" value={Math.round(block.x)} onChange={e => patchBlock(block.id, { x: Number(e.target.value) })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
        </div>
        <div>
          <label className="text-[10px] text-slate-500 uppercase">Y</label>
          <input type="number" value={Math.round(block.y)} onChange={e => patchBlock(block.id, { y: Number(e.target.value) })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
        </div>
        <div>
          <label className="text-[10px] text-slate-500 uppercase">W</label>
          <input type="number" value={Math.round(block.w)} onChange={e => patchBlock(block.id, { w: Math.max(50, Number(e.target.value)) })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
        </div>
        <div>
          <label className="text-[10px] text-slate-500 uppercase">H</label>
          <input type="number" value={Math.round(block.h)} onChange={e => patchBlock(block.id, { h: Math.max(30, Number(e.target.value)) })}
            className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
        </div>
      </div>

      <div>
        <label className="text-[10px] text-slate-500 uppercase">Rotation</label>
        <input type="number" value={Math.round(block.rotation || 0)} onChange={e => patchBlock(block.id, { rotation: Number(e.target.value) })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
      </div>

      <div>
        <label className="text-[10px] text-slate-500 uppercase">Opacity</label>
        <input type="range" min="0" max="1" step="0.05" value={block.opacity ?? 1}
          onChange={e => patchBlock(block.id, { opacity: Number(e.target.value) })}
          className="w-full accent-violet-500" />
      </div>

      <div className="flex items-center gap-2">
        <label className="text-[10px] text-slate-500 uppercase">z-index</label>
        <input type="number" value={block.z} onChange={e => patchBlock(block.id, { z: Number(e.target.value) })}
          className="w-20 rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none focus:border-violet-500" />
      </div>

      {Props && <Props block={block} />}
    </div>
  );
}
