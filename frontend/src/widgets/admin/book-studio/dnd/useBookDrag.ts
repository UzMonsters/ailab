'use client';

import { useCallback, useRef } from 'react';
import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function useBookDrag() {
  const { patchBlock, blocks } = useBookStudioStore();
  const dragRef = useRef<{ id: string; mode: 'move'; startX: number; startY: number; original: Block } | null>(null);

  const startDrag = useCallback((id: string, e: React.PointerEvent) => {
    const block = blocks.find(b => b.id === id);
    if (!block) return;
    e.stopPropagation();
    dragRef.current = { id, mode: 'move', startX: e.clientX, startY: e.clientY, original: { ...block } };

    const moveHandler = (ev: PointerEvent) => {
      const d = dragRef.current;
      if (!d) return;
      const dx = ev.clientX - d.startX;
      const dy = ev.clientY - d.startY;
      patchBlock(d.id, {
        x: Math.max(0, d.original.x + dx),
        y: Math.max(0, d.original.y + dy),
      }, false);
    };

    const upHandler = () => {
      dragRef.current = null;
      window.removeEventListener('pointermove', moveHandler);
      window.removeEventListener('pointerup', upHandler);
    };

    window.addEventListener('pointermove', moveHandler);
    window.addEventListener('pointerup', upHandler);
  }, [blocks, patchBlock]);

  return { startDrag };
}
