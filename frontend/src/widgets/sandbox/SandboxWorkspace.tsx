"use client";
import { useLocale, useTranslations } from "next-intl";
import ThemeToggle from '@/shared/ui/ThemeToggle';
import LanguageSwitcher from '@/shared/ui/LanguageSwitcher';

import Link from "next/link";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import {
  ArrowLeft,
  ArrowRight,
  Award,
  Bot,
  BookOpen,
  Eye,
  FlaskConical,
  Sparkles,
  X,
} from "lucide-react";
import { CommandHistory } from "@/engine/history/CommandHistory";
import {
  AddItemCommand,
  RemoveItemCommand,
  PourCommand,
  SceneSnapshotCommand,
  ConnectCommand,
  DisconnectCommand,
  MaterialRemoveCommand,
  RouteEditCommand,
} from "@/engine/history/SandboxCommands";

import { useLabEngine } from "@/engine/app/useLabEngine";
import { useEngineState } from "@/engine/app/useEngineState";
import { createDefaultEquipmentRegistry, ConnectionEngine } from "@/engine";
import { useAuthStore } from "@/stores/auth.store";
import { DebugPanel } from "./DebugPanel";
import { useToast } from "@/shared/ui/ToastContainer";
import type {
  Material,
  Item,
  Connection,
  LibraryItem,
  LibraryTab,
  ContentComponent,
  EquipmentOperation,
  QuickActionState,
  ExperimentResult,
} from "./types";
import { isVessel } from "./types";
import { ParticleCanvas } from "./ParticleCanvas";
import { SandboxCanvas } from "@/widgets/sandbox/SandboxCanvas";

import { Library } from "@/features/sandbox/add-item/ui/Library";
import { Properties, equipmentDescription } from "@/features/sandbox/edit-properties/ui/Properties";
import { ConnectionDialog, PourDialog, ResetConfirmDialog, JasScienceModal } from "@/widgets/sandbox/Dialogs";
import { LaboratoryObject } from "@/engine/objects/LaboratoryObject";
import { SandboxMenuBar } from "./SandboxMenuBar";
import { ShareDialog, serializeSnapshot, SandboxSnapshot, parseSnapshotFromHash, parseShareModeFromHash } from "@/features/sandbox/share-workspace/ui/ShareDialog";
import { useSandboxSync } from "./hooks/useSandboxSync";
import { useSandboxGestures } from "./hooks/useSandboxGestures";
import { useConnections } from "@/features/sandbox/manage-connections/model/useConnections";
import { usePour } from "@/features/sandbox/pour-material/model/usePour";
import { MobileSheet as SandboxMobileSheet } from "@/widgets/sandbox/SandboxPanels";
import { SandboxToolbar } from "./SandboxToolbar";
import { SandboxDock } from "./SandboxDock";
import { EquipmentThumbnail } from "@/entities/equipment/ui/EquipmentRendererRegistry";
import { capacityFor, projectSandboxConnections, projectSandboxItems } from "./sandboxProjection";
import { canPlace } from "./collision";
import { applyAcidBaseTemplate, applyItemPatch, applyOperationToScene } from "./sandboxActions";
import { CodexModal } from "@/widgets/academy/CodexModal";
import type { CodexLabContext } from "@/widgets/academy/CodexModal";
import { mixObjectContents } from "@/engine/simulation/mixContents";
import { LevelIntro } from "./LevelIntro";
import { GuideCursor } from "./GuideCursor";
import { AssistantPanel } from "./AssistantPanel";
import { useAssistant } from "./hooks/useAssistant";
import { learningApi } from '@/entities/learning/api/learning.api';
import { useScenarioRuntime } from './runtime/useScenarioRuntime';
import { collectRuntimeFacts, evaluateRuntimeStep } from './runtime/evaluateRuntimeRule';
import { flattenRuntimeConditions } from './runtime/runtime.types';
import { reconcileSimulationResult } from './runtime/reconcileSimulationResult';
import { canonicalizeLegacyMaterialId, legacyHelpTab, legacyHelpTargets, legacyLevelDefinition, legacyScenarioForExperiment, legacyScenarioForLevel, type SupportedLocale } from './runtime/legacy/legacyScenarioAdapter';
import { runtimeScenarioFromDraft, runtimeStepIndex } from './runtime/normalizeScenarioRuntime';
import { normalizeRuntimeLevelIntro } from './runtime/normalizeRuntimeLevel';
import type { ScenarioDraft } from '@/widgets/admin/scenario/scenario.types';
import { ScenarioGuideOverlay } from './runtime/ScenarioGuideOverlay';
import { ScenarioRuntimeProvider } from './runtime/ScenarioRuntimeProvider';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { getRuntimeEquipment, getRuntimeEquipmentRenderer, getRuntimeMaterial, getRuntimeMaterialTranslation } from './runtime/runtimeCatalog';
import { convertRuntimeUnit } from './runtime/unitConversion';
import type { PortDefinition, PortDirection, PortType } from '@/engine/core/types';

const MobileSheet = SandboxMobileSheet;

const ru = (locale: string) => locale === "ru";
const uz = (locale: string) => locale === "uz";

const registry = createDefaultEquipmentRegistry();
const runtimePortType = (value: unknown): PortType => ({ FLUID: 'Liquid', LIQUID: 'Liquid', GAS: 'Gas', POWER: 'Electric', ELECTRICAL: 'Electric', THERMAL: 'Thermal', SENSOR: 'Sensor', GLASS: 'Glass' }[String(value).toUpperCase()] ?? 'Glass') as PortType;
const runtimePortDirection = (value: unknown): PortDirection => ({ INPUT: 'in', IN: 'in', OUTPUT: 'out', OUT: 'out', BIDIRECTIONAL: 'bidirectional' }[String(value).toUpperCase()] ?? 'bidirectional') as PortDirection;
const runtimePort = (raw: JsonObject): PortDefinition => { const position = raw.position && typeof raw.position === 'object' && !Array.isArray(raw.position) ? raw.position as JsonObject : {}; const connector = String(raw.connector ?? raw.requiredConnector ?? ''); return { id: String(raw.id), name: String(raw.name ?? raw.id), role: typeof raw.role === 'string' ? raw.role : undefined, type: runtimePortType(raw.type ?? raw.medium), position: { x: Number(position.x ?? .5), y: Number(position.y ?? .5) }, direction: runtimePortDirection(raw.direction), capacity: raw.allowMultiple ? undefined : 1, requiredConnector: ['glass-tube', 'rubber-hose', 'wire', 'direct'].includes(connector) ? connector as PortDefinition['requiredConnector'] : undefined, isOpen: true }; };

function HelpArrows({ targets, active, locale }: { targets?: string[]; active: boolean; locale: string }) {
  const [points, setPoints] = useState<Array<{ id: string; x: number; y: number; label: string }>>([]);
  const copy = locale === "ru"
    ? { target: "Сделайте этот шаг" }
    : locale === "uz"
      ? { target: "Shu qadamni bajaring" }
      : { target: "Do this step" };

  useEffect(() => {
    if (!active) {
      const timer = window.setTimeout(() => setPoints([]), 0);
      return () => window.clearTimeout(timer);
    }
    const update = () => {
      const next: Array<{ id: string; x: number; y: number; label: string }> = [];
      const seen = new Set<string>();
      for (const target of targets ?? []) {
        if (seen.has(target)) continue;
        const nodes = Array.from(document.querySelectorAll<HTMLElement>(`[data-help-target="${target}"]`));
        const node = nodes.find((candidate) => {
          const rect = candidate.getBoundingClientRect();
          return rect.width > 0 && rect.height > 0 && rect.bottom > 0 && rect.right > 0 && rect.top < window.innerHeight && rect.left < window.innerWidth;
        });
        if (!node) continue;
        const rect = node.getBoundingClientRect();
        next.push({ id: target, x: rect.left + rect.width / 2, y: rect.top + rect.height / 2, label: copy.target });
        seen.add(target);
        break;
      }
      setPoints(next);
    };
    update();
    const timer = window.setInterval(update, 250);
    window.addEventListener("resize", update);
    window.addEventListener("scroll", update, true);
    return () => {
      window.clearInterval(timer);
      window.removeEventListener("resize", update);
      window.removeEventListener("scroll", update, true);
    };
  }, [active, targets?.join("|"), locale]);

  if (!active || points.length === 0) return null;
  return (
    <div className="pointer-events-none fixed inset-0 z-[240] overflow-hidden" aria-hidden="true">
      <svg className="absolute inset-0 h-full w-full overflow-visible">
        <defs>
          <marker id="guide-arrow" markerWidth="8" markerHeight="8" refX="7" refY="4" orient="auto" markerUnits="strokeWidth">
            <path d="M0,0 L8,4 L0,8 L2,4 z" fill="#9b6cff" />
          </marker>
        </defs>
        {points.map((point, index) => {
          const fromX = point.x;
          const fromY = point.y < 130 ? point.y + 54 : Math.max(24, point.y - 54);
          const toY = point.y < 130 ? point.y + 7 : point.y - 7;
          return <line key={point.id} x1={fromX} y1={fromY} x2={point.x} y2={toY} stroke="#9b6cff" strokeWidth="2" strokeLinecap="round" markerEnd="url(#guide-arrow)" className="animate-pulse drop-shadow-[0_0_6px_rgba(156,107,255,.55)]" />;
        })}
      </svg>
      {points.map((point, index) => {
        const top = point.y < 130 ? point.y + 58 : Math.max(8, point.y - 82);
        return <span key={`${point.id}-label`} className="absolute -translate-x-1/2 rounded-lg border border-violet-300/60 bg-slate-950/95 px-2.5 py-1 text-[10px] font-bold text-violet-100 shadow-[0_0_16px_rgba(156,107,255,.35)]" style={{ left: point.x, top }}>{point.label}</span>;
      })}
    </div>
  );
}

function LevelRewardOverlay({ level, nextLevel, onNext, onAcademy, onClose }: { level: number; nextLevel?: number; onNext: () => void; onAcademy: () => void; onClose: () => void }) {
  const currentLocale = useLocale();
  const rewardLocale = currentLocale === "ru" ? "ru" : currentLocale === "uz" ? "uz" : "en";
  const copy = {
    ru: { eyebrow: "Уровень пройден", title: "Отличная работа!", description: "Эксперимент завершён. Ваш результат сохранён в прогрессе академии.", badge: "Значок исследователя", nextTitle: "Следующий уровень готов", nextDescription: "Продолжайте обучение и откройте новый эксперимент.", nextButton: (value: number) => `Перейти к уровню ${value}`, academyButton: "Открыть академию", closeButton: "Остаться в лаборатории" },
    en: { eyebrow: "Level complete", title: "Excellent work!", description: "The experiment is complete. Your result has been saved to academy progress.", badge: "Researcher badge", nextTitle: "The next level is ready", nextDescription: "Continue learning and unlock a new experiment.", nextButton: (value: number) => `Go to level ${value}`, academyButton: "Open academy", closeButton: "Stay in the lab" },
    uz: { eyebrow: "Daraja yakunlandi", title: "Ajoyib ish!", description: "Tajriba yakunlandi. Natijangiz akademiya taraqqiyotiga saqlandi.", badge: "Tadqiqotchi nishoni", nextTitle: "Keyingi daraja tayyor", nextDescription: "O'qishni davom ettiring va yangi tajribani oching.", nextButton: (value: number) => `${value}-darajaga o'tish`, academyButton: "Akademiyani ochish", closeButton: "Laboratoriyada qolish" },
  }[rewardLocale];
  return (
    <div className="fixed inset-0 z-[250] flex items-center justify-center bg-slate-950/70 p-4 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label={copy.eyebrow}>
      <div className="relative w-full max-w-md overflow-hidden rounded-3xl border border-lime-300/50 bg-card p-6 text-center text-foreground shadow-[0_0_90px_rgba(132,204,22,.28)] animate-fade-in-up sm:p-8">
        <button type="button" onClick={onClose} className="absolute right-3 top-3 rounded-lg p-1.5 text-muted-foreground transition hover:bg-muted hover:text-foreground" aria-label={copy.closeButton}><X size={17} /></button>
        <div className="pointer-events-none absolute inset-x-8 top-0 flex justify-between text-lime-300/80"><Sparkles size={18} className="animate-bounce" /><Sparkles size={13} className="mt-7 animate-pulse text-cyan-300" /><Sparkles size={16} className="animate-bounce text-violet-300" /></div>
        <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full border-4 border-lime-300 bg-lime-400/15 text-lime-300 shadow-[0_0_35px_rgba(163,230,53,.45)] animate-[pulse_1.8s_ease-in-out_infinite]"><Award size={42} /></div>
        <p className="mt-5 text-[11px] font-black uppercase tracking-[.2em] text-lime-300">{copy.eyebrow}</p>
        <h2 className="mt-2 text-3xl font-black">{copy.title}</h2>
        <p className="mt-3 text-sm leading-6 text-muted-foreground">{copy.description}</p>
        <p className="mt-5 text-xs font-semibold text-muted-foreground">{copy.badge}</p>
        <div className="mt-6 rounded-2xl border border-[var(--primary)]/30 bg-[var(--primary)]/10 p-4 text-left">
          <p className="font-bold text-[var(--primary-bright)]">{nextLevel ? copy.nextTitle : copy.academyButton}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">{nextLevel ? copy.nextDescription : copy.description}</p>
        </div>
        <div className="mt-5 flex flex-col gap-2 sm:flex-row-reverse">
          <button type="button" onClick={nextLevel ? onNext : onAcademy} className="flex min-h-11 flex-1 items-center justify-center gap-2 rounded-xl bg-[var(--primary)] px-4 py-3 text-sm font-bold text-white transition hover:brightness-110">{nextLevel ? copy.nextButton(nextLevel) : copy.academyButton}<ArrowRight size={16} /></button>
          <button type="button" onClick={onClose} className="min-h-11 rounded-xl border border-border px-4 py-3 text-sm font-semibold text-muted-foreground transition hover:bg-muted hover:text-foreground">{copy.closeButton}</button>
        </div>
      </div>
    </div>
  );
}

export function SandboxWorkspace({ previewDraft, previewCatalog, embedded = false }: { previewDraft?: ScenarioDraft; previewCatalog?: { equipment?: JsonObject[]; materials?: JsonObject[] }; embedded?: boolean } = {}) {
  const ts = useTranslations("sandbox");

  const pathname = usePathname();
  const router = useRouter();
  const query = useSearchParams();
  const locale = pathname.split("/")[1] || "en";
  const resultFallback = locale === "ru"
    ? { title: "Результат получен", description: "Сценарий успешно завершён." }
    : locale === "uz"
      ? { title: "Natija olindi", description: "Ssenariy muvaffaqiyatli yakunlandi." }
      : { title: "Result obtained", description: "The scenario was completed successfully." };
  const resultText = (key: string, fallback: string) => ts.has(key) ? ts(key) : fallback;
  const workspaceId = query.get("workspace");
  const template = query.get("template");
  const academyLevel = query.get("level");
  const canonicalLevelId = query.get("levelId");
  const learningAttemptId = query.get("attempt");
  const levelDefinition = legacyLevelDefinition(academyLevel);
  const loadedScenarioRuntime = useScenarioRuntime({ attemptId: learningAttemptId, levelId: canonicalLevelId, legacyLevelNumber: academyLevel, locale: (['en', 'ru', 'uz'].includes(locale) ? locale : 'en') as 'en' | 'ru' | 'uz' });
  const previewRuntime = previewDraft ? runtimeScenarioFromDraft(previewDraft, (['en', 'ru', 'uz'].includes(locale) ? locale : 'en') as 'en' | 'ru' | 'uz', previewCatalog) : null;
  const scenarioRuntime = previewRuntime ? { scenario: previewRuntime, level: null, attempt: null, loading: false, error: null } : loadedScenarioRuntime;
  const runtimeLevelIntro = normalizeRuntimeLevelIntro(scenarioRuntime.level, scenarioRuntime.attempt, scenarioRuntime.scenario);
  const activeLevelIntro = runtimeLevelIntro ?? levelDefinition;
  const sandboxMode = previewRuntime ? 'ADMIN_PREVIEW' : scenarioRuntime.scenario ? 'LEARNING' : 'NORMAL';
  const levelMode = Boolean(levelDefinition || learningAttemptId || scenarioRuntime.scenario);
  const levelLabel = levelDefinition
    ? `${locale === "ru" ? "Уровень" : locale === "uz" ? "Daraja" : "Level"} ${levelDefinition.id} · ${levelDefinition.title[locale as SupportedLocale] ?? levelDefinition.title.en}`
    : undefined;
  const adventureFromUrl = query.get("adventure") === "1";
  const equipmentId = query.get("equipmentId");
  const experimentId = query.get("experimentId");
  const materialId = query.get("materialId");
  const { isAuthenticated, isLoading: authLoading, fetchUser } = useAuthStore();
  const canvasRef = useRef<HTMLDivElement>(null);
  const [sessionId, setSessionId] = useState<string | null>(null);
  const { engine } = useLabEngine(workspaceId ?? undefined, sessionId ?? undefined);
  const { messages: assistantMessages, sendMessage: sendAssistantMessage, executeAction: executeAssistantAction, isTeamChat } = useAssistant(engine, registry, workspaceId);
  const [assistantOpen, setAssistantOpen] = useState(false);
  const [history] = useState(() => new CommandHistory());
  const mixTimersRef = useRef<Map<string, number>>(new Map());

  useEffect(() => () => {
    for (const timer of mixTimersRef.current.values()) window.clearTimeout(timer);
    mixTimersRef.current.clear();
  }, []);

  // Older snapshots could contain solid samples inside a pipette. Clean that
  // invalid state once when loading the scene so the UI and transfer logic
  // agree on the same physical model.
  useEffect(() => {
    if (!engine) return;
    let changed = false;
    for (const object of engine.workspace.scene.objects.values()) {
      if (object.type !== "pipette" && object.type !== "burette") continue;
      const liquidContents = object.contents.filter((content) => content.phase === "liquid" || content.phase === "aqueous");
      if (liquidContents.length === object.contents.length) continue;
      object.contents = liquidContents;
      object.properties.massG = 0;
      object.properties.moles = liquidContents.reduce((sum, content) => sum + Number(content.molarAmount ?? 0), 0);
      object.properties.volumeMl = liquidContents.reduce((sum, content) => sum + Number(content.amount ?? 0), 0);
      object.properties.liquidLevel = Math.min(1, Number(object.properties.volumeMl) / Number(object.properties.capacityMl ?? object.metadata.capacity ?? 10));
      object.material = liquidContents[0]
        ? { id: liquidContents[0].materialId, name: liquidContents[0].name, formula: liquidContents[0].formula, state: liquidContents[0].phase, color: liquidContents[0].color }
        : undefined;
      changed = true;
    }
    if (changed) engine.notifyUpdate();
  }, [engine]);

  const resolveGuideTargets = (scenarioId?: string, step?: number) => {
    if (!scenarioId || step === undefined) return undefined;
    const runtime = scenarioRuntime.scenario;
    if (!runtime || runtime.source === 'legacy') return legacyHelpTargets(scenarioId, step);
    const current = runtime.steps[step];
    if (!current) return undefined;
    const aliasTypes = new Map(runtime.initialScene?.objects.map((item) => [item.alias, item.equipmentId]));
    const targets = current.hints.flatMap((hint) => [hint.targetAlias && aliasTypes.get(hint.targetAlias), hint.targetAlias, hint.targetPortId && hint.targetAlias ? `port:${aliasTypes.get(hint.targetAlias) ?? hint.targetAlias}:${hint.targetPortId}` : undefined, hint.fromAlias && aliasTypes.get(hint.fromAlias), hint.toAlias && aliasTypes.get(hint.toAlias)]).filter(Boolean) as string[];
    flattenRuntimeConditions(current.completionRule).forEach((condition) => { if (condition.materialId) targets.push(condition.materialId); });
    return [...new Set(targets)];
  };
  const getHelpTab = (scenarioId?: string, step?: number): LibraryTab => {
    if (!scenarioId || step === undefined) return "equipment";
    const runtime = scenarioRuntime.scenario;
    if (!runtime || runtime.source === 'legacy') return legacyHelpTab(scenarioId, step);
    return runtime.steps[step] && flattenRuntimeConditions(runtime.steps[step].completionRule).some((condition) => condition.type === 'MATERIAL_PRESENT') ? 'materials' : 'equipment';
  };
  const handleHelp = () => {
    if (!activeScenario) return;
    setLibraryTab(getHelpTab(activeScenario.id, activeScenario.step));
    setLeftPanelVisible(true);
    // The guide must teach the interaction in order. In particular, do not
    // silently enter Connect mode: first show the user the Connect button.
    setHelpActive((value) => !value);
  };
  const { tick } = useEngineState(engine);

  const items = useMemo<Item[]>(() => (engine ? projectSandboxItems(engine, registry) : []), [engine, tick]);
  const [marquee, setMarquee] = useState<{ startX: number; startY: number; currentX: number; currentY: number } | null>(null);
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
  const selectedId = selectedIds.size === 1 ? Array.from(selectedIds)[0] : null;
  const [libraryTab, setLibraryTab] = useState<LibraryTab>("equipment");
  const [tool, setTool] = useState<"select" | "pan" | "connect">("select");
  const [jasScienceLevel, setJasScienceLevel] = useState<number | null>(null);
          
  const [zoom, setZoom] = useState(1);
  const [pan, setPan] = useState({ x: 0, y: 0 });

  useEffect(() => {
    const handleCenter = () => {
      setPan({ x: 0, y: 0 });
      setZoom(1);
    };
    const handleZoomToFit = () => {
      if (items.length === 0) {
        handleCenter();
        return;
      }
      const minX = Math.min(...items.map((i) => i.x));
      const minY = Math.min(...items.map((i) => i.y));
      const maxX = Math.max(...items.map((i) => i.x + i.w * (i.scaleX ?? i.scale)));
      const maxY = Math.max(...items.map((i) => i.y + i.h * (i.scaleY ?? i.scale)));
      const padding = 100;
      const vw = window.innerWidth;
      const vh = window.innerHeight;
      const width = maxX - minX + padding * 2;
      const height = maxY - minY + padding * 2;
      const newZoom = Math.min(2, Math.max(0.5, Math.min(vw / width, vh / height)));

      setZoom(newZoom);
      setPan({
        x: (vw / newZoom - (maxX - minX)) / 2 - minX,
        y: (vh / newZoom - (maxY - minY)) / 2 - minY,
      });
    };

    window.addEventListener("sandbox-center-scene", handleCenter);
    window.addEventListener("sandbox-zoom-to-fit", handleZoomToFit);
    return () => {
      window.removeEventListener("sandbox-center-scene", handleCenter);
      window.removeEventListener("sandbox-zoom-to-fit", handleZoomToFit);
    };
  }, [items]);

  const connections = useMemo<Connection[]>(() => (engine ? projectSandboxConnections(engine) : []), [engine, tick]);
  

    const { addToast } = useToast();
  
  


  // When Sandbox is opened from the academy, the learner must immediately see
  // the objective and the current step instead of landing on an empty canvas.
  const [bottomDockOpen, setBottomDockOpen] = useState(() => Boolean(academyLevel));
  const [bottomDockTab, setBottomDockTab] = useState<"Events" | "Measurements" | "Charts" | "Warnings" | "Simulation">("Events");
  const [mobilePanel, setMobilePanel] = useState<"library" | "inspector" | "dock" | null>(null);
  const [measurementSamples, setMeasurementSamples] = useState<Array<{ time: string; temperature: number; volume: number; mass: number; pressure: number }>>([]);
    const [leftPanelWidth, setLeftPanelWidth] = useState(360);
  const [rightPanelWidth, setRightPanelWidth] = useState(360);
  const [leftPanelVisible, setLeftPanelVisible] = useState(true);
  const [rightPanelVisible, setRightPanelVisible] = useState(true);
  const [showNavbar, setShowNavbar] = useState(true);
  const [showGrid, setShowGrid] = useState(true);
  const [snapToGrid, setSnapToGrid] = useState(false);
  const [shareMode, setShareMode] = useState<"viewer" | "editor" | null>(null);
  const [collisionItemId, setCollisionItemId] = useState<string | null>(null);
  const {
    connectSource, setConnectSource,
    connectSourcePort, setConnectSourcePort,
    connectionSnap, setConnectionSnap,
    connectionDraft, setConnectionDraft,
    connectionPointer, setConnectionPointer,
    selectedConnectionId, setSelectedConnectionId,
    portCompatibility, startPortConnection: hookStartPortConnection, hoverPort: hookHoverPort, cancelConnection: hookCancelConnection, connectionEngine
  } = useConnections(engine, items, (id) => setSelectedIds(id ? new Set([id]) : new Set()), setTool);

  useEffect(() => {
    if (tool !== "connect") {
      // Connection gesture state is external interaction state and must be reset
      // when the active editor tool changes.
      setConnectSource(null);
      setConnectSourcePort(null);
      setConnectionPointer(null);
      setConnectionSnap(null);
    }
  }, [setConnectSource, setConnectSourcePort, setConnectionPointer, setConnectionSnap, tool]);
    const [runState, setRunState] = useState<"Draft" | "Validating" | "Ready" | "Blocked" | "Running" | "Paused" | "Finished" | "Error">("Draft");
  const [simulationSpeed, setSimulationSpeed] = useState(1);
  const panelResizeRef = useRef<{ side: "left" | "right"; startX: number; startWidth: number } | null>(null);
  const templateLoaded = useRef(false);
  const runtimeSceneLoaded = useRef<string | null>(null);
  const codexContextLoaded = useRef(false);
  
  const [, setQuickAction] = useState<QuickActionState | null>(null);
  const initialScenarioId = scenarioRuntime.scenario?.id ?? legacyScenarioForLevel(academyLevel);
  const [activeScenario, setActiveScenario] = useState<{ id: string; step: number } | null>(() => initialScenarioId ? { id: initialScenarioId, step: 0 } : null);
  const [authoritativeFacts, setAuthoritativeFacts] = useState({ reactionIds: [] as string[], formedMaterialIds: [] as string[] });
  const [scenarioIntro, setScenarioIntro] = useState(() => Boolean(initialScenarioId));
  const [levelIntroOpen, setLevelIntroOpen] = useState(() => Boolean(activeLevelIntro));
  const [codexOpen, setCodexOpen] = useState(adventureFromUrl);
  const [codexTarget, setCodexTarget] = useState<CodexLabContext | undefined>();

  useEffect(() => {
    if (!engine || codexContextLoaded.current || (!equipmentId && !experimentId && !materialId)) return;
    codexContextLoaded.current = true;
    if (equipmentId && registry.get(equipmentId)) {
      const object = registry.create(equipmentId, { id: `codex-${equipmentId}` });
      object.position = { x: 420, y: 260 };
      engine.workspace.scene.add(object);
      engine.notifyUpdate();
      addToast(`Adventure equipment loaded: ${equipmentId}`, "success");
    }
    const scenarioId = legacyScenarioForExperiment(experimentId);
    if (scenarioId) {
      // This effect hydrates the existing sandbox UI from an explicit Codex URL context.
      // eslint-disable-next-line react-hooks/set-state-in-effect
      setActiveScenario({ id: scenarioId, step: 0 });
      setBottomDockOpen(true);
      setScenarioIntro(true);
      addToast(`Adventure experiment loaded: ${experimentId}`, "success");
    }
    if (materialId) {
      setLibraryTab("materials");
      addToast(`Material loaded: ${materialId}`, "success");
    }
  }, [engine, equipmentId, experimentId, materialId, addToast]);

  useEffect(() => {
    const scenarioId = scenarioRuntime.scenario?.id ?? legacyScenarioForLevel(academyLevel);
    if (!scenarioId) return;
    const initTimer = window.setTimeout(() => {
      if (activeScenario?.id !== scenarioId) setActiveScenario({ id: scenarioId, step: runtimeStepIndex(scenarioRuntime.attempt) });
      setBottomDockOpen(true);
    }, 0);
    const introTimer = window.setTimeout(() => setScenarioIntro(false), 4200);
    return () => { window.clearTimeout(initTimer); window.clearTimeout(introTimer); };
  }, [academyLevel, activeScenario?.id, scenarioRuntime.attempt, scenarioRuntime.scenario?.id]);

  useEffect(() => {
    setAuthoritativeFacts({ reactionIds: [], formedMaterialIds: [] });
  }, [scenarioRuntime.scenario?.id]);

  useEffect(() => {
    if (!activeLevelIntro || previewRuntime) return;
    const timer = window.setTimeout(() => setLevelIntroOpen(true), 0);
    return () => window.clearTimeout(timer);
  }, [activeLevelIntro, previewRuntime]);
  const [helpActive, setHelpActive] = useState(false);
  const [scenarioHintIndex, setScenarioHintIndex] = useState(0);
  const [guideDemoOpen, setGuideDemoOpen] = useState(false);
  const [experimentResult, setExperimentResult] = useState<ExperimentResult | null>(null);
  const [rewardDismissed, setRewardDismissed] = useState(false);
  const [shareSnapshot, setShareSnapshot] = useState<SandboxSnapshot | null>(null);
  const [clearWorkspaceModal, setClearWorkspaceModal] = useState(false);
  const completedScenarioRef = useRef<string | null>(null);

  useEffect(() => {
    if (!helpActive || !activeScenario) return;
    setLibraryTab(getHelpTab(activeScenario.id, activeScenario.step));
  }, [activeScenario?.id, activeScenario?.step, helpActive]);

  useEffect(() => {
    const runtime = scenarioRuntime.scenario;
    if (!engine || !runtime?.initialScene || runtime.source === 'legacy' || runtimeSceneLoaded.current === runtime.id || workspaceId || template) return;
    runtimeSceneLoaded.current = runtime.id;
    const aliases = new Map<string, LaboratoryObject>();
    let skipped = 0;
    for (const sceneItem of runtime.initialScene.objects) {
      const equipmentDefinition = getRuntimeEquipment(runtime.catalog, sceneItem.equipmentId);
      const equipmentType = getRuntimeEquipmentRenderer(runtime.catalog, sceneItem.equipmentId) ?? (registry.get(sceneItem.equipmentId) ? sceneItem.equipmentId : '');
      if (!equipmentType || !registry.get(equipmentType)) { skipped += 1; continue; }
      const object = registry.create(equipmentType, { id: sceneItem.id, position: { x: sceneItem.x, y: sceneItem.y } });
      if (equipmentDefinition?.ports.length) object.ports = equipmentDefinition.ports.map(runtimePort);
      object.rotation = sceneItem.rotation;
      object.boundingBox = { x: sceneItem.x, y: sceneItem.y, width: sceneItem.width, height: sceneItem.height };
      object.metadata = { ...object.metadata, scenarioAlias: sceneItem.alias, equipmentId: sceneItem.equipmentId, equipmentCode: equipmentDefinition?.code, rendererKey: equipmentType };
      object.properties = { ...object.properties, temperature: sceneItem.temperatureC, contentsTemperature: sceneItem.temperatureC, volumeMl: sceneItem.contents.reduce((sum, content) => sum + (convertRuntimeUnit(content.amount, content.unit, 'mL') ?? 0), 0) };
      object.contents = sceneItem.contents.map((content) => { const material = getRuntimeMaterial(runtime.catalog, content.materialId); return { materialId: content.materialId, name: getRuntimeMaterialTranslation(runtime.catalog, content.materialId, (['en', 'ru', 'uz'].includes(locale) ? locale : 'en') as 'en' | 'ru' | 'uz'), formula: material?.formula ?? content.materialId, amount: content.amount, unit: content.unit, phase: String(material?.phase ?? content.phase).toLowerCase(), color: material?.color ?? '#94a3b8', metadata: { opacity: material?.opacity ?? 1 } }; });
      engine.workspace.scene.add(object);
      aliases.set(sceneItem.alias, object);
    }
    const connectionEngine = new ConnectionEngine();
    for (const link of runtime.initialScene.connections) {
      const from = aliases.get(link.fromAlias), to = aliases.get(link.toAlias);
      if (!from || !to) continue;
      try { engine.workspace.scene.connect({ ...connectionEngine.create(from, link.fromPortId, to, link.toPortId, [...engine.workspace.scene.connections.values()]), id: link.id }); } catch { skipped += 1; }
    }
    engine.notifyUpdate();
    if (skipped) addToast(`${skipped} Scenario resource${skipped === 1 ? '' : 's'} could not be resolved in the Sandbox registry.`, 'error');
  }, [addToast, engine, scenarioRuntime.scenario, template, workspaceId]);

  useEffect(() => {
    if (!engine || templateLoaded.current) return;
    if (template === "fracture") {
      templateLoaded.current = true;
      const vessel = registry.create("beaker", { id: "fracture-fixture" });
      vessel.position = { x: 320, y: 250 };
      vessel.metadata.displayName = "Fracture fixture";
      vessel.properties = {
        ...vessel.properties,
        integrity: "cracked",
        broken: false,
        thermalState: { ambientTemperature: 24.5, surfaceTemperature: 400, wallTemperature: 160, bottomTemperature: 400, contentsTemperature: 24.5, gasTemperature: 24.5, stress: 34 },
      };
      engine.workspace.scene.add(vessel);
      engine.notifyUpdate();
      addToast("Fracture sequence loaded", "info");
      return;
    }
    const data = parseSnapshotFromHash();
    if (data && data.version === 2) {
      const mode = parseShareModeFromHash() ?? "viewer";
      templateLoaded.current = true;
      engine.workspace.scene.objects.clear();
      engine.workspace.scene.connections.clear();
      for (const obj of data.objects) {
        engine.workspace.scene.objects.set(obj.id, LaboratoryObject.deserialize(obj));
      }
      for (const conn of data.connections) {
        engine.workspace.scene.connections.set(conn.id, conn);
      }
      // eslint-disable-next-line react-hooks/set-state-in-effect
      if (data.scenario) setActiveScenario(data.scenario);
      if (data.viewport) {
        setZoom(data.viewport.zoom || 1);
        setPan({ x: data.viewport.panX || 0, y: data.viewport.panY || 0 });
      }
      engine.notifyUpdate();
      setShareMode(mode);
      addToast("Эксперимент загружен по ссылке", "success");
      if (typeof window !== "undefined") window.history.replaceState(null, "", window.location.pathname + window.location.search);
    }
  }, [engine, addToast, template]);

  const selected = items.find((item) => item.id === selectedId);

  const thermometerMeasuresItem = useCallback((thermometer: Item, itemId: string) => {
    if (thermometer.type !== "thermometer") return false;
    const sensorLink = connections.find((connection) => connection.medium === "sensor" && (connection.from === thermometer.id || connection.to === thermometer.id));
    if (sensorLink) return (sensorLink.from === thermometer.id ? sensorLink.to : sensorLink.from) === itemId;
    const target = items.find((candidate) => candidate.id === itemId);
    if (!target || !isVessel(target)) return false;
    const probe = { x: thermometer.x + thermometer.w / 2, y: thermometer.y + thermometer.h };
    return probe.x >= target.x && probe.x <= target.x + target.w && probe.y >= target.y && probe.y <= target.y + target.h;
  }, [connections, items]);

  const temperatureConnected = useCallback((itemId: string) => {
    const item = items.find((candidate) => candidate.id === itemId);
    if (!item) return false;
    if (item.type === "thermometer") return items.some((candidate) => isVessel(candidate) && thermometerMeasuresItem(item, candidate.id));
    return isVessel(item) && items.some((candidate) => candidate.type === "thermometer" && thermometerMeasuresItem(candidate, itemId));
  }, [items, thermometerMeasuresItem]);

  const temperatureReading = useCallback(
    (itemId: string) => {
      const thermometer = items.find((item) => item.id === itemId && item.type === "thermometer");
      if (thermometer) return temperatureConnected(thermometer.id) && thermometer.measurementStatus !== 'OVER RANGE' ? thermometer.measuredTemperatureC ?? null : null;
      const target = items.find((item) => item.id === itemId);
      if (!target || !isVessel(target)) return null;
      const measuringThermometer = items.find((item) => item.type === "thermometer" && thermometerMeasuresItem(item, itemId) && item.measurementStatus !== 'OVER RANGE');
      return measuringThermometer?.measuredTemperatureC ?? null;
    },
    [items, temperatureConnected, thermometerMeasuresItem]
  );

  useEffect(() => {
    const measuredTemperature = selected ? temperatureReading(selected.id) : null;
    if (!selected || measuredTemperature === null) return;
    const sample = () => setMeasurementSamples((current) => [
      ...current,
      { time: new Date().toLocaleTimeString([], { minute: "2-digit", second: "2-digit" }), temperature: measuredTemperature, volume: 0, mass: 0, pressure: 0 },
    ].slice(-30));
    sample();
    const timer = window.setInterval(sample, 1000);
    return () => window.clearInterval(timer);
  }, [selected?.id, temperatureReading]);

  const handleQuickAction = (action: QuickActionState) => {
    if (action.action === "material") {
      setLibraryTab("materials");
      setMobilePanel("library");
    } else if (action.action === "pour") {
      setPourSource(action.sourceId || null);
    } else if (action.action === "heat") {
      const item = items.find((i) => i.id === action.sourceId);
      if (item && item.capabilities?.heater) {
        applyOperation(item, item.operation === "heating" ? "idle" : "heating");
      } else {
        addToast("Невозможно нагреть этот предмет напрямую", "error");
      }
    } else if (action.action === "tube") {
      if (action.sourceId) {
        setConnectSource(action.sourceId);
        setTool("connect");
      }
    } else {
      setQuickAction(action);
    }
  };

  const [authChecked, setAuthChecked] = useState(false);
  const startPanelResize = useCallback(
    (side: "left" | "right", event: React.PointerEvent<HTMLDivElement>) => {
      event.preventDefault();
      panelResizeRef.current = { side, startX: event.clientX, startWidth: side === "left" ? leftPanelWidth : rightPanelWidth };
    },
    [leftPanelWidth, rightPanelWidth]
  );

  useEffect(() => {
    const onPointerMove = (event: PointerEvent) => {
      const resize = panelResizeRef.current;
      if (!resize) return;
      const delta = event.clientX - resize.startX;
      if (resize.side === "left") setLeftPanelWidth(Math.min(480, Math.max(300, resize.startWidth + delta)));
      else setRightPanelWidth(Math.min(480, Math.max(300, resize.startWidth - delta)));
    };
    const onPointerUp = () => {
      panelResizeRef.current = null;
    };
    window.addEventListener("pointermove", onPointerMove);
    window.addEventListener("pointerup", onPointerUp);
    return () => {
      window.removeEventListener("pointermove", onPointerMove);
      window.removeEventListener("pointerup", onPointerUp);
    };
  }, []);

  useEffect(() => {
    if (authChecked) return;
    if (isAuthenticated) {
      window.setTimeout(() => setAuthChecked(true), 0);
      return;
    }
    if (authLoading) return;
    let cancelled = false;
    void fetchUser().finally(() => {
      if (!cancelled) setAuthChecked(true);
    });
    return () => {
      cancelled = true;
    };
  }, [authLoading, fetchUser, isAuthenticated, authChecked]);

  
  

  

  
  
    const getWorkspaceSnapshot = useCallback(() => {
    if (!engine) return null;
    return {
      version: 1 as const,
      scene: engine.workspace.scene.serialize(),
      simulation: {
        running: false,
        time: 0,
        pan,
        zoom,
      } as any,
      updatedAt: new Date().toISOString(),
    } as any;
  }, [engine, pan.x, pan.y, zoom]);

  const { syncStatus, eventLog, queueWorkspaceEvent, historyAction, stateVersionRef } = useSandboxSync({
    engine,
    workspaceId,
    sessionId,
    setSessionId,
    history,
    isAuthenticated,
    authChecked,
    showToast: addToast,
    registry,
    pan,
    getWorkspaceSnapshot,
    zoom,
    setPan,
    setZoom
  });

  const { pourSource, setPourSource, pourAmount, setPourAmount, pourAnimation, spillAnimation, pour, triggerPourAnimation } = usePour(engine, items, queueWorkspaceEvent, history);

  const physicsHistoryRef = useRef<Record<string, number>>({});
  useEffect(() => {
    for (const item of items) {
      const seen = physicsHistoryRef.current[item.id] ?? 0;
      const entries = (item.history ?? []).slice(seen);
      for (const detail of entries) {
        queueWorkspaceEvent('PHYSICS_EVENT', { itemId: item.id, detail });
        if (detail === 'Contents released into virtual workspace') addToast(`Сосуд «${item.name}» разбит: содержимое разлито.`, 'error');
      }
      physicsHistoryRef.current[item.id] = (item.history ?? []).length;
    }
  }, [items, queueWorkspaceEvent, addToast]);

  const runExperiment = useCallback(() => {
    if (!engine || items.length === 0) {
      setRunState("Blocked");
      addToast("Добавьте оборудование перед запуском эксперимента.", "error");
      return;
    }
    setRunState("Validating");
    window.setTimeout(() => {
      if (collisionItemId !== null) {
        setRunState("Blocked");
        addToast("Сначала устраните пересечение оборудования.", "error");
        return;
      }
      engine.setSimulationSpeed(simulationSpeed);
      engine.setSimulationRunning(true);
      setRunState("Running");
      queueWorkspaceEvent("SIMULATION_STARTED", { objectCount: items.length });
    }, 0);
  }, [addToast, collisionItemId, engine, items.length, queueWorkspaceEvent, setRunState, simulationSpeed]);

  const pauseExperiment = useCallback(() => {
    engine?.setSimulationRunning(false);
    setRunState("Paused");
    queueWorkspaceEvent("SIMULATION_PAUSED", {});
  }, [engine, queueWorkspaceEvent, setRunState]);

  const stopExperiment = useCallback(() => {
    engine?.setSimulationRunning(false);
    setRunState("Ready");
    queueWorkspaceEvent("SIMULATION_STOPPED", {});
  }, [engine, queueWorkspaceEvent, setRunState]);

  useEffect(() => {
    if (!activeScenario || !engine) return;
    const scenario = scenarioRuntime.scenario;
    if (!scenario) return;
    let nextStep = activeScenario.step;
    const facts = collectRuntimeFacts(items, connections, authoritativeFacts, eventLog);
    while (nextStep < scenario.steps.length && evaluateRuntimeStep(scenario.steps[nextStep], facts, engine)) nextStep += 1;
    if (nextStep === activeScenario.step) return;
    if (nextStep >= scenario.steps.length) {
      if (completedScenarioRef.current === activeScenario.id) return;
      completedScenarioRef.current = activeScenario.id;
      if (learningAttemptId) {
        const stateVersion = stateVersionRef.current;
        void learningApi.sendEvent(learningAttemptId, {
          eventId: crypto.randomUUID(),
          type: 'SCENARIO_COMPLETED',
          payload: { scenarioId: activeScenario.id },
          experimentStateVersion: stateVersion,
        }).then(() => learningApi.complete(learningAttemptId, {
          idempotencyKey: crypto.randomUUID(),
          stateVersion,
        }, locale)).catch((reason) => addToast(reason instanceof Error ? reason.message : 'Прогресс уровня не сохранён', 'error'));
      }
      const vessel = items.find((item) => item.type === "beaker" && item.temperature >= 75);
      const finalTemperature = vessel?.temperature ?? selected?.temperature ?? 24.5;
      const result: ExperimentResult = { scenarioId: activeScenario.id, title: scenario.successTitle || resultText("result.generic.title", resultFallback.title), description: scenario.successDescription || resultText("result.generic.description", resultFallback.description), temperatureC: finalTemperature, volumeMl: vessel?.volumeMl ?? selected?.volumeMl ?? 0 };
      window.setTimeout(() => {
        setExperimentResult(result);
        setRewardDismissed(false);
        setActiveScenario((current) => (current ? { ...current, step: nextStep } : current));
        queueWorkspaceEvent("RESULT_OBTAINED", { scenarioId: activeScenario.id, temperatureC: finalTemperature, volumeMl: result.volumeMl });
        addToast(result.description, "success");
      }, 0);
    } else {
      window.setTimeout(() => {
        setActiveScenario((current) => (current ? { ...current, step: nextStep } : current));
      }, 0);
    }
  }, [activeScenario, authoritativeFacts, connections, engine, eventLog, items, selected, queueWorkspaceEvent, addToast, scenarioRuntime.scenario]);

  useEffect(() => {
    const dialog = document.querySelector<HTMLElement>('[role="dialog"]');
    if (!dialog) return;
    const previous = document.activeElement instanceof HTMLElement ? document.activeElement : null;
    const focusable = () => Array.from(dialog.querySelectorAll<HTMLElement>('button, select, input, [tabindex]:not([tabindex="-1"])')).filter((element) => !element.hasAttribute("disabled"));
    focusable()[0]?.focus();
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setConnectionDraft(null);
        setPourSource(null);
        return;
      }
      if (event.key !== "Tab") return;
      const elements = focusable();
      if (!elements.length) return;
      const first = elements[0];
      const last = elements[elements.length - 1];
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    };
    document.addEventListener("keydown", onKeyDown);
    return () => {
      document.removeEventListener("keydown", onKeyDown);
      previous?.focus();
    };
  }, [connectionDraft, pourSource]);

  useEffect(() => {
    if (template !== "acid-base" || templateLoaded.current || !engine) return;
    templateLoaded.current = true;
    const timer = window.setTimeout(() => {
      const flaskId = applyAcidBaseTemplate(engine.workspace.scene, registry);
      engine.notifyUpdate();
      setSelectedIds(new Set([flaskId]));
      addToast("Template loaded: connected flask, burner and thermometer", "success");
    }, 0);
    return () => window.clearTimeout(timer);
  }, [template, engine, addToast]);

  const centers = useMemo(
    () =>
      new Map(
        items.map((item) => [
          item.id,
          {
            x: item.x + (item.w * (item.scaleX ?? item.scale)) / 2,
            y: item.y + (item.h * (item.scaleY ?? item.scale)) / 2,
          },
        ])
      ),
    [items]
  );

  const addItem = useCallback(
    (libraryItem: LibraryItem) => {
      const bounds = canvasRef.current?.getBoundingClientRect();
      const rendererKey = libraryItem.rendererKey ?? libraryItem.id;
      const id = `${libraryItem.catalogId ?? libraryItem.id}-${crypto.randomUUID()}`;
      const definition = registry.get(rendererKey);
      const width = definition?.defaultSize.width ?? libraryItem.w;
      const height = definition?.defaultSize.height ?? libraryItem.h;
      const canvasWidth = bounds?.width || 800;
      const canvasHeight = bounds?.height || 600;
      const occupied = items.map((item) => ({ x: item.x, y: item.y, width: item.w * (item.scaleX ?? item.scale), height: item.h * (item.scaleY ?? item.scale) }));
      const visibleWorldWidth = canvasWidth / zoom;
      const visibleWorldHeight = canvasHeight / zoom;
      const minVisibleX = Math.max(24, -pan.x / zoom);
      const minVisibleY = Math.max(90, -pan.y / zoom);
      const maxVisibleX = Math.max(minVisibleX, -pan.x / zoom + visibleWorldWidth - width - 24);
      const maxVisibleY = Math.max(minVisibleY, -pan.y / zoom + visibleWorldHeight - height - 24);
      const startX = Math.max(minVisibleX, Math.min(maxVisibleX, (-pan.x / zoom) + visibleWorldWidth / 2 - width / 2));
      const startY = Math.max(minVisibleY, Math.min(maxVisibleY, (-pan.y / zoom) + visibleWorldHeight / 2 - height / 2));
      let x = startX;
      let y = startY;
      const step = Math.max(28, Math.round(Math.max(width, height) * 0.72));
      for (let radius = 0; radius < 18; radius += 1) {
        const candidates =
          radius === 0
            ? [[0, 0]]
            : Array.from({ length: radius * 8 }, (_, index) => {
                const angle = (index / (radius * 8)) * Math.PI * 2;
                return [Math.round(Math.cos(angle) * radius), Math.round(Math.sin(angle) * radius)];
              });
        const free = candidates.find(([offsetX, offsetY]) => {
          const candidateX = Math.max(minVisibleX, Math.min(maxVisibleX, startX + offsetX * step));
          const candidateY = Math.max(minVisibleY, Math.min(maxVisibleY, startY + offsetY * step));
          return canPlace({ x: candidateX, y: candidateY, width, height }, occupied);
        });
        if (free) {
          x = Math.max(minVisibleX, Math.min(maxVisibleX, startX + free[0] * step));
          y = Math.max(minVisibleY, Math.min(maxVisibleY, startY + free[1] * step));
          break;
        }
      }

      if (engine) {
        const obj = registry.create(rendererKey, { id });
        obj.position.x = x;
        obj.position.y = y;
        obj.metadata.displayName = libraryItem.name;
        obj.metadata.catalogEquipmentId = libraryItem.catalogId ?? libraryItem.id;
        obj.metadata.rendererKey = rendererKey;
        history.execute(new AddItemCommand(engine.workspace.scene, obj));

        setSelectedIds(new Set([obj.id]));
        queueWorkspaceEvent("ITEM_ADDED", {
          id: obj.id,
          equipmentType: obj.type,
          name: obj.metadata.name || obj.type,
          x: obj.position.x,
          y: obj.position.y,
          w: width,
          h: height,
          scale: obj.scale.x,
          rotation: obj.rotation,
          capacityMl: obj.metadata.capacity || 100,
          contents: obj.contents,
        });
      }
    },
    [engine, items, pan.x, pan.y, queueWorkspaceEvent, history, setSelectedIds, zoom]
  );

  const updateItem = useCallback(
    (id: string, patch: Partial<Item>) => {
      const normalizedPatch = snapToGrid
        ? { ...patch, ...(typeof patch.x === "number" ? { x: Math.round(patch.x / 16) * 16 } : {}), ...(typeof patch.y === "number" ? { y: Math.round(patch.y / 16) * 16 } : {}) }
        : patch;
      if (engine) {
        const object = engine.workspace.scene.objects.get(id);
        if (object) history.execute(new SceneSnapshotCommand(engine.workspace.scene, `Update ${object.type}`, () => applyItemPatch(object, normalizedPatch)));
      }
      if (typeof patch.scale === "number" || typeof patch.scaleX === "number" || typeof patch.scaleY === "number")
        queueWorkspaceEvent("ITEM_RESIZED", { itemId: id, scale: patch.scale, scaleX: patch.scaleX, scaleY: patch.scaleY });
      if (typeof patch.rotation === "number")
        queueWorkspaceEvent("ITEM_ROTATED", {
          itemId: id,
          rotation: patch.rotation,
        });
    },
    [engine, history, queueWorkspaceEvent, snapToGrid]
  );

  const emptyItem = useCallback(
    (id: string) => {
      updateItem(id, { contents: [], volumeMl: 0, material: undefined, massG: 0, liquidLevel: 0, moles: 0 });
      queueWorkspaceEvent("DEVICE_ACTION", { itemId: id, action: "Empty" });
      addToast("Vessel emptied", "info");
    },
    [updateItem, queueWorkspaceEvent, addToast]
  );

  const removeMaterial = useCallback(
    (itemId: string, materialId: string, phase: string) => {
      if (!engine) return;
      const object = engine.workspace.scene.objects.get(itemId);
      const content = object?.contents.find((entry) => entry.materialId === materialId && entry.phase === phase);
      if (!object || !content) return;
      history.execute(new MaterialRemoveCommand(engine.workspace.scene, itemId, materialId, phase));
      engine.notifyUpdate();
      queueWorkspaceEvent("MATERIAL_REMOVED", { itemId, materialId: canonicalizeLegacyMaterialId(materialId), phase });
      addToast(`${content.formula} removed`, "info");
    },
    [addToast, engine, history, queueWorkspaceEvent]
  );

  
  
  const addMaterial = (material: Material) => {
    if (!selected || !isVessel(selected)) {
      addToast("Select a vessel first", "error");
      return;
    }
    if (!engine) return;
    if (material.state === "gas") {
      addToast("Oxygen is a gas: an open flask cannot retain it. Use a sealed Gas connector.", "error");
      return;
    }
    if ((selected.type === "pipette" || selected.type === "burette") && material.state !== "liquid" && material.state !== "aqueous") {
      addToast("Пипетка и бюретка предназначены только для жидкостей.", "error");
      return;
    }
    const amount = material.state === "solid" ? 10 : 25;
    const addedMoles = material.molarMass && material.state === "solid" ? amount / material.molarMass : 0;
    const existing = selected.contents.find((content) => content.materialId === material.id && content.phase === material.state);
    const liquidAmount = material.state === "liquid" || material.state === "aqueous" ? 25 : 0;
    const capacityRemaining = Math.max(0, capacityFor(selected) - selected.volumeMl);
    const acceptedLiquidAmount = Math.min(liquidAmount, capacityRemaining);
    const overflowAmount = Math.max(0, liquidAmount - acceptedLiquidAmount);
    const addedAmount = liquidAmount > 0 ? acceptedLiquidAmount : amount;
    if (overflowAmount > 0) {
      const object = engine.workspace.scene.objects.get(selected.id);
      if (object) {
        object.properties.overflowing = true;
        object.properties.lastOverflowAt = Date.now();
        object.history.push(`Overflow: ${overflowAmount.toFixed(1)} mL released`);
        engine.workspace.scene.environment.spills.push({
          id: crypto.randomUUID(), materialId: material.id, amount: overflowAmount, time: Date.now(),
          x: object.position.x + object.boundingBox.width / 2, y: object.position.y + object.boundingBox.height,
          color: material.color, sourceId: object.id,
        });
      }
      engine.notifyUpdate();
      queueWorkspaceEvent("OVERFLOW", { itemId: selected.id, materialId: canonicalizeLegacyMaterialId(material.id), amountMl: overflowAmount });
      if (acceptedLiquidAmount <= 0) {
        addToast(`Сосуд заполнен: ${overflowAmount.toFixed(1)} мл разлито.`, "info");
        triggerPourAnimation(selected.id, selected.id, 0, overflowAmount);
        return;
      }
    }
    const contents: ContentComponent[] = existing
      ? selected.contents.map((content) => (content === existing ? { ...content, amount: content.amount + addedAmount, molarAmount: (content.molarAmount || 0) + addedMoles } : content))
      : [
          ...selected.contents,
          {
            materialId: material.id,
            name: material.name,
            formula: material.formula,
            amount: addedAmount,
            molarAmount: addedMoles,
            unit: material.state === "solid" ? "g" : "mL",
            phase: material.state,
            color: material.color,
          },
        ];
    updateItem(selected.id, {
      material,
      contents,
      massG: selected.massG + (material.state === "solid" ? amount : 0),
      moles: selected.moles + addedMoles,
      volumeMl: material.state === "liquid" || material.state === "aqueous" ? selected.volumeMl + acceptedLiquidAmount : selected.volumeMl,
      liquidLevel: material.state === "liquid" || material.state === "aqueous" ? Math.min(1, (selected.volumeMl + acceptedLiquidAmount) / capacityFor(selected)) : selected.liquidLevel,
    });
    queueWorkspaceEvent("MATERIAL_ADDED", {
      itemId: selected.id,
      materialId: canonicalizeLegacyMaterialId(material.id),
      amountMl: acceptedLiquidAmount || 1,
      phase: material.state,
    });
    triggerPourAnimation(selected.id, selected.id, acceptedLiquidAmount || 25, overflowAmount);
    addToast(overflowAmount > 0 ? `${material.name}: ${acceptedLiquidAmount.toFixed(1)} мл принято, ${overflowAmount.toFixed(1)} мл разлито.` : `${material.name} added · ${material.state === "liquid" ? "25 mL" : "solid sample"}`, overflowAmount > 0 ? "info" : "success");
  };

  const mixItem = useCallback((itemId: string) => {
    if (!engine || mixTimersRef.current.has(itemId)) return;
    const object = engine.workspace.scene.objects.get(itemId);
    if (!object || !object.capabilities.container) return;
    if (object.properties.broken || object.properties.integrity === 'shattered') {
      addToast(ru(locale) ? "Повреждённый сосуд нельзя смешивать." : uz(locale) ? "Shikastlangan idishda aralashtirib bo‘lmaydi." : "A damaged vessel cannot be mixed.", "error");
      return;
    }
    if (object.contents.length < 2) {
      addToast(ru(locale) ? "Для смешивания нужно минимум два компонента." : uz(locale) ? "Aralashtirish uchun kamida ikki komponent kerak." : "Mixing needs at least two components.", "info");
      return;
    }

    object.state = 'mixing';
    object.properties.mixing = true;
    object.history.push('Mixing started');
    queueWorkspaceEvent('MIXING_STARTED', { itemId, message: 'Mixing started' });
    engine.notifyUpdate();

    const timer = window.setTimeout(() => {
      mixTimersRef.current.delete(itemId);
      const current = engine.workspace.scene.objects.get(itemId);
      if (!current) return;
      let result: ReturnType<typeof mixObjectContents> | undefined;
      history.execute(new SceneSnapshotCommand(engine.workspace.scene, `Mix ${current.type}`, () => {
        result = mixObjectContents(current);
        current.state = 'idle';
        current.properties.mixing = false;
        current.properties.lastMixedAt = new Date().toISOString();
        current.history.push(...(result?.events.slice(1) ?? []));
      }));
      for (const message of result?.events.slice(1) ?? []) {
        queueWorkspaceEvent(message.startsWith('Reaction detected') ? 'REACTION_DETECTED' : 'MIXING_EVENT', { itemId, message });
      }
      addToast(
        result?.reacted
          ? (ru(locale) ? 'Реакция обнаружена — состав обновлён.' : uz(locale) ? 'Reaksiya aniqlandi — tarkib yangilandi.' : 'Reaction detected — composition updated.')
          : (ru(locale) ? 'Однородная смесь готова.' : uz(locale) ? 'Bir jinsli aralashma tayyor.' : 'Homogeneous mixture ready.'),
        result?.reacted ? 'success' : 'info',
      );
      engine.notifyUpdate();
    }, 900);
    mixTimersRef.current.set(itemId, timer);
  }, [addToast, engine, history, locale, queueWorkspaceEvent]);

  const applyOperation = (item: Item, operation: EquipmentOperation) => {
    const isHeater = item.type === "burner" || item.type === "hotplate" || item.type === "magnetic_stirrer";
    if (item.broken || item.integrity === 'shattered') {
      addToast(ru(locale) ? "Действия с повреждённым объектом недоступны." : uz(locale) ? "Shikastlangan obyektda harakat mavjud emas." : "Actions are unavailable for a damaged object.", "error");
      return;
    }
    if (operation === "heating" && isHeater && !item.attachedTo) {
      addToast(
        ru(locale) ? "Сначала поставьте нагреватель под сосуд." : uz(locale) ? "Avval isitgichni idish ostiga qo‘ying." : "First place the heater under a vessel.",
        "error"
      );
      return;
    }
    const defaultTarget = item.targetTemperature || (operation === "heating" ? 80 : operation === "cooling" ? 5 : item.temperature);
    const requestedTarget = operation === "heating" ? Number(String(item.targetTemperature ?? defaultTarget)) : defaultTarget;
    const target = Number.isFinite(requestedTarget) ? Math.max(0, requestedTarget) : defaultTarget;
    if (engine) {
      const operationCommand = new SceneSnapshotCommand(engine.workspace.scene, operation, () => {
        applyOperationToScene(engine.workspace.scene.objects.values(), item.id, operation, target);
      });
      history.execute(operationCommand);
    }
    if (sessionId) {
      void engine?.simulation
        ?.executeOperation?.(sessionId, {
          expectedStateVersion: stateVersionRef.current,
          idempotencyKey: crypto.randomUUID(),
          command: {
            commandId: { value: crypto.randomUUID() },
            stepId: `sandbox-${item.id}`,
            targetVesselId: item.id,
            operation: {
              operationType: operation === "heating" || operation === "cooling" ? "THERMAL_OPERATION" : "BOOKKEEPING_MIX",
              modelSelection: {
                calculationMethod: operation === "heating" ? "SENSIBLE_HEATING" : operation === "cooling" ? "SENSIBLE_COOLING" : "WORKSPACE_MIX",
                reactionOrProfileIdentifier: "workspace-sandbox",
                model: { identifier: "workspace-sandbox", version: "1.0" },
                datasets: [],
                assumptions: { source: "sandbox" },
              },
            },
            inputs: {
              targetTemperatureK: String(target + 273.15),
              targetTemperatureC: String(target),
            },
            materialDeltas: [],
          },
        })
        .then((result) => {
          stateVersionRef.current = result.newVersion;
          const reconciled = reconcileSimulationResult(engine, result);
          if (reconciled.reactionIds.length || reconciled.formedMaterialIds.length) {
            setAuthoritativeFacts((current) => ({
              reactionIds: [...new Set([...current.reactionIds, ...reconciled.reactionIds])],
              formedMaterialIds: [...new Set([...current.formedMaterialIds, ...reconciled.formedMaterialIds])],
            }));
          }
        })
        .catch((error: unknown) => addToast(error instanceof Error ? error.message : "Scientific operation failed", "error"));
    }
    queueWorkspaceEvent(operation === "heating" ? "HEAT_START" : operation === "cooling" ? "COOL" : operation === "stirring" ? "STIR_START" : "HEAT_STOP", {
      equipmentId: item.id,
      vesselId: item.id,
      targetTemperatureC: target,
    });
  };

  const connectPorts = (fromId: string, fromPortId: string, toId: string, toPortId: string, allowAdapter = false) => {
    const from = engine?.workspace.scene.objects.get(fromId);
    const to = engine?.workspace.scene.objects.get(toId);
    if (!from || !to) return false;
    try {
      void allowAdapter;
      const existing = Array.from(engine.workspace.scene.connections.values());
      const connection = connectionEngine.create(from, fromPortId, to, toPortId, existing);
      history.execute(new ConnectCommand(engine.workspace.scene, connection));
      // Scenario completion and sensor readings consume the projected scene state.
      // Notify immediately after a successful connection so Level Mode advances
      // without waiting for an unrelated canvas update.
      engine.notifyUpdate();
      if (connection.medium === "thermal") {
        const vessel = from.capabilities.container ? from : to.capabilities.container ? to : null;
        const heater = from.capabilities.thermalOutput ? from : to.capabilities.thermalOutput ? to : null;
        if (vessel && heater) {
          const uiVessel = items.find((i) => i.id === vessel.id);
          const uiHeater = items.find((i) => i.id === heater.id);
          if (uiVessel && uiHeater) {
            const x = uiVessel.x + uiVessel.w / 2 - uiHeater.w / 2;
            const y = uiVessel.y + uiVessel.h - 35;
            updateItem(heater.id, { x, y, attachedTo: vessel.id });
          }
        }
      }
      queueWorkspaceEvent("CONNECT", {
        id: connection.id,
        fromItemId: from.id,
        toItemId: to.id,
        fromPortId: connection.from.portId,
        toPortId: connection.to.portId,
        medium: connection.medium,
        connector: connection.connector,
      });
      addToast(`${connection.connector === "adapter" ? "Adapter connected · " : "Connected "}${connection.medium ?? connection.type}`, "success");
      return true;
    } catch (error) {
      addToast(error instanceof Error ? error.message : "Ports are not compatible", "error");
      return false;
    }
  };

  const connectionPortPriority = (object: LaboratoryObject, port: LaboratoryObject["ports"][number]) => {
    const preferred =
      object.type === "thermometer"
        ? ["sensor"]
        : object.type === "hotplate" || object.type === "burner" || object.type === "magnetic_stirrer"
        ? ["heat"]
        : object.type === "beaker" || object.type === "erlenmeyer" || object.type === "testtube"
        ? ["thermal", "sensor", "liquid", "gas"]
        : ["liquid", "gas", "thermal", "sensor"];
    const index = preferred.indexOf(port.id);
    return index === -1 ? preferred.length : index;
  };

  const connectObjects = (fromId: string, toId: string) => {
    const from = engine?.workspace.scene.objects.get(fromId);
    const to = engine?.workspace.scene.objects.get(toId);
    if (!from || !to || !engine) {
      cancelConnection();
      return false;
    }
    const existing = Array.from(engine.workspace.scene.connections.values());
    const exactPair = [...from.ports]
      .sort((a, b) => connectionPortPriority(from, a) - connectionPortPriority(from, b))
      .flatMap((source) =>
        [...to.ports]
          .sort((a, b) => connectionPortPriority(to, a) - connectionPortPriority(to, b))
          .map((target) => ({ source, target }))
      )
      .find(({ source, target }) => connectionEngine.canConnect(from, source.id, to, target.id, existing));
    if (exactPair) return connectPorts(fromId, exactPair.source.id, toId, exactPair.target.id);
    addToast("Нет совместимых портов между этими приборами", "error");
    cancelConnection();
    return false;
  };

  const startPortConnection = (itemId: string, portId: string) => {
    if (connectSourcePort && connectSourcePort.itemId !== itemId) {
      finishPortConnection(itemId, portId);
      return;
    }
    setSelectedIds(new Set([itemId]));
    setTool("connect");
    setConnectSource(itemId);
    setConnectSourcePort({ itemId, portId });
    setConnectionSnap(null);
    addToast("Drag to a compatible port", "info");
  };

  const finishPortConnection = (itemId: string, portId: string) => {
    if (!connectSourcePort || connectSourcePort.itemId === itemId) return;
    const source = engine?.workspace.scene.objects.get(connectSourcePort.itemId);
    const target = engine?.workspace.scene.objects.get(itemId);
    if (!source || !target || !engine) {
      cancelConnection();
      return;
    }
    const existing = Array.from(engine.workspace.scene.connections.values());
    const compatible = connectionEngine.canConnect(source, connectSourcePort.portId, target, portId, existing);
    if (compatible) {
      connectPorts(connectSourcePort.itemId, connectSourcePort.portId, itemId, portId);
    } else {
      addToast("Порты несовместимы", "error");
    }
    setConnectSource(null);
    setConnectSourcePort(null);
    setConnectionSnap(null);
    setConnectionPointer(null);
    setTool("select");
  };

  const connectFromPort = (fromId: string, fromPortId: string, toId: string) => {
    const source = engine?.workspace.scene.objects.get(fromId);
    const target = engine?.workspace.scene.objects.get(toId);
    if (!source || !target || !engine) return false;
    const existing = Array.from(engine.workspace.scene.connections.values());
    const targetPort = [...target.ports]
      .sort((a, b) => connectionPortPriority(target, a) - connectionPortPriority(target, b))
      .find((port) => connectionEngine.canConnect(source, fromPortId, target, port.id, existing));
    if (targetPort) return connectPorts(fromId, fromPortId, toId, targetPort.id);
    addToast("Нет совместимых портов", "error");
    cancelConnection();
    return false;
  };

  const cancelConnection = (preserveTool = false) => {
    if (!preserveTool) setTool("select");
    setConnectSource(null);
    setConnectSourcePort(null);
    setConnectionPointer(null);
    setConnectionSnap(null);
  };

  const hoverPort = (itemId: string, portId: string, point: { x: number; y: number }) => {
    if (!connectSourcePort || connectSourcePort.itemId === itemId) return;
    setConnectionSnap({ itemId, portId, x: point.x, y: point.y });
    setConnectionPointer(point);
  };

  const disconnectConnection = (connectionId: string) => {
    const connection = engine?.workspace.scene.connections.get(connectionId);
    if (!connection || !engine) return;
    history.execute(new DisconnectCommand(engine.workspace.scene, connectionId));
    engine.notifyUpdate();
    queueWorkspaceEvent("DISCONNECT", { connectionId });
    addToast("Connection removed", "info");
  };

  const editConnection = (connectionId: string) => {
    const connection = engine?.workspace.scene.connections.get(connectionId);
    if (!connection || !engine) return;
    const connectors: NonNullable<typeof connection.connector>[] = ["glass-tube", "rubber-hose", "wire", "direct", "adapter"];
    const currentIndex = Math.max(0, connectors.indexOf(connection.connector ?? "glass-tube"));
    const connector = connectors[(currentIndex + 1) % connectors.length];
    history.execute(
      new SceneSnapshotCommand(engine.workspace.scene, "Edit connection", () => {
        engine.workspace.scene.connections.set(connectionId, { ...connection, connector });
      })
    );
    engine.notifyUpdate();
    queueWorkspaceEvent("CONNECT", {
      id: connection.id,
      fromItemId: connection.from.objectId,
      toItemId: connection.to.objectId,
      fromPortId: connection.from.portId,
      toPortId: connection.to.portId,
      medium: connection.medium,
      connector,
    });
    addToast("Connection updated", "info");
  };

  const editRoute = (connectionId: string, routePoints: Array<{ x: number; y: number }>) => {
    if (!engine) return;
    const connection = engine.workspace.scene.connections.get(connectionId);
    if (!connection) return;
    history.execute(new RouteEditCommand(engine.workspace.scene, connectionId, connection.routePoints ?? [], routePoints));
    engine.notifyUpdate();
  };

  const deviceAction = (item: Item, action: string) => {
    if (action === "Ignite") {
      if (!item.attachedTo) {
        addToast(ru(locale) ? "Сначала поставьте горелку под сосуд." : uz(locale) ? "Avval gorelkani idish ostiga qo‘ying." : "First place the burner under a vessel.", "error");
        return;
      }
      updateItem(item.id, { operation: "heating" });
    }
    if (action === "Extinguish") updateItem(item.id, { operation: "idle" });
    if (action === "Tare" || action === "Zero" || action === "Calibrate") updateItem(item.id, { massG: 0 });
    queueWorkspaceEvent("DEVICE_ACTION", { itemId: item.id, action });
    addToast(`${item.name}: ${action}`, "info");
  };

  const { endDrag, onPointerDown, onPointerMove, onCanvasPointerDown, isPanning } = useSandboxGestures({
    canvasRef,
    items,
    engine,
    history,
    connectSource,
    connectSourcePort,
    queueWorkspaceEvent,
    updateItem,
    selectedIds,
      setSelectedIds,
      setConnectionPointer,
    setConnectionSnap,
    marquee,
    setMarquee,
    setConnectSource,
    setConnectSourcePort,
    showToast: addToast,
    connectObjects,
    connectFromPort,
    tool,
    setTool,
    zoom,
    pan,
    setPan,
    setCollisionItemId,
  });

  
    const hide = useCallback(() => {
    if (!engine) return;
    const patch = { hidden: true };
    selectedIds.forEach((id) => {
      updateItem(id, patch);
    });
    engine.notifyUpdate();
  }, [engine, selectedIds]);

  const remove = useCallback(() => {
    if (!engine) return;
    
    if (selectedConnectionId) {
      disconnectConnection(selectedConnectionId);
      setSelectedConnectionId(null);
      return;
    }
    
    if (selectedIds.size > 0) {
      selectedIds.forEach(id => {
        const obj = engine.workspace.scene.objects.get(id);
        if (obj) history.execute(new RemoveItemCommand(engine.workspace.scene, id));
      });
      if (connectSource && selectedIds.has(connectSource)) {
        cancelConnection();
      }
      setSelectedIds(new Set());
      engine.notifyUpdate();
      queueWorkspaceEvent("REMOVE_ITEM", { itemId: "multiple" });
    }
  }, [selectedConnectionId, selectedIds, engine, connectSource, history, queueWorkspaceEvent, disconnectConnection, cancelConnection]);

  const undo = useCallback(() => {
    if (!engine) return;
    history.undo();
    engine.notifyUpdate();
  }, [engine, history]);

  const redo = useCallback(() => {
    if (!engine) return;
    history.redo();
    engine.notifyUpdate();
  }, [engine, history]);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      const activeEl = document.activeElement as HTMLElement | null;
      if (activeEl && (activeEl.tagName === 'INPUT' || activeEl.tagName === 'TEXTAREA' || activeEl.isContentEditable)) {
        return;
      }

      if (e.key === 'Delete' || e.key === 'Backspace') {
        if (selectedIds.size > 0 || selectedConnectionId) {
            e.preventDefault();
            remove();
          }
      } else if (e.key === 'Escape') {
        if (tool === 'connect' || connectSource) {
          cancelConnection();
        } else if (selectedId || selectedConnectionId) {
          setSelectedIds(new Set());
          setSelectedConnectionId(null);
        }
      } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'z') {
        e.preventDefault();
        if (e.shiftKey) {
          redo();
        } else {
          undo();
        }
      } else if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'y') {
        e.preventDefault();
        redo();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [selectedIds, selectedConnectionId, remove, tool, connectSource, undo, redo]);

  const duplicate = useCallback(() => {
    if (!selected) return;
    const newId = `${selected.type}-${crypto.randomUUID()}`;
    const newX = selected.x + 24;
    const newY = selected.y + 24;

    if (engine) {
      const obj = registry.create(selected.type, { id: newId });
      obj.position.x = newX;
      obj.position.y = newY;
      history.execute(new AddItemCommand(engine.workspace.scene, obj));

      setSelectedIds(new Set([obj.id]));

      queueWorkspaceEvent("ITEM_ADDED", {
        id: obj.id,
        equipmentType: obj.type,
        name: obj.metadata.name || obj.type,
        x: obj.position.x,
        y: obj.position.y,
        w: selected.w,
        h: selected.h,
        scale: obj.scale.x,
        rotation: obj.rotation,
        capacityMl: obj.metadata.capacity || 100,
      });
    }
  }, [selected, engine, history, queueWorkspaceEvent, setSelectedIds]);

  useEffect(() => {
    const key = (event: KeyboardEvent) => {
      const target = event.target as HTMLElement | null;
      if (target?.isContentEditable || ["INPUT", "SELECT", "TEXTAREA"].includes(target?.tagName || "")) return;
      if (!event.metaKey && !event.ctrlKey && !event.altKey && event.key.toLowerCase() === "b") {
        event.preventDefault();
        setCodexTarget(undefined);
        setCodexOpen(true);
        return;
      }
      if (event.key === "Delete" || event.key === "Backspace") remove();
      if (event.key === "Escape") {
        if (tool === "connect") {
          setTool("select");
          setConnectSource(null);
          setConnectSourcePort(null);
          setConnectionPointer(null);
          setConnectionSnap(null);
        } else {
          setSelectedIds(new Set());
          setSelectedConnectionId(null);
        }
        setCollisionItemId(null);
      }
      if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "d") {
        event.preventDefault();
        duplicate();
      }
      if ((event.ctrlKey || event.metaKey) && event.key === "z" && !event.shiftKey) {
        event.preventDefault();
        historyAction("undo");
      }
      if ((event.ctrlKey || event.metaKey) && (event.key === "y" || (event.shiftKey && event.key.toLowerCase() === "z"))) {
        event.preventDefault();
        historyAction("redo");
      }
    };
    window.addEventListener("keydown", key);
    return () => window.removeEventListener("keydown", key);
  }, [remove, duplicate, historyAction, tool]);

  return (
    <ScenarioRuntimeProvider scenario={scenarioRuntime.scenario}>
    <div data-sandbox-mode={sandboxMode} aria-busy={scenarioRuntime.loading} className={`sandbox-ui relative flex ${embedded ? 'h-full min-h-[680px]' : 'h-[100dvh]'} w-full min-h-0 overflow-hidden bg-background text-foreground ${shareMode === "viewer" ? "sandbox-viewer-mode" : ""}`}>
    {scenarioRuntime.loading && <div role="status" className="absolute left-1/2 top-16 z-[95] -translate-x-1/2 rounded-lg border border-border bg-card/95 px-4 py-2 text-xs shadow-xl">Loading Scenario…</div>}
    {scenarioRuntime.error && <div role="alert" className="absolute left-1/2 top-16 z-[95] max-w-md -translate-x-1/2 rounded-lg border border-amber-400/30 bg-amber-950/95 px-4 py-2 text-xs text-amber-100 shadow-xl"><strong>Could not load the current Scenario.</strong><details className="mt-1"><summary>Show details</summary>{scenarioRuntime.error}</details></div>}
    {activeLevelIntro && levelIntroOpen && <LevelIntro level={activeLevelIntro} catalog={scenarioRuntime.scenario?.catalog} locale={(locale === "ru" || locale === "uz" ? locale : "en") as SupportedLocale} onStart={() => {
      setLevelIntroOpen(false);
      setScenarioIntro(false);
      setLibraryTab("equipment");
      setLeftPanelVisible(true);
      setBottomDockOpen(true);
      setHelpActive(true);
    }} />}
    <HelpArrows
      active={helpActive}
      targets={resolveGuideTargets(activeScenario?.id, activeScenario?.step)}
      locale={locale}
    />
    <GuideCursor active={guideDemoOpen} locale={locale} onFinish={() => setGuideDemoOpen(false)} />
    {showNavbar && <header className="sandbox-topbar absolute left-0 right-0 top-0 z-[80] flex h-14 items-center gap-5 border-b border-border bg-card/95 px-4 shadow-[0_4px_18px_rgba(0,0,0,.18)] backdrop-blur-xl">
        <div className="flex shrink-0 items-center gap-3">
          <Link href={`/${locale}/dashboard`} className="grid h-8 w-8 place-items-center rounded-lg bg-white/5 transition-colors hover:bg-foreground/10">
            <ArrowLeft size={16} className="text-[var(--muted-foreground)]" />
          </Link>
          <div className="sandbox-workspace-title flex flex-col">
            <h1 className="text-xs font-bold uppercase tracking-widest text-foreground">{locale === "ru" ? "Лаборатория" : locale === "uz" ? "Laboratoriya" : "Laboratory"}</h1>
            <span className={`text-[10px] font-bold ${activeScenario === null ? "text-lime-400" : "text-[var(--primary)]"}`}>
              {activeScenario === null
                ? (locale === "ru" ? "Свободный эксперимент" : locale === "uz" ? "Erkin tajriba" : "Free experiment")
                : (locale === "ru" ? "Прохождение сценария" : locale === "uz" ? "Stsenariy o'tilmoqda" : "Scenario in progress")}
            </span>
          </div>
        </div>
        {shareMode !== "viewer" && <SandboxMenuBar
          canUndo={history.canUndo}
          canRedo={history.canRedo}
          hasSelection={selectedId !== null}
          hasMultipleItems={selectedIds.size > 1}
          showGrid={showGrid}
          showNavbar={showNavbar}
          isRunning={runState === "Running"}
          activeScenario={activeScenario}
          onAction={(actionId) => {
            if (actionId === "new-experiment") {
              setClearWorkspaceModal(true);
            } else if (actionId === "save-experiment") {
              const snapshot = serializeSnapshot("Сохранённый эксперимент", engine!.workspace.scene.serialize(), activeScenario, zoom, pan);
              window.localStorage.setItem("jasscience-sandbox-draft", JSON.stringify(snapshot));
              addToast("Эксперимент сохранён на этом устройстве", "success");
            } else if (actionId === "save-copy") {
              const snapshot = serializeSnapshot("Копия эксперимента", engine!.workspace.scene.serialize(), activeScenario, zoom, pan);
              window.localStorage.setItem(`jasscience-sandbox-copy-${Date.now()}`, JSON.stringify(snapshot));
              addToast("Копия эксперимента сохранена", "success");
            } else if (actionId === "import-snapshot") {
              const input = document.createElement("input");
              input.type = "file";
              input.accept = "application/json";
              input.onchange = async (e) => {
                const file = (e.target as HTMLInputElement).files?.[0];
                if (!file || !engine) return;
                try {
                  const text = await file.text();
                  const data = JSON.parse(text) as SandboxSnapshot;
                  if (data.version !== 2) throw new Error("Unsupported version");
                  engine.workspace.scene.objects.clear();
                  engine.workspace.scene.connections.clear();
                  for (const obj of data.objects) {
                    engine.workspace.scene.objects.set(obj.id, LaboratoryObject.deserialize(obj));
                  }
                  for (const conn of data.connections) {
                    engine.workspace.scene.connections.set(conn.id, conn);
                  }
                  if (data.scenario) setActiveScenario(data.scenario);
                  if (data.viewport) {
                    setZoom(data.viewport.zoom || 1);
                    setPan({ x: data.viewport.panX || 0, y: data.viewport.panY || 0 });
                  }
                  engine.notifyUpdate();
                  addToast("Эксперимент загружен", "success");
                } catch {
                  addToast("Ошибка при загрузке файла", "error");
                }
              };
              input.click();
            } else if (actionId === "export-snapshot") {
              const snapshot = serializeSnapshot(
                activeScenario ? scenarioRuntime.scenario?.title ?? "Experiment" : "Free experiment",
                engine!.workspace.scene.serialize(),
                activeScenario,
                zoom,
                pan
              );
              const json = JSON.stringify(snapshot, null, 2);
              const blob = new Blob([json], { type: "application/json" });
              const url = URL.createObjectURL(blob);
              const a = document.createElement("a");
              a.href = url;
              a.download = `${snapshot.title || "experiment"}.json`;
              document.body.appendChild(a);
              a.click();
              document.body.removeChild(a);
              URL.revokeObjectURL(url);
            } else if (actionId === "share") {
              setShareSnapshot(
                serializeSnapshot(
                  activeScenario ? scenarioRuntime.scenario?.title ?? "Experiment" : "Free experiment",
                  engine!.workspace.scene.serialize(),
                  activeScenario,
                  zoom,
                  pan
                )
              );
            } else if (actionId === "undo") {
              historyAction("undo");
            } else if (actionId === "redo") {
              historyAction("redo");
            } else if (actionId === "duplicate") {
              duplicate();
            } else if (actionId === "copy") {
              const selectedObjects = items.filter((item) => selectedIds.has(item.id));
              void navigator.clipboard?.writeText(JSON.stringify(selectedObjects, null, 2));
              addToast("Выбранные объекты скопированы", "success");
            } else if (actionId === "delete") {
              remove();
            } else if (actionId === "deselect") {
              setSelectedIds(new Set());
            } else if (actionId === "select-all") {
              setSelectedIds(new Set(items.map((item) => item.id)));
            } else if (actionId === "toggle-grid") {
              setShowGrid(!showGrid);
            } else if (actionId === "toggle-snap") {
              setSnapToGrid((enabled) => !enabled);
              addToast(snapToGrid ? "Привязка к сетке выключена" : "Привязка к сетке включена", "info");
            } else if (actionId === "zoom-in") {
              setZoom((z) => Math.min(z + 0.1, 3));
            } else if (actionId === "zoom-out") {
              setZoom((z) => Math.max(z - 0.1, 0.1));
            } else if (actionId === "zoom-100") {
              setZoom(1);
            } else if (actionId === "zoom-fit") {
              window.dispatchEvent(new CustomEvent("sandbox-zoom-to-fit"));
            } else if (actionId === "center-scene") {
              window.dispatchEvent(new CustomEvent("sandbox-center-scene"));
            } else if (actionId === "toggle-library") {
              setLeftPanelVisible(!leftPanelVisible);
            } else if (actionId === "toggle-inspector") {
              setRightPanelVisible(!rightPanelVisible);
            } else if (actionId === "toggle-navbar") {
              setShowNavbar((visible) => !visible);
            } else if (actionId === "fullscreen") {
              if (document.fullscreenElement) void document.exitFullscreen();
              else void document.documentElement.requestFullscreen();
            } else if (actionId === "free-mode") {
              setActiveScenario(null);
              setExperimentResult(null);
            } else if (actionId === "clear-workspace") {
              setClearWorkspaceModal(true);
            } else if (actionId === "scenarios") {
              setLeftPanelVisible(true);
              setLibraryTab("scenarios");
              if (window.innerWidth < 1280) setMobilePanel("library");
            } else if (actionId === "jasscience-os") {
              setCodexTarget(undefined);
              setCodexOpen(true);
            } else if (actionId.startsWith("level-")) {
              const lvl = actionId.startsWith("level-") ? parseInt(actionId.replace("level-", ""), 10) : 1;
              setJasScienceLevel(lvl);
            } else if (actionId.startsWith("align-") || actionId.startsWith("distribute-")) {
              const selectedObjects = items.filter((item) => selectedIds.has(item.id));
              if (engine && selectedObjects.length > 1) {
                history.execute(new SceneSnapshotCommand(engine.workspace.scene, actionId, () => {
                  const xs = selectedObjects.map((item) => item.x);
                  const ys = selectedObjects.map((item) => item.y);
                  const left = Math.min(...xs), right = Math.max(...xs), top = Math.min(...ys), bottom = Math.max(...ys);
                  const sorted = [...selectedObjects].sort((a, b) => actionId === "distribute-v" ? a.y - b.y : a.x - b.x);
                  sorted.forEach((item, index) => {
                    const object = engine.workspace.scene.objects.get(item.id);
                    if (!object) return;
                    if (actionId === "align-left") object.position.x = left;
                    if (actionId === "align-center-h") object.position.x = (left + right) / 2;
                    if (actionId === "align-right") object.position.x = right;
                    if (actionId === "align-top") object.position.y = top;
                    if (actionId === "align-middle-v") object.position.y = (top + bottom) / 2;
                    if (actionId === "align-bottom") object.position.y = bottom;
                    if (actionId === "distribute-h") object.position.x = left + ((right - left) * index) / Math.max(1, sorted.length - 1);
                    if (actionId === "distribute-v") object.position.y = top + ((bottom - top) * index) / Math.max(1, sorted.length - 1);
                  });
                }));
              }
            } else if (actionId === "group" || actionId === "ungroup") {
              if (engine) history.execute(new SceneSnapshotCommand(engine.workspace.scene, actionId, () => {
                const groupId = actionId === "group" ? `group-${crypto.randomUUID()}` : undefined;
                for (const item of items.filter((candidate) => selectedIds.has(candidate.id))) {
                  const object = engine.workspace.scene.objects.get(item.id);
                  if (!object) continue;
                  if (groupId) object.metadata.groupId = groupId;
                  else delete object.metadata.groupId;
                }
              }));
            } else if (actionId === "safety-check") {
              const unsafe = items.filter((item) => item.unsafeConfiguration || item.broken || item.integrity === "cracked");
              addToast(unsafe.length ? `Обнаружено небезопасных объектов: ${unsafe.length}` : "Проверка пройдена: опасности не обнаружены", unsafe.length ? "error" : "success");
            } else if (actionId === "schema-check") {
              const invalid = items.filter((item) => !item.id || !item.type || !Number.isFinite(item.x) || !Number.isFinite(item.y));
              addToast(invalid.length ? `Ошибок структуры: ${invalid.length}` : "Структура сцены корректна", invalid.length ? "error" : "success");
            } else if (actionId === "clear-states") {
              if (engine) history.execute(new SceneSnapshotCommand(engine.workspace.scene, "Clear transient states", () => {
                for (const object of engine.workspace.scene.objects.values()) object.state = "idle";
              }));
              setExperimentResult(null);
              setRunState("Ready");
            } else if (actionId === "debug-mode") {
              window.dispatchEvent(new KeyboardEvent("keydown", { key: "d", shiftKey: true }));
            }
          }}
        />}
        <div className="ml-auto flex items-center gap-3">
          <div className="sandbox-language"><LanguageSwitcher variant="ghost" /></div>
          <ThemeToggle />
          {shareMode === "viewer" ? <div className="sandbox-viewer-badge flex items-center gap-2 rounded-full border border-cyan-400/25 bg-cyan-400/10 px-3 py-1.5 text-[11px] font-semibold text-cyan-700 dark:text-cyan-200"><Eye size={14} /> Только просмотр</div> : <button
            type="button"
            onClick={() => {
              setShareSnapshot(
                serializeSnapshot(
                activeScenario ? scenarioRuntime.scenario?.title ?? "Experiment" : "Free experiment",
                  engine!.workspace.scene.serialize(),
                  activeScenario,
                  zoom,
                  pan
                )
              );
            }}
            aria-label={ts("menu.share")}
            title={ts("menu.share")}
            className="flex items-center gap-2 rounded-lg bg-[var(--primary)] px-2.5 sm:px-4 py-1.5 text-xs font-semibold text-white shadow-sm transition-colors hover:bg-[var(--primary)]/90"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="18" cy="5" r="3" />
              <circle cx="6" cy="12" r="3" />
              <circle cx="18" cy="19" r="3" />
              <line x1="8.59" y1="13.51" x2="15.42" y2="17.49" />
              <line x1="15.41" y1="6.51" x2="8.59" y2="10.49" />
            </svg>
            <span className="hidden xl:inline">{ts("menu.share")}</span>
          </button>}
        </div>
      </header>}
      {!showNavbar && (
        <button
          type="button"
          onClick={() => setShowNavbar(true)}
          className="absolute left-3 top-3 z-50 flex h-9 items-center gap-2 rounded-lg border border-border bg-card/95 px-3 text-xs font-semibold text-foreground shadow-lg backdrop-blur-xl"
          aria-label={ts("menu.show_navbar")}
        >
          <span aria-hidden="true">☰</span>{ts("menu.show_navbar")}
        </button>
      )}

      {/* Immersive background glow & grid */}
      <div className="absolute inset-0 pointer-events-none" style={{ background: "radial-gradient(circle at 50% 50%, rgba(124,58,237,0.03), transparent 70%), linear-gradient(to bottom, transparent, rgba(0,0,0,0.4))" }}></div>
      <div className="absolute inset-0 pointer-events-none opacity-20" style={{ backgroundImage: "radial-gradient(circle at 1px 1px, rgba(192,161,255,0.4) 1px, transparent 0)", backgroundSize: "32px 32px" }}></div>

      {/* Resizer Handle (Left) */}
      <div
        aria-label="Изменить ширину библиотеки"
        role="separator"
        onPointerDown={(event) => startPanelResize("left", event)}
        className={`absolute bottom-0 ${showNavbar ? "top-14" : "top-0"} z-40 hidden w-2 cursor-col-resize bg-transparent transition-colors hover:bg-[var(--primary)]/50 min-[1600px]:block`}
        style={{ left: leftPanelWidth - 1 }}
      />

      {/* Floating Library Panel (Left) */}
      <aside
        style={{ width: leftPanelWidth }}
        className={`sandbox-panel sandbox-library-panel ${
          leftPanelVisible && shareMode !== "viewer" ? "is-panel-visible hidden min-[1600px]:flex" : "hidden"
        } absolute bottom-0 left-0 ${showNavbar ? "top-14" : "top-0"} z-30 flex-col overflow-hidden rounded-r-2xl border border-r-[var(--border)] border-border bg-card/95 shadow-[0_12px_40px_rgba(0,0,0,0.4),inset_0_1px_0_var(--border)] backdrop-blur-xl transition-[width]`}
      >
        <Library
          tab={libraryTab}
          setTab={setLibraryTab}
          addItem={addItem}
          addMaterial={addMaterial}
          selected={selected}
          mode={scenarioRuntime.scenario?.mode ?? (levelMode ? 'LEARNING' : 'NORMAL')}
          runtimeCatalog={scenarioRuntime.scenario?.catalog}
          allowedEquipmentIds={scenarioRuntime.scenario?.equipmentIds ?? []}
          allowedMaterialIds={scenarioRuntime.scenario?.materialIds ?? []}
          levelLabel={levelLabel}
          helpActive={helpActive}
          helpTab={getHelpTab(activeScenario?.id, activeScenario?.step)}
          helpTargets={resolveGuideTargets(activeScenario?.id, activeScenario?.step)}
          onStartScenario={(id) => {
            setActiveScenario({ id, step: 0 });
            setScenarioIntro(true);
            setTimeout(() => setScenarioIntro(false), 5000);
            completedScenarioRef.current = null;
            setExperimentResult(null);
            setRewardDismissed(false);
            setBottomDockOpen(true);
          }}
        />
        {selected && (
          <div className="border-t border-border/50 bg-muted/50 p-5 text-xs text-[var(--muted-foreground)] mt-auto shrink-0">
            <p className="mb-3 font-semibold text-foreground">Доступные порты</p>
            <div className="grid grid-cols-2 gap-y-2 text-[11px]">
              <span className="flex items-center gap-2">
                <i className="h-1.5 w-1.5 rounded-full bg-slate-400"></i> Стекло
              </span>
              <span className="flex items-center gap-2">
                <i className="h-1.5 w-1.5 rounded-full bg-cyan-400 shadow-[0_0_6px_rgba(34,211,238,0.4)]"></i> Жидкость
              </span>
              <span className="flex items-center gap-2">
                <i className="h-1.5 w-1.5 rounded-full bg-violet-400 shadow-[0_0_6px_rgba(167,139,250,0.4)]"></i> Газ
              </span>
              <span className="flex items-center gap-2">
                <i className="h-1.5 w-1.5 rounded-full bg-orange-400 shadow-[0_0_6px_rgba(251,146,60,0.4)]"></i> Нагрев
              </span>
              <span className="flex items-center gap-2 col-span-2">
                <i className="h-1.5 w-1.5 rounded-full bg-emerald-400 shadow-[0_0_6px_rgba(52,211,153,0.4)]"></i> Электричество
              </span>
            </div>
          </div>
        )}
      </aside>


      {/* Main Canvas Area */}
      <main className={`absolute left-0 right-0 bottom-0 ${showNavbar ? "top-14" : "top-0"} z-10 overflow-hidden ${shareMode === "viewer" ? "pointer-events-none" : ""}`}>
        <ParticleCanvas engine={engine} />
        <SandboxCanvas
          setSelectedId={() => setSelectedIds(new Set())}
          setConnectSourcePort={setConnectSourcePort}
          canvasRef={canvasRef}
          zoom={zoom}
          pan={pan}
          setPan={setPan}
          tool={tool}
          items={items}
          connections={connections}
          selectedId={selectedId}
          onPourExecute={pour}
          setPourSource={setPourSource}
          pourSource={pourSource}
          pourAnimation={pourAnimation}
          pourAmount={pourAmount}
          setPourAmount={setPourAmount}
            spillAnimation={spillAnimation}
          spills={engine?.workspace.scene.environment.spills ?? []}
          centers={centers}
          setSelectedIds={setSelectedIds}
            selectedIds={selectedIds}
          setConnectSource={setConnectSource}
          onPortPointerDown={startPortConnection}
          onPortPointerUp={finishPortConnection}
          onPortPointerEnter={hoverPort}
          setTool={setTool}
          cancelConnection={cancelConnection}
          onPointerMove={onPointerMove}
          endDrag={endDrag}
          onPointerDown={onPointerDown}
            onCanvasPointerDown={onCanvasPointerDown}
          isPanning={isPanning}
            temperatureReading={temperatureReading}
          duplicate={duplicate}
          remove={remove}
            hide={hide}
          connectSource={connectSource}
          connectSourcePort={connectSourcePort}
          connectionSnap={connectionSnap}
          portCompatibility={portCompatibility}
          connectionPointer={connectionPointer}
            marquee={marquee}
            setMarquee={setMarquee}
          emptyItem={emptyItem}
          temperatureConnected={temperatureConnected}
          collisionItemId={collisionItemId}
          showGrid={showGrid}
          updateItem={updateItem}
          onMix={mixItem}
          selectedConnectionId={selectedConnectionId}
          onConnectionSelect={setSelectedConnectionId}
          onConnectionDelete={disconnectConnection}
          onRoutePointMove={editRoute}
          helpTargets={resolveGuideTargets(activeScenario?.id, activeScenario?.step)}
          onResize={(id, scaleX, scaleY) => updateItem(id, { scaleX, scaleY })}
          onNudge={(id, dx, dy) => {
            const current = items.find((item) => item.id === id);
            if (current) updateItem(id, { x: Math.max(0, current.x + dx), y: Math.max(70, current.y + dy) });
          }}
          helpActive={helpActive}
          onSimulationStart={runExperiment}
        />
      </main>

      {/* Floating Top Toolbar */}
      {shareMode !== "viewer" && <SandboxToolbar
        tool={tool}
        setTool={setTool}
        showGrid={showGrid}
        runState={runState}
        helpActive={helpActive}
        helpTool={helpActive && tool !== "connect" && scenarioRuntime.scenario?.steps[activeScenario?.step ?? 0]?.hints[scenarioHintIndex % Math.max(1, scenarioRuntime.scenario?.steps[activeScenario?.step ?? 0]?.hints.length ?? 1)]?.type === 'CONNECT_PORTS' ? "connect" : undefined}
        onRun={runExperiment}
        onPause={pauseExperiment}
        onStop={stopExperiment}
        setShowGrid={setShowGrid}
        setConnectSource={setConnectSource}
        leftPanelVisible={leftPanelVisible}
        rightPanelVisible={rightPanelVisible}
        setLeftPanelVisible={setLeftPanelVisible}
        setRightPanelVisible={setRightPanelVisible}
        setZoom={setZoom}
        zoom={zoom}
        setMobilePanel={setMobilePanel}
        setBottomDockTab={setBottomDockTab}
        onUndo={undo}
        onRedo={redo}
        canUndo={history.canUndo}
        canRedo={history.canRedo}
        showNavbar={showNavbar}
      />}

      {mobilePanel === "library" && (
        <MobileSheet title={ts("equipment")} onClose={() => setMobilePanel(null)}>
          <Library
            tab={libraryTab}
            setTab={setLibraryTab}
            addItem={(value) => {
              addItem(value);
              setMobilePanel(null);
            }}
            addMaterial={addMaterial}
            selected={selected}
            mode={scenarioRuntime.scenario?.mode ?? (levelMode ? 'LEARNING' : 'NORMAL')}
            runtimeCatalog={scenarioRuntime.scenario?.catalog}
            allowedEquipmentIds={scenarioRuntime.scenario?.equipmentIds ?? []}
            allowedMaterialIds={scenarioRuntime.scenario?.materialIds ?? []}
            levelLabel={levelLabel}
            helpActive={helpActive}
            helpTab={getHelpTab(activeScenario?.id, activeScenario?.step)}
            helpTargets={resolveGuideTargets(activeScenario?.id, activeScenario?.step)}
            onStartScenario={(id) => {
              setActiveScenario({ id, step: 0 });
              setScenarioIntro(true);
              setTimeout(() => setScenarioIntro(false), 5000);
              completedScenarioRef.current = null;
              setExperimentResult(null);
              setRewardDismissed(false);
              setBottomDockOpen(true);
              setMobilePanel(null);
            }}
          />
        </MobileSheet>
      )}

      {shareMode !== "viewer" && (
        <nav className="sandbox-mobile-bottom-nav absolute inset-x-2 bottom-2 z-[70] hidden h-14 items-center justify-around rounded-2xl border border-border bg-card/95 px-2 shadow-[0_14px_40px_rgba(0,0,0,.28)] backdrop-blur-xl" aria-label="Мобильная навигация лаборатории">
          <button type="button" onClick={() => setMobilePanel("library")} className="flex min-w-16 flex-col items-center gap-0.5 text-[10px] font-semibold text-muted-foreground"><FlaskConical size={19}/><span>Колбы</span></button>
          <button type="button" onClick={() => { setBottomDockTab("Events"); setMobilePanel("dock"); }} className="flex min-w-16 flex-col items-center gap-0.5 text-[10px] font-semibold text-[var(--primary-bright)]"><BookOpen size={19}/><span>Журнал</span></button>
          <button type="button" onClick={() => selected ? setMobilePanel("inspector") : addToast("Сначала выберите прибор", "info")} className="flex min-w-16 flex-col items-center gap-0.5 text-[10px] font-semibold text-muted-foreground"><Sparkles size={19}/><span>Детали</span></button>
        </nav>
      )}

      {mobilePanel === "inspector" && selectedIds.size > 0 && (
        <MobileSheet title={ts("dock.equipmentDetails")} onClose={() => setMobilePanel(null)}>
            {selectedIds.size > 1 ? (
              <div className="flex flex-col items-center justify-center h-full text-muted-foreground p-8 text-center">
                <div className="w-12 h-12 rounded-full border-2 border-dashed border-muted-foreground/50 mb-4" />
                <p>Выделено несколько объектов ({selectedIds.size})</p>
              </div>
            ) : selected ? (
              <Properties
                item={selected}
                update={updateItem}
                onOperation={applyOperation}
                connections={connections.filter((connection) => connection.from === selected.id || connection.to === selected.id)}
                onConnectionDelete={disconnectConnection}
                onConnectionEdit={editConnection}
                onDeviceAction={deviceAction}
                onMaterialRemove={removeMaterial}
                setPourSource={setPourSource}
                pourSource={pourSource}
                temperatureConnected={temperatureConnected(selected.id)}
                levelMode={levelMode}
                onQuickAction={handleQuickAction}
              />
            ) : null}
          </MobileSheet>
      )}

      {/* Collapsible scientific dock */}
      <SandboxDock
        bottomDockOpen={bottomDockOpen}
        setBottomDockOpen={setBottomDockOpen}
        bottomDockTab={bottomDockTab}
        setBottomDockTab={setBottomDockTab}
        syncStatus={syncStatus}
        itemsLength={items.length}
        eventLog={eventLog}
        selected={selected}
        measurementSamples={measurementSamples}
        mobilePanel={mobilePanel}
        setMobilePanel={setMobilePanel}
        activeScenario={activeScenario}
        runtimeScenario={scenarioRuntime.scenario}
        scenarioIntro={levelMode ? false : scenarioIntro}
        experimentResult={experimentResult}
        leftOffset={(leftPanelVisible ? leftPanelWidth : 0) + 40}
        rightOffset={(rightPanelVisible ? rightPanelWidth : 0) + 40}
        temperatureConnected={selected ? temperatureConnected(selected.id) : false}
        measuredTemperature={selected ? temperatureReading(selected.id) : null}
        helpActive={helpActive}
        onHelp={handleHelp}
        onShowHow={() => setGuideDemoOpen(true)}
        hintIndex={scenarioHintIndex}
        onNextHint={() => setScenarioHintIndex((current) => {
          const count = scenarioRuntime.scenario?.steps[activeScenario?.step ?? 0]?.hints.length ?? 1;
          return (current + 1) % Math.max(1, count);
        })}
      />

      <ScenarioGuideOverlay active={helpActive} hint={scenarioRuntime.scenario?.steps[activeScenario?.step ?? 0]?.hints[scenarioHintIndex % Math.max(1, scenarioRuntime.scenario?.steps[activeScenario?.step ?? 0]?.hints.length ?? 1)]}/>

      {/* Floating Inspector Panel (Right) */}
      <div
        aria-label="Изменить ширину панели инспектора"
        role="separator"
        onPointerDown={(event) => startPanelResize("right", event)}
        className={`absolute bottom-0 ${showNavbar ? "top-14" : "top-0"} z-40 hidden w-2 cursor-col-resize bg-transparent transition-colors hover:bg-[var(--primary)]/50 min-[1600px]:block`}
        style={{ right: rightPanelWidth - 1 }}
      />

      <aside
        style={{ width: rightPanelWidth }}
        className={`sandbox-panel sandbox-inspector-panel ${
          rightPanelVisible && shareMode !== "viewer" ? "is-panel-visible hidden min-[1600px]:flex" : "hidden"
        } absolute bottom-0 right-0 ${showNavbar ? "top-14" : "top-0"} z-30 flex-col overflow-hidden rounded-l-2xl border border-l-[var(--border)] border-border bg-card/95 shadow-[0_12px_40px_rgba(0,0,0,0.4),inset_0_1px_0_var(--border)] backdrop-blur-xl transition-[width]`}
      >
        <div className="border-b border-border/50 bg-muted/30 p-5">
          <h2 className="mt-1.5 text-base font-semibold tracking-tight text-foreground">{ts("dock.equipmentDetails")}</h2>
        </div>
        <section className="border-b border-border p-3">
          <div className="mb-2 flex items-center justify-between">
            <h3 className="text-xs font-bold uppercase tracking-[.14em] text-muted-foreground">{ts("equipment")}</h3>
            <span className="text-[10px] text-[var(--muted-foreground)]">{items.length}</span>
          </div>
          {selected && (
            <div className="sandbox-encyclopedia mb-2 rounded-lg border border-[var(--primary)]/35 bg-[var(--primary)]/[.1] p-3 shadow-inner">
              <p className="text-[10px] font-bold uppercase tracking-[.12em] text-[var(--primary-bright)]">{ts("dock.description")}</p>
              <p className="mt-1 text-sm font-bold text-foreground">{ts.has(`equip.${selected.type}.name`) ? ts(`equip.${selected.type}.name`) : selected.name}</p>
              <p className="mt-1 text-xs leading-relaxed text-muted-foreground">{ts.has(`equip.${selected.type}.desc`) ? ts(`equip.${selected.type}.desc`) : equipmentDescription(selected)}</p>
              <button
                type="button"
                onClick={() => {
                  setCodexTarget({ type: 'equipment', id: selected.type });
                  setCodexOpen(true);
                }}
                className="mt-2 inline-flex items-center gap-1 rounded-md border border-[var(--primary)]/30 bg-[var(--primary)]/10 px-2 py-1 text-[10px] font-semibold text-[var(--primary-bright)] transition-colors hover:bg-[var(--primary)]/20 hover:text-foreground"
              >
                {ts("dock.readMore")}
                <span aria-hidden="true">→</span>
              </button>
              
              {(selected.metadata?.historyYear || selected.metadata?.historyText) && (
                <div className="mt-3 border-t border-[var(--primary)]/10 pt-3">
                  <p className="flex items-center gap-1.5 text-[10px] font-bold uppercase tracking-[.12em] text-emerald-600 dark:text-emerald-400">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" /></svg>
                    {ts("dock.encyclopedia")} {selected.metadata.historyYear ? `(${selected.metadata.historyYear})` : ''}
                  </p>
                  {(selected.metadata.historyText as string).length > 100 ? (
                    <details className="group">
                      <summary className="list-none cursor-pointer outline-none">
                        <div className="mt-1.5 text-[11px] leading-relaxed text-foreground/70 line-clamp-3 group-open:line-clamp-none">
                          {selected.metadata.historyText as string}
                        </div>
                        <span className="mt-1 inline-block text-[10px] font-semibold text-[var(--primary-bright)] group-open:hidden">…</span>
                      </summary>
                    </details>
                  ) : (
                    <p className="mt-1.5 text-[11px] leading-relaxed text-foreground/70">
                      {selected.metadata.historyText as string}
                    </p>
                  )}
                  {selected.metadata?.referenceLink && (
                    <a href={selected.metadata.referenceLink as string} target="_blank" rel="noreferrer" className="mt-2 inline-flex items-center gap-1 text-[10px] font-semibold text-[var(--primary-bright)] hover:text-foreground transition-colors">
                      {ts("dock.readOnWiki")}
                      <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path><polyline points="15 3 21 3 21 9"></polyline><line x1="10" y1="14" x2="21" y2="3"></line></svg>
                    </a>
                  )}
                </div>
              )}
            </div>
          )}
          <div className="max-h-44 space-y-1.5 overflow-y-auto pr-1">
            {items.length === 0 ? (
              <p className="text-[11px] text-[var(--muted-foreground)]">{ts("dock.selectApparatus")}</p>
            ) : (
              items.map((equipment) => {
                const heaterPower = Number((equipment.capabilities?.thermalOutput as { powerW?: number } | undefined)?.powerW ?? 0);
                const equipmentValue = isVessel(equipment)
                  ? `${equipment.volumeMl.toFixed(1)} мл`
                  : equipment.type === "thermometer"
                  ? `${equipment.temperature.toFixed(1)} °C`
                  : heaterPower > 0
                  ? `${heaterPower} Вт`
                  : "";
                return (
                  <button
                    key={equipment.id}
                    type="button"
                    onClick={() => setSelectedIds(new Set([equipment.id]))}
                    className={`flex w-full items-center gap-2 rounded-lg border px-2 py-1.5 text-left transition ${
                      selectedId === equipment.id ? "border-[var(--primary)] bg-[var(--primary)]/15" : "border-border bg-foreground/5 hover:border-foreground/20"
                    }`}
                  >
                    <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-md bg-foreground/10">
                      <EquipmentThumbnail type={equipment.type} size={30} />
                    </span>
                    <span className="min-w-0 flex-1 truncate text-[11px] font-semibold text-foreground/85">{ts.has(`equip.${equipment.type}.name`) ? ts(`equip.${equipment.type}.name`) : equipment.name}</span>
                    {equipmentValue && <span className="shrink-0 text-[10px] font-mono text-[var(--primary-bright)]">{equipmentValue}</span>}
                    {equipment.operation === "heating" && <span className="h-2 w-2 shrink-0 rounded-full bg-orange-400 shadow-[0_0_8px_#fb923c]" />}
                  </button>
                );
              })
            )}
          </div>
        </section>

        <div className="flex-1 overflow-y-auto">
          {experimentResult && (
            <div className="m-4 mb-2 animate-fade-in-up rounded-2xl border border-lime-400/70 bg-[#17210f] px-4 py-3 text-slate-100 shadow-[0_0_24px_rgba(132,204,22,.22)]">
              <div className="mb-2 flex items-center gap-2">
                <div className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-lime-400 text-xs font-black text-slate-950">✓</div>
                <p className="text-[13px] font-bold text-lime-300">{experimentResult.title}</p>
              </div>
              <p className="text-[11px] text-slate-300">{experimentResult.description}</p>
              <div className="mt-2 flex flex-wrap gap-2 font-mono text-[10px] text-lime-300/90">
                {experimentResult.volumeMl !== undefined && <span>{experimentResult.volumeMl.toFixed(0)} мл</span>}
                {experimentResult.temperatureC !== undefined && <span>{experimentResult.temperatureC.toFixed(1)} °C</span>}
                <span>1 атм</span>
              </div>
            </div>
          )}
          {selected ? (
            <Properties
              item={selected}
              update={updateItem}
              onOperation={applyOperation}
              connections={connections.filter((connection) => connection.from === selected.id || connection.to === selected.id)}
              onConnectionDelete={disconnectConnection}
              onConnectionEdit={editConnection}
              onDeviceAction={deviceAction}
              onMaterialRemove={removeMaterial}
              setPourSource={setPourSource}
              pourSource={pourSource}
              temperatureConnected={temperatureConnected(selected.id)}
              levelMode={levelMode}
              onQuickAction={handleQuickAction}
              />
          ) : (
            <div className="sandbox-inspector-empty flex h-full max-h-64 flex-col items-center justify-center p-6 text-center text-[var(--muted-foreground)]">
              <div className="mb-3 rounded-2xl border border-[var(--primary)]/25 bg-[var(--primary)]/10 p-3 text-[var(--primary-bright)]">
                <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.7">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                </svg>
              </div>
              <p className="max-w-[220px] text-sm font-semibold text-foreground">{ts("dock.selectApparatus")}</p>
              <p className="mt-2 max-w-[220px] text-[11px] leading-relaxed text-muted-foreground">{ts.has("dock.selectHint") ? ts("dock.selectHint") : "Кликните по оборудованию на сцене."}</p>
            </div>
          )}
        </div>
      </aside>

      {/* Dialogs */}
      {connectionDraft && (
        <ConnectionDialog
          onSave={() => {
            const from = engine?.workspace.scene.objects.get(connectionDraft.from);
            const to = engine?.workspace.scene.objects.get(connectionDraft.to);
            if (from && to && engine) {
              const fromPort = from.ports[0]?.id || "default";
              const toPort = to.ports[0]?.id || "default";
              connectPorts(from.id, fromPort, to.id, toPort);
            }
            setConnectionDraft(null);
          }}
          onClose={() => setConnectionDraft(null)}
        />
      )}

      {shareSnapshot && <ShareDialog snapshot={shareSnapshot} workspaceId={workspaceId} onClose={() => setShareSnapshot(null)} />}

      {shareMode !== "viewer" && (
        <>
          <button type="button" onClick={() => setAssistantOpen((open) => !open)} className={`sandbox-assistant-trigger fixed bottom-5 right-5 z-[95] grid h-13 w-13 place-items-center rounded-full border shadow-[0_14px_36px_rgba(124,58,237,.28)] transition-all hover:-translate-y-1 active:scale-95 ${assistantOpen ? "border-[var(--primary)] bg-[var(--primary)] text-white" : "border-border bg-card text-[var(--primary)]"}`} aria-label={assistantOpen ? "Закрыть чат ассистента" : "Открыть чат ассистента"} aria-expanded={assistantOpen}>
            <Bot size={24} strokeWidth={1.8} />
          </button>
          {assistantOpen && <AssistantPanel messages={assistantMessages} onSend={sendAssistantMessage} onExecuteAction={executeAssistantAction} onClose={() => setAssistantOpen(false)} isTeamChat={isTeamChat} />}
        </>
      )}

      {experimentResult && academyLevel && !rewardDismissed && (() => {
        const completedLevel = Number(academyLevel);
        const nextLevel = Number.isFinite(completedLevel) && legacyScenarioForLevel(String(completedLevel + 1)) ? completedLevel + 1 : undefined;
        return (
          <LevelRewardOverlay
            level={completedLevel}
            nextLevel={nextLevel}
            onNext={() => {
              if (!nextLevel) return;
              const nextQuery = new URLSearchParams(query.toString());
              nextQuery.set("level", String(nextLevel));
              nextQuery.delete("experimentId");
              setRewardDismissed(true);
              router.push(`${pathname}?${nextQuery.toString()}`);
            }}
            onAcademy={() => router.push(`/${locale}/lchemistry-level`)}
            onClose={() => setRewardDismissed(true)}
          />
        );
      })()}

      {clearWorkspaceModal && (
        <ResetConfirmDialog
          title="Очистить рабочее поле?"
          description="Все созданные приборы, налитые вещества и действующие соединения будут полностью удалены."
          confirmLabel="Очистить"
          cancelLabel="Отмена"
          onConfirm={() => {
            if (engine) {
              engine.workspace.scene.objects.clear();
              engine.workspace.scene.connections.clear();
              engine.notifyUpdate();
              setSelectedIds(new Set());
              setExperimentResult(null);
            }
          }}
          onClose={() => setClearWorkspaceModal(false)}
        />
      )}

      <DebugPanel
        items={items}
        connections={connections}
        selectedId={selectedId}
        historyLogs={items.flatMap((i) => i.history ?? [])}
      />

      {jasScienceLevel !== null && (
        <JasScienceModal
          initialLevel={jasScienceLevel}
          onClose={() => setJasScienceLevel(null)}
        />
      )}

      {codexOpen && <CodexModal initialContext={codexTarget} onClose={() => { setCodexOpen(false); setCodexTarget(undefined); }} onOpenLab={(context) => {
        setCodexOpen(false);
        setCodexTarget(undefined);
        // The context is applied immediately below. Mark URL hydration as handled
        // so router.replace cannot spawn the same Codex entity a second time.
        codexContextLoaded.current = true;
        const nextQuery = new URLSearchParams(query.toString());
        
        if (context.type === 'equipment') {
          nextQuery.set("equipmentId", context.id);
          if (engine && registry.get(context.id)) {
            const objectId = typeof crypto !== "undefined" && "randomUUID" in crypto
              ? `codex-${context.id}-${crypto.randomUUID()}`
              : `codex-${context.id}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;
            const object = registry.create(context.id, { id: objectId });
            object.position = { x: Math.random() * 200 + 300, y: Math.random() * 100 + 200 };
            engine.workspace.scene.add(object);
            engine.notifyUpdate();
            setSelectedIds(new Set([object.id]));
            addToast(`Spawned: ${context.id}`, "success");
          }
        }
        
        if (context.type === 'scenario') {
          nextQuery.set("experimentId", context.id);
          if (context.level) nextQuery.set("level", context.level);
          setActiveScenario({ id: context.id, step: 0 });
          setBottomDockOpen(true);
          setScenarioIntro(true);
          addToast(`Scenario loaded: ${context.id}`, "success");
        }
        
        if (context.type === 'material') {
          nextQuery.set("materialId", context.id);
          setLibraryTab("materials");
          addToast(`Opened material catalog`, "info");
        }
        
        router.replace(`${pathname}?${nextQuery.toString()}`);
      }} />}
    </div>
    </ScenarioRuntimeProvider>
  );
}
