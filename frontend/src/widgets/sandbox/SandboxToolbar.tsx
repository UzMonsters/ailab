import { Move, Minus, Plus, PanelLeft, SlidersHorizontal, BarChart3, Link2, Grid3X3, Undo2, Redo2, ChevronRight, ChevronUp, ChevronDown } from "lucide-react";
import { ToolButton } from "@/widgets/sandbox/SandboxCanvas";
import { useTranslations } from "next-intl";
import { useState } from "react";

interface SandboxToolbarProps {
  tool: "select" | "pan" | "connect";
  setTool: (tool: "select" | "pan" | "connect") => void;
  setConnectSource: (source: string | null) => void;
  setZoom: (updater: (value: number) => number) => void;
  zoom: number;
  setMobilePanel: (panel: 'library' | 'inspector' | 'dock' | null) => void;
  setBottomDockTab: (tab: 'Events' | 'Measurements' | 'Charts' | 'Warnings' | 'Simulation') => void;
  showGrid: boolean;
  setShowGrid: (value: boolean) => void;
  runState: 'Draft' | 'Validating' | 'Ready' | 'Blocked' | 'Running' | 'Paused' | 'Finished' | 'Error';
  onRun: () => void;
  onPause: () => void;
  onStop: () => void;
  leftPanelVisible: boolean;
  rightPanelVisible: boolean;
  setLeftPanelVisible: (value: boolean) => void;
  setRightPanelVisible: (value: boolean) => void;
  onUndo?: () => void;
  onRedo?: () => void;
  canUndo?: boolean;
  canRedo?: boolean;
  helpActive?: boolean;
  helpTool?: string;
  showNavbar?: boolean;
}

export function SandboxToolbar({

  tool,
  setTool,
  setConnectSource,
  setZoom,
  zoom,
  setMobilePanel,
  setBottomDockTab,
  showGrid,
  setShowGrid,
  runState,
  onRun,
  onPause,
  onStop,
  leftPanelVisible,
  rightPanelVisible,
  setLeftPanelVisible,
  setRightPanelVisible,
  helpActive,
  helpTool,
  onUndo,
  onRedo,
  canUndo,
  canRedo
  , showNavbar = true
}: SandboxToolbarProps) {
  const ts = useTranslations("sandbox");
  const [collapsed, setCollapsed] = useState(false);
  if (collapsed) return <button type="button" aria-label="Показать панель инструментов" title="Показать панель инструментов" onPointerDown={event => event.stopPropagation()} onClick={() => setCollapsed(false)} className={`absolute left-1/2 ${showNavbar ? 'top-14' : 'top-0'} z-[60] grid h-7 w-10 -translate-x-1/2 place-items-center rounded-b-lg border border-t-0 border-border bg-card/95 text-muted-foreground shadow-md transition-colors hover:text-foreground`}><ChevronDown size={17} /></button>;
  return (
    <div className={`absolute left-1/2 ${showNavbar ? 'top-14' : 'top-0'} z-[60] -translate-x-1/2`}>
    <div className="sandbox-toolbar sandbox-toolbar-reveal flex max-w-[calc(100vw-1rem)] items-center gap-1.5 overflow-x-auto overflow-y-visible rounded-full border border-border bg-card/95 p-1.5 shadow-[0_8px_32px_rgba(139,92,246,0.12)] backdrop-blur-xl transition-all duration-300 hover:shadow-[0_8px_32px_rgba(139,92,246,0.22)] [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]">
      <ToolButton label="Отменить (Ctrl+Z)" disabled={!canUndo} onClick={onUndo}><Undo2 size={17} /></ToolButton>
      <ToolButton label="Повторить (Ctrl+Y)" disabled={!canRedo} onClick={onRedo}><Redo2 size={17} /></ToolButton>
      <div className="mx-1.5 h-6 w-px bg-[var(--border)]" />
      <ToolButton
        label={ts("toolbar.select")}
        labelVisible
        active={tool === "select"}
        onClick={() => setTool("select")}
      >
        ↖
      </ToolButton>
      <ToolButton
        label={ts("toolbar.pan")}
        labelVisible
        active={tool === "pan"}
        onClick={() => setTool("pan")}
      >
        <Move size={17} />
      </ToolButton>
      <ToolButton
        label={ts("toolbar.connect")}
        labelVisible
        active={tool === "connect"}
        guideTarget="toolbar:connect"
        onClick={() => {
          setTool("connect");
          setConnectSource(null);
        }}
        className={helpActive && helpTool === "connect" ? "help-arrow-target ring-2 ring-violet-300 shadow-[0_0_20px_rgba(156,107,255,.65)] animate-pulse" : ""}
      >
        <Link2 size={17} />
      </ToolButton>
      <div className="mx-1.5 h-6 w-px bg-[var(--border)]" />
      <div className="flex h-10 shrink-0 items-center gap-1 rounded-xl bg-foreground/5 px-1">
        <ToolButton label="Уменьшить" onClick={() => setZoom((value: number) => Math.max(0.5, value - 0.1))}><Minus size={15} /></ToolButton>
        <span className="min-w-12 text-center text-[11px] font-semibold text-foreground/80">{Math.round(zoom * 100)}%</span>
        <ToolButton label="Увеличить" onClick={() => setZoom((value: number) => Math.min(2, value + 0.1))}><Plus size={15} /></ToolButton>
        <div className="mx-0.5 h-4 w-px bg-[var(--border)]" />
        <ToolButton label="Центрировать сцену" onClick={() => {
          setZoom(() => 1);
          const event = new CustomEvent('sandbox-center-scene');
          window.dispatchEvent(event);
        }}>
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="3"/><path d="M12 2v4M12 18v4M2 12h4M18 12h4"/></svg>
        </ToolButton>
        <ToolButton label="Показать всё" onClick={() => {
          const event = new CustomEvent('sandbox-zoom-to-fit');
          window.dispatchEvent(event);
        }}>
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M4 8V4h4M16 4h4v4M4 16v4h4M16 20h4v-4"/></svg>
        </ToolButton>
      </div>
      <div className="mx-1.5 h-6 w-px bg-[var(--border)]" />
      <ToolButton label="Сетка" active={showGrid} onClick={() => setShowGrid(!showGrid)}><Grid3X3 size={17} /></ToolButton>
      <ToolButton label="Библиотека" active={leftPanelVisible} onClick={() => window.innerWidth < 1600 ? setMobilePanel('library') : setLeftPanelVisible(!leftPanelVisible)}><PanelLeft size={17} /></ToolButton>
      <ToolButton label="Инспектор" active={rightPanelVisible} onClick={() => window.innerWidth < 1600 ? setMobilePanel('inspector') : setRightPanelVisible(!rightPanelVisible)}><SlidersHorizontal size={17} /></ToolButton>
      <div className="mx-1.5 h-6 w-px bg-[var(--border)]" />
      <div className="mx-1.5 h-6 w-px shrink-0 bg-[var(--border)] min-[1600px]:hidden" />
      <div className="flex shrink-0 gap-1.5 min-[1600px]:hidden">
        <ToolButton label="Измерения" onClick={() => { setBottomDockTab('Measurements'); setMobilePanel('dock'); }}><BarChart3 size={17} /></ToolButton>
      </div>
      
    </div>
    <button type="button" aria-label="Скрыть панель инструментов" title="Скрыть панель инструментов" onPointerDown={event => event.stopPropagation()} onClick={() => setCollapsed(true)} className="absolute left-1/2 top-full z-50 grid h-7 w-10 -translate-x-1/2 place-items-center rounded-b-lg border border-t-0 border-border bg-card/95 text-muted-foreground/70 shadow-md transition-colors hover:text-foreground"><ChevronUp size={16} /></button>
    </div>
  );
}
