import type { CodexPage } from "../model/codexTypes";

const references = {
  physics: {
    roman: "VII", title: "Физика лаборатории", subtitle: "Энергия, поток и границы материала",
    topics: ["Теплопередача", "Кипение", "Конденсация", "Тепловой удар", "Поток жидкости", "Давление"],
    text: "В лаборатории энергия не появляется отдельно от вещества. Нагреватель, стекло, жидкость и окружающий воздух обмениваются теплом с разной скоростью. Поэтому показания датчика и видимое состояние вещества всегда нужно читать вместе.",
    note: "Сначала проследите путь энергии. Потом объясняйте результат."
  },
  safety: {
    roman: "VIII", title: "Безопасность", subtitle: "Ограничения — часть эксперимента",
    topics: ["Тепловое напряжение", "Давление", "Открытое пламя", "Совместимость", "Опоры и крепления", "Диапазоны датчиков"],
    text: "Безопасность не появляется после того, как установка собрана. Она заложена в выборе материала, допустимой температуры, устойчивости опоры и направлении каждого соединения. Ограничение — это не помеха опыту, а условие, при котором его результат можно доверенно интерпретировать.",
    note: "Прежде чем включить установку, назовите её слабое место."
  }
} as const;

export function CodexReference({ page }: { page: CodexPage }) {
  const chapter = page.chapter;
  if (chapter !== "physics" && chapter !== "safety") return null;
  const content = references[chapter];
  if (page.type === "chapter-opening") return <section className="codex-reference-opening"><p>{content.roman}</p><h1>{content.title}</h1><span>{content.subtitle}</span><div/><blockquote>{content.note}</blockquote></section>;
  return <section className="codex-reference"><p className="codex-prose__eyebrow">{content.subtitle}</p><h1>{content.title}</h1><p className="codex-reference__lead">{content.text}</p><ol>{content.topics.map((topic, index) => <li key={topic}><span>{String(index + 1).padStart(2, "0")}</span>{topic}<i/></li>)}</ol><aside>{content.note}</aside></section>;
}
