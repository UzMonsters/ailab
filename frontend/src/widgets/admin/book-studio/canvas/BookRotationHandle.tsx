'use client';

import { useCallback, useRef } from 'react';
import { useBookStudioStore, type Block } from '../store/useBookStudioStore';

export function BookRotationHandle({ block }: { block: Block }) {
  const { patchBlock } = useBookStudioStore();
  const startAngle = useRef(0);
  const startRotation = useRef(0);

  const getAngle = useCallback((cx: number, cy: number, mx: number, my: number) => {
    return Math.atan2(my - cy, mx - cx) * (180 / Math.PI);
  }, []);

  const onPointerDown = useCallback((e: React.PointerEvent) => {
    e.stopPropagation();
    e.preventDefault();
    const cx = block.x + block.w / 2;
    const cy = block.y + block.h / 2;
    startAngle.current = getAngle(cx, cy, e.clientX, e.clientY);
    startRotation.current = block.rotation || 0;

    const moveHandler = (ev: PointerEvent) => {
      const angle = getAngle(cx, cy, ev.clientX, ev.clientY);
      let rotation = startRotation.current + (angle - startAngle.current);
      if (ev.shiftKey) {
        rotation = Math.round(rotation / 15) * 15;
      }
      patchBlock(block.id, { rotation }, false);
    };

    const upHandler = () => {
      window.removeEventListener('pointermove', moveHandler);
      window.removeEventListener('pointerup', upHandler);
    };

    window.addEventListener('pointermove', moveHandler);
    window.addEventListener('pointerup', upHandler);
  }, [block, getAngle, patchBlock]);

  const cx = block.x + block.w / 2;
  const cy = block.y - 24;

  return (
    <div
      className="absolute flex items-center justify-center cursor-grab"
      style={{ left: cx - 8, top: cy - 8, width: 16, height: 16, zIndex: 10001 }}
      onPointerDown={onPointerDown}
      title="Rotate"
    >
      <div className="h-5 w-px bg-violet-500" />
      <div className="absolute h-3 w-3 rounded-full border-2 border-violet-500 bg-[#1a1f2e]" />
    </div>
  );
}
