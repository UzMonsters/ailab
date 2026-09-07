'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { BookResizeHandles } from './BookResizeHandles';
import { BookRotationHandle } from './BookRotationHandle';
import { blockRegistry } from '../registry/BookBlockRegistry';

export function BookSelectionOverlay() {
  const { blocks, selectedBlockId, interactionMode } = useBookStudioStore();

  if (interactionMode !== 'select' || !selectedBlockId) return null;

  const block = blocks.find(b => b.id === selectedBlockId);
  if (!block) return null;

  const config = blockRegistry[block.kind];
  const showRotation = config?.canRotate ?? false;

  return (
    <>
      <div
        className="pointer-events-none absolute rounded-sm border-2 border-violet-500"
        style={{
          left: block.x - 1,
          top: block.y - 1,
          width: block.w + 2,
          height: block.h + 2,
          zIndex: 9999,
        }}
      />
      <BookResizeHandles block={block} />
      {showRotation && <BookRotationHandle block={block} />}
    </>
  );
}
