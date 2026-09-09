'use client';
import katex from 'katex';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { sanitizeSvgMarkup } from '@/shared/lib/sanitizeSvg';
import { RichTextPreview } from './RichTextPreview';

export type BookBlockKind = 'RICH_TEXT' | 'IMAGE' | 'SVG' | 'FORMULA' | 'INTERACTIVE_EXPERIMENT_LINK';
export type BookPageBlock = { id: string; kind: BookBlockKind; x: number; y: number; w: number; h: number; z: number; translations?: Record<string, unknown>; text?: string; src?: string; assetId?: string; svg?: string; formula?: string; scenarioId?: string };
const object=(value:unknown):JsonObject=>value&&typeof value==='object'&&!Array.isArray(value)?value as JsonObject:{};
const records=(value:unknown):JsonObject[]=>Array.isArray(value)?value.filter(item=>item&&typeof item==='object') as JsonObject[]:[];
const number=(value:unknown,fallback:number)=>Number.isFinite(Number(value))?Number(value):fallback;
export function hydrateBookPageBlocks(raw:unknown,locale='en'):BookPageBlock[]{return records(raw).map((entry,index)=>{const data=object(entry.data),layout=object(data.layout),translations=object(entry.translations),translated=translations[locale]??translations.en??translations.ru??translations.uz,richText=typeof translated==='string'?translated:String(object(translated).content??data.text??'');return{id:String(entry.id??`block-${index}`),kind:String(entry.type??entry.kind??'RICH_TEXT')as BookBlockKind,x:number(layout.x??entry.x,40+(index%2)*310),y:number(layout.y??entry.y,40+Math.floor(index/2)*180),w:number(layout.width??entry.w,280),h:number(layout.height??entry.h,140),z:number(layout.zIndex??entry.z,index+1),translations,text:richText,src:String(data.src??entry.src??''),assetId:String(data.assetId??entry.assetId??''),svg:sanitizeSvgMarkup(String(data.svg??entry.svg??'')),formula:String(data.latex??entry.formula??''),scenarioId:String(data.scenarioId??entry.scenarioId??'')}});}

export function BookBlockRenderer({ block, scenarioLabel, interactive = false, onInteract }: { block: BookPageBlock; scenarioLabel?: string; interactive?: boolean; onInteract?: () => void }) {
  if (block.kind === 'IMAGE') return block.src ? <img src={block.src} alt="Book page asset" className="h-full w-full object-contain"/> : <span>Choose image</span>;
  if (block.kind === 'SVG') return block.svg ? <div className="h-full w-full [&>svg]:h-full [&>svg]:w-full" dangerouslySetInnerHTML={{ __html: block.svg }}/> : <span>Choose SVG</span>;
  if (block.kind === 'FORMULA') { const formula = block.formula || String.raw`H_2O`; const markup = katex.renderToString(formula, { throwOnError: false, displayMode: true, strict: false }); return <div className="grid h-full w-full place-items-center overflow-auto" role="img" aria-label={`Formula ${formula}`} dangerouslySetInnerHTML={{ __html: markup }}/>; }
  if (block.kind === 'INTERACTIVE_EXPERIMENT_LINK') return <button type="button" onDoubleClick={onInteract} onClick={interactive ? onInteract : undefined} className="h-full w-full rounded-xl border border-violet-300 bg-violet-100 p-4 text-left text-violet-950"><b>Interactive Scenario</b><span className="mt-2 block text-sm">{scenarioLabel || block.scenarioId || 'Select a Scenario'}</span><span className="mt-3 block text-xs text-violet-700">{interactive ? 'Open interactive runtime' : 'Double-click to interact'}</span></button>;
  return <RichTextPreview content={block.text || '<p>Text block</p>'}/>;
}

export function BookPageRenderer({ blocks, scenarioName, onInteract, className = '' }: { blocks: BookPageBlock[]; scenarioName?: (id: string) => string; onInteract?: (id: string) => void; className?: string }) {
  return <div className={`relative overflow-hidden bg-[#fff9e9] text-slate-950 ${className}`}>{[...blocks].sort((a, b) => a.z - b.z).map((block) => <div key={block.id} className="absolute overflow-hidden rounded p-2" style={{ left: block.x, top: block.y, width: block.w, height: block.h, zIndex: block.z }}><BookBlockRenderer block={block} scenarioLabel={block.scenarioId ? scenarioName?.(block.scenarioId) : undefined} interactive={Boolean(onInteract)} onInteract={block.scenarioId ? () => onInteract?.(block.scenarioId!) : undefined}/></div>)}</div>;
}
