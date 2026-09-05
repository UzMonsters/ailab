"use client";

import { forwardRef, useEffect, useRef, useState, type ReactNode } from "react";
import HTMLFlipBook from "react-pageflip";

type BookFlipProps = {
  pages: ReactNode[];
  currentPage: number;
  totalPages: number;
  onNavigate: (page: number) => void;
  initialBookmark?: number | null;
  onBookmarkChange?: (page: number | null) => void;
};

const FlipSheet = forwardRef<HTMLDivElement, { children: ReactNode }>(({ children }, ref) => (
  <div ref={ref} className="academy-pageflip-sheet" data-density="soft">
    <div className="academy-pageflip-sheet__content">{children}</div>
  </div>
));
FlipSheet.displayName = "FlipSheet";

export function BookFlip({ pages, currentPage, totalPages, onNavigate, initialBookmark, onBookmarkChange }: BookFlipProps) {
  const bookRef = useRef<{ pageFlip: () => { getCurrentPageIndex: () => number; turnToPage: (page: number) => void; flipNext: (corner: "top" | "bottom") => void; flipPrev: (corner: "top" | "bottom") => void; flip: (page: number, corner: "top" | "bottom") => void } } | null>(null);
  const [savedPage, setSavedPage] = useState<number | null>(() => {
    if (initialBookmark && initialBookmark > 0) return initialBookmark;
    if (typeof window === 'undefined') return null;
    const fallback = Number(window.localStorage.getItem("jasscience-book-bookmark"));
    return Number.isInteger(fallback) && fallback > 0 ? fallback : null;
  });
  useEffect(() => {
    if (initialBookmark == null) return;
    const timer = window.setTimeout(() => setSavedPage(current => current === initialBookmark ? current : initialBookmark > 0 ? initialBookmark : null), 0);
    return () => window.clearTimeout(timer);
  }, [initialBookmark]);
  useEffect(() => {
    const book = bookRef.current?.pageFlip();
    if (book && book.getCurrentPageIndex() !== currentPage - 1) book.turnToPage(Math.max(0, currentPage - 1));
  }, [currentPage]);
  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "ArrowRight" && currentPage < totalPages) bookRef.current?.pageFlip().flipNext("bottom");
      if (event.key === "ArrowLeft" && currentPage > 1) bookRef.current?.pageFlip().flipPrev("bottom");
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [currentPage, totalPages]);
  const bookmarked = savedPage === currentPage;
  const toggleBookmark = () => {
    if (savedPage !== null && savedPage !== currentPage) {
      bookRef.current?.pageFlip().flip(savedPage - 1, "bottom");
      return;
    }
    const next = bookmarked ? null : currentPage;
    setSavedPage(next); onBookmarkChange?.(next);
    if (next === null) window.localStorage.removeItem("jasscience-book-bookmark");
    else window.localStorage.setItem("jasscience-book-bookmark", String(next));
  };

  return (
    <>
      {currentPage > 1 && <button type="button" aria-label="Previous page" className="academy-nav prev" onClick={() => bookRef.current?.pageFlip().flipPrev("bottom")}>‹</button>}
      {currentPage < totalPages && <button type="button" aria-label="Next page" className="academy-nav next" onClick={() => bookRef.current?.pageFlip().flipNext("bottom")}>›</button>}
      <button type="button" aria-label={bookmarked ? "Remove bookmark" : "Bookmark this spread"} aria-pressed={bookmarked} className={`academy-ribbon ${bookmarked ? "is-saved" : ""}`} onClick={toggleBookmark} />
      {bookmarked && <div className="academy-bookmark-slip codex-handwriting">стр. {String(currentPage).padStart(3, "0")}</div>}
      <HTMLFlipBook
        ref={bookRef}
        className="academy-pageflip-book"
        style={{ width: "1120px", height: "680px" }}
        width={1120}
        height={680}
        size="fixed"
        minWidth={1120}
        maxWidth={1120}
        minHeight={680}
        maxHeight={680}
        startPage={Math.max(0, currentPage - 1)}
        drawShadow
        flippingTime={720}
        usePortrait
        startZIndex={10}
        autoSize={false}
        maxShadowOpacity={0.55}
        showCover={false}
        mobileScrollSupport
        clickEventForward
        useMouseEvents
        swipeDistance={24}
        showPageCorners
        disableFlipByClick={false}
        renderOnlyPageLengthChange
        onFlip={(event) => onNavigate(Number(event.data) + 1)}
      >
        {pages.map((page, index) => <FlipSheet key={index}>{page}</FlipSheet>)}
      </HTMLFlipBook>
    </>
  );
}
