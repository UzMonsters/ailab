'use client';
import { useEffect, useState, useCallback } from 'react';
import type { RuntimeHint } from './runtime.types';

type Anchor = { x: number; y: number; width: number; height: number };
const anchor = (attribute: 'scenarioAlias' | 'scenarioPort', value?: string): Anchor | null => {
  if (!value) return null;
  const nodes = Array.from(document.querySelectorAll<HTMLElement>(attribute === 'scenarioAlias' ? '[data-scenario-alias]' : '[data-scenario-port]'));
  const node = nodes.find((candidate) => candidate.dataset[attribute] === value);
  if (!node) return null;
  const rect = node.getBoundingClientRect();
  return rect.width && rect.height ? { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2, width: rect.width, height: rect.height } : null;
};

const highlightTypes = new Set(['HIGHLIGHT', 'HIGHLIGHT_EQUIPMENT', 'HIGHLIGHT_MATERIAL', 'HIGHLIGHT_OBJECT', 'HIGHLIGHT_PORT']);
const arrowTypes = new Set(['ARROW', 'SHOW_ARROW']);
const ghostTypes = new Set(['GHOST_PLACEMENT', 'GHOST_PLACE_OBJECT']);
const mediaTypes = new Set(['VIDEO', 'IMAGE']);
const noAnchorTypes = new Set(['OPEN_TAB', 'HIGHLIGHT_TAB', 'VIDEO', 'IMAGE']);

export function ScenarioGuideOverlay({ hint, active, onOpenTab }: { hint?: RuntimeHint; active: boolean; onOpenTab?: (tab: string) => void }) {
  const [from, setFrom] = useState<Anchor | null>(null);
  const [to, setTo] = useState<Anchor | null>(null);

  useEffect(() => {
    if (!active || !hint || noAnchorTypes.has(hint.type)) {
      const resetTimer = window.setTimeout(() => { setFrom(null); setTo(null); }, 0);
      return () => window.clearTimeout(resetTimer);
    }
    const update = () => {
      if (hint.type === 'CONNECT_PORTS' || arrowTypes.has(hint.type)) {
        setFrom(anchor(hint.fromPortId ? 'scenarioPort' : 'scenarioAlias', hint.fromPortId ? `${hint.fromAlias}:${hint.fromPortId}` : hint.fromAlias));
        setTo(anchor(hint.toPortId ? 'scenarioPort' : 'scenarioAlias', hint.toPortId ? `${hint.toAlias}:${hint.toPortId}` : hint.toAlias));
      } else {
        setFrom(null);
        setTo(anchor(hint.targetPortId ? 'scenarioPort' : 'scenarioAlias', hint.targetPortId ? `${hint.targetAlias}:${hint.targetPortId}` : hint.targetAlias));
      }
    };
    update();
    const timer = window.setInterval(update, 180);
    window.addEventListener('resize', update);
    return () => { window.clearInterval(timer); window.removeEventListener('resize', update); };
  }, [active, hint]);

  const handleOpenTab = useCallback(() => {
    if (hint?.targetTab && onOpenTab) onOpenTab(hint.targetTab);
  }, [hint, onOpenTab]);

  useEffect(() => {
    if (active && hint?.type === 'OPEN_TAB' && hint.targetTab && onOpenTab) {
      onOpenTab(hint.targetTab);
    }
  }, [active, hint, onOpenTab]);

  if (!active || !hint) return null;
  if (!noAnchorTypes.has(hint.type) && !from && !to) return null;

  const isHighlight = highlightTypes.has(hint.type);
  const isGhost = ghostTypes.has(hint.type);

  return (
    <div className="pointer-events-none fixed inset-0 z-[246]" aria-hidden="true">
      <svg className="absolute inset-0 h-full w-full overflow-visible">
        <defs>
          <marker id="scenario-guide-arrow" markerWidth="9" markerHeight="9" refX="8" refY="4.5" orient="auto">
            <path d="M0,0 L9,4.5 L0,9 z" fill="#fbbf24" />
          </marker>
        </defs>
        {from && to && (
          <path
            d={`M ${from.x} ${from.y} C ${(from.x + to.x) / 2} ${from.y - 42}, ${(from.x + to.x) / 2} ${to.y + 42}, ${to.x} ${to.y}`}
            fill="none" stroke="#fbbf24" strokeWidth="3" strokeDasharray="8 5"
            markerEnd="url(#scenario-guide-arrow)"
            className="motion-safe:animate-pulse"
          />
        )}
        {to && (
          <rect
            x={to.x - to.width / 2 - 7} y={to.y - to.height / 2 - 7}
            width={to.width + 14} height={to.height + 14} rx="14"
            fill={isGhost ? 'rgba(251,191,36,.12)' : isHighlight ? 'rgba(251,191,36,.08)' : 'none'}
            stroke="#fbbf24" strokeWidth="3"
            strokeDasharray={isGhost ? '9 6' : undefined}
            className="motion-safe:animate-pulse"
          />
        )}
      </svg>
      {hint.text && to && (
        <span
          className="absolute max-w-64 -translate-x-1/2 rounded-lg border border-amber-300/40 bg-slate-950/95 px-3 py-2 text-xs font-semibold text-amber-100 shadow-xl"
          style={{ left: to.x, top: to.y + to.height / 2 + 14 }}
        >
          {hint.text}
        </span>
      )}
      {hint.type === 'OPEN_TAB' && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-2 rounded-lg border border-cyan-300/40 bg-slate-950/95 px-4 py-2 text-xs font-semibold text-cyan-100 shadow-xl">
          <span className="inline-block h-2 w-2 rounded-full bg-cyan-400 motion-safe:animate-pulse" />
          Open: {hint.targetTab?.replace('SANDBOX_', '').replace('_TAB', '')}
        </div>
      )}
      {hint.type === 'HIGHLIGHT_TAB' && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-2 rounded-lg border border-amber-300/40 bg-slate-950/95 px-4 py-2 text-xs font-semibold text-amber-100 shadow-xl">
          <span className="inline-block h-2 w-2 rounded-full bg-amber-400 motion-safe:animate-pulse" />
          Highlight: {hint.targetTab?.replace('SANDBOX_', '').replace('_TAB', '')}
        </div>
      )}
      {hint.type === 'VIDEO' && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 rounded-lg border border-violet-300/40 bg-slate-950/95 px-4 py-3 shadow-xl">
          <p className="mb-2 text-xs font-semibold text-violet-100">Video hint</p>
          {hint.assetUrl ? (
            <video src={hint.assetUrl} controls className="max-h-48 rounded" />
          ) : hint.text ? (
            <p className="text-xs text-slate-300">{hint.text}</p>
          ) : null}
        </div>
      )}
      {hint.type === 'IMAGE' && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 rounded-lg border border-blue-300/40 bg-slate-950/95 px-4 py-3 shadow-xl">
          <p className="mb-2 text-xs font-semibold text-blue-100">Image hint</p>
          {hint.assetUrl ? (
            <img src={hint.assetUrl} alt={hint.text || 'Hint'} className="max-h-48 rounded" />
          ) : hint.text ? (
            <p className="text-xs text-slate-300">{hint.text}</p>
          ) : null}
        </div>
      )}
      {hint.type === 'HIGHLIGHT_MATERIAL' && !to && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-2 rounded-lg border border-amber-300/40 bg-slate-950/95 px-4 py-2 text-xs font-semibold text-amber-100 shadow-xl">
          <span className="inline-block h-2 w-2 rounded-full bg-amber-400 motion-safe:animate-pulse" />
          {hint.text || 'Check Materials tab'}
        </div>
      )}
      {hint.type === 'HIGHLIGHT_EQUIPMENT' && !to && (
        <div className="pointer-events-auto fixed bottom-4 left-1/2 -translate-x-1/2 flex items-center gap-2 rounded-lg border border-amber-300/40 bg-slate-950/95 px-4 py-2 text-xs font-semibold text-amber-100 shadow-xl">
          <span className="inline-block h-2 w-2 rounded-full bg-amber-400 motion-safe:animate-pulse" />
          {hint.text || 'Check Equipment catalog'}
        </div>
      )}
    </div>
  );
}
