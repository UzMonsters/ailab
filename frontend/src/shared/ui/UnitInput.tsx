'use client';

import type { UnitValue } from '@/shared/types/catalog';

export function UnitInput({ value, units, onChange, label, disabled }: { value: UnitValue; units: readonly string[]; onChange: (value: UnitValue) => void; label?: string; disabled?: boolean }) {
  return <label className="block"><span className="text-xs font-medium text-slate-400">{label}</span><span className="mt-1.5 flex overflow-hidden rounded-lg border border-white/10 bg-black/20 focus-within:border-violet-400"><input disabled={disabled} type="number" inputMode="decimal" value={value.value ?? ''} onChange={event => onChange({ ...value, value: event.target.value === '' ? null : Number(event.target.value) })} className="min-w-0 flex-1 bg-transparent px-3 py-2 text-sm text-white outline-none disabled:opacity-50"/><select disabled={disabled} value={value.unit} onChange={event => onChange({ ...value, unit: event.target.value })} className="border-l border-white/10 bg-[#141b2a] px-3 text-sm text-slate-200 outline-none disabled:opacity-50">{units.map(unit => <option value={unit} key={unit}>{unit}</option>)}</select></span></label>;
}
