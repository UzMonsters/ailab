"use client";

import { useMemo, useState } from "react";

type Substance = {
  formula: string;
  name: string;
  state: "Liquid" | "Solid" | "Gas";
  properties: string[];
  mass: string;
  density: string;
  color: string;
  atoms: { x: number; y: number; label: string }[];
  bonds: [number, number][];
};

const substances: Substance[] = [
  { formula: "H₂O", name: "Water", state: "Liquid", properties: ["polar"], mass: "18.015 g/mol", density: "1.00 g/cm³", color: "#38bdf8", atoms: [{ x: 50, y: 48, label: "O" }, { x: 29, y: 31, label: "H" }, { x: 71, y: 31, label: "H" }], bonds: [[0, 1], [0, 2]] },
  { formula: "NaCl", name: "Sodium chloride", state: "Solid", properties: ["ionic"], mass: "58.44 g/mol", density: "2.16 g/cm³", color: "#a78bfa", atoms: [{ x: 34, y: 50, label: "Na⁺" }, { x: 66, y: 50, label: "Cl⁻" }], bonds: [[0, 1]] },
  { formula: "H₂SO₄", name: "Sulfuric acid", state: "Liquid", properties: ["corrosive", "oxidizer"], mass: "98.079 g/mol", density: "1.84 g/cm³", color: "#f59e0b", atoms: [{ x: 50, y: 50, label: "S" }, { x: 26, y: 27, label: "O" }, { x: 74, y: 27, label: "O" }, { x: 50, y: 79, label: "OH" }], bonds: [[0, 1], [0, 2], [0, 3]] },
  { formula: "C₂H₅OH", name: "Ethanol", state: "Liquid", properties: ["flammable"], mass: "46.069 g/mol", density: "0.789 g/cm³", color: "#34d399", atoms: [{ x: 31, y: 50, label: "C" }, { x: 54, y: 50, label: "C" }, { x: 75, y: 50, label: "OH" }], bonds: [[0, 1], [1, 2]] },
];

function Molecule({ substance }: { substance: Substance }) {
  return <svg viewBox="0 0 100 100" className="h-36 w-36 overflow-visible" aria-label={`${substance.name} molecular diagram`}>
    {substance.bonds.map(([a, b]) => <line key={`${a}-${b}`} x1={substance.atoms[a].x} y1={substance.atoms[a].y} x2={substance.atoms[b].x} y2={substance.atoms[b].y} stroke={substance.color} strokeOpacity=".55" strokeWidth="2" />)}
    {substance.atoms.map(atom => <g key={`${atom.label}-${atom.x}`}><circle cx={atom.x} cy={atom.y} r="10" fill={`${substance.color}22`} stroke={substance.color} strokeWidth="1"/><text x={atom.x} y={atom.y + 3} textAnchor="middle" fill="white" fontSize="7" fontWeight="700">{atom.label}</text></g>)}
  </svg>;
}

export function SubstanceIndex() {
  const [state, setState] = useState<Substance["state"] | "All">("All");
  const [property, setProperty] = useState<string | "All">("All");
  const [selected, setSelected] = useState<Substance>(substances[0]);
  const properties = ["flammable", "corrosive", "oxidizer", "ionic"];
  const filtered = useMemo(() => substances.filter(item => (state === "All" || item.state === state) && (property === "All" || item.properties.includes(property))), [state, property]);
  return <div className="grid gap-6 lg:grid-cols-[1.1fr_.9fr]"><div><div className="mb-6 flex items-end justify-between border-b border-white/10 pb-4"><div><p className="text-xs font-bold uppercase tracking-[.25em] text-violet-300">Reference catalogue</p><h1 className="mt-2 font-serif text-4xl font-bold text-white">Substance Index</h1></div><span className="text-xs text-slate-500">{filtered.length} records</span></div><div className="mb-5 grid gap-3 sm:grid-cols-2"><label className="text-[10px] font-bold uppercase tracking-[.2em] text-slate-500">State<select value={state} onChange={event => setState(event.target.value as Substance["state"] | "All")} className="mt-2 block w-full border-b border-white/15 bg-transparent py-2 text-sm font-normal normal-case tracking-normal text-slate-200 outline-none"><option>All</option><option>Liquid</option><option>Solid</option><option>Gas</option></select></label><label className="text-[10px] font-bold uppercase tracking-[.2em] text-slate-500">Property<select value={property} onChange={event => setProperty(event.target.value)} className="mt-2 block w-full border-b border-white/15 bg-transparent py-2 text-sm font-normal normal-case tracking-normal text-slate-200 outline-none"><option>All</option>{properties.map(item => <option key={item}>{item}</option>)}</select></label></div><div className="space-y-1">{filtered.map(item => <button key={item.formula} onClick={() => setSelected(item)} className={`group flex w-full items-center gap-4 border-b p-4 text-left transition ${selected.formula === item.formula ? "border-violet-300/60 bg-violet-300/10" : "border-white/5 hover:bg-white/5"}`}><span className="w-24 font-mono text-xl font-bold text-white">{item.formula}</span><span className="flex-1"><strong className="block text-sm text-slate-200">{item.name}</strong><small className="text-xs text-slate-500">{item.state} · {item.properties.join(" · ")}</small></span><span className="h-2 w-2 rounded-full" style={{ backgroundColor: item.color }}/></button>)}</div></div><aside className="relative overflow-hidden rounded-2xl border border-violet-300/20 bg-violet-300/[.04] p-6"><div className="absolute -right-12 -top-12 h-40 w-40 rounded-full blur-3xl" style={{ backgroundColor: `${selected.color}22` }}/><div className="relative"><div className="flex items-start justify-between"><div><p className="text-xs uppercase tracking-[.25em] text-violet-300">Molecular reference</p><h2 className="mt-2 font-mono text-4xl font-bold text-white">{selected.formula}</h2><p className="mt-1 text-sm text-slate-400">{selected.name}</p></div><span className="rounded-full border border-emerald-300/30 px-3 py-1 text-xs text-emerald-300">{selected.state}</span></div><div className="my-8 grid place-items-center"><Molecule substance={selected}/></div><dl className="grid grid-cols-2 gap-4 border-t border-white/10 pt-5 text-sm"><div><dt className="text-xs text-slate-500">Molar mass</dt><dd className="mt-1 text-slate-200">{selected.mass}</dd></div><div><dt className="text-xs text-slate-500">Density</dt><dd className="mt-1 text-slate-200">{selected.density}</dd></div></dl><div className="mt-5 border-t border-white/10 pt-5"><p className="text-xs uppercase tracking-[.2em] text-slate-500">Compatibility</p><div className="mt-3 flex flex-wrap gap-2"><span className="rounded border border-emerald-300/30 px-2 py-1 text-xs text-emerald-300">✓ Glass</span><span className="rounded border border-emerald-300/30 px-2 py-1 text-xs text-emerald-300">✓ PTFE</span><span className="rounded border border-red-300/30 px-2 py-1 text-xs text-red-300">✕ Reactive metals</span></div></div></div></aside></div>;
}
