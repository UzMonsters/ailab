'use client';
import { useRef, useState, useEffect } from 'react';
import { ShieldCheck } from 'lucide-react';

function CountUp({ value, duration = 2000 }: { value: string; duration?: number }) {
  const ref = useRef<HTMLElement>(null);
  const [display, setDisplay] = useState('0');
  useEffect(() => {
    const node = ref.current;
    if (!node) return;
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
    observer.observe(node);
    return () => observer.disconnect();
  }, [value, duration]);
  return <strong className="trust-num" ref={ref}>{display}</strong>;
}

export default function PlatformSection() {
  return (
    <div className="trust-bar" data-reveal>
      <span className="trust-label"><ShieldCheck /> Trusted by researchers</span>
      <div className="trust-items">
        <div><CountUp value="480" /><span>Models</span></div>
        <div><CountUp value="920K" /><span>Simulations</span></div>
        <div><CountUp value="38" /><span>Disciplines</span></div>
        <div><CountUp value="43" /><span>Datasets</span></div>
      </div>
    </div>
  );
}
