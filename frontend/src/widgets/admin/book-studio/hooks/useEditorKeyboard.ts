'use client';

import { useEffect } from 'react';
import { useBookStudioStore } from '../store/useBookStudioStore';

export function useEditorKeyboard() {
  const {
    active, blocks, selectedBlockId, interactionMode,
    undo, redo, deleteBlock, patchBlock, setSelected,
    copyBlock, pasteBlock, save,
  } = useBookStudioStore() as any;

  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      const target = e.target as HTMLElement;
      const isEditing = target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable;

      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'z') {
        e.preventDefault();
        e.shiftKey ? redo() : undo();
        return;
      }

      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
        e.preventDefault();
        void save();
        return;
      }

      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'c' && selectedBlockId && !isEditing) {
        e.preventDefault();
        copyBlock(selectedBlockId);
        return;
      }

      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'v' && !isEditing) {
        e.preventDefault();
        pasteBlock();
        return;
      }

      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'd' && selectedBlockId && !isEditing) {
        e.preventDefault();
        useBookStudioStore.getState().duplicateBlock(selectedBlockId);
        return;
      }

      if (isEditing) return;
      if (interactionMode !== 'select') return;

      if (e.key === 'Escape') {
        setSelected('');
        return;
      }

      if (e.key === 'Delete' || e.key === 'Backspace') {
        if (selectedBlockId) {
          e.preventDefault();
          deleteBlock(selectedBlockId);
        }
        return;
      }

      const block = blocks.find((b: any) => b.id === selectedBlockId);
      if (!block) return;

      const delta = e.shiftKey ? 10 : 1;
      const moves: Record<string, Partial<any>> = {
        ArrowLeft: { x: block.x - delta },
        ArrowRight: { x: block.x + delta },
        ArrowUp: { y: block.y - delta },
        ArrowDown: { y: block.y + delta },
      };
      if (moves[e.key]) {
        e.preventDefault();
        patchBlock(block.id, moves[e.key]);
      }
    };

    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  });
}
