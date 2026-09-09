"use client";
import { useTranslations } from "next-intl";
import { useState, useEffect } from 'react';
import { MobileSheet, MeasurementCard, MiniChart } from "@/widgets/sandbox/SandboxPanels";
import type { Item } from "./types";
import type { SandboxSyncStatus } from "./hooks/useSandboxSync";
import type { RuntimeScenario } from './runtime/runtime.types';

interface SandboxDockProps {
  bottomDockOpen: boolean;
  setBottomDockOpen: (value: boolean | ((val: boolean) => boolean)) => void;
  bottomDockTab: 'Events' | 'Measurements' | 'Charts' | 'Warnings' | 'Simulation';
  setBottomDockTab: (tab: 'Events' | 'Measurements' | 'Charts' | 'Warnings' | 'Simulation') => void;
  syncStatus: SandboxSyncStatus;
  itemsLength: number;
  eventLog: Array<{ time: string; event: string; detail?: string }>;
  selected?: Item;
  measurementSamples: Array<{ time: string; temperature: number; volume: number; mass: number; pressure: number }>;
  mobilePanel: 'library' | 'inspector' | 'dock' | null;
  setMobilePanel: (panel: 'library' | 'inspector' | 'dock' | null) => void;
  activeScenario?: { id: string; step: number } | null;
  runtimeScenario?: RuntimeScenario | null;
  scenarioIntro?: boolean;
  experimentResult?: { title: string; description: string; temperatureC?: number; volumeMl?: number } | null;
  leftOffset?: number;
  rightOffset?: number;
  temperatureConnected?: boolean;
  measuredTemperature?: number | null;
  helpActive?: boolean;
  onHelp?: () => void;
  onShowHow?: () => void;
  hintIndex?: number;
  onNextHint?: () => void;
}

export function SandboxDock({
  bottomDockOpen,
  setBottomDockOpen,
  bottomDockTab,
  setBottomDockTab,
  syncStatus,
  itemsLength,
  eventLog,
  selected,
  measurementSamples,
  mobilePanel,
  setMobilePanel,
  activeScenario,
  runtimeScenario,
  scenarioIntro,
  experimentResult,
  leftOffset = 360,
  rightOffset = 360,
  temperatureConnected = false,
  measuredTemperature = null,
  helpActive = false,
  onHelp,
  onShowHow,
  hintIndex = 0,
  onNextHint,
}: SandboxDockProps) {
  const ts = useTranslations("sandbox");
  const showHowLabel = 'Show how';
  const eventLabel = (event: string) => {
    const key = `dock.event.${event.toLowerCase()}`;
    return ts.has(key) ? ts(key) : event.replaceAll('_', ' ');
  };
  const syncLabel = (status: SandboxSyncStatus) => {
    const key = `dock.sync.${status}`;
    return ts.has(key) ? ts(key) : status;
  };

  const [dockHeight, setDockHeight] = useState(220);
  const [helpDialogOpen, setHelpDialogOpen] = useState(false);

  useEffect(() => {
    if (!scenarioIntro) return;
    const blockWorkspaceInput = (event: KeyboardEvent) => {
      if (event.key === 'Escape') return;
      event.preventDefault();
      event.stopPropagation();
    };
    window.addEventListener('keydown', blockWorkspaceInput, true);
    return () => window.removeEventListener('keydown', blockWorkspaceInput, true);
  }, [scenarioIntro]);

  const openHelpDialog = () => {
    onHelp?.();
    const count = activeScenario && runtimeScenario ? runtimeScenario.steps[activeScenario.step]?.hints.length ?? 0 : 0;
    if (count > 1) onNextHint?.();
    // Help is an in-place guide: it must not block the workspace with a modal.
    setHelpDialogOpen(false);
  };

  const handlePointerDown = (e: React.PointerEvent) => {
    e.preventDefault();
    const startY = e.clientY;
    const startHeight = dockHeight;
    const onPointerMove = (moveEvent: PointerEvent) => {
      const delta = startY - moveEvent.clientY;
      setDockHeight(Math.max(120, Math.min(800, startHeight + delta)));
    };
    const onPointerUp = () => {
      document.removeEventListener('pointermove', onPointerMove);
      document.removeEventListener('pointerup', onPointerUp);
    };
    document.addEventListener('pointermove', onPointerMove);
    document.addEventListener('pointerup', onPointerUp);
  };

  return (
    <>
      {helpDialogOpen && activeScenario && runtimeScenario && (() => {
        const scenario = runtimeScenario;
        const isComplete = activeScenario.step >= scenario.steps.length;
        const stepIndex = Math.min(Math.max(activeScenario.step, 0), scenario.steps.length - 1);
        const currentStep = scenario.steps[stepIndex];
        const scenarioName = scenario.title;
        const stepTitle = isComplete ? 'Scenario complete' : currentStep.title;
        const stepDescription = isComplete ? 'Every step is complete. You can repeat this Scenario or continue to the next level.' : currentStep.instruction;
        const stepHint = isComplete ? 'Close this panel when you are ready.' : currentStep.hints[hintIndex % Math.max(1, currentStep.hints.length)]?.text ?? 'Complete the instruction shown above.';
        return (
          <div className="fixed inset-0 z-[260] flex items-center justify-center bg-black/65 p-4 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label="Scenario instructions">
            <button type="button" aria-label="Close instructions" className="absolute inset-0 cursor-default" onClick={() => setHelpDialogOpen(false)} />
            <div className="relative z-10 w-full max-w-xl overflow-hidden rounded-3xl border border-lime-300/40 bg-[#101723] text-foreground shadow-[0_0_80px_rgba(163,230,53,.18)]">
              <div className="h-1.5 bg-gradient-to-r from-cyan-400 via-lime-300 to-violet-400" />
              <div className="flex items-start justify-between gap-4 p-5 pb-3 sm:p-7 sm:pb-4">
                <div><span className="text-[10px] font-bold uppercase tracking-[.2em] text-lime-300">Scenario instructions</span><h2 className="mt-2 text-xl font-black sm:text-2xl">{scenarioName}</h2></div>
                <button type="button" onClick={() => setHelpDialogOpen(false)} className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-border text-lg text-foreground/70 transition hover:bg-white/10 hover:text-white" aria-label="Close">×</button>
              </div>
              <div className="space-y-4 px-5 pb-5 sm:px-7 sm:pb-7">
                <div className="flex items-center gap-2 text-xs font-semibold text-foreground/60"><span className="rounded-full bg-[var(--primary)]/20 px-2.5 py-1 text-[var(--primary-bright)]">{isComplete ? 'Complete' : `Step ${activeScenario.step + 1} of ${scenario.steps.length}`}</span><span>The hint is specific to the current step</span></div>
                <div className="rounded-2xl border border-white/10 bg-white/[.04] p-4 sm:p-5"><h3 className="text-lg font-bold text-white sm:text-xl">{stepTitle}</h3><p className="mt-3 text-sm leading-6 text-foreground/75 sm:text-base">{stepDescription}</p><div className="mt-4 rounded-xl border border-cyan-300/20 bg-cyan-300/[.08] p-3.5 text-sm leading-6 text-cyan-100"><span className="mr-1 font-bold text-cyan-300">Next action:</span>{stepHint}</div></div>
                {!isComplete && <p className="text-xs leading-5 text-foreground/50">The workspace advances automatically after the completion rule becomes true.</p>}
                <button type="button" onClick={() => setHelpDialogOpen(false)} className="w-full rounded-xl bg-[var(--primary)] px-4 py-3 text-sm font-bold text-white transition hover:brightness-110">Got it</button>
              </div>
            </div>
          </div>
        );
      })()}
      {scenarioIntro && activeScenario && runtimeScenario && (() => {
        const scenario = runtimeScenario;
        const isComplete = activeScenario.step >= scenario.steps.length;
        const currentStep = isComplete ? scenario.steps[scenario.steps.length - 1] : scenario.steps[activeScenario.step];
        return (
          <div className="pointer-events-auto fixed inset-0 z-[200] flex items-center justify-center bg-black/50 p-4 backdrop-blur-md animate-scenario-intro" role="dialog" aria-modal="true" aria-label="Введение в сценарий" onContextMenu={(event) => event.preventDefault()}>
              <div className="relative flex w-full max-w-2xl flex-col items-center justify-center gap-4 overflow-hidden rounded-[2.5rem] border border-cyan-500/30 bg-slate-950/90 p-10 text-center shadow-[0_0_100px_rgba(34,211,238,0.15)] drop-shadow-2xl backdrop-blur-xl">
                
                {/* Decorative glows */}
                <div className="absolute -top-24 -left-24 h-48 w-48 rounded-full bg-cyan-500/20 blur-3xl" />
                <div className="absolute -bottom-24 -right-24 h-48 w-48 rounded-full bg-violet-500/20 blur-3xl" />
                
                <div className="mb-2 inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-[10px] font-bold uppercase tracking-widest text-cyan-300">
                   {isComplete ? "Scenario complete" : `Step ${activeScenario.step + 1}`}
                </div>

                <h1 className="max-w-full text-3xl font-black tracking-tight text-white md:text-5xl">
                  {scenario.title}
                </h1>
                
                <div className="mt-4 flex flex-col items-center gap-2">
                  <p className="max-w-lg text-lg font-bold text-white/90 md:text-2xl">{currentStep.title}</p>
                  <p className="max-w-lg text-sm leading-relaxed text-white/60 md:text-base">{currentStep.instruction}</p>
                </div>
              </div>
            </div>
        );
      })()}
      {/* Collapsible scientific dock */}
      <section style={{ left: leftOffset, right: rightOffset, height: bottomDockOpen ? dockHeight : 44 }} className={`sandbox-dock absolute bottom-6 z-30 hidden overflow-hidden rounded-2xl border border-border bg-[rgba(10,12,18,.86)] shadow-2xl backdrop-blur-xl lg:flex flex-col transition-all duration-300 animate-fade-in-up hover:scale-[1.01]`}>
        {bottomDockOpen && (
          <div 
            className="absolute left-0 right-0 top-0 z-50 h-2 cursor-row-resize hover:bg-foreground/10" 
            onPointerDown={handlePointerDown} 
            title="Resize dock"
            />
          )}
        {/* Active Scenario Guidance */}
         {bottomDockOpen && activeScenario && runtimeScenario && (() => {
           const scenario = runtimeScenario;
           const isComplete = activeScenario.step >= scenario.steps.length;
           const currentStep = isComplete ? scenario.steps[scenario.steps.length - 1] : scenario.steps[activeScenario.step];
           if (!currentStep) return null;
           return (
      <>
        {/* Normal Dock Content */}
               <div className={`flex flex-col gap-1 border-b border-[var(--primary)]/20 bg-[var(--primary)]/10 p-3 text-sm text-foreground transition-opacity duration-500 ${scenarioIntro ? 'opacity-0' : 'opacity-100'}`}>
                 <div className="flex items-center justify-between">
                   <div className="min-w-0">
                     <span className="block text-[10px] font-semibold uppercase tracking-[.16em] text-[var(--muted-foreground)]">SCENARIO PROGRESS</span>
                     <span className="block truncate font-bold text-[var(--primary-bright)]">{scenario.title}</span>
                   </div>
                   <div className="flex items-center gap-2">
                     {currentStep.hints.some((hint) => hint.type !== 'TEXT') && <button type="button" onClick={onShowHow} className="rounded-lg border border-violet-300/50 bg-violet-500/10 px-2.5 py-1 text-[10px] font-semibold text-violet-100 transition hover:bg-violet-500/20">{showHowLabel}</button>}
                     <button type="button" onClick={openHelpDialog} className={`rounded-lg border px-2.5 py-1 text-[10px] font-semibold transition ${helpActive ? 'border-lime-300/70 bg-lime-300/20 text-lime-200' : 'border-white/15 text-foreground/80 hover:bg-foreground/10'}`}>HELP</button>
                   <span className="text-xs text-[var(--muted-foreground)]">
                   {isComplete ? 'COMPLETE' : `STEP ${activeScenario.step + 1} OF ${scenario.steps.length}`}
                   </span>
                 </div>
               </div>
               <p className="font-semibold text-xs mb-1">{isComplete ? 'SCENARIO COMPLETE' : currentStep.title}</p>
               {!isComplete && <p className="text-[10px] text-[var(--muted-foreground)] mb-2">{currentStep.instruction} <span className="text-[var(--primary)]">{currentStep.hints[hintIndex % Math.max(1, currentStep.hints.length)]?.text}</span></p>}
               
               {/* Step indicators */}
               <div className="flex gap-2">
                 {scenario.steps.map((step, idx) => {
                   const isPast = activeScenario.step > idx;
                   const isCurrent = activeScenario.step === idx;
                   return (
                     <button type="button" key={idx} onClick={openHelpDialog} className={`flex items-center gap-1.5 rounded-full px-2 py-0.5 text-[10px] font-semibold transition-all ${isPast ? 'bg-emerald-500/20 text-emerald-300' : isCurrent ? 'bg-[var(--primary)]/30 text-[var(--primary-bright)] border border-[var(--primary)]/50' : 'bg-white/5 text-muted-foreground'}`}>
                       {isPast ? <span>✓</span> : <span>{idx + 1}</span>}
                       <span className="truncate max-w-[100px]">{step.title}</span>
                     </button>
                   );
                 })}
                 </div>
               </div>
             </>
           );
         })()}
        <div className="flex h-11 items-center gap-1 border-b border-border px-2">
          <button aria-label={ts("dock.events")} className="mr-1 grid h-8 w-8 place-items-center rounded-lg text-xs font-bold text-foreground hover:bg-foreground/10" onClick={() => setBottomDockOpen((open: boolean) => !open)}>{bottomDockOpen ? '⌄' : '⌃'}</button>
          {([
            { id: 'Events', label: ts("dock.events") },
            { id: 'Measurements', label: ts("dock.measurements") },
            { id: 'Charts', label: ts("dock.charts") },
            { id: 'Simulation', label: ts("dock.simulation") }
          ] as const).map((tab) => (
            <button key={tab.id} onClick={() => { setBottomDockTab(tab.id as any); setBottomDockOpen(true); }} className={`rounded-lg px-3 py-1.5 text-[11px] font-semibold transition-all duration-300 ${bottomDockTab === tab.id ? 'bg-[var(--primary)] text-white' : 'text-[var(--muted-foreground)] hover:bg-white/[.06]'}`}>{tab.label}</button>
          ))}
          <span data-testid="sandbox-sync-status" className={`ml-auto text-[10px] ${syncStatus === 'error' || syncStatus === 'offline' || syncStatus === 'conflict' ? 'text-red-300' : syncStatus === 'saving' || syncStatus === 'reconciling' ? 'text-amber-300' : 'text-emerald-300'}`}>{syncLabel(syncStatus)} <span className="text-foreground/35">·</span> {itemsLength} {ts("dock.objects")}</span>
        </div>
          {bottomDockOpen && <div className="flex-1 overflow-y-auto p-3 text-xs text-[var(--muted-foreground)]">
          {bottomDockTab === 'Events' && <div className={`sandbox-event-log space-y-1 ${eventLog.length === 0 ? 'sandbox-event-log-empty' : ''}`}>{eventLog.length === 0 ? <p>{ts("dock.emptyLog")}</p> : eventLog.slice().reverse().map((entry, idx) => <p key={`${entry.time}-${entry.event}-${idx}`}><span className="mr-2 font-mono text-[10px] text-cyan-200/50">{entry.time}</span><strong className="mr-2 text-foreground/90">{eventLabel(entry.event)}</strong>{entry.detail}</p>)}</div>}
           {bottomDockTab === 'Measurements' && (temperatureConnected && selected && measuredTemperature !== null ? <div className="grid grid-cols-1 gap-3 md:grid-cols-3"><MeasurementCard label="Температура · LIVE" value={`${measuredTemperature.toFixed(1)} °C`} /></div> : <div className="sandbox-empty-state rounded-xl border border-cyan-400/20 bg-cyan-400/[.05] p-4 text-center text-xs">Нет измерений. Подключите измерительный прибор.</div>)}
          {bottomDockTab === 'Charts' && <div className="grid gap-4 md:grid-cols-2"><MiniChart title={ts("dock.temperature")} samples={measurementSamples} field="temperature" color="#F97316" /></div>}
          {bottomDockTab === 'Simulation' && (
            <div className="flex flex-col gap-3 p-1">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 border-b border-border pb-3">
                 <div className="flex flex-col"><span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">{ts("dock.engine")}</span><span className="sandbox-engine-version text-sm font-mono font-bold text-lime-400 drop-shadow-[0_0_8px_rgba(163,230,53,0.4)]">JAS-CHEM-ENG v2</span></div>
                 <div className="flex flex-col"><span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">{ts("dock.frameRate")}</span><span className="text-sm font-mono font-bold text-foreground">{ts("dock.fps")}</span></div>
                 <div className="flex flex-col"><span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">{ts("dock.objectsLabel")}</span><span className="text-sm font-mono font-bold text-cyan-400">{itemsLength} {ts("dock.objects")}</span></div>
                 <div className="flex flex-col"><span className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">{ts("dock.thermodynamics")}</span><span className="text-sm font-mono font-bold text-orange-400">{ts("dock.enabled")}</span></div>
              </div>
              <div className="text-xs leading-relaxed text-muted-foreground max-w-4xl">{ts("dock.simulationDescription")}</div>
              <div className="hidden text-xs leading-relaxed text-muted-foreground max-w-4xl">
                Р РЋР С‘РЎРѓРЎвЂљР ВµР СР В° РЎРѓР С‘Р СРЎС“Р В»РЎРЏРЎвЂ Р С‘Р С‘ РЎР‚Р В°Р В±Р С•РЎвЂљР В°Р ВµРЎвЂљ Р В»Р С•Р С”Р В°Р В»РЎРЉР Р…Р С• Р Р† Р В±РЎР‚Р В°РЎС“Р В·Р ВµРЎР‚Р Вµ Р Р† РЎР‚Р ВµР В¶Р С‘Р СР Вµ РЎР‚Р ВµР В°Р В»РЎРЉР Р…Р С•Р С–Р С• Р Р†РЎР‚Р ВµР СР ВµР Р…Р С‘. Р вЂ™РЎРѓР Вµ РЎвЂћР С‘Р В·Р С‘РЎвЂЎР ВµРЎРѓР С”Р С‘Р Вµ РЎРѓР Р†Р С•Р в„–РЎРѓРЎвЂљР Р†Р В° (Р С•РЎвЂ¦Р В»Р В°Р В¶Р Т‘Р ВµР Р…Р С‘Р Вµ Р СњРЎРЉРЎР‹РЎвЂљР С•Р Р…Р В°, Р С‘РЎРѓР С—Р В°РЎР‚Р ВµР Р…Р С‘Р Вµ, РЎвЂљР ВµР С—Р В»Р С•РЎвЂР СР С”Р С•РЎРѓРЎвЂљРЎРЉ, Р С”Р С•Р Р…Р Т‘Р ВµР Р…РЎРѓР В°РЎвЂ Р С‘РЎРЏ Р С‘ Р Т‘Р В°Р Р†Р В»Р ВµР Р…Р С‘Р Вµ Р С–Р В°Р В·Р С•Р Р†) Р С—РЎР‚Р С•РЎРѓРЎвЂЎР С‘РЎвЂљРЎвЂ№Р Р†Р В°РЎР‹РЎвЂљРЎРѓРЎРЏ РЎРѓ Р С‘РЎРѓР С—Р С•Р В»РЎРЉР В·Р С•Р Р†Р В°Р Р…Р С‘Р ВµР С Р Т‘Р ВµРЎвЂљР ВµРЎР‚Р СР С‘Р Р…Р С‘РЎР‚Р С•Р Р†Р В°Р Р…Р Р…Р С•Р С–Р С• РЎвЂћР С‘Р В·Р С‘РЎвЂЎР ВµРЎРѓР С”Р С•Р С–Р С• Р Т‘Р Р†Р С‘Р В¶Р С”Р В°. Р вЂњРЎР‚Р В°РЎвЂћР С‘Р С”Р В° Р С‘ Р Р†Р ВµР С”РЎвЂљР С•РЎР‚Р Р…РЎвЂ№Р Вµ РЎРѓР С•РЎРѓРЎвЂљР С•РЎРЏР Р…Р С‘РЎРЏ Р С—Р ВµРЎР‚Р ВµР Т‘Р В°РЎР‹РЎвЂљРЎРѓРЎРЏ Р Р† UI РЎвЂЎР ВµРЎР‚Р ВµР В· React Context, Р С•Р В±Р ВµРЎРѓР С—Р ВµРЎвЂЎР С‘Р Р†Р В°РЎРЏ Р СР С–Р Р…Р С•Р Р†Р ВµР Р…Р Р…РЎвЂ№Р в„– Р С•РЎвЂљР С”Р В»Р С‘Р С”.
              </div>
            </div>
          )}
        </div>}
      </section>

      {mobilePanel === 'dock' && <MobileSheet title={bottomDockTab} onClose={() => setMobilePanel(null)}><div className="space-y-3">{activeScenario && <button type="button" onClick={openHelpDialog} className={`w-full rounded-lg border px-3 py-2 text-left text-xs font-semibold ${helpActive ? 'border-lime-300/70 bg-lime-300/15 text-lime-200' : 'border-border text-foreground/80'}`}>Помощь — показать, что делать</button>}<div className="flex gap-1 overflow-x-auto">{([{ id: 'Events', label: ts('dock.events') }, { id: 'Measurements', label: ts('dock.measurements') }, { id: 'Charts', label: ts('dock.charts') }, { id: 'Simulation', label: ts('dock.simulation') }] as const).map((tab) => <button key={tab.id} onClick={() => setBottomDockTab(tab.id)} className={`rounded-lg px-3 py-2 text-xs transition-all duration-300 ${bottomDockTab === tab.id ? 'bg-[var(--primary)] text-white' : 'bg-white/[.05]'}`}>{tab.label}</button>)}</div>{bottomDockTab === 'Measurements' && (temperatureConnected && selected && measuredTemperature !== null ? <MeasurementCard label={`${ts('dock.temperature')} · LIVE`} value={`${measuredTemperature.toFixed(1)} °C`} /> : <p className="rounded-lg border border-cyan-400/20 bg-cyan-400/[.05] p-3 text-xs">Нет измерений. Подключите измерительный прибор.</p>)}{bottomDockTab === 'Charts' && <div className="space-y-2"><MiniChart title={ts('dock.temperature')} samples={measurementSamples} field="temperature" color="#F97316" /></div>}{bottomDockTab === 'Events' && <div className="space-y-1 text-xs">{eventLog.slice().reverse().map((entry, idx) => <p key={`${entry.time}-${entry.event}-${idx}`}><span className="mr-2 font-mono">{entry.time}</span>{eventLabel(entry.event)}</p>)}</div>}{bottomDockTab === 'Simulation' && <p className="text-xs">{ts('dock.simulationDescriptionShort')}</p>}</div></MobileSheet>}
    </>
  );
}
