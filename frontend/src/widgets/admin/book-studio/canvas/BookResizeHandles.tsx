'use client';

import { useCallback, useRef } from 'react';
import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

type HandleDir = 'nw' | 'n' | 'ne' | 'e' | 'se' | 's' | 'sw' | 'w';
type DragState = { dir: HandleDir; startX: number; startY: number; original: Block } | null;

const cursors: Record<HandleDir, string> = {
  nw: 'cursor-nw-resize', n: 'cursor-n-resize', ne: 'cursor-ne-resize',
  e: 'cursor-e-resize', se: 'cursor-se-resize', s: 'cursor-s-resize',
  sw: 'cursor-sw-resize', w: 'cursor-w-resize',
};

export function BookResizeHandles({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  const dragRef = useRef<DragState>(null);

  const onPointerDown = useCallback((dir: HandleDir, e: React.PointerEvent) => {
    e.stopPropagation();
    e.preventDefault();
    dragRef.current = { dir, startX: e.clientX, startY: e.clientY, original: { ...block } };
    (e.target as HTMLElement).setPointerCapture(e.pointerId);

    const moveHandler = (ev: PointerEvent) => {
      const d = dragRef.current;
      if (!d) return;
      const dx = ev.clientX - d.startX;
      const dy = ev.clientY - d.startY;
      const o = d.original;
      let patch: Partial<Block> = {};

      switch (d.dir) {
        case 'se': patch = { w: Math.max(100, o.w + dx), h: Math.max(70, o.h + dy) }; break;
        case 'e': patch = { w: Math.max(100, o.w + dx) }; break;
        case 's': patch = { h: Math.max(70, o.h + dy) }; break;
        case 'nw': patch = { x: o.x + dx, y: o.y + dy, w: Math.max(100, o.w - dx), h: Math.max(70, o.h - dy) }; break;
        case 'n': patch = { y: o.y + dy, h: Math.max(70, o.h - dy) }; break;
        case 'ne': patch = { y: o.y + dy, w: Math.max(100, o.w + dx), h: Math.max(70, o.h - dy) }; break;
        case 'sw': patch = { x: o.x + dx, w: Math.max(100, o.w - dx), h: Math.max(70, o.h + dy) }; break;
        case 'w': patch = { x: o.x + dx, w: Math.max(100, o.w - dx) }; break;
      }
      patchBlock(o.id, patch, false);
    };

    const upHandler = () => {
      dragRef.current = null;
      window.removeEventListener('pointermove', moveHandler);
      window.removeEventListener('pointerup', upHandler);
    };

    window.addEventListener('pointermove', moveHandler);
    window.addEventListener('pointerup', upHandler);
  }, [block, patchBlock]);

  const handles: HandleDir[] = ['nw', 'n', 'ne', 'e', 'se', 's', 'sw', 'w'];
  const size = 8;

  return (
    <>
      {handles.map(dir => {
        let x = 0, y = 0;
        if (dir.includes('w')) x = block.x - size / 2;
        else if (dir.includes('e')) x = block.x + block.w - size / 2;
        else x = block.x + block.w / 2 - size / 2;

        if (dir.includes('n') && !dir.includes('ne') && !dir.includes('nw')) y = block.y - size / 2;
        else if (dir === 'se' || dir === 's' || dir === 'sw') y = block.y + block.h - size / 2;
        else if (dir === 'ne' || dir === 'nw') y = block.y - size / 2;
        else y = block.y + block.h / 2 - size / 2;

        return (
          <div
            key={dir}
            className={`absolute bg-white border border-violet-500 rounded-sm ${cursors[dir]}`}
            style={{ left: x, top: y, width: size, height: size, zIndex: 10000 }}
            onPointerDown={e => onPointerDown(dir, e)}
          />
        );
      })}
    </>
  );
}
