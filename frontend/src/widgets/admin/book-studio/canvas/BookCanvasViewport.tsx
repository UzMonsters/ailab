'use client';

import { useRef, useCallback, useEffect, useState } from 'react';
import { useBookStudioStore } from '../store/useBookStudioStore';
import { BookPageCanvas } from './BookPageCanvas';

const PAGE_W = 590;
const PAGE_H = 760;

export function BookCanvasViewport() {
  const { zoom, setZoom, panX, panY, setPan, isPanning, setIsPanning } = useBookStudioStore();
  const viewportRef = useRef<HTMLDivElement>(null);
  const lastPos = useRef({ x: 0, y: 0 });
  const [spaceHeld, setSpaceHeld] = useState(false);

  useEffect(() => {
    const down = (e: KeyboardEvent) => { if (e.code === 'Space' && !e.repeat) { e.preventDefault(); setSpaceHeld(true); } };
    const up = (e: KeyboardEvent) => { if (e.code === 'Space') setSpaceHeld(false); };
    window.addEventListener('keydown', down);
    window.addEventListener('keyup', up);
    return () => { window.removeEventListener('keydown', down); window.removeEventListener('keyup', up); };
  }, []);

  const onPointerDown = useCallback((e: React.PointerEvent) => {
    if (spaceHeld || e.button === 1) {
      e.preventDefault();
      setIsPanning(true);
      lastPos.current = { x: e.clientX, y: e.clientY };
      (e.target as HTMLElement).setPointerCapture(e.pointerId);
    }
  }, [spaceHeld, setIsPanning]);

  const onPointerMove = useCallback((e: React.PointerEvent) => {
    if (!isPanning) return;
    const dx = e.clientX - lastPos.current.x;
    const dy = e.clientY - lastPos.current.y;
    lastPos.current = { x: e.clientX, y: e.clientY };
    setPan(panX + dx, panY + dy);
  }, [isPanning, panX, panY, setPan]);

  const onPointerUp = useCallback(() => {
    setIsPanning(false);
  }, [setIsPanning]);

  const onWheel = useCallback((e: React.WheelEvent) => {
    if (e.ctrlKey || e.metaKey) {
      e.preventDefault();
      const delta = e.deltaY > 0 ? -0.05 : 0.05;
      setZoom(zoom + delta);
    }
  }, [zoom, setZoom]);

  return (
    <div
      ref={viewportRef}
      className={`flex-1 overflow-hidden bg-[#1a1f2e] ${isPanning || spaceHeld ? 'cursor-grab' : 'cursor-default'}`}
      onPointerDown={onPointerDown}
      onPointerMove={onPointerMove}
      onPointerUp={onPointerUp}
      onWheel={onWheel}
    >
      <div
        className="origin-center"
        style={{
          transform: `translate(${panX}px, ${panY}px) scale(${zoom})`,
          transition: isPanning ? 'none' : 'transform 0.1s ease-out',
          width: '100%',
          height: '100%',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <BookPageCanvas />
      </div>
    </div>
  );
}
