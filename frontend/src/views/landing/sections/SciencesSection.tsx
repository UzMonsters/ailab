'use client';
import { useRef, useState, useEffect } from 'react';
import type { CSSProperties } from 'react';
import { Atom, Orbit, Dna, Layers3, Microscope, Waves, ChevronRight } from 'lucide-react';

const sciences = [
  { icon: Atom, name: 'Chemistry', copy: 'Molecular reactions, energy states, and reaction pathways.', accent: '#8b5cf6', formula: 'O₂', meta: [['Models', '12.4K'], ['Difficulty', 'Advanced'], ['Updated', 'Today']], image: '/chemistry.png' },
  { icon: Orbit, name: 'Physics', copy: 'Field systems, orbital dynamics, and material behavior.', accent: '#3b82f6', formula: 'E = mc²', meta: [['Models', '2.8K'], ['Difficulty', 'Expert'], ['Updated', 'Today']], image: '/physics.png' },
  { icon: Dna, name: 'Biology', copy: 'Living systems from cellular scale to complete organisms.', accent: '#10b981', formula: 'DNA', meta: [['Models', '8.1K'], ['Difficulty', 'Intermediate'], ['Updated', 'Today']], image: '/dnk.png' },
  { icon: Layers3, name: 'Materials', copy: 'Crystal lattices, composites, and advanced surfaces.', accent: '#2dd4bf', formula: 'C₆H₆', meta: [['Models', '4.6K'], ['Difficulty', 'Advanced'], ['Updated', 'Today']], image: '/atom.png' },
  { icon: Microscope, name: 'Microbiology', copy: 'Microorganisms, cultures, and biological interactions.', accent: '#f59e0b', formula: 'E. coli', meta: [['Models', '1.9K'], ['Difficulty', 'Intermediate'], ['Updated', 'Today']] },
  { icon: Waves, name: 'Electrochemistry', copy: 'Energy transfer, ions, and electrochemical systems.', accent: '#c084fc', formula: 'Li⁺', meta: [['Models', '3.2K'], ['Difficulty', 'Advanced'], ['Updated', 'Today']] },
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

function SectionDecor() {
  return (
    <div className="section-decor section-decor-sciences" aria-hidden="true">
      <img className="decor-img decor-blueprint" src="/decor-blueprint.png" alt="" loading="lazy" />
      <img className="decor-img decor-atom" src="/decor-atom.png" alt="" loading="lazy" />
      <img className="decor-img decor-molecule" src="/decor-molecule.png" alt="" loading="lazy" />
      <span className="decor-icon decor-microscope"><Microscope /></span>
      <span className="decor-icon decor-flask">🧪</span>
    </div>
  );
}

export default function SciencesSection() {
  return (
    <section className="section-wrap section-block sciences-section" id="sciences">
      <SectionDecor />
      <Reveal className="section-heading">
        <div>
          <p className="eyebrow">One system, many disciplines</p>
          <h2>Scientific work without the seams.</h2>
        </div>
        <p>From molecular structures to complex fields, every model lives in one coherent environment.</p>
      </Reveal>
      <Reveal className="science-grid reveal-stagger">
        {sciences.map(({ icon: Icon, name, copy, accent, formula, meta, image }) => (
          <a className="science-card" href="#workspace" key={name} style={{ '--card-accent': accent } as CSSProperties}>
            <span className="science-formula">{formula}</span>
            <span className="science-icon">{image ? <img src={image} alt="" /> : <Icon />}</span>
            <div className="science-copy">
              <h3>{name}</h3>
              <p>{copy}</p>
              <dl className="science-meta">
                {meta.map(([key, value]) => (
                  <div key={key}><dt>{key}</dt><dd>{value}</dd></div>
                ))}
              </dl>
            </div>
            <ChevronRight className="card-arrow" />
          </a>
        ))}
      </Reveal>
    </section>
  );
}
