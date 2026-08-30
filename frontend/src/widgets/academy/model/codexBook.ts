import type { CodexBook, CodexChapter, CodexPage } from "./codexTypes";

const chapterDefinitions = [
  ["introduction", "I", "Introduction", "Вступление", "chapter-introduction"],
  ["chemistry", "II", "Chemistry", "Химия", "chapter-chemistry"],
  ["sandbox", "III", "Sandbox", "Песочница", "chapter-sandbox"],
  ["equipment", "IV", "Equipment", "Оборудование", "chapter-equipment"],
  ["substances", "V", "Materials and substances", "Материалы и вещества", "chapter-substances"],
  ["scenarios", "VI", "Scenarios", "Сценарии", "chapter-scenarios"],
  ["physics", "VII", "Laboratory physics", "Физика лаборатории", "chapter-physics"],
  ["safety", "VIII", "Safety", "Безопасность", "chapter-safety"],
  ["conclusion", "IX", "Field notes", "Полевые записи", "chapter-conclusion"],
] as const;

function createPage(id: string, type: CodexPage["type"], pageNumber: number, chapter?: CodexPage["chapter"]): CodexPage {
  return { id, type, pageNumber, chapter };
}

const pages: CodexPage[] = [
  createPage("cover", "cover", 0),
  createPage("title", "title", 1),
  createPage("contents-author", "contents", 2),
  createPage("contents-list", "contents", 3),
];

const chapters: CodexChapter[] = chapterDefinitions.map(([id, romanNumber, title, _localizedTitle, pageId], chapterIndex) => {
  const chapterId = id;
  const startPage = pages.length;
  const chapterPages = [
    createPage(`${pageId}-opening`, "chapter-opening", startPage, chapterId),
    createPage(`${pageId}-text-1`, "prose", startPage + 1, chapterId),
    createPage(`${pageId}-text-2`, chapterId === "equipment" ? "equipment-index" : "technical", startPage + 2, chapterId),
  ];
  pages.push(...chapterPages);
  return { id: chapterId, romanNumber, title, startPage, pages: chapterPages };
});

export const codexBook: CodexBook = { pages, chapters };

export const firstContentPageIndex = 1;

export function getPageIndex(pageId: string) {
  return codexBook.pages.findIndex((page) => page.id === pageId);
}

export function getChapterPageIndex(chapterId: CodexChapter["id"]) {
  return codexBook.chapters.find((chapter) => chapter.id === chapterId)?.startPage ?? firstContentPageIndex;
}
