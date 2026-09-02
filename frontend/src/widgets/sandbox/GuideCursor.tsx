"use client";

import { MousePointer2 } from "lucide-react";
import { useEffect, useState } from "react";

type Point = { x: number; y: number };

function targetPoint(target: string): Point | null {
  const node = document.querySelector<HTMLElement>(`[data-help-target="${target}"]`);
  if (!node) return null;
  const rect = node.getBoundingClientRect();
  return rect.width && rect.height ? { x: rect.left + rect.width / 2, y: rect.top + rect.height / 2 } : null;
}

/** A visual-only connection demonstration. It never calls the sandbox engine. */
export function GuideCursor({ active, locale, onFinish }: { active: boolean; locale: string; onFinish: () => void }) {
  const [index, setIndex] = useState(0);
  const [points, setPoints] = useState<Point[]>([]);

  useEffect(() => {
    if (!active) return;
    const next = ["toolbar:connect", "port:thermometer:sensor", "port:beaker:sensor"].map(targetPoint).filter((point): point is Point => Boolean(point));
    setPoints(next);
    setIndex(0);
    if (next.length < 3) {
      const timer = window.setTimeout(onFinish, 900);
      return () => window.clearTimeout(timer);
    }
    const timers = [
      window.setTimeout(() => setIndex(1), 700),
      window.setTimeout(() => setIndex(2), 1500),
      window.setTimeout(onFinish, 2400),
    ];
    return () => timers.forEach(window.clearTimeout);
  }, [active, onFinish]);

  if (!active || points.length === 0) return null;
  const point = points[index] ?? points[0];
  const label = locale === "ru" ? "Теперь повторите сами" : locale === "uz" ? "Endi o'zingiz takrorlang" : "Now try it yourself";
  const source = points[1];
  const showLine = index >= 1 && source;
  return (
    <div className="pointer-events-none fixed inset-0 z-[245]" aria-hidden="true">
      {showLine && <svg className="absolute inset-0 h-full w-full overflow-visible"><path d={`M ${source.x} ${source.y} C ${(source.x + point.x) / 2} ${source.y - 28}, ${(source.x + point.x) / 2} ${point.y + 28}, ${point.x} ${point.y}`} fill="none" stroke="#5ddcff" strokeWidth="2" strokeDasharray="5 5" className="animate-pulse" /></svg>}
      <div className="absolute transition-[left,top] duration-700 ease-in-out" style={{ left: point.x, top: point.y, transform: "translate(-4px,-4px)" }}>
        <MousePointer2 size={26} className="fill-white text-violet-500 drop-shadow-[0_0_8px_rgba(156,107,255,.9)]" />
        <span className="absolute -right-20 top-7 whitespace-nowrap rounded-md border border-violet-300/50 bg-slate-950/95 px-2 py-1 text-[10px] font-semibold text-white">{index === 2 ? label : "•"}</span>
      </div>
    </div>
  );
}
