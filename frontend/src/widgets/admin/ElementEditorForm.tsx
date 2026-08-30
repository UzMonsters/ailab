'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { Activity, Atom, BookOpen, CheckCircle2, Database, FlaskConical, Globe2, Orbit, Save, ShieldAlert, Sparkles } from 'lucide-react';

const tabs = [
  ['Identity', Atom], ['Atomic data', Orbit], ['Properties', Activity], ['Isotopes', Database],
  ['Safety', ShieldAlert], ['Localization', Globe2], ['Preview', BookOpen],
] as const;

const inputClass = 'mt-1.5 w-full rounded-lg border border-white/10 bg-[#141b2a] px-3.5 py-2.5 text-sm text-white outline-none transition-colors focus:border-violet-500';

export default function ElementEditorForm({ elementId }: { elementId?: string }) {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('Identity');
  const isNew = !elementId || elementId === 'new';

  return <div className="min-h-screen bg-[#070b14] p-4 text-white md:p-6">
    <AdminPageHeader title={isNew ? 'Create Element' : `Edit Element: ${elementId}`} description="Configure scientific identity, atomic properties and catalog presentation." actions={<div className="flex gap-2"><button onClick={() => router.back()} className="rounded-lg border border-white/10 bg-[#141b2a] px-4 py-2 text-sm font-semibold text-[#a9b2c2] hover:text-white">Cancel</button><button className="flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500"><Save size={16}/>Save element</button></div>} />

    <div className="grid gap-6 lg:grid-cols-[220px_1fr]">
      <aside className="h-fit rounded-xl border border-white/[.06] bg-[#0b101a] p-2 lg:sticky lg:top-6">{tabs.map(([label,Icon]) => <button key={label} onClick={() => setActiveTab(label)} className={`flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left text-sm font-medium ${activeTab === label ? 'bg-violet-500/15 text-violet-300' : 'text-[#8490a3] hover:bg-white/[.03] hover:text-white'}`}><Icon size={17}/>{label}</button>)}</aside>

      <main className="min-h-[620px] overflow-hidden rounded-2xl border border-white/[.06] bg-[#0b101a]">
        <div className="border-b border-white/[.06] bg-gradient-to-r from-violet-500/10 to-cyan-500/[.06] px-6 py-5"><div className="mb-1 flex items-center gap-2 text-[11px] font-bold uppercase tracking-[.18em] text-violet-400"><Sparkles size={13}/>Periodic catalog</div><h2 className="text-xl font-bold">{activeTab}</h2></div>

        {activeTab === 'Identity' && <div className="grid gap-6 p-6 xl:grid-cols-[1fr_280px]">
          <section className="grid gap-4 sm:grid-cols-2"><label>Element name<input className={inputClass} defaultValue={isNew ? '' : 'Hydrogen'} placeholder="Hydrogen"/></label><label>Symbol<input className={inputClass} defaultValue={isNew ? '' : 'H'} placeholder="H"/></label><label>Atomic number<input type="number" className={inputClass} defaultValue={isNew ? '' : 1} placeholder="1"/></label><label>Atomic mass<input className={inputClass} defaultValue={isNew ? '' : '1.008'} placeholder="1.008"/></label><label>Category<select className={inputClass} defaultValue="Nonmetal"><option>Nonmetal</option><option>Alkali metal</option><option>Transition metal</option><option>Halogen</option><option>Noble gas</option><option>Metalloid</option></select></label><label>Standard state<select className={inputClass} defaultValue="Gas"><option>Gas</option><option>Liquid</option><option>Solid</option><option>Unknown</option></select></label><label className="sm:col-span-2">Short scientific description<textarea rows={5} className={`${inputClass} resize-none`} placeholder="Concise catalog description shown in reference surfaces."/></label></section>
          <aside className="rounded-2xl border border-cyan-400/20 bg-gradient-to-br from-cyan-500/10 via-[#111827] to-violet-500/15 p-5"><div className="text-xs uppercase tracking-widest text-[#8490a3]">Cell preview</div><div className="mt-8 flex aspect-square items-center justify-center rounded-2xl border border-white/10 bg-black/20"><div className="text-center"><div className="text-xs text-cyan-300">1</div><div className="text-7xl font-semibold">H</div><div className="mt-2 text-sm">Hydrogen</div><div className="text-xs text-[#8490a3]">1.008</div></div></div><div className="mt-5 flex items-center gap-2 text-xs text-emerald-400"><CheckCircle2 size={15}/>Ready for periodic-table preview</div></aside>
        </div>}

        {activeTab === 'Atomic data' && <div className="grid gap-5 p-6 md:grid-cols-2"><label>Electron configuration<input className={inputClass} defaultValue="1s¹"/></label><label>Oxidation states<input className={inputClass} defaultValue="−1, +1"/></label><label>Group<input type="number" className={inputClass} defaultValue="1"/></label><label>Period<input type="number" className={inputClass} defaultValue="1"/></label><label>Block<select className={inputClass}><option>s-block</option><option>p-block</option><option>d-block</option><option>f-block</option></select></label><label>Electronegativity<input className={inputClass} defaultValue="2.20"/></label></div>}

        {activeTab === 'Properties' && <div className="grid gap-4 p-6 sm:grid-cols-2 xl:grid-cols-3">{[['Density','0.08988 g/L'],['Melting point','−259.16 °C'],['Boiling point','−252.87 °C'],['Atomic radius','53 pm'],['Ionization energy','1312 kJ/mol'],['Thermal conductivity','0.1805 W/m·K']].map(([label,value]) => <label key={label}>{label}<input className={inputClass} defaultValue={value}/></label>)}</div>}

        {activeTab === 'Isotopes' && <div className="p-6"><div className="overflow-hidden rounded-xl border border-white/[.06]">{[['¹H','Protium','99.985%','Stable'],['²H','Deuterium','0.015%','Stable'],['³H','Tritium','Trace','Radioactive']].map(row => <div key={row[0]} className="grid grid-cols-4 border-b border-white/[.05] px-5 py-4 text-sm last:border-0"><strong>{row[0]}</strong><span>{row[1]}</span><span className="text-[#8490a3]">{row[2]}</span><span className={row[3] === 'Stable' ? 'text-emerald-400' : 'text-amber-400'}>{row[3]}</span></div>)}</div></div>}

        {activeTab === 'Safety' && <div className="grid gap-5 p-6 md:grid-cols-2"><label>Hazard class<select className={inputClass}><option>Flammable gas</option><option>Oxidizing</option><option>Corrosive</option><option>No classification</option></select></label><label>Signal word<select className={inputClass}><option>Danger</option><option>Warning</option><option>None</option></select></label><label className="md:col-span-2">Editorial safety note<textarea rows={5} className={`${inputClass} resize-none`} defaultValue="Catalog-level scientific warning for educational display. No real laboratory procedure is provided."/></label></div>}

        {activeTab === 'Localization' && <div className="p-6"><div className="grid gap-4 md:grid-cols-3">{[['RU','Водород','Complete'],['UZ','Vodorod','Review'],['EN','Hydrogen','Complete']].map(([lang,name,status]) => <section key={lang} className="rounded-xl border border-white/[.06] bg-[#141b2a] p-4"><div className="flex justify-between text-xs"><strong>{lang}</strong><span className={status === 'Complete' ? 'text-emerald-400' : 'text-amber-400'}>{status}</span></div><input className={inputClass} defaultValue={name}/><textarea rows={4} className={`${inputClass} resize-none`} placeholder="Localized description"/></section>)}</div></div>}

        {activeTab === 'Preview' && <div className="grid gap-6 p-6 lg:grid-cols-[300px_1fr]"><div className="flex aspect-square items-center justify-center rounded-2xl border border-cyan-400/20 bg-gradient-to-br from-cyan-500/15 to-violet-500/15"><div className="text-center"><div className="text-sm text-cyan-300">1</div><div className="text-8xl font-semibold">H</div><div className="mt-2 text-lg">Hydrogen</div></div></div><section className="rounded-2xl border border-white/[.06] bg-[#141b2a] p-6"><div className="flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-violet-400"><FlaskConical size={15}/>Reference preview</div><h3 className="mt-5 text-3xl font-bold">Hydrogen</h3><p className="mt-3 leading-7 text-[#aeb7c7]">The lightest element and the most abundant chemical element in the universe. This preview shows how the record will appear in scientific reference surfaces.</p><div className="mt-6 grid grid-cols-2 gap-3">{[['Group','1'],['Period','1'],['State','Gas'],['Mass','1.008']].map(([k,v]) => <div key={k} className="rounded-lg border border-white/[.06] bg-[#0b101a] p-3"><div className="text-xs text-[#8490a3]">{k}</div><div className="mt-1 font-semibold">{v}</div></div>)}</div></section></div>}
      </main>
    </div>
  </div>;
}
