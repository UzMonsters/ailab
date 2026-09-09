'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { Type, Heading1, Heading2, Heading3, AlignLeft, Quote, MessageSquare } from 'lucide-react';

const presets = [
  { kind: 'RICH_TEXT' as const, label: 'Heading', icon: Heading1, text: '<h1>Chapter Title</h1>', w: 420, h: 80 },
  { kind: 'RICH_TEXT' as const, label: 'Subheading', icon: Heading2, text: '<h2>Section Title</h2>', w: 420, h: 60 },
  { kind: 'RICH_TEXT' as const, label: 'Body text', icon: AlignLeft, text: '<p>Add your text here...</p>', w: 420, h: 160 },
  { kind: 'RICH_TEXT' as const, label: 'Caption', icon: Type, text: '<p><em>Caption text</em></p>', w: 300, h: 50 },
  { kind: 'RICH_TEXT' as const, label: 'Quote', icon: Quote, text: '<blockquote>Add a quote here...</blockquote>', w: 400, h: 100 },
  { kind: 'RICH_TEXT' as const, label: 'Scientific note', icon: MessageSquare, text: '<p><strong>Note:</strong> Add a scientific note...</p>', w: 400, h: 100 },
];

export function TextPanel() {
  const addBlock = useBookStudioStore(s => s.addBlock);

  return (
    <div className="space-y-2">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Presets</p>
      <div className="space-y-1">
        {presets.map((preset, i) => {
          const Icon = preset.icon;
          return (
            <button
              key={i}
              onClick={() => addBlock(preset.kind, { text: preset.text, w: preset.w, h: preset.h })}
              className="flex w-full items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 text-left hover:border-violet-500/40 transition-colors"
            >
              <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded bg-violet-600/10">
                <Icon size={13} className="text-violet-400" />
              </div>
              <span className="text-xs text-slate-300">{preset.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
