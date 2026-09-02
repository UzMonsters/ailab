"use client";
import { useLocale, useTranslations } from "next-intl";
import { useState, useEffect } from 'react';
import { MobileSheet, MeasurementCard, MiniChart } from "@/widgets/sandbox/SandboxPanels";
import type { Item } from "./types";
import type { SandboxSyncStatus } from "./hooks/useSandboxSync";
import { SCENARIOS } from "./scenarios";

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
  scenarioIntro?: boolean;
  experimentResult?: { title: string; description: string; temperatureC?: number; volumeMl?: number } | null;
  leftOffset?: number;
  rightOffset?: number;
  temperatureConnected?: boolean;
  measuredTemperature?: number | null;
  helpActive?: boolean;
  onHelp?: () => void;
  onShowHow?: () => void;
}

const scenarioCopy = {
  water_intro: { name: 'Введение в песочницу', steps: [
    { title: 'Поставьте колбу', desc: 'Добавьте пустой сосуд на рабочее поле.', hint: 'Откройте вкладку «Оборудование» и выберите колбу или стакан.' },
    { title: 'Добавьте воду', desc: 'Добавьте воду в установленный сосуд.', hint: 'Откройте вкладку «Материалы», выберите воду и кликните по сосуду.' },
  ] },
  measure_water: { name: 'Измерение показателей', steps: [
    { title: 'Поставьте стакан', desc: 'Добавьте стакан на рабочее поле.', hint: 'Выберите стакан во вкладке «Оборудование».' },
    { title: 'Добавьте воду', desc: 'Добавьте воду в установленный стакан.', hint: 'Откройте «Материалы», выберите воду и нажмите на стакан.' },
    { title: 'Подключите термометр', desc: 'Чтобы измерить температуру, соедините порты датчиков термометра и стакана.', hint: 'Нажмите «Соединить», выберите порт термометра, затем порт стакана.' },
  ] },
  heat_water: { name: 'Нагрев воды', steps: [
    { title: 'Поставьте стакан', desc: 'Добавьте стакан на рабочее поле.', hint: 'Выберите стакан во вкладке «Оборудование».' },
    { title: 'Добавьте воду', desc: 'Добавьте воду в стакан.', hint: 'Откройте «Материалы» и выберите воду.' },
    { title: 'Подключите нагреватель', desc: 'Соедините тепловые порты нагревателя и стакана.', hint: 'Включите режим «Соединить» и выберите два тепловых порта.' },
    { title: 'Нагрейте воду', desc: 'Нагрейте воду выше 35 °C.', hint: 'Включите нагреватель и подождите.' },
    { title: 'Наблюдайте кипение', desc: 'Наблюдайте появление пара.', hint: 'Оставьте симуляцию запущенной.' },
  ] },
  transfer_water: { name: 'Переливание жидкости', steps: [
    { title: 'Поставьте первый стакан', desc: 'Добавьте стакан на рабочее поле.', hint: 'Выберите стакан во вкладке «Оборудование».' },
    { title: 'Добавьте воду', desc: 'Налейте воду в первый стакан.', hint: 'Откройте «Материалы» и выберите воду.' },
    { title: 'Поставьте второй стакан', desc: 'Добавьте рядом пустой стакан.', hint: 'Вернитесь во вкладку «Оборудование».' },
    { title: 'Перелейте воду', desc: 'Соедините жидкостные порты сосудов.', hint: 'Включите режим «Соединить».' },
  ] },
  distillation: { name: 'Простая дистилляция', steps: [
    { title: 'Сборка установки', desc: 'Поставьте колбу с водой на стол.', hint: 'Нам понадобится колба с жидкостью.' },
    { title: 'Добавьте оборудование', desc: 'Добавьте нагреватель, термометр и холодильник.', hint: 'Поставьте холодильник (condenser) рядом с колбой.' },
    { title: 'Соединение цепи', desc: 'Соедините нагреватель, колбу и холодильник.', hint: 'Соедините колбу с холодильником.' },
  ] },
  cuso4: { name: 'Раствор CuSO₄', steps: [
    { title: 'Поставьте стакан', desc: 'Добавьте стакан на рабочее поле.', hint: 'Выберите стакан во вкладке «Оборудование».' },
    { title: 'Добавьте сульфат меди', desc: 'Добавьте раствор сульфата меди в стакан.', hint: 'Откройте «Материалы» и выберите CuSO₄.' },
    { title: 'Добавьте воду', desc: 'Добавьте воду в тот же стакан.', hint: 'Выберите H₂O во вкладке «Материалы».' },
    { title: 'Смешайте', desc: 'Получите однородный раствор.', hint: 'Выберите действие «Смешать».' },
  ] },
  kmno4: { name: 'Разбавление KMnO₄', steps: [
    { title: 'Добавьте перманганат', desc: 'Добавьте перманганат калия в сосуд.', hint: 'Откройте вкладку «Вещества» и выберите KMnO₄.' },
    { title: 'Добавьте воду', desc: 'Добавьте воду в сосуд.', hint: 'Выберите H₂O и добавьте его в сосуд.' },
    { title: 'Смешайте', desc: 'Наблюдайте, как меняется концентрация раствора.', hint: 'Перелейте или смешайте компоненты.' },
  ] },
  hcl_naoh: { name: 'Нейтрализация HCl + NaOH', steps: [
    { title: 'Добавьте кислоту', desc: 'Добавьте соляную кислоту в колбу.', hint: 'Выберите HCl и добавьте его в сосуд.' },
    { title: 'Добавьте щёлочь', desc: 'Добавьте гидроксид натрия во второй сосуд.', hint: 'Выберите NaOH и добавьте его в другой сосуд.' },
    { title: 'Смешайте', desc: 'Проведите реакцию нейтрализации.', hint: 'Соедините сосуды и наблюдайте результат.' },
  ] },
  zn_hcl: { name: 'Реакция Zn + HCl', steps: [
    { title: 'Добавьте цинк', desc: 'Поместите цинк в сосуд.', hint: 'Откройте вкладку «Вещества» и выберите Zn.' },
    { title: 'Добавьте кислоту', desc: 'Добавьте соляную кислоту к цинку.', hint: 'Выберите HCl и добавьте его в сосуд.' },
    { title: 'Наблюдайте реакцию', desc: 'Наблюдайте выделение водорода.', hint: 'Соедините компоненты и запустите эксперимент.' },
  ] },
  sulfur_heat: { name: 'Плавление серы', steps: [
    { title: 'Добавьте серу', desc: 'Поместите серу в жаропрочный сосуд.', hint: 'Выберите серу во вкладке «Вещества».' },
    { title: 'Подключите нагреватель', desc: 'Подключите нагреватель к сосуду.', hint: 'Соедините нагреватель с сосудом.' },
    { title: 'Наблюдайте плавление', desc: 'Изучите фазовый переход серы.', hint: 'Поднимите температуру выше 115 °C.' },
  ] },
} as const;

// Level Mode needs a stable step order even while a local dev server still has
// an older JSON message chunk in memory. These strings intentionally mirror
// the level definition and are used only for the guided CuSO₄ lesson.
const levelScenarioCopy = {
  cuso4: {
    ru: { name: "Раствор CuSO₄", steps: [
      { title: "Поставьте стакан", desc: "Добавьте стакан на рабочее поле.", hint: "Откройте «Оборудование» и выберите стакан." },
      { title: "Добавьте сульфат меди", desc: "Добавьте медный купорос в стакан.", hint: "Во вкладке «Материалы» выберите CuSO₄." },
      { title: "Добавьте воду", desc: "Добавьте воду в тот же стакан.", hint: "Выберите H₂O во вкладке «Материалы»." },
      { title: "Смешайте", desc: "Смешайте содержимое, чтобы получить раствор.", hint: "Выберите действие «Смешать»." },
    ] },
    en: { name: "CuSO₄ solution", steps: [
      { title: "Place a beaker", desc: "Add a beaker to the workspace.", hint: "Open Equipment and choose a beaker." },
      { title: "Add copper sulfate", desc: "Add copper sulfate to the beaker.", hint: "In Materials, choose CuSO₄." },
      { title: "Add water", desc: "Add water to the same beaker.", hint: "Choose H₂O in Materials." },
      { title: "Mix", desc: "Mix the contents to create a solution.", hint: "Choose the Mix action." },
    ] },
    uz: { name: "CuSO₄ eritmasi", steps: [
      { title: "Stakan qo'ying", desc: "Ish maydoniga stakan qo'shing.", hint: "«Uskunalar»ni ochib, stakanni tanlang." },
      { title: "Mis sulfat qo'shing", desc: "Stakanga mis sulfat qo'shing.", hint: "«Materiallar»dan CuSO₄ ni tanlang." },
      { title: "Suv qo'shing", desc: "Shu stakanga suv qo'shing.", hint: "«Materiallar»dan H₂O ni tanlang." },
      { title: "Aralashtiring", desc: "Eritma olish uchun tarkibni aralashtiring.", hint: "«Aralashtirish» amalini tanlang." },
    ] },
  },
} as const;

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
  scenarioIntro,
  experimentResult,
  leftOffset = 360,
  rightOffset = 360,
  temperatureConnected = false,
  measuredTemperature = null,
  helpActive = false,
  onHelp,
  onShowHow
}: SandboxDockProps) {
  const ts = useTranslations("sandbox");
  const locale = useLocale();
  const showHowLabel = locale === "ru" ? "Показать как" : locale === "uz" ? "Qandayligini ko'rsatish" : "Show how";
  const localizedScenarioCopy = (scenarioId: string) => {
    const directCopy = levelScenarioCopy[scenarioId as keyof typeof levelScenarioCopy];
    return directCopy ? directCopy[locale as "ru" | "en" | "uz"] ?? directCopy.en : scenarioCopy[scenarioId as keyof typeof scenarioCopy];
  };
  const translateScenario = (key: string, fallback: string, useFallback = false) => useFallback || !ts.has(key) ? fallback : ts(key);
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
      {helpDialogOpen && activeScenario && (() => {
        const scenario = SCENARIOS[activeScenario.id];
        if (!scenario) return null;
        const copy = localizedScenarioCopy(activeScenario.id);
        const useDirectCopy = Boolean(levelScenarioCopy[activeScenario.id as keyof typeof levelScenarioCopy]);
        const isComplete = activeScenario.step >= scenario.steps.length;
        const stepIndex = Math.min(Math.max(activeScenario.step, 0), scenario.steps.length - 1);
        const currentStep = scenario.steps[stepIndex];
        const currentCopy = copy?.steps[stepIndex];
        const scenarioName = translateScenario(scenario.nameKey, copy?.name ?? activeScenario.id, useDirectCopy);
        const stepTitle = isComplete ? 'Сценарий завершён' : translateScenario(currentStep.titleKey, currentCopy?.title ?? currentStep.titleKey, useDirectCopy);
        const stepDescription = isComplete ? 'Все этапы выполнены. Можно повторить сценарий или перейти к следующему уровню.' : translateScenario(currentStep.descKey, currentCopy?.desc ?? currentStep.descKey, useDirectCopy);
        const stepHint = isComplete ? 'Нажмите «Понятно», чтобы закрыть это окно.' : translateScenario(currentStep.hintKey, currentCopy?.hint ?? currentStep.hintKey, useDirectCopy);
        return (
          <div className="fixed inset-0 z-[260] flex items-center justify-center bg-black/65 p-4 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label="Инструкция сценария">
            <button type="button" aria-label="Закрыть инструкцию" className="absolute inset-0 cursor-default" onClick={() => setHelpDialogOpen(false)} />
            <div className="relative z-10 w-full max-w-xl overflow-hidden rounded-3xl border border-lime-300/40 bg-[#101723] text-foreground shadow-[0_0_80px_rgba(163,230,53,.18)]">
              <div className="h-1.5 bg-gradient-to-r from-cyan-400 via-lime-300 to-violet-400" />
              <div className="flex items-start justify-between gap-4 p-5 pb-3 sm:p-7 sm:pb-4">
                <div><span className="text-[10px] font-bold uppercase tracking-[.2em] text-lime-300">Инструкция сценария</span><h2 className="mt-2 text-xl font-black sm:text-2xl">{scenarioName}</h2></div>
                <button type="button" onClick={() => setHelpDialogOpen(false)} className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-border text-lg text-foreground/70 transition hover:bg-white/10 hover:text-white" aria-label="Закрыть">×</button>
              </div>
              <div className="space-y-4 px-5 pb-5 sm:px-7 sm:pb-7">
                <div className="flex items-center gap-2 text-xs font-semibold text-foreground/60"><span className="rounded-full bg-[var(--primary)]/20 px-2.5 py-1 text-[var(--primary-bright)]">{isComplete ? 'Готово' : `Шаг ${activeScenario.step + 1} из ${scenario.steps.length}`}</span><span>Подсказка показывает, что делать сейчас</span></div>
                <div className="rounded-2xl border border-white/10 bg-white/[.04] p-4 sm:p-5"><h3 className="text-lg font-bold text-white sm:text-xl">{stepTitle}</h3><p className="mt-3 text-sm leading-6 text-foreground/75 sm:text-base">{stepDescription}</p><div className="mt-4 rounded-xl border border-cyan-300/20 bg-cyan-300/[.08] p-3.5 text-sm leading-6 text-cyan-100"><span className="mr-1 font-bold text-cyan-300">Что сделать:</span>{stepHint}</div></div>
                {!isComplete && <p className="text-xs leading-5 text-foreground/50">После выполнения действия интерфейс автоматически перейдёт к следующему шагу. Если нужный объект находится в другой вкладке, она будет подсвечена.</p>}
                <button type="button" onClick={() => setHelpDialogOpen(false)} className="w-full rounded-xl bg-[var(--primary)] px-4 py-3 text-sm font-bold text-white transition hover:brightness-110">Понятно</button>
              </div>
            </div>
          </div>
        );
      })()}
      {scenarioIntro && activeScenario && (() => {
        const scenario = SCENARIOS[activeScenario.id];
        if (!scenario) return null;
        const copy = localizedScenarioCopy(activeScenario.id);
        const useDirectCopy = Boolean(levelScenarioCopy[activeScenario.id as keyof typeof levelScenarioCopy]);
        const isComplete = activeScenario.step >= scenario.steps.length;
        const currentStep = isComplete ? scenario.steps[scenario.steps.length - 1] : scenario.steps[activeScenario.step];
        const currentCopy = copy?.steps[isComplete ? copy.steps.length - 1 : activeScenario.step];
        return (
          <div className="pointer-events-auto fixed inset-0 z-[200] flex items-center justify-center bg-black/50 p-4 backdrop-blur-md animate-scenario-intro" role="dialog" aria-modal="true" aria-label="Введение в сценарий" onContextMenu={(event) => event.preventDefault()}>
              <div className="relative flex w-full max-w-2xl flex-col items-center justify-center gap-4 overflow-hidden rounded-[2.5rem] border border-cyan-500/30 bg-slate-950/90 p-10 text-center shadow-[0_0_100px_rgba(34,211,238,0.15)] drop-shadow-2xl backdrop-blur-xl">
                
                {/* Decorative glows */}
                <div className="absolute -top-24 -left-24 h-48 w-48 rounded-full bg-cyan-500/20 blur-3xl" />
                <div className="absolute -bottom-24 -right-24 h-48 w-48 rounded-full bg-violet-500/20 blur-3xl" />
                
                <div className="mb-2 inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/5 px-3 py-1 text-[10px] font-bold uppercase tracking-widest text-cyan-300">
                   {isComplete ? "Этап завершён" : `Шаг ${activeScenario.step + 1}`}
                </div>

                <h1 className="max-w-full text-3xl font-black tracking-tight text-white md:text-5xl">
                  {translateScenario(scenario.nameKey, copy?.name ?? activeScenario.id)}
                </h1>
                
                <div className="mt-4 flex flex-col items-center gap-2">
                  <p className="max-w-lg text-lg font-bold text-white/90 md:text-2xl">{translateScenario(currentStep.titleKey, currentCopy?.title ?? currentStep.titleKey, useDirectCopy)}</p>
                  <p className="max-w-lg text-sm leading-relaxed text-white/60 md:text-base">{translateScenario(currentStep.descKey, currentCopy?.desc ?? currentStep.descKey, useDirectCopy)}</p>
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
            title="Изменить высоту"
            />
          )}
        {/* Active Scenario Guidance */}
        {bottomDockOpen && activeScenario && (() => {
           const scenario = SCENARIOS[activeScenario.id];
           if (!scenario) return null;
           const copy = localizedScenarioCopy(activeScenario.id);
           const useDirectCopy = Boolean(levelScenarioCopy[activeScenario.id as keyof typeof levelScenarioCopy]);
           const isComplete = activeScenario.step >= scenario.steps.length;
           const currentStep = isComplete ? scenario.steps[scenario.steps.length - 1] : scenario.steps[activeScenario.step];
           const currentCopy = copy?.steps[isComplete ? copy.steps.length - 1 : activeScenario.step];
           return (
      <>
        {/* Normal Dock Content */}
               <div className={`flex flex-col gap-1 border-b border-[var(--primary)]/20 bg-[var(--primary)]/10 p-3 text-sm text-foreground transition-opacity duration-500 ${scenarioIntro ? 'opacity-0' : 'opacity-100'}`}>
                 <div className="flex items-center justify-between">
                   <div className="min-w-0">
                     <span className="block text-[10px] font-semibold uppercase tracking-[.16em] text-[var(--muted-foreground)]">ХОД СЦЕНАРИЯ</span>
                     <span className="block truncate font-bold text-[var(--primary-bright)]">{translateScenario(scenario.nameKey, copy?.name ?? activeScenario.id)}</span>
                   </div>
                   <div className="flex items-center gap-2">
                     {activeScenario.id === "measure_water" && activeScenario.step === 2 && <button type="button" onClick={onShowHow} className="rounded-lg border border-violet-300/50 bg-violet-500/10 px-2.5 py-1 text-[10px] font-semibold text-violet-100 transition hover:bg-violet-500/20">{showHowLabel}</button>}
                     <button type="button" onClick={openHelpDialog} className={`rounded-lg border px-2.5 py-1 text-[10px] font-semibold transition ${helpActive ? 'border-lime-300/70 bg-lime-300/20 text-lime-200' : 'border-white/15 text-foreground/80 hover:bg-foreground/10'}`}>ПОМОЩЬ</button>
                   <span className="text-xs text-[var(--muted-foreground)]">
                   {isComplete ? 'ЗАВЕРШЕН' : `ШАГ ${activeScenario.step + 1} ИЗ ${scenario.steps.length}`}
                   </span>
                 </div>
               </div>
               <p className="font-semibold text-xs mb-1">{isComplete ? 'ОТЛИЧНО, СЦЕНАРИЙ ЗАВЕРШЕН!' : translateScenario(currentStep.titleKey, currentCopy?.title ?? currentStep.titleKey, useDirectCopy)}</p>
               {!isComplete && <p className="text-[10px] text-[var(--muted-foreground)] mb-2">{translateScenario(currentStep.descKey, currentCopy?.desc ?? currentStep.descKey, useDirectCopy)} <span className="text-[var(--primary)]">{translateScenario(currentStep.hintKey, currentCopy?.hint ?? currentStep.hintKey, useDirectCopy)}</span></p>}
               
               {/* Step indicators */}
               <div className="flex gap-2">
                 {scenario.steps.map((step: any, idx: number) => {
                   const isPast = activeScenario.step > idx;
                   const isCurrent = activeScenario.step === idx;
                   return (
                     <button type="button" key={idx} onClick={openHelpDialog} className={`flex items-center gap-1.5 rounded-full px-2 py-0.5 text-[10px] font-semibold transition-all ${isPast ? 'bg-emerald-500/20 text-emerald-300' : isCurrent ? 'bg-[var(--primary)]/30 text-[var(--primary-bright)] border border-[var(--primary)]/50' : 'bg-white/5 text-muted-foreground'}`}>
                       {isPast ? <span>✓</span> : <span>{idx + 1}</span>}
                       <span className="truncate max-w-[100px]">{translateScenario(step.titleKey, copy?.steps[idx]?.title ?? step.titleKey, useDirectCopy)}</span>
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
