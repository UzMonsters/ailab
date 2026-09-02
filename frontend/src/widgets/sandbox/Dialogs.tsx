import { useState } from "react";
import { useTranslations } from "next-intl";
import {
  ArrowRight,
  BookOpen,
  CheckCircle2,
  FlaskConical,
  LockKeyhole,
  ShieldCheck,
  Sparkles,
  Trophy,
  X,
} from "lucide-react";
import type { Port, Connection, Item } from "@/widgets/sandbox/types";

export function ConnectionDialog({
  onSave,
  onClose,
}: {
  onSave: (port: Port, direction: Connection["direction"]) => void;
  onClose: () => void;
}) {
  const ts = useTranslations("sandbox");
  const [port, setPort] = useState<Port>("Glass");
  const [direction, setDirection] =
    useState<Connection["direction"]>("source-to-target");
  return (
    <div
      className="fixed inset-0 z-[90] grid place-items-center bg-black/55 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="connection-dialog-title"
    >
      <div className="w-full max-w-sm rounded-2xl border border-[var(--border)] bg-[var(--card)] p-5 shadow-2xl animate-fade-in-up transition-all duration-300">
        <div className="flex items-center justify-between">
          <h2 id="connection-dialog-title" className="font-bold">
            {ts("configureConnection")}
          </h2>
          <button
            className="touch-target"
            onClick={onClose}
            aria-label={ts("close")}
          >
            <X size={18} />
          </button>
        </div>
        <label className="mt-4 block text-sm">
          {ts("type")}
          <select
            value={port}
            onChange={(event) => setPort(event.target.value as Port)}
            className="mt-2 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] p-3"
          >
            <option>Glass</option>
            <option>Liquid</option>
            <option>Gas</option>
            <option>Thermal</option>
            <option>Electrical</option>
          </select>
        </label>
        <label className="mt-4 block text-sm">
          {ts("direction")}
          <select
            value={direction}
            onChange={(event) =>
              setDirection(event.target.value as Connection["direction"])
            }
            className="mt-2 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] p-3"
          >
            <option value="source-to-target">{ts("sourceToTarget")}</option>
            <option value="target-to-source">{ts("targetToSource")}</option>
          </select>
        </label>
        <div className="mt-5 flex justify-end gap-2">
          <button
            className="min-h-11 rounded-xl border border-[var(--border)] px-4"
            onClick={onClose}
          >
            {ts("cancel")}
          </button>
          <button
            className="min-h-11 rounded-xl bg-[var(--primary)] px-4 font-semibold text-white"
            onClick={() => onSave(port, direction)}
          >
            {ts("connect")}
          </button>
        </div>
      </div>
    </div>
  );
}

export function PourDialog({
  source,
  targets,
  amount,
  setAmount,
  onPour,
  onClose,
}: {
  source?: Item;
  targets: Item[];
  amount: number;
  setAmount: (value: number) => void;
  onPour: (id: string) => void;
  onClose: () => void;
}) {
  const ts = useTranslations("sandbox");
  return (
    <div
      className="fixed inset-0 z-[90] grid place-items-center bg-black/55 p-4"
      role="dialog"
      aria-modal="true"
      aria-labelledby="pour-dialog-title"
    >
      <div className="w-full max-w-sm rounded-2xl border border-[var(--border)] bg-[var(--card)] p-5 animate-fade-in-up transition-all duration-300">
        <div className="flex items-center justify-between">
          <h2 id="pour-dialog-title" className="font-bold">
            {ts("pourFrom", { name: source?.name ?? "" })}
          </h2>
          <button
            className="touch-target"
            onClick={onClose}
            aria-label={ts("close")}
          >
            <X size={18} />
          </button>
        </div>
        <label className="mt-4 block text-sm font-medium">
          {ts("amount")}: <span className="text-[var(--primary-bright)] font-bold">{amount} мл</span>
          <input
            aria-label={ts("amount")}
            type="range"
            min="1"
            max={source?.volumeMl ?? 100}
            step="1"
            value={amount}
            onChange={(event) => setAmount(Number(event.target.value))}
            className="mt-3 w-full accent-[var(--primary)]"
          />
        </label>
        <div className="mt-4 grid gap-2">
          {targets.map((target) => (
            <button
              key={target.id}
              className="min-h-11 rounded-xl border border-[var(--border)] text-left px-3"
              onClick={() => onPour(target.id)}
            >
              {target.name}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

export function ResetConfirmDialog({
  title = "Сбросить эксперимент?",
  description = "Текущие изменения, налитые вещества и временные соединения будут сброшены до начального состояния.",
  confirmLabel = "Сбросить",
  cancelLabel = "Отмена",
  onConfirm,
  onClose,
}: {
  title?: string;
  description?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onClose: () => void;
}) {
  return (
    <div
      className="fixed inset-0 z-[200] grid place-items-center bg-black/70 p-4 backdrop-blur-md transition-opacity"
      role="dialog"
      aria-modal="true"
      aria-labelledby="reset-dialog-title"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="w-full max-w-sm animate-fade-in-up rounded-2xl border border-red-500/30 bg-card p-6 shadow-[0_32px_80px_rgba(0,0,0,0.8)]">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-2 text-red-400">
            <span className="h-3 w-3 rounded-full bg-red-500 animate-ping" />
            <h2 id="reset-dialog-title" className="text-base font-bold text-white">
              {title}
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Закрыть"
            className="rounded-lg p-1.5 text-white/40 transition-colors hover:bg-white/10 hover:text-white"
          >
            <X size={16} />
          </button>
        </div>

        <p className="mt-3 text-xs leading-relaxed text-muted-foreground">
          {description}
        </p>

        <div className="mt-6 flex justify-end gap-2">
          <button
            type="button"
            onClick={onClose}
            className="rounded-xl border border-border bg-white/[0.03] px-4 py-2.5 text-xs font-semibold text-muted-foreground transition-all hover:bg-white/10 hover:text-white"
          >
            {cancelLabel}
          </button>
          <button
            type="button"
            onClick={() => {
              onConfirm();
              onClose();
            }}
            className="rounded-xl bg-red-600 px-4 py-2.5 text-xs font-bold text-white shadow-lg transition-all hover:bg-red-500 active:scale-95"
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );
}

export function JasScienceModal({
  onClose,
  initialLevel = 1,
}: {
  onClose: () => void;
  initialLevel?: number;
}) {
  const t = useTranslations("academy");
  const [activeLevel, setActiveLevel] = useState(initialLevel);

  const levels = [
    {
      id: 1,
      title: t("levels.l1.title"),
      description: t("levels.l1.desc"),
      img: "/images/academy/levels-hero.png",
      object: t("levels.l1.object"),
      equipment: t("levels.l1.equipment"),
      skill: t("levels.l1.skill"),
      reward: t("levels.l1.reward"),
      color: "cyan",
    },
    {
      id: 2,
      title: t("levels.l2.title"),
      description: t("levels.l2.desc"),
      img: "/icon-physics.png",
      object: t("levels.l2.object"),
      equipment: t("levels.l2.equipment"),
      skill: t("levels.l2.skill"),
      reward: t("levels.l2.reward"),
      color: "orange",
    },
    {
      id: 3,
      title: t("levels.l3.title"),
      description: t("levels.l3.desc"),
      img: "/mol-water.png",
      object: t("levels.l3.object"),
      equipment: t("levels.l3.equipment"),
      skill: t("levels.l3.skill"),
      reward: t("levels.l3.reward"),
      color: "violet",
    },
    {
      id: 4,
      title: t("levels.l4.title"),
      description: t("levels.l4.desc"),
      img: "/molecular_network.png",
      object: t("levels.l4.object"),
      equipment: t("levels.l4.equipment"),
      skill: t("levels.l4.skill"),
      reward: t("levels.l4.reward"),
      color: "blue",
    },
  ];
  const selected = levels.find((level) => level.id === activeLevel) ?? levels[0];

  const colorStyles: Record<string, string> = {
    cyan: "border-cyan-400/30 bg-cyan-400/[0.08] text-cyan-200",
    orange: "border-orange-400/30 bg-orange-400/[0.08] text-orange-200",
    violet: "border-violet-400/30 bg-violet-400/[0.08] text-violet-200",
    blue: "border-blue-400/30 bg-blue-400/[0.08] text-blue-200",
  };

  return (
    <div
      className="fixed inset-0 z-[200] grid place-items-center overflow-y-auto bg-[#050712]/90 p-3 py-6 backdrop-blur-xl transition-opacity sm:p-6 sm:py-10"
      role="dialog"
      aria-modal="true"
      onClick={(e) => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="level-encyclopedia-modal w-full max-w-5xl animate-fade-in-up overflow-hidden rounded-[28px] border border-violet-400/25 bg-[#0d1020] shadow-[0_0_100px_rgba(124,58,237,0.22)]">
        <div className="border-b border-white/10 bg-gradient-to-r from-violet-500/[0.14] via-transparent to-cyan-400/[0.08] p-5 sm:p-7">
          <div className="flex items-start justify-between gap-4">
            <div className="flex items-start gap-3">
              <div className="grid h-11 w-11 shrink-0 place-items-center rounded-2xl border border-violet-300/30 bg-violet-400/15 text-violet-200">
                <BookOpen size={21} />
              </div>
              <div>
                <p className="text-[10px] font-bold uppercase tracking-[0.24em] text-cyan-300/80">{t("eyebrow")}</p>
                <h2 className="mt-1 text-2xl font-extrabold tracking-tight text-white md:text-3xl">{t("title")}</h2>
                <p className="mt-2 max-w-2xl text-xs leading-relaxed text-white/55 md:text-sm">{t("subtitle")}</p>
              </div>
            </div>
          <button
            type="button"
            onClick={onClose}
            aria-label="Закрыть"
            className="rounded-xl border border-white/10 bg-white/5 p-2 text-white/45 transition-colors hover:bg-white/10 hover:text-white"
          >
            <X size={20} />
          </button>
        </div>
          <div className="mt-5 flex flex-wrap items-center gap-2 text-[11px] text-white/45">
            <span className="rounded-full border border-white/10 bg-black/20 px-3 py-1.5">{t("entryCount", { count: levels.length })}</span>
            <span className="rounded-full border border-emerald-300/20 bg-emerald-400/10 px-3 py-1.5 text-emerald-200"><Sparkles size={12} className="mr-1 inline" />{t("readHint")}</span>
          </div>
        </div>

        <div className="grid gap-5 p-4 sm:p-6 lg:grid-cols-[1.3fr_0.7fr] lg:p-7">
          <article className={`overflow-hidden rounded-3xl border ${colorStyles[selected.color]} bg-black/20`}>
            <div className="relative h-44 overflow-hidden border-b border-white/10 bg-[#070a16] sm:h-56">
              <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_20%,rgba(124,58,237,0.25),transparent_58%)]" />
              <img src={selected.img} alt={selected.title} className="relative h-full w-full object-contain p-7 opacity-90 transition-transform duration-700 hover:scale-105" />
              <span className="absolute left-4 top-4 rounded-full border border-white/15 bg-black/45 px-2.5 py-1 text-[10px] font-bold uppercase tracking-wider text-white/75">{t("fieldNote", { id: selected.id })}</span>
              {selected.id === initialLevel && <span className="absolute right-4 top-4 rounded-full bg-violet-500 px-2.5 py-1 text-[10px] font-bold text-white">{t("current")}</span>}
            </div>
            <div className="p-5 sm:p-6">
              <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-[0.18em] text-cyan-300/75"><FlaskConical size={14} />{t("researchEntry")}</div>
              <h3 className="mt-3 text-2xl font-extrabold leading-tight text-white">{t("levelLabel", { id: selected.id })}: {selected.title}</h3>
              <p className="mt-3 text-sm leading-7 text-white/60">{selected.description}</p>
              <div className="mt-5 grid gap-2 sm:grid-cols-2">
                <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3"><p className="text-[10px] font-bold uppercase tracking-wider text-white/35">{t("objectLabel")}</p><p className="mt-1 text-sm text-white/80">{selected.object}</p></div>
                <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3"><p className="text-[10px] font-bold uppercase tracking-wider text-white/35">{t("equipmentLabel")}</p><p className="mt-1 text-sm text-white/80">{selected.equipment}</p></div>
              </div>
              <div className="mt-4 flex items-start gap-3 rounded-2xl border border-emerald-300/15 bg-emerald-400/[0.07] p-3 text-sm text-emerald-100/80"><ShieldCheck size={17} className="mt-0.5 shrink-0 text-emerald-300" /><span><b className="font-semibold text-emerald-200">{t("skillLabel")}:</b> {selected.skill}</span></div>
              <div className="mt-5 flex flex-wrap items-center justify-between gap-3 border-t border-white/10 pt-4">
                <div className="flex items-center gap-2 text-sm text-amber-200"><Trophy size={16} />{selected.reward}</div>
                <button onClick={onClose} className="inline-flex items-center gap-2 rounded-xl bg-violet-500 px-4 py-2.5 text-sm font-bold text-white shadow-lg shadow-violet-900/30 transition hover:bg-violet-400">{selected.id === initialLevel ? t("continue") : t("start")} <ArrowRight size={15} /></button>
              </div>
            </div>
          </article>

          <aside className="rounded-3xl border border-white/10 bg-black/20 p-3 sm:p-4">
            <div className="flex items-center justify-between px-2 pb-3"><div><p className="text-[10px] font-bold uppercase tracking-[0.2em] text-white/35">{t("catalog")}</p><h3 className="mt-1 font-bold text-white">{t("chapters")}</h3></div><span className="text-xs text-white/35">{activeLevel}/{levels.length}</span></div>
            <div className="space-y-2">
              {levels.map((level) => (
            <div
              key={level.id}
              role="button"
              tabIndex={0}
              onClick={() => setActiveLevel(level.id)}
              onKeyDown={(event) => { if (event.key === "Enter" || event.key === " ") setActiveLevel(level.id); }}
              className={`group flex cursor-pointer items-center gap-3 rounded-2xl border p-3 text-left transition-all duration-300 ${activeLevel === level.id ? "border-violet-400/60 bg-violet-400/15 shadow-[0_0_24px_rgba(124,58,237,0.14)]" : "border-white/10 bg-white/[0.03] hover:border-white/25 hover:bg-white/[0.07]"}`}
            >
              <div className={`grid h-11 w-11 shrink-0 place-items-center rounded-xl border ${colorStyles[level.color]}`}><span className="text-sm font-black">{level.id}</span></div>
              <div className="min-w-0 flex-1"><p className="truncate text-sm font-bold text-white/90">{level.title}</p><p className="mt-0.5 line-clamp-2 text-[11px] leading-relaxed text-white/40">{level.description}</p></div>
              {level.id < initialLevel ? <CheckCircle2 size={16} className="shrink-0 text-emerald-300" /> : level.id > initialLevel ? <LockKeyhole size={15} className="shrink-0 text-white/25" /> : <ArrowRight size={16} className="shrink-0 text-violet-300" />}
            </div>
              ))}
            </div>
            <div className="mt-4 rounded-2xl border border-white/10 bg-white/[0.03] p-4 text-xs leading-relaxed text-white/45"><p className="font-semibold text-white/70">{t("readingTipTitle")}</p><p className="mt-1">{t("readingTip")}</p></div>
          </aside>
        </div>

        <div className="flex justify-center border-t border-white/10 p-5 sm:p-6">
          <a
            href="http://localhost:3000/ru#platform"
            target="_blank"
            rel="noreferrer"
            className="inline-flex items-center gap-2 rounded-xl border border-violet-300/25 bg-violet-400/10 px-5 py-2.5 text-xs font-bold text-violet-200 transition-colors hover:bg-violet-400/20 hover:text-white"
          >
            {t("platformLink")}
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"></path><polyline points="15 3 21 3 21 9"></polyline><line x1="10" y1="14" x2="21" y2="3"></line></svg>
          </a>
        </div>
      </div>
    </div>
  );
}
