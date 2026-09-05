import type { ReactNode } from 'react';
import { X } from 'lucide-react';

export function MobileSheet({ title, onClose, children }: { title: string; onClose: () => void; children: ReactNode }) {
  return <div className="fixed inset-0 z-[80] flex items-end bg-black/60 min-[1600px]:hidden" onClick={onClose}><section className="max-h-[88dvh] w-full overflow-y-auto rounded-t-3xl border-t border-border bg-card p-4 pb-[calc(1rem+env(safe-area-inset-bottom))] shadow-2xl md:mx-auto md:mb-5 md:max-w-2xl md:rounded-3xl md:border" onClick={(event) => event.stopPropagation()}><div className="mb-3 flex items-center justify-between"><h2 className="text-sm font-bold">{title}</h2><button aria-label="Close panel" onClick={onClose} className="grid h-9 w-9 place-items-center rounded-lg border border-border"><X size={16} /></button></div>{children}</section></div>;
}

export function MeasurementCard({ label, value }: { label: string; value: string }) {
  return <div className="rounded-xl border border-border bg-white/[.03] p-3"><p className="text-[10px] uppercase tracking-wider text-[var(--muted-foreground)]">{label}</p><p className="mt-1 font-mono text-sm font-semibold text-foreground">{value}</p></div>;
}

export function MiniChart({ title, samples, field, color }: { title: string; samples: Array<Record<string, number | string>>; field: string; color: string }) {
  const values = samples.map((sample) => Number(sample[field] ?? 0));
  const max = Math.max(...values, 1) + 1;
  const min = Math.max(0, Math.min(...values, 0) - 1);
  const range = Math.max(max - min, 1);
  
  // Calculate points
  const w = 280, h = 60;
  const pointCoords = values.map((value, index) => ({
    x: (index / Math.max(values.length - 1, 1)) * w + 10,
    y: 75 - ((value - min) / range) * h
  }));
  
  // Create simple smooth curve using bezier curves
  let path = '';
  if (pointCoords.length > 0) {
    path = `M ${pointCoords[0].x},${pointCoords[0].y}`;
    for (let i = 1; i < pointCoords.length; i++) {
      const p0 = pointCoords[i - 1];
      const p1 = pointCoords[i];
      const cx = (p0.x + p1.x) / 2;
      path += ` C ${cx},${p0.y} ${cx},${p1.y} ${p1.x},${p1.y}`;
    }
  } else {
    path = `M 10,75 L 290,75`;
  }

  const fillPath = path.replace('M', 'M').replace('L 290,75', '') + ` L ${pointCoords.length ? pointCoords[pointCoords.length - 1].x : 290},80 L 10,80 Z`;

  return (
    <div className="relative overflow-hidden rounded-xl border border-border bg-gradient-to-b from-white/[0.03] to-transparent p-4 shadow-inner">
      <div className="mb-2 flex items-end justify-between">
        <span className="text-xs font-semibold tracking-wider text-[var(--muted-foreground)] uppercase">{title}</span>
        <span className="font-mono text-sm font-bold text-foreground drop-shadow-sm">{values.at(-1)?.toFixed(2) ?? '—'}</span>
      </div>
      <svg viewBox="0 0 300 80" className="h-20 w-full overflow-visible" role="img" aria-label={`${title} chart`}>
        <defs>
          <linearGradient id={`grad-${field}`} x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor={color} stopOpacity="0.4" />
            <stop offset="100%" stopColor={color} stopOpacity="0.0" />
          </linearGradient>
          <filter id={`glow-${field}`}><feGaussianBlur stdDeviation="2" result="coloredBlur"/><feMerge><feMergeNode in="coloredBlur"/><feMergeNode in="SourceGraphic"/></feMerge></filter>
        </defs>
        
        {/* Grid lines */}
        <line x1="10" y1="15" x2="290" y2="15" stroke="var(--border)" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="10" y1="45" x2="290" y2="45" stroke="var(--border)" strokeWidth="1" strokeDasharray="4 4" />
        <line x1="10" y1="75" x2="290" y2="75" stroke="rgba(255,255,255,0.15)" strokeWidth="1" />
        
        {/* Area fill */}
        {pointCoords.length > 0 && (
          <path d={fillPath} fill={`url(#grad-${field})`} />
        )}
        
        {/* Main Line */}
        <path 
          d={path} 
          fill="none" 
          stroke={color} 
          strokeWidth="2.5" 
          strokeLinecap="round" 
          strokeLinejoin="round" 
          filter={`url(#glow-${field})`}
        />
        
        {/* Active Point */}
        {pointCoords.length > 0 && (
          <circle cx={pointCoords[pointCoords.length - 1].x} cy={pointCoords[pointCoords.length - 1].y} r="3" fill="#fff" stroke={color} strokeWidth="1.5" className="animate-pulse" />
        )}
      </svg>
    </div>
  );
}
