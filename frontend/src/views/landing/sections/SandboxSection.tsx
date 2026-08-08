'use client';
import { useRef, useState, useEffect } from 'react';
import { ArrowRight } from 'lucide-react';
import ScientificCoreModel from '@/components/scientific-core';

const molecules = [
  { formula: 'DNA', name: 'Deoxyribonucleic Acid', type: 'dna', copy: 'The molecular archive of life, modeled across genetic scale.' },
  { formula: 'H₂O', name: 'Water', type: 'water', copy: 'A universal solvent with a surprisingly complex energy profile.' },
  { formula: 'C₆H₆', name: 'Benzene', type: 'benzene', copy: 'A stable aromatic ring used to study molecular resonance.' },
  { formula: 'Graphene', name: 'Carbon lattice', type: 'graphene', copy: 'A single layer of carbon with exceptional strength and conductivity.' },
];

const elements = [
  { symbol: 'H', number: 1, name: 'Hydrogen', mass: '1.008', config: '1s¹', category: 'Nonmetal', state: 'Gas', melting: '−259.2°C', boiling: '−252.9°C' },
  { symbol: 'O', number: 8, name: 'Oxygen', mass: '15.999', config: '1s² 2s² 2p⁴', category: 'Nonmetal', state: 'Gas', melting: '−218.8°C', boiling: '−183.0°C' },
  { symbol: 'C', number: 6, name: 'Carbon', mass: '12.011', config: '1s² 2s² 2p²', category: 'Nonmetal', state: 'Solid', melting: '3,550°C', boiling: '4,827°C' },
  { symbol: 'Na', number: 11, name: 'Sodium', mass: '22.990', config: '1s² 2s² 2p⁶ 3s¹', category: 'Alkali metal', state: 'Solid', melting: '97.8°C', boiling: '883°C' },
  { symbol: 'Cl', number: 17, name: 'Chlorine', mass: '35.45', config: '1s² 2s² 2p⁶ 3s² 3p⁵', category: 'Halogen', state: 'Gas', melting: '−101.5°C', boiling: '−34.0°C' },
  { symbol: 'Au', number: 79, name: 'Gold', mass: '196.97', config: '[Xe] 4f¹⁴ 5d¹⁰ 6s¹', category: 'Transition metal', state: 'Solid', melting: '1,064°C', boiling: '2,856°C' },
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

function MoleculeArt({ type }: { type: string }) {
  const src = type === 'dna' ? '/dna.png' : type === 'water' ? '/molecule.png' : type === 'benzene' ? '/chemistry.png' : '/molecular_network.png';
  return <img className="molecule-art" src={src} alt={`${type} model`} />;
}

function SectionDecor({ variant }: { variant: 'molecules' | 'workspace' }) {
  return (
    <div className={`section-decor section-decor-${variant}`} aria-hidden="true">
      <img className="decor-img decor-blueprint" src="/decor-blueprint.png" alt="" loading="lazy" />
      <img className="decor-img decor-atom" src="/decor-atom.png" alt="" loading="lazy" />
      <img className="decor-img decor-molecule" src="/decor-molecule.png" alt="" loading="lazy" />
    </div>
  );
}

export default function SandboxSection() {
  const [selectedSymbol, setSelectedSymbol] = useState('O');
  const selected = elements.find((e) => e.symbol === selectedSymbol) ?? elements[1];

  return (
    <>
      <section className="section-wrap section-block" id="molecules">
        <SectionDecor variant="molecules" />
        <Reveal className="section-heading">
          <div>
            <p className="eyebrow">The molecular library</p>
            <h2>Build from the smallest scale.</h2>
          </div>
          <a className="inline-link" href="#workspace">Explore library <ArrowRight /></a>
        </Reveal>
        <Reveal className="molecule-grid reveal-stagger">
          {molecules.map((molecule, index) => (
            <article className={`molecule-card molecule-${molecule.type}`} key={molecule.formula}>
              <div className="molecule-copy">
                <span className="molecule-index">0{index + 1}</span>
                <h3>{molecule.formula}</h3>
                <p className="molecule-name">{molecule.name}</p>
                <p>{molecule.copy}</p>
                <a href="#workspace">Inspect model <ArrowRight /></a>
              </div>
              <MoleculeArt type={molecule.type} />
            </article>
          ))}
        </Reveal>
      </section>

      <section className="section-wrap section-block workspace-grid" id="workspace">
        <SectionDecor variant="workspace" />
        <Reveal className="tool-panel periodic-panel reveal-left">
          <div className="tool-heading">
            <div>
              <p className="eyebrow">Element explorer</p>
              <h2>See the field behind every element.</h2>
              <p>Select an element to inspect its properties and orbital model.</p>
            </div>
            <span className="tool-icon">⚛</span>
          </div>
          <div className="periodic-content">
            <div className="element-grid">
              {elements.map((element) => (
                <button key={element.symbol} className={selectedSymbol === element.symbol ? 'element-cell selected' : 'element-cell'} onClick={() => setSelectedSymbol(element.symbol)}>
                  <strong>{element.symbol}</strong>
                  <span>{element.number}</span>
                  <small>{element.name}</small>
                </button>
              ))}
            </div>
            <div className="element-details">
              <div className="detail-title">
                <div>
                  <h3>{selected.name}</h3>
                  <p>{selected.category} · {selected.state}</p>
                </div>
                <strong>{selected.number}</strong>
              </div>
              <div className="element-body">
                <dl>
                  <div><dt>Atomic mass</dt><dd>{selected.mass}</dd></div>
                  <div><dt>Electron configuration</dt><dd>{selected.config}</dd></div>
                  <div><dt>Melting point</dt><dd>{selected.melting}</dd></div>
                  <div><dt>Boiling point</dt><dd>{selected.boiling}</dd></div>
                </dl>
                <div className="element-orbit">
                  <ScientificCoreModel size={360} accentColor="#8b5cf6" electrons={selected.number} />
                </div>
              </div>
            </div>
          </div>
          <button className="discover-link" onClick={() => setSelectedSymbol(elements[(elements.findIndex((e) => e.symbol === selectedSymbol) + 1) % elements.length].symbol)}>
            Discover next element <ArrowRight />
          </button>
        </Reveal>
      </section>
    </>
  );
}
