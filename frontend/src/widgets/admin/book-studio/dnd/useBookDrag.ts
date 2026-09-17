'use client';

import { useCallback, useRef } from 'react';
import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function useBookDrag() {
  const { patchBlock, blocks, zoom } = useBookStudioStore();
  const dragRef = useRef<{ id: string; startX: number; startY: number; original: Block; originalBlocks: Block[] } | null>(null);

  const startDrag = useCallback((id: string, e: React.PointerEvent) => {
    const originalBlocks = useBookStudioStore.getState().blocks;
    const block = originalBlocks.find(b => b.id === id);
    if (!block) return;
    
    // Only capture event if it's the primary button (left click)
    if (e.button !== 0) return;
    
    e.stopPropagation();
    
    document.body.style.userSelect = 'none';

    dragRef.current = { id, startX: e.clientX, startY: e.clientY, original: { ...block }, originalBlocks };

    const moveHandler = (ev: PointerEvent) => {
      const d = dragRef.current;
      if (!d) return;
      const dx = (ev.clientX - d.startX) / zoom;
      const dy = (ev.clientY - d.startY) / zoom;
      patchBlock(d.id, {
        x: Math.max(0, d.original.x + dx),
        y: Math.max(0, d.original.y + dy),
      }, false);
    };

    const upHandler = () => {
      const d = dragRef.current;
      if (d) {
        const modifiedBlocks = useBookStudioStore.getState().blocks;
        const modifiedBlock = modifiedBlocks.find(b => b.id === d.id);
        if (modifiedBlock && (modifiedBlock.x !== d.original.x || modifiedBlock.y !== d.original.y)) {
          // Properly commit history
          useBookStudioStore.setState(state => ({
            history: [...state.history, d.originalBlocks],
            future: [],
            dirty: true,
          }));
        }
      }
      
      dragRef.current = null;
      document.body.style.userSelect = '';
      window.removeEventListener('pointermove', moveHandler);
      window.removeEventListener('pointerup', upHandler);
    };

    window.addEventListener('pointermove', moveHandler);
    window.addEventListener('pointerup', upHandler);
  }, [patchBlock, zoom]);

  return { startDrag };
}
