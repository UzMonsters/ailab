'use client';

import { useState } from 'react';
import { useBookStudioStore } from '../store/useBookStudioStore';
import type { BookBlockKind } from '../BookPageRenderer';
import { FileText } from 'lucide-react';

const layouts = [
  { name: 'Blank', blocks: [] },
  { name: 'Title page', blocks: [{ kind: 'RICH_TEXT', x: 100, y: 200, w: 400, h: 100, text: '<h1>Book Title</h1>' }] },
  { name: 'Chapter intro', blocks: [{ kind: 'RICH_TEXT', x: 60, y: 100, w: 480, h: 60, text: '<h1>Chapter 1</h1>' }, { kind: 'RICH_TEXT', x: 60, y: 200, w: 480, h: 200, text: '<p>Chapter introduction text...</p>' }] },
  { name: 'Two columns', blocks: [{ kind: 'RICH_TEXT', x: 40, y: 60, w: 250, h: 600, text: '<p>Left column...</p>' }, { kind: 'RICH_TEXT', x: 310, y: 60, w: 250, h: 600, text: '<p>Right column...</p>' }] },
  { name: 'Image + text', blocks: [{ kind: 'IMAGE', x: 40, y: 60, w: 260, h: 350 }, { kind: 'RICH_TEXT', x: 320, y: 60, w: 240, h: 350, text: '<p>Image description...</p>' }] },
];

export function LayoutsPanel() {
  const { blocks, replaceBlocks, page } = useBookStudioStore();
  const [confirming, setConfirming] = useState<number | null>(null);
  const [applying, setApplying] = useState(false);

  const applyLayout = (layoutIndex: number) => {
    if (!page) return;
    const layout = layouts[layoutIndex];
    if (blocks.length > 0 && confirming !== layoutIndex) {
      setConfirming(layoutIndex);
      return;
    }
    setConfirming(null);
    setApplying(true);
    const newBlocks = layout.blocks.map((b, i) => ({
      id: crypto.randomUUID(),
      kind: b.kind as BookBlockKind,
      x: b.x,
      y: b.y,
      w: b.w,
      h: b.h,
      z: i + 1,
      text: b.text || '',
      formula: '',
      scenarioId: '',
      translations: {},
    }));
    replaceBlocks(newBlocks);
    setTimeout(() => setApplying(false), 300);
  };

  if (!page) {
    return (
      <div className="space-y-2">
        <p className="text-[10px] text-slate-500 uppercase tracking-wider">Page templates</p>
        <p className="text-xs text-slate-500 py-4 text-center">Select a page first.</p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Page templates</p>
      <div className="space-y-1.5">
        {layouts.map((layout, i) => (
          <div key={i}>
            <button
              onClick={() => applyLayout(i)}
              disabled={applying}
              className="flex w-full items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 text-left hover:border-violet-500/40 transition-colors disabled:opacity-50"
            >
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-slate-600/20">
                <FileText size={14} className="text-slate-400" />
              </div>
              <div>
                <p className="text-xs font-medium text-slate-300">{layout.name}</p>
                <p className="text-[10px] text-slate-500">{layout.blocks.length} blocks</p>
              </div>
            </button>
            {confirming === i && (
              <div className="mt-1 rounded-lg border border-amber-400/20 bg-amber-500/10 p-2 text-xs text-amber-200">
                <p>This will replace current page content.</p>
                <div className="mt-2 flex gap-2">
                  <button onClick={() => setConfirming(null)} className="rounded border border-white/10 px-2 py-1 text-slate-300 hover:bg-white/5">Cancel</button>
                  <button onClick={() => applyLayout(i)} className="rounded bg-violet-600 px-2 py-1 text-white hover:bg-violet-500">Replace page</button>
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
