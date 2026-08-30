"use client";

import { useState, type CSSProperties } from "react";
import { useTranslations } from "next-intl";
import { usePathname } from "next/navigation";
import { ExternalLink, ArrowRight } from "lucide-react";
import { equipmentDefinitions, type EquipmentDefinition } from "@/entities/codex/model/codexDefinitions";
import { materialDefinitions } from "@/entities/material/model/materialDefinitions";
import { scenarioDefinitions } from "@/entities/experiment/model/scenarioDefinitions";
import { useLocaleSwitch } from "@/shared/hooks/useLocaleSwitch";
import { EquipmentThumbnail } from "@/entities/equipment/ui/EquipmentRendererRegistry";
import {
  InfoBox,
  SectionHeading,
  FigureCaption,
  ChapterHeader,
  MarginNote,
  HandwrittenStrike,
  RedCorrection,
  FormulaBlock,
  TextbookBlockquote,
  HistoryTimeline,
  HistoryPeriod,
  JournalDate,
} from "./components/TextbookPage";
import { PracticeQuiz, PracticeStepSorter } from "./components/PracticeQuiz";
import { TornPageSpread, TornPageFinalSpread } from "./components/TornPage";
import { BoyleVolumeLab, EquipmentMiniLab, MendeleevCompare, PeriodicTableFoldout, SubstanceStateDemo, SubstanceVisual, SandboxMaterialVisual } from "./components/JournalInteractions";
import type { CodexLabContext } from "./CodexModal";

export type CodexSection = string;

// ─── Helpers ─────────────────────────────────────────────────────────────────

const Img = ({
  src,
  width = "100%",
  height = 260,
  className = "",
  alt = "",
  style,
}: {
  src: string;
  width?: string | number;
  height?: string | number;
  className?: string;
  alt?: string;
  style?: CSSProperties;
}) => (
  <img
    src={src}
    alt={alt}
    className={className}
    style={{ width, height, objectFit: "contain", maxWidth: "100%", display: "block", ...style }}
  />
);

// A standard left+right book spread wrapper
function Spread({
  left,
  right,
  leftHeader,
  rightHeader,
  pageNumLeft,
  pageNumRight,
}: {
  left: React.ReactNode;
  right: React.ReactNode;
  leftHeader?: string;
  rightHeader?: string;
  pageNumLeft: number;
  pageNumRight: number;
}) {
  const t = useTranslations("book");
  return (
    <>
      <section className="academy-page-surface left">
        <div className="academy-page-inner">
          <span className="academy-running-header">{leftHeader || t("title.journal")}</span>
          {left}
          <span className="academy-page-number">{String(pageNumLeft).padStart(3, "0")}</span>
        </div>
      </section>
      <section className="academy-page-surface right">
        <div className="academy-page-inner">
          <span className="academy-running-header">{rightHeader || t("cover.archive")}</span>
          {right}
          <span className="academy-page-number">{String(pageNumRight).padStart(3, "0")}</span>
        </div>
      </section>
    </>
  );
}

function FigureRow({
  children,
  image,
  caption,
  reverse = false
}: {
  children: React.ReactNode;
  image: React.ReactNode;
  caption: React.ReactNode;
  reverse?: boolean;
}) {
  return (
    <div className="figure-row" style={{ flexDirection: reverse ? 'row-reverse' : 'row' }}>
      <figure style={{ margin: 0, flex: '0 0 40%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        {image}
        <figcaption className="codex-serif" style={{ fontSize: '0.8rem', color: 'var(--book-muted)', marginTop: '0.75rem', textAlign: 'center', fontStyle: 'italic', lineHeight: 1.4 }}>
          {caption}
        </figcaption>
      </figure>
      <div className="figure-row-copy">
        {children}
      </div>
    </div>
  );
}

// ─── 1. COVER ─────────────────────────────────────────────────────────────────

export function Cover({
  onOpen,
  theme,
  onThemeChange,
}: {
  onOpen: () => void;
  theme?: "dark" | "light";
  onThemeChange?: (theme: "dark" | "light") => void;
}) {
  const t = useTranslations("book");
  const { currentLocale, switchLocale } = useLocaleSwitch();

  return (
    <div className="academy-cover-wrapper">
      <button onClick={onOpen} className="academy-cover">
        <div className="academy-cover-spine" />
        <p className="academy-cover-archive">{t("cover.archive")}</p>
        <h1
          className="codex-cinzel"
          dangerouslySetInnerHTML={{ __html: t("cover.subtitle1", { defaultValue: "ПРИКЛЮЧЕНИЕ\nХИМИКА" }) }}
          style={{ fontSize: "3rem", lineHeight: 1.1, color: "#f1f5f9", margin: "0.5rem 0 0" }}
        />
        <p className="codex-serif" style={{ fontSize: "1rem", color: "rgba(241,245,249,0.7)", marginTop: "1rem" }}>
          {t("cover.subtitle2", { defaultValue: "Интерактивный путеводитель\nпо экспериментальной химии" })}
        </p>
        <p className="codex-mono" style={{ fontSize: "0.75rem", color: "rgba(241,245,249,0.45)", marginTop: "1.5rem", letterSpacing: "0.3em" }}>
          {t("cover.volume", { defaultValue: "ТОМ I · 2026" })}
        </p>
        <span className="academy-cover-action">{t("cover.openBtn")}</span>
      </button>

      {/* Controls: Language + Theme outside the clickable cover */}
      <div className="cover-controls">
        <div className="cover-controls-group">
          <span className="cover-controls-label">
            {t("cover.langLabel", { defaultValue: "ЯЗЫК" })}
          </span>
          <div className="cover-controls-row">
            {(["ru", "uz", "en"] as const).map((l) => (
              <button
                key={l}
                className={`cover-control-btn${currentLocale === l ? " active" : ""}`}
                onClick={(e) => {
                  e.stopPropagation();
                  switchLocale(l);
                }}
              >
                {l.toUpperCase()}
              </button>
            ))}
          </div>
        </div>
        {onThemeChange && (
          <div className="cover-controls-group">
            <span className="cover-controls-label">
              {t("cover.themeLabel", { defaultValue: "СТРАНИЦЫ" })}
            </span>
            <div className="cover-controls-row">
              <button
                className={`cover-control-btn${theme === "dark" ? " active" : ""}`}
                onClick={(e) => {
                  e.stopPropagation();
                  onThemeChange("dark");
                }}
              >
                ☾
              </button>
              <button
                className={`cover-control-btn${theme === "light" ? " active" : ""}`}
                onClick={(e) => {
                  e.stopPropagation();
                  onThemeChange("light");
                }}
              >
                ☀
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

// ─── 2. OWNER / OPENING SPREAD ────────────────────────────────────────────────

export function OwnerSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const classification =
    lang === "ru"
      ? "ИССЛЕДОВАТЕЛЬСКИЕ ЗАПИСКИ · ТОМ I"
      : lang === "uz"
      ? "TADQIQOT QAYDLARI · I TOM"
      : "RESEARCH NOTES · VOLUME I";

  const ownerLabel =
    lang === "ru" ? "Этот журнал принадлежит:" : lang === "uz" ? "Bu jurnal egasi:" : "This journal belongs to:";

  const labLabel =
    lang === "ru" ? "Лаборатория jasScience" : lang === "uz" ? "jasScience Laboratoriyasi" : "jasScience Laboratory";

  const warningText =
    lang === "ru"
      ? "Обращаться бережно. Содержит незавершённые опыты."
      : lang === "uz"
      ? "Ehtiyotkorlik bilan muomala qiling. Tugallanmagan tajribalar mavjud."
      : "Handle with care. Contains unfinished experiments.";

  const startedText =
    lang === "ru" ? "Начало: август 2026" : lang === "uz" ? "Boshlangan: 2026 yil avgust" : "Started: August 2026";

  return (
    <Spread
      pageNumLeft={1}
      pageNumRight={2}
      leftHeader="JASSCIENCE · FIELD JOURNAL"
      rightHeader="JASSCIENCE · FIELD JOURNAL"
      left={
        <div className="page-layout-balanced owner-cover-page" style={{ alignItems: "flex-start", paddingTop: "3rem" }}>
          {/* Decorative cover art fragment */}
          <Img
            src="/journal/chemistry/page-001-notebook.png"
            height={220}
            className="textured-image"
            alt="Chemistry notebook"
            width="80%"
          />
          <div style={{ marginTop: "2rem" }}>
            <span className="codex-mono" style={{ fontSize: "0.7rem", letterSpacing: "0.3em", opacity: 0.4, textTransform: "uppercase", display: "block", marginBottom: "0.5rem" }}>
              {classification}
            </span>
            <div
              className="codex-handwriting"
              style={{ fontSize: "1rem", opacity: 0.5, transform: "rotate(-1deg)", marginTop: "0.5rem" }}
            >
              {startedText}
            </div>
          </div>

          {/* Researcher fields */}
          <div style={{ marginTop: "1.5rem", borderTop: "1px solid var(--book-border)", paddingTop: "1rem", width: "100%" }}>
            {([
              { label: lang === "ru" ? "ИССЛЕДОВАТЕЛЬ" : lang === "uz" ? "TADQIQOTCHI" : "RESEARCHER", field: "____________________" },
              { label: lang === "ru" ? "ГРУППА / КЛАСС" : lang === "uz" ? "GURUH / SINF" : "GROUP / CLASS", field: "____________________" },
              { label: lang === "ru" ? "ЛАБОРАТОРИЯ" : lang === "uz" ? "LABORATORIYA" : "LABORATORY", field: "Chemistry / Sandbox" },
            ] as {label:string;field:string}[]).map((row, i) => (
              <div key={i} style={{ marginBottom: "0.8rem" }}>
                <span className="codex-mono" style={{ fontSize: "0.6rem", letterSpacing: "0.2em", opacity: 0.4, display: "block", textTransform: "uppercase" }}>{row.label}</span>
                <span className="codex-handwriting" style={{ fontSize: "1rem", opacity: 0.5 }}>{row.field}</span>
              </div>
            ))}
          </div>

          {/* Handwritten note */}
          <div
            className="codex-handwriting"
            style={{ marginTop: "1rem", fontSize: "0.9rem", color: "var(--book-pencil)", transform: "rotate(-2deg)", opacity: 0.55, lineHeight: 1.5 }}
          >
            {lang === "ru"
              ? "«Не верить результату,\nпока не повторил опыт.»"
              : lang === "uz"
              ? "«Tajribani takrorlamaguncha\nnatijaga ishonma.»"
              : "«Do not trust the result\nuntil you repeat the experiment.»"}
          </div>
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "4rem" }}>
          {/* Owner page — classic book ownership plate */}
          <div
            style={{
              border: "1px solid var(--book-border)",
              padding: "2.5rem 2rem",
              position: "relative",
            }}
          >
            {/* Decorative corner marks */}
            <span style={{ position: "absolute", top: 8, left: 8, width: 16, height: 16, borderTop: "2px solid var(--book-border)", borderLeft: "2px solid var(--book-border)" }} />
            <span style={{ position: "absolute", top: 8, right: 8, width: 16, height: 16, borderTop: "2px solid var(--book-border)", borderRight: "2px solid var(--book-border)" }} />
            <span style={{ position: "absolute", bottom: 8, left: 8, width: 16, height: 16, borderBottom: "2px solid var(--book-border)", borderLeft: "2px solid var(--book-border)" }} />
            <span style={{ position: "absolute", bottom: 8, right: 8, width: 16, height: 16, borderBottom: "2px solid var(--book-border)", borderRight: "2px solid var(--book-border)" }} />

            <p className="codex-mono" style={{ fontSize: "0.75rem", letterSpacing: "0.2em", opacity: 0.5, textTransform: "uppercase", marginBottom: "1.5rem" }}>
              ПОЛЕВОЙ ЖУРНАЛ / FIELD JOURNAL
            </p>

            <p className="codex-serif" style={{ fontSize: "1rem", opacity: 0.6, marginBottom: "0.5rem" }}>
              {ownerLabel}
            </p>
            <div
              className="codex-handwriting"
              style={{ fontSize: "1.6rem", borderBottom: "1px solid var(--book-border)", paddingBottom: "1rem", marginBottom: "1.5rem" }}
            >
              ______________________
            </div>

            <p className="codex-serif" style={{ fontSize: "0.9rem", opacity: 0.5, marginBottom: "0.3rem" }}>
              {labLabel}
            </p>
            <p className="codex-mono" style={{ fontSize: "0.8rem", opacity: 0.4, letterSpacing: "0.15em" }}>
              2026
            </p>
          </div>

          <p
            className="codex-mono"
            style={{ fontSize: "0.7rem", letterSpacing: "0.15em", opacity: 0.35, marginTop: "1.5rem", textAlign: "center", textTransform: "uppercase" }}
          >
            {warningText}
          </p>

          {/* If found note */}
          <p className="codex-serif" style={{ fontSize: "0.85rem", opacity: 0.4, marginTop: "0.8rem", fontStyle: "italic", textAlign: "center" }}>
            {lang === "ru" ? "Если найдено — вернуть в лабораторию." : lang === "uz" ? "Topilsa — laboratoriyaga qaytaring." : "If found — return to the laboratory."}
          </p>

          {/* JAS SCIENCE RESEARCH ARCHIVE stamp */}
          <svg width="130" height="64" viewBox="0 0 130 64" style={{ margin: "1.5rem auto 0", display: "block", opacity: 0.12 }}>
            <rect x="2" y="2" width="126" height="60" rx="2" fill="none" stroke="var(--book-text)" strokeWidth="2"/>
            <rect x="6" y="6" width="118" height="52" rx="1" fill="none" stroke="var(--book-text)" strokeWidth="0.5"/>
            <text x="65" y="22" textAnchor="middle" fontFamily="monospace" fontSize="8" fill="var(--book-text)" letterSpacing="2">JAS SCIENCE</text>
            <text x="65" y="35" textAnchor="middle" fontFamily="monospace" fontSize="6" fill="var(--book-text)" letterSpacing="1">RESEARCH ARCHIVE</text>
            <text x="65" y="47" textAnchor="middle" fontFamily="monospace" fontSize="5" fill="var(--book-text)" letterSpacing="1">VOL. I · 2026</text>
          </svg>

          <MarginNote position="right" color="red" rotate={3}>
            {lang === "ru" ? "начать читать →" : lang === "uz" ? "o'qishni boshlash →" : "start reading →"}
          </MarginNote>
        </div>
      }
    />
  );
}

// ─── 3. TABLE OF CONTENTS ─────────────────────────────────────────────────────

type ContentsEntry = {
  chapterGroup?: string;
  title: string;
  page: string;
  spreadIndex: number;
};

export function ContentsSpreadA({ navigate }: { navigate: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const chapterI_label = lang === "ru" ? "ГЛАВА I. ХИМИЯ" : lang === "uz" ? "I BOB. KIMYO" : "CHAPTER I. CHEMISTRY";
  const chapterII_label =
    lang === "ru"
      ? "ГЛАВА II. ИСТОРИЯ И УЧЁНЫЕ"
      : lang === "uz"
      ? "II BOB. TARIX VA OLIMLAR"
      : "CHAPTER II. HISTORY & SCIENTISTS";
  const chapterIII_label =
    lang === "ru"
      ? "ГЛАВА III. ВИРТУАЛЬНАЯ ЛАБОРАТОРИЯ"
      : lang === "uz"
      ? "III BOB. VIRTUAL LABORATORIYA"
      : "CHAPTER III. VIRTUAL LABORATORY";

  const entries: ContentsEntry[] = [
    { chapterGroup: chapterI_label, title: t("contents.ch1"), page: "007", spreadIndex: 4 },
    { title: lang === "ru" ? "Что изучает химия" : lang === "uz" ? "Kimyo nima o'rganadi" : "What Chemistry Studies", page: "009", spreadIndex: 5 },
    { title: lang === "ru" ? "Вещества и явления" : lang === "uz" ? "Moddalar va hodisalar" : "Substances & Phenomena", page: "011", spreadIndex: 6 },
    { chapterGroup: chapterII_label, title: lang === "ru" ? "История химии" : lang === "uz" ? "Kimyo tarixi" : "History of Chemistry", page: "013", spreadIndex: 7 },
    { title: t("contents.ch2"), page: "015", spreadIndex: 8 },
    { title: t("contents.ch3"), page: "017", spreadIndex: 9 },
    { title: t("contents.ch4"), page: "019", spreadIndex: 10 },
    { title: t("contents.ch5"), page: "021", spreadIndex: 11 },
    { title: lang === "ru" ? "Джабир ибн Хайян и аль-Рази" : lang === "uz" ? "Jobir ibn Hayyon va al-Roziy" : "Jabir ibn Hayyan & al-Razi", page: "023", spreadIndex: 12 },
    { chapterGroup: chapterIII_label, title: t("contents.ch6"), page: "027", spreadIndex: 13 },
    { title: lang === "ru" ? "Интерфейс лаборатории" : lang === "uz" ? "Laboratoriya interfeysi" : "Lab Interface", page: "029", spreadIndex: 14 },
    { title: t("contents.ch7"), page: "031", spreadIndex: 15 },
  ];

  return (
    <Spread
      pageNumLeft={3}
      pageNumRight={4}
      leftHeader={t("contents.header")}
      rightHeader={t("contents.header")}
      left={
        <div className="page-layout-dense">
          <h2 className="codex-cinzel" style={{ fontSize: "1.8rem", borderBottom: "1px solid var(--book-border)", paddingBottom: "0.75rem", marginBottom: "0.5rem" }}>
            {t("contents.header")}
          </h2>
          <p className="codex-serif" style={{ fontSize: "0.9rem", opacity: 0.6, marginBottom: "1.5rem", fontStyle: "italic" }}>
            {t("contents.p1")}
          </p>
          <div className="editorial-index">
            {entries.slice(0, 6).map((entry, i) => (
              <div key={i}>
                {entry.chapterGroup && (
                  <p className="codex-mono index-chapter-label" style={{ marginTop: i > 0 ? "1rem" : 0 }}>
                    {entry.chapterGroup}
                  </p>
                )}
                <button className="editorial-index-row" onClick={() => navigate(entry.spreadIndex)}>
                  <span className="index-title codex-serif">{entry.title}</span>
                  <span className="index-dots" />
                  <span className="index-page codex-mono">{entry.page}</span>
                </button>
              </div>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-dense">
          <div style={{ paddingTop: "2.5rem" }}>
            <div className="editorial-index">
              {entries.slice(6).map((entry, i) => (
                <div key={i}>
                  {entry.chapterGroup && (
                    <p className="codex-mono index-chapter-label" style={{ marginTop: i > 0 ? "1rem" : 0 }}>
                      {entry.chapterGroup}
                    </p>
                  )}
                  <button className="editorial-index-row" onClick={() => navigate(entry.spreadIndex)}>
                    <span className="index-title codex-serif">{entry.title}</span>
                    <span className="index-dots" />
                    <span className="index-page codex-mono">{entry.page}</span>
                  </button>
                </div>
              ))}
            </div>

            {/* Reading order note */}
            <div
              className="codex-handwriting"
              style={{ marginTop: "2.5rem", fontSize: "1rem", color: "var(--book-pencil)", transform: "rotate(-3deg)", opacity: 0.65, lineHeight: 1.5 }}
            >
              {lang === "ru"
                ? "не обязательно читать\nпо порядку →"
                : lang === "uz"
                ? "tartib bo'yicha o'qish shart emas →"
                : "no need to read\nin order →"}
            </div>

            {/* Decorative H₂O structure */}
            <svg width="120" height="80" viewBox="0 0 120 80" style={{ marginTop: "2rem", opacity: 0.2, display: "block" }}>
              <circle cx="60" cy="40" r="14" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <text x="60" y="45" textAnchor="middle" fontFamily="monospace" fontSize="10" fill="var(--book-text)">O</text>
              <circle cx="20" cy="20" r="10" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <text x="20" y="25" textAnchor="middle" fontFamily="monospace" fontSize="9" fill="var(--book-text)">H</text>
              <circle cx="100" cy="20" r="10" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <text x="100" y="25" textAnchor="middle" fontFamily="monospace" fontSize="9" fill="var(--book-text)">H</text>
              <line x1="46" y1="29" x2="28" y2="26" stroke="var(--book-text)" strokeWidth="1.2" />
              <line x1="74" y1="29" x2="92" y2="26" stroke="var(--book-text)" strokeWidth="1.2" />
            </svg>
          </div>
        </div>
      }
    />
  );
}

export function ContentsSpreadB({ navigate }: { navigate: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const chapterIV_label =
    lang === "ru"
      ? "ГЛАВА IV. ЭКСПЕРИМЕНТ"
      : lang === "uz"
      ? "IV BOB. TAJRIBA"
      : "CHAPTER IV. EXPERIMENT";

  const entries: ContentsEntry[] = [
    { chapterGroup: lang === "ru" ? "— КАТАЛОГ —" : lang === "uz" ? "— KATALOG —" : "— CATALOGUE —", title: t("contents.ch9"), page: "055", spreadIndex: 27 },
    { chapterGroup: chapterIV_label, title: t("contents.ch10"), page: "093", spreadIndex: 46 },
    { title: lang === "ru" ? "Практика 01 — Нагрев" : lang === "uz" ? "Amaliyot 01 — Isitish" : "Practice 01 — Heating", page: "105", spreadIndex: 52 },
    { title: lang === "ru" ? "Практика 02 — Оборудование" : lang === "uz" ? "Amaliyot 02 — Jihozlar" : "Practice 02 — Equipment", page: "107", spreadIndex: 53 },
    { title: lang === "ru" ? "Практика 03 — Вещества" : lang === "uz" ? "Amaliyot 03 — Moddalar" : "Practice 03 — Substances", page: "109", spreadIndex: 54 },
    { title: lang === "ru" ? "Практика 04 — Порядок сборки" : lang === "uz" ? "Amaliyot 04 — Yig'ish tartibi" : "Practice 04 — Assembly Order", page: "111", spreadIndex: 55 },
  ];

  return (
    <Spread
      pageNumLeft={5}
      pageNumRight={6}
      leftHeader={t("contents.header")}
      rightHeader={t("contents.header")}
      left={
        <div className="page-layout-dense" style={{ paddingTop: "2rem" }}>
          <div className="editorial-index">
            {entries.map((entry, i) => (
              <div key={i}>
                {entry.chapterGroup && (
                  <p className="codex-mono index-chapter-label" style={{ marginTop: i > 0 ? "1.2rem" : 0 }}>
                    {entry.chapterGroup}
                  </p>
                )}
                <button className="editorial-index-row" onClick={() => navigate(entry.spreadIndex)}>
                  <span className="index-title codex-serif">{entry.title}</span>
                  <span className="index-dots" />
                  <span className="index-page codex-mono">{entry.page}</span>
                </button>
              </div>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-balanced periodic-reference-layout" style={{ paddingTop: "3rem", alignItems: "center" }}>
          {/* Decorative periodic table fragment */}
          <Img
            src="/journal/chemistry/periodic-system.svg"
            height={200}
            className="textured-image"
            alt="Periodic system fragment"
            width="90%"
          />
          <div
            className="codex-handwriting"
            style={{ marginTop: "1.5rem", fontSize: "1.1rem", opacity: 0.5, transform: "rotate(-2deg)" }}
          >
            {lang === "ru" ? "важно повторить →" : lang === "uz" ? "muhim, takrorlash →" : "important to review →"}
          </div>
        </div>
      }
    />
  );
}

// ─── 4. INTRODUCTION — HANDWRITTEN JOURNAL ────────────────────────────────────

export function IntroductionSpread1() {
  const lang = usePathname().split("/")[1] || "en";

  const date = lang === "ru" ? "27 августа" : lang === "uz" ? "27 avgust" : "August 27";
  const expId = lang === "ru" ? "опыт №001" : lang === "uz" ? "tajriba №001" : "experiment #001";

  const strike = lang === "ru" ? "Химия — это магия" : lang === "uz" ? "Kimyo — bu sehrdir" : "Chemistry is magic";
  const correction = lang === "ru" ? "слишком громко" : lang === "uz" ? "juda baland" : "too loud";

  const p1 =
    lang === "ru"
      ? "Дорогой дневник — нет. Кодекс. Эта книга появилась не для того, чтобы хранить правильные ответы. Её задача скромнее и полезнее: оставить место для вопроса, который ещё не успел стать правилом."
      : lang === "uz"
      ? "Aziz kundalik — yo'q. Kodeks. Ushbu kitob to'g'ri javoblarni saqlash uchun paydo bo'lmagan. Uning vazifasi kamtarroq va foydali: hali qoidaga aylanmagan savol uchun joy qoldirish."
      : "Dear diary — no. Codex. This book did not appear to store correct answers. Its purpose is more modest and useful: to leave room for a question that has not yet become a rule.";

  const p2 =
    lang === "ru"
      ? "Химия начинается в тот момент, когда мы замечаем изменение. Жидкость нагревается не так быстро, как ожидалось; осадок появляется раньше; стекло отвечает на пламя собственной температурой."
      : lang === "uz"
      ? "Kimyo biz o'zgarishni sezgan paytdan boshlanadi. Suyuqlik kutilgancha tez qizimadi; cho'kma ertaroq paydo bo'ldi; shisha alangaga o'z harorati bilan javob berdi."
      : "Chemistry begins the moment we notice a change. The liquid heats up not as quickly as expected; the precipitate appears earlier; the glass responds to the flame with its own temperature.";

  const annotation =
    lang === "ru" ? "проверить позже →" : lang === "uz" ? "keyinroq tekshirish →" : "check later →";

  return (
    <Spread
      pageNumLeft={7}
      pageNumRight={8}
      leftHeader={lang === "ru" ? "ВСТУПЛЕНИЕ" : lang === "uz" ? "KIRISH" : "INTRODUCTION"}
      rightHeader={lang === "ru" ? "ВСТУПЛЕНИЕ" : lang === "uz" ? "KIRISH" : "INTRODUCTION"}
      left={
        <div className="page-layout-journal" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <JournalDate date={date} expId={expId} temp="t = 23°C" />

          <div style={{ marginTop: "1.5rem", position: "relative" }}>
            {/* Strikethrough "Dear diary" */}
            <p className="codex-handwriting" style={{ fontSize: "1.25rem", lineHeight: 1.5 }}>
              <HandwrittenStrike>{lang === "ru" ? "Дорогой дневник" : lang === "uz" ? "Aziz kundalik" : "Dear diary"}</HandwrittenStrike>
            </p>
            <p style={{ marginTop: "0.2rem" }}>
              <RedCorrection rotate={-3}>{lang === "ru" ? "нет. книга." : lang === "uz" ? "yo'q. kitob." : "no. codex."}</RedCorrection>
            </p>
            <div className="codex-handwriting margin-note-right margin-note-red" style={{ fontSize: "0.9rem", top: "0.3rem" }}>
              {annotation}
            </div>
          </div>

          <div style={{ marginTop: "1.5rem" }}>
            <p className="codex-serif" style={{ fontSize: "1rem", lineHeight: 1.75 }}>
              {lang === "ru"
                ? "Эта книга появилась не для того, чтобы хранить правильные ответы. Она нужна для вопросов, которые ещё не успели стать правилами. В лаборатории редко всё происходит точно так, как ожидаешь: жидкость нагревается медленнее, цвет появляется раньше, капля оказывается там, где её никто не ждал."
                : lang === "uz"
                ? "Bu kitob to'g'ri javoblarni saqlash uchun paydo bo'lmagan. U hali qoidaga aylanmagan savollar uchun kerak. Laboratoriyada hamma narsa kutgandek bo'lavermaydi: suyuqlik sekinroq qiziydi, rang ertaroq paydo bo'ladi, tomchi kutilmagan joyda."
                : "This book did not appear to store correct answers. It exists for questions that have not yet become rules. In the laboratory, things rarely happen exactly as expected: the liquid heats up more slowly, the colour appears earlier, the drop ends up where no one expected it."}
            </p>
          </div>

          <div style={{ marginTop: "1.2rem" }}>
            <p className="codex-serif" style={{ fontSize: "1rem", lineHeight: 1.75 }}>
              {lang === "ru"
                ? "Именно поэтому эксперимент начинается не с формулы, а с наблюдения."
                : lang === "uz"
                ? "Aynan shuning uchun tajriba formuladan emas, balki kuzatishdan boshlanadi."
                : "That is why an experiment begins not with a formula, but with observation."}
            </p>
          </div>

          {/* Experiment note fields */}
          <div style={{ marginTop: "1.8rem", borderTop: "1px solid var(--book-border)", paddingTop: "1rem" }}>
            {[
              { label: lang === "ru" ? "ОПЫТ" : lang === "uz" ? "TAJRIBA" : "EXPERIMENT", value: "№001" },
              { label: "T", value: "= 23°C" },
              { label: lang === "ru" ? "ДАТА" : lang === "uz" ? "SANA" : "DATE", value: "____________" },
              { label: lang === "ru" ? "НАБЛЮДЕНИЕ" : lang === "uz" ? "KUZATISH" : "OBSERVATION", value: "____________" },
            ].map((row, i) => (
              <div key={i} style={{ display: "flex", gap: "0.5rem", marginBottom: "0.4rem", alignItems: "baseline" }}>
                <span className="codex-mono" style={{ fontSize: "0.65rem", opacity: 0.4, letterSpacing: "0.12em", textTransform: "uppercase", flexShrink: 0, minWidth: "90px" }}>{row.label}</span>
                <span className="codex-handwriting" style={{ fontSize: "0.95rem", opacity: 0.55 }}>{row.value}</span>
              </div>
            ))}
          </div>

          {/* Small flask sketch */}
          <svg width="50" height="70" viewBox="0 0 50 70" style={{ position: "absolute", bottom: "2rem", right: "1.5rem", opacity: 0.15 }}>
            <rect x="20" y="5" width="10" height="20" rx="2" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
            <path d="M14,25 L8,60 Q8,65 25,65 Q42,65 42,60 L36,25 Z" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
            <ellipse cx="25" cy="50" rx="10" ry="5" fill="none" stroke="var(--book-text)" strokeWidth="1" strokeDasharray="2,2" />
          </svg>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "2rem", position: "relative" }}>
          <p className="codex-serif" style={{ fontSize: "1rem", lineHeight: 1.75 }}>
            {p2}
          </p>

          <p className="codex-serif" style={{ fontSize: "1rem", lineHeight: 1.75, marginTop: "1rem" }}>
            {lang === "ru"
              ? "Это не ошибка и не случайность. Это информация. Каждое отклонение от ожидаемого — это сигнал системы: «посмотри внимательнее». Именно так работает наблюдение в лаборатории."
              : lang === "uz"
              ? "Bu xato ham, tasodif ham emas. Bu ma'lumot. Kutilgandan har bir og'ish tizimning signali: «yanada diqqat bilan qara». Laboratoriyada kuzatish aynan shunday ishlaydi."
              : "This is not an error or a coincidence. It is information. Every deviation from the expected is a signal from the system: «look more carefully». That is how observation works in the laboratory."}
          </p>

          <Img
            src="/journal/chemistry/intro-heating-transparent.png"
            height={190}
            className="textured-image"
            alt="Heating experiment illustration"
            width="88%"
            style={{ margin: "1.5rem auto 0", display: "block" } as React.CSSProperties}
          />
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 0.2 — Нагревание воды как первый пример наблюдаемой системы."
              : lang === "uz"
              ? "Rasm 0.2 — Kuzatiladigan tizimning birinchi misoli sifatida suvni qizdirish."
              : "Fig. 0.2 — Heating water as a first example of an observable system."}
          </FigureCaption>
          <div style={{ marginTop: "2rem", borderTop: "1px solid var(--book-border)", paddingTop: "1rem" }}>
            <p className="codex-mono" style={{ fontSize: "0.62rem", letterSpacing: "0.18em", opacity: 0.45 }}>
              {lang === "ru" ? "ПЕРЕД ЗАКЛЮЧЕНИЕМ" : lang === "uz" ? "XULOSADAN OLDIN" : "BEFORE THE CONCLUSION"}
            </p>
            <p className="codex-serif" style={{ margin: "0.5rem 0 0", lineHeight: 1.6 }}>
              {lang === "ru" ? "Завершите последний опыт: повторите наблюдение, сравните результат с гипотезой и оставьте одну новую записку для следующего исследователя." : lang === "uz" ? "So'nggi tajribani yakunlang: kuzatuvni takrorlang, natijani gipoteza bilan solishtiring va keyingi tadqiqotchi uchun yangi qayd qoldiring." : "Complete the final experiment: repeat the observation, compare the result with the hypothesis, and leave one new note for the next researcher."}
            </p>
          </div>

          <div
            className="codex-handwriting"
            style={{ marginTop: "1.5rem", fontSize: "1.1rem", color: "var(--book-pencil)", transform: "rotate(-2deg)", opacity: 0.75, lineHeight: 1.6 }}
          >
            {lang === "ru"
              ? "сначала смотри.\nпотом объясняй."
              : lang === "uz"
              ? "avval kuzat.\nkeyin tushuntir."
              : "first observe.\nthen explain."}
          </div>
        </div>
      }
    />
  );
}

export function IntroductionSpread2() {
  const lang = usePathname().split("/")[1] || "en";

  const p4 =
    lang === "ru"
      ? "Начиная с этой страницы, я пытаюсь записывать всё, что происходит в лаборатории. Не для красоты — для точности. Хороший опыт начинается до того, как зажжена горелка."
      : lang === "uz"
      ? "Ushbu sahifadan boshlab, laboratoriyada sodir bo'layotgan hamma narsani yozishga harakat qilaman. Go'zallik uchun emas — aniqlik uchun. Yaxshi tajriba gorelka yoqilgunga qadar boshlanadi."
      : "Starting from this page, I try to record everything that happens in the laboratory. Not for beauty — for accuracy. A good experiment begins before the burner is lit.";

  const p5 =
    lang === "ru"
      ? "Один из первых уроков лаборатории: разница между тем, что ты ожидал увидеть, и тем, что произошло на самом деле — это и есть точка начала науки."
      : lang === "uz"
      ? "Laboratoriyaning birinchi saboqlaridan biri: kutganingiz va haqiqatda sodir bo'lgan narsa o'rtasidagi farq — bu fanining boshlanish nuqtasidir."
      : "One of the first lessons of the laboratory: the difference between what you expected to see and what actually happened — that is the starting point of science.";

  const note =
    lang === "ru"
      ? "Ни один опыт не начинается с кнопки. Он начинается с внимания."
      : lang === "uz"
      ? "Hech bir tajriba tugma bilan boshlanmaydi. U diqqat bilan boshlanadi."
      : "No experiment begins with a button. It begins with attention.";

  return (
    <Spread
      pageNumLeft={9}
      pageNumRight={10}
      leftHeader={lang === "ru" ? "ВСТУПЛЕНИЕ" : lang === "uz" ? "KIRISH" : "INTRODUCTION"}
      rightHeader={lang === "ru" ? "ВСТУПЛЕНИЕ" : lang === "uz" ? "KIRISH" : "INTRODUCTION"}
      left={
        <div className="page-layout-journal" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <p className="codex-handwriting" style={{ fontSize: "1.15rem", lineHeight: 1.75, marginBottom: "1.5rem" }}>
            {p4}
          </p>
          <p className="codex-handwriting" style={{ fontSize: "1.15rem", lineHeight: 1.75 }}>
            {p5}
          </p>

          {/* Red underline emphasis */}
          <div style={{ marginTop: "2rem", borderBottom: "2px solid var(--book-annotation)", display: "inline-block" }}>
            <p className="codex-handwriting" style={{ fontSize: "1.1rem", color: "var(--book-annotation)" }}>
              {lang === "ru" ? "повторить" : lang === "uz" ? "takrorlash" : "repeat"}
            </p>
          </div>

          {/* RULE №1 */}
          <div style={{ marginTop: "1.5rem", padding: "0.6rem 0.9rem", borderLeft: "3px solid var(--book-accent-red)", background: "rgba(159,71,71,0.06)" }}>
            <p className="codex-mono" style={{ fontSize: "0.62rem", letterSpacing: "0.2em", textTransform: "uppercase", opacity: 0.5, marginBottom: "0.3rem" }}>
              {lang === "ru" ? "ПРАВИЛО №1" : lang === "uz" ? "QOIDA №1" : "RULE №1"}
            </p>
            <p className="codex-serif" style={{ fontSize: "0.92rem", lineHeight: 1.55, margin: 0 }}>
              {lang === "ru"
                ? "Не записывать то, что должно было произойти. Записывать то, что произошло."
                : lang === "uz"
                ? "Bo'lishi kerak bo'lgan narsani emas. Sodir bo'lgan narsani yoz."
                : "Do not write what should have happened. Write what actually happened."}
            </p>
          </div>

          {/* Circled word */}
          <div
            style={{
              position: "absolute",
              bottom: "5rem",
              right: "2rem",
              border: "1.5px solid var(--book-annotation)",
              borderRadius: "50%",
              padding: "0.4rem 0.8rem",
              transform: "rotate(-6deg)",
            }}
          >
            <span className="codex-handwriting" style={{ fontSize: "1rem", color: "var(--book-annotation)" }}>
              {lang === "ru" ? "важно" : lang === "uz" ? "muhim" : "important"}
            </span>
          </div>
        </div>
      }
      right={
        <div className="page-layout-balanced atomic-layout" style={{ paddingTop: "1.5rem" }}>
          <TextbookBlockquote>{note}</TextbookBlockquote>

          <Img
            src="/journal/chemistry/atomic-structure-transparent.png"
            height={290}
            className="textured-image"
            alt="Electron orbitals diagram from Wikimedia Commons"
            width="90%"
            style={{ margin: "1.5rem auto 0" } as React.CSSProperties}
          />
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 0.3 — Строение атома: электроны на орбиталях."
              : lang === "uz"
              ? "Rasm 0.3 — Atom tuzilishi: elektronylar orbitalarda."
              : "Fig. 0.3 — Atomic structure: electrons in orbitals."}
          </FigureCaption>
          <p className="codex-serif atomic-structure-note">
            {lang === "ru"
              ? "Ядро удерживает почти всю массу атома, а электроны образуют движущееся электронное облако. Именно его строение определяет химические свойства элемента."
              : lang === "uz"
              ? "Yadro atom massasining deyarli barchasini saqlaydi, elektronlar esa harakatlanuvchi elektron bulutini hosil qiladi. Elementning kimyoviy xossalarini aynan shu tuzilish belgilaydi."
              : "The nucleus contains almost all of an atom’s mass, while electrons form a moving electron cloud. Its structure determines the element’s chemical properties."}
          </p>
          <div className="atomic-reading-note">
            <p className="codex-mono">НАБЛЮДЕНИЕ / 0.3</p>
            <p className="codex-serif">
              {lang === "ru"
                ? "Атомная модель полезна не как готовая картинка, а как способ задавать вопросы. Почему одни атомы легко образуют связи, а другие почти не реагируют? Ответ ищут в расположении электронов и в том, как система меняет энергию при встрече с другими частицами."
                : lang === "uz"
                ? "Atom modeli tayyor rasm emas, savol berish usulidir. Nega ayrim atomlar bog‘larni oson hosil qiladi, boshqalari esa deyarli reaksiyaga kirmaydi? Javob elektronlarning joylashuvi va tizim boshqa zarrachalar bilan uchrashganda energiyani qanday o‘zgartirishida izlanadi."
                : "An atomic model is useful not as a finished picture, but as a way to ask questions. Why do some atoms form bonds readily while others barely react? The answer lies in electron arrangement and in how a system changes energy when it meets other particles."}
            </p>
          </div>
        </div>
      }
    />
  );
}

// ─── 5. CHEMISTRY CHAPTER ─────────────────────────────────────────────────────

export function ChemistrySpread1() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const chTitle = lang === "ru" ? "Химия" : lang === "uz" ? "Kimyo" : "Chemistry";
  const chSub =
    lang === "ru"
      ? "Наука о веществах и их превращениях"
      : lang === "uz"
      ? "Moddalar va ularning o'zgarishlari haqidagi fan"
      : "The science of substances and their transformations";
  const epigraph =
    lang === "ru"
      ? "«Вещество не исчезает: оно меняет способ быть видимым.»"
      : lang === "uz"
      ? "«Modda yo'qolmaydi: u ko'rinadigan bo'lish usulini o'zgartiradi.»"
      : '"Matter does not disappear: it changes the way of being visible."';

  const p1 =
    lang === "ru"
      ? t("chemistry.p1")
      : lang === "uz"
      ? "Kimyo — moddalar va ularning o'zgarishlari haqidagi fan. U moddaning tarkibini, tuzilishini va xossalarini, shuningdek, ba'zi moddalarning boshqalariga aylanish shartlarini o'rganadi."
      : "Chemistry is the science of substances and their transformations. It studies the composition, structure and properties of matter, as well as the conditions under which some substances transform into others.";

  const box1Title = lang === "ru" ? "ОСНОВНЫЕ ПОНЯТИЯ" : lang === "uz" ? "ASOSIY TUSHUNCHALAR" : "KEY CONCEPTS";
  const box2Title = lang === "ru" ? "ЗАПОМНИ" : lang === "uz" ? "ESLAB QOL" : "REMEMBER";

  const box1Items =
    lang === "ru"
      ? [
          "Химия — изучение состава и превращений вещества",
          "Вещество — то, из чего состоят все тела",
          "Явление — любое изменение, происходящее с веществом",
          "Химическая реакция — процесс превращения одних веществ в другие",
        ]
      : lang === "uz"
      ? [
          "Kimyo — modda tarkibi va o'zgarishlarini o'rganish",
          "Modda — barcha jismlarning tarkibiy qismi",
          "Hodisa — moddada sodir bo'ladigan har qanday o'zgarish",
          "Kimyoviy reaktsiya — bir moddaning boshqasiga aylanish jarayoni",
        ]
      : [
          "Chemistry — study of composition and transformations of matter",
          "Substance — what all bodies consist of",
          "Phenomenon — any change occurring with a substance",
          "Chemical reaction — process of transforming some substances into others",
        ];

  const box2Content =
    lang === "ru"
      ? "Химия изучает только те изменения, при которых образуются новые вещества. Физические изменения (плавление льда, испарение воды) химическими не считаются."
      : lang === "uz"
      ? "Kimyo faqat yangi moddalar hosil bo'ladigan o'zgarishlarni o'rganadi. Fizik o'zgarishlar (muz erishi, suv bug'lanishi) kimyoviy hisoblanmaydi."
      : "Chemistry studies only those changes in which new substances are formed. Physical changes (melting ice, water evaporation) are not considered chemical.";

  return (
    <Spread
      pageNumLeft={11}
      pageNumRight={12}
      leftHeader={lang === "ru" ? "ГЛАВА I · ХИМИЯ" : lang === "uz" ? "I BOB · KIMYO" : "CHAPTER I · CHEMISTRY"}
      rightHeader={lang === "ru" ? "ГЛАВА I · ХИМИЯ" : lang === "uz" ? "I BOB · KIMYO" : "CHAPTER I · CHEMISTRY"}
      left={
        <div className="page-layout-textbook chemistry-opening-page" style={{ paddingTop: "0.45rem" }}>
          <ChapterHeader roman="I" title={chTitle} subtitle={chSub} epigraph={epigraph} />

          {/* Decorative formula banner */}
          <div className="chemistry-formula-note">
            <span className="codex-handwriting">H₂ + O₂ → H₂O</span>
            <small>{lang === "ru" ? "из двух веществ рождается новое" : lang === "uz" ? "ikki moddadan yangi modda tug'iladi" : "two substances become something new"}</small>
          </div>

          {/* Journal-style glossary — not a web card */}
          <div style={{ marginTop: "0.8rem", paddingLeft: "1rem", borderLeft: "2px solid var(--book-border)" }}>
            <p className="codex-mono" style={{ fontSize: "0.62rem", letterSpacing: "0.2em", textTransform: "uppercase", opacity: 0.4, marginBottom: "0.5rem" }}>
              {box1Title}
            </p>
            {(lang === "ru"
              ? [
                  { term: "Химия", def: "наука о веществах, их свойствах и превращениях" },
                  { term: "Вещество", def: "форма материи с определённым составом и свойствами" },
                  { term: "Реакция", def: "процесс превращения одних веществ в другие" },
                ]
              : lang === "uz"
              ? [
                  { term: "Kimyo", def: "moddalar, ularning xossalari va o'zgarishlari haqidagi fan" },
                  { term: "Modda", def: "ma'lum tarkib va xossalarga ega materiyaning shakli" },
                  { term: "Reaktsiya", def: "ba'zi moddalarni boshqalarga aylantirish jarayoni" },
                ]
              : [
                  { term: "Chemistry", def: "science of substances, their properties and transformations" },
                  { term: "Substance", def: "form of matter with definite composition and properties" },
                  { term: "Reaction", def: "process of converting some substances into others" },
                ]
            ).map((item, i) => (
              <div key={i} style={{ marginBottom: "0.55rem" }}>
                <span className="codex-serif" style={{ fontSize: "0.95rem", fontWeight: 600 }}>{item.term}</span>
                <span className="codex-serif" style={{ fontSize: "0.9rem", opacity: 0.7 }}> — {item.def}</span>
              </div>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-textbook chemistry-content-page" style={{ paddingTop: "0.75rem" }}>
          <SectionHeading
            number="1.1"
            title={
              lang === "ru"
                ? "Что изучает химия"
                : lang === "uz"
                ? "Kimyo nima o'rganadi"
                : "What Chemistry Studies"
            }
          />
          <p className="codex-serif drop-cap">{p1}</p>

          <InfoBox type="green" title={box1Title}>
            <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
              {box1Items.map((item, i) => (
                <li key={i} className="codex-serif" style={{ padding: "0.3rem 0", borderBottom: "1px dotted var(--book-border)", fontSize: "0.9rem" }}>
                  {item}
                </li>
              ))}
            </ul>
          </InfoBox>

          <InfoBox type="blue" title={box2Title}>
            <p className="codex-serif" style={{ margin: 0, fontSize: "0.9rem" }}>
              {box2Content}
            </p>
          </InfoBox>
        </div>
      }
    />
  );
}

export function ChemistrySpread2() {
  const lang = usePathname().split("/")[1] || "en";

  const section2 = lang === "ru" ? "1.2 Вещества и явления" : lang === "uz" ? "1.2 Moddalar va hodisalar" : "1.2 Substances and Phenomena";

  const p2 =
    lang === "ru"
      ? "В отличие от физики, которая описывает физические свойства материи без изменения её состава, химия изучает именно превращения: как атомы перегруппировываются, образуя новые молекулы с совершенно иными свойствами. Ржавчина на железе, горение дерева, приготовление пищи — всё это химические реакции."
      : lang === "uz"
      ? "Fizikadan farqli o'laroq, kimyo aynan o'zgarishlarni o'rganadi: atomlar qanday qayta tuziladi, yangi xossalarga ega yangi molekulalar hosil qiladi. Temirdagi zang, yog'och yonishi, ovqat pishirish — bularning barchasi kimyoviy reaktsiyalar."
      : "Unlike physics, which describes the physical properties of matter without changing its composition, chemistry studies precisely transformations: how atoms rearrange themselves, forming new molecules with completely different properties. Rust on iron, burning wood, cooking food — these are all chemical reactions.";

  const p3 =
    lang === "ru"
      ? "Предмет химии охватывает весь диапазон: от простейших элементов таблицы Менделеева до сложнейших молекул ДНК. Именно поэтому химия связана с биологией, физикой, геологией, медициной и технологией."
      : lang === "uz"
      ? "Kimyo predmeti butun diapazonni qamrab oladi: Mendeleev jadvalidagi eng oddiy elementlardan tortib, eng murakkab DNK molekulalarigacha."
      : "The subject of chemistry spans the entire range: from the simplest elements in the periodic table to the most complex DNA molecules. That is why chemistry is connected to biology, physics, geology, medicine and technology.";

  const annotation = lang === "ru" ? "см. Менделеев → стр. 017" : lang === "uz" ? "Mendeleev → bet 017" : "see Mendeleev → p. 017";
  const factTitle = lang === "ru" ? "ИНТЕРЕСНЫЙ ФАКТ" : lang === "uz" ? "QIZIQARLI FAKT" : "INTERESTING FACT";
  const factContent =
    lang === "ru"
      ? "Человеческое тело содержит около 60 химических элементов. 99% массы тела составляют: кислород, углерод, водород, азот, кальций и фосфор."
      : lang === "uz"
      ? "Inson tanasi taxminan 60 kimyoviy element o'z ichiga oladi. Tana massasining 99% ini: kislorod, uglerod, vodorod, azot, kaltsiy va fosfor tashkil etadi."
      : "The human body contains about 60 chemical elements. 99% of body mass consists of: oxygen, carbon, hydrogen, nitrogen, calcium and phosphorus.";

  return (
    <Spread
      pageNumLeft={13}
      pageNumRight={14}
      leftHeader={lang === "ru" ? "ГЛАВА I · ХИМИЯ" : lang === "uz" ? "I BOB · KIMYO" : "CHAPTER I · CHEMISTRY"}
      rightHeader={lang === "ru" ? "ГЛАВА I · ХИМИЯ" : lang === "uz" ? "I BOB · KIMYO" : "CHAPTER I · CHEMISTRY"}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <SectionHeading number="1.2" title={lang === "ru" ? "Вещества и явления" : lang === "uz" ? "Moddalar va hodisalar" : "Substances and Phenomena"} />
          <p className="codex-serif">{p2}</p>

          <Img
            src="/journal/chemistry/matter-phases.png"
            height={180}
            className="textured-image"
            alt="Matter phases diagram"
            width="90%"
            style={{ margin: "1rem auto" } as React.CSSProperties}
          />
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 1.2 — Фазовые состояния вещества: твёрдое, жидкое, газообразное."
              : lang === "uz"
              ? "Rasm 1.2 — Moddaning agregat holatlari: qattiq, suyuq, gazsimon."
              : "Fig. 1.2 — States of matter: solid, liquid, gas."}
          </FigureCaption>

          {/* Physical vs Chemical examples */}
          <div style={{ marginTop: "1rem", display: "flex", flexDirection: "column", gap: "0.4rem" }}>
            <div style={{ display: "flex", gap: "1rem", alignItems: "baseline" }}>
              <span className="codex-mono" style={{ fontSize: "0.6rem", opacity: 0.45, flexShrink: 0, textTransform: "uppercase", letterSpacing: "0.1em", width: "70px" }}>
                {lang === "ru" ? "физич." : lang === "uz" ? "fizik" : "physical"}
              </span>
              <code className="codex-mono" style={{ fontSize: "0.95rem", color: "var(--book-accent)" }}>
                H₂O(l) → H₂O(g)
              </code>
            </div>
            <div style={{ display: "flex", gap: "1rem", alignItems: "baseline" }}>
              <span className="codex-mono" style={{ fontSize: "0.6rem", opacity: 0.45, flexShrink: 0, textTransform: "uppercase", letterSpacing: "0.1em", width: "70px" }}>
                {lang === "ru" ? "химич." : lang === "uz" ? "kimyoviy" : "chemical"}
              </span>
              <code className="codex-mono" style={{ fontSize: "0.95rem", color: "var(--book-accent-2)" }}>
                2H₂ + O₂ → 2H₂O
              </code>
            </div>
          </div>

          <MarginNote position="right" color="pencil" rotate={2}>
            {annotation}
          </MarginNote>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <p className="codex-serif">{p3}</p>

          <InfoBox type="purple" title={factTitle}>
            <p className="codex-serif" style={{ margin: 0, fontSize: "0.9rem" }}>
              {factContent}
            </p>
          </InfoBox>

          <Img
            src="/journal/chemistry/conservation-matter.png"
            height={150}
            className="textured-image"
            alt="Conservation of mass diagram"
            width="90%"
            style={{ margin: "1.5rem auto 0" } as React.CSSProperties}
          />
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 1.1 — Закон сохранения массы: масса реагентов равна массе продуктов."
              : lang === "uz"
              ? "Rasm 1.1 — Massa saqlanish qonuni: reagentlar massasi mahsulotlar massasiga teng."
              : "Fig. 1.1 — Law of conservation of mass: the mass of reactants equals the mass of products."}
          </FigureCaption>

          {/* m(reactants) = m(products) formula */}
          <div style={{ textAlign: "center", marginTop: "0.75rem", padding: "0.6rem", borderTop: "1px solid var(--book-border)" }}>
            <code className="codex-mono" style={{ fontSize: "1.05rem", color: "var(--book-accent)" }}>
              m<sub style={{ fontSize: "0.7em" }}>{lang === "ru" ? "реаг" : "react"}</sub>
              {" = "}
              m<sub style={{ fontSize: "0.7em" }}>{lang === "ru" ? "прод" : "prod"}</sub>
            </code>
          </div>

          <PeriodicTableFoldout lang={lang} />

          {/* Handwritten margin note */}
          <div
            className="codex-handwriting"
            style={{ marginTop: "1rem", fontSize: "0.95rem", color: "var(--book-annotation)", transform: "rotate(-2deg)", opacity: 0.8, lineHeight: 1.4 }}
          >
            {lang === "ru"
              ? "«масса не исчезает — ищи, куда она ушла»"
              : lang === "uz"
              ? "«massa yo'qolmaydi — u qayerga ketganini izla»"
              : "«mass does not vanish — find where it went»"}
          </div>
        </div>
      }
    />
  );
}

// ─── 6. HISTORY OF CHEMISTRY ──────────────────────────────────────────────────

export function HistorySpread() {
  const lang = usePathname().split("/")[1] || "en";

  const chTitle =
    lang === "ru" ? "История химии" : lang === "uz" ? "Kimyo tarixi" : "History of Chemistry";
  const intro =
    lang === "ru"
      ? "На протяжении тысячелетий человечество наблюдало химические явления, не понимая их природы. Путь от алхимии к современной науке занял более двух тысяч лет и потребовал революционных открытий."
      : lang === "uz"
      ? "Mingllab yillar davomida insoniyat kimyoviy hodisalarni kuzatdi, lekin ularning tabiatini tushunmadi. Alkimyodan zamonaviy fanga o'tish yo'li ikki ming yildan ortiq vaqt oldi."
      : "For thousands of years, humanity observed chemical phenomena without understanding their nature. The path from alchemy to modern science took more than two thousand years.";

  const periods = {
    ru: [
      { year: "до н.э.", title: "Доалхимический период", text: "Египтяне, греки и китайцы открыли металлургию, стеклоделие и крашение. Первые «химики» — ремесленники, работавшие с огнём и металлами." },
      { year: "VIII–XVI в.", title: "Алхимия", text: "Арабские и европейские алхимики искали философский камень. Несмотря на мистицизм, они разработали важные лабораторные методы: дистилляцию, кристаллизацию, возгонку." },
      { year: "XVII в.", title: "Научная химия", text: "Бойль отверг алхимические элементы и ввёл понятие «химического элемента». Лавуазье открыл закон сохранения массы." },
      { year: "XIX в.", title: "Классическая химия", text: "Дальтон выдвинул атомную теорию. Менделеев создал периодическую таблицу. Кюри открыла радиоактивность." },
      { year: "XX–XXI в.", title: "Современная химия", text: "Квантовая механика объяснила химическую связь. Полимеры, фармацевтика, нанохимия и вычислительная химия изменили мир." },
    ],
    uz: [
      { year: "mil. av.", title: "Alkimyogacha bo'lgan davr", text: "Misrliklar, yunonlar va xitoyliklar metallurgiya, shisha yasash va bo'yashni kashf etishdi." },
      { year: "VIII–XVI asr", title: "Alkimyo", text: "Arab va evropalik alkimyogarlar falsafiy tosh izlashdi. Distillyatsiya, kristallanish kabi usullarni ishlab chiqishdi." },
      { year: "XVII asr", title: "Ilmiy kimyo", text: "Boyl alkimyoviy elementlarni rad etdi. Lavuazye massa saqlanish qonunini kashf etdi." },
      { year: "XIX asr", title: "Klassik kimyo", text: "Dalton atom nazariyasini taklif qildi. Mendeleev davriy jadvalni yaratdi. Kyuri radioaktivlikni kashf etdi." },
      { year: "XX–XXI asr", title: "Zamonaviy kimyo", text: "Kvant mexanikasi kimyoviy bog'lanishni tushuntirdi. Polimerlar va nanokimyo dunyoni o'zgartirdi." },
    ],
    en: [
      { year: "BCE", title: "Pre-Alchemical Period", text: "Egyptians, Greeks and Chinese discovered metallurgy, glassmaking and dyeing. The first 'chemists' were craftsmen who worked with fire and metals." },
      { year: "VIII–XVI c.", title: "Alchemy", text: "Arab and European alchemists sought the philosopher's stone. Despite mysticism, they developed key laboratory methods: distillation, crystallization, sublimation." },
      { year: "XVII c.", title: "Scientific Chemistry", text: "Boyle rejected alchemical elements and introduced 'chemical element'. Lavoisier discovered the law of conservation of mass." },
      { year: "XIX c.", title: "Classical Chemistry", text: "Dalton proposed atomic theory. Mendeleev created the periodic table. Curie discovered radioactivity." },
      { year: "XX–XXI c.", title: "Modern Chemistry", text: "Quantum mechanics explained chemical bonding. Polymers, pharmaceuticals, nanochemistry changed the world." },
    ],
  };

  const periodsData = periods[lang as keyof typeof periods] || periods.en;

  return (
    <Spread
      pageNumLeft={15}
      pageNumRight={16}
      leftHeader={lang === "ru" ? "ИСТОРИЯ ХИМИИ" : lang === "uz" ? "KIMYO TARIXI" : "HISTORY OF CHEMISTRY"}
      rightHeader={lang === "ru" ? "ИСТОРИЯ ХИМИИ" : lang === "uz" ? "KIMYO TARIXI" : "HISTORY OF CHEMISTRY"}
      left={
        <div className="page-layout-textbook history-opening-page" style={{ paddingTop: "0.5rem", position: "relative", overflow: "hidden" }}>
          <img src="/journal/history/alchemist-philosophers-stone.png" alt="" aria-hidden="true" className="history-alchemy-bg" />
          <div className="history-opening-content">
          <ChapterHeader
            roman="I"
            title={chTitle}
            subtitle={lang === "ru" ? "От алхимии к науке" : lang === "uz" ? "Alkimyodan fangacha" : "From Alchemy to Science"}
          />
          <p className="codex-serif" style={{ marginTop: "1rem" }}>{intro}</p>
          </div>
        </div>
      }
      right={
        <div className="page-layout-dense" style={{ paddingTop: "1.5rem" }}>
          <HistoryTimeline>
            {periodsData.map((p, i) => (
              <HistoryPeriod key={i} year={p.year} title={p.title} text={p.text} />
            ))}
          </HistoryTimeline>
        </div>
      }
    />
  );
}

// ─── 7. SCIENTIST SPREADS ─────────────────────────────────────────────────────

// LAVOISIER — Portrait lower-left on left page, conservation of mass on right

export function LavoisierSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const name = t("lavoisier.name");
  const header = t("lavoisier.header");
  const p1 = t("lavoisier.p1");
  const p2 = t("lavoisier.p2");
  const p3 = t("lavoisier.p3");
  const quote = t("lavoisier.quote");

  const formulaLabel =
    lang === "ru"
      ? "ЗАКОН СОХРАНЕНИЯ МАССЫ"
      : lang === "uz"
      ? "MASSA SAQLANISH QONUNI"
      : "LAW OF CONSERVATION OF MASS";

  const annotation =
    lang === "ru"
      ? "это проверить\nв песочнице →"
      : lang === "uz"
      ? "buni qumloqda\ntekshirish →"
      : "check this\nin the sandbox →";

  const discoveries = lang === "ru"
    ? ["Сформулировал закон сохранения массы.", "Доказал роль кислорода в горении и дыхании.", "Показал, что вода состоит из водорода и кислорода.", "Ввёл точные измерения и современную номенклатуру химии."]
    : lang === "uz"
    ? ["Massaning saqlanish qonunini shakllantirdi.", "Yonish va nafas olishda kislorod rolini isbotladi.", "Suv vodorod va kisloroddan tuzilganini ko'rsatdi.", "Aniq o'lchovlar va zamonaviy kimyo nomenklaturasini kiritdi."]
    : ["Formulated the law of conservation of mass.", "Proved oxygen’s role in combustion and respiration.", "Showed that water is made of hydrogen and oxygen.", "Introduced precise measurement and modern chemical nomenclature."];

  return (
    <Spread
      pageNumLeft={15}
      pageNumRight={16}
      leftHeader={header}
      rightHeader={header}
      left={
        <div className="page-layout-scientist-left" style={{ paddingTop: "1.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem", marginBottom: "0.5rem" }}>{name}</h2>
          <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5, letterSpacing: "0.15em", marginBottom: "1.5rem" }}>
            1743–1794
          </p>
          
          <FigureRow 
            reverse={false}
            image={
              <Img
                src="/journal/history/lavoisier-cartoon-transparent.png"
                height={220}
                className="textured-image scientist-img"
                alt="Antoine Lavoisier illustration"
                width="auto"
              />
            }
            caption={`Рис. 1. ${lang === "ru" ? "Антуан Лавуазье (1743–1794)" : lang === "uz" ? "Antuan Lavuazye (1743–1794)" : "Antoine Lavoisier (1743–1794)"}`}
          >
            <p className="codex-serif">{p1}</p>
          </FigureRow>
          <div className="scientist-discoveries">
            <p className="codex-mono scientist-discoveries-label">{lang === "ru" ? "ЕГО ОТКРЫТИЯ" : lang === "uz" ? "UNING KASHFIYOTLARI" : "KEY DISCOVERIES"}</p>
            {discoveries.map((item, index) => (
              <p key={item} className="codex-serif"><span>{String(index + 1).padStart(2, "0")}</span>{item}</p>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <FormulaBlock label={formulaLabel}>
            m<sub>реагентов</sub> = m<sub>продуктов</sub>
          </FormulaBlock>

          <FigureRow
            reverse={true}
            image={
              <><Img src="/journal/chemistry/scale-transparent.png" height={110} className="textured-image" alt={lang === "ru" ? "Весы Лавуазье" : lang === "uz" ? "Lavuazye tarozisi" : "Lavoisier balance"} />
              <svg width="200" height="80" viewBox="0 0 200 80" style={{ display: "none" }}>
                <line x1="100" y1="10" x2="100" y2="45" stroke="var(--book-text)" strokeWidth="2" />
                <line x1="40" y1="45" x2="160" y2="45" stroke="var(--book-text)" strokeWidth="2" />
                <line x1="40" y1="45" x2="40" y2="60" stroke="var(--book-text)" strokeWidth="1.5" />
                <line x1="160" y1="45" x2="160" y2="60" stroke="var(--book-text)" strokeWidth="1.5" />
                <ellipse cx="40" cy="65" rx="20" ry="10" fill="none" stroke="var(--book-accent)" strokeWidth="1.5" />
                <ellipse cx="160" cy="65" rx="20" ry="10" fill="none" stroke="var(--book-accent)" strokeWidth="1.5" />
                <circle cx="100" cy="8" r="4" fill="var(--book-text)" />
              </svg></>
            }
            caption={
              lang === "ru"
                ? "Рис. 2. Весы Лавуазье."
                : lang === "uz"
                ? "Rasm 2. Lavuazye tarozisi."
                : "Fig. 2. Lavoisier's balance."
            }
          >
            <p className="codex-serif">{p2}</p>
          </FigureRow>

          <TextbookBlockquote>{quote}</TextbookBlockquote>

          <p className="codex-serif" style={{ fontSize: "0.9rem" }}>{p3}</p>

          {/* В ЛАБОРАТОРИИ — sandbox connection sidenote */}
          <div style={{ marginTop: "1.5rem", borderLeft: "3px solid var(--book-accent)", paddingLeft: "0.9rem", background: "rgba(121,220,232,0.05)" }}>
            <p className="codex-mono" style={{ fontSize: "0.6rem", letterSpacing: "0.2em", textTransform: "uppercase", opacity: 0.45, marginBottom: "0.3rem" }}>
              {lang === "ru" ? "В ЛАБОРАТОРИИ JASSCIENCE" : lang === "uz" ? "JASSCIENCE LABORATORIYASIDA" : "IN JASSCIENCE LAB"}
            </p>
            <p className="codex-serif" style={{ fontSize: "0.9rem", lineHeight: 1.55, margin: 0 }}>
              {lang === "ru"
                ? "Симулятор использует этот принцип при расчёте количества вещества до и после реакции. Суммарная масса всегда сохраняется — посмотрите в инспекторе."
                : lang === "uz"
                ? "Simulyator bu printsipni reaktsiyadan oldin va keyin modda miqdorini hisoblashda ishlatadi. Umumiy massa doimo saqlanadi."
                : "The simulator applies this principle when calculating the amount of substance before and after a reaction. Total mass is always conserved — check the inspector panel."}
            </p>
          </div>

          <MarginNote position="right" color="pencil" rotate={3}>
            {annotation}
          </MarginNote>
        </div>
      }
    />
  );
}

// MENDELEEV — Left: text + periodic table fragments; Right: portrait + notes

export function MendeleevSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const name = t("mendeleev.name");
  const header = t("mendeleev.header");
  const p1 = t("mendeleev.p1");
  const p2 = t("mendeleev.p2");
  const p3 = t("mendeleev.p3");
  const quote = t("mendeleev.quote");

  const note1 = lang === "ru" ? "«элементы —\nфункции их масс»" : lang === "uz" ? "«elementlar —\nmassalarining funksiyasi»" : "\"elements are\nfunctions of their masses\"";
  const note2 = lang === "ru" ? "проверить →" : lang === "uz" ? "tekshirish →" : "check →";

  return (
    <Spread
      pageNumLeft={17}
      pageNumRight={18}
      leftHeader={header}
      rightHeader={header}
      left={
        <div className="page-layout-textbook mendeleev-layout" style={{ paddingTop: "1.15rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem", marginBottom: "0.5rem" }}>{name}</h2>
          <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5, letterSpacing: "0.15em", marginBottom: "1.5rem" }}>
            1834–1907
          </p>
          
          <FigureRow
            reverse={false}
            image={
              <MendeleevCompare lang={lang} />
            }
            caption={
              lang === "ru"
                ? "Рис. 3. Фрагмент ранней периодической системы (1869)."
                : lang === "uz"
                ? "Rasm 3. Dastlabki davriy sistema parchasi (1869)."
                : "Fig. 3. Early periodic table fragment (1869)."
            }
          >
            <p className="codex-serif">{p1}</p>
            <p className="codex-serif">{p2}</p>
          </FigureRow>
          <div className="scientist-note-block">
            <p className="codex-mono scientist-note-label">{lang === "ru" ? "ПОЧЕМУ ЭТО ВАЖНО" : lang === "uz" ? "NEGA BU MUHIM" : "WHY IT MATTERS"}</p>
            <p className="codex-serif">{lang === "ru" ? "Менделеев оставил место неизвестному: по положению элемента можно было предсказать его свойства ещё до открытия. Таблица стала не архивом, а инструментом исследования." : lang === "uz" ? "Mendeleev noma'lum elementlar uchun joy qoldirdi: element joylashuviga qarab uning xossalarini kashf etilishidan oldin ham taxmin qilish mumkin edi. Jadval tadqiqot quroliga aylandi." : "Mendeleev left room for the unknown: an element’s position could predict its properties before discovery. The table became a research instrument, not an archive."}</p>
          </div>
        </div>
      }
      right={
        <div className="page-layout-scientist-right mendeleev-layout" style={{ paddingTop: "0.85rem", position: "relative" }}>
          <TextbookBlockquote>{quote}</TextbookBlockquote>
          <FigureRow
            reverse={true}
            image={
              <Img
                src="/journal/history/mendeleev-cartoon-transparent.png"
                height={220}
                className="textured-image scientist-img"
                alt="Dmitri Mendeleev illustration"
                width="auto"
              />
            }
            caption={`Рис. 4. ${lang === "ru" ? "Дмитрий Менделеев" : lang === "uz" ? "Dmitriy Mendeleev" : "Dmitri Mendeleev"}`}
          >
            <p className="codex-serif">{p3}</p>
            <p className="codex-serif" style={{ marginTop: "1rem", lineHeight: 1.65 }}>
              {lang === "ru"
                ? "Его таблица стала рабочим инструментом: пустые клетки были не пробелами, а приглашением к поиску. В каждой новой строке исследователь видит не только данные, но и вопрос, который ещё предстоит проверить."
                : lang === "uz"
                ? "Uning jadvali ish quroliga aylandi: bo'sh kataklar bo'shliq emas, izlanishga taklif edi. Har bir yangi qatorda tadqiqotchi nafaqat ma'lumotni, balki tekshirilishi kerak bo'lgan savolni ham ko'radi."
                : "His table became a working instrument: empty cells were not gaps, but invitations to search. In every new row, a researcher sees not only data, but a question still waiting to be tested."}
            </p>
          </FigureRow>
          <div className="scientist-note-block scientist-note-block--accent">
            <p className="codex-mono scientist-note-label">{lang === "ru" ? "НАСЛЕДИЕ" : lang === "uz" ? "MEROS" : "LEGACY"}</p>
            <p className="codex-serif">{lang === "ru" ? "Периодический закон связал наблюдение, измерение и прогноз. Сегодня он помогает находить закономерности в новых материалах и реакциях." : lang === "uz" ? "Davriy qonun kuzatish, o'lchash va bashoratni birlashtirdi. Bugun u yangi materiallar va reaktsiyalardagi qonuniyatlarni topishga yordam beradi." : "The periodic law connected observation, measurement and prediction. Today it helps researchers find patterns in new materials and reactions."}</p>
          </div>
          <MarginNote position="left" color="pencil" rotate={-4}>
            {note1}
          </MarginNote>
          <MarginNote position="right" color="pencil" rotate={4}>
            {note2}
          </MarginNote>
        </div>
      }
    />
  );
}

// MARIE CURIE — Left: large text + radiation diagram; Right: portrait lower-right + annotation

export function CurieSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const name = t("curie.name");
  const header = t("curie.header");
  const p1 = t("curie.p1");
  const p2 = t("curie.p2");
  const p3 = t("curie.p3");
  const quote = t("curie.quote");

  const observation =
    lang === "ru"
      ? "«Не нужно бояться жизни, нужно понимать её»"
      : lang === "uz"
      ? "«Hayotdan qo'rqmaslik kerak, uni tushunish kerak»"
      : '"Nothing in life is to be feared, it is only to be understood"';

  return (
    <Spread
      pageNumLeft={19}
      pageNumRight={20}
      leftHeader={header}
      rightHeader={header}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem", marginBottom: "0.5rem" }}>{name}</h2>
          <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5, letterSpacing: "0.15em", marginBottom: "1.5rem" }}>
            1867–1934
          </p>
          <FigureRow
            reverse={false}
            image={
              <svg width="180" height="100" viewBox="0 0 180 100" style={{ opacity: 0.65 }}>
                {/* Nucleus */}
                <circle cx="90" cy="50" r="12" fill="none" stroke="var(--book-accent-red)" strokeWidth="2" />
                <circle cx="90" cy="50" r="5" fill="var(--book-accent-red)" opacity="0.5" />
                {/* Alpha rays */}
                <path d="M78,44 L30,20" stroke="var(--book-accent)" strokeWidth="1.5" markerEnd="url(#arr)" />
                <text x="20" y="16" fontSize="8" fill="var(--book-muted)" fontFamily="monospace">α</text>
                {/* Beta rays */}
                <path d="M102,44 L150,20" stroke="var(--book-accent-2)" strokeWidth="1.5" />
                <text x="153" y="16" fontSize="8" fill="var(--book-muted)" fontFamily="monospace">β</text>
                {/* Gamma rays */}
                <path d="M90,62 L90,90" stroke="var(--book-accent-3)" strokeWidth="1.5" strokeDasharray="4,2" />
                <text x="95" y="96" fontSize="8" fill="var(--book-muted)" fontFamily="monospace">γ</text>
              </svg>
            }
            caption={
              lang === "ru"
                ? "Рис. 5. Виды радиоактивного излучения (α, β, γ)."
                : lang === "uz"
                ? "Rasm 5. Radioaktiv nurlanish turlari (α, β, γ)."
                : "Fig. 5. Types of radioactive radiation (α, β, γ)."
            }
          >
            <p className="codex-serif drop-cap">{p1}</p>
            <p className="codex-serif">{p2}</p>
          </FigureRow>
          <p className="codex-serif scientist-continuation">
            {lang === "ru" ? "Работа Кюри требовала терпеливого сравнения множества измерений. Её пример напоминает: слабый сигнал не следует отбрасывать только потому, что он не даёт мгновенного объяснения. Сначала его нужно аккуратно записать и проверить повторением." : lang === "uz" ? "Kyurining ishi ko‘plab o‘lchovlarni sabr bilan solishtirishni talab qilardi. Uning misoli shuni eslatadi: zaif signal darhol izoh bermagani uchun uni rad etmaslik kerak. Avval uni puxta qayd etib, takrorlash orqali tekshirish lozim." : "Curie’s work required patiently comparing many measurements. Her example reminds us not to discard a weak signal simply because it offers no immediate explanation. First record it carefully, then test it through repetition."}
          </p>
        </div>
      }
      right={
        <div className="page-layout-scientist-right" style={{ position: "relative", paddingTop: "1.5rem" }}>
          <TextbookBlockquote>{quote}</TextbookBlockquote>
          
          <FigureRow
            reverse={true}
            image={
              <Img
                src="/journal/history/curie-cartoon-transparent.png"
                height={220}
                className="textured-image scientist-img"
                alt="Marie Curie illustration"
                width="auto"
              />
            }
            caption={`Рис. 6. ${lang === "ru" ? "Мария Кюри" : lang === "uz" ? "Mariya Kyuri" : "Marie Curie"}`}
          >
            <p className="codex-serif">{p3}</p>
          </FigureRow>
          <div className="scientist-note-block scientist-note-block--accent">
            <p className="codex-mono scientist-note-label">{lang === "ru" ? "ОТКРЫТИЯ" : lang === "uz" ? "KASHFIYOTLAR" : "DISCOVERIES"}</p>
            <p className="codex-serif">{lang === "ru" ? "Кюри ввела в научный язык слово «радиоактивность», открыла полоний и радий и показала, что излучение связано со строением самого атома." : lang === "uz" ? "Kyuri «radioaktivlik» so'zini ilmiy tilga kiritdi, poloniy va radiyni kashf etdi hamda nurlanish atom tuzilishi bilan bog'liqligini ko'rsatdi." : "Curie introduced radioactivity into scientific language, discovered polonium and radium, and showed that radiation is linked to the structure of the atom itself."}</p>
          </div>
          <MarginNote position="left" color="pencil" rotate={-4}>
            {observation}
          </MarginNote>
        </div>
      }
    />
  );
}

// BOYLE — Left: portrait; Right: PV diagram + gas laws

export function BoyleSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const name = t("boyle.name");
  const header = t("boyle.header");
  const p1 = t("boyle.p1");
  const p2 = t("boyle.p2");
  const p3 = t("boyle.p3");
  const quote = t("boyle.quote");

  const lawLabel =
    lang === "ru" ? "ЗАКОН БОЙЛЯ–МАРИОТТА" : lang === "uz" ? "BOYL–MARIOTT QONUNI" : "BOYLE–MARIOTTE LAW";

  return (
    <Spread
      pageNumLeft={21}
      pageNumRight={22}
      leftHeader={header}
      rightHeader={header}
      left={
        <div className="page-layout-scientist-left" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem", marginBottom: "0.5rem" }}>{name}</h2>
          <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5, letterSpacing: "0.15em", marginBottom: "1.5rem" }}>
            1627–1691
          </p>

          <FigureRow
            reverse={false}
            image={
              <Img
                src="/journal/history/boyle-cartoon-transparent.png"
                height={220}
                className="textured-image scientist-img"
                alt="Robert Boyle illustration"
                width="auto"
              />
            }
            caption={`Рис. 7. ${lang === "ru" ? "Роберт Бойль" : lang === "uz" ? "Robert Boyl" : "Robert Boyle"}`}
          >
            <p className="codex-serif">{p1}</p>
            <TextbookBlockquote>{quote}</TextbookBlockquote>
          </FigureRow>
          <p className="codex-serif scientist-continuation">
            {lang === "ru" ? "Для Бойля опыт был не демонстрацией заранее известной истины. Он менял один параметр, удерживал остальные и записывал результат так, чтобы его можно было повторить. Именно эта дисциплина превратила наблюдение за газом в проверяемый закон." : lang === "uz" ? "Boyl uchun tajriba oldindan ma’lum haqiqatning namoyishi emas edi. U bitta parametrni o‘zgartirar, qolganlarini saqlar va natijani takrorlash mumkin bo‘ladigan tarzda qayd etardi. Aynan shu tartib gaz kuzatuvini tekshiriladigan qonunga aylantirdi." : "For Boyle, an experiment was not a demonstration of a known truth. He changed one parameter, held the rest steady, and recorded the result so it could be repeated. That discipline turned an observation of gas into a testable law."}
          </p>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <FormulaBlock label={lawLabel}>
            P · V = const &nbsp;&nbsp; (T = const)
          </FormulaBlock>

          <FigureRow
            reverse={true}
            image={
              <BoyleVolumeLab lang={lang} />
            }
            caption={
              lang === "ru"
                ? "Рис. 8. Зависимость давления от объёма газа."
                : lang === "uz"
                ? "Rasm 8. Gaz bosimining hajmga bog'liqligi."
                : "Fig. 8. Pressure-volume relationship."
            }
          >
            <p className="codex-serif">{p2}</p>
            <p className="codex-serif" style={{ fontSize: "0.9rem" }}>{p3}</p>
          </FigureRow>
          <div className="scientist-note-block scientist-note-block--accent">
            <p className="codex-mono scientist-note-label">{lang === "ru" ? "КАК ЧИТАТЬ МОДЕЛЬ" : lang === "uz" ? "MODELNI QANDAY O‘QISH" : "HOW TO READ THE MODEL"}</p>
            <p className="codex-serif">{lang === "ru" ? "Изменяйте объём ползунком и сравнивайте кривую с числом давления. Важен не отдельный кадр, а направление изменения: при постоянной температуре уменьшение доступного объёма повышает давление модели." : lang === "uz" ? "Hajmni slayder bilan o‘zgartiring va egri chiziqni bosim soni bilan solishtiring. Alohida kadr emas, o‘zgarish yo‘nalishi muhim: harorat doimiy bo‘lsa, mavjud hajmning kamayishi model bosimini oshiradi." : "Adjust volume with the slider and compare the curve with the pressure value. The direction of change matters more than a single frame: at constant temperature, reducing available volume raises the model’s pressure."}</p>
          </div>
        </div>
      }
    />
  );
}

// JABIR IBN HAYYAN — Historical Islamic chemistry spread

export function JabirSpread() {
  const lang = usePathname().split("/")[1] || "en";

  const header =
    lang === "ru" ? "ИСЛАМСКАЯ ХИМИЯ" : lang === "uz" ? "ISLOM KIMYOSI" : "ISLAMIC CHEMISTRY";

  const name1 =
    lang === "ru" ? "Джабир ибн Хайян" : lang === "uz" ? "Jobir ibn Hayyon" : "Jabir ibn Hayyan";
  const dates1 = lang === "ru" ? "ок. 721–815 н.э." : lang === "uz" ? "taxminan 721–815 CE" : "c. 721–815 CE";
  const p1 =
    lang === "ru"
      ? "Арабский учёный Джабир ибн Хайян, известный в Европе как Гебер, считается отцом химии. Он первым систематически описал лабораторные процессы: дистилляцию, кальцинацию, выпаривание и кристаллизацию. Его работы были переведены на латынь и легли в основу европейской алхимии."
      : lang === "uz"
      ? "Arab olimi Jobir ibn Hayyon, Yevropada Geber nomi bilan tanilgan, kimyoning otasi hisoblanadi. U laboratoriya jarayonlarini birinchi bo'lib tizimli ravishda tasvirladi: distillyatsiya, kalsinatsiya, bug'latish va kristallanish."
      : "The Arab scholar Jabir ibn Hayyan, known in Europe as Geber, is considered the father of chemistry. He was the first to systematically describe laboratory processes: distillation, calcination, evaporation and crystallization.";

  const name2 =
    lang === "ru" ? "Мухаммад аль-Рази" : lang === "uz" ? "Muhammad al-Roziy" : "Muhammad al-Razi";
  const dates2 = "854–925 CE";
  const p2 =
    lang === "ru"
      ? "Персидский учёный аль-Рази классифицировал химические вещества на животные, растительные и минеральные — первая систематическая классификация. Он также описал кислоту (серную) и разработал методы её получения. Его «Книга тайн» содержит детальные инструкции по химическим операциям."
      : lang === "uz"
      ? "Fors olimi al-Roziy kimyoviy moddalarni hayvon, o'simlik va mineral kategoriyalarga tasnifladi — bu birinchi tizimli tasniflash edi. U sulfat kislotasini tasvirlab, uni olish usullarini ishlab chiqdi."
      : "The Persian scholar al-Razi classified chemical substances into animal, plant and mineral categories — the first systematic classification. He also described sulfuric acid and developed methods for its production.";

  const annotation =
    lang === "ru"
      ? "эти опыты повторить в Песочнице →"
      : lang === "uz"
      ? "bu tajribalarni Qumloqda takrorlang →"
      : "repeat these experiments in the Sandbox →";

  return (
    <Spread
      pageNumLeft={23}
      pageNumRight={24}
      leftHeader={header}
      rightHeader={header}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem" }}>{name1}</h2>
          <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, letterSpacing: "0.15em", marginBottom: "1rem" }}>
            {dates1}
          </p>

          {/* Decorative alembic SVG instead of portrait */}
          <div style={{ float: "left", marginRight: "1rem", marginBottom: "0.5rem" }}>
            <svg width="80" height="120" viewBox="0 0 80 120" style={{ opacity: 0.5 }}>
              {/* Alembic shape */}
              <ellipse cx="40" cy="100" rx="30" ry="15" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <line x1="10" y1="100" x2="10" y2="70" stroke="var(--book-text)" strokeWidth="1.5" />
              <line x1="70" y1="100" x2="70" y2="70" stroke="var(--book-text)" strokeWidth="1.5" />
              <path d="M10,70 Q40,50 70,70" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <path d="M40,50 Q50,30 60,10" fill="none" stroke="var(--book-text)" strokeWidth="1.5" />
              <circle cx="62" cy="8" r="5" fill="none" stroke="var(--book-accent)" strokeWidth="1.5" />
            </svg>
          </div>

          <p className="codex-serif">{p1}</p>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <h3 className="codex-cinzel" style={{ fontSize: "1.2rem", marginBottom: "0.3rem" }}>{name2}</h3>
          <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, letterSpacing: "0.15em", marginBottom: "1rem" }}>
            {dates2}
          </p>

          <p className="codex-serif">{p2}</p>

          {/* Manuscript-style decorative element */}
          <div
            style={{
              marginTop: "1.5rem",
              border: "1px solid var(--book-border)",
              padding: "1rem",
              opacity: 0.7,
            }}
          >
            <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.6, letterSpacing: "0.2em", textTransform: "uppercase", marginBottom: "0.5rem" }}>
              {lang === "ru" ? "методы алхимии" : lang === "uz" ? "alkimyo usullari" : "alchemical methods"}
            </p>
            {(lang === "ru"
              ? ["Дистилляция", "Кальцинация", "Возгонка", "Кристаллизация", "Растворение"]
              : lang === "uz"
              ? ["Distillyatsiya", "Kalsinatsiya", "Sublimatsiya", "Kristallanish", "Eritish"]
              : ["Distillation", "Calcination", "Sublimation", "Crystallization", "Dissolution"]
            ).map((m, i) => (
              <p key={i} className="codex-mono" style={{ fontSize: "0.8rem", padding: "0.2rem 0", borderBottom: "1px dotted var(--book-border)" }}>
                {String(i + 1).padStart(2, "0")} · {m}
              </p>
            ))}
          </div>

          <div
            className="codex-handwriting margin-note-right margin-note-pencil"
            style={{ fontSize: "0.9rem" }}
          >
            {annotation}
          </div>
        </div>
      }
    />
  );
}

// ─── 8. SANDBOX SPREADS ───────────────────────────────────────────────────────

export function SandboxSpread({ onOpenLab }: { onOpenLab?: (ctx: CodexLabContext) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const header = t("sandbox.header");
  const p1 = t("sandbox.p1");
  const p2 = t("sandbox.p2");
  const annotation = t("sandbox.annotation_sandbox", { defaultValue: "начать здесь →" });
  const modeSelect = t("sandbox.mode_select", { defaultValue: "SELECT — выбор и перемещение объектов" });
  const modeConnect = t("sandbox.mode_connect", { defaultValue: "CONNECT — соединение портов объектов" });
  const modeInspect = t("sandbox.mode_inspect", { defaultValue: "INSPECT — чтение свойств и состояний" });

  return (
    <Spread
      pageNumLeft={27}
      pageNumRight={28}
      leftHeader={lang === "ru" ? "ГЛАВА III · ПЕСОЧНИЦА" : lang === "uz" ? "III BOB · QUMLOQ" : "CHAPTER III · SANDBOX"}
      rightHeader={lang === "ru" ? "ГЛАВА III · ПЕСОЧНИЦА" : lang === "uz" ? "III BOB · QUMLOQ" : "CHAPTER III · SANDBOX"}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "0.5rem" }}>
          <ChapterHeader
            roman="III"
            title={header}
            subtitle={t("sandbox.subtitle", { defaultValue: "Где теория становится действием" })}
            epigraph={lang === "ru" ? "«Сначала собрать. Затем запустить. Потом объяснить.»" : lang === "uz" ? "«Avval yig'ing. Keyin ishga tushiring. Keyin tushuntiring.»" : '"First assemble. Then run. Then explain."'}
          />
          <p className="codex-serif" style={{ marginTop: "1.5rem" }}>{p1}</p>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <p className="codex-serif">{p2}</p>

          {/* Mode legend */}
          <div style={{ marginTop: "1.5rem" }}>
            {[modeSelect, modeConnect, modeInspect].map((mode, i) => (
              <div
                key={i}
                style={{
                  display: "flex",
                  gap: "0.75rem",
                  alignItems: "flex-start",
                  padding: "0.5rem 0",
                  borderBottom: "1px dotted var(--book-border)",
                }}
              >
                <span className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.5, marginTop: "0.15rem", flexShrink: 0 }}>
                  0{i + 1}
                </span>
                <span className="codex-serif" style={{ fontSize: "0.9rem" }}>{mode}</span>
              </div>
            ))}
          </div>

          <Img
            src="/journal/sandbox/atmospheric-lab-transparent.png"
            height={200}
            className="textured-image sandbox-illustration"
            alt="Sandbox laboratory illustration"
            width="95%"
            style={{ margin: "1.5rem auto 0" } as React.CSSProperties}
          />
          <FigureCaption>
            {t("sandbox.caption_diagram", { defaultValue: "Рис. 3.1 — Три рабочих режима Песочницы." })}
          </FigureCaption>

          <div className="codex-handwriting margin-note-right margin-note-red" style={{ fontSize: "1rem" }}>
            {annotation}
          </div>
          <button
            className="action-btn open-in-lab-btn codex-mono"
            style={{ marginTop: "1.25rem" }}
            onClick={() => onOpenLab?.({ type: "scenario", id: "water_intro", level: "1" })}
          >
            <ExternalLink size={13} />
            &nbsp;{t("buttons.openLab", { defaultValue: "Открыть оригинальную песочницу" })}
          </button>
        </div>
      }
    />
  );
}

export function SandboxInterfaceSpread({ onOpenLab }: { onOpenLab?: (ctx: CodexLabContext) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const p3 = t("sandbox.p3", {
    defaultValue:
      "Каждый объект на рабочем поле имеет порты — точки подключения. Жидкостные порты передают вещество, тепловые порты передают энергию, газовые порты работают с паром и давлением.",
  });
  const p4 = t("sandbox.p4", {
    defaultValue:
      "Inspector — правая панель, показывающая свойства выбранного объекта. Здесь можно увидеть текущую температуру, уровень жидкости, давление и состояние.",
  });

  // Show a mini lab setup using real SVGs
  const demoEquipment = [
    { id: "hotplate", x: 0, label: lang === "ru" ? "нагрев" : "heating" },
    { id: "beaker", x: 120, label: lang === "ru" ? "сосуд" : "vessel" },
    { id: "thermometer", x: 240, label: lang === "ru" ? "датчик" : "sensor" },
  ];

  return (
    <Spread
      pageNumLeft={29}
      pageNumRight={30}
      leftHeader={lang === "ru" ? "ГЛАВА III · ПЕСОЧНИЦА" : lang === "uz" ? "III BOB · QUMLOQ" : "CHAPTER III · SANDBOX"}
      rightHeader={lang === "ru" ? "ИНТЕРФЕЙС ЛАБОРАТОРИИ" : lang === "uz" ? "LABORATORIYA INTERFEYSI" : "LAB INTERFACE"}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <SectionHeading
            number="2.3"
            title={lang === "ru" ? "Соединения" : lang === "uz" ? "Ulanishlar" : "Connections"}
          />
          <p className="codex-serif">{p3}</p>

          {/* Connection type diagram */}
          <div style={{ marginTop: "1.5rem" }}>
            {[
              { color: "var(--book-accent)", label: lang === "ru" ? "Жидкостный порт" : "Liquid port", dash: false },
              { color: "var(--book-accent-2)", label: lang === "ru" ? "Тепловой порт" : "Thermal port", dash: true },
              { color: "var(--book-accent-3)", label: lang === "ru" ? "Газовый порт" : "Gas port", dash: false },
            ].map((conn, i) => (
              <div key={i} style={{ display: "flex", alignItems: "center", gap: "0.8rem", padding: "0.5rem 0", borderBottom: "1px dotted var(--book-border)" }}>
                <svg width="40" height="4">
                  <line
                    x1="0"
                    y1="2"
                    x2="40"
                    y2="2"
                    stroke={conn.color}
                    strokeWidth="2"
                    strokeDasharray={conn.dash ? "4,2" : "0"}
                  />
                </svg>
                <span className="codex-serif" style={{ fontSize: "0.9rem" }}>{conn.label}</span>
              </div>
            ))}
          </div>

          <SectionHeading
            number="2.4"
            title={lang === "ru" ? "Inspector" : "Inspector"}
          />
          <p className="codex-serif">{p4}</p>
        </div>
      }
      right={
        <div className="page-layout-diagram" style={{ paddingTop: "1.5rem" }}>
          <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, letterSpacing: "0.2em", textTransform: "uppercase", marginBottom: "1.5rem" }}>
            {lang === "ru" ? "ПРИМЕР УСТАНОВКИ" : lang === "uz" ? "NAMUNA QURILMA" : "EXAMPLE SETUP"}
          </p>

          {/* Real equipment SVGs in a mini lab setup */}
          <div style={{ display: "flex", gap: "1rem", alignItems: "flex-end", justifyContent: "center" }}>
            {demoEquipment.map((eq) => (
              <div key={eq.id} style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: "0.5rem" }}>
                <button
                  className="clickable-figure"
                  onClick={() => onOpenLab?.({ type: "equipment", id: eq.id })}
                  style={{ background: "none", border: "none", cursor: "pointer" }}
                >
                  <EquipmentThumbnail type={eq.id} size={80} />
                </button>
                <span className="codex-handwriting" style={{ fontSize: "0.9rem", opacity: 0.6 }}>
                  {eq.label}
                </span>
              </div>
            ))}
          </div>

          {/* Arrows between equipment */}
          <svg width="280" height="30" viewBox="0 0 280 30" style={{ margin: "0 auto", display: "block" }}>
            <line x1="90" y1="15" x2="130" y2="15" stroke="var(--book-accent)" strokeWidth="1.5" markerEnd="url(#arrow-head)" />
            <line x1="200" y1="15" x2="240" y2="15" stroke="var(--book-accent-2)" strokeWidth="1.5" strokeDasharray="4,2" />
            <defs>
              <marker id="arrow-head" markerWidth="8" markerHeight="8" refX="6" refY="3" orient="auto">
                <path d="M0,0 L0,6 L8,3 z" fill="var(--book-accent)" />
              </marker>
            </defs>
          </svg>

          <FigureCaption>
            {lang === "ru"
              ? "Рис. 3.2 — Базовая установка: нагрев + сосуд + термометр."
              : lang === "uz"
              ? "Rasm 3.2 — Asosiy qurilma: isitish + idish + termometr."
              : "Fig. 3.2 — Basic setup: heating + vessel + thermometer."}
          </FigureCaption>

          <button
            className="action-btn open-in-lab-btn codex-mono"
            style={{ marginTop: "1.5rem" }}
            onClick={() => onOpenLab?.({ type: "equipment", id: "hotplate" })}
          >
            <ExternalLink size={13} />
            &nbsp;{t("buttons.openLab", { defaultValue: "Открыть в лаборатории" })}
          </button>
        </div>
      }
    />
  );
}

// ─── 9. EQUIPMENT INDEX ───────────────────────────────────────────────────────

const equipmentNames: Record<string, { ru: string; uz: string; en: string }> = {
  erlenmeyer: { ru: "Колба Эрленмейера", uz: "Erlenmeyer kolbasi", en: "Erlenmeyer Flask" },
  beaker: { ru: "Стакан", uz: "Stakan", en: "Beaker" },
  burette: { ru: "Бюретка", uz: "Byuretka", en: "Burette" },
  condenser: { ru: "Холодильник Либиха", uz: "Libix sovutgichi", en: "Liebig Condenser" },
  burner: { ru: "Горелка Бунзена", uz: "Bunsen gorelkasi", en: "Bunsen Burner" },
  hotplate: { ru: "Нагревательная плита", uz: "Isitish plitasi", en: "Hot Plate" },
  thermometer: { ru: "Стеклянный термометр", uz: "Shisha termometr", en: "Glass Thermometer" },
  roundflask: { ru: "Круглодонная колба", uz: "Dumaloq tubli kolba", en: "Round-bottom Flask" },
  petridish: { ru: "Чашка Петри", uz: "Petri kosachasi", en: "Petri Dish" },
  clampstand: { ru: "Штатив с кольцом", uz: "Halqali shtativ", en: "Ring Stand" },
  pipette: { ru: "Мерная пипетка", uz: "O'lchov pipetkasi", en: "Volumetric Pipette" },
};
const equipmentCategories: Record<string, { ru: string; uz: string; en: string }> = {
  Vessels: { ru: "Сосуды", uz: "Idishlar", en: "Vessels" }, Measurement: { ru: "Измерение", uz: "O'lchash", en: "Measurement" }, Cooling: { ru: "Охлаждение", uz: "Sovutish", en: "Cooling" }, Heating: { ru: "Нагрев", uz: "Isitish", en: "Heating" }, Support: { ru: "Опора", uz: "Tayanch", en: "Support" }, Transfer: { ru: "Перенос", uz: "O'tkazish", en: "Transfer" },
};
const equipmentDescriptions: Record<string, { ru: string; uz: string; en: string }> = {
  erlenmeyer: { ru: "Сосуд для смешивания, нагревания и контролируемых опытов.", uz: "Aralashtirish, isitish va nazoratli tajribalar uchun idish.", en: "Reaction vessel for mixing, heating and controlled experiments." },
  beaker: { ru: "Открытый сосуд для переливания и наблюдений.", uz: "Quyish va kuzatish uchun ochiq idish.", en: "Open vessel for transfer and observation." },
  burette: { ru: "Точный прибор для подачи жидкости при титровании.", uz: "Titrlashda suyuqlikni aniq berish asbobi.", en: "Precision delivery instrument for titration." },
  condenser: { ru: "Охлаждающая рубашка для превращения пара в жидкость.", uz: "Bug'ni suyuqlikka aylantiruvchi sovutish qobig'i.", en: "Cooling jacket for condensing vapor into liquid." },
  burner: { ru: "Регулируемый источник пламени для нагревания.", uz: "Nazoratli isitish uchun sozlanadigan alanga manbai.", en: "Adjustable flame source for controlled heating." },
  hotplate: { ru: "Устойчивая керамическая поверхность для нагрева.", uz: "Isitish uchun barqaror keramik yuza.", en: "Stable ceramic heating surface." },
  thermometer: { ru: "Прибор для прямого измерения температуры.", uz: "Haroratni bevosita o'lchash asbobi.", en: "Direct temperature measurement instrument." },
  roundflask: { ru: "Сферический сосуд для нагревания и дистилляции.", uz: "Isitish va distillatsiya uchun sferik idish.", en: "Spherical vessel for heating and distillation." },
  petridish: { ru: "Неглубокий сосуд для наблюдений и выращивания образцов.", uz: "Kuzatish va namunalarni o'stirish uchun sayoz idish.", en: "Shallow vessel for observation and culture work." },
  clampstand: { ru: "Механическая опора для сосудов и зажимов.", uz: "Idishlar va qisqichlar uchun mexanik tayanch.", en: "Mechanical support for vessels and clamps." },
  pipette: { ru: "Точный прибор для переноса измеренных объёмов.", uz: "O'lchangan hajmlarni aniq o'tkazish asbobi.", en: "Precision transfer instrument for measured volumes." },
};
function localizedEquipment(eq: EquipmentDefinition, lang: string) { const locale = lang === "ru" || lang === "uz" ? lang : "en"; return { name: equipmentNames[eq.id]?.[locale] ?? eq.name, category: equipmentCategories[eq.category]?.[locale] ?? eq.category, description: equipmentDescriptions[eq.id]?.[locale] ?? eq.description }; }
function equipmentEditorial(eq: EquipmentDefinition, lang: string) {
  const shape = eq.category === 'Vessels'
    ? { ru: 'Форма сосуда управляет перемешиванием, испарением и устойчивостью. Узкая горловина уменьшает случайные брызги, а широкое основание помогает равномерно передавать тепло и делает объект устойчивым на рабочей поверхности.', uz: 'Idish shakli aralashtirish, bug‘lanish va barqarorlikni boshqaradi. Tor bo‘yin tasodifiy sachrashni kamaytiradi, keng taglik esa issiqlik uzatilishini tekislaydi.', en: 'Vessel geometry controls mixing, evaporation and stability. A narrow neck reduces accidental splashing, while a broad base distributes heat and keeps the object stable on the workspace.' }
    : eq.category === 'Measurement'
    ? { ru: 'Измерительный прибор превращает изменение системы в читаемый сигнал. Его точность зависит не только от шкалы, но и от правильного положения датчика относительно исследуемой среды.', uz: 'O‘lchov asbobi tizimdagi o‘zgarishni o‘qiladigan signalga aylantiradi. Aniqlik shkala bilan birga sensorning muhitga nisbatan joylashuviga ham bog‘liq.', en: 'A measuring instrument turns a system change into a readable signal. Accuracy depends not only on the scale, but also on the sensor position relative to the observed medium.' }
    : { ru: 'Этот прибор является частью установки, поэтому его поведение определяется связями с соседними объектами. Важно наблюдать не отдельный значок, а поток энергии, вещества или механического усилия через всю систему.', uz: 'Bu asbob qurilmaning bir qismi bo‘lib, uning xatti-harakati qo‘shni obyektlar bilan bog‘lanishlarga bog‘liq. Energiya, modda yoki mexanik kuch oqimini butun tizim bo‘ylab kuzatish muhim.', en: 'This instrument is part of a setup, so its behavior is defined by connections to neighboring objects. Observe the flow of energy, matter or mechanical force through the whole system rather than the isolated icon.' };
  const sandbox = { ru: `В Песочнице объект использует ${eq.ports?.length || 0} виртуальных порта. Inspector показывает доступные соединения и сразу отмечает несовместимый тип. Это позволяет проверить назначение прибора без реальной лабораторной процедуры.`, uz: `Sandboxda obyekt ${eq.ports?.length || 0} ta virtual portdan foydalanadi. Inspector mavjud ulanishlarni ko‘rsatadi va mos kelmaydigan turini belgilaydi.`, en: `In the Sandbox the object exposes ${eq.ports?.length || 0} virtual ports. The Inspector lists valid connections and immediately marks an incompatible type, allowing its role to be explored without a real laboratory procedure.` };
  const locale = lang === 'ru' || lang === 'uz' ? lang : 'en'; return [shape[locale], sandbox[locale]];
}

export function EquipmentIndexSpread({ navigate, onOpenLab }: { navigate: (page: number) => void; onOpenLab?: (ctx: CodexLabContext) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const equipmentWithNums = equipmentDefinitions.map((eq, i) => ({
    ...eq,
    num: `EQ.${String(i + 1).padStart(2, "0")}`,
    // Navigate to equipment detail — starts at spread index 16
    detailSpread: 17 + i,
  }));

  const half = Math.ceil(equipmentWithNums.length / 2);
  const leftEquipment = equipmentWithNums.slice(0, half);
  const rightEquipment = equipmentWithNums.slice(half);

  return (
    <Spread
      pageNumLeft={31}
      pageNumRight={32}
      leftHeader={lang === "ru" ? "ГЛАВА III · ОБОРУДОВАНИЕ" : lang === "uz" ? "III BOB · JIHOZLAR" : "CHAPTER III · EQUIPMENT"}
      rightHeader={t("equipment.title")}
      left={
        <div className="page-layout-catalogue" style={{ paddingTop: "0.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.6rem", borderBottom: "1px solid var(--book-border)", paddingBottom: "0.5rem", marginBottom: "1rem" }}>
            {t("equipment.header")}
          </h2>
          <p className="codex-serif" style={{ fontSize: "0.9rem", marginBottom: "1rem", opacity: 0.7 }}>
            {t("equipment.p1")}
          </p>
          <div className="equipment-grid">
            {leftEquipment.map((eq) => (
              <button
                key={eq.id}
                className="equipment-grid-item"
                onClick={() => navigate(eq.detailSpread)}
              >
                <span className="equipment-number codex-mono">{eq.num}</span>
                <EquipmentThumbnail type={eq.id} size={70} />
                <span className="equipment-name codex-serif">{localizedEquipment(eq, lang).name}</span>
                <span className="equipment-category codex-mono">{localizedEquipment(eq, lang).category}</span>
              </button>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-catalogue" style={{ paddingTop: "2.5rem" }}>
          <div className="equipment-grid">
            {rightEquipment.map((eq) => (
              <button
                key={eq.id}
                className="equipment-grid-item"
                onClick={() => navigate(eq.detailSpread)}
              >
                <span className="equipment-number codex-mono">{eq.num}</span>
                <EquipmentThumbnail type={eq.id} size={70} />
                <span className="equipment-name codex-serif">{localizedEquipment(eq, lang).name}</span>
                <span className="equipment-category codex-mono">{localizedEquipment(eq, lang).category}</span>
              </button>
            ))}
          </div>
        </div>
      }
    />
  );
}

// ─── 10. EQUIPMENT DETAIL ─────────────────────────────────────────────────────

export function EquipmentDetailSpread({
  equipment,
  onOpenLab,
  spreadIndex,
  navigate,
}: {
  equipment: EquipmentDefinition;
  onOpenLab?: (ctx: CodexLabContext) => void;
  spreadIndex: number;
  navigate?: (page: number) => void;
}) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";
  const localized = localizedEquipment(equipment, lang);
  const editorial = equipmentEditorial(equipment, lang);

  const figText = lang === "ru" ? "Рис." : lang === "uz" ? "Rasm" : "Fig.";
  const openLab = t("buttons.openLab", { defaultValue: "Открыть в лаборатории" });
  const backLabel = lang === "ru" ? "← оборудование" : lang === "uz" ? "← jihozlar" : "← equipment";

  // Purpose text from description
  const purposeTitle = lang === "ru" ? "Назначение" : lang === "uz" ? "Maqsad" : "Purpose";
  const featuresTitle = lang === "ru" ? "Особенности" : lang === "uz" ? "Xususiyatlar" : "Features";
  const portsTitle = lang === "ru" ? "Порты подключения" : lang === "uz" ? "Ulanish portlari" : "Connection Ports";
  const tryTitle = lang === "ru" ? "Попробуйте в лаборатории" : lang === "uz" ? "Laboratoriyada sinab ko'ring" : "Try it in the lab";
  const tryText = lang === "ru"
    ? "Добавьте немного воды из библиотеки материалов, выберите этот сосуд и проверьте, как меняются объём и состояние вещества в Inspector. Затем соедините жидкостный порт со следующим сосудом."
    : lang === "uz"
    ? "Materiallar kutubxonasidan ozgina suv qo'shing, ushbu idishni tanlang va Inspector'da hajm hamda modda holati qanday o'zgarishini kuzating. Keyin suyuqlik portini keyingi idish bilan ulang."
    : "Add a little water from the materials library, select this vessel and observe the volume and substance state in the Inspector. Then connect its liquid port to the next vessel.";

  const annotation =
    lang === "ru"
      ? "проверить\nв песочнице →"
      : lang === "uz"
      ? "qumloqda\ntekshirish →"
      : "check in\nsandbox →";

  return (
    <Spread
      pageNumLeft={33 + (spreadIndex - 16) * 2}
      pageNumRight={34 + (spreadIndex - 16) * 2}
      leftHeader={localized.name}
      rightHeader={localized.name}
      left={
        <div className="page-layout-balanced detail-spread" style={{ paddingTop: "1rem" }}>
          <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
            {/* Large equipment SVG — the main illustration */}
            <EquipmentThumbnail type={equipment.id} size={110} />

            <FigureCaption>
              {figText} {spreadIndex}. {localized.name}
              <br />
              ID: {equipment.id.toUpperCase()}
              {equipment.capacity && ` · ${equipment.capacity}`}
            </FigureCaption>
          </div>

          {/* Dimension annotations (decorative line annotations) */}
          <div style={{ marginTop: "1.5rem", borderTop: "1px solid var(--book-border)", paddingTop: "1rem" }}>
            {equipment.material && (
              <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5 }}>
                {lang === "ru" ? "Материал: " : lang === "uz" ? "Material: " : "Material: "}{equipment.material}
              </p>
            )}
            {equipment.capacity && (
              <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5 }}>
                {lang === "ru" ? "Ёмкость: " : lang === "uz" ? "Sig'imi: " : "Capacity: "}{equipment.capacity}
              </p>
            )}
            <p className="codex-mono" style={{ fontSize: "0.75rem", opacity: 0.5 }}>
                {lang === "ru" ? "Категория: " : lang === "uz" ? "Turkum: " : "Category: "}{localized.category}
            </p>
          </div>
          <EquipmentMiniLab equipmentId={equipment.id} lang={lang} />
        </div>
      }
      right={
        <div className="page-layout-balanced detail-spread-right" style={{ paddingTop: "1.5rem", position: "relative" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.4rem", marginBottom: "1rem" }}>{localized.name}</h2>

          <div style={{ marginBottom: "1.5rem" }}>
            <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: "0.4rem" }}>
              {purposeTitle}
            </p>
            <p className="codex-serif">{localized.description}</p>
            {editorial.map(text => <p key={text} className="codex-serif" style={{fontSize:'.86rem',marginTop:'.65rem'}}>{text}</p>)}
          </div>

          {equipment.ports && equipment.ports.length > 0 && (
            <div style={{ marginBottom: "1.5rem" }}>
              <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: "0.4rem" }}>
                {portsTitle}
              </p>
              <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
                {equipment.ports.map((port, i) => (
                  <li
                    key={i}
                    className="codex-mono"
                    style={{ fontSize: "0.8rem", padding: "0.25rem 0", borderBottom: "1px dotted var(--book-border)", opacity: 0.7 }}
                  >
                    {port.type} · {port.id}
                  </li>
                ))}
              </ul>
            </div>
          )}

          {/* Handwritten note */}
          <div
            className="codex-handwriting"
            style={{
              color: "var(--book-pencil)",
              fontSize: "1rem",
              transform: "rotate(-2deg)",
              opacity: 0.6,
              marginBottom: "1rem",
            }}
          >
            {lang === "ru"
              ? "всегда проверяй наличие трещин перед нагревом"
              : lang === "uz"
              ? "isitishdan oldin doimo yoriqlarni tekshiring"
              : "always check for cracks before heating"}
          </div>

          <div className="equipment-practice-note">
            <p className="codex-mono equipment-practice-label">{tryTitle}</p>
            <p className="codex-serif">{tryText}</p>
          </div>

          <div style={{ marginTop: "auto", display: "flex", gap: "1rem", flexWrap: "wrap" }}>
            {navigate && (
              <button className="action-btn codex-mono" onClick={() => navigate(15)}>
                {backLabel}
              </button>
            )}
            <button
              className="action-btn open-in-lab-btn codex-mono"
              onClick={() => onOpenLab?.({ type: "equipment", id: equipment.id })}
            >
              <ExternalLink size={13} />
              &nbsp;{openLab}
            </button>
          </div>

          <MarginNote position="right" color="pencil" rotate={3}>
            {annotation}
          </MarginNote>
        </div>
      }
    />
  );
}

// ─── 11. SUBSTANCE INDEX ──────────────────────────────────────────────────────

export function SubstanceIndexSpread({ navigate }: { navigate: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";
  const midpoint = Math.ceil(materialDefinitions.length / 2);

  return (
    <Spread
      pageNumLeft={55}
      pageNumRight={56}
      leftHeader={t("substance.header")}
      rightHeader={t("substance.title")}
      left={
        <div className="page-layout-catalogue" style={{ paddingTop: "0.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.6rem", borderBottom: "1px solid var(--book-border)", paddingBottom: "0.5rem", marginBottom: "1rem" }}>
            {t("substance.header")}
          </h2>
          <p className="codex-serif" style={{ fontSize: "0.9rem", opacity: 0.7, marginBottom: "1.5rem" }}>
            {lang === "ru"
              ? "Вещества образуют материальную часть виртуальной лаборатории. Каждая запись объединяет формулу, агрегатное состояние и измеримые свойства, которые использует симуляция. Откройте карточку, чтобы сравнить молекулярную или кристаллическую модель, проследить условное изменение состояния и затем перенести выбранное вещество в Песочницу."
              : lang === "uz"
              ? "Moddalar virtual laboratoriyaning material qatlamini tashkil etadi. Har bir yozuv formula, agregat holat va simulyatsiya foydalanadigan o‘lchanadigan xususiyatlarni birlashtiradi. Molekulyar yoki kristall modelni solishtirish, holatning shartli o‘zgarishini kuzatish va tanlangan moddani Sandboxga o‘tkazish uchun kartani oching."
              : "Substances form the material layer of the virtual laboratory. Each record combines a formula, physical state and measurable properties used by the simulation. Open a card to compare its molecular or crystal model, explore a conceptual state change and then transfer the selected substance into the Sandbox."}
          </p>
          <div className="substance-grid">
            {materialDefinitions.slice(0, midpoint).map((m, i) => (
              <button
                key={m.id}
                className="substance-item"
                onClick={() => navigate(29 + i)}
                style={{
                  background: "none",
                  border: "none",
                  borderBottom: "1px solid var(--book-border)",
                  cursor: "pointer",
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  padding: "0.38rem 0.25rem",
                  gap: "0.22rem",
                }}
              >
                <SandboxMaterialVisual materialId={m.id} formula={m.formulaHtml} size={48} />
                <span
                  className="formula-display codex-mono"
                  dangerouslySetInnerHTML={{ __html: m.formulaHtml }}
                />
                <span className="codex-serif" style={{ fontSize: "0.74rem" }}>{t(m.nameKey)}</span>
              </button>
            ))}
          </div>

          <div className="codex-handwriting" style={{marginTop:'1.2rem',color:'var(--book-pencil)',transform:'rotate(-2deg)',opacity:.65}}>{lang === 'ru' ? 'формула — это карта состава, а не рисунок вещества' : lang === 'uz' ? 'formula — tarkib xaritasi, moddaning rasmi emas' : 'a formula maps composition; it is not a picture of the substance'}</div>
        </div>
      }
      right={
        <div className="page-layout-catalogue" style={{ paddingTop: "2rem" }}>
          <div className="substance-grid">
            {materialDefinitions.slice(midpoint).map((m, i) => (
              <button
                key={m.id}
                className="substance-item"
                onClick={() => navigate(29 + midpoint + i)}
                style={{
                  background: "none",
                  border: "none",
                  borderBottom: "1px solid var(--book-border)",
                  cursor: "pointer",
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  padding: "0.38rem 0.25rem",
                  gap: "0.22rem",
                }}
              >
                <SandboxMaterialVisual materialId={m.id} formula={m.formulaHtml} size={48} />
                <span
                  className="formula-display codex-mono"
                  dangerouslySetInnerHTML={{ __html: m.formulaHtml }}
                />
                <span className="codex-serif" style={{ fontSize: "0.74rem" }}>{t(m.nameKey)}</span>
              </button>
            ))}
          </div>

          {/* Note about periodic table */}
          <div
            className="codex-handwriting"
            style={{ marginTop: "2.5rem", fontSize: "1rem", color: "var(--book-pencil)", transform: "rotate(-2deg)", opacity: 0.6, lineHeight: 1.5 }}
          >
            {lang === "ru"
              ? "база расширяется\nс каждым опытом →"
              : lang === "uz"
              ? "har bir tajriba bilan\nbaza kengayadi →"
              : "database grows\nwith each experiment →"}
          </div>
        </div>
      }
    />
  );
}

// ─── 12. SUBSTANCE DETAIL ─────────────────────────────────────────────────────

function substanceNarrative(materialId: string, lang: string) {
  const ru: Record<string, string> = {
    water: 'Дистиллированная вода — чистая жидкая среда, с которой удобно начинать наблюдение за объёмом, нагревом и переносом жидкости. В Sandbox она остаётся прозрачной и меняет уровень только тогда, когда действительно добавлена или вылита.',
    sulfuric_acid: 'Серная кислота — плотная коррозионная жидкость. В виртуальной лаборатории её запись нужна для сравнения плотности и поведения кислоты с водой; смешивание всегда следует рассматривать как отдельное событие модели.',
    hydrochloric_acid: 'Соляная кислота представлена как водный раствор хлороводорода. Она участвует в реакции с цинком и нейтрализации со щёлочью, поэтому её ценность в Sandbox — в наблюдении за последовательностью, а не в одном числовом показателе.',
    hydrogen_peroxide: 'Перекись водорода — жидкость с окислительными свойствами. Карточка отделяет состав вещества от результата реакции: цвет, объём и состояние в сцене меняются только после действия пользователя.',
    copper_sulfate: 'Сульфат меди(II) — кристаллическая соль с характерным синим цветом. В опыте кристаллы могут стать частью раствора, но запись вещества сохраняет исходную форму и свойства для сравнения.',
    copper_sulfate_solution: 'Раствор сульфата меди показывает, как твёрдое вещество распределяется в воде. В Sandbox важны не только синий цвет, но и фактически перенесённый объём и состав содержимого сосуда.',
    potassium_permanganate: 'Перманганат калия — фиолетовая кристаллическая соль и сильный окислитель. Его удобно узнавать по цвету кристаллов, а при растворении — по насыщенности раствора и изменению доли вещества.',
    potassium_permanganate_solution: 'Раствор перманганата калия сохраняет фиолетовую окраску даже при разбавлении. Интерактивная модель помогает увидеть, что добавление воды изменяет концентрацию, но не создаёт новый материал из ничего.',
    sodium_hydroxide: 'Гидроксид натрия — твёрдая щёлочь. В книге он нужен как участник нейтрализации: его поведение определяется контактом с раствором, а не декоративным изменением карточки.',
    sodium_carbonate: 'Карбонат натрия — ионная соль, обычно встречающаяся в виде белого порошка. Виртуальная карточка связывает формулу, твёрдое состояние и возможность растворения с наблюдаемым количеством.',
    zinc: 'Цинк — реакционноспособный металл. В Sandbox он вступает в реакцию с соляной кислотой с выделением водорода, поэтому его запись полезна как точка сравнения между веществом и процессом.',
    copper: 'Медь — плотный переходный металл с характерным тёплым оттенком. Она служит материальным образцом: цвет и масса помогают отличать металл от раствора медного купороса.',
    gold: 'Золото — тяжёлый химически устойчивый металл. В каталоге оно показывает, что одинаковое твёрдое состояние не означает одинаковую плотность, реакционность или область применения.',
    sulfur: 'Сера — жёлтое твёрдое вещество, которое при нагреве переходит в жидкую фазу. В интерактивной модели этот переход виден через изменение состояния, а не через подмену самого материала.',
    ph_indicator: 'Индикатор pH — чувствительная к кислотности среда. Он становится полезным только рядом с раствором и измерительным прибором: сама карточка описывает вещество, а показание появляется после фактического подключения датчика.',
  };
  if (lang === 'ru') return ru[materialId] ?? 'Это вещество связывает химическую формулу, физическое состояние и наблюдаемое поведение в виртуальной лаборатории. Его карточка помогает сравнивать состав, свойства и результат действия в Sandbox.';
  if (lang === 'uz') return 'Bu modda kimyoviy formula, fizik holat va virtual laboratoriyadagi kuzatiladigan xatti-harakatni birlashtiradi. Kartochka tarkib, xususiyat va Sandboxdagi amaliy natijani solishtirishga yordam beradi.';
  return 'This substance connects a chemical formula, a physical state and an observable behavior in the virtual laboratory. Its record helps compare composition, properties and the result of an action in the Sandbox.';
}

export function SubstanceDetailSpread({
  material,
  onOpenLab,
  spreadIndex,
  navigate,
}: {
  material: any;
  onOpenLab?: (ctx: CodexLabContext) => void;
  spreadIndex: number;
  navigate?: (page: number) => void;
}) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const openLab = t("buttons.openLab", { defaultValue: "Открыть в лаборатории" });
  const stateLabel = t("substance.state");
  const molarLabel = t("substance.molar");
  const boilingLabel = t("substance.boiling");
  const densityLabel = t("substance.density");
  const stateText = t(material.stateKey);
  const backLabel = lang === "ru" ? "← вещества" : lang === "uz" ? "← moddalar" : "← substances";

  const pageNum = 57 + (spreadIndex - 28) * 2;
  const substanceNotes = lang === 'ru'
    ? [`Формула описывает состав частицы, а физические свойства показывают, как множество таких частиц ведёт себя как материал. Поэтому одинаковое агрегатное состояние ещё не означает одинаковую плотность, летучесть или способность растворяться.`, `В симуляции запись связана с температурой, количеством и текущей фазой. Изменение ползунка ниже — это условная модель распределения частиц, а не инструкция для реального опыта.`]
    : lang === 'uz'
    ? [`Formula zarracha tarkibini, fizik xususiyatlar esa ko‘plab zarrachalarning material sifatidagi xatti-harakatini ifodalaydi. Bir xil agregat holat zichlik yoki uchuvchanlik bir xil degani emas.`, `Simulyatsiyada yozuv harorat, miqdor va joriy faza bilan bog‘langan. Quyidagi model haqiqiy tajriba ko‘rsatmasi emas.`]
    : [`A formula describes particle composition, while physical properties describe how many such particles behave as a material. Sharing a state does not imply identical density, volatility or solubility.`, `In the simulation this record is linked to temperature, amount and current phase. The model below represents particle behavior conceptually; it is not a real experiment instruction.`];

  return (
    <Spread
      pageNumLeft={pageNum}
      pageNumRight={pageNum + 1}
      leftHeader={t(material.nameKey)}
      rightHeader={t(material.nameKey)}
      left={
        <div className="page-layout-balanced detail-spread" style={{ paddingTop: "1rem" }}>
          <div className="detail-image-container">
            <SandboxMaterialVisual materialId={material.id} formula={material.formulaHtml} size={148} />
            <FigureCaption>
              {lang === "ru" ? `Рис. ${spreadIndex}. ` : lang === "uz" ? `Rasm ${spreadIndex}. ` : `Fig. ${spreadIndex}. `}
              {t(material.nameKey)} — {stateText.toLowerCase()}
            </FigureCaption>
          </div>
          <div
            className="formula-display codex-mono"
            dangerouslySetInnerHTML={{ __html: material.formulaHtml }}
            style={{ fontSize: "2rem", textAlign: "center", marginTop: "1rem" }}
          />
          <div style={{ marginTop: "1.1rem", padding: "0.85rem 0.95rem", borderLeft: "2px solid var(--book-accent)", background: "rgba(127,181,197,0.06)" }}>
            <p className="codex-mono" style={{ fontSize: "0.58rem", letterSpacing: "0.18em", opacity: 0.55, marginBottom: "0.4rem" }}>
              {lang === "ru" ? "НАБЛЮДЕНИЕ В ЛАБОРАТОРИИ" : lang === "uz" ? "LABORATORIYADAGI KUZATUV" : "LABORATORY OBSERVATION"}
            </p>
            <p className="codex-serif" style={{ fontSize: "0.82rem", lineHeight: 1.52, margin: 0 }}>
              {substanceNarrative(material.id, lang)}
            </p>
          </div>
          <p className="codex-handwriting" style={{ marginTop: "0.8rem", color: "var(--book-pencil)", fontSize: "0.9rem", transform: "rotate(-1.5deg)", opacity: 0.7 }}>
            {lang === "ru" ? "сначала увидеть вещество, затем менять условие" : lang === "uz" ? "avval moddani ko‘rish, keyin shartni o‘zgartirish" : "observe the substance first, then change one condition"}
          </p>
        </div>
      }
      right={
        <div className="page-layout-balanced detail-spread-right" style={{ paddingTop: "2rem", position: "relative" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.4rem", marginBottom: "1.5rem" }}>
            {t(material.nameKey)}
          </h2>
          {substanceNotes.map(note => <p key={note} className="codex-serif" style={{fontSize:'.84rem',lineHeight:1.55,marginBottom:'.65rem'}}>{note}</p>)}
          <ul className="substance-properties codex-mono">
            <li style={{ display: "flex", justifyContent: "space-between", padding: "0.5rem 0", borderBottom: "1px dotted var(--book-border)" }}>
              <span style={{ opacity: 0.5 }}>{stateLabel}</span>
              <span>{stateText}</span>
            </li>
            <li style={{ display: "flex", justifyContent: "space-between", padding: "0.5rem 0", borderBottom: "1px dotted var(--book-border)" }}>
              <span style={{ opacity: 0.5 }}>{molarLabel}</span>
              <span>{material.molarMass}</span>
            </li>
            <li style={{ display: "flex", justifyContent: "space-between", padding: "0.5rem 0", borderBottom: "1px dotted var(--book-border)" }}>
              <span style={{ opacity: 0.5 }}>{boilingLabel}</span>
              <span>{material.boilingPoint}</span>
            </li>
            <li style={{ display: "flex", justifyContent: "space-between", padding: "0.5rem 0", borderBottom: "1px dotted var(--book-border)" }}>
              <span style={{ opacity: 0.5 }}>{densityLabel}</span>
              <span>{material.density}</span>
            </li>
          </ul>

          <SubstanceStateDemo materialId={material.id} formula={material.formulaHtml} lang={lang} />

          {/* Behavior in sandbox note */}
          <div style={{ marginTop: "1.2rem", padding: "0.6rem 0.8rem", borderLeft: "2px solid var(--book-accent)", background: "rgba(127,181,197,0.05)" }}>
            <p className="codex-mono" style={{ fontSize: "0.6rem", letterSpacing: "0.2em", opacity: 0.4, textTransform: "uppercase", marginBottom: "0.3rem" }}>
              {lang === "ru" ? "В ПЕСОЧНИЦЕ" : lang === "uz" ? "QUMLOQDA" : "IN SANDBOX"}
            </p>
            <p className="codex-serif" style={{ fontSize: "0.85rem", lineHeight: 1.5, margin: 0 }}>
              {lang === "ru"
                ? "В виртуальной модели изменение условной энергии может переключать визуальное состояние вещества. Inspector фиксирует переход как изменение данных симуляции."
                : lang === "uz"
                ? "Virtual modelda shartli energiyaning o‘zgarishi moddaning ko‘rsatilgan holatini almashtirishi mumkin. Inspector bu o‘tishni simulyatsiya ma’lumotlaridagi o‘zgarish sifatida qayd etadi."
                : "In the virtual model, changing relative energy can switch the displayed state. The Inspector records the transition as simulation data."}
            </p>
          </div>

          <div
            className="codex-handwriting"
            style={{ marginTop: "1.2rem", color: "var(--book-pencil)", fontSize: "1rem", transform: "rotate(-2deg)", opacity: 0.6 }}
          >
            {lang === "ru" ? "условная модель — сначала наблюдать, потом объяснять" : lang === "uz" ? "shartli model — avval kuzatish, keyin tushuntirish" : "conceptual model — observe first, explain second"}
          </div>

          <div style={{ marginTop: "auto", display: "flex", gap: "1rem", flexWrap: "wrap" }}>
            {navigate && (
              <button className="action-btn codex-mono" onClick={() => navigate(28)}>
                {backLabel}
              </button>
            )}
            <button
              className="action-btn open-in-lab-btn codex-mono"
              onClick={() => onOpenLab?.({ type: "material", id: material.id })}
            >
              <ExternalLink size={13} />
              &nbsp;{openLab}
            </button>
          </div>
        </div>
      }
    />
  );
}

// ─── 13. SCENARIOS ────────────────────────────────────────────────────────────

export function ScenarioIndexSpread({ navigate, onOpenLab }: { navigate: (page: number) => void; onOpenLab?: (ctx: CodexLabContext) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={93}
      pageNumRight={94}
      leftHeader={t("scenario.header")}
      rightHeader={t("scenario.title")}
      left={
        <div className="page-layout-dense" style={{ paddingTop: "0.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.6rem", borderBottom: "1px solid var(--book-border)", paddingBottom: "0.5rem", marginBottom: "1rem" }}>
            {t("scenario.header")}
          </h2>
          <p className="codex-serif" style={{ fontSize: "0.9rem", opacity: 0.7, marginBottom: "1.5rem" }}>
            {t("scenario.p1")}
          </p>
          <div className="editorial-index">
            {scenarioDefinitions.map((sc, i) => (
              <button className="editorial-index-row" key={sc.id} onClick={() => navigate(33 + i)}>
                <span className="index-title codex-serif">
                  <span className="codex-mono" style={{ opacity: 0.5, marginRight: "0.5rem" }}>
                    {String(i + 1).padStart(2, "0")}
                  </span>
                  {t(sc.nameKey)}
                </span>
                <span className="index-dots" />
                <span className="index-page codex-mono">{String(95 + i * 2).padStart(3, "0")}</span>
              </button>
            ))}
          </div>
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "2rem" }}>
          <p className="codex-serif">{t("scenario.p2")}</p>
          <div
            className="codex-handwriting"
            style={{ marginTop: "2rem", fontSize: "1.1rem", color: "var(--book-annotation)", transform: "rotate(-3deg)", opacity: 0.7 }}
          >
            {lang === "ru" ? "Протоколы должны строго соблюдаться." : lang === "uz" ? "Protokollar qat'iy bajarilishi shart." : "Protocols must be strictly followed."}
          </div>

          {/* Mini equipment illustration */}
          <div style={{ display: "flex", gap: "0.5rem", justifyContent: "center", marginTop: "3rem", opacity: 0.6 }}>
            <EquipmentThumbnail type="beaker" size={60} />
            <EquipmentThumbnail type="hotplate" size={60} />
            <EquipmentThumbnail type="thermometer" size={60} />
          </div>
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 4.1 — Типовая установка для базового нагрева."
              : lang === "uz"
              ? "Rasm 4.1 — Asosiy isitish uchun tipik qurilma."
              : "Fig. 4.1 — Typical setup for basic heating."}
          </FigureCaption>
        </div>
      }
    />
  );
}

export function ScenarioDetailSpread({
  scenario,
  onOpenLab,
  spreadIndex,
  navigate,
}: {
  scenario: any;
  onOpenLab?: (ctx: CodexLabContext) => void;
  spreadIndex: number;
  navigate?: (page: number) => void;
}) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";
  const openLab = t("buttons.loadScenario", { defaultValue: "Загрузить сценарий" });
  const backLabel = lang === "ru" ? "← сценарии" : lang === "uz" ? "← stsenariylar" : "← scenarios";

  const pageNum = 95 + (spreadIndex - 47) * 2;

  const protocolNum = `PROTOCOL #${scenario.id.toUpperCase()}`;

  return (
    <Spread
      pageNumLeft={pageNum}
      pageNumRight={pageNum + 1}
      leftHeader={t("scenario.header")}
      rightHeader={t("scenario.header")}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <span className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, letterSpacing: "0.2em", textTransform: "uppercase", display: "block", marginBottom: "1rem" }}>
            {protocolNum}
          </span>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem", marginBottom: "1.5rem" }}>{t(scenario.nameKey)}</h2>
          <p className="codex-serif">
            {lang === "ru"
              ? `Сценарий "${t(scenario.nameKey)}" предназначен для изучения базовых лабораторных операций в виртуальной среде jasScience. Следуйте инструкциям, наблюдайте за поведением системы и записывайте результаты.`
              : lang === "uz"
              ? `"${t(scenario.nameKey)}" stsenariysi jasScience virtual muhitida asosiy laboratoriya operatsiyalarini o'rganish uchun mo'ljallangan.`
              : `The "${t(scenario.nameKey)}" scenario is designed to study basic laboratory operations in the jasScience virtual environment.`}
          </p>

          {/* Mini lab setup SVGs */}
          <div style={{ display: "flex", gap: "0.75rem", justifyContent: "center", marginTop: "2rem", opacity: 0.7 }}>
            <EquipmentThumbnail type="beaker" size={70} />
            <EquipmentThumbnail type="hotplate" size={70} />
          </div>
          <FigureCaption>
            {lang === "ru" ? `Рис. — Установка для: ${t(scenario.nameKey)}` : `Fig. — Setup for: ${t(scenario.nameKey)}`}
          </FigureCaption>
        </div>
      }
      right={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <div style={{ marginBottom: "1.5rem" }}>
            <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: "0.5rem" }}>
              {lang === "ru" ? "ЦЕЛЬ ОПЫТА" : lang === "uz" ? "TAJRIBA MAQSADI" : "EXPERIMENT GOAL"}
            </p>
            <p className="codex-serif">
              {lang === "ru"
                ? "На рабочем поле появятся необходимые приборы и вещества, но соединения, расположение и последовательность действий останутся за вами."
                : lang === "uz"
                ? "Ish maydonida zarur asboblar va moddalar paydo bo'ladi, lekin ulanishlar, joylashtirish va harakatlar ketma-ketligi sizga bog'liq."
                : "The necessary equipment and substances will appear on the workspace, but connections, arrangement and sequence of actions remain up to you."}
            </p>
          </div>

          <div style={{ marginBottom: "1rem" }}>
            <p className="codex-mono" style={{ fontSize: "0.7rem", opacity: 0.4, textTransform: "uppercase", letterSpacing: "0.15em", marginBottom: "0.5rem" }}>
              {lang === "ru" ? "ЧТО НАБЛЮДАТЬ" : lang === "uz" ? "NIMA KUZATISH" : "WHAT TO OBSERVE"}
            </p>
            <p className="codex-serif" style={{ fontSize: "0.95rem" }}>
              {lang === "ru"
                ? "Следите за показаниями термометра, уровнем жидкости и состоянием вещества. Записывайте изменения в журнал."
                : lang === "uz"
                ? "Termometr ko'rsatkichlari, suyuqlik darajasi va modda holatini kuzating. O'zgarishlarni kundalikka yozing."
                : "Monitor thermometer readings, liquid level and substance state. Record changes in the journal."}
            </p>
          </div>

          <div style={{marginBottom:'1rem'}}>
            <p className="codex-mono" style={{fontSize:'.7rem',opacity:.4,letterSpacing:'.15em'}}>{lang === 'ru' ? 'ВИРТУАЛЬНЫЕ ЗАДАЧИ' : lang === 'uz' ? 'VIRTUAL VAZIFALAR' : 'VIRTUAL TASKS'}</p>
            {(lang === 'ru' ? ['Разместите указанные объекты на поле','Выберите совместимые порты','Запустите виртуальную модель','Зафиксируйте наблюдение в Inspector'] : lang === 'uz' ? ['Obyektlarni maydonga joylashtiring','Mos portlarni tanlang','Virtual modelni ishga tushiring','Inspector kuzatuvini qayd eting'] : ['Place the listed objects on the field','Choose compatible virtual ports','Run the virtual model','Record the Inspector observation']).map((task,index)=><div key={task} style={{display:'flex',gap:'.65rem',padding:'.3rem 0',borderBottom:'1px dotted var(--book-border)'}}><span className="codex-mono" style={{fontSize:'.65rem',opacity:.45}}>0{index+1}</span><span className="codex-serif" style={{fontSize:'.82rem'}}>{task}</span></div>)}
          </div>

          <div
            className="codex-handwriting"
            style={{ marginTop: "1rem", color: "var(--book-pencil)", fontSize: "1.1rem", transform: "rotate(-2deg)", opacity: 0.6 }}
          >
            {lang === "ru" ? "повторяемость — ключ к проверке гипотезы" : lang === "uz" ? "takrorlanish — gipotezani tekshirish kaliti" : "repeatability is the key to testing a hypothesis"}
          </div>

          <div style={{ marginTop: "auto", display: "flex", gap: "1rem", flexWrap: "wrap" }}>
            {navigate && (
              <button className="action-btn codex-mono" onClick={() => navigate(32)}>
                {backLabel}
              </button>
            )}
            <button
              className="action-btn open-in-lab-btn codex-mono"
              onClick={() => onOpenLab?.({ type: "scenario", id: scenario.id })}
            >
              <ExternalLink size={13} />
              &nbsp;{openLab}
            </button>
          </div>
        </div>
      }
    />
  );
}

// ─── 14. PRACTICE SPREADS ─────────────────────────────────────────────────────

export function PracticeSpread1({ navigate }: { navigate?: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={105}
      pageNumRight={106}
      leftHeader={lang === "ru" ? "ГЛАВА IV · ПРАКТИКА" : lang === "uz" ? "IV BOB · AMALIYOT" : "CHAPTER IV · PRACTICE"}
      rightHeader={lang === "ru" ? "ПРАКТИКА 01" : lang === "uz" ? "AMALIYOT 01" : "PRACTICE 01"}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "0.5rem" }}>
          <ChapterHeader
            roman="IV"
            title={lang === "ru" ? "Практика" : lang === "uz" ? "Amaliyot" : "Practice"}
            subtitle={lang === "ru" ? "Виртуальные эксперименты" : lang === "uz" ? "Virtual tajribalar" : "Virtual Experiments"}
            epigraph={lang === "ru" ? "«Теория без практики — это план без действия.»" : lang === "uz" ? "«Amaliyotsiz nazariya — harakatsiz reja.»" : '"Theory without practice is a plan without action."'}
          />
          <p className="codex-serif" style={{ marginTop: "1.5rem" }}>
            {lang === "ru"
              ? "В этой главе собраны практические задания, выполняемые в виртуальной лаборатории jasScience. Все опыты безопасны и предназначены только для симуляции."
              : lang === "uz"
              ? "Ushbu bobda jasScience virtual laboratoriyasida bajariladigan amaliy topshiriqlar to'plangan."
              : "This chapter contains practical exercises performed in the jasScience virtual laboratory. All experiments are safe and designed for simulation only."}
          </p>
        </div>
      }
      right={
        <div className="page-layout-practice">
          <PracticeQuiz
            number={t("practice.p01_number")}
            title={t("practice.p01_title")}
            goal={t("practice.p01_goal")}
            equipmentHint={t("practice.p01_equipment")}
            question={t("practice.p01_question")}
            answers={[
              t("practice.p01_a1"),
              t("practice.p01_a2"),
              t("practice.p01_a3"),
              t("practice.p01_a4"),
            ]}
            correctIndex={0}
            feedbackCorrect={t("practice.p01_feedback_correct")}
            feedbackWrong={t("practice.p01_feedback_wrong")}
            onNext={() => navigate?.(40)}
            illustrationEquipmentId="thermometer"
          />
        </div>
      }
    />
  );
}

export function PracticeSpread2({ navigate }: { navigate?: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={107}
      pageNumRight={108}
      leftHeader={lang === "ru" ? "ПРАКТИКА 02" : lang === "uz" ? "AMALIYOT 02" : "PRACTICE 02"}
      rightHeader={lang === "ru" ? "ПРАКТИКА 02" : lang === "uz" ? "AMALIYOT 02" : "PRACTICE 02"}
      left={
        <div className="page-layout-practice">
          <PracticeQuiz
            number={t("practice.p02_number")}
            title={t("practice.p02_title")}
            question={t("practice.p02_question")}
            answers={[
              t("practice.p02_a1"),
              t("practice.p02_a2"),
              t("practice.p02_a3"),
              t("practice.p02_a4"),
            ]}
            correctIndex={2}
            feedbackCorrect={t("practice.p02_feedback_correct")}
            feedbackWrong={t("practice.p02_feedback_wrong")}
            onNext={() => navigate?.(41)}
            illustrationEquipmentId="burette"
          />
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "2rem", alignItems: "center" }}>
          {/* Burette illustration */}
          <EquipmentThumbnail type="burette" size={180} />
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 4.2 — Бюретка: точный инструмент для титрования."
              : lang === "uz"
              ? "Rasm 4.2 — Byuretka: titratsiya uchun aniq asbob."
              : "Fig. 4.2 — Burette: precision instrument for titration."}
          </FigureCaption>
          <div
            className="codex-handwriting"
            style={{ marginTop: "2rem", fontSize: "1rem", color: "var(--book-pencil)", transform: "rotate(-2deg)", opacity: 0.6 }}
          >
            {lang === "ru" ? "точность до 0.1 мл" : lang === "uz" ? "0.1 ml gacha aniqlik" : "precision to 0.1 ml"}
          </div>
        </div>
      }
    />
  );
}

export function PracticeSpread3({ navigate }: { navigate?: (page: number) => void }) {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={109}
      pageNumRight={110}
      leftHeader={lang === "ru" ? "ПРАКТИКА 03" : lang === "uz" ? "AMALIYOT 03" : "PRACTICE 03"}
      rightHeader={lang === "ru" ? "ПРАКТИКА 03" : lang === "uz" ? "AMALIYOT 03" : "PRACTICE 03"}
      left={
        <div className="page-layout-practice">
          <PracticeQuiz
            number={t("practice.p03_number")}
            title={t("practice.p03_title")}
            question={t("practice.p03_question")}
            answers={[
              t("practice.p03_a1"),
              t("practice.p03_a2"),
              t("practice.p03_a3"),
              t("practice.p03_a4"),
            ]}
            correctIndex={1}
            feedbackCorrect={t("practice.p03_feedback_correct")}
            feedbackWrong={t("practice.p03_feedback_wrong")}
            onNext={() => navigate?.(42)}
          />
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "2rem", alignItems: "center" }}>
          {/* Water illustration */}
          <Img
            src="/journal/substances/water.png"
            height={180}
            className="textured-image"
            alt="Water"
            width="80%"
          />
          <div
            className="formula-display codex-mono"
            style={{ fontSize: "2.5rem", textAlign: "center", marginTop: "1rem" }}
          >
            H<sub>2</sub>O
          </div>
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 4.3 — Вода, H₂O. Плотность: 997 кг/м³. Кипение: 100°C."
              : lang === "uz"
              ? "Rasm 4.3 — Suv, H₂O. Zichligi: 997 kg/m³. Qaynash: 100°C."
              : "Fig. 4.3 — Water, H₂O. Density: 997 kg/m³. Boiling: 100°C."}
          </FigureCaption>
        </div>
      }
    />
  );
}

export function PracticeSpread4() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  const steps =
    lang === "ru"
      ? [
          "Разместить нагревательную плиту",
          "Поставить стакан на плиту",
          "Добавить термометр в стакан",
          "Соединить порты нагрева",
          "Запустить симуляцию",
        ]
      : lang === "uz"
      ? [
          "Isitish plitasini joylashtiring",
          "Stakanni plitaga qo'ying",
          "Termometrni stakanga qo'shing",
          "Isitish portlarini ulang",
          "Simulyatsiyani ishga tushiring",
        ]
      : [
          "Place the hot plate",
          "Place the beaker on the plate",
          "Add thermometer to the beaker",
          "Connect the heating ports",
          "Start the simulation",
        ];

  return (
    <Spread
      pageNumLeft={111}
      pageNumRight={112}
      leftHeader={lang === "ru" ? "ПРАКТИКА 04" : lang === "uz" ? "AMALIYOT 04" : "PRACTICE 04"}
      rightHeader={lang === "ru" ? "ПРАКТИКА 04" : lang === "uz" ? "AMALIYOT 04" : "PRACTICE 04"}
      left={
        <div className="page-layout-practice">
          <PracticeStepSorter
            number={t("practice.p04_number")}
            title={t("practice.p04_title")}
            question={t("practice.p04_question")}
            steps={steps}
            correctOrder={[0, 1, 2, 3, 4]}
            feedback={t("practice.p04_feedback")}
          />
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "2rem", alignItems: "center" }}>
          {/* Setup assembly illustration */}
          <div style={{ display: "flex", flexDirection: "column", gap: "0.5rem", alignItems: "center" }}>
            <EquipmentThumbnail type="hotplate" size={80} />
            <span className="codex-mono" style={{ fontSize: "0.65rem", opacity: 0.4 }}>↓</span>
            <EquipmentThumbnail type="beaker" size={80} />
            <span className="codex-mono" style={{ fontSize: "0.65rem", opacity: 0.4 }}>↕</span>
            <EquipmentThumbnail type="thermometer" size={80} />
          </div>
          <FigureCaption>
            {lang === "ru"
              ? "Рис. 4.4 — Порядок сборки базовой установки нагрева."
              : lang === "uz"
              ? "Rasm 4.4 — Asosiy isitish qurilmasini yig'ish tartibi."
              : "Fig. 4.4 — Assembly order for basic heating setup."}
          </FigureCaption>
          <div
            className="codex-handwriting"
            style={{ marginTop: "2rem", fontSize: "1rem", color: "var(--book-pencil)", transform: "rotate(-2deg)", opacity: 0.55 }}
          >
            {lang === "ru" ? "сначала основание →" : lang === "uz" ? "avval asos →" : "base first →"}
          </div>
        </div>
      }
    />
  );
}

// ─── 15. TORN PAGE SPREADS ────────────────────────────────────────────────────

export function TornSpread1() {
  const lang = usePathname().split("/")[1] || "en";

  const normalText =
    lang === "ru"
      ? "В следующем опыте нужно проверить, как изменится концентрация раствора при повышении температуры выше критической отметки. Если моя гипотеза верна, то реакция должна..."
      : lang === "uz"
      ? "Keyingi tajribada harorat kritik chegaradan oshganda eritma kontsentratsiyasi qanday o'zgarishini tekshirish kerak. Agar mening gipotezam to'g'ri bo'lsa, reaktsiya..."
      : "In the next experiment, I need to check how the solution concentration changes when the temperature exceeds the critical mark. If my hypothesis is correct, then the reaction should...";

  const handwriting1 = lang === "ru" ? "результат странный" : lang === "uz" ? "natija g'alati" : "result is strange";
  const handwriting2 = lang === "ru" ? "повторить" : lang === "uz" ? "takrorlash" : "repeat";
  const handwriting3 = lang === "ru" ? "проверить соединение" : lang === "uz" ? "ulanishni tekshirish" : "check the connection";

  return (
    <TornPageSpread
      leftContent={
        <div style={{ paddingTop: "3rem", position: "relative" }}>
          <span className="academy-running-header" style={{ fontSize: "9px", opacity: 0.4, textTransform: "uppercase", letterSpacing: "0.15em" }}>
            {lang === "ru" ? "НЕЗАВЕРШЁННЫЙ ОПЫТ" : lang === "uz" ? "TUGALLANMAGAN TAJRIBA" : "UNFINISHED EXPERIMENT"}
          </span>

          <p className="codex-serif" style={{ marginTop: "2rem", fontSize: "1.05rem", lineHeight: 1.7 }}>
            {normalText}
          </p>

          {/* Rushed handwriting starts here */}
          <div style={{ marginTop: "2rem" }}>
            <div
              className="codex-handwriting"
              style={{
                fontSize: "1.2rem",
                color: "var(--book-annotation)",
                transform: "rotate(-3deg)",
                marginBottom: "0.5rem",
                display: "block",
              }}
            >
              {handwriting1}
            </div>
            <div
              className="codex-handwriting"
              style={{ fontSize: "1.1rem", color: "var(--book-pencil)", transform: "rotate(1deg)", display: "block", marginBottom: "0.5rem" }}
            >
              {handwriting2}
            </div>
            <div
              className="codex-handwriting"
              style={{ fontSize: "1rem", color: "var(--book-pencil)", transform: "rotate(-1deg)", display: "block" }}
            >
              {handwriting3}
            </div>
            <div
              className="codex-handwriting"
              style={{ fontSize: "1.3rem", color: "var(--book-annotation)", marginTop: "0.5rem", display: "block", fontWeight: "700" }}
            >
              ???
            </div>
          </div>
        </div>
      }
      rightContent={
        <div style={{ paddingTop: "2rem", position: "relative" }}>
          <p className="codex-serif" style={{ fontSize: "1.05rem", lineHeight: 1.7 }}>
            {lang === "ru"
              ? "Если температура превысит критическую отметку, то реакция должна..."
              : lang === "uz"
              ? "Agar harorat kritik chegara oshib ketsa, reaktsiya..."
              : "If the temperature exceeds the critical mark, then the reaction should..."}
          </p>

          {/* Increasingly rushed handwriting */}
          <div style={{ marginTop: "2rem" }}>
            <div
              className="codex-handwriting"
              style={{
                fontSize: "1.3rem",
                color: "var(--book-text)",
                transform: "rotate(-4deg)",
                marginBottom: "1rem",
                lineHeight: 1.4,
              }}
            >
              {lang === "ru"
                ? "Если моя гипотеза верна, то при следующем..."
                : lang === "uz"
                ? "Agar mening gipotezam to'g'ri bo'lsa, keyingi..."
                : "If my hypothesis is correct, then with the next..."}
            </div>
          </div>

          {/* Unfinished diagram */}
          <svg width="180" height="120" viewBox="0 0 180 120" style={{ marginTop: "1.5rem", opacity: 0.5 }}>
            <path d="M20,100 L60,60 Q90,30 110,50" stroke="var(--book-text)" strokeWidth="2" fill="none" strokeDasharray="5,3" />
            <circle cx="20" cy="100" r="4" fill="var(--book-text)" />
            <circle cx="60" cy="60" r="4" fill="var(--book-text)" />
          </svg>

          <FigureCaption>
            {lang === "ru" ? "[незавершённый график]" : lang === "uz" ? "[tugallanmagan grafik]" : "[unfinished graph]"}
          </FigureCaption>
        </div>
      }
    />
  );
}

export function TornSpread2() {
  const lang = usePathname().split("/")[1] || "en";

  const finalFragment =
    lang === "ru"
      ? "...не забудь проверить..."
      : lang === "uz"
      ? "...tekshirishni unutma..."
      : "...don't forget to check...";

  return (
    <TornPageFinalSpread
      finalText={
        <div style={{ paddingTop: "4rem" }}>
          <div
            className="codex-handwriting"
            style={{
              fontSize: "1.4rem",
              color: "var(--book-text)",
              transform: "rotate(-5deg)",
              opacity: 0.6,
              lineHeight: 1.5,
            }}
          >
            {finalFragment}
          </div>
        </div>
      }
    />
  );
}

// ─── Legacy / compatibility exports ──────────────────────────────────────────

export function LanguageSpread({ onSelect }: { onSelect: (lang: string) => void }) {
  return null; // replaced by Cover language selector
}

export function TitleSpread() {
  return <OwnerSpread />;
}

export function ContentsSpread({ navigate }: { navigate: (page: number) => void }) {
  return <ContentsSpreadA navigate={navigate} />;
}

export function ProseSpread() {
  return <IntroductionSpread1 />;
}

export function SandboxSpreadLegacy() {
  return <SandboxSpread />;
}

export function EquipmentSpread({ onOpenLab, navigate }: { onOpenLab?: (ctx: CodexLabContext) => void; navigate: (page: number) => void }) {
  return <EquipmentIndexSpread navigate={navigate} onOpenLab={onOpenLab} />;
}

export function SubstanceSpread({ navigate, onOpenLab }: { navigate?: (page: number) => void; onOpenLab?: (ctx: CodexLabContext) => void }) {
  return <SubstanceIndexSpread navigate={navigate || (() => {})} />;
}

export function PhysicsSpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={113}
      pageNumRight={114}
      leftHeader={t("physics.header")}
      rightHeader={t("physics.title")}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem" }}>{t("physics.name")}</h2>
          <p className="codex-serif">{t("physics.p1")}</p>
          <p className="codex-serif">{t("physics.p2")}</p>
        </div>
      }
      right={
        <div className="page-layout-dense" style={{ paddingTop: "1.5rem" }}>
          <Img src="/journal/physics/heat-transfer.png" height={160} className="textured-image" alt="Heat transfer" />
          <Img src="/journal/physics/condenser-cutaway.png" height={130} className="textured-image" alt="Condenser cutaway" style={{ marginTop: "1rem" } as React.CSSProperties} />
          <FigureCaption>{t("physics.fig")}</FigureCaption>
        </div>
      }
    />
  );
}

export function SafetySpread() {
  const t = useTranslations("book");
  const lang = usePathname().split("/")[1] || "en";

  return (
    <Spread
      pageNumLeft={115}
      pageNumRight={116}
      leftHeader={t("safety.header")}
      rightHeader={t("safety.title")}
      left={
        <div className="page-layout-balanced" style={{ paddingTop: "1.5rem" }}>
          <h2 className="codex-cinzel" style={{ fontSize: "1.5rem" }}>{t("safety.header")}</h2>
          <p className="codex-serif" style={{marginTop:'1.4rem',lineHeight:1.7}}>{lang === 'ru' ? 'Безопасность в jasScience начинается с чтения состояния системы. Цвет соединения, предупреждение Inspector и границы параметров помогают понять, почему виртуальная установка требует осмысленной последовательности действий. Здесь ошибка остаётся частью модели: она останавливает сценарий, объясняет конфликт и предлагает проверить схему.' : lang === 'uz' ? 'jasScience xavfsizligi tizim holatini o‘qishdan boshlanadi. Ulanish rangi, Inspector ogohlantirishi va parametr chegaralari virtual qurilma nima uchun ongli harakatlar ketma-ketligini talab qilishini ko‘rsatadi.' : 'Safety in jasScience begins with reading the state of the system. Connection color, Inspector warnings and parameter limits explain why a virtual setup needs a deliberate sequence of actions. Here an error remains part of the model: it pauses the scenario, explains the conflict and invites the learner to inspect the diagram.'}</p>
          <div style={{marginTop:'1.5rem'}}>{(lang === 'ru' ? ['Проверить совместимость виртуальных портов','Сравнить значение с допустимым диапазоном','Остановить модель при критическом предупреждении'] : lang === 'uz' ? ['Virtual portlar mosligini tekshirish','Qiymatni ruxsat etilgan oraliq bilan solishtirish','Muhim ogohlantirishda modelni to‘xtatish'] : ['Check virtual-port compatibility','Compare the value with its modeled range','Pause the model on a critical warning']).map((item,index)=><div key={item} style={{display:'flex',gap:'.7rem',padding:'.45rem 0',borderBottom:'1px dotted var(--book-border)'}}><span className="codex-mono" style={{fontSize:'.65rem',opacity:.45}}>0{index+1}</span><span className="codex-serif" style={{fontSize:'.88rem'}}>{item}</span></div>)}</div>
          <div className="safety-log-note">
            <p className="codex-mono">ПОРЯДОК ЗАПИСИ</p>
            <p className="codex-serif">{lang === 'ru' ? 'Перед запуском отметьте исходные значения в Inspector. После каждого изменения сохраняйте только наблюдаемый результат: состояние порта, температуру, давление и появившееся предупреждение. Так модель остаётся понятной даже после неудачной попытки.' : lang === 'uz' ? 'Ishga tushirishdan oldin Inspector dagi boshlang‘ich qiymatlarni qayd eting. Har bir o‘zgarishdan keyin faqat kuzatilgan natijani yozing: port holati, harorat, bosim va paydo bo‘lgan ogohlantirish. Shunda model muvaffaqiyatsiz urinishdan keyin ham tushunarli bo‘lib qoladi.' : 'Before starting, record the initial values in the Inspector. After every change, keep only the observable result: port state, temperature, pressure and any warning. This keeps the model readable even after an unsuccessful attempt.'}</p>
          </div>
          <MarginNote position="left" color="red">CRITICAL!</MarginNote>
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "1.5rem" }}>
          <InfoBox type="red" title={lang === "ru" ? "ОСТОРОЖНО" : lang === "uz" ? "EHTIYOT BO'LING" : "CAUTION"}>
            <p className="codex-serif" style={{ margin: 0, fontSize: "0.9rem" }}>{t("safety.p1")}</p>
          </InfoBox>
          <p className="codex-serif" style={{ marginTop: "1.5rem" }}>{t("safety.p2")}</p>
          <ul style={{ marginTop: "1rem", listStyle: "none", padding: 0 }}>
            {["rule1", "rule2", "rule3"].map((r) => (
              <li key={r} className="codex-serif" style={{ padding: "0.4rem 0", borderBottom: "1px dotted var(--book-border)", fontSize: "0.9rem" }}>
                {t(`safety.${r}`)}
              </li>
            ))}
          </ul>
        </div>
      }
    />
  );
}

export function ConclusionSpread() {
  const lang = usePathname().split("/")[1] || "en";
  const fieldNotes = lang === "ru" ? "ПОЛЕВЫЕ ЗАПИСИ" : lang === "uz" ? "DALA QAYDLARI" : "FIELD NOTES";

  return (
    <Spread
      pageNumLeft={117}
      pageNumRight={118}
      leftHeader={fieldNotes}
      rightHeader={fieldNotes}
      left={
        <div className="page-layout-textbook" style={{ paddingTop: "1.5rem" }}>
          <p className="codex-serif" style={{ lineHeight: 1.75 }}>
            {lang === "ru"
              ? "Чем дольше работаешь в лаборатории, тем меньше эксперимент похож на проверку готового ответа. Он становится разговором с системой, которая всегда отвечает, но не всегда так, как ждёшь."
              : lang === "uz"
              ? "Laboratoriyada qancha ko'p ishlasang, tajriba tayyor javobni tekshirishga shunchalik kam o'xshaydi. U har doim javob beradigan tizim bilan suhbatga aylanadi."
              : "The longer you work in the laboratory, the less an experiment resembles checking a ready answer. It becomes a conversation with a system that always responds, but not always as expected."}
          </p>
          <p className="codex-serif" style={{ lineHeight: 1.75, marginTop: "1rem" }}>
            {lang === "ru"
              ? "Запись нужна не для того, чтобы сделать результат красивее. Она сохраняет условия, сомнения и мелкие отклонения — всё то, что память быстро превращает в удобную версию событий. Число без контекста почти ничего не говорит; наблюдение становится знанием только тогда, когда его можно сравнить и повторить."
              : lang === "uz"
              ? "Qayd natijani chiroyli qilish uchun emas. U shartlarni, shubhalarni va kichik og‘ishlarni — xotira tezda qulay voqeaga aylantiradigan narsalarni saqlaydi. Kontekstsiz son deyarli hech narsa demaydi; kuzatuv faqat solishtirish va takrorlash mumkin bo‘lgandagina bilimga aylanadi."
              : "A record is not meant to make a result look cleaner. It preserves conditions, doubts and small deviations—the details memory quickly edits into a convenient story. A number without context says very little; an observation becomes knowledge only when it can be compared and repeated."}
          </p>
          <p className="codex-serif" style={{ lineHeight: 1.75, marginTop: "1rem" }}>
            {lang === "ru"
              ? "Каждый опыт — это маленький разрыв между тем, что ты предполагал, и тем, что произошло. Именно этот разрыв и называется наблюдением."
              : lang === "uz"
              ? "Har bir tajriba — siz kutgan narsa va sodir bo'lgan narsa o'rtasidagi kichik bo'shliq. Aynan shu bo'shliq kuzatish deb ataladi."
              : "Every experiment is a small gap between what you assumed and what happened. That gap is precisely what is called observation."}
          </p>
          <div className="codex-handwriting" style={{marginTop:"1.2rem",color:"var(--book-annotation)",transform:"rotate(-2deg)"}}>{lang === "ru" ? "не исправлять странные данные до повторной проверки" : lang === "uz" ? "g‘alati ma’lumotni qayta tekshirmasdan tuzatmaslik" : "do not correct strange data before checking it again"}</div>
        </div>
      }
      right={
        <div className="page-layout-balanced" style={{ paddingTop: "2rem", position: "relative" }}>
          {/* Narrative trailing off */}
          <p className="codex-handwriting" style={{ fontSize: "1.1rem", lineHeight: 1.85, opacity: 0.9, color: "var(--book-text)" }}>
            {lang === "ru"
              ? "Я снова поставил колбу на стол. Термометр показывал 23,4°C. Я проверил соединение, обнулил таймер и повторил наблюдение с теми же начальными условиями. Всё выглядело совершенно обычно, но на двадцать третьей секунде линия графика едва заметно изменила наклон. Возможно, это всего лишь погрешность интерфейса. Возможно, нет. Я отметил точку красным и решил ничего не объяснять раньше времени. Если изменение повторится ещё раз, тогда, возможно, стоит проверить—"
              : lang === "uz"
              ? "Men kolbani yana stolga qo‘ydim. Termometr 23,4°C ni ko‘rsatardi. Ulanishni tekshirdim, taymerni nolga tushirdim va kuzatuvni ayni boshlang‘ich sharoitlarda takrorladim. Hammasi odatdagidek ko‘rinardi, ammo yigirma uchinchi soniyada grafik chizig‘ining qiyaligi zo‘rg‘a seziladigan darajada o‘zgardi. Balki bu interfeys xatosidir. Balki yo‘q. Nuqtani qizil bilan belgiladim va shoshilib izoh bermaslikka qaror qildim. Agar o‘zgarish yana takrorlansa, ehtimol tekshirish kerak bo‘lgan narsa—"
              : "I placed the flask on the table again. The thermometer read 23.4°C. I checked the connection, reset the timer and repeated the observation under the same initial conditions. Everything looked ordinary, but at the twenty-third second the graph changed slope almost imperceptibly. It may be an interface error. It may not. I marked the point in red and decided not to explain it too early. If the change repeats once more, then perhaps it is worth checking—"}
          </p>

          <Img src="/journal/final/torn-ink-edge-transparent.png" height={90} className="textured-image" alt="" style={{ position: "absolute", bottom: "1.5rem", left: "0", width: "100%", objectFit: "contain", objectPosition: "bottom" }} />

          {/* Ink splatter at bottom */}
          <div className="ink-splatter" style={{ position: "absolute", bottom: "4rem", right: "3rem", width: "35px", height: "35px" }} />

          {/* Unfinished graph — visual incomplete feeling */}
          <div className="unfinished-diagram" style={{ marginTop: "2rem", opacity: 0.45 }}>
            <svg width="180" height="110" viewBox="0 0 180 110">
              <path d="M 20,95 L 60,55 L 90,70 L 120,30" stroke="var(--book-text)" strokeWidth="1.5" fill="none" strokeDasharray="5,4" />
              <circle cx="20" cy="95" r="3" fill="var(--book-text)" />
              <circle cx="60" cy="55" r="3" fill="var(--book-text)" />
              <circle cx="90" cy="70" r="3" fill="var(--book-text)" />
              <circle cx="120" cy="30" r="3" fill="var(--book-text)" />
              {/* axes */}
              <line x1="15" y1="100" x2="150" y2="100" stroke="var(--book-text)" strokeWidth="1" opacity="0.4" />
              <line x1="15" y1="100" x2="15" y2="10" stroke="var(--book-text)" strokeWidth="1" opacity="0.4" />
            </svg>
          </div>
        </div>
      }
    />
  );
}

export function EmptyFinalSpread() {
  return null;
}
