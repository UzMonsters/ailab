'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { useBookDrag } from '../dnd/useBookDrag';
import type { BookPageBlock } from '../BookPageRenderer';

interface BookBlockFrameProps {
  block: BookPageBlock;
  children: React.ReactNode;
  onSelect: (id: string) => void;
  selected: boolean;
}

export function BookBlockFrame({ block, children, onSelect, selected }: BookBlockFrameProps) {
  const { startDrag } = useBookDrag();
  const { interactionMode, setInteractionMode } = useBookStudioStore();

  const handlePointerDown = (e: React.PointerEvent) => {
    e.stopPropagation();
    onSelect(block.id);

    // If it's selected and we are in text-edit mode, let the rich text editor handle events.
    if (selected && interactionMode === 'text-edit') {
      return;
    }
    
    // Otherwise, start dragging
    startDrag(block.id, e);
  };

  const handleDoubleClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (block.kind === 'RICH_TEXT') {
      setInteractionMode('text-edit');
    }
  };

  return (
    <div
      onPointerDown={handlePointerDown}
      onDoubleClick={handleDoubleClick}
      className={`absolute overflow-visible ${selected ? 'z-50' : ''}`}
      style={{
        left: block.x,
        top: block.y,
        width: block.w,
        height: block.h,
        zIndex: selected ? 9999 : block.z,
        opacity: block.opacity ?? 1,
        transform: block.rotation ? `rotate(${block.rotation}deg)` : undefined,
        cursor: interactionMode === 'text-edit' && selected ? 'text' : 'grab',
      }}
    >
      <div 
        className="h-full w-full" 
        style={{ 
          pointerEvents: (selected && interactionMode === 'text-edit') ? 'auto' : 'none' 
        }}
      >
        {children}
      </div>
    </div>
  );
}
