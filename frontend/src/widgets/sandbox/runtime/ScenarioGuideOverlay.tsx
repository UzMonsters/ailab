'use client';
import { useEffect, useState } from 'react';
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

export function ScenarioGuideOverlay({ hint, active }: { hint?: RuntimeHint; active: boolean }) {
  const [from, setFrom] = useState<Anchor | null>(null); const [to, setTo] = useState<Anchor | null>(null);
  useEffect(() => {
    if (!active || !hint) return;
    const update = () => {
      if (hint.type === 'CONNECT_PORTS' || hint.type === 'ARROW') {
        setFrom(anchor(hint.fromPortId ? 'scenarioPort' : 'scenarioAlias', hint.fromPortId ? `${hint.fromAlias}:${hint.fromPortId}` : hint.fromAlias));
        setTo(anchor(hint.toPortId ? 'scenarioPort' : 'scenarioAlias', hint.toPortId ? `${hint.toAlias}:${hint.toPortId}` : hint.toAlias));
      } else {
        setFrom(null); setTo(anchor(hint.targetPortId ? 'scenarioPort' : 'scenarioAlias', hint.targetPortId ? `${hint.targetAlias}:${hint.targetPortId}` : hint.targetAlias));
      }
    };
    update(); const timer = window.setInterval(update, 180); window.addEventListener('resize', update);
    return () => { window.clearInterval(timer); window.removeEventListener('resize', update); };
  }, [active, hint]);
  if (!active || !hint || (!from && !to)) return null;
  return <div className="pointer-events-none fixed inset-0 z-[246]" aria-hidden="true"><svg className="absolute inset-0 h-full w-full overflow-visible"><defs><marker id="scenario-guide-arrow" markerWidth="9" markerHeight="9" refX="8" refY="4.5" orient="auto"><path d="M0,0 L9,4.5 L0,9 z" fill="#fbbf24"/></marker></defs>{from && to && <path d={`M ${from.x} ${from.y} C ${(from.x + to.x) / 2} ${from.y - 42}, ${(from.x + to.x) / 2} ${to.y + 42}, ${to.x} ${to.y}`} fill="none" stroke="#fbbf24" strokeWidth="3" strokeDasharray="8 5" markerEnd="url(#scenario-guide-arrow)" className="motion-safe:animate-pulse"/>}{to && hint.type !== 'TEXT' && <rect x={to.x - to.width / 2 - 7} y={to.y - to.height / 2 - 7} width={to.width + 14} height={to.height + 14} rx="14" fill={hint.type === 'GHOST_PLACEMENT' ? 'rgba(251,191,36,.12)' : 'none'} stroke="#fbbf24" strokeWidth="3" strokeDasharray={hint.type === 'GHOST_PLACEMENT' ? '9 6' : undefined} className="motion-safe:animate-pulse"/>}</svg>{hint.text && to && <span className="absolute max-w-64 -translate-x-1/2 rounded-lg border border-amber-300/40 bg-slate-950/95 px-3 py-2 text-xs font-semibold text-amber-100 shadow-xl" style={{ left: to.x, top: to.y + to.height / 2 + 14 }}>{hint.text}</span>}</div>;
}
