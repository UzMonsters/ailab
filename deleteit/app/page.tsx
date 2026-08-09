'use client'

import { useEffect, useRef, useState } from 'react'
import ScientificCoreModel from '@/components/scientific-core'
import {
  ArrowRight, Atom, Beaker, BrainCircuit, ChevronRight, CircleDot,
  Code2, Database, Dna, FlaskConical, Gauge, Globe2, Layers3, Menu,
  Microscope, Orbit, Play, Radio, Search, Sparkles, Waves, X, Zap,
} from 'lucide-react'

const sciences = [
  { icon: Atom, name: 'Chemistry', copy: 'Molecular reactions, energy states, and reaction pathways.', stat: '12.4k models' },
  { icon: Orbit, name: 'Physics', copy: 'Field systems, orbital dynamics, and material behavior.', stat: '2.8k models' },
  { icon: Dna, name: 'Biology', copy: 'Living systems from cellular scale to complete organisms.', stat: '8.1k models' },
  { icon: Layers3, name: 'Materials', copy: 'Crystal lattices, composites, and advanced surfaces.', stat: '4.6k models' },
  { icon: Microscope, name: 'Microbiology', copy: 'Microorganisms, cultures, and biological interactions.', stat: '1.9k models' },
  { icon: Waves, name: 'Electrochemistry', copy: 'Energy transfer, ions, and electrochemical systems.', stat: '3.2k models' },
]

const elements = [
  { symbol: 'H', number: 1, name: 'Hydrogen', mass: '1.008', category: 'Nonmetal', state: 'Gas', melting: '−259.2°C', boiling: '−252.9°C' },
  { symbol: 'O', number: 8, name: 'Oxygen', mass: '15.999', category: 'Nonmetal', state: 'Gas', melting: '−218.8°C', boiling: '−183.0°C' },
  { symbol: 'C', number: 6, name: 'Carbon', mass: '12.011', category: 'Nonmetal', state: 'Solid', melting: '3,550°C', boiling: '4,827°C' },
  { symbol: 'Na', number: 11, name: 'Sodium', mass: '22.990', category: 'Alkali metal', state: 'Solid', melting: '97.8°C', boiling: '883°C' },
  { symbol: 'Cl', number: 17, name: 'Chlorine', mass: '35.45', category: 'Halogen', state: 'Gas', melting: '−101.5°C', boiling: '−34.0°C' },
  { symbol: 'Au', number: 79, name: 'Gold', mass: '196.97', category: 'Transition metal', state: 'Solid', melting: '1,064°C', boiling: '2,856°C' },
]

const molecules = [
  { formula: 'DNA', name: 'Deoxyribonucleic Acid', type: 'dna', copy: 'The molecular archive of life, modeled across genetic scale.' },
  { formula: 'H₂O', name: 'Water', type: 'water', copy: 'A universal solvent with a surprisingly complex energy profile.' },
  { formula: 'C₆H₆', name: 'Benzene', type: 'benzene', copy: 'A stable aromatic ring used to study molecular resonance.' },
  { formula: 'Graphene', name: 'Carbon lattice', type: 'graphene', copy: 'A single layer of carbon with exceptional strength and conductivity.' },
]

function Reveal({ children, className = '', delay = 0, id }: { children: React.ReactNode; className?: string; delay?: number; id?: string }) {
  const ref = useRef<HTMLDivElement>(null)
  const [visible, setVisible] = useState(false)
  useEffect(() => {
    const node = ref.current
    if (!node) return
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) { setVisible(true); observer.disconnect() }
    }, { threshold: 0.12 })
    observer.observe(node)
    return () => observer.disconnect()
  }, [])
  return <div ref={ref} id={id} data-reveal className={`${className} ${visible ? 'is-visible' : ''}`} style={{ '--reveal-delay': `${delay}ms` } as React.CSSProperties}>{children}</div>
}

function Logo() {
  return <a className="logo" href="#home" aria-label="AI Laboratory home"><span className="logo-symbol"><Atom /></span><span>AI Laboratory</span></a>
}

function MoleculeArt({ type }: { type: string }) {
  if (type === 'water') return <svg viewBox="0 0 240 180" className="molecule-art" aria-label="Water molecule"><line x1="113" y1="93" x2="60" y2="45" /><line x1="113" y1="93" x2="182" y2="130" /><circle className="atom-main" cx="113" cy="93" r="31" /><circle className="atom-soft" cx="50" cy="38" r="19" /><circle className="atom-soft" cx="195" cy="138" r="19" /></svg>
  if (type === 'benzene') return <svg viewBox="0 0 260 190" className="molecule-art" aria-label="Benzene molecule"><polygon points="130,24 193,60 193,130 130,166 67,130 67,60" /><circle className="atom-soft" cx="130" cy="24" r="14" /><circle className="atom-soft" cx="193" cy="60" r="14" /><circle className="atom-soft" cx="193" cy="130" r="14" /><circle className="atom-soft" cx="130" cy="166" r="14" /><circle className="atom-soft" cx="67" cy="130" r="14" /><circle className="atom-soft" cx="67" cy="60" r="14" /></svg>
  if (type === 'graphene') return <svg viewBox="0 0 300 190" className="molecule-art" aria-label="Graphene lattice"><path d="M20 66 55 44 90 66 90 108 55 130 20 108ZM90 66l35-22 35 22v42l-35 22-35-22ZM160 66l35-22 35 22v42l-35 22-35-22ZM230 66l35-22 35 22v42l-35 22-35-22" /><path d="m55 44 35 22m70 0 35-22m70 0 35-22M55 130l35-22m70 0 35 22m70 0 35-22" /></svg>
  return <svg viewBox="0 0 380 200" className="molecule-art" aria-label="DNA double helix"><path d="M20 20 C120 170 245 20 360 160" /><path d="M20 160 C120 10 245 170 360 20" />{[35,75,115,155,195,235,275,315].map((x, i) => <line key={x} x1={x} y1={i % 2 ? 31 : 149} x2={x + 34} y2={i % 2 ? 149 : 31} />)}</svg>
}

function Orbital({ selected }: { selected: typeof elements[number] }) {
  return <svg key={selected.symbol} className="orbital" viewBox="0 0 260 230" aria-label={`${selected.name} orbital model`}><defs><radialGradient id="nucleus"><stop stopColor="var(--primary-bright)" /><stop offset="1" stopColor="var(--primary)" stopOpacity="0" /></radialGradient></defs><ellipse cx="130" cy="115" rx="105" ry="38" /><ellipse cx="130" cy="115" rx="105" ry="38" transform="rotate(60 130 115)" /><ellipse cx="130" cy="115" rx="105" ry="38" transform="rotate(-60 130 115)" /><circle className="orbital-nucleus" cx="130" cy="115" r="42" fill="url(#nucleus)" /><circle className="orbital-core" cx="130" cy="115" r="18" /><circle className="electron electron-a" r="6" cx="25" cy="115"><animateMotion dur="4.8s" repeatCount="indefinite"><mpath href="#orbit-path" /></animateMotion></circle><path id="orbit-path" d="M25 115 A105 38 0 1 0 235 115 A105 38 0 1 0 25 115" fill="none" /></svg>
}

export default function Page() {
  const [mobileOpen, setMobileOpen] = useState(false)
  const [selectedSymbol, setSelectedSymbol] = useState('O')
  const [assistantInput, setAssistantInput] = useState('')
  const [assistantReply, setAssistantReply] = useState('Water is polar because oxygen attracts electrons more strongly than hydrogen, creating partial charges across the molecule.')
  const [thinking, setThinking] = useState(false)
  const [simulationRunning, setSimulationRunning] = useState(false)
  const selected = elements.find((element) => element.symbol === selectedSymbol) ?? elements[1]
  const askAssistant = () => { const prompt = assistantInput.trim(); if (!prompt || thinking) return; setThinking(true); setAssistantInput(''); window.setTimeout(() => { setAssistantReply(`I mapped ${prompt} across structure, energy, and reaction pathways. The highest-confidence model is ready to inspect.`); setThinking(false) }, 850) }
  return <main className="site-shell" id="home">
    <header className="site-nav"><div className="nav-inner"><Logo /><nav className={mobileOpen ? 'nav-links nav-open' : 'nav-links'} aria-label="Main navigation">{['Platform', 'Sciences', 'Molecules', 'Research', 'Developers'].map((item) => <a key={item} href={`#${item.toLowerCase()}`} onClick={() => setMobileOpen(false)}>{item}</a>)}</nav><div className="nav-actions"><button className="icon-button" aria-label="Search"><Search /></button><a className="text-button login-button" href="#workspace">Sign in</a><a className="button button-primary nav-cta" href="#workspace">Open workspace <ArrowRight /></a><button className="mobile-toggle icon-button" aria-label={mobileOpen ? 'Close menu' : 'Open menu'} onClick={() => setMobileOpen(!mobileOpen)}>{mobileOpen ? <X /> : <Menu />}</button></div></div></header>

    <section className="hero section-wrap" id="platform"><Reveal className="hero-copy"><p className="eyebrow"><Sparkles /> The scientific operating system</p><h1>Experiment.<br />Simulate.<br /><span>Discover.</span></h1><p className="hero-description">AI Laboratory gives scientists, students, and engineers a living environment for building models, running simulations, and exploring what comes next.</p><div className="hero-actions"><a className="button button-primary" href="#workspace">Open workspace <ArrowRight /></a><a className="button button-secondary" href="#research">View research</a></div><div className="hero-highlights"><span><Gauge /> Real-time simulation</span><span><BrainCircuit /> AI copilot</span><span><Database /> Scientific database</span><span><Globe2 /> Cloud workspace</span></div></Reveal><ScientificCoreModel size={620} accentColor="#7c3aed" /></section>

    <section className="section-wrap section-block" id="sciences"><Reveal className="section-heading"><div><p className="eyebrow">One system, many disciplines</p><h2>Scientific work without the seams.</h2></div><p>From molecular structures to complex fields, every model lives in one coherent environment.</p></Reveal><Reveal className="science-grid reveal-stagger">{sciences.map(({ icon: Icon, name, copy, stat }) => <a className="science-card" href="#workspace" key={name}><span className="science-icon"><Icon /></span><div><h3>{name}</h3><p>{copy}</p><small>{stat}</small></div><ChevronRight className="card-arrow" /></a>)}</Reveal></section>

    <section className="section-wrap section-block" id="molecules"><Reveal className="section-heading"><div><p className="eyebrow">The molecular library</p><h2>Build from the smallest scale.</h2></div><a className="inline-link" href="#explorer">Explore library <ArrowRight /></a></Reveal><Reveal className="molecule-grid reveal-stagger">{molecules.map((molecule, index) => <article className={`molecule-card molecule-${molecule.type}`} key={molecule.formula}><div className="molecule-copy"><span className="molecule-index">0{index + 1}</span><h3>{molecule.formula}</h3><p className="molecule-name">{molecule.name}</p><p>{molecule.copy}</p><a href="#explorer">Inspect model <ArrowRight /></a></div><MoleculeArt type={molecule.type} /></article>)}</Reveal></section>

    <section className="section-wrap section-block workspace-grid" id="workspace"><Reveal className="tool-panel periodic-panel"><div className="tool-heading"><div><p className="eyebrow">Element explorer</p><h2>See the field behind every element.</h2><p>Select an element to inspect its properties and orbital model.</p></div><Atom /></div><div className="periodic-content"><div className="element-grid">{elements.map((element) => <button key={element.symbol} className={selectedSymbol === element.symbol ? 'element-cell selected' : 'element-cell'} onClick={() => setSelectedSymbol(element.symbol)}><strong>{element.symbol}</strong><span>{element.number}</span><small>{element.name}</small></button>)}</div><div className="element-details"><div className="detail-title"><div><h3>{selected.name}</h3><p>{selected.category} · {selected.state}</p></div><strong>{selected.number}</strong></div><dl><div><dt>Atomic mass</dt><dd>{selected.mass}</dd></div><div><dt>Electron configuration</dt><dd>1s² 2s² 2p⁶</dd></div><div><dt>Melting point</dt><dd>{selected.melting}</dd></div><div><dt>Boiling point</dt><dd>{selected.boiling}</dd></div></dl><Orbital selected={selected} /></div></div><button className="discover-link" onClick={() => setSelectedSymbol(elements[(elements.findIndex((item) => item.symbol === selectedSymbol) + 1) % elements.length].symbol)}>Discover next element <ArrowRight /></button></Reveal>
      <Reveal className="tool-panel assistant-panel"><div className="tool-heading"><div><p className="eyebrow">Research copilot</p><h2>Ask better questions.</h2><p>Turn a hypothesis into a model with a little help from AI.</p></div><BrainCircuit /></div><div className="chat-window"><div className="user-bubble">Why is water polar?</div><div className={`assistant-bubble ${thinking ? 'is-thinking' : 'reply-enter'}`}><Sparkles />{thinking ? <span className="typing-dots" aria-label="AI is thinking"><i /><i /><i /></span> : <p key={assistantReply}>{assistantReply}</p>}</div><div className="citation"><span>Source trace</span><a href="#research">Molecular polarity / 04 papers <ArrowRight /></a></div><div className="prompt-chips"><button onClick={() => setAssistantInput('Explain molecular polarity')}>Explain</button><button onClick={() => setAssistantInput('Analyze this reaction')}>Analyze</button><button onClick={() => setAssistantInput('Predict a reaction')}>Predict</button></div><div className="prompt-row"><input aria-label="Ask the AI chemistry assistant" placeholder="Ask anything..." value={assistantInput} onChange={(event) => setAssistantInput(event.target.value)} onKeyDown={(event) => { if (event.key === 'Enter' && !event.nativeEvent.isComposing && event.keyCode !== 229) askAssistant() }} /><button className="button button-primary prompt-submit" aria-label="Send prompt" onClick={askAssistant}><ArrowRight /></button></div></div></Reveal></section>

    <section className="section-wrap section-block simulation-section" id="research"><Reveal className="section-heading"><div><p className="eyebrow">Live workspace</p><h2>From idea to evidence.</h2></div><p>Every run is observable, reproducible, and ready to share with your team.</p></Reveal><Reveal className="simulation-grid reveal-stagger"><article className="simulation-card simulation-feature"><div className="simulation-meta"><span className="status-pill"><i /> running now</span><span>SIM-0429</span></div><h3>Protein folding / energy landscape</h3><p>A multi-stage simulation exploring stable conformations across 42,100 iterations.</p><div className="chart"><span /><span /><span /><span /><span /><span /><span /><span /><span /></div><div className="simulation-footer"><span>87.4% complete</span><strong>01:42:08 remaining</strong></div></article><article className="simulation-card"><Radio /><span className="card-label">Recent discovery</span><h3>New stable state detected</h3><p>Model #8821 found a lower-energy configuration under constrained conditions.</p><a href="#workspace">Inspect finding <ArrowRight /></a></article><article className="simulation-card"><Play /><span className="card-label">Popular experiment</span><h3>Reaction pathway builder</h3><p>Generate, compare, and share reaction mechanisms with your team.</p><button className="button button-secondary" onClick={() => setSimulationRunning(!simulationRunning)}>{simulationRunning ? 'Simulation running' : 'Run experiment'} <ArrowRight /></button></article></Reveal></section>

    <section className="section-wrap section-block architecture-section" id="developers"><Reveal className="architecture-copy"><p className="eyebrow">Platform architecture</p><h2>One layer for every scientific workflow.</h2><p>Use the interface for exploration, the API for scale, and the database for a shared source of truth.</p><a className="button button-secondary" href="#home">Read the API docs <ArrowRight /></a></Reveal><Reveal className="architecture-map"><div className="arch-node arch-top"><Sparkles /><span>AI models</span><small>Reasoning layer</small></div><div className="arch-line line-a" /><div className="arch-line line-b" /><div className="arch-node arch-left"><Code2 /><span>Workspace</span><small>Build & simulate</small></div><div className="arch-node arch-center"><Atom /><span>Scientific core</span><small>Shared state</small></div><div className="arch-node arch-right"><Database /><span>Knowledge base</span><small>Versioned data</small></div><div className="arch-node arch-bottom"><Globe2 /><span>Cloud runtime</span><small>Scale anywhere</small></div></Reveal></section>

    <section className="section-wrap section-block stats-section"><Reveal className="section-heading"><div><p className="eyebrow">The laboratory, live</p><h2>Built for the next result.</h2></div></Reveal><Reveal className="stats-grid reveal-stagger">{([['118', 'Elements indexed', Atom], ['12,458', 'Molecular models', FlaskConical], ['4,231', 'Reaction pathways', Sparkles], ['99.98%', 'Model accuracy', Gauge], ['42', 'Active simulations', Radio], ['1.2m', 'Data points today', Database]] as const).map(([value, label, Icon]) => <div className="stat-card" key={label}><Icon /><strong>{value}</strong><span>{label}</span><i className="stat-spark" /></div>)}</Reveal></section>

    <footer className="site-footer"><div className="footer-grid section-wrap"><div className="footer-brand"><Logo /><p>The operating system for scientific simulation. Build, understand, and share what comes next.</p><div className="social-row"><button aria-label="Github">GH</button><button aria-label="Discord">DS</button><button aria-label="LinkedIn">in</button></div></div><div><h3>Platform</h3><a href="#workspace">Workspace</a><a href="#sciences">Sciences</a><a href="#molecules">Molecules</a><a href="#research">Research</a></div><div><h3>Resources</h3><a href="#home">Documentation</a><a href="#home">API reference</a><a href="#home">Tutorials</a><a href="#home">Community</a></div><div><h3>Company</h3><a href="#home">About</a><a href="#home">Careers</a><a href="#home">Contact</a><a href="#home">Privacy</a></div><div className="footer-newsletter"><h3>Stay in the field</h3><p>New models, discoveries, and platform notes — once a month.</p><div className="newsletter-row"><input aria-label="Email address" placeholder="Email address" /><button className="button button-primary" aria-label="Subscribe"><ArrowRight /></button></div></div></div><div className="copyright section-wrap">© 2026 AI Laboratory. All systems operational.</div></footer>
  </main>
}
