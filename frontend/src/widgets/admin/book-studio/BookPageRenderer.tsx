'use client';
import katex from 'katex';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { sanitizeSvgMarkup } from '@/shared/lib/sanitizeSvg';
import { RichTextPreview } from './RichTextPreview';

export type BookBlockKind = 'RICH_TEXT' | 'IMAGE' | 'SVG' | 'FORMULA' | 'INTERACTIVE_EXPERIMENT_LINK' | 'EQUIPMENT_REFERENCE' | 'MATERIAL_REFERENCE' | 'REACTION_REFERENCE' | 'SHAPE' | 'DATA_WIDGET' | 'TABLE';
export type BookPageBlock = {
  id: string; kind: BookBlockKind; x: number; y: number; w: number; h: number; z: number;
  translations?: Record<string, unknown>; text?: string; src?: string; assetId?: string;
  svg?: string; formula?: string; scenarioId?: string;
  equipmentId?: string; rendererKey?: string; materialId?: string; reactionId?: string;
  shapeType?: string; fillColor?: string; strokeColor?: string; strokeWidth?: number;
  borderRadius?: number;
  widgetType?: string; widgetValue?: number; widgetLabel?: string;
  fontFamily?: string; fontSize?: number; color?: string; backgroundColor?: string;
  fit?: string; alt?: string; displayMode?: string;
  showLabel?: boolean; showFormula?: boolean; showName?: boolean; showTitle?: boolean;
  showDescription?: boolean; rotation?: number; opacity?: number; locked?: boolean; visible?: boolean;
};
const object = (value: unknown): JsonObject => value && typeof value === 'object' && !Array.isArray(value) ? value as JsonObject : {};
const records = (value: unknown): JsonObject[] => Array.isArray(value) ? value.filter(item => item && typeof item === 'object') as JsonObject[] : [];
const number = (value: unknown, fallback: number) => Number.isFinite(Number(value)) ? Number(value) : fallback;

export function hydrateBookPageBlocks(raw: unknown, locale = 'en'): BookPageBlock[] {
  return records(raw).map((entry, index) => {
    const data = object(entry.data), layout = object(data.layout), translations = object(entry.translations);
    const translated = translations[locale] ?? translations.en ?? translations.ru ?? translations.uz;
    const richText = typeof translated === 'string' ? translated : String(object(translated).content ?? data.text ?? '');
    return {
      id: String(entry.id ?? `block-${index}`),
      kind: String(entry.type ?? entry.kind ?? 'RICH_TEXT') as BookBlockKind,
      x: number(layout.x ?? entry.x, 40 + (index % 2) * 310),
      y: number(layout.y ?? entry.y, 40 + Math.floor(index / 2) * 180),
      w: number(layout.width ?? entry.w, 280),
      h: number(layout.height ?? entry.h, 140),
      z: number(layout.zIndex ?? entry.z, index + 1),
      translations, text: richText,
      src: String(data.src ?? entry.src ?? ''),
      assetId: String(data.assetId ?? entry.assetId ?? ''),
      svg: sanitizeSvgMarkup(String(data.svg ?? entry.svg ?? '')),
      formula: String(data.latex ?? entry.formula ?? ''),
      equipmentId: String(data.equipmentId ?? ''),
      rendererKey: String(data.rendererKey ?? ''),
      materialId: String(data.materialId ?? ''),
      reactionId: String(data.reactionId ?? ''),
      shapeType: String(data.shapeType ?? ''),
      fillColor: String(data.fillColor ?? ''),
      strokeColor: String(data.strokeColor ?? ''),
      strokeWidth: number(data.strokeWidth, 1),
      borderRadius: number(data.borderRadius, 0),
      widgetType: String(data.widgetType ?? ''),
      widgetValue: number(data.widgetValue, 75),
      widgetLabel: String(data.widgetLabel ?? ''),
      fontFamily: String(data.fontFamily ?? ''),
      fontSize: number(data.fontSize, 14),
      color: String(data.color ?? ''),
      backgroundColor: String(data.backgroundColor ?? ''),
      fit: String(data.fit ?? ''),
      alt: String(data.alt ?? ''),
      displayMode: String(data.displayMode ?? ''),
      showLabel: typeof data.showLabel === 'boolean' ? data.showLabel : undefined,
      showFormula: typeof data.showFormula === 'boolean' ? data.showFormula : undefined,
      showName: typeof data.showName === 'boolean' ? data.showName : undefined,
      showTitle: typeof data.showTitle === 'boolean' ? data.showTitle : undefined,
      showDescription: typeof data.showDescription === 'boolean' ? data.showDescription : undefined,
      rotation: number(data.rotation ?? entry.rotation, 0),
      opacity: number(data.opacity ?? entry.opacity, 1),
      locked: typeof data.locked === 'boolean' ? data.locked : typeof entry.locked === 'boolean' ? entry.locked : undefined,
      visible: typeof data.visible === 'boolean' ? data.visible : typeof entry.visible === 'boolean' ? entry.visible : undefined,
    };
  });
}

import { EquipmentThumbnail } from '@/entities/equipment/ui/EquipmentRendererRegistry';

function EquipmentPlaceholder({ block }: { block: BookPageBlock }) {
  if (block.rendererKey || block.equipmentId) {
    return (
      <div className="flex h-full w-full items-center justify-center p-2">
        <EquipmentThumbnail
          type={block.rendererKey || block.equipmentId!}
          size={Math.min(block.w, block.h) - 20}
          frameWidth="100%"
          frameHeight="100%"
          className="bg-transparent"
        />
        {block.showName !== false && (
          <span className="absolute bottom-1 left-1/2 -translate-x-1/2 rounded bg-black/50 px-2 py-0.5 text-[10px] text-white">
            {block.equipmentId}
          </span>
        )}
      </div>
    );
  }
  return (
    <div className="flex h-full w-full flex-col items-center justify-center rounded-lg border border-violet-300/30 bg-violet-100/50 p-3 text-center">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="h-10 w-10 text-violet-500">
        <path d="M9 3h6M10 3v6.5L6 18.5C5.5 19.5 6.2 21 7.5 21h9c1.3 0 2-1.5 1.5-2.5L14 9.5V3" />
      </svg>
      <p className="mt-2 text-xs font-medium text-violet-800">Equipment</p>
      <p className="text-[10px] text-violet-600">{block.equipmentId ? block.equipmentId : 'Select equipment'}</p>
      {block.showName !== false && <p className="mt-1 text-[10px] font-semibold text-violet-700">Equipment Reference</p>}
    </div>
  );
}

function MaterialPlaceholder({ block }: { block: BookPageBlock }) {
  return (
    <div className="flex h-full w-full flex-col items-center justify-center rounded-lg border border-cyan-300/30 bg-cyan-100/50 p-3 text-center">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="h-10 w-10 text-cyan-500">
        <circle cx="12" cy="12" r="4" /><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83" />
      </svg>
      <p className="mt-2 text-xs font-medium text-cyan-800">Material</p>
      <p className="text-[10px] text-cyan-600">{block.materialId ? block.materialId : 'Select material'}</p>
      {block.showName !== false && <p className="mt-1 text-[10px] font-semibold text-cyan-700">Material Reference</p>}
    </div>
  );
}

function ReactionPlaceholder({ block }: { block: BookPageBlock }) {
  return (
    <div className="flex h-full w-full flex-col items-center justify-center rounded-lg border border-emerald-300/30 bg-emerald-100/50 p-3 text-center">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="h-10 w-10 text-emerald-500">
        <path d="M8 3v4l-3 5v2h14v-2l-3-5V3M6 10h12" />
      </svg>
      <p className="mt-2 text-xs font-medium text-emerald-800">Reaction</p>
      <p className="text-[10px] text-emerald-600">{block.reactionId ? block.reactionId : 'Select reaction'}</p>
      {block.showName !== false && <p className="mt-1 text-[10px] font-semibold text-emerald-700">Reaction Reference</p>}
    </div>
  );
}

function DataWidgetPlaceholder({ block }: { block: BookPageBlock }) {
  const value = block.widgetValue || 75;
  const label = block.widgetLabel || 'Value';
  const r = 40, c = 2 * Math.PI * r;
  const offset = c - (value / 100) * c;
  return (
    <div className="flex h-full w-full flex-col items-center justify-center p-3">
      <svg width="100" height="100" viewBox="0 0 100 100">
        <circle cx="50" cy="50" r={r} fill="none" stroke="#e2e8f0" strokeWidth="8" />
        <circle cx="50" cy="50" r={r} fill="none" stroke="#8b5cf6" strokeWidth="8"
          strokeDasharray={c} strokeDashoffset={offset} strokeLinecap="round"
          transform="rotate(-90 50 50)" />
        <text x="50" y="50" textAnchor="middle" dominantBaseline="central" fontSize="18" fontWeight="bold" fill="#1e293b">
          {value}%
        </text>
      </svg>
      <p className="mt-1 text-xs text-slate-600">{label}</p>
    </div>
  );
}

export function BookBlockRenderer({ block, scenarioLabel, interactive = false, onInteract }: { block: BookPageBlock; scenarioLabel?: string; interactive?: boolean; onInteract?: () => void }) {
  if (block.kind === 'IMAGE') return block.src ? <img src={block.src} alt={block.alt || 'Book page asset'} className="h-full w-full object-contain" /> : <span className="text-xs text-slate-400">Choose image</span>;
  if (block.kind === 'SVG') return block.svg ? <div className="h-full w-full [&>svg]:h-full [&>svg]:w-full" dangerouslySetInnerHTML={{ __html: block.svg }} /> : <span className="text-xs text-slate-400">Choose SVG</span>;
  if (block.kind === 'FORMULA') { const formula = block.formula || String.raw`H_2O`; const markup = katex.renderToString(formula, { throwOnError: false, displayMode: true, strict: false }); return <div className="grid h-full w-full place-items-center overflow-auto" role="img" aria-label={`Formula ${formula}`} dangerouslySetInnerHTML={{ __html: markup }} />; }
  if (block.kind === 'INTERACTIVE_EXPERIMENT_LINK') return <button type="button" onDoubleClick={onInteract} onClick={interactive ? onInteract : undefined} className="h-full w-full rounded-xl border border-violet-300 bg-violet-100 p-4 text-left text-violet-950"><b>Interactive Scenario</b><span className="mt-2 block text-sm">{scenarioLabel || block.scenarioId || 'Select a Scenario'}</span><span className="mt-3 block text-xs text-violet-700">{interactive ? 'Open interactive runtime' : 'Double-click to interact'}</span></button>;
  if (block.kind === 'EQUIPMENT_REFERENCE') return <EquipmentPlaceholder block={block} />;
  if (block.kind === 'MATERIAL_REFERENCE') return <MaterialPlaceholder block={block} />;
  if (block.kind === 'REACTION_REFERENCE') return <ReactionPlaceholder block={block} />;
  if (block.kind === 'DATA_WIDGET') return <DataWidgetPlaceholder block={block} />;
  if (block.kind === 'SHAPE') return <div className="h-full w-full rounded" style={{ background: block.fillColor || '#e2e8f0', border: `${block.strokeWidth || 1}px solid ${block.strokeColor || '#94a3b8'}`, borderRadius: block.borderRadius || 0 }} />;
  return <RichTextPreview content={block.text || '<p>Text block</p>'} />;
}

export function BookPageRenderer({ blocks, scenarioName, onInteract, onSelectBlock, className = '' }: { blocks: BookPageBlock[]; scenarioName?: (id: string) => string; onInteract?: (id: string) => void; onSelectBlock?: (id: string) => void; className?: string }) {
  return <div className={`absolute inset-0 overflow-hidden bg-[#fff9e9] text-slate-950 ${className}`}>{[...blocks].sort((a, b) => a.z - b.z).filter(b => b.visible !== false).map((block) => <div key={block.id} onPointerDown={onSelectBlock ? (e) => { e.stopPropagation(); onSelectBlock(block.id); } : undefined} className="absolute overflow-hidden rounded p-2" style={{ left: block.x, top: block.y, width: block.w, height: block.h, zIndex: block.z, opacity: block.opacity ?? 1, transform: block.rotation ? `rotate(${block.rotation}deg)` : undefined }}><BookBlockRenderer block={block} scenarioLabel={block.scenarioId ? scenarioName?.(block.scenarioId) : undefined} interactive={Boolean(onInteract)} onInteract={block.scenarioId ? () => onInteract?.(block.scenarioId!) : undefined} /></div>)}</div>;
}
