'use client';

import { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import { BookOpen, ChevronLeft, ChevronRight, Eye, GripVertical, Image, Languages, LayoutTemplate, ListTree, Save, Sparkles, Type } from 'lucide-react';

const chapters = [
  { number: '01', title: 'Laboratory Basics', spreads: 8, status: 'Published' },
  { number: '02', title: 'Matter & Reactions', spreads: 12, status: 'Review' },
  { number: '03', title: 'Laboratory Systems', spreads: 9, status: 'Draft' },
  { number: '04', title: 'Energy & Change', spreads: 11, status: 'Draft' },
];

const spreads = [
  { id: 1, title: 'Welcome to the laboratory', type: 'Opening' },
  { id: 2, title: 'Your workspace', type: 'Concept' },
  { id: 3, title: 'Core glassware', type: 'Gallery' },
  { id: 4, title: 'Safety before action', type: 'Checklist' },
];

export default function AdminBookPage() {
  const [chapter, setChapter] = useState(0);
  const [spread, setSpread] = useState(0);
  const [mode, setMode] = useState<'edit' | 'preview'>('edit');

  return <div className="min-h-screen bg-[#070b14] p-4 text-white md:p-6">
    <AdminPageHeader title="Book Studio" description="Shape chapters, spreads and visual learning moments in one focused workspace." actions={<div className="flex gap-2"><button onClick={() => setMode(mode === 'edit' ? 'preview' : 'edit')} className="flex items-center gap-2 rounded-lg border border-white/10 bg-[#141b2a] px-4 py-2 text-sm font-semibold"><Eye size={16}/>{mode === 'edit' ? 'Preview' : 'Edit spread'}</button><button className="flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold"><Save size={16}/>Save layout</button></div>} />

    <div className="mb-5 grid gap-4 sm:grid-cols-3">
      {[['Chapters','12','4 published'],['Spreads','126','18 in review'],['Localization','94%','RU · UZ · EN']].map(([label,value,meta]) => <div key={label} className="rounded-xl border border-white/[.06] bg-[#0b101a] p-4"><div className="text-xs text-[#8490a3]">{label}</div><div className="mt-1 flex items-end justify-between"><span className="text-2xl font-bold">{value}</span><span className="text-xs text-emerald-400">{meta}</span></div></div>)}
    </div>

    <div className="grid min-h-[650px] overflow-hidden rounded-2xl border border-white/[.07] bg-[#0b101a] xl:grid-cols-[240px_260px_1fr]">
      <aside className="border-b border-white/[.06] p-3 xl:border-b-0 xl:border-r">
        <div className="mb-3 flex items-center gap-2 px-2 py-2 text-xs font-bold uppercase tracking-[.16em] text-[#8490a3]"><ListTree size={15}/>Chapters</div>
        <div className="space-y-1">{chapters.map((item,index) => <button key={item.number} onClick={() => {setChapter(index); setSpread(0);}} className={`w-full rounded-xl p-3 text-left transition-colors ${chapter === index ? 'bg-violet-500/15 ring-1 ring-violet-400/25' : 'hover:bg-white/[.03]'}`}><div className="flex gap-3"><span className={`mt-0.5 text-xs font-bold ${chapter === index ? 'text-violet-400' : 'text-[#8490a3]'}`}>{item.number}</span><div className="min-w-0"><div className="truncate text-sm font-semibold">{item.title}</div><div className="mt-1 flex gap-2 text-[11px] text-[#8490a3]"><span>{item.spreads} spreads</span><span>·</span><span>{item.status}</span></div></div></div></button>)}</div>
      </aside>

      <aside className="border-b border-white/[.06] bg-[#0d1320] p-3 xl:border-b-0 xl:border-r">
        <div className="mb-3 flex items-center justify-between px-2 py-2"><span className="text-xs font-bold uppercase tracking-[.16em] text-[#8490a3]">Spreads</span><span className="rounded-full bg-white/5 px-2 py-1 text-[10px] text-[#8490a3]">{chapters[chapter].number}</span></div>
        <div className="space-y-2">{spreads.map((item,index) => <button key={item.id} onClick={() => setSpread(index)} className={`group flex w-full gap-3 rounded-xl border p-3 text-left ${spread === index ? 'border-cyan-400/30 bg-cyan-400/[.07]' : 'border-transparent hover:border-white/[.06]'}`}><GripVertical size={15} className="mt-1 shrink-0 text-[#566174]"/><div><div className="mb-2 flex h-16 w-24 items-center justify-center rounded-lg border border-white/[.07] bg-gradient-to-br from-violet-500/10 to-cyan-500/10 text-xl font-bold text-white/70">{item.id * 2 - 1}–{item.id * 2}</div><div className="text-xs font-semibold">{item.title}</div><div className="mt-1 text-[10px] uppercase tracking-wider text-[#8490a3]">{item.type}</div></div></button>)}</div>
      </aside>

      <main className="flex min-w-0 flex-col">
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/[.06] px-5 py-3"><div><div className="text-xs text-[#8490a3]">Spread {spreads[spread].id} · {chapters[chapter].title}</div><div className="mt-0.5 font-semibold">{spreads[spread].title}</div></div><div className="flex items-center gap-1 rounded-lg border border-white/[.07] bg-[#141b2a] p-1">{[[Type,'Text'],[Image,'Media'],[LayoutTemplate,'Layout'],[Languages,'Locale']].map(([Icon,label]) => {const IconComponent = Icon as typeof Type; return <button key={label as string} title={label as string} className="rounded-md p-2 text-[#8490a3] hover:bg-white/[.05] hover:text-white"><IconComponent size={16}/></button>})}</div></div>

        <div className="flex-1 overflow-y-auto p-5 md:p-8">
          {mode === 'edit' ? <div className="mx-auto grid max-w-5xl gap-5 lg:grid-cols-2">
            <section className="min-h-[460px] rounded-[24px_10px_10px_24px] border border-white/10 bg-[#f3efe4] p-8 text-[#20242b] shadow-2xl shadow-black/20"><div className="text-xs font-bold uppercase tracking-[.2em] text-violet-600">Chapter {chapters[chapter].number}</div><input defaultValue={spreads[spread].title} className="mt-8 w-full border-0 border-b border-black/10 bg-transparent pb-3 text-3xl font-bold outline-none"/><textarea defaultValue="A laboratory is more than a room full of equipment. It is a system where every observation begins with careful preparation." className="mt-8 h-40 w-full resize-none rounded-xl border border-black/10 bg-white/40 p-4 text-sm leading-7 outline-none"/><div className="mt-6 flex gap-2"><span className="rounded-full bg-violet-100 px-3 py-1.5 text-xs font-semibold text-violet-700">Core concept</span><span className="rounded-full bg-cyan-100 px-3 py-1.5 text-xs font-semibold text-cyan-700">4 min read</span></div></section>
            <section className="relative min-h-[460px] overflow-hidden rounded-[10px_24px_24px_10px] border border-white/10 bg-[#111827] p-8 shadow-2xl shadow-black/20"><div className="absolute inset-0 bg-[radial-gradient(circle_at_70%_25%,rgba(34,211,238,.18),transparent_32%),radial-gradient(circle_at_25%_80%,rgba(139,92,246,.22),transparent_35%)]"/><div className="relative flex h-full flex-col"><Sparkles className="text-cyan-300"/><div className="my-auto"><div className="text-6xl">⚗</div><h3 className="mt-6 text-2xl font-bold">Observe before you act</h3><p className="mt-3 max-w-sm text-sm leading-6 text-[#aeb8c8]">Every instrument has a purpose, a safe operating range and a place in the experimental sequence.</p></div><div className="flex items-center justify-between text-xs text-[#8490a3]"><span>Interactive illustration</span><span>02</span></div></div></section>
          </div> : <div className="mx-auto flex max-w-4xl flex-col items-center justify-center rounded-2xl border border-violet-400/20 bg-gradient-to-br from-violet-500/10 to-cyan-500/10 p-12 text-center"><BookOpen size={48} className="text-violet-400"/><div className="mt-5 text-xs font-bold uppercase tracking-[.2em] text-[#8490a3]">Learner preview</div><h2 className="mt-3 text-3xl font-bold">{spreads[spread].title}</h2><p className="mt-4 max-w-xl text-[#8490a3]">The spread preview keeps typography, pacing and visual hierarchy close to the reading experience.</p></div>}
        </div>
        <div className="flex items-center justify-between border-t border-white/[.06] px-5 py-3"><button onClick={() => setSpread(Math.max(0,spread-1))} disabled={spread === 0} className="flex items-center gap-2 text-sm text-[#8490a3] disabled:opacity-30"><ChevronLeft size={16}/>Previous</button><span className="text-xs text-[#8490a3]">Spread {spread + 1} of {spreads.length}</span><button onClick={() => setSpread(Math.min(spreads.length-1,spread+1))} disabled={spread === spreads.length-1} className="flex items-center gap-2 text-sm text-[#8490a3] disabled:opacity-30">Next<ChevronRight size={16}/></button></div>
      </main>
    </div>
  </div>;
}
