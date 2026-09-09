'use client';

import Link from 'next/link';
import { ArrowUpRight, CheckCircle2, CircleDot, Languages, Layers3 } from 'lucide-react';

const learningCopy: Record<string, { title: string; subtitle: string; stats: [string, string][]; rows: [string, string, string][] }> = {
  levels: { title: 'Level library', subtitle: 'Structure the learning journey from first contact to mastery.', stats: [['Published', '47'], ['In review', '6'], ['Avg. duration', '14 min']], rows: [['Laboratory Basics', '12 levels', '92% ready'], ['Matter & Reactions', '18 levels', '76% ready'], ['Laboratory Systems', '9 levels', '61% ready']] },
  chapters: { title: 'Curriculum chapters', subtitle: 'Balance concepts, practice and checkpoints across every chapter.', stats: [['Chapters', '12'], ['Learning blocks', '146'], ['Coverage', '84%']], rows: [['01 · Laboratory Basics', 'Beginner', 'Published'], ['02 · Matter & Reactions', 'Beginner', 'Review'], ['03 · Chemical Systems', 'Intermediate', 'Draft']] },
  tasks: { title: 'Task pipeline', subtitle: 'A compact editorial view of exercises moving toward publication.', stats: [['Ready', '38'], ['Review', '11'], ['Blocked', '3']], rows: [['Identify lab glassware', 'Knowledge check', 'Ready'], ['Heat 50 ml of water', 'Simulation', 'Review'], ['Balance a reaction', 'Interactive', 'Draft']] },
  rewards: { title: 'Reward economy', subtitle: 'Keep progression motivating, legible and proportionate to effort.', stats: [['XP issued', '128k'], ['Badges', '24'], ['Unlock paths', '31']], rows: [['Safety Basics', 'Badge', '2,841 earned'], ['Thermal Observer', 'Badge', '1,506 earned'], ['Precision Pack', 'Equipment set', '924 unlocked']] },
  progress: { title: 'Learner progress', subtitle: 'Compare cohort momentum and identify where learners slow down.', stats: [['Active learners', '4,812'], ['Completion', '73%'], ['Mastery', '41%']], rows: [['School cohort A', '1,240 learners', '81% completion'], ['Independent learners', '2,816 learners', '69% completion'], ['Teacher pilots', '756 learners', '77% completion']] },
  localization: { title: 'Localization coverage', subtitle: 'Review curriculum readiness across the three product languages.', stats: [['Coverage', '94%'], ['Needs review', '87'], ['Locales', '3']], rows: [['Russian', '1,842 / 1,842 strings', 'Complete'], ['Uzbek', '1,701 / 1,842 strings', 'Review'], ['English', '1,829 / 1,842 strings', 'Near complete']] },
};

export function LearningPanel({ tab, locale = 'ru' }: { tab: string; locale?: string }) {
  const data = learningCopy[tab] ?? learningCopy.levels;
  return <div className="space-y-5">
    <div className="rounded-2xl border border-violet-400/15 bg-gradient-to-r from-violet-500/15 via-[#0b101a] to-cyan-500/10 p-6 md:p-8">
      <div className="flex flex-col gap-5 md:flex-row md:items-end md:justify-between"><div><div className="mb-2 text-xs font-bold uppercase tracking-[.18em] text-violet-400">Curriculum workspace</div><h2 className="text-2xl font-bold">{data.title}</h2><p className="mt-2 max-w-2xl text-sm text-[#8490a3]">{data.subtitle}</p></div>{tab === 'levels' && <Link href={`/${locale}/admin/learning/levels`} className="inline-flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2.5 text-sm font-semibold text-white">Open level manager <ArrowUpRight size={16}/></Link>}</div>
    </div>
    <div className="grid gap-4 sm:grid-cols-3">{data.stats.map(([label,value]) => <div key={label} className="rounded-xl border border-white/[.06] bg-[#0b101a] p-5"><div className="text-xs text-[#8490a3]">{label}</div><div className="mt-2 text-2xl font-bold">{value}</div></div>)}</div>
    <div className="overflow-hidden rounded-xl border border-white/[.06] bg-[#0b101a]"><div className="flex items-center justify-between border-b border-white/[.06] px-5 py-4"><h3 className="font-semibold">Editorial overview</h3><Layers3 size={18} className="text-violet-400"/></div>{data.rows.map(([name, meta, status]) => <div key={name} className="grid gap-2 border-b border-white/[.05] px-5 py-4 last:border-0 md:grid-cols-[1.4fr_.8fr_.7fr] md:items-center"><div className="font-medium">{name}</div><div className="text-sm text-[#8490a3]">{meta}</div><div className="flex items-center gap-2 text-sm text-emerald-400"><CheckCircle2 size={15}/>{status}</div></div>)}</div>
  </div>;
}

const chemistryCopy: Record<string, { title: string; description: string; cards: [string,string,string][] }> = {
  Overview: { title: 'Chemistry knowledge graph', description: 'A visual health check for the catalog that powers references and simulations.', cards: [['Catalog coverage','86%','Elements, substances and reactions with complete metadata'],['Simulation ready','74%','Records with validated physical behavior'],['Safety reviewed','93%','Items with current hazard classifications']] },
  Properties: { title: 'Property dictionary', description: 'Shared scientific attributes and their catalog coverage.', cards: [['Thermodynamic','42 fields','Boiling point, melting point, heat capacity'],['Optical','18 fields','Color, transparency, refractive index'],['Mechanical','27 fields','Density, hardness, viscosity']] },
  Hazards: { title: 'Hazard taxonomy', description: 'Classification coverage across substances and reaction outcomes.', cards: [['Flammable','18 items','6 require editorial review'],['Corrosive','24 items','All labels localized'],['Toxic / irritant','31 items','3 thresholds need review']] },
  Scenarios: { title: 'Scenario readiness', description: 'How chemistry records are used across the learning experience.', cards: [['Heating & cooling','16 scenarios','92% catalog coverage'],['Acid–base','11 scenarios','8 production ready'],['Gas evolution','7 scenarios','2 in safety review']] },
};

export function ChemistryPanel({ tab }: { tab: string }) {
  const data = chemistryCopy[tab] ?? chemistryCopy.Overview;
  return <div className="space-y-5">
    <div className="rounded-2xl border border-cyan-400/15 bg-gradient-to-r from-cyan-500/10 via-[#0b101a] to-violet-500/10 p-7"><div className="mb-2 flex items-center gap-2 text-xs font-bold uppercase tracking-[.18em] text-cyan-400"><CircleDot size={14}/>Catalog intelligence</div><h2 className="text-2xl font-bold text-white">{data.title}</h2><p className="mt-2 max-w-2xl text-sm text-[#8490a3]">{data.description}</p></div>
    <div className="grid gap-4 lg:grid-cols-3">{data.cards.map(([title,value,description]) => <section key={title} className="rounded-xl border border-white/[.06] bg-[#0b101a] p-5"><div className="mb-6 flex items-center justify-between"><span className="text-sm text-[#8490a3]">{title}</span>{tab === 'Hazards' ? <CircleDot size={17} className="text-amber-400"/> : tab === 'Properties' ? <Layers3 size={17} className="text-violet-400"/> : <Languages size={17} className="text-cyan-400"/>}</div><div className="text-2xl font-bold text-white">{value}</div><p className="mt-2 text-sm leading-6 text-[#8490a3]">{description}</p><div className="mt-5 h-1.5 overflow-hidden rounded-full bg-[#141b2a]"><div className="h-full w-4/5 rounded-full bg-gradient-to-r from-cyan-400 to-violet-500"/></div></section>)}</div>
  </div>;
}
