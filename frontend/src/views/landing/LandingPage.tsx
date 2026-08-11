'use client'

import { useEffect, useRef, useState } from 'react'
import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { useTranslations } from 'next-intl'
import ScientificCoreModel from '@/components/scientific-core'
import { useAuthStore } from '@/stores/auth.store'
import { useLocaleSwitch } from '@/hooks/useLocaleSwitch'
import { LOCALES } from '@/constants'
import {
  ArrowRight, Atom, Beaker, BrainCircuit, ChevronRight,
  Database, Dna, Droplets, FlaskConical, Gauge, Globe2, Hexagon, Layers3, Menu,
  Microscope, Network, Orbit, Radar, Send, Sparkles, Waves, X,
} from 'lucide-react'

const scienceIcons = [
  Atom, Orbit, Dna, Layers3, Microscope, Waves,
]

const scienceKeys = [
  { nameKey: 'scienceChemName', copyKey: 'scienceChemCopy', statKey: 'scienceChemStat', image: '/chemistry.png' },
  { nameKey: 'sciencePhysName', copyKey: 'sciencePhysCopy', statKey: 'sciencePhysStat', image: '/physics.png' },
  { nameKey: 'scienceBioName', copyKey: 'scienceBioCopy', statKey: 'scienceBioStat', image: '/dnk.png' },
  { nameKey: 'scienceMatName', copyKey: 'scienceMatCopy', statKey: 'scienceMatStat', image: '/atom.png' },
  { nameKey: 'scienceMicroName', copyKey: 'scienceMicroCopy', statKey: 'scienceMicroStat', image: '/mol-graphene.png' },
  { nameKey: 'scienceElectroName', copyKey: 'scienceElectroCopy', statKey: 'scienceElectroStat', image: '/mol-water.png' },
]

const elements = [
  { symbol: 'H', number: 1, nameKey: 'elementNameH', categoryKey: 'elementCatNonmetal', stateKey: 'elementStateGas', mass: '1.008', config: '1s\u00B9', melting: '\u2212259.2\u00B0C', boiling: '\u2212252.9\u00B0C' },
  { symbol: 'O', number: 8, nameKey: 'elementNameO', categoryKey: 'elementCatNonmetal', stateKey: 'elementStateGas', mass: '15.999', config: '1s\u00B2 2s\u00B2 2p\u2074', melting: '\u2212218.8\u00B0C', boiling: '\u2212183.0\u00B0C' },
  { symbol: 'C', number: 6, nameKey: 'elementNameC', categoryKey: 'elementCatNonmetal', stateKey: 'elementStateSolid', mass: '12.011', config: '1s\u00B2 2s\u00B2 2p\u00B2', melting: '3,550\u00B0C', boiling: '4,827\u00B0C' },
  { symbol: 'Na', number: 11, nameKey: 'elementNameNa', categoryKey: 'elementCatAlkali', stateKey: 'elementStateSolid', mass: '22.990', config: '1s\u00B2 2s\u00B2 2p\u2076 3s\u00B9', melting: '97.8\u00B0C', boiling: '883\u00B0C' },
  { symbol: 'Cl', number: 17, nameKey: 'elementNameCl', categoryKey: 'elementCatHalogen', stateKey: 'elementStateGas', mass: '35.45', config: '1s\u00B2 2s\u00B2 2p\u2076 3s\u00B2 3p\u2075', melting: '\u2212101.5\u00B0C', boiling: '\u221234.0\u00B0C' },
  { symbol: 'Au', number: 79, nameKey: 'elementNameAu', categoryKey: 'elementCatTransition', stateKey: 'elementStateSolid', mass: '196.97', config: '[Xe] 4f\u00B9\u2074 5d\u00B9\u2070 6s\u00B9', melting: '1,064\u00B0C', boiling: '2,856\u00B0C' },
]

const moleculeKeys = [
  { formula: 'DNA', type: 'dna', nameKey: 'moleculeDnaName', copyKey: 'moleculeDnaCopy', image: '/dna.png' },
  { formula: 'H\u2082O', type: 'water', nameKey: 'moleculeWaterName', copyKey: 'moleculeWaterCopy', image: '/molecule.png' },
  { formula: 'C\u2086H\u2086', type: 'benzene', nameKey: 'moleculeBenzeneName', copyKey: 'moleculeBenzeneCopy', image: '/atom.png' },
  { formula: 'Graphene', type: 'graphene', nameKey: 'moleculeGrapheneName', copyKey: 'moleculeGrapheneCopy', image: '/molecular_network.png' },
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
  return <Link className="logo" href="#home" aria-label="jasScience home"><span className="logo-symbol"><Atom /></span><span>jas<span className="text-[#8b5cf6]">Science</span></span></Link>
}

function SectionDecor({ icons }: { icons: { Icon: typeof Atom; className: string; color: string; ring?: boolean }[] }) {
  return (
    <div className="section-decor section-decor-icons" aria-hidden="true">
      {icons.map(({ Icon, className, color, ring = true }, i) => (
        <span key={i} className={`decor-edge-icon ${ring ? 'ed-ring' : 'ed-soft'} ${className}`} style={{ color }}>
          <Icon />
        </span>
      ))}
    </div>
  )
}

function ElementRingViz() {
  return (
    <div className="stat-viz">
      <svg className="viz-svg" viewBox="0 0 70 52">
        <defs>
          <linearGradient id="ringGrad" x1="0" y1="0" x2="1" y2="1">
            <stop offset="0%" stopColor="#8b5cf6" />
            <stop offset="100%" stopColor="#22d3ee" />
          </linearGradient>
        </defs>
        <circle cx="27" cy="26" r="19" fill="none" stroke="rgba(255,255,255,0.1)" strokeWidth="4" />
        <circle className="viz-ring" cx="27" cy="26" r="19" fill="none" stroke="url(#ringGrad)" strokeWidth="4" strokeLinecap="round" strokeDasharray="119.4" strokeDashoffset="0" transform="rotate(-90 27 26)" />
        <text x="27" y="31" textAnchor="middle" fill="#e9d5ff" fontSize="14" fontWeight="700" fontFamily="var(--font-mono)">118</text>
      </svg>
      <div className="viz-grid" style={{ gridTemplateColumns: 'repeat(5, 1fr)' }}>
        {Array.from({ length: 15 }).map((_, i) => (
          <span key={i} className={`viz-cell ${[0, 4, 7, 11, 14].includes(i) ? 'hot' : ''}`} style={{ background: [0, 4, 7, 11, 14].includes(i) ? (i % 2 ? '#22d3ee' : '#8b5cf6') : 'rgba(255,255,255,0.06)' }} />
        ))}
      </div>
    </div>
  )
}

function MoleculeViz() {
  return (
    <svg className="viz-svg" viewBox="0 0 140 52" fill="none">
      <line x1="20" y1="26" x2="55" y2="15" stroke="rgba(139,92,246,0.35)" strokeWidth="1.2" />
      <line x1="55" y1="15" x2="90" y2="28" stroke="rgba(96,165,250,0.35)" strokeWidth="1.2" />
      <line x1="20" y1="26" x2="48" y2="42" stroke="rgba(139,92,246,0.35)" strokeWidth="1.2" />
      <line x1="48" y1="42" x2="92" y2="34" stroke="rgba(96,165,250,0.35)" strokeWidth="1.2" />
      <line x1="90" y1="28" x2="120" y2="12" stroke="rgba(139,92,246,0.35)" strokeWidth="1.2" />
      <circle className="viz-node" cx="20" cy="26" r="5" fill="#8b5cf6" />
      <circle className="viz-node" cx="55" cy="15" r="4" fill="#60a5fa" />
      <circle className="viz-node" cx="90" cy="28" r="6" fill="#a78bfa" />
      <circle className="viz-node" cx="48" cy="42" r="3.5" fill="#60a5fa" />
      <circle className="viz-node" cx="92" cy="34" r="4" fill="#8b5cf6" />
      <circle className="viz-node" cx="120" cy="12" r="3" fill="#a78bfa" />
    </svg>
  )
}

function PathwayViz() {
  return (
    <svg className="viz-svg" viewBox="0 0 140 52" fill="none">
      <path d="M10 26 H30 M30 26 L52 12 M30 26 L52 26 M30 26 L52 40" stroke="rgba(52,211,153,0.5)" strokeWidth="1.3" />
      <path d="M52 12 L74 8 M52 12 L74 18" stroke="rgba(34,211,238,0.5)" strokeWidth="1.3" />
      <path d="M52 26 L76 24 M52 26 L76 30" stroke="rgba(52,211,153,0.5)" strokeWidth="1.3" />
      <path d="M52 40 L76 44 M52 40 L76 38" stroke="rgba(34,211,238,0.5)" strokeWidth="1.3" />
      <circle className="viz-node" cx="10" cy="26" r="4" fill="#34d399" />
      <circle className="viz-node" cx="30" cy="26" r="3" fill="#34d399" />
      <circle className="viz-node" cx="52" cy="12" r="3.5" fill="#34d399" />
      <circle className="viz-node" cx="52" cy="26" r="3.5" fill="#22d3ee" />
      <circle className="viz-node" cx="52" cy="40" r="3.5" fill="#34d399" />
      <circle className="viz-node" cx="74" cy="8" r="3" fill="#22d3ee" />
      <circle className="viz-node" cx="74" cy="18" r="3" fill="#34d399" />
      <circle className="viz-node" cx="76" cy="24" r="3" fill="#34d399" />
      <circle className="viz-node" cx="76" cy="30" r="3" fill="#22d3ee" />
      <circle className="viz-node" cx="76" cy="44" r="3" fill="#22d3ee" />
      <circle className="viz-node" cx="76" cy="38" r="3" fill="#34d399" />
    </svg>
  )
}

function InstrumentViz() {
  return (
    <div className="stat-tokens">
      <span className="viz-token stat-token"><FlaskConical size={14} /></span>
      <span className="viz-token stat-token"><Microscope size={14} /></span>
      <span className="viz-token stat-token"><Gauge size={14} /></span>
      <span className="viz-token stat-token"><Beaker size={14} /></span>
    </div>
  )
}

export default function LandingPage() {
  const pathname = usePathname()
  const t = useTranslations('landing')
  const locale = pathname.split('/')[1] || 'en'
  const { user, fetchUser } = useAuthStore()
  const { switchLocale } = useLocaleSwitch()
  const [mobileOpen, setMobileOpen] = useState(false)
  const [selectedSymbol, setSelectedSymbol] = useState('O')
  const [assistantInput, setAssistantInput] = useState('')
  const [assistantReply, setAssistantReply] = useState<string>('')
  const [thinking, setThinking] = useState(false)
  useEffect(() => {
    if (!user) void fetchUser()
  }, [fetchUser, user])
  const selected = elements.find((element) => element.symbol === selectedSymbol) ?? elements[1]
  const askAssistant = () => { const prompt = assistantInput.trim(); if (!prompt || thinking) return; setThinking(true); setAssistantInput(''); window.setTimeout(() => { setAssistantReply(`${t('copilotReplyPrefix')} ${prompt}. ${t('copilotReplySuffix')}`); setThinking(false) }, 850) }
  const defaultReply = `${t('copilotReplyPrefix')} ${t('copilotUserQuestion')}. ${t('copilotReplySuffix')}`

  const sciences = scienceKeys.map((s, i) => ({ icon: scienceIcons[i], ...s }))
  const molecules = moleculeKeys
  const stats = [
    { value: '118/118', badge: t('statComplete'), label: t('statElements'), accent: '#8b5cf6', accent2: '#22d3ee', viz: <ElementRingViz /> },
    { value: '12,458', label: t('statModels'), accent: '#60a5fa', accent2: '#a78bfa', viz: <MoleculeViz /> },
    { value: '4,231', label: t('statPathways'), accent: '#34d399', accent2: '#22d3ee', viz: <PathwayViz /> },
    { value: '24', label: t('statInstruments'), accent: '#f59e0b', accent2: '#a78bfa', viz: <InstrumentViz /> },
  ]

  return <main className="site-shell" id="home">
    <header className="site-nav"><div className="nav-inner"><Logo /><nav className={mobileOpen ? 'nav-links nav-open' : 'nav-links'} aria-label="Main navigation">{[{ label: t('navPlatform'), href: 'platform' }, { label: t('navSciences'), href: 'sciences' }, { label: t('navMolecules'), href: 'molecules' }, { label: t('navResearch'), href: 'research' }].map((item) => <a key={item.label} href={`#${item.href}`} onClick={() => setMobileOpen(false)}>{item.label}</a>)}</nav><div className="nav-actions"><div className="nav-langs" role="group" aria-label="Language">{LOCALES.map((code) => <button key={code} type="button" onClick={() => switchLocale(code)} className={`nav-lang ${locale === code ? 'active' : ''}`} aria-current={locale === code}>{code}</button>)}</div><Link className="button button-primary nav-cta" href={user ? `/${locale}/workspace/sandbox` : `/${locale}/auth`}>{user ? t('navWorkspace') : t('navSignIn')} <ArrowRight /></Link><button className="mobile-toggle icon-button" aria-label={mobileOpen ? t('navClose') : t('navOpen')} onClick={() => setMobileOpen(!mobileOpen)}>{mobileOpen ? <X /> : <Menu />}</button></div></div></header>

    <section className="hero section-wrap" id="platform" style={{ backgroundImage: "radial-gradient(ellipse 48% 70% at 8% 52%, rgba(124,58,237,.34), #000000a6 72%), linear-gradient(90deg, rgba(6,8,12,.96) 0%, rgba(6,8,12,.78) 45%, rgba(6,8,12,.22) 100%), url('/background_herosection.jpg') right center / cover no-repeat, #06080c" }}>
      <Reveal className="hero-copy">
        <p className="eyebrow"><Sparkles /> {t('heroEyebrow')}</p>
        <h1><span className="hero-line hero-line-bright">{t('heroLine1')}</span><span className="hero-line">{t('heroLine2')}</span><span className="hero-line">{t('heroLine3')}</span></h1>
        <p className="hero-description">{t('heroDesc')}</p>
        <div className="hero-actions">
          <Link className="button button-primary" href={`/${locale}/auth`}>{t('heroCta')} <ArrowRight /></Link>
          <a className="button button-secondary" href="#sciences">{t('heroCtaSecondary')}</a>
        </div>
        <div className="hero-highlights">
          <span><Gauge /> {t('heroHighlight1')}</span>
          <span><BrainCircuit /> {t('heroHighlight2')}</span>
          <span><Database /> {t('heroHighlight3')}</span>
          <span><Globe2 /> {t('heroHighlight4')}</span>
        </div>
      </Reveal>
      <div className="hero-core">
        <ScientificCoreModel size={800} accentColor="#7c3aed" />
      </div>
    </section>

    <section className="section-wrap section-block" id="sciences">
      <div className="section-decor" aria-hidden="true" style={{ position: 'absolute', top: 0, bottom: 0, left: '50%', width: '100vw', transform: 'translateX(-50%)', zIndex: 0, pointerEvents: 'none', overflow: 'hidden' }}>
        <img src="/decor-blueprint.png" alt="" style={{ position: 'absolute', width: 'min(540px, 46vw)', opacity: 0.12, top: '-120px', left: '-145px', transform: 'rotate(-11deg)', filter: 'drop-shadow(0 0 18px rgba(139,92,246,.22))', mixBlendMode: 'screen' }} />
        <img src="/decor-atom.png" alt="" style={{ position: 'absolute', width: 'min(280px, 24vw)', opacity: 0.2, right: '-70px', top: '18px', transform: 'rotate(18deg)', filter: 'drop-shadow(0 0 15px rgba(139,92,246,.3))', mixBlendMode: 'screen' }} />
        <img src="/decor-molecule.png" alt="" style={{ position: 'absolute', width: 'min(230px, 20vw)', opacity: 0.22, left: '-62px', bottom: '24px', transform: 'rotate(-12deg)', filter: 'drop-shadow(0 0 12px rgba(139,92,246,.25))', mixBlendMode: 'screen' }} />
      </div>
      <Reveal className="section-heading"><div><p className="eyebrow">{t('sciencesBadge')}</p><h2>{t('sciencesTitle')}</h2></div><p>{t('sciencesDesc')}</p></Reveal>
      <Reveal className="science-grid reveal-stagger">
        {sciences.map(({ icon: Icon, nameKey, copyKey, statKey, image }) => (
          <a className="science-card" href="#workspace" key={nameKey}>
            <span className="science-icon"><img src={image} alt={t(nameKey)} /></span>
            <div><h3>{t(nameKey)}</h3><p>{t(copyKey)}</p><small>{t(statKey)}</small></div>
            <ChevronRight className="card-arrow" />
          </a>
        ))}
      </Reveal>
    </section>

    <section className="section-wrap section-block" id="molecules">
      <SectionDecor icons={[
        { Icon: Dna, className: 'ed-top-left', color: '#a78bfa' },
        { Icon: Droplets, className: 'ed-top-right', color: '#22d3ee', ring: false },
        { Icon: Hexagon, className: 'ed-bottom-left', color: '#34d399', ring: false },
        { Icon: Orbit, className: 'ed-bottom-right', color: '#8b5cf6' },
        { Icon: Atom, className: 'ed-mid-left', color: '#60a5fa', ring: false },
        { Icon: Waves, className: 'ed-mid-right', color: '#e879f9' },
      ]} />
      <Reveal className="section-heading"><div><p className="eyebrow">{t('moleculesBadge')}</p><h2>{t('moleculesTitle')}</h2></div><a className="inline-link" href="#workspace">{t('moleculesExplore')} <ArrowRight /></a></Reveal>
      <Reveal className="molecule-grid reveal-stagger">
        {molecules.map((molecule, index) => (
          <article className={`molecule-card molecule-${molecule.type}`} key={molecule.formula}>
            <div className="molecule-copy">
              <span className="molecule-index">0{index + 1}</span>
              <h3>{molecule.formula}</h3>
              <p className="molecule-name">{t(molecule.nameKey)}</p>
              <p>{t(molecule.copyKey)}</p>
              <a href="#workspace">{t('moleculeInspect')} <ArrowRight /></a>
            </div>
            <img className="molecule-art" src={molecule.image} alt={t(molecule.nameKey)} />
          </article>
        ))}
      </Reveal>
    </section>

    <section className="section-wrap section-block workspace-grid" id="workspace">
      <SectionDecor icons={[
        { Icon: Orbit, className: 'ed-top-left', color: '#8b5cf6' },
        { Icon: Waves, className: 'ed-top-right', color: '#22d3ee', ring: false },
        { Icon: Sparkles, className: 'ed-bottom-left', color: '#f59e0b', ring: false },
        { Icon: Atom, className: 'ed-bottom-right', color: '#60a5fa' },
        { Icon: Network, className: 'ed-mid-left', color: '#34d399', ring: false },
        { Icon: Radar, className: 'ed-mid-right', color: '#e879f9' },
      ]} />
      <Reveal className="tool-panel periodic-panel">
        <div className="tool-heading"><div><p className="eyebrow">{t('elementEyebrow')}</p><h2>{t('elementTitle')}</h2><p>{t('elementDesc')}</p></div><span className="tool-icon"><Atom /></span></div>
        <div className="periodic-content">
          <div className="element-grid">{elements.map((element) => <button key={element.symbol} className={selectedSymbol === element.symbol ? 'element-cell selected' : 'element-cell'} onClick={() => setSelectedSymbol(element.symbol)}><strong>{element.symbol}</strong><span>{element.number}</span><small>{t(element.nameKey)}</small></button>)}</div>
          <div className="element-details">
            <div className="detail-title"><div><h3>{t(selected.nameKey)}</h3><p>{t(selected.categoryKey)} &middot; {t(selected.stateKey)}</p></div><strong>{selected.number}</strong></div>
            <dl><div><dt>{t('elementAtomicMass')}</dt><dd>{selected.mass}</dd></div><div><dt>{t('elementElectronConfig')}</dt><dd>{selected.config}</dd></div><div><dt>{t('elementMelting')}</dt><dd>{selected.melting}</dd></div><div><dt>{t('elementBoiling')}</dt><dd>{selected.boiling}</dd></div></dl>
            <ScientificCoreModel size={370} accentColor="#7c3aed" electrons={selected.number} />
          </div>
        </div>
        <button className="discover-link" onClick={() => setSelectedSymbol(elements[(elements.findIndex((item) => item.symbol === selectedSymbol) + 1) % elements.length].symbol)}>{t('elementDiscover')} <ArrowRight /></button>
      </Reveal>
      <Reveal className="tool-panel assistant-panel">
        <div className="tool-heading"><div><p className="eyebrow">{t('copilotEyebrow')}</p><h2>{t('copilotTitle')}</h2><p>{t('copilotDesc')}</p></div><span className="tool-icon"><BrainCircuit /></span></div>
        <div className="chat-window">
          <div className="chat-messages">
            <div className="user-bubble">{t('copilotUserQuestion')}</div>
            <div className={`assistant-bubble ${thinking ? 'is-thinking' : 'reply-enter'}`}><BrainCircuit size={14} />{thinking ? <span className="typing-dots" aria-label="AI is thinking"><i /><i /><i /></span> : <p key={assistantReply}>{assistantReply || defaultReply}</p>}</div>
            <div className="source-trace"><span>{t('copilotSourceTrace')}</span><a href="#research">{t('copilotSourceLink')} <ArrowRight /></a></div>
          </div>
          <div className="chat-suggest">
            <span className="chat-suggest-label">{t('copilotSuggestLabel')}</span>
            <div className="chat-suggest-row">
              <button onClick={() => setAssistantInput('Explain molecular polarity')}>{t('copilotSuggestPolarity')}</button>
              <button onClick={() => setAssistantInput('Analyze this reaction')}>{t('copilotSuggestReaction')}</button>
              <button onClick={() => setAssistantInput('Predict a reaction')}>{t('copilotSuggestPathway')}</button>
            </div>
          </div>
          <div className="prompt-row"><input aria-label="Ask the AI chemistry assistant" placeholder={t('copilotPlaceholder')} value={assistantInput} onChange={(event) => setAssistantInput(event.target.value)} onKeyDown={(event) => { if (event.key === 'Enter' && !event.nativeEvent.isComposing && event.keyCode !== 229) askAssistant() }} /><button className="button button-primary prompt-submit" aria-label="Send prompt" onClick={askAssistant}><Send size={14} /></button></div>
        </div>
      </Reveal>
    </section>

    <section className="section-wrap section-block stats-section" id="research">
      <SectionDecor icons={[
        { Icon: Atom, className: 'ed-top-left', color: '#8b5cf6' },
        { Icon: FlaskConical, className: 'ed-top-right', color: '#22d3ee', ring: false },
        { Icon: Dna, className: 'ed-bottom-left', color: '#34d399', ring: false },
        { Icon: Sparkles, className: 'ed-bottom-right', color: '#f59e0b' },
        { Icon: Microscope, className: 'ed-mid-left', color: '#60a5fa', ring: false },
        { Icon: Gauge, className: 'ed-mid-right', color: '#e879f9' },
      ]} />
      <Reveal className="section-heading"><div><p className="eyebrow">{t('statsBadge')}</p><h2>{t('statsTitle')}</h2></div><p className="section-heading-desc">{t('statsDesc')}</p></Reveal>
      <Reveal className="stats-grid reveal-stagger">
        {stats.map(({ value, badge, label, accent, accent2, viz }) => (
          <div className="stat-card" key={label} style={{ '--stat-accent': accent, '--stat-accent-2': accent2 } as React.CSSProperties}>
            <span className="stat-label">{label}</span>
            <strong className="stat-value">{value}</strong>
            {badge && <span className="stat-badge">{badge}</span>}
            {viz}
          </div>
        ))}
      </Reveal>
    </section>

    <footer className="site-footer"><div className="footer-grid section-wrap">
      <div className="footer-brand">
        <Logo />
        <p>{t('footerDesc')}</p>
        <div className="social-row">
          <button aria-label="GitHub"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M15 22v-4a4.8 4.8 0 0 0-1-3.5c3 0 6-2 6-5.5.08-1.25-.27-2.48-1-3.5.28-1.15.28-2.35 0-3.5 0 0-1 0-3 1.5-2.64-.5-5.36-.5-8 0C6 2 5 2 5 2c-.3 1.15-.3 2.35 0 3.5A5.403 5.403 0 0 0 4 9c0 3.5 3 5.5 6 5.5-.39.49-.68 1.05-.85 1.65-.17.6-.22 1.23-.15 1.85v4"/><path d="M9 18c-4.51 2-5-2-7-2"/></svg></button>
          <button aria-label="Discord"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M8 12a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"/><path d="M16 12a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z"/><path d="M15.5 17c0 1 1.5 3 2 3 1.5 0 2.833-1.667 3.5-3 .667-1.667.5-5.833-1.5-11.5-1.457-1.015-3-1.34-4.5-1.5l-1 2.5"/><path d="M8.5 17c0 1-1.356 3-1.832 3-1.429 0-2.698-1.667-3.333-3-.635-1.667-.476-5.833 1.428-11.5C6.151 4.485 7.545 4.16 9 4l1 2.5"/></svg></button>
          <button aria-label="LinkedIn"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"/><rect width="4" height="12" x="2" y="9"/><circle cx="4" cy="4" r="2"/></svg></button>
        </div>
      </div>
      <div>
        <h3>{t('footerProduct')}</h3>
        <a href="#workspace">{t('footerWorkspace')}</a>
        <a href="#sciences">{t('footerSciences')}</a>
        <a href="#molecules">{t('footerMolecules')}</a>
        <a href="#workspace">{t('navOpenWorkspace')}</a>
      </div>
      <div>
        <h3>{t('footerResources')}</h3>
        <Link href={`/${locale}/about`}>{t('footerAbout')}</Link>
        <Link href={`/${locale}/terms`}>{t('footerTerms')}</Link>
        <a href="#research">{t('footerMolecules')}</a>
      </div>
      <div>
        <h3>{t('footerCompany')}</h3>
        <Link href={`/${locale}/about`}>{t('footerAbout')}</Link>
        <Link href={`/${locale}/terms`}>{t('footerPrivacy')}</Link>
        <Link href={`/${locale}/terms`}>{t('footerTerms')}</Link>
      </div>
      <div className="footer-newsletter">
        <h3>{t('footerNewsletterTitle')}</h3>
        <p>{t('footerNewsletterDesc')}</p>
        <div className="newsletter-row"><input aria-label={t('footerNewsletterPlaceholder')} placeholder={t('footerNewsletterPlaceholder')} /><button className="button button-primary" aria-label="Subscribe"><ArrowRight /></button></div>
      </div>
    </div><div className="copyright section-wrap"><span>&copy; 2026 jasScience</span><span>{t('footerCopyright')}</span></div></footer>
  </main>
}
