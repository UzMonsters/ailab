'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';

export function BookAlignmentGuides() {
  const { blocks, selectedBlockId, showGuides, zoom } = useBookStudioStore();

  if (!showGuides || !selectedBlockId) return null;

  const selected = blocks.find(b => b.id === selectedBlockId);
  if (!selected) return null;

  const guides: { x1: number; y1: number; x2: number; y2: number }[] = [];
  const pageW = 590;
  const pageH = 760;
  const threshold = 5;
  const scx = selected.x + selected.w / 2;
  const scy = selected.y + selected.h / 2;

  // Page center guides
  if (Math.abs(scx - pageW / 2) < threshold) {
    guides.push({ x1: pageW / 2, y1: 0, x2: pageW / 2, y2: pageH });
  }
  if (Math.abs(scy - pageH / 2) < threshold) {
    guides.push({ x1: 0, y1: pageH / 2, x2: pageW, y2: pageH / 2 });
  }

  // Other block center guides
  for (const b of blocks) {
    if (b.id === selectedBlockId) continue;
    const bcx = b.x + b.w / 2;
    const bcy = b.y + b.h / 2;
    if (Math.abs(scx - bcx) < threshold) {
      guides.push({ x1: bcx, y1: 0, x2: bcx, y2: pageH });
    }
    if (Math.abs(scy - bcy) < threshold) {
      guides.push({ x1: 0, y1: bcy, x2: pageW, y2: bcy });
    }
  }

  if (!guides.length) return null;

  return (
    <svg className="pointer-events-none absolute inset-0" style={{ zIndex: 9998 }} width={pageW} height={pageH}>
      {guides.map((g, i) => (
        <line
          key={i}
          x1={g.x1} y1={g.y1} x2={g.x2} y2={g.y2}
          stroke="#a78bfa"
          strokeWidth={1}
          strokeDasharray="4 4"
          opacity={0.6}
        />
      ))}
    </svg>
  );
}
