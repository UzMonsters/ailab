"use client";

import { type ReactNode } from "react";

// ─── Info Boxes ─────────────────────────────────────────────────────────────

type InfoBoxType = "green" | "purple" | "blue" | "red";

export function InfoBox({
  type,
  title,
  children,
}: {
  type: InfoBoxType;
  title: string;
  children: ReactNode;
}) {
  return (
    <div className={`info-box info-box-${type}`}>
      <span className="info-box-label">{title}</span>
      <div className="info-box-content">{children}</div>
    </div>
  );
}

// ─── Section Heading ─────────────────────────────────────────────────────────

export function SectionHeading({
  number,
  title,
}: {
  number: string;
  title: string;
}) {
  return (
    <div className="section-heading-block">
      <span className="section-number codex-mono">{number}</span>
      <h3 className="section-heading codex-cinzel">{title}</h3>
    </div>
  );
}

// ─── Figure Caption ──────────────────────────────────────────────────────────

export function FigureCaption({ children }: { children: ReactNode }) {
  return <p className="figure-caption codex-mono">{children}</p>;
}

// ─── Chapter Header ──────────────────────────────────────────────────────────

export function ChapterHeader({
  roman,
  title,
  subtitle,
  epigraph,
}: {
  roman: string;
  title: string;
  subtitle?: string;
  epigraph?: string;
}) {
  return (
    <div className="chapter-opening">
      <span className="chapter-roman codex-mono">{roman}</span>
      <h1 className="chapter-title codex-cinzel">{title}</h1>
      {subtitle && <p className="chapter-subtitle codex-serif">{subtitle}</p>}
      <div className="chapter-divider" />
      {epigraph && (
        <blockquote className="chapter-epigraph codex-serif">
          {epigraph}
        </blockquote>
      )}
    </div>
  );
}

// ─── Drop Cap Paragraph ──────────────────────────────────────────────────────

export function DropCapParagraph({ children }: { children: string }) {
  return (
    <p className="codex-serif drop-cap">
      {children}
    </p>
  );
}

// ─── Handwritten Margin Note ─────────────────────────────────────────────────

export function MarginNote({
  position = "right",
  color = "pencil",
  children,
  rotate,
}: {
  position?: "left" | "right" | "bottom";
  color?: "red" | "pencil";
  children: ReactNode;
  rotate?: number;
}) {
  const cls = `margin-note margin-note-${position} margin-note-${color}`;
  const style = rotate !== undefined ? { transform: `rotate(${rotate}deg)` } : undefined;
  return (
    <div className={`${cls} codex-handwriting`} style={style}>
      {children}
    </div>
  );
}

// ─── SVG Strike-Through ──────────────────────────────────────────────────────

export function HandwrittenStrike({ children }: { children: ReactNode }) {
  return (
    <span className="handwritten-strike">
      {children}
      <span className="handwritten-strike-line" aria-hidden="true" />
    </span>
  );
}

// ─── Red Correction ──────────────────────────────────────────────────────────

export function RedCorrection({
  children,
  rotate = -2,
}: {
  children: ReactNode;
  rotate?: number;
}) {
  return (
    <span
      className="red-correction codex-handwriting"
      style={{ transform: `rotate(${rotate}deg)`, display: "inline-block" }}
    >
      {children}
    </span>
  );
}

// ─── Formula Block ────────────────────────────────────────────────────────────

export function FormulaBlock({
  label,
  children,
}: {
  label?: string;
  children: ReactNode;
}) {
  return (
    <div className="formula-block">
      {label && <span className="formula-label codex-mono">{label}</span>}
      <div className="formula-content codex-mono">{children}</div>
    </div>
  );
}

// ─── Blockquote ──────────────────────────────────────────────────────────────

export function TextbookBlockquote({ children }: { children: ReactNode }) {
  return (
    <blockquote className="blockquote-styled codex-serif">{children}</blockquote>
  );
}

// ─── History Timeline ────────────────────────────────────────────────────────

export function HistoryTimeline({ children }: { children: ReactNode }) {
  return <div className="history-timeline">{children}</div>;
}

export function HistoryPeriod({
  year,
  title,
  text,
}: {
  year: string;
  title: string;
  text: string;
}) {
  return (
    <div className="history-period">
      <span className="history-period-year codex-mono">{year}</span>
      <div className="history-period-content">
        <h4 className="history-period-title codex-serif">{title}</h4>
        <p className="history-period-text codex-serif">{text}</p>
      </div>
    </div>
  );
}

// ─── Journal Date Entry ───────────────────────────────────────────────────────

export function JournalDate({ date, expId, temp }: { date: string; expId?: string; temp?: string }) {
  return (
    <div className="journal-date-block">
      <span className="journal-date codex-handwriting">{date}</span>
      {expId && <span className="experiment-id codex-mono">{expId}</span>}
      {temp && <span className="experiment-id codex-mono">{temp}</span>}
    </div>
  );
}
