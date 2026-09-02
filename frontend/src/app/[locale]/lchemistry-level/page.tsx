"use client";
import { useEffect, useMemo, useState, useRef } from "react";
import Link from "next/link";
import { useLocale } from "next-intl";
import { useRouter } from "next/navigation";
import { ArrowLeft, Check, FlaskConical, LockKeyhole, Play, X, Beaker, Flame, Gauge, Zap } from "lucide-react";
import ScienceBackground, { BackgroundGlow } from "@/shared/ui/ScienceBackground";
import Logo from "@/shared/ui/Logo";
import dynamic from 'next/dynamic';
const ScientificCoreModel = dynamic(() => import('@/components/scientific-core'), { ssr: false });
import styles from "./page.module.css";

type Size = "normal" | "important" | "milestone" | "major";
type LayoutNode = { id: number; x: number; y: number; size: Size; category: string };

const layoutData: LayoutNode[] = [
  // ZONE 1: 1-10 (outer orbit)
  { id: 1, x: 54, y: 91, size: "milestone", category: "measurement" },
  { id: 2, x: 68, y: 84, size: "normal", category: "equipment" },
  { id: 3, x: 78, y: 74, size: "normal", category: "equipment" },
  { id: 4, x: 84, y: 62, size: "important", category: "heat" },
  { id: 5, x: 86, y: 48, size: "milestone", category: "reaction" },
  { id: 6, x: 82, y: 35, size: "normal", category: "reaction" },
  { id: 7, x: 74, y: 24, size: "important", category: "heat" },
  { id: 8, x: 62, y: 17, size: "normal", category: "measurement" },
  { id: 9, x: 50, y: 14, size: "normal", category: "equipment" },
  { id: 10, x: 38, y: 16, size: "milestone", category: "system" },

  // ZONE 2: 11-20 (middle orbit)
  { id: 11, x: 27, y: 22, size: "normal", category: "reaction" },
  { id: 12, x: 17, y: 32, size: "normal", category: "reaction" },
  { id: 13, x: 12, y: 44, size: "important", category: "reaction" },
  { id: 14, x: 13, y: 56, size: "normal", category: "measurement" },
  { id: 15, x: 19, y: 67, size: "milestone", category: "reaction" },
  { id: 16, x: 29, y: 74, size: "normal", category: "heat" },
  { id: 17, x: 40, y: 77, size: "normal", category: "heat" },
  { id: 18, x: 51, y: 75, size: "normal", category: "heat" },
  { id: 19, x: 60, y: 69, size: "important", category: "heat" },
  { id: 20, x: 68, y: 60, size: "milestone", category: "system" },

  // ZONE 3: 21-30 (inner orbit, close to 50,50 center core)
  { id: 21, x: 70, y: 48, size: "normal", category: "equipment" },
  { id: 22, x: 65, y: 38, size: "normal", category: "equipment" },
  { id: 23, x: 57, y: 31, size: "important", category: "measurement" },
  { id: 24, x: 48, y: 28, size: "normal", category: "measurement" },
  { id: 25, x: 38, y: 32, size: "milestone", category: "system" },
  { id: 26, x: 28, y: 39, size: "normal", category: "system" },
  { id: 27, x: 24, y: 51, size: "important", category: "system" },
  { id: 28, x: 28, y: 61, size: "normal", category: "system" },
  { id: 29, x: 37, y: 64, size: "normal", category: "system" },
  { id: 30, x: 50, y: 50, size: "major", category: "system" },
];

type Level = { id: number; title: string; skill: string; description: string; reward: string; xp: number; x: number; y: number; size: Size; category: string };

const ui = { 
  ru:{brand:"Лабораторная экспедиция",back:"Назад",sandbox:"Открыть песочницу",eyebrow:"ХИМИЧЕСКАЯ ЭКСПЕДИЦИЯ",levels:"30 уровней",desc:"Пройдите путь от первых измерений до сложных лабораторных систем.",available:"Доступно",current:"Текущий уровень",locked:"Заблокировано",done:"Пройдено",start:"Начать эксперимент",mission:"Этап",core:"Лабораторное ядро"}, 
  en:{brand:"Laboratory expedition",back:"Back",sandbox:"Open Sandbox",eyebrow:"CHEMISTRY EXPEDITION",levels:"30 levels",desc:"Walk the path from first measurements to complex laboratory systems.",available:"Available",current:"Current level",locked:"Locked",done:"Completed",start:"Start experiment",mission:"Stage",core:"Laboratory Core"}, 
  uz:{brand:"Laboratoriya ekspeditsiyasi",back:"Orqaga",sandbox:"Sandboxni ochish",eyebrow:"KIMYO EKSPEDITSIYASI",levels:"30 bosqich",desc:"Birinchi o'lchovlardan tortib murakkab tizimlargacha bo'lgan yo'lni bosib o'ting.",available:"Mavjud",current:"Joriy bosqich",locked:"Bloklangan",done:"Tugallangan",start:"Tajribani boshlash",mission:"Bosqich",core:"Laboratoriya yadrosi"} 
} as const;

const levelLocales = { 
  en:{titles:["First Drop","Precision Work","Transfer","Temperature Control","First Heating","Mixing","Phase Change","Pressure","Solutions","Laboratory Master","Acids","Bases","Neutralization","Indicators","Concentration","Gas Processes","Evaporation","Condensation","Heat Exchange","Simple Distillation","Cooler","Flow Control","Burette","Precise Dosing","Separation","Complex Setup","Coupled Systems","Pressure Control","Research Scheme","Laboratory Expedition"]}, 
  ru:{titles:["Первая капля","Точная работа","Переливание","Контроль температуры","Первое нагревание","Смеси","Фазовый переход","Давление","Растворы","Мастер лаборатории","Кислоты","Основания","Нейтрализация","Индикаторы","Концентрация","Газовые процессы","Испарение","Конденсация","Теплообмен","Простая дистилляция","Холодильник","Контроль потока","Бюретка","Точная дозировка","Разделение","Сложная установка","Связанные системы","Контроль давления","Исследовательская схема","Лабораторная экспедиция"]},
  uz:{titles:["Birinchi tomchi","Aniq ish","Quyish","Harorat nazorati","Birinchi isitish","Aralashmalar","Faza o'zgarishi","Bosim","Eritmalar","Laboratoriya ustasi","Kislotalar","Asoslar","Neytrallash","Indikatorlar","Konsentratsiya","Gaz jarayonlari","Bug'lanish","Kondensatsiya","Issiqlik almashinuvi","Oddiy distillash","Sovutgich","Oqim nazorati","Byuretka","Aniq dozalash","Ajratish","Murakkab qurilma","Bog'langan tizimlar","Bosim nazorati","Tadqiqot sxemasi","Laboratoriya ekspeditsiyasi"]}
} as const;

function generateLevels(locale: string): Level[] {
  const t = levelLocales[locale as keyof typeof levelLocales] || levelLocales.ru;
  return layoutData.map((node) => ({
    ...node,
    title: t.titles[node.id - 1] || `Level ${node.id}`,
    skill: "Эксперимент",
    description: "Исследуйте законы химии на практике.",
    reward: "Опыт",
    xp: 100 + node.id * 10
  }));
}

function ConnectionGraph({ layout, current, hovered, completed }: { layout: LayoutNode[], current: number, hovered: number | null, completed: number[] }) {
  const paths = [];
  const getPath = (from: LayoutNode, to: LayoutNode) => {
    const mx = (from.x + to.x) / 2;
    const my = (from.y + to.y) / 2;
    const dx = to.x - from.x;
    const dy = to.y - from.y;
    const len = Math.sqrt(dx*dx + dy*dy);
    const curve = len * 0.15;
    const ox = (-dy / len) * curve;
    const oy = (dx / len) * curve;
    return `M ${from.x} ${from.y} Q ${mx + ox} ${my + oy} ${to.x} ${to.y}`;
  };

  for (let i = 0; i < layout.length - 1; i++) {
    const from = layout[i];
    const to = layout[i+1];
    
    const isCompleted = completed.includes(from.id) && (completed.includes(to.id) || to.id === current);
    const isHovered = hovered === from.id || hovered === to.id;
    
    let pathClass = styles.connPath;
    if (isCompleted) pathClass += ` ${styles.connCompleted}`;
    if (isHovered) pathClass += ` ${styles.connHovered}`;

    const pathData = getPath(from, to);

    paths.push(
      <g key={`conn-${i}`}>
         <path d={pathData} className={pathClass} />
         {(isCompleted || isHovered) && (
           <circle r="0.25" className={styles.particleLight}>
             <animateMotion dur={`${2 + (i % 3)}s`} repeatCount="indefinite" path={pathData} />
           </circle>
         )}
      </g>
    );
  }

  return (
    <svg className={styles.connectionGraph} viewBox="0 0 100 100" preserveAspectRatio="none">
       {paths}
    </svg>
  );
}

function Station({ level, status, isHovered, onHover, onClick, copy }: { level: Level; status: "locked" | "current" | "completed"; isHovered: boolean; onHover: (id: number | null) => void; onClick: () => void; copy: any }) {
  const iconProps = { size: level.size === 'major' ? 26 : level.size === 'milestone' ? 22 : 18 };
  let Icon = FlaskConical;
  if (level.category === 'measurement') Icon = Gauge;
  else if (level.category === 'heat') Icon = Flame;
  else if (level.category === 'reaction') Icon = Beaker;
  else if (level.category === 'system') Icon = Zap;

  if (status === "locked") Icon = LockKeyhole;
  if (status === "completed") Icon = Check;

  return (
    <div className={`${styles.stationWrapper} ${styles[level.size]}`} style={{ left: `${level.x}%`, top: `${level.y}%` }} onMouseEnter={() => onHover(level.id)} onMouseLeave={() => onHover(null)}>
      <button 
        type="button" 
        className={`${styles.station} ${styles[status]}`} 
        onClick={onClick} 
        aria-label={`${level.title}, ${copy.mission} ${level.id}`}
      >
        <span className={styles.art}>
          <Icon {...iconProps} />
          {status !== 'locked' && <span className={styles.badge}>{level.id}</span>}
          {status === 'locked' && <span className={styles.badgeLocked}>{level.id}</span>}
        </span>
      </button>
      
      {/* Node label rendered outside */}
      <span className={styles.stationLabel} style={{
        // Simple collision avoidance: top half goes up, bottom half goes down
        top: level.y < 50 ? (level.size === 'major' ? '-30px' : '-24px') : 'auto',
        bottom: level.y >= 50 ? (level.size === 'major' ? '-30px' : '-24px') : 'auto'
      }}>
        {level.title}
      </span>

      {isHovered && (
        <div className={styles.microTooltip}>
          <strong>{copy.mission} {level.id}</strong>
          <span>{level.title}</span>
          {status === 'locked' ? <small>🔒 Завершите предыдущий этап</small> : <small>≈ 5 мин</small>}
        </div>
      )}
    </div>
  );
}

export default function ChemistryLevelsPage() {
  const locale = useLocale() as keyof typeof ui;
  const copy = ui[locale] ?? ui.ru;
  const router = useRouter();
  const [completed, setCompleted] = useState<number[]>(() => {
    try {
      const raw = localStorage.getItem("chemistry-academy-progress-v2");
      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed) ? parsed.filter((id): id is number => Number.isInteger(id) && id >= 1 && id <= 30) : [];
    } catch { return []; }
  });
  
  const [selected, setSelected] = useState<Level | null>(null);
  const [hovered, setHovered] = useState<number | null>(null);
  const levels = useMemo(() => generateLevels(locale), [locale]);
  const current = useMemo(() => levels.find(l => !completed.includes(l.id))?.id ?? 30, [completed, levels]);
  
  useEffect(() => {
    localStorage.setItem("chemistry-academy-progress-v2", JSON.stringify(completed));
  }, [completed]);
  
  const status = (id: number) => completed.includes(id) ? "completed" : id === current ? "current" : "locked";
  const progress = Math.round(completed.length / levels.length * 100);

  // Auto center on load
  const mapRef = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (mapRef.current) {
      const currentLevel = levels.find(l => l.id === current) || levels[0];
      const viewport = mapRef.current.parentElement;
      if (viewport) {
        const cw = viewport.clientWidth;
        const ch = viewport.clientHeight;
        const targetX = (currentLevel.x / 100) * mapRef.current.clientWidth - cw / 2;
        const targetY = (currentLevel.y / 100) * mapRef.current.clientHeight - ch / 2;
        viewport.scrollTo({ left: targetX, top: targetY, behavior: 'smooth' });
      }
    }
  }, [current, levels]);

  return (
    <main className={`${styles.page} ${hovered !== null ? styles.hasHover : ''}`}>
      <ScienceBackground className={styles.heroBackground} />
      
      <header className={styles.header}>
        <Link href={`/${locale}/workspace/sandbox`} className={styles.back} aria-label={copy.back}>
          <ArrowLeft size={20} />
        </Link>
        <div className={styles.brand}><Logo /></div>
        <div className={styles.progressHeader}>
          <span>{copy.eyebrow}</span>
          <div className={styles.progressInfo}>
            {completed.length} / {levels.length}
            <div className={styles.progressBar}><b style={{ width: `${progress}%` }} /></div>
          </div>
        </div>
        <Link href={`/${locale}/workspace/sandbox`} className={styles.sandbox}>{copy.sandbox}</Link>
      </header>
      
      <section className={styles.mapViewport}>
        <div className={styles.map} ref={mapRef}>
          <ConnectionGraph layout={layoutData} current={current} hovered={hovered} completed={completed} />
          
          <div className={styles.mapIntro}>
            <span>{copy.eyebrow}</span>
            <h1>{copy.levels}</h1>
            <p>{copy.desc}</p>
          </div>
          
          <div className={styles.mapLegend}>
            <i className={styles.legendAvailable} /><span>{copy.available}</span>
            <i className={styles.legendCurrent} /><span>{copy.current}</span>
            <i className={styles.legendDone} /><span>{copy.done}</span>
            <i className={styles.legendLocked} /><span>{copy.locked}</span>
          </div>

          <div className={styles.core} aria-label={copy.core}>
            <div className={styles.coreModelWrapper}>
              <ScientificCoreModel size={533} accentColor="#7c3aed" coreOnly={true} />
            </div>
          </div>

          {levels.map(level => (
            <Station 
              key={level.id} 
              level={level} 
              status={status(level.id)} 
              isHovered={hovered === level.id}
              onHover={setHovered}
              onClick={() => {
                if (status(level.id) !== 'locked') setSelected(level);
              }}
              copy={copy}
            />
          ))}
        </div>
      </section>
      
      {selected && (
        <div className={styles.backdrop} onClick={() => setSelected(null)}>
          <aside className={styles.previewPanel} onClick={e => e.stopPropagation()}>
            <button type="button" className={styles.close} onClick={() => setSelected(null)}><X /></button>
            <span>{copy.mission} {selected.id}</span>
            <h2>{selected.title}</h2>
            <p>{selected.description}</p>
            <button className={styles.start} type="button" onClick={() => router.push(`/${locale}/workspace/sandbox?level=${selected.id}`)}>
              {copy.start} <Play size={16} />
            </button>
          </aside>
        </div>
      )}
    </main>
  );
}
