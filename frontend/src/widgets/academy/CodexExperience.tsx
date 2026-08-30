"use client";

import { useState, useEffect, useRef, useMemo, useSyncExternalStore } from "react";
import { useTheme } from "next-themes";
import { Atom, Beaker, BookOpen, ClipboardCheck, FlaskConical, History, Microscope, Wrench } from "lucide-react";
import { BookFlip } from "./BookFlip";
import {
  Cover,
  OwnerSpread,
  ContentsSpreadA,
  ContentsSpreadB,
  IntroductionSpread1,
  IntroductionSpread2,
  ChemistrySpread1,
  ChemistrySpread2,
  HistorySpread,
  LavoisierSpread,
  MendeleevSpread,
  CurieSpread,
  BoyleSpread,
  JabirSpread,
  SandboxSpread,
  SandboxInterfaceSpread,
  EquipmentIndexSpread,
  EquipmentDetailSpread,
  SubstanceIndexSpread,
  SubstanceDetailSpread,
  ScenarioIndexSpread,
  ScenarioDetailSpread,
  PracticeSpread1,
  PracticeSpread2,
  PracticeSpread3,
  PracticeSpread4,
  TornSpread1,
  TornSpread2,
  SafetySpread,
  ConclusionSpread,
} from "./CodexSpreads";
import type { CodexLabContext } from "./CodexModal";
import { equipmentDefinitions } from "@/entities/codex/model/codexDefinitions";
import { materialDefinitions } from "@/entities/material/model/materialDefinitions";
import { scenarioDefinitions } from "@/entities/experiment/model/scenarioDefinitions";
import "./book.css";

const BOOK_WIDTH = 1120;
const BOOK_HEIGHT = 680;
const STORAGE_PAGE_KEY = "jasscience-book-page";

const equipmentAliases: Record<string, string> = {
  beaker50: 'beaker', beaker100: 'beaker', beaker250: 'beaker', beaker500: 'beaker',
  roundflask: 'roundflask', distillation_flask: 'roundflask',
  graduated_cylinder: 'beaker', volumetric_flask: 'erlenmeyer',
  magnetic_stirrer: 'hotplate', clampstand: 'clampstand', ringstand: 'clampstand',
};
const materialAliases: Record<string, string> = {
  H2O: 'water', 'H₂O': 'water', NaCl: 'salt', ethanol: 'ethanol',
  CuSO4: 'copper_sulfate', 'CuSO₄': 'copper_sulfate',
  H2SO4: 'sulfuric_acid', HCl: 'hydrochloric_acid', H2O2: 'hydrogen_peroxide',
  'CuSO4(aq)': 'copper_sulfate_solution', KMnO4: 'potassium_permanganate',
  'KMnO4(aq)': 'potassium_permanganate_solution', NaOH: 'sodium_hydroxide',
  Na2CO3: 'sodium_carbonate', Zn: 'zinc', Cu: 'copper', Au: 'gold', S: 'sulfur',
  pH: 'ph_indicator',
};

export function getCodexPageForEntity(context?: CodexLabContext) {
  if (!context?.type || !context.id) return 0;
  if (context.type === 'equipment') {
    const entityId = equipmentAliases[context.id] ?? context.id;
    const index = equipmentDefinitions.findIndex((equipment) => equipment.id === entityId);
    return index >= 0 ? 17 + index : 16;
  }
  if (context.type === 'material') {
    const entityId = materialAliases[context.id] ?? context.id;
    const index = materialDefinitions.findIndex((material) => material.id === entityId);
    return index >= 0 ? 29 + index : 28;
  }
  if (context.type === 'scenario') {
    const index = scenarioDefinitions.findIndex((scenario) => scenario.id === context.id);
    return index >= 0 ? 34 + index : 33;
  }
  return 0;
}

export default function CodexExperience({
  onOpenLab,
  onClose,
  initialContext,
}: {
  onOpenLab?: (context: CodexLabContext) => void;
  onClose?: () => void;
  initialContext?: CodexLabContext;
}) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [scale, setScale] = useState(1);

  // ── Theme — persisted to localStorage ────────────────────────────────────
  const { theme: globalTheme, resolvedTheme, setTheme: setGlobalTheme } = useTheme();
  const themeMounted = useSyncExternalStore(() => () => undefined, () => true, () => false);
  const theme: "dark" | "light" = themeMounted && (resolvedTheme || globalTheme) === "light" ? "light" : "dark";
  const handleThemeChange = (newTheme: "dark" | "light") => {
    setGlobalTheme(newTheme);
    window.localStorage.setItem("chemistry-theme-preference", newTheme);
    window.localStorage.setItem("ai-lab-theme", newTheme);
    window.dispatchEvent(new CustomEvent("chemistry-theme-change", { detail: newTheme }));
  };

  // ── Current page ─────────────────────────────────────────────────────────
  const [currentPage, setCurrentPage] = useState(() => getCodexPageForEntity(initialContext));

  const navigate = (page: number) => {
    setCurrentPage(page);
    window.localStorage.setItem(STORAGE_PAGE_KEY, String(page));
  };

  // ── Scale to viewport ─────────────────────────────────────────────────────
  useEffect(() => {
    const handleResize = () => {
      const availableWidth = window.innerWidth - 64;
      const availableHeight = window.innerHeight - 64;
      const newScale = Math.min(
        availableWidth / BOOK_WIDTH,
        availableHeight / BOOK_HEIGHT,
        1
      );
      setScale(newScale);
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  // ── Page definitions ─────────────────────────────────────────────────────
  // Spread index   Content
  // 0              Cover
  // 1              Owner/Opening
  // 2              Contents A (003-004)
  // 3              Contents B (005-006)
  // 4              Introduction 1 (007-008)
  // 5              Introduction 2 (009-010)
  // 6              Chemistry 1 (011-012)
  // 7              Chemistry 2 (013-014)
  // 8              History (013-014 offset)
  // 9              Lavoisier (015-016)
  // 10             Mendeleev (017-018)
  // 11             Curie (019-020)
  // 12             Boyle (021-022)
  // 13             Jabir (023-024)
  // 14             Sandbox (027-028)
  // 15             SandboxInterface (029-030)
  // 16             EquipmentIndex (031-032)
  // 17..27         EquipmentDetail (11 items)
  // 28             SubstanceIndex (055-056)
  // 29..32         SubstanceDetail (4 items)
  // 33             ScenarioIndex (065-066)
  // 34..38         ScenarioDetail (5 items)
  // 38             Practice 1 (077-078)  ← [actual 38]
  // 39             Practice 2 (079-080)
  // 40             Practice 3 (081-082)
  // 41             Practice 4 (083-084)
  // 42             Torn 1 (085-086)
  // 43             Torn 2 (087-088)
  // 44             Physics
  // 45             Safety
  // 46             Conclusion

  const bookmarks = [
    { label: "Intro", page: 4,  color: "#6B2A2A", icon: BookOpen },
    { label: "Chem",  page: 6,  color: "#5B3E8A", icon: FlaskConical },
    { label: "Hist",  page: 8,  color: "#2D5A8E", icon: History },
    { label: "Lab",   page: 14, color: "#2D6B5A", icon: Microscope },
    { label: "Equip", page: 16, color: "#1A6B6B", icon: Wrench },
    { label: "Mat",   page: 28, color: "#3A6B3A", icon: Beaker },
    { label: "Exp",   page: 33, color: "#6B5A2A", icon: Atom },
    { label: "Pract", page: 39, color: "#7A2020", icon: ClipboardCheck },
  ];

  const pages = useMemo(
    () => [
      // 0 — Cover
      {
        id: "cover",
        render: () => (
          <Cover
            onOpen={() => navigate(1)}
            theme={theme}
            onThemeChange={handleThemeChange}
          />
        ),
      },
      // 1 — Owner
      { id: "owner", render: () => <OwnerSpread /> },
      // 2 — Contents A
      { id: "contents-a", render: () => <ContentsSpreadA navigate={navigate} /> },
      // 3 — Contents B
      { id: "contents-b", render: () => <ContentsSpreadB navigate={navigate} /> },
      // 4 — Introduction 1
      { id: "intro-1", render: () => <IntroductionSpread1 /> },
      // 5 — Introduction 2
      { id: "intro-2", render: () => <IntroductionSpread2 /> },
      // 6 — Chemistry 1
      { id: "chemistry-1", render: () => <ChemistrySpread1 /> },
      // 7 — Chemistry 2
      { id: "chemistry-2", render: () => <ChemistrySpread2 /> },
      // 8 — History
      { id: "history", render: () => <HistorySpread /> },
      // 9 — Lavoisier
      { id: "lavoisier", render: () => <LavoisierSpread /> },
      // 10 — Mendeleev
      { id: "mendeleev", render: () => <MendeleevSpread /> },
      // 11 — Curie
      { id: "curie", render: () => <CurieSpread /> },
      // 12 — Boyle
      { id: "boyle", render: () => <BoyleSpread /> },
      // 13 — Jabir
      { id: "jabir", render: () => <JabirSpread /> },
      // 14 — Sandbox
      { id: "sandbox", render: () => <SandboxSpread onOpenLab={onOpenLab} /> },
      // 15 — Sandbox Interface
      {
        id: "sandbox-interface",
        render: () => <SandboxInterfaceSpread onOpenLab={onOpenLab} />,
      },
      // 16 — Equipment Index
      {
        id: "equipment-index",
        render: () => (
          <EquipmentIndexSpread navigate={navigate} onOpenLab={onOpenLab} />
        ),
      },
      // 17..27 — Equipment Details (11 items)
      ...equipmentDefinitions.map((eq, i) => ({
        id: `equipment-detail-${eq.id}`,
        render: () => (
          <EquipmentDetailSpread
            equipment={eq}
            onOpenLab={onOpenLab}
            spreadIndex={17 + i}
            navigate={navigate}
          />
        ),
      })),
      // 28 — Substance Index
      {
        id: "substance-index",
        render: () => <SubstanceIndexSpread navigate={navigate} />,
      },
      // 29..45 — Substance Details (full Sandbox material catalogue)
      ...materialDefinitions.map((mat, i) => ({
        id: `substance-detail-${mat.id}`,
        render: () => (
          <SubstanceDetailSpread
            material={mat}
            onOpenLab={onOpenLab}
            spreadIndex={29 + i}
            navigate={navigate}
          />
        ),
      })),
      // 46 — Scenario Index (after the expanded substance catalogue)
      {
        id: "scenario-index",
        render: () => (
          <ScenarioIndexSpread navigate={navigate} onOpenLab={onOpenLab} />
        ),
      },
      // 34..38 — Scenario Details (5 items)
      ...scenarioDefinitions.map((sc, i) => ({
        id: `scenario-detail-${sc.id}`,
        render: () => (
          <ScenarioDetailSpread
            scenario={sc}
            onOpenLab={onOpenLab}
            spreadIndex={47 + i}
            navigate={navigate}
          />
        ),
      })),
      // 39 — Practice 1
      { id: "practice-1", render: () => <PracticeSpread1 navigate={navigate} /> },
      // 40 — Practice 2
      { id: "practice-2", render: () => <PracticeSpread2 navigate={navigate} /> },
      // 41 — Practice 3
      { id: "practice-3", render: () => <PracticeSpread3 navigate={navigate} /> },
      // 42 — Practice 4
      { id: "practice-4", render: () => <PracticeSpread4 /> },
      // 43 — Safety
      { id: "safety", render: () => <SafetySpread /> },
      // 44 — final field notes
      { id: "conclusion", render: () => <ConclusionSpread /> },
      // 45–46 — physically missing pages
      { id: "torn-1", render: () => <TornSpread1 /> },
      { id: "torn-2", render: () => <TornSpread2 /> },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [onOpenLab, theme]
  );

  return (
    <main className={`academy-page ${theme}-theme`}>
      <div className="academy-background" />
      <div className="academy-container" ref={containerRef}>
        <div
          className="academy-book-scale-wrapper"
          style={{ transform: `scale(${scale})`, position: 'relative' }}
        >
          {onClose && (
            <div 
              style={{
                position: 'absolute',
                right: '-4rem',
                top: '-2rem',
                zIndex: 250,
                display: 'flex',
                flexDirection: 'column',
                gap: '0.75rem',
              }}
            >
              <button 
                type="button" 
                onClick={onClose} 
                aria-label="Close" 
                className="rounded-xl border border-white/10 bg-[#0b1020]/90 p-3 text-white/70 shadow-lg hover:bg-white/10 hover:text-white transition-colors"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
              </button>
              
            </div>
          )}
          {currentPage === 0 ? (
            // Cover — standalone, not inside the open book
            pages[0].render()
          ) : (
            <div className="academy-book is-open">
              {/* Bookmarks */}
              <div 
                className="academy-bookmarks"
                style={{
                  position: 'absolute',
                  right: '-2rem',
                  top: '4rem',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '0.5rem',
                  zIndex: 0
                }}
              >
                {bookmarks.map((bm, i) => (
                  <button
                    key={bm.label}
                    onClick={() => navigate(bm.page)}
                    aria-label={bm.label}
                    title={bm.label}
                    className={`academy-bookmark ${currentPage === bm.page ? 'academy-bookmark-active' : ''} ${i % 3 === 1 ? 'academy-bookmark-overlap' : ''}`}
                    style={{
                      backgroundColor: bm.color,
                      color: '#fff',
                      padding: '0.65rem 0.7rem',
                      borderRadius: '0 0.5rem 0.5rem 0',
                      border: '1px solid rgba(255,255,255,0.2)',
                      borderLeft: 'none',
                      fontFamily: 'var(--font-cinzel)',
                      cursor: 'pointer',
                      boxShadow: '2px 2px 5px rgba(0,0,0,0.3)',
                      width: '3.1rem',
                      height: '3.1rem',
                    }}
                  >
                    <bm.icon size={18} strokeWidth={1.8} aria-hidden="true" />
                  </button>
                ))}
              </div>

              <BookFlip
                currentPage={currentPage}
                onTurn={(dir) =>
                  navigate(
                    dir === "next"
                      ? Math.min(currentPage + 1, pages.length - 1)
                      : Math.max(currentPage - 1, 1)
                  )
                }
                totalPages={pages.length - 1}
              >
                {pages[currentPage]?.render()}
              </BookFlip>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}
