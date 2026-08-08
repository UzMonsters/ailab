'use client';
import { useEffect, useRef, useState } from 'react';
import { ArrowRight, Gauge, BrainCircuit, Database, Globe2, Sparkles } from 'lucide-react';
import ScientificCoreModel from '@/components/scientific-core';

function Reveal({ children, className = '', delay = 0 }: { children: React.ReactNode; className?: string; delay?: number }) {
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
  return <div ref={ref} data-reveal className={`${className} ${visible ? 'is-visible' : ''}`} style={{ '--reveal-delay': `${delay}ms` } as React.CSSProperties}>{children}</div>;
}

export default function HeroSection() {
  return (
    <section className="hero" id="platform">
      <Reveal className="hero-copy">
        <p className="eyebrow"><Sparkles /> The scientific operating system</p>
        <h1>
          <span className="hero-line">Experiment.</span>
          <span className="hero-line">Simulate.</span>
          <span className="hero-line hero-line-bright">Discover.</span>
        </h1>
        <p className="hero-description">AI Laboratory gives scientists, students, and engineers a living environment for building models, running simulations, and exploring what comes next.</p>
        <div className="hero-actions">
          <a className="button button-primary" href="#workspace">Open workspace <ArrowRight /></a>
          <a className="button button-secondary" href="#sciences">View sciences</a>
        </div>
        <div className="hero-highlights">
          <span><Gauge /> Real-time simulation</span>
          <span><BrainCircuit /> AI copilot</span>
          <span><Database /> Scientific database</span>
          <span><Globe2 /> Cloud workspace</span>
        </div>
      </Reveal>
      <div className="hero-core">
        <ScientificCoreModel size={700} accentColor="#8b5cf6" />
      </div>
    </section>
  );
}
