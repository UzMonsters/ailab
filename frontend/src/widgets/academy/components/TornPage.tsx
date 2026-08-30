"use client";

import { type ReactNode } from "react";

// The torn page effect. This component renders a page spread where
// the left page appears to be physically torn, revealing the Sandbox
// grid behind it. The right page contains the final handwritten notes.

export function TornPageSpread({
  leftContent,
  rightContent,
}: {
  leftContent?: ReactNode;
  rightContent: ReactNode;
}) {
  return (
    <>
      {/* LEFT PAGE — with tear effect */}
      <section className="academy-page-surface left">
        <div className="academy-page-inner page-layout-torn">
          {/* Sandbox grid visible through the tear */}
          <div className="torn-bg-reveal" aria-hidden="true" />

          {/* The surviving left portion with content */}
          <div className="torn-page-clip">
            {leftContent && (
              <div className="torn-page-text">
                {leftContent}
              </div>
            )}
          </div>

          {/* SVG torn edge overlay */}
          <TornEdge />

          <span className="academy-page-number" style={{ left: 48 }}>085</span>
        </div>
      </section>

      {/* RIGHT PAGE — rushed handwriting ending */}
      <section className="academy-page-surface right">
        <div className="academy-page-inner page-layout-torn">
          <span className="academy-running-header">НЕЗАВЕРШЁННЫЙ ОПЫТ</span>
          {rightContent}
          <span className="academy-page-number" style={{ right: 48 }}>086</span>
        </div>
      </section>
    </>
  );
}

// Second torn page — even more damaged
export function TornPageFinalSpread({ finalText }: { finalText: ReactNode }) {
  return (
    <>
      {/* LEFT PAGE — mostly torn away */}
      <section className="academy-page-surface left">
        <div className="academy-page-inner page-layout-torn">
          <div className="torn-bg-reveal" aria-hidden="true" />

          {/* Only a fragment survives */}
          <div className="torn-page-clip torn-page-clip-heavy">
            <div className="torn-page-text torn-fragment">
              {finalText}
            </div>
          </div>

          <TornEdgeHeavy />

          <span className="academy-page-number" style={{ left: 48 }}>087</span>
        </div>
      </section>

      {/* RIGHT PAGE — fully torn, only grid visible */}
      <section className="academy-page-surface right">
        <div className="academy-page-inner page-layout-torn" style={{ position: "relative" }}>
          {/* The page is entirely removed — sandbox grid shows */}
          <div className="torn-bg-reveal torn-bg-reveal-full" aria-hidden="true" />

          {/* Torn edge on right side */}
          <div className="torn-right-edge" aria-hidden="true" />

          {/* Ghost fragment of last sentence */}
          <div
            className="torn-ghost-text codex-handwriting"
            style={{
              position: "absolute",
              top: "30%",
              left: "20%",
              transform: "rotate(-3deg)",
              opacity: 0.25,
              fontSize: "1.4rem",
              color: "var(--book-text)",
              maxWidth: "200px",
            }}
          >
            ...не забудь проверить...
          </div>

          <span className="academy-page-number" style={{ right: 48 }}>088</span>
        </div>
      </section>
    </>
  );
}

// ─── SVG torn edge shape ─────────────────────────────────────────────────────

function TornEdge() {
  // Irregular torn edge polygon - right side of left page
  // The tear goes roughly from top-right diagonally to bottom, with jagged edges
  return (
    <svg
      className="torn-edge-svg"
      viewBox="0 0 80 680"
      xmlns="http://www.w3.org/2000/svg"
      preserveAspectRatio="none"
    >
      {/* Paper fiber texture along the tear */}
      <defs>
        <filter id="paper-rough">
          <feTurbulence type="fractalNoise" baseFrequency="0.9" numOctaves="4" result="noise" />
          <feDisplacementMap in="SourceGraphic" in2="noise" scale="2" />
        </filter>
        <linearGradient id="tear-shadow" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stopColor="rgba(0,0,0,0.3)" />
          <stop offset="100%" stopColor="rgba(0,0,0,0)" />
        </linearGradient>
      </defs>

      {/* Shadow of the tear */}
      <path
        d="M30,0 Q35,40 28,80 Q22,120 32,160 Q38,200 25,240 Q18,280 30,320 Q40,360 22,400 Q15,440 28,480 Q35,520 20,560 Q12,600 25,640 Q30,660 25,680 L0,680 L0,0 Z"
        fill="url(#tear-shadow)"
        opacity="0.4"
      />

      {/* The torn edge itself — irregular polygon */}
      <path
        d="M55,0 Q50,15 58,30 Q62,45 50,62 Q44,78 56,95 Q60,112 48,128 Q40,144 54,162 Q62,178 46,196 Q38,214 52,232 Q58,250 42,268 Q34,286 50,305 Q56,322 40,340 Q32,358 48,377 Q56,394 38,412 Q30,430 46,448 Q54,465 36,483 Q28,500 44,518 Q52,535 34,553 Q26,570 42,588 Q50,605 32,622 Q24,638 40,656 Q48,668 38,680 L80,680 L80,0 Z"
        fill="var(--book-page, #F0EAD8)"
        filter="url(#paper-rough)"
      />

      {/* Fiber lines at tear edge */}
      {[20, 60, 110, 160, 210, 260, 310, 360, 420, 470, 530, 580, 640].map((y, i) => (
        <line
          key={i}
          x1={55 + Math.sin(i) * 5}
          y1={y}
          x2={55 + Math.sin(i) * 5 + 8 + Math.cos(i * 2) * 4}
          y2={y + 12}
          stroke="var(--book-border)"
          strokeWidth="0.5"
          opacity="0.6"
        />
      ))}
    </svg>
  );
}

function TornEdgeHeavy() {
  return (
    <svg
      className="torn-edge-svg torn-edge-heavy"
      viewBox="0 0 120 680"
      xmlns="http://www.w3.org/2000/svg"
      preserveAspectRatio="none"
    >
      <defs>
        <filter id="paper-rough2">
          <feTurbulence type="fractalNoise" baseFrequency="0.8" numOctaves="5" result="noise" />
          <feDisplacementMap in="SourceGraphic" in2="noise" scale="3" />
        </filter>
        <linearGradient id="tear-shadow2" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stopColor="rgba(0,0,0,0.35)" />
          <stop offset="100%" stopColor="rgba(0,0,0,0)" />
        </linearGradient>
      </defs>

      <path
        d="M60,0 Q55,20 68,45 Q74,70 56,90 Q44,108 66,130 Q76,152 54,170 Q40,190 62,215 Q72,238 48,258 Q34,278 58,302 Q70,325 44,345 Q30,365 55,390 Q68,412 42,432 Q28,452 53,478 Q66,500 38,522 Q24,542 50,568 Q64,590 36,612 Q22,632 48,655 Q58,670 42,680 L120,680 L120,0 Z"
        fill="var(--book-page, #F0EAD8)"
        filter="url(#paper-rough2)"
      />
    </svg>
  );
}
