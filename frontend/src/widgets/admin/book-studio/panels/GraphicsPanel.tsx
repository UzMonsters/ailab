'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { RectangleHorizontal, Circle, Triangle, Minus, ArrowRight } from 'lucide-react';

const shapes = [
  { label: 'Rectangle', icon: RectangleHorizontal, svg: '<rect width="200" height="150" fill="#e2e8f0" stroke="#94a3b8" rx="4"/>' },
  { label: 'Circle', icon: Circle, svg: '<circle cx="100" cy="100" r="80" fill="#e2e8f0" stroke="#94a3b8"/>' },
  { label: 'Triangle', icon: Triangle, svg: '<polygon points="100,20 180,160 20,160" fill="#e2e8f0" stroke="#94a3b8"/>' },
  { label: 'Line', icon: Minus, svg: '<line x1="20" y1="100" x2="280" y2="100" stroke="#94a3b8" stroke-width="2"/>' },
  { label: 'Arrow', icon: ArrowRight, svg: '<line x1="20" y1="100" x2="240" y2="100" stroke="#94a3b8" stroke-width="2"/><polygon points="240,90 260,100 240,110" fill="#94a3b8"/>' },
];

export function GraphicsPanel() {
  const addBlock = useBookStudioStore(s => s.addBlock);

  return (
    <div className="space-y-2">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Shapes</p>
      <div className="grid grid-cols-2 gap-1.5">
        {shapes.map((shape, i) => {
          const Icon = shape.icon;
          return (
            <button
              key={i}
              onClick={() => addBlock('SVG', { svg: `<svg viewBox="0 0 300 200" xmlns="http://www.w3.org/2000/svg">${shape.svg}</svg>`, w: 200, h: 150 })}
              className="flex flex-col items-center gap-1 rounded-lg border border-white/5 bg-white/[.03] p-3 hover:border-violet-500/40 transition-colors"
            >
              <Icon size={20} className="text-slate-400" />
              <span className="text-[10px] text-slate-400">{shape.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}
