"use client";

import { Play, Timer } from "lucide-react";
import { EquipmentThumbnail } from "@/entities/equipment/ui/EquipmentRendererRegistry";
import type { ChemistryLevelDefinition, SupportedLocale } from "@/data/chemistryLevels";

const equipmentNames: Record<string, Record<SupportedLocale, string>> = {
  beaker: { ru: "Стакан 250 мл", en: "Beaker 250 ml", uz: "Stakan 250 ml" },
  thermometer: { ru: "Термометр", en: "Thermometer", uz: "Termometr" },
  hotplate: { ru: "Нагреватель", en: "Hot plate", uz: "Qizdirgich" },
  pipette: { ru: "Пипетка", en: "Pipette", uz: "Pipetka" },
};

const materialNames: Record<string, Record<SupportedLocale, string>> = {
  H2O: { ru: "Вода", en: "Water", uz: "Suv" },
  "CuSO4(aq)": { ru: "Раствор CuSO₄", en: "Copper sulfate solution", uz: "Mis sulfat eritmasi" },
};

const copy: Record<SupportedLocale, { level: string; objective: string; learn: string; required: string; start: string }> = {
  ru: { level: "Уровень", objective: "Цель", learn: "Сегодня вы научитесь", required: "Понадобится", start: "Начать" },
  en: { level: "Level", objective: "Objective", learn: "Today you will learn", required: "You will need", start: "Start" },
  uz: { level: "Daraja", objective: "Maqsad", learn: "Bugun o'rganasiz", required: "Kerak bo'ladi", start: "Boshlash" },
};

export function LevelIntro({ level, locale, onStart }: { level: ChemistryLevelDefinition; locale: SupportedLocale; onStart: () => void }) {
  const text = copy[locale];
  return (
    <div className="fixed inset-0 z-[220] flex items-start justify-center bg-slate-950/35 px-4 pt-24 backdrop-blur-[1px]" role="dialog" aria-modal="true" aria-label={`${text.level} ${level.id}`}>
      <section className="w-full max-w-xl overflow-hidden rounded-3xl border border-cyan-400/25 bg-card/95 text-foreground shadow-[0_20px_70px_rgba(8,15,32,.45)] backdrop-blur-xl animate-fade-in-up">
        <div className="h-1 bg-gradient-to-r from-cyan-400 via-[var(--primary)] to-violet-400" />
        <div className="p-5 sm:p-6">
          <div className="flex items-start justify-between gap-4">
            <div className="min-w-0">
              <span className="text-[11px] font-black uppercase tracking-[.18em] text-cyan-400">{text.level} {level.id}</span>
              <h1 className="mt-1 text-2xl font-black sm:text-3xl">{level.title[locale]}</h1>
            </div>
            <img src="/workspace-previews/cartoon-chemistry-lab.png" alt="" className="hidden h-14 w-20 shrink-0 rounded-xl border border-cyan-300/25 object-cover object-center shadow-[0_0_20px_rgba(34,211,238,.16)] sm:block" />
          </div>

          <div className="mt-5 rounded-2xl border border-border bg-foreground/[.03] p-4">
            <p className="text-[10px] font-black uppercase tracking-[.14em] text-muted-foreground">{text.objective}</p>
            <p className="mt-1 text-sm leading-6 text-foreground/85">{level.objective[locale]}</p>
          </div>

          <div className="mt-4 grid gap-4 sm:grid-cols-2">
            <div>
              <p className="text-[10px] font-black uppercase tracking-[.14em] text-muted-foreground">{text.learn}</p>
              <ul className="mt-2 space-y-1.5 text-xs text-foreground/80">
                {level.learningPoints.map((point) => <li key={point.en} className="flex gap-2"><span className="text-cyan-400">✓</span>{point[locale]}</li>)}
              </ul>
            </div>
            <div>
              <p className="text-[10px] font-black uppercase tracking-[.14em] text-muted-foreground">{text.required}</p>
              <div className="mt-2 flex flex-wrap gap-2">
                {level.allowedEquipment.map((equipment) => <span key={equipment} className="flex items-center gap-1.5 rounded-xl border border-border bg-muted/30 px-2 py-1 text-[11px] font-semibold"><EquipmentThumbnail type={equipment} size={22} />{equipmentNames[equipment]?.[locale] ?? equipment}</span>)}
                {level.allowedMaterials.map((material) => <span key={material} className="rounded-xl border border-cyan-400/20 bg-cyan-400/10 px-2 py-1 text-[11px] font-semibold text-cyan-200">{materialNames[material]?.[locale] ?? material}</span>)}
              </div>
            </div>
          </div>

          <div className="mt-5 flex items-center justify-between gap-3">
            <span className="flex items-center gap-1.5 text-xs font-semibold text-muted-foreground"><Timer size={14} />{level.duration[locale]}</span>
            <button type="button" onClick={onStart} className="inline-flex min-h-11 items-center gap-2 rounded-xl bg-[var(--primary)] px-5 text-sm font-bold text-white shadow-[0_8px_24px_rgba(124,58,237,.3)] transition hover:brightness-110 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cyan-300">{text.start}<Play size={16} /></button>
          </div>
        </div>
      </section>
    </div>
  );
}
