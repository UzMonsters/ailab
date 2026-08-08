'use client';
import { useRef, useState, useEffect } from 'react';
import type { CSSProperties } from 'react';
import { Atom, FlaskConical, Sparkles, Radio, Database } from 'lucide-react';

const stats = [
  { value: '118', label: 'Elements indexed', icon: Atom, accent: '#c0a1ff', detail: '+2 today' },
  { value: '12,458', label: 'Molecular models', icon: FlaskConical, accent: '#a78bfa', detail: '+184 this week' },
  { value: '4,231', label: 'Reaction pathways', icon: Sparkles, accent: '#8b5cf6', detail: '12 queued' },
  { value: '42', label: 'Active simulations', icon: Radio, accent: '#a855f7', live: true, detail: 'queue: 7' },
  { value: '1.2m', label: 'Data points today', icon: Database, accent: '#60a5fa', big: true, heatmap: true, detail: '≈1.2M / 24h' },
];

function Reveal({ children, className = '' }: { children: React.ReactNode; className?: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(false);
  useEffect(() => {
    const node = ref.current;
    if (!node) return;
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) { setVisible(true); observer.disconnect(); }
    }, { threshold: 0.12 });
    observer.observe(node);
    return () => observer.disconnect();
  }, []);
  return <div ref={ref} data-reveal className={`${className} ${visible ? 'is-visible' : ''}`}>{children}</div>;
}

function CountUp({ value, duration = 2000 }: { value: string; duration?: number }) {
  const ref = useRef<HTMLElement>(null);
  const [display, setDisplay] = useState('0');
  useEffect(() => {
    const match = value.match(/^([^\d]*)([\d.,]+)(.*)$/);
    const prefix = match?.[1] ?? '';
    const suffix = match?.[3] ?? '';
    const target = Number.parseFloat((match?.[2] ?? '0').replace(/,/g, ''));
    const decimals = (match?.[2].split('.')[1] ?? '').length;
    const formatter = new Intl.NumberFormat('en-US', { minimumFractionDigits: decimals, maximumFractionDigits: decimals });
    let started = false;
    const observer = new IntersectionObserver(([entry]) => {
      if (!entry.isIntersecting || started) return;
      started = true;
      const start = performance.now();
      const tick = (now: number) => {
        const progress = Math.min(1, (now - start) / duration);
        const eased = 1 - Math.pow(1 - progress, 3);
        setDisplay(`${prefix}${formatter.format(target * eased)}${suffix}`);
        if (progress < 1) requestAnimationFrame(tick);
      };
      requestAnimationFrame(tick);
      observer.disconnect();
    }, { threshold: 0.4 });
    observer.observe(ref.current);
    return () => observer.disconnect();
  }, [value, duration]);
  return <strong className="stat-value" ref={ref}>{display}</strong>;
}

function MiniHeatmap({ seed, accent }: { seed: number; accent: string }) {
  const cells = Array.from({ length: 40 }).map((_, i) => 0.14 + ((seed * (i + 3) * 7) % 10) / 11);
  return <div className="stat-heatmap" aria-hidden="true">{cells.map((opacity, i) => <i key={i} style={{ background: accent, opacity }} />)}</div>;
}

function Sparkline({ seed, accent }: { seed: number; accent: string }) {
  const points = Array.from({ length: 14 }).map((_, i) => ({
    x: i * 8,
    y: 52 - (20 + Math.abs(Math.sin((i + seed) * 1.6)) * 24 + (i * 7) % 13),
  }));
  const line = points.map((p) => `${p.x},${p.y}`).join(' ');
  const last = points[points.length - 1];
  return (
    <svg className="stat-sparkline" viewBox="0 0 104 52" preserveAspectRatio="none" aria-hidden="true">
      <polyline className="spark-draw" points={line} fill="none" stroke={accent} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" pathLength="100" />
      <circle className="spark-dot" cx={last.x} cy={last.y} r="2.6" fill={accent} />
    </svg>
  );
}

function SectionDecor() {
  return (
    <div className="section-decor section-decor-stats" aria-hidden="true">
      <img className="decor-img decor-blueprint" src="/decor-blueprint.png" alt="" loading="lazy" />
      <img className="decor-img decor-atom" src="/decor-atom.png" alt="" loading="lazy" />
    </div>
  );
}

export default function StatsSection() {
  const [liveCount, setLiveCount] = useState(42);
  useEffect(() => {
    const id = window.setInterval(() => {
      setLiveCount((c) => (c >= 50 ? 42 : c + 1));
    }, 3000);
    return () => window.clearInterval(id);
  }, []);

  const statsData = stats.map((s) => s.live ? { ...s, value: String(liveCount) } : s);

  return (
    <section className="section-wrap section-block stats-section">
      <SectionDecor />
      <Reveal className="section-heading">
        <div>
          <p className="eyebrow">The laboratory, live</p>
          <h2>Built for the next result.</h2>
        </div>
      </Reveal>
      <Reveal className="stats-grid reveal-stagger">
        {statsData.map(({ value, label, icon: Icon, accent, big, live, heatmap, detail }, index) => (
          <div className={`stat-card ${big ? 'stat-big' : 'stat-small'}${live ? ' stat-live-card' : ''}`} key={label} style={{ '--stat-accent': accent } as CSSProperties}>
            <div className="stat-top">
              {live ? <span className="stat-live-pill"><i /> LIVE</span> : <Icon className="stat-icon" />}
            </div>
            <div className="stat-main">
              {live ? <strong className="stat-value">{value}</strong> : <CountUp value={value} />}
              <span className="stat-label">{label}</span>
              <span className="stat-detail">{detail}</span>
            </div>
            {heatmap ? <MiniHeatmap seed={index + value.length} accent={accent} /> : <Sparkline seed={index + value.length} accent={accent} />}
          </div>
        ))}
      </Reveal>
    </section>
  );
}
