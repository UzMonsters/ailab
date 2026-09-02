"use client";

import { useCallback, useEffect, useRef, useState, type ReactNode } from "react";

type BookFlipProps = {
  children: ReactNode;
  currentPage: number;
  totalPages: number;
  onTurn: (direction: "next" | "prev") => void;
};

export function BookFlip({ children, currentPage, totalPages, onTurn }: BookFlipProps) {
  const pointerStartX = useRef<number | null>(null);
  const [turnDirection, setTurnDirection] = useState<'next' | 'prev'>('next');
  const [bookmarked, setBookmarked] = useState(false);
  const turn = useCallback((direction: 'next' | 'prev') => { setTurnDirection(direction); onTurn(direction); }, [onTurn]);

  // Handle Keyboard
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "ArrowRight" && currentPage < totalPages) {
        turn("next");
      } else if (e.key === "ArrowLeft" && currentPage > 1) {
        turn("prev");
      }
    };
    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [currentPage, totalPages, turn]);
  const handlePointerDown = (event: React.PointerEvent<HTMLDivElement>) => {
    if (event.pointerType === "mouse" && event.button !== 0) return;
    pointerStartX.current = event.clientX;
    event.currentTarget.setPointerCapture(event.pointerId);
  };
  const handlePointerUp = (event: React.PointerEvent<HTMLDivElement>) => {
    const startX = pointerStartX.current;
    pointerStartX.current = null;
    if (startX === null) return;
    const delta = event.clientX - startX;
    if (Math.abs(delta) < 55) return;
    if (delta < 0 && currentPage < totalPages) turn("next");
    if (delta > 0 && currentPage > 1) turn("prev");
  };

  return (
    <>
      {/* Outer Navs */}
      {currentPage > 1 && (
        <button aria-label="Previous page" className="academy-nav prev" onClick={() => turn("prev")}>
          ‹
        </button>
      )}
      {currentPage < totalPages && (
        <button aria-label="Next page" className="academy-nav next" onClick={() => turn("next")}>
          ›
        </button>
      )}

      <button type="button" aria-label={bookmarked ? 'Remove bookmark' : 'Bookmark this spread'} aria-pressed={bookmarked} className={`academy-ribbon ${bookmarked ? 'is-saved' : ''}`} onClick={() => setBookmarked(value => !value)} />
      {bookmarked && <div className="academy-bookmark-slip codex-handwriting">saved · {String(currentPage).padStart(3,'0')}</div>}
      <div className="academy-spine-shadow" />

      {/* Current Spread Container */}
      <div className="academy-page-surface left" />
      <div className="academy-page-surface right" />

      <div key={currentPage} className={`academy-spread-turn academy-spread-turn-${turnDirection}`} style={{ position: "absolute", inset: 0, zIndex: 1, display: 'flex', touchAction: 'none' }} onPointerDown={handlePointerDown} onPointerUp={handlePointerUp} onPointerCancel={() => { pointerStartX.current = null; }}>
        {children}
      </div>
    </>
  );
}
