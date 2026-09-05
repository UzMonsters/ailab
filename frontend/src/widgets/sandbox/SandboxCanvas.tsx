"use client";
/* React refs below are read only inside pointer handlers, never during render. */
/* eslint-disable react-hooks/refs */
import { useLocale, useTranslations } from "next-intl";
import React from 'react';
import { renderEquipmentCanvas } from '@/entities/equipment/ui/EquipmentRendererRegistry';
import { resolvePortScreenPosition, resolvePortWorldPosition } from '@/entities/equipment/lib/equipmentRenderBounds';
import type { Item, Connection, Port as PortType } from '@/widgets/sandbox/types';
import { Copy, Trash2, EyeOff, Link2, Plus, Droplets, RefreshCw, ChevronLeft, ChevronRight } from 'lucide-react';
import { type ReactNode, useMemo, useRef, useState } from 'react';
import { ContextMenu } from './ContextMenu';

import { isVessel } from '@/widgets/sandbox/types';
import { connectionPath } from './ConnectionGeometry';
import type { TransferAnimationKind } from './animationProfiles';

type CanvasSpill = { id: string; amount: number; time: number; x?: number; y?: number; color?: string; sourceId?: string };

export function ToolButton({ label, active, disabled, onClick, children, labelVisible = false, className = "", guideTarget }: { label: string; active?: boolean; disabled?: boolean; onClick: () => void; children: ReactNode; labelVisible?: boolean; className?: string; guideTarget?: string }) { 
  return <button data-help-target={guideTarget} aria-label={label} title={label} disabled={disabled} onPointerDown={(event) => event.stopPropagation()} onClick={onClick} className={`${labelVisible ? 'sandbox-tool-with-label h-10 min-w-[74px] px-2 gap-1.5' : 'h-10 w-10'} shrink-0 flex items-center justify-center rounded-xl transition-colors ${disabled ? 'opacity-30 cursor-not-allowed text-muted-foreground' : active ? 'bg-[var(--primary)] text-white shadow-md' : 'text-muted-foreground hover:bg-muted hover:text-foreground'} ${className}`}>{children}{labelVisible && <span className="sandbox-tool-label text-[10px] font-semibold">{label}</span>}</button>;
}

export function Port({ name, color, onClick }: { name: string; color: string; onClick: () => void }) { 
  return <button aria-label={`${name} port`} title={`${name} port — click to connect`} onPointerDown={(event) => event.stopPropagation()} onClick={(event) => { event.stopPropagation(); onClick(); }} className="group flex min-h-7 items-center gap-1"><span className={`h-5 w-5 shrink-0 rounded-full border-2 border-[var(--card)] shadow-sm ${color}`} /><span className="pointer-events-none absolute right-6 hidden whitespace-nowrap rounded bg-card px-1.5 py-0.5 text-[10px] font-semibold shadow-md group-hover:block">{name}</span></button>; 
}

function itemPortLayout(item: Item, port: { id: string; type: string; x: number; y: number }) {
  return resolvePortScreenPosition({ x: port.x, y: port.y });
}

function portPoint(item: Item, port: { id: string; type: string; x: number; y: number }) {
  const width = item.w * (item.scaleX ?? item.scale);
  const height = item.h * (item.scaleY ?? item.scale);
  return resolvePortWorldPosition(
    { x: port.x, y: port.y },
    { x: item.x, y: item.y, width, height, rotation: item.rotation },
  );
}

function SvgDefs() {
  return <defs><marker id="arrow-cyan" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto"><path d="M 0 0 L 10 5 L 0 10 z" fill="#22D3EE" /></marker></defs>;
}

export function SelectionToolbar({
  item,
  items,
  connections,
  duplicate,
  remove,
  onPourExecute,
  onBeginPour,
  updateItem,
  onMix,
  onClose,
}: {
  item: Item;
  items: Item[];
  connections: Connection[];
  duplicate: () => void;
  remove: () => void;
  onPourExecute?: (sourceId: string, targetId: string, amount: number) => void;
  onBeginPour?: (sourceId: string) => void;
  updateItem?: (id: string, patch: Partial<Item>) => void;
  onMix?: (id: string) => void;
  onClose?: () => void;
}) {
  const ts = useTranslations("sandbox");

  const [actionOffset, setActionOffset] = useState({ x: 0, y: 0 });
  const dragStart = useRef<{ x: number; y: number; offsetX: number; offsetY: number } | null>(null);
  const dragActionStart = (event: React.PointerEvent<HTMLDivElement>) => {
    event.stopPropagation();
    dragStart.current = { x: event.clientX, y: event.clientY, offsetX: actionOffset.x, offsetY: actionOffset.y };
    event.currentTarget.setPointerCapture(event.pointerId);
  };
  const dragActionMove = (event: React.PointerEvent<HTMLDivElement>) => {
    if (!dragStart.current) return;
    setActionOffset({ x: dragStart.current.offsetX + event.clientX - dragStart.current.x, y: dragStart.current.offsetY + event.clientY - dragStart.current.y });
  };
  const stopActionDrag = () => { dragStart.current = null; };
  const actionStyle = { transform: `translateX(calc(-50% + ${actionOffset.x}px)) translateY(${actionOffset.y}px)` };

  const canPour = !!onPourExecute && isVessel(item) && item.liquidLevel > 0;
  
  const canStir = item.capabilities?.stirrable;

  return (
    <div style={actionStyle} className="quick-actions-panel absolute top-[126px] z-[100] left-1/2 flex min-w-[300px] max-w-[520px] max-h-[220px] resize both flex-col overflow-auto rounded-2xl border border-border bg-card/95 p-3 shadow-2xl backdrop-blur-md pointer-events-auto" onPointerDown={e => e.stopPropagation()}>
      <div className="mb-2 flex items-center justify-between gap-4"><div className="cursor-grab select-none text-center text-[11px] font-bold tracking-wide text-muted-foreground uppercase active:cursor-grabbing" onPointerDown={dragActionStart} onPointerMove={dragActionMove} onPointerUp={stopActionDrag}>{ts("quickActions.title")}</div></div>
      <div className="grid w-full grid-cols-4 gap-3">
        
        <button 
          className={`quick-action-button group flex min-w-0 min-h-[58px] aspect-square flex-col items-center justify-center gap-2 rounded-xl border-2 transition-all ${
            canPour 
              ? 'border-cyan-600/30 dark:border-cyan-500/30 bg-cyan-500/10 hover:border-cyan-600 dark:hover:border-cyan-400 hover:bg-cyan-500/20 text-cyan-700 dark:text-cyan-400' 
              : 'border-border bg-muted/50 text-muted-foreground opacity-50 cursor-not-allowed'
          }`}
          onClick={() => {
            if (!canPour) return;
            onBeginPour?.(item.id);
          }}
          disabled={!canPour}
          title={canPour ? ts("quickActions.pourTooltip") : ts("quickActions.noPour")}
        >
          <Droplets size={24} className={canPour ? "transition-transform group-hover:scale-110" : ""} />
          <span className="text-[10px] font-semibold">{ts("quickActions.pour")}</span>
        </button>

        <button 
          data-help-target="action:mix"
          className={`quick-action-button group flex min-w-0 min-h-[58px] aspect-square flex-col items-center justify-center gap-2 rounded-xl border-2 transition-all ${
            canStir 
              ? 'border-amber-600/30 dark:border-amber-500/30 bg-amber-500/10 hover:border-amber-600 dark:hover:border-amber-400 hover:bg-amber-500/20 text-amber-700 dark:text-amber-400' 
              : 'border-border bg-muted/50 text-muted-foreground opacity-50 cursor-not-allowed'
          }`}
          onClick={() => {
            if (!canStir || !onMix || item.operation === 'mixing') return;
            onMix(item.id);
          }}
          disabled={!canStir}
          title={canStir ? "Смешивать содержимое" : "Этот сосуд нельзя перемешивать"}
        >
          <RefreshCw size={24} className={canStir && item.operation === 'mixing' ? 'animate-spin' : canStir ? 'transition-transform group-hover:scale-110' : ''} />
          <span className="text-[10px] font-semibold">{item.operation === 'mixing' ? (ts.has("quickActions.mixing") ? ts("quickActions.mixing") : "Mixing…") : ts("quickActions.mix")}</span>
        </button>

        <button 
          className="quick-action-button group flex min-w-0 min-h-[58px] aspect-square flex-col items-center justify-center gap-2 rounded-xl border-2 border-slate-400/50 dark:border-slate-500/30 bg-slate-500/10 text-slate-700 dark:text-muted-foreground hover:border-slate-500 dark:hover:border-slate-400 hover:bg-slate-500/20 hover:text-foreground transition-all"
          onClick={duplicate}
          title="Дублировать объект"
        >
          <Copy size={24} className="transition-transform group-hover:scale-110" />
          <span className="text-[10px] font-semibold">{ts("quickActions.copy")}</span>
        </button>

        <button 
          className="quick-action-button group flex min-w-0 min-h-[58px] aspect-square flex-col items-center justify-center gap-1 rounded-xl border-2 border-red-600/30 dark:border-red-500/30 bg-red-500/10 text-red-600 dark:text-red-400 hover:border-red-600 dark:hover:border-red-400 hover:bg-red-500/20 hover:text-red-700 dark:hover:text-red-300 transition-all"
          onClick={remove}
          title={ts("quickActions.deleteTooltip")}
        >
          <Trash2 size={18} className="transition-transform group-hover:scale-110" />
          <span className="text-[9px] font-semibold">{ts("quickActions.delete")}</span>
        </button>
      </div>
      
    </div>
  );
}

interface SandboxCanvasProps {
  canvasRef: React.RefObject<HTMLDivElement>;
  zoom: number;
  pan: { x: number; y: number };
  setPan: (pan: { x: number; y: number }) => void;
  tool: 'select' | 'pan' | 'connect';
  items: Item[];
  connections: Connection[];
  selectedId: string | null;
  selectedIds: Set<string>;
  marquee: { startX: number; startY: number; currentX: number; currentY: number } | null;
  setMarquee: (marquee: { startX: number; startY: number; currentX: number; currentY: number } | null) => void;
  connectSource: string | null;
  connectSourcePort: { itemId: string; portId: string } | null;
  connectionSnap: { itemId: string; portId: string; x: number; y: number } | null;
  portCompatibility: Record<string, 'compatible' | 'adapter' | 'incompatible'>;
  connectionPointer: { x: number; y: number } | null;
  pourAnimation: { sourceId: string; targetId: string; amountMl: number; kind: TransferAnimationKind; durationMs: number; arcLift: number; streamWidth: number } | null;
  spillAnimation?: string | null;
  spills?: CanvasSpill[];
  centers: Map<string, { x: number; y: number }>;
  setSelectedId: (id: string | null) => void;
  setSelectedIds: (ids: Set<string>) => void;
  setConnectSource: (id: string | null) => void;
  setConnectSourcePort: (source: { itemId: string; portId: string } | null) => void;
  onPortPointerDown: (itemId: string, portId: string) => void;
  onPortPointerUp: (itemId: string, portId: string) => void;
  onPortPointerEnter: (itemId: string, portId: string, point: { x: number; y: number }) => void;
  setTool: (tool: 'select' | 'pan' | 'connect') => void;
  cancelConnection: (preserveTool?: boolean) => void;
  onPointerMove: (event: React.PointerEvent<HTMLDivElement>) => void;
  endDrag: () => void;
  onPointerDown: (event: React.PointerEvent<HTMLDivElement>, id: string) => void;
  onCanvasPointerDown: (event: React.PointerEvent<HTMLDivElement>) => void;
  isPanning?: boolean;
  duplicate: () => void;
  hide?: () => void;
  remove: () => void;
  emptyItem?: (id: string) => void;
  setPourSource?: (id: string | null) => void;
  pourSource?: string | null;
  pourAmount?: number;
  setPourAmount?: (amount: number) => void;
  onPourExecute?: (sourceId: string, targetId: string, amount: number) => void;
  temperatureConnected: (id: string) => boolean;
  temperatureReading?: (id: string) => number | null;
  collisionItemId: string | null;
  showGrid: boolean;
  updateItem?: (id: string, patch: Partial<Item>) => void;
  onMix?: (id: string) => void;
  selectedConnectionId: string | null;
  onConnectionSelect: (id: string | null) => void;
  onConnectionDelete?: (id: string) => void;
  onRoutePointMove: (id: string, points: Array<{ x: number; y: number }>) => void;
  onResize: (id: string, scaleX: number, scaleY: number) => void;
  onNudge: (id: string, dx: number, dy: number) => void;
  helpActive?: boolean;
  helpTargets?: string[];
  onSimulationStart?: () => void;
}

export function SandboxCanvas({
  canvasRef,
  zoom,
  pan,
  setPan,
  tool,
  items,
  connections,
  selectedId,
  selectedIds,
  marquee,
  setMarquee,
  connectSource,
  connectSourcePort,
  connectionSnap,
  portCompatibility,
  connectionPointer,
  pourAnimation,
  spillAnimation,
  spills = [],
  centers,
  setSelectedId,
  setSelectedIds,
  setConnectSource,
  setConnectSourcePort,
  onPortPointerDown,
  onPortPointerUp,
  onPortPointerEnter,
  setTool,
  cancelConnection,
  onPointerMove,
  endDrag,
  onPointerDown,
  onCanvasPointerDown,
  isPanning = false,
  duplicate,
  hide,
  remove,
  emptyItem,
  setPourSource,
  pourSource,
  pourAmount = 10,
  setPourAmount,
  onPourExecute,
  temperatureConnected,
  temperatureReading,
  collisionItemId,
  showGrid,
  updateItem,
  onMix,
  selectedConnectionId,
  onConnectionSelect,
  onConnectionDelete,
  onRoutePointMove,
  onResize,
  onNudge,
  helpActive = false,
  helpTargets = [],
  onSimulationStart
}: SandboxCanvasProps) {
  const ts = useTranslations("sandbox");
  const locale = useLocale();

  const [contextMenu, setContextMenu] = useState<{ x: number; y: number; itemId: string } | null>(null);
  const [routeDraft, setRouteDraft] = useState<{ id: string; points: Array<{ x: number; y: number }> } | null>(null);
  const [resizeDraft, setResizeDraft] = useState<{ id: string; scaleX: number; scaleY: number } | null>(null);
  const [rotationDraft, setRotationDraft] = useState<{ id: string; rotation: number } | null>(null);
  const [miniMapOpen, setMiniMapOpen] = useState(true);
  const [canvasSize, setCanvasSize] = useState({ width: 800, height: 600 });
  const [spillNow, setSpillNow] = useState(() => Date.now());
  const resizeRef = React.useRef<{ id: string; startX: number; startY: number; startScaleX: number; startScaleY: number; minScale: number; maxScale: number; width: number; height: number } | null>(null);
  const rotationRef = React.useRef<{ id: string; centerX: number; centerY: number; pointerAngle: number; startRotation: number } | null>(null);
  const connectionDragRef = React.useRef<{ id: string; startX: number; startY: number; points: Array<{ x: number; y: number }> } | null>(null);
  React.useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const updateSize = () => setCanvasSize({ width: canvas.clientWidth, height: canvas.clientHeight });
    updateSize();
    const observer = new ResizeObserver(updateSize);
    observer.observe(canvas);
    return () => observer.disconnect();
  }, [canvasRef]);
  React.useEffect(() => {
    const timer = window.setInterval(() => setSpillNow(Date.now()), 80);
    return () => window.clearInterval(timer);
  }, []);
  const miniMapBounds = useMemo(() => {
    const maxX = Math.max(1000, ...items.map((item) => item.x + item.w * (item.scaleX ?? item.scale)));
    const maxY = Math.max(700, ...items.map((item) => item.y + item.h * (item.scaleY ?? item.scale)));
    return { maxX, maxY };
  }, [items]);
  const activePourSource = pourSource ? items.find((item) => item.id === pourSource) : undefined;
  const navigateMiniMap = React.useCallback((event: React.PointerEvent<SVGSVGElement>) => {
    const bounds = event.currentTarget.getBoundingClientRect();
    const worldX = ((event.clientX - bounds.left) / bounds.width) * miniMapBounds.maxX;
    const worldY = ((event.clientY - bounds.top) / bounds.height) * miniMapBounds.maxY;
    setPan({ x: canvasSize.width / (2 * zoom) - worldX, y: canvasSize.height / (2 * zoom) - worldY });
    event.currentTarget.setPointerCapture?.(event.pointerId);
  }, [canvasSize.height, canvasSize.width, miniMapBounds.maxX, miniMapBounds.maxY, setPan, zoom]);

  const handleContextMenuAction = (action: string, itemId: string) => {
    if (action === 'empty' && emptyItem) {
      emptyItem(itemId);
    } else if (action === 'delete') {
      setSelectedId(itemId);
      remove(); // will remove currently selected
    } else if (action === 'toggle_stopper') {
      const item = items.find(i => i.id === itemId);
      if (item) updateItem(itemId, { sealed: !item.sealed });
    }
  };

  return (
    <div 
      ref={canvasRef} 
      onPointerDown={(e) => {
        if (pourSource) setPourSource?.(null);
        setSelectedId(null);
        // Pan must stay active after a canvas click. Previously this reset the
        // connection state unconditionally, which also forced the tool back to Select.
        if (tool === 'connect') cancelConnection(false);
        onCanvasPointerDown(e);
      }}
      onPointerMove={onPointerMove} 
      onPointerUp={(e) => {
          if (marquee) setMarquee(null);
          if (tool === 'connect' && connectionSnap) {
          onPortPointerUp(connectionSnap.itemId, connectionSnap.portId);
        }
        endDrag();
        if (e.currentTarget.hasPointerCapture(e.pointerId)) {
          e.currentTarget.releasePointerCapture(e.pointerId);
        }
      }} 
      onPointerCancel={endDrag}
      onContextMenu={(event) => {
        event.preventDefault();
        setSelectedId(null);
        cancelConnection(true);
      }}
       className={`sandbox-canvas relative h-full w-full overflow-hidden ${tool === 'pan' ? `pan-mode ${isPanning ? 'is-panning' : ''} cursor-move` : tool === 'connect' ? 'cursor-crosshair' : 'cursor-default'}`}
      style={{ backgroundColor: "var(--background)", cursor: tool === 'pan' ? 'move' : tool === 'connect' ? 'crosshair' : 'default', backgroundImage: showGrid ? 'linear-gradient(to right, rgba(148,163,184,0.22) 1px, transparent 1px), linear-gradient(to bottom, rgba(148,163,184,0.22) 1px, transparent 1px), linear-gradient(to right, rgba(148,163,184,0.36) 1px, transparent 1px), linear-gradient(to bottom, rgba(148,163,184,0.36) 1px, transparent 1px)' : 'none', backgroundSize: `${20 * zoom}px ${20 * zoom}px, ${20 * zoom}px ${20 * zoom}px, ${100 * zoom}px ${100 * zoom}px, ${100 * zoom}px ${100 * zoom}px`, backgroundPosition: `${pan.x * zoom}px ${pan.y * zoom}px, ${pan.x * zoom}px ${pan.y * zoom}px, ${pan.x * zoom}px ${pan.y * zoom}px, ${pan.x * zoom}px ${pan.y * zoom}px`, touchAction: 'none' }}
    >
      <svg width="0" height="0" style={{ position: 'absolute' }} aria-hidden="true">
        <SvgDefs />
      </svg>
      {activePourSource && (
        <div className="sandbox-pour-guidance pointer-events-auto absolute left-1/2 top-5 z-[110] flex min-w-[330px] -translate-x-1/2 items-center gap-3 rounded-2xl border border-cyan-300/40 bg-card/95 px-4 py-2 text-xs font-semibold text-foreground shadow-[0_12px_32px_rgba(6,182,212,.2)] backdrop-blur-xl">
          <Droplets size={16} className="text-cyan-400" />
          <label className="min-w-0 flex-1"><span className="flex justify-between gap-3"><span>Объём переливания</span><b className="font-mono text-cyan-300">{Math.min(pourAmount,activePourSource.volumeMl).toFixed(0)} мл</b></span><input aria-label="Объём переливания в миллилитрах" type="range" min="1" max={Math.max(1,Math.floor(activePourSource.volumeMl))} value={Math.min(pourAmount,Math.max(1,activePourSource.volumeMl))} onChange={event=>setPourAmount?.(Number(event.target.value))} className="mt-1 w-full accent-cyan-400"/></label>
          <button type="button" className="ml-1 grid h-6 w-6 place-items-center rounded-full bg-muted text-muted-foreground hover:text-foreground" onClick={(event) => { event.stopPropagation(); setPourSource?.(null); }} aria-label="Отменить переливание">×</button>
        </div>
      )}
      {pourAnimation && (()=>{const source=items.find(item=>item.id===pourAnimation.sourceId);const target=items.find(item=>item.id===pourAnimation.targetId);if(!source||!target||source.id===target.id)return null;const x1=(source.x+source.w/2+pan.x)*zoom;const y1=(source.y+source.h*.45+pan.y)*zoom;const x2=(target.x+target.w/2+pan.x)*zoom;const y2=(target.y+target.h*.18+pan.y)*zoom;const color=source.material?.color??'#22d3ee';const path=`M ${x1} ${y1} Q ${(x1+x2)/2} ${Math.min(y1,y2)-pourAnimation.arcLift} ${x2} ${y2}`;return <svg className="pointer-events-none absolute inset-0 z-40 h-full w-full overflow-visible" aria-label={`${pourAnimation.kind} liquid transfer`}><path d={path} fill="none" stroke={color} strokeWidth={Math.max(2,pourAnimation.streamWidth*zoom)} strokeLinecap="round" className={`sandbox-transfer-stream sandbox-transfer-${pourAnimation.kind}`} style={{animationDuration:`${Math.max(280,pourAnimation.durationMs/3)}ms, ${pourAnimation.durationMs}ms`}}/>{pourAnimation.kind==='pipette'&&<><circle cx={x2} cy={y2-24} r="4" fill={color} className="sandbox-transfer-drop"/><circle cx={x2} cy={y2-42} r="2.5" fill={color} className="sandbox-transfer-drop animation-delay-300"/></>}<circle cx={x2} cy={y2} r="10" fill="none" stroke={color} strokeWidth="3" className="sandbox-transfer-ripple"/></svg>})()}
      {/* Layer 1: Connection lines (SVG, behind everything) */}
      <svg className="pointer-events-none absolute inset-0 h-full w-full z-0" style={{ overflow: 'visible' }}>
        <defs><marker id="arrow-cyan" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#22D3EE" /></marker><marker id="arrow-violet" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#A78BFA" /></marker><marker id="arrow-orange" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#FB923C" /></marker><marker id="arrow-emerald" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="#34D399" /></marker><marker id="arrow-glass" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse"><path d="M 0 0 L 10 5 L 0 10 z" fill="rgba(255,255,255,0.7)" /></marker></defs>{connections.map((link) => { const fromItem = items.find((item) => item.id === link.from); const toItem = items.find((item) => item.id === link.to); const fromPort = fromItem?.ports.find((port) => port.id === link.fromPort);
          const toPort = toItem?.ports.find((port) => port.id === link.toPort);
          // Do not draw stale connections from item centers: links must attach
          // to actual ports even while a catalog/schema update is in flight.
          const from = fromItem && fromPort ? portPoint(fromItem, fromPort) : null;
          const to = toItem && toPort ? portPoint(toItem, toPort) : null;
          if (!from || !to) return null;
          
          const x1 = (from.x + pan.x) * zoom;
          const y1 = (from.y + pan.y) * zoom;
          const x2 = (to.x + pan.x) * zoom;
          const y2 = (to.y + pan.y) * zoom;
          const visibleRoutePoints = routeDraft?.id === link.id ? routeDraft.points : (link.routePoints ?? []);
          const routeWorld = visibleRoutePoints.length > 0 ? [from, ...visibleRoutePoints, to] : [from, { x: (from.x + to.x) / 2, y: from.y }, { x: (from.x + to.x) / 2, y: to.y }, to];
          const routeScreen = routeWorld.map((point) => ({ x: (point.x + pan.x) * zoom, y: (point.y + pan.y) * zoom }));
          const path = connectionPath({ points: routeScreen });
          
          let stroke = 'var(--sandbox-link-color)';
          let strokeWidth = "2.5";
          let strokeDasharray = "none";
          let markerId = "url(#arrow-cyan)";
          if (link.medium === 'liquid') { stroke = '#22D3EE'; strokeWidth = "2"; strokeDasharray = "8 5"; }
          if (link.medium === 'gas') { stroke = '#93C5FD'; strokeWidth = "2.5"; strokeDasharray = "7 5"; markerId = "url(#arrow-cyan)"; }
          else if (link.medium === 'thermal') { stroke = '#FB923C'; strokeWidth = "2.5"; markerId = "url(#arrow-orange)"; }
          else if (link.medium === 'electrical') { stroke = '#34D399'; strokeWidth = "2.5"; markerId = "url(#arrow-emerald)"; }
          else if (link.medium === 'sensor') { stroke = '#60A5FA'; strokeWidth = "2.5"; strokeDasharray = "4 2"; markerId = "url(#arrow-cyan)"; }
          else if (link.medium === 'mechanical') { stroke = '#94A3B8'; strokeWidth = "3"; markerId = "url(#arrow-glass)"; }
          else if (link.connector === 'glass-tube' || link.connector === 'adapter') { stroke = 'var(--sandbox-link-color)'; strokeWidth = "2.5"; markerId = "url(#arrow-glass)"; }
          
          let labelText = 'Соединение';
          if (link.medium === 'gas') labelText = 'Пар / газ';
          else if (link.medium === 'thermal') labelText = 'Нагрев';
          else if (link.medium === 'electrical') labelText = 'Питание';
          else if (link.medium === 'sensor') labelText = 'Сенсор';
          else if (link.medium === 'liquid') {
            labelText = link.fromPort === 'condensate-out' ? 'Конденсат' : link.toPort === 'coolant-in' || link.fromPort === 'coolant-out' ? 'Охлаждение' : 'Переливание';
          }
          else if (link.medium === 'mechanical') labelText = 'Крепление';
          
          const midPointIndex = Math.floor(routeScreen.length / 2);
          const p1 = routeScreen[Math.max(0, midPointIndex - 1)];
          const p2 = routeScreen[midPointIndex];
          const midX = (p1.x + p2.x) / 2;
          const midY = (p1.y + p2.y) / 2 - 10;
          
          return (
            <g key={link.id} className={selectedConnectionId === link.id ? "drop-shadow-[0_0_8px_rgba(255,255,255,0.4)]" : ""}>
              <path d={path} stroke={stroke} strokeWidth={selectedConnectionId === link.id ? '4' : strokeWidth} strokeDasharray={strokeDasharray !== "none" ? strokeDasharray : undefined} fill="none"  className="pointer-events-auto cursor-pointer transition-all" onPointerDown={(event) => { event.stopPropagation(); onConnectionSelect(link.id); connectionDragRef.current = { id: link.id, startX: event.clientX, startY: event.clientY, points: [...visibleRoutePoints] }; event.currentTarget.setPointerCapture(event.pointerId); }} onPointerMove={(event) => { const drag = connectionDragRef.current; if (!drag || drag.id !== link.id || !event.currentTarget.hasPointerCapture(event.pointerId)) return; const dx = (event.clientX - drag.startX) / zoom; const dy = (event.clientY - drag.startY) / zoom; setRouteDraft({ id: link.id, points: drag.points.map((point) => ({ x: point.x + dx, y: point.y + dy })) }); }} onPointerUp={(event) => { const drag = connectionDragRef.current; if (drag?.id === link.id) { const dx = (event.clientX - drag.startX) / zoom; const dy = (event.clientY - drag.startY) / zoom; onRoutePointMove(link.id, drag.points.map((point) => ({ x: point.x + dx, y: point.y + dy }))); } connectionDragRef.current = null; setRouteDraft(null); event.currentTarget.releasePointerCapture(event.pointerId); }} onClick={(event) => { event.stopPropagation(); onConnectionSelect(link.id); }} />
              <circle cx={x1} cy={y1} r={selectedConnectionId === link.id ? "5" : "4"} fill={stroke} stroke="#06080c" strokeWidth="1.5" />
              <circle cx={x2} cy={y2} r={selectedConnectionId === link.id ? "5" : "4"} fill={stroke} stroke="#06080c" strokeWidth="1.5" />
              <text x={midX} y={midY} textAnchor="middle" fill="rgba(255,255,255,0.8)" fontSize="10" fontWeight="bold" className="pointer-events-none drop-shadow-md">{labelText}</text>
              
              {selectedConnectionId === link.id && (
                  <foreignObject x={midX - 16} y={midY + 5} width="32" height="32" className="overflow-visible">
                    <button 
                      type="button"
                      onPointerDown={e => e.stopPropagation()} 
                      onClick={(e) => { e.stopPropagation(); onConnectionDelete?.(link.id); onConnectionSelect(null); }}
                      className="pointer-events-auto flex h-7 w-7 items-center justify-center rounded-full bg-red-500 text-white shadow-lg transition-transform hover:scale-110 hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-400"
                      title={ts("quickActions.deleteTooltip")}
                    >
                      <Trash2 size={14} />
                    </button>
                  </foreignObject>
                )}

              {selectedConnectionId === link.id && visibleRoutePoints.map((point, index) => <circle key={`${link.id}-bend-${index}`} cx={(point.x + pan.x) * zoom} cy={(point.y + pan.y) * zoom} r="4" fill="#A78BFA" stroke="#fff" strokeWidth="1.5" className="pointer-events-auto cursor-move" onPointerDown={(event) => { event.stopPropagation(); setRouteDraft({ id: link.id, points: [...visibleRoutePoints] }); event.currentTarget.setPointerCapture(event.pointerId); }} onPointerMove={(event) => { if (!event.currentTarget.hasPointerCapture(event.pointerId)) return; const bounds = canvasRef.current?.getBoundingClientRect(); if (!bounds) return; const next = [...visibleRoutePoints]; next[index] = { x: (event.clientX - bounds.left - pan.x) / zoom, y: (event.clientY - bounds.top - pan.y) / zoom }; setRouteDraft({ id: link.id, points: next }); }} onPointerUp={(event) => { event.stopPropagation(); if (routeDraft?.id === link.id) onRoutePointMove(link.id, routeDraft.points); setRouteDraft(null); event.currentTarget.releasePointerCapture(event.pointerId); }} />)}
            </g>
          );
        })}
        {connectSource && connectionPointer && (() => {
          let fromWorld = centers.get(connectSource);
          if (connectSourcePort) {
            const item = items.find(i => i.id === connectSourcePort.itemId);
            const port = item?.ports.find(p => p.id === connectSourcePort.portId);
            if (item && port) {
              fromWorld = portPoint(item, port);
            }
          }
          if (!fromWorld) return null;
          
          const x1 = (fromWorld.x + pan.x) * zoom;
          const y1 = (fromWorld.y + pan.y) * zoom;
          
          const toWorld = connectionSnap ?? connectionPointer;
          const x2 = (toWorld.x + pan.x) * zoom;
          const y2 = (toWorld.y + pan.y) * zoom;
          
          const path = connectionPath({ points: [{x: x1, y: y1}, {x: x2, y: y2}] });
          
          const isCompatible = connectionSnap ? portCompatibility[`${connectionSnap.itemId}:${connectionSnap.portId}`] === 'compatible' : true;
          const stroke = connectionSnap ? (isCompatible ? '#34D399' : '#EF4444') : 'var(--primary-bright)';
          return (
            <g key="active-drag-line">
              <path d={path} stroke={stroke} strokeWidth="3" fill="none" strokeDasharray="5 5" opacity=".95" />
              {connectionSnap && (
                <g transform={`translate(${x2}, ${y2})`}>
                  <circle r="12" fill={stroke} fillOpacity="0.25" stroke={stroke} strokeWidth="2" className="animate-ping" />
                  <circle r="6" fill={stroke} stroke="#ffffff" strokeWidth="2" />
                </g>
              )}
            </g>
          );
        })()}
      </svg>
      {/* Persistent liquid left after overflow or a shattered vessel. */}
      {spills.map((spill, index) => {
        const age = Math.max(0, spillNow - spill.time);
        if (age >= 3000) return null;
        const size = Math.min(104, Math.max(38, 26 + Math.sqrt(Math.max(0, spill.amount)) * 8));
        const left = ((spill.x ?? 0) + pan.x) * zoom - size / 2;
        const top = ((spill.y ?? 0) + pan.y) * zoom - size / 3;
        const color = spill.color || '#22D3EE';
        const progress = age / 3000;
        return <div key={spill.id} className="pointer-events-none absolute z-[5] rounded-full border border-white/20 shadow-[0_5px_16px_rgba(8,47,73,.28)]" title={`Разлито: ${spill.amount.toFixed(1)} мл`} style={{ left, top, width: size, height: Math.max(14, size * .34), background: `radial-gradient(ellipse at 48% 42%, ${color}cc 0%, ${color}66 52%, transparent 74%)`, transform: `rotate(${(index * 23) % 35 - 17}deg) scale(${1 + progress * .18})`, opacity: .9 * (1 - progress) }} />;
      })}
      {/* Layer 2: Equipment items */}
          {/* Group Outline (Bounding Box) */}
          {selectedIds.size > 1 && (() => {
             let minX = Infinity, minY = Infinity, maxX = -Infinity, maxY = -Infinity;
             selectedIds.forEach(id => {
                const item = items.find(i => i.id === id);
                if (item) {
                   const itemWidth = item.w * (item.scaleX ?? item.scale);
                   const itemHeight = item.h * (item.scaleY ?? item.scale);
                   if (item.x < minX) minX = item.x;
                   if (item.y < minY) minY = item.y;
                   if (item.x + itemWidth > maxX) maxX = item.x + itemWidth;
                   if (item.y + itemHeight > maxY) maxY = item.y + itemHeight;
                }
             });
             if (minX === Infinity) return null;
             return (
               <div 
                 className="absolute border-2 border-dashed border-[var(--primary)] pointer-events-none z-10"
                 style={{
                   left: minX * zoom + pan.x * zoom - 4,
                   top: minY * zoom + pan.y * zoom - 4,
                   width: (maxX - minX) * zoom + 8,
                   height: (maxY - minY) * zoom + 8,
                   borderRadius: '12px'
                 }}
               />
             );
          })()}
          {items.filter(item => !item.hidden).map((item) => {

        // Items themselves apply pan/zoom to coordinate rendering
        const screenX = (item.x + pan.x) * zoom;
        const screenY = (item.y + pan.y) * zoom;
        const renderScaleX = resizeDraft?.id === item.id ? resizeDraft.scaleX : (item.scaleX ?? item.scale);
        const renderScaleY = resizeDraft?.id === item.id ? resizeDraft.scaleY : (item.scaleY ?? item.scale);
        const screenW = item.w * renderScaleX * zoom;
        const screenH = item.h * renderScaleY * zoom;
        const measuredTemperature = item.type === 'thermometer' ? temperatureReading?.(item.id) : null;
        const displayedTemperature = measuredTemperature ?? item.measuredTemperatureC;
        const displayRotation = rotationDraft?.id === item.id ? rotationDraft.rotation : item.rotation;
        const visibleLiquid = item.contents.find((content) => content.phase === 'liquid' || content.phase === 'aqueous');
          const visibleSolid = item.contents.find((content) => content.phase === 'solid');
          const isVaporizing = item.contents.some((content) => content.phase === 'gas') || Boolean(visibleLiquid && item.temperature >= Number(item.material?.boilingPointC ?? 100));
          const isPourTarget = Boolean(activePourSource && item.id !== activePourSource.id && isVessel(item) && !item.broken && item.volumeMl < (item.capacityMl ?? Number.POSITIVE_INFINITY));
          const isPourDimmed = Boolean(activePourSource && item.id !== activePourSource.id && !isPourTarget);
        
        const isHelpTarget = helpActive && helpTargets.includes(item.type);
        return (
        <div 
          key={item.id} 
          data-sandbox-item={item.id}
          data-scenario-alias={String(item.metadata?.scenarioAlias ?? '') || undefined}
          title={`${item.name}\nОбъём: ${item.volumeMl?.toFixed(1) ?? '0.0'} / ${item.capacityMl ?? '-'} мл\nСодержимое: ${item.contents?.length ? item.contents.map(c => c.name ?? c.formula).join(', ') : 'пусто'}`}
          onPointerDown={(event) => {
            if (activePourSource) {
              event.preventDefault();
              event.stopPropagation();
              if (isPourTarget && onPourExecute) {
                onPourExecute(activePourSource.id, item.id, Math.min(pourAmount, activePourSource.volumeMl));
                setPourSource?.(null);
                setSelectedIds(new Set([item.id]));
              }
              return;
            }
            onPointerDown(event, item.id);
          }}
          onPointerMove={onPointerMove}
          onPointerUp={(event) => { 
            event.stopPropagation(); 
            if (event.currentTarget.hasPointerCapture(event.pointerId)) {
              event.currentTarget.releasePointerCapture(event.pointerId);
            }
            endDrag(); 
          }}
          onPointerCancel={(event) => { 
            event.stopPropagation(); 
            if (event.currentTarget.hasPointerCapture(event.pointerId)) {
              event.currentTarget.releasePointerCapture(event.pointerId);
            }
            endDrag(); 
          }}
          onContextMenu={(event) => {
            event.preventDefault();
            event.stopPropagation();
            setContextMenu({ x: event.clientX, y: event.clientY, itemId: item.id });
          }}
          style={{ left: screenX, top: screenY, width: screenW, height: screenH, transform: `rotate(${displayRotation}deg)`, touchAction: 'none' }}
          data-help-target={item.type}
          className={`group absolute rounded-xl p-1 select-none outline-none focus:ring-2 focus:ring-[var(--primary)] ${activePourSource ? (isPourTarget ? 'sandbox-pour-target z-30 cursor-pointer ring-4 ring-cyan-300 bg-cyan-300/10 shadow-[0_0_34px_rgba(34,211,238,.72)]' : isPourDimmed ? 'opacity-30 saturate-50' : 'z-20 ring-2 ring-cyan-400/50') : tool === 'pan' ? 'cursor-move' : tool === 'connect' ? 'cursor-crosshair' : 'cursor-default'} ${selectedId === item.id ? 'z-20 ring-2 ring-[var(--primary)]' : 'z-10 hover:ring-1 hover:ring-[var(--primary)]/60'} ${collisionItemId === item.id ? 'ring-2 ring-red-400 bg-red-500/10' : ''} ${isHelpTarget ? 'help-arrow-target ring-2 ring-violet-300 shadow-[0_0_20px_rgba(156,107,255,.55)] animate-pulse' : ''}`}
        >
          <span className={`equipment-art pointer-events-none absolute inset-0 grid place-items-center rounded-xl ${item.operation === 'mixing' ? 'sandbox-vessel-mixing' : ''} ${pourAnimation?.sourceId===item.id?`sandbox-pour-source sandbox-pour-${pourAnimation.kind}`:''} ${pourAnimation?.targetId===item.id?'sandbox-pour-receiver':''}`}>
            {renderEquipmentCanvas(item.type, { type: item.type, width: screenW, height: screenH, size: Math.min(screenW, screenH), liquidLevel: visibleLiquid ? item.liquidLevel : 0, volumeMl: item.volumeMl, capacityMl: item.capacityMl, liquidColor: item.material?.color ?? visibleLiquid?.color, hasGas: isVaporizing, hasSolid: !!visibleSolid, solidColor: visibleSolid?.color, massG: item.massG, operation: item.operation, temperature: item.temperature, connected: temperatureConnected(item.id), sealed: item.sealed, broken: item.broken })}
          </span>
          {isVaporizing && !item.sealed && !item.broken && <span className="sandbox-steam pointer-events-none absolute left-1/2 top-0 z-20 h-20 w-16 -translate-x-1/2 -translate-y-12" aria-label="Пар"><i/><i/><i/></span>}
          {item.integrity && ['microcracked', 'cracked', 'leaking', 'shattered'].includes(item.integrity) && (
            <svg className="pointer-events-none absolute inset-0 h-full w-full" viewBox="0 0 100 100" preserveAspectRatio="none" aria-label="Glass crack">
              <path d="M48 8 L43 30 L51 43 L38 62 L44 92 M44 35 L24 28 M49 43 L70 31 M39 62 L21 77" fill="none" stroke="rgba(255,255,255,.82)" strokeWidth={item.integrity === 'microcracked' ? 0.7 : 1.2} strokeDasharray={item.integrity === 'microcracked' ? '5 5' : undefined} />
              {['cracked', 'leaking', 'shattered'].includes(item.integrity) && <path d="M51 43 L78 58 L68 82 M38 62 L56 72" fill="none" stroke="rgba(248,113,113,.8)" strokeWidth=".9" />}
            </svg>
          )}
          <div className="pointer-events-none absolute -bottom-12 left-1/2 z-20 flex min-w-[110px] -translate-x-1/2 flex-col items-center rounded-md border border-border/60 bg-card/90 px-1.5 py-1 text-center text-[9px] font-medium leading-tight text-foreground shadow-sm backdrop-blur-sm">
            <span className="max-w-[180px] truncate font-semibold text-foreground">{item.name}</span>
            <span className="text-[8px] text-muted-foreground">id: {item.id.slice(0, 8)}</span>
            {isVessel(item) && !item.broken && (
              <span className="mt-0.5 text-[9px] font-bold text-cyan-300">
                Объём: {item.volumeMl?.toFixed(1) ?? '0.0'} мл
              </span>
            )}
          </div>
                    {item.type === 'thermometer' && (
            <span className="absolute -top-8 left-1/2 -translate-x-1/2 flex items-center gap-1 whitespace-nowrap rounded-md bg-slate-900/90 px-2 py-1 text-xs font-bold text-foreground shadow-md backdrop-blur-sm border border-border">
              {temperatureConnected(item.id) && <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#34D399" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>}
              {measuredTemperature === null ? 'No valid measurement' : item.measurementStatus === 'OVER RANGE' ? 'OVER RANGE' : `${displayedTemperature.toFixed(1)}°C`}
            </span>
          )}
          {isVessel(item) && temperatureConnected(item.id) && temperatureReading?.(item.id) !== null && (
            <span className="absolute bottom-1 left-1/2 -translate-x-1/2 rounded bg-card/90 px-1 text-[10px] font-semibold">{temperatureReading(item.id)?.toFixed(1)}°C</span>
          )}
          {!item.broken && item.integrity && item.integrity !== 'intact' && (
            <span className={`pointer-events-none absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap rounded-lg border px-2.5 py-0.5 text-[10px] font-extrabold shadow-xl backdrop-blur-sm ${item.integrity === 'stressed' ? 'border-amber-400/50 bg-amber-500/20 text-amber-200' : 'border-orange-400/60 bg-orange-500/25 text-orange-200'}`}>
              {item.integrity.toUpperCase()}
            </span>
          )}
          {item.integrity === 'leaking' && item.volumeMl > 0 && (
            <span className="pointer-events-none absolute bottom-0 left-1/2 h-8 w-1 -translate-x-1/2 translate-y-6 rounded-full bg-cyan-300/80 shadow-[0_4px_10px_rgba(34,211,238,.6)] animate-pulse" aria-label="Liquid leaking" />
          )}
          {item.broken && (
            <span className="pointer-events-none absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap rounded-lg border border-red-500/50 bg-red-600/90 px-2.5 py-0.5 text-[10px] font-extrabold text-foreground shadow-xl backdrop-blur-sm animate-pulse">
              ⚠️ {item.integrity === 'shattered' ? 'РАЗБИТ (SHATTERED)' : 'ПОВРЕЖДЁН'}
            </span>
          )}
          {(item.overflowing || (item.lastOverflowAt && spillNow - item.lastOverflowAt < 3500)) && (
            <span className="pointer-events-none absolute -top-14 left-1/2 -translate-x-1/2 whitespace-nowrap rounded-lg border border-cyan-300/50 bg-cyan-950/90 px-2.5 py-0.5 text-[10px] font-extrabold text-cyan-100 shadow-xl">РАЗЛИВ · ПЕРЕПОЛНЕНИЕ</span>
          )}
          {item.attachedTo && (
            <span className="pointer-events-none absolute -top-7 right-0 whitespace-nowrap rounded-lg border border-orange-500/30 bg-orange-500/15 px-2 py-0.5 text-[9px] font-bold text-orange-400 shadow-md backdrop-blur-sm">
              Attached &middot; Heat link
            </span>
          )}

          {/* Floating Controls for Hotplate, Burner, pH Meter on Sandbox Canvas (to the left of item) */}
          {(item.type === 'hotplate' || item.type === 'burner' || item.type === 'phmeter') && selectedIds.has(item.id) && updateItem && (
            <div
              className="absolute -left-52 top-1/2 -translate-y-1/2 z-50 flex items-center gap-2.5 rounded-2xl border border-border bg-card/95 p-2.5 shadow-2xl backdrop-blur-md"
              onPointerDown={(e) => e.stopPropagation()}
            >
              {/* Round ON/OFF Button: Red when OFF, Green when ON */}
              <button
                type="button"
                onPointerDown={(e) => e.stopPropagation()}
                onClick={() => {
                  const isON = item.operation === 'heating' || item.operation === 'measuring' || item.operation === 'active';
                  const nextOp = isON ? 'idle' : (item.type === 'phmeter' ? 'active' : 'heating');
                  updateItem(item.id, { operation: nextOp });
                  if (!isON && onSimulationStart) {
                    onSimulationStart();
                  }
                }}
                className={`grid h-8 w-8 shrink-0 place-items-center rounded-full text-[10px] font-extrabold tracking-wider text-foreground shadow-md transition-all ${
                  (item.operation === 'heating' || item.operation === 'measuring' || item.operation === 'active')
                    ? 'bg-emerald-500 hover:bg-emerald-600 shadow-[0_0_12px_rgba(16,185,129,0.7)] animate-pulse'
                    : 'bg-red-500 hover:bg-red-600 shadow-[0_0_10px_rgba(239,68,68,0.6)]'
                }`}
                title={(item.operation === 'heating' || item.operation === 'measuring' || item.operation === 'active') ? 'ВКЛ (Нажмите для выключения)' : 'ВЫКЛ (Нажмите для включения)'}
              >
                {(item.operation === 'heating' || item.operation === 'measuring' || item.operation === 'active') ? 'ON' : 'OFF'}
              </button>

              {/* Temperature Scrollbar Slider for Heaters */}
              {(item.type === 'hotplate' || item.type === 'burner') && (
                <div className="flex items-center gap-2">
                  <input
                    type="range"
                    min="20"
                    max="400"
                    step="1"
                    value={item.targetTemperature ?? 80}
                    onChange={(e) => updateItem(item.id, { targetTemperature: Number(e.target.value) })}
                    className="w-20 cursor-pointer h-2 accent-orange-400"
                  />
                  <span className="min-w-[42px] font-mono text-xs font-bold text-orange-300">
                    {item.targetTemperature ?? 80}&deg;C
                  </span>
                </div>
              )}

              {/* pH Reading Display for pH Meter */}
              {item.type === 'phmeter' && (
                <div className="flex flex-col pr-1">
                  <span className="text-[9px] font-bold uppercase tracking-wider text-muted-foreground">pH показатель</span>
                  <span className="font-mono text-xs font-bold text-cyan-300">
                    {(item.operation === 'active' || item.operation === 'measuring') && item.measurementStatus === 'valid' && typeof item.measuredValue === 'number' ? `pH ${item.measuredValue.toFixed(2)}` : 'pH — подключите зонд к раствору'}
                  </span>
                </div>
              )}
            </div>
          )}

          {pourAnimation?.targetId === item.id && pourAnimation.sourceId !== item.id && (
            <svg className="pointer-events-none absolute left-1/2 top-0 z-30 h-20 w-12 -translate-x-1/2 -translate-y-[4.5rem] overflow-visible" viewBox="0 0 48 80" aria-label="Pouring liquid">
              <path d="M24 0 C22 18 28 30 24 48 C22 58 24 68 24 80" fill="none" stroke={item.material?.color ?? '#22D3EE'} strokeWidth="5" strokeLinecap="round" className="sandbox-pour-stream" />
              <circle cx="17" cy="16" r="2.5" fill={item.material?.color ?? '#22D3EE'} className="animate-rise" />
              <circle cx="31" cy="32" r="2" fill={item.material?.color ?? '#22D3EE'} className="animate-rise animation-delay-300" />
              <circle cx="24" cy="48" r="1.5" fill={item.material?.color ?? '#22D3EE'} className="animate-rise animation-delay-600" />
            </svg>
          )}
          {spillAnimation === item.id && (
            <svg className="pointer-events-none absolute left-1/2 bottom-0 z-10 h-16 w-48 -translate-x-1/2 translate-y-12 overflow-visible opacity-80" viewBox="0 0 128 64" aria-label="Spilling liquid">
              <ellipse cx="64" cy="32" rx="40" ry="12" fill={item.material?.color ?? '#22D3EE'} className="animate-ping opacity-40" />
              <ellipse cx="64" cy="32" rx="30" ry="8" fill={item.material?.color ?? '#22D3EE'} className="opacity-90" />
            </svg>
          )}
          {item.ports.map((port) => {
            const anchor = itemPortLayout(item, port);
            const status = portCompatibility[`${item.id}:${port.id}`];
            const isSource = port.direction === 'out' || port.direction === 'bidirectional';
            const isHelpPort = helpActive && (helpTargets.includes(`port:${item.type}:${port.id}`) || helpTargets.includes(`port:${item.type}:*`));
            const visibilityClass = tool === 'connect' ? 'opacity-100' : 'opacity-0 group-hover:opacity-100';
            const guidePortClass = helpActive && helpTargets.some((target) => target.startsWith('port:')) && !isHelpPort
              ? '!opacity-30'
              : visibilityClass;
            const color = status === 'incompatible'
              ? 'bg-slate-600 opacity-35'
              : status === 'adapter'
                ? 'bg-amber-400'
                : port.type === 'Thermal'
                  ? 'bg-orange-400'
                  : port.type === 'Gas'
                    ? 'bg-violet-400'
                    : port.type === 'Liquid'
                      ? 'bg-cyan-400'
                      : port.type === 'Electrical'
                        ? 'bg-emerald-400'
                        : 'bg-slate-300';
            return (
              <button
                key={port.id}
                type="button"
                data-help-target={`port:${item.type}:${port.id}`}
                data-scenario-port={item.metadata?.scenarioAlias ? `${String(item.metadata.scenarioAlias)}:${port.id}` : undefined}
                aria-label={`${port.name}${status === 'adapter' ? ' · доступен адаптер' : ''}`}
                title={port.name}
                                 onPointerDown={(event) => { event.stopPropagation(); onPortPointerDown(item.id, port.id); }}
                onPointerUp={(event) => { event.stopPropagation(); onPortPointerUp(item.id, port.id); }}
                onClick={(event) => { event.stopPropagation(); onPortPointerUp(item.id, port.id); }}
                onPointerEnter={() => { const point = portPoint(item, port); onPortPointerEnter(item.id, port.id, point); }}
                className={`absolute z-[70] flex h-4 w-4 items-center justify-center -translate-x-1/2 -translate-y-1/2 rounded-full border border-white/80 shadow-md transition-all hover:scale-135 hover:cursor-crosshair before:absolute before:inset-[-8px] before:rounded-full before:content-[''] ${guidePortClass} ${color} ${isHelpPort ? 'help-arrow-target !opacity-100 !h-6 !w-6 !border-cyan-100 !bg-cyan-400 shadow-[0_0_22px_rgba(93,220,255,.9)] animate-pulse' : ''}`}
                style={{ left: `${anchor.x * 100}%`, top: `${anchor.y * 100}%` }}
                >
                  <Plus size={10} className="stroke-[3]" />
                  {isSource && <span className="absolute inset-0 rounded-full animate-ping bg-inherit opacity-75" />}
              </button>
            );
          })}
          {selectedIds.has(item.id) && selectedIds.size === 1 && updateItem && (
            <button
              type="button"
              aria-label="Повернуть 3D-объект"
              title={`Повернуть · ${Math.round(displayRotation)}°`}
              className="absolute left-1/2 top-[-28px] z-[75] grid h-7 w-7 -translate-x-1/2 place-items-center rounded-full border border-white/80 bg-[var(--primary)] text-white shadow-[0_0_12px_rgba(139,92,246,.65)] hover:scale-110"
              onPointerDown={(event) => {
                event.preventDefault();
                event.stopPropagation();
                const parent = event.currentTarget.parentElement;
                if (!parent) return;
                const bounds = parent.getBoundingClientRect();
                const centerX = bounds.left + bounds.width / 2;
                const centerY = bounds.top + bounds.height / 2;
                rotationRef.current = {
                  id: item.id,
                  centerX,
                  centerY,
                  pointerAngle: Math.atan2(event.clientY - centerY, event.clientX - centerX) * 180 / Math.PI,
                  startRotation: item.rotation,
                };
                event.currentTarget.setPointerCapture(event.pointerId);
              }}
              onPointerMove={(event) => {
                const rotation = rotationRef.current;
                if (!rotation || rotation.id !== item.id || !event.currentTarget.hasPointerCapture(event.pointerId)) return;
                const nextPointerAngle = Math.atan2(event.clientY - rotation.centerY, event.clientX - rotation.centerX) * 180 / Math.PI;
                setRotationDraft({ id: item.id, rotation: rotation.startRotation + nextPointerAngle - rotation.pointerAngle });
              }}
              onPointerUp={(event) => {
                event.preventDefault();
                event.stopPropagation();
                const nextRotation = rotationDraft?.id === item.id ? rotationDraft.rotation : item.rotation;
                updateItem(item.id, { rotation: ((nextRotation % 360) + 360) % 360 });
                rotationRef.current = null;
                setRotationDraft(null);
                if (event.currentTarget.hasPointerCapture(event.pointerId)) event.currentTarget.releasePointerCapture(event.pointerId);
              }}
            >
              <RefreshCw size={14} />
            </button>
          )}
          {selectedIds.has(item.id) && selectedIds.size === 1 && [
            { key: 'nw', x: '-1.5', y: '-1.5', cursor: 'nwse-resize', sx: -1, sy: -1 },
            { key: 'ne', x: '101.5', y: '-1.5', cursor: 'nesw-resize', sx: 1, sy: -1 },
            { key: 'se', x: '101.5', y: '101.5', cursor: 'nwse-resize', sx: 1, sy: 1 },
            { key: 'sw', x: '-1.5', y: '101.5', cursor: 'nesw-resize', sx: -1, sy: 1 },
          ].map((handle) => (
            <button
              key={handle.key}
              type="button"
              aria-label={`Изменить размер: ${handle.key}`}
              title="Изменить размер"
                            onPointerDown={(event) => {
                event.stopPropagation();
                event.currentTarget.setPointerCapture(event.pointerId);
                const rect = (event.currentTarget.parentElement)?.getBoundingClientRect();
                if (!rect) return;
                resizeRef.current = { id: item.id, startX: event.clientX,
                  startY: event.clientY,
                  startScaleX: item.visualScaleX ?? 1,
                  startScaleY: item.visualScaleY ?? 1,
                  width: rect.width / (item.visualScaleX ?? 1),
                  height: rect.height / (item.visualScaleY ?? 1),
                  minScale: 0.45,
                  maxScale: 2.0,
                };
              }}
              onPointerMove={(event) => {
                if (!event.currentTarget.hasPointerCapture(event.pointerId)) return;
                const resize = resizeRef.current;
                if (!resize) return;
                const dx = event.clientX - resize.startX;
                const dy = event.clientY - resize.startY;
                let nextScaleX = handle.sx === 0 ? resize.startScaleX : Math.min(resize.maxScale, Math.max(resize.minScale, resize.startScaleX + handle.sx * dx / (resize.width * zoom)));
                let nextScaleY = handle.sy === 0 ? resize.startScaleY : Math.min(resize.maxScale, Math.max(resize.minScale, resize.startScaleY + handle.sy * dy / (resize.height * zoom)));
                
                const lockRatio = ['thermometer', 'pipette', 'burette', 'phmeter', 'scale'].includes(item.type);
                if (lockRatio) {
                  const maxDelta = Math.abs(nextScaleX - resize.startScaleX) > Math.abs(nextScaleY - resize.startScaleY) ? (nextScaleX - resize.startScaleX) : (nextScaleY - resize.startScaleY);
                  nextScaleX = resize.startScaleX + maxDelta;
                  nextScaleY = resize.startScaleY + maxDelta;
                }
                
                setResizeDraft({ id: item.id, scaleX: nextScaleX, scaleY: nextScaleY });
              }}
              onPointerUp={(event) => {
                const resize = resizeRef.current;
                if (resize) onResize(item.id, resizeDraft?.id === item.id ? resizeDraft.scaleX : resize.startScaleX, resizeDraft?.id === item.id ? resizeDraft.scaleY : resize.startScaleY);
                resizeRef.current = null;
                setResizeDraft(null);
                event.currentTarget.releasePointerCapture(event.pointerId);
              }}
              style={{ left: `${handle.x}%`, top: `${handle.y}%`, cursor: handle.cursor }}
              className="absolute z-50 h-2.5 w-2.5 -translate-x-1/2 -translate-y-1/2 rounded-full border border-white bg-[var(--primary)] shadow-[0_0_7px_rgba(139,92,246,.6)]"
              data-resize-handle={handle.key}
            />
          ))}
          {resizeDraft?.id === item.id && (
            <span className="pointer-events-none absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap rounded-lg border border-[var(--primary)] bg-card px-2 py-1 text-[10px] font-bold text-foreground shadow-lg">
              {Math.round(resizeDraft.scaleX * 100)}% x {Math.round(resizeDraft.scaleY * 100)}%
            </span>
          )}
        </div>
      )})}
      
      {/* Global Quick Actions Panel */}
      {(() => {
        const selectedItem = items.find(i => i.id === selectedId);
        if (!selectedItem || !isVessel(selectedItem)) return null;
        return (
          <SelectionToolbar 
            item={selectedItem}
            items={items}
            connections={connections}
            duplicate={duplicate} 
            remove={remove} 
            onPourExecute={onPourExecute}
            onBeginPour={(sourceId) => { setPourSource?.(sourceId); setSelectedId(null); }}
            updateItem={updateItem}
            onMix={onMix}
            onClose={() => setSelectedId(null)}
          />
        );
      })()}
      
      {/* Compatible ports themselves provide the connection guidance. */}

      <div
        className="sandbox-minimap pointer-events-auto absolute bottom-16 right-4 z-[90] hidden md:block"
        onPointerDown={(event) => event.stopPropagation()}
      >
        {miniMapOpen ? (
          <div className="w-52 rounded-xl border border-[var(--border)] bg-card/90 p-2 shadow-2xl backdrop-blur-xl">
            <div className="mb-1 flex items-center justify-between px-1 text-[9px] font-bold uppercase tracking-wider text-muted-foreground">
              <span>Мини-карта</span>
              <button type="button" className="rounded px-1.5 py-0.5 text-muted-foreground hover:bg-muted hover:text-foreground" onClick={() => setMiniMapOpen(false)} aria-label="Скрыть мини-карту">×</button>
            </div>
            <svg viewBox={`0 0 ${miniMapBounds.maxX} ${miniMapBounds.maxY}`} className="h-28 w-full cursor-crosshair touch-none rounded-lg border border-border/60 bg-background/70" onPointerDown={navigateMiniMap} onPointerMove={(event)=>{if(event.buttons===1)navigateMiniMap(event)}} aria-label="Scene minimap. Click or drag to move the viewport.">
              {connections.map((connection) => {
                const from = centers.get(connection.from);
                const to = centers.get(connection.to);
                return from && to ? <line key={connection.id} x1={from.x} y1={from.y} x2={to.x} y2={to.y} stroke="var(--primary-bright)" strokeWidth="7" opacity=".65" /> : null;
              })}
              {items.map((item) => <rect key={item.id} x={item.x} y={item.y} width={Math.max(18, item.w * (item.scaleX ?? item.scale))} height={Math.max(18, item.h * (item.scaleY ?? item.scale))} rx="8" fill={selectedIds.has(item.id) ? "var(--primary)" : "var(--muted-foreground)"} opacity={selectedIds.has(item.id) ? ".95" : ".6"} />)}
              <rect x={Math.max(0, -pan.x / zoom)} y={Math.max(0, -pan.y / zoom)} width={canvasSize.width / zoom} height={canvasSize.height / zoom} fill="none" stroke="var(--primary)" strokeWidth="5" strokeDasharray="12 8" opacity=".8" />
            </svg>
            <button type="button" className="mt-2 w-full rounded-lg border border-border bg-muted/70 px-2 py-1 text-[10px] font-semibold text-foreground hover:bg-muted focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary" onClick={()=>window.dispatchEvent(new CustomEvent('sandbox-zoom-to-fit'))}>Fit scene</button>
          </div>
        ) : (
          <button type="button" className="rounded-lg border border-border bg-card/90 px-2.5 py-1.5 text-[10px] font-semibold text-foreground shadow-xl backdrop-blur-xl hover:bg-muted" onClick={() => setMiniMapOpen(true)}>Мини-карта</button>
        )}
      </div>
      
      
        {marquee && (
          <div 
            className="absolute pointer-events-none bg-blue-500/20 border border-blue-500/50 z-50"
            style={{
              left: Math.min(marquee.startX, marquee.currentX),
              top: Math.min(marquee.startY, marquee.currentY),
              width: Math.abs(marquee.startX - marquee.currentX),
              height: Math.abs(marquee.startY - marquee.currentY),
            }}
          />
        )}
        {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          item={items.find(i => i.id === contextMenu.itemId)!}
          onClose={() => setContextMenu(null)}
          onAction={handleContextMenuAction}
        />
      )}
    </div>
  );
}


