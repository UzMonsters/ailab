'use client';

import { ArrowDown, ArrowUp, Film, Image, MousePointer2, Plus, Trash2 } from 'lucide-react';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { ScenarioHint, ScenarioSceneObject } from '../scenario.types';
import type { RecordedPort } from '../scene/ScenarioSceneEditor';

const input = 'rounded-lg border border-white/10 bg-[#080c14] px-3 py-2 text-sm text-white';

const HINT_TYPES = [
  { value: 'TEXT', label: 'Text', group: 'Basic' },
  { value: 'HIGHLIGHT', label: 'Highlight', group: 'Basic' },
  { value: 'ARROW', label: 'Arrow', group: 'Basic' },
  { value: 'GHOST_PLACEMENT', label: 'Ghost placement', group: 'Basic' },
  { value: 'CONNECT_PORTS', label: 'Connect ports', group: 'Basic' },
  { value: 'VIDEO', label: 'Video', group: 'Media' },
  { value: 'IMAGE', label: 'Image', group: 'Media' },
  { value: 'OPEN_TAB', label: 'Open tab', group: 'Navigation' },
  { value: 'HIGHLIGHT_TAB', label: 'Highlight tab', group: 'Navigation' },
  { value: 'HIGHLIGHT_EQUIPMENT', label: 'Highlight equipment', group: 'Highlight' },
  { value: 'HIGHLIGHT_MATERIAL', label: 'Highlight material', group: 'Highlight' },
  { value: 'HIGHLIGHT_OBJECT', label: 'Highlight object', group: 'Highlight' },
  { value: 'HIGHLIGHT_PORT', label: 'Highlight port', group: 'Highlight' },
  { value: 'SHOW_ARROW', label: 'Show arrow', group: 'Visual' },
  { value: 'GHOST_PLACE_OBJECT', label: 'Ghost place object', group: 'Visual' },
  { value: 'FOCUS_CAMERA', label: 'Focus camera', group: 'Visual' },
] as const;

const TAB_OPTIONS = [
  'SANDBOX_EQUIPMENT_TAB',
  'SANDBOX_MATERIALS_TAB',
  'SANDBOX_MEASUREMENTS_TAB',
  'SANDBOX_REACTION_LOG',
] as const;

const empty = (): ScenarioHint => ({
  id: crypto.randomUUID(),
  type: 'TEXT',
  translations: { ru: { text: '' }, uz: { text: '' }, en: { text: '' } },
  targetAlias: '',
  targetPortId: '',
  fromAlias: '',
  fromPortId: '',
  toAlias: '',
  toPortId: '',
  assetId: '',
  targetTab: '',
});

export function HintEditor({
  value, objects, locale, onChange, onRecord, previewId, materials, equipment,
}: {
  value: ScenarioHint[];
  objects: ScenarioSceneObject[];
  locale: Locale;
  onChange: (v: ScenarioHint[]) => void;
  onRecord: (id: string, side: 'from' | 'to' | 'target') => void;
  previewId: string;
  materials?: JsonObject[];
  equipment?: JsonObject[];
}) {
  const patch = (id: string, next: Partial<ScenarioHint>) => {
    onChange(value.map(x => x.id === id ? { ...x, ...next } : x));
  };

  const move = (i: number, d: number) => {
    const next = [...value];
    const j = i + d;
    if (j < 0 || j >= next.length) return;
    [next[i], next[j]] = [next[j], next[i]];
    onChange(next);
  };

  const aliases = (
    <>
      <option value="">Select object</option>
      {objects.map(x => (
        <option key={x.id} value={x.alias}>{x.alias}</option>
      ))}
    </>
  );

  const needsTarget = (type: string) =>
    ['HIGHLIGHT', 'HIGHLIGHT_OBJECT', 'HIGHLIGHT_PORT', 'GHOST_PLACEMENT', 'GHOST_PLACE_OBJECT', 'FOCUS_CAMERA'].includes(type);

  const needsFromTo = (type: string) =>
    ['ARROW', 'SHOW_ARROW', 'CONNECT_PORTS'].includes(type);

  const needsTab = (type: string) =>
    ['OPEN_TAB', 'HIGHLIGHT_TAB'].includes(type);

  const needsMedia = (type: string) =>
    ['VIDEO', 'IMAGE'].includes(type);

  const needsMaterial = (type: string) =>
    type === 'HIGHLIGHT_MATERIAL';

  const needsEquipment = (type: string) =>
    type === 'HIGHLIGHT_EQUIPMENT';

  return (
    <section className="space-y-3 rounded-xl border border-white/10 bg-black/15 p-4">
      <h3 className="text-sm font-semibold">Hints</h3>

      {value.map((hint, index) => (
        <article key={hint.id} className={`rounded-lg border p-3 ${previewId === hint.id ? 'border-amber-300' : 'border-white/5'}`}>
          <div className="flex gap-2">
            <select
              className={input}
              value={hint.type}
              onChange={e => patch(hint.id, { type: e.target.value as ScenarioHint['type'] })}
            >
              {Object.entries(
                HINT_TYPES.reduce((groups, item) => {
                  if (!groups[item.group]) groups[item.group] = [];
                  groups[item.group].push(item);
                  return groups;
                }, {} as Record<string, typeof HINT_TYPES[number][]>),
              ).map(([group, items]) => (
                <optgroup key={group} label={group}>
                  {items.map(item => (
                    <option key={item.value} value={item.value}>{item.label}</option>
                  ))}
                </optgroup>
              ))}
            </select>
            <button onClick={() => move(index, -1)} aria-label="Move hint up" className="text-slate-400 hover:text-white">
              <ArrowUp size={14} />
            </button>
            <button onClick={() => move(index, 1)} aria-label="Move hint down" className="text-slate-400 hover:text-white">
              <ArrowDown size={14} />
            </button>
            <button onClick={() => onChange(value.filter(x => x.id !== hint.id))} className="text-slate-400 hover:text-rose-300">
              <Trash2 size={14} />
            </button>
          </div>

          <div className="mt-2 flex gap-1">
            {CONTENT_LOCALES.map(x => (
              <span key={x} className={`rounded px-1.5 text-[10px] ${x === locale ? 'bg-violet-600' : 'bg-white/5'}`}>
                {x.toUpperCase()}
              </span>
            ))}
          </div>

          <input
            className={`mt-2 w-full ${input}`}
            value={hint.translations[locale].text}
            onChange={e => patch(hint.id, { translations: { ...hint.translations, [locale]: { text: e.target.value } } })}
            placeholder="Learner-facing hint text"
          />

          {needsTarget(hint.type) && (
            <div className="mt-2 flex gap-2">
              <select
                className={`flex-1 ${input}`}
                value={hint.targetAlias}
                onChange={e => patch(hint.id, { targetAlias: e.target.value })}
              >
                {aliases}
              </select>
              <button
                onClick={() => onRecord(hint.id, 'target')}
                className="flex items-center gap-1 rounded border border-white/10 px-2 text-xs text-slate-400 hover:text-violet-300"
              >
                <MousePointer2 size={12} /> Select
              </button>
            </div>
          )}

          {needsMaterial(hint.type) && (
            <div className="mt-2">
              <label className="text-[10px] text-slate-500">Material</label>
              <select
                className={`mt-1 w-full ${input}`}
                value={hint.materialId || ''}
                onChange={e => patch(hint.id, { materialId: e.target.value })}
              >
                <option value="">Select material</option>
                {(materials ?? []).map(m => (
                  <option key={String(m.id)} value={String(m.id)}>{String(m.name || m.code || m.id)}</option>
                ))}
              </select>
            </div>
          )}

          {needsEquipment(hint.type) && (
            <div className="mt-2">
              <label className="text-[10px] text-slate-500">Equipment</label>
              <select
                className={`mt-1 w-full ${input}`}
                value={hint.equipmentId || ''}
                onChange={e => patch(hint.id, { equipmentId: e.target.value })}
              >
                <option value="">Select equipment</option>
                {(equipment ?? []).map(eq => (
                  <option key={String(eq.id)} value={String(eq.id)}>{String(eq.name || eq.code || eq.id)}</option>
                ))}
              </select>
            </div>
          )}

          {needsFromTo(hint.type) && (
            <div className="mt-2 grid gap-2 md:grid-cols-2">
              <div>
                <label className="text-[10px] text-slate-500">From</label>
                <div className="flex gap-1">
                  <select className={`flex-1 ${input}`} value={hint.fromAlias}
                    onChange={e => patch(hint.id, { fromAlias: e.target.value, fromPortId: '' })}>
                    {aliases}
                  </select>
                  <button onClick={() => onRecord(hint.id, 'from')}
                    className="text-xs text-slate-400 hover:text-violet-300">
                    <MousePointer2 size={12} />
                  </button>
                </div>
              </div>
              <div>
                <label className="text-[10px] text-slate-500">To</label>
                <div className="flex gap-1">
                  <select className={`flex-1 ${input}`} value={hint.toAlias}
                    onChange={e => patch(hint.id, { toAlias: e.target.value, toPortId: '' })}>
                    {aliases}
                  </select>
                  <button onClick={() => onRecord(hint.id, 'to')}
                    className="text-xs text-slate-400 hover:text-violet-300">
                    <MousePointer2 size={12} />
                  </button>
                </div>
              </div>
            </div>
          )}

          {needsTab(hint.type) && (
            <div className="mt-2">
              <select
                className={`w-full ${input}`}
                value={hint.targetTab || ''}
                onChange={e => patch(hint.id, { targetTab: e.target.value })}
              >
                <option value="">Select tab</option>
                {TAB_OPTIONS.map(tab => (
                  <option key={tab} value={tab}>{tab.replace('SANDBOX_', '').replace('_', ' ')}</option>
                ))}
              </select>
            </div>
          )}

          {needsMedia(hint.type) && (
            <div className="mt-2">
              <label className="text-[10px] text-slate-500">Asset ID</label>
              <input
                className={`mt-1 w-full ${input}`}
                value={hint.assetId || ''}
                onChange={e => patch(hint.id, { assetId: e.target.value })}
                placeholder="Asset ID for video/image"
              />
              <p className="mt-1 text-[10px] text-slate-600">
                {hint.type === 'VIDEO' ? 'Upload video via Asset API, then paste asset ID.' : 'Upload image via Asset API, then paste asset ID.'}
              </p>
            </div>
          )}
        </article>
      ))}

      <button
        onClick={() => onChange([...value, empty()])}
        className="flex w-full items-center justify-center gap-2 rounded-lg border border-dashed border-white/10 py-2 text-xs text-slate-500 hover:text-violet-300 hover:border-violet-500/40"
      >
        <Plus size={12} /> Add Hint
      </button>
    </section>
  );
}

export const applyRecordedPort = (
  hints: ScenarioHint[],
  id: string,
  side: 'from' | 'to' | 'target',
  port: RecordedPort,
): ScenarioHint[] =>
  hints.map(x => {
    if (x.id !== id) return x;
    if (side === 'from') return { ...x, fromAlias: port.alias, fromPortId: port.portId };
    if (side === 'to') return { ...x, toAlias: port.alias, toPortId: port.portId };
    return { ...x, targetAlias: port.alias, targetPortId: port.portId };
  });
