'use client';

import { useBookStudioStore, type Block } from '../store/useBookStudioStore';
import { blockRegistry } from '../registry/BookBlockRegistry';
import {
  Bold, Italic, Underline, Strikethrough, AlignLeft, AlignCenter, AlignRight, AlignJustify,
  Palette, Type, Trash2, Copy,
} from 'lucide-react';

function CommonActions({ block }: { block: Block }) {
  const { deleteBlock, duplicateBlock, bringForward, sendBackward } = useBookStudioStore();
  return (
    <div className="flex items-center gap-0.5">
      <button onClick={() => duplicateBlock(block.id)} className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white" title="Duplicate" aria-label="Duplicate">
        <Copy size={14} />
      </button>
      <button onClick={() => bringForward(block.id)} className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white" title="Bring forward" aria-label="Bring forward">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 19V5M5 12l7-7 7 7"/></svg>
      </button>
      <button onClick={() => sendBackward(block.id)} className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white" title="Send backward" aria-label="Send backward">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M12 5v14M19 12l-7 7-7-7"/></svg>
      </button>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <button onClick={() => deleteBlock(block.id)} className="flex h-7 w-7 items-center justify-center rounded hover:bg-rose-500/20 text-slate-400 hover:text-rose-300" title="Delete" aria-label="Delete">
        <Trash2 size={14} />
      </button>
    </div>
  );
}

function TextToolbar({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="flex items-center gap-0.5">
      <span className="text-xs text-slate-400 font-medium mr-1">Text</span>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <CommonActions block={block} />
    </div>
  );
}

function ImageToolbar({ block }: { block: Block }) {
  const { uploadImage } = useBookStudioStore();
  const fileRef = { current: null as HTMLInputElement | null };

  return (
    <div className="flex items-center gap-0.5">
      <span className="text-xs text-slate-400 font-medium mr-1">Image</span>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <CommonActions block={block} />
    </div>
  );
}

function FormulaToolbar({ block }: { block: Block }) {
  return (
    <div className="flex items-center gap-0.5">
      <span className="text-xs text-slate-400 font-medium mr-1">Formula</span>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <CommonActions block={block} />
    </div>
  );
}

function ShapeToolbar({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  return (
    <div className="flex items-center gap-0.5">
      <span className="text-xs text-slate-400 font-medium mr-1">Shape</span>
      <label className="flex h-7 items-center gap-1 rounded px-1.5 hover:bg-white/5 cursor-pointer text-xs text-slate-400">
        <Palette size={12} />
        <input type="color" className="h-3 w-3" onChange={e => patchBlock(block.id, { fillColor: e.target.value })} />
      </label>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <CommonActions block={block} />
    </div>
  );
}

function EntityToolbar({ block }: { block: Block }) {
  const kindLabel = blockRegistry[block.kind]?.label || block.kind;
  return (
    <div className="flex items-center gap-0.5">
      <span className="text-xs text-slate-400 font-medium mr-1">{kindLabel}</span>
      <div className="mx-1 h-4 w-px bg-white/10" />
      <CommonActions block={block} />
    </div>
  );
}

const toolbarByKind: Record<string, React.FC<{ block: Block }>> = {
  RICH_TEXT: TextToolbar,
  IMAGE: ImageToolbar,
  SVG: ShapeToolbar,
  FORMULA: FormulaToolbar,
  SHAPE: ShapeToolbar,
  EQUIPMENT_REFERENCE: EntityToolbar,
  MATERIAL_REFERENCE: EntityToolbar,
  REACTION_REFERENCE: EntityToolbar,
  DATA_WIDGET: EntityToolbar,
  INTERACTIVE_EXPERIMENT_LINK: EntityToolbar,
  TABLE: EntityToolbar,
};

export function ContextToolbar() {
  const { blocks, selectedBlockId } = useBookStudioStore();
  const block = blocks.find(b => b.id === selectedBlockId);

  if (!block) {
    return (
      <div className="flex items-center gap-1 text-xs text-slate-500">
        Select a block to edit
      </div>
    );
  }

  const Toolbar = toolbarByKind[block.kind] || EntityToolbar;
  return <Toolbar block={block} />;
}
