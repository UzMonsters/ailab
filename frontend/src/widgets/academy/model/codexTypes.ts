export type CodexChapterId =
  | "introduction"
  | "chemistry"
  | "sandbox"
  | "equipment"
  | "substances"
  | "scenarios"
  | "physics"
  | "safety"
  | "conclusion";

export type CodexPageType =
  | "cover"
  | "title"
  | "contents"
  | "chapter-opening"
  | "prose"
  | "figure"
  | "technical"
  | "interactive"
  | "equipment-index"
  | "equipment-entry"
  | "conclusion";

export type CodexPage = {
  id: string;
  type: CodexPageType;
  chapter?: CodexChapterId;
  title?: string;
  pageNumber: number;
};

export type CodexChapter = {
  id: CodexChapterId;
  romanNumber: string;
  title: string;
  startPage: number;
  pages: CodexPage[];
};

export type CodexBook = {
  pages: CodexPage[];
  chapters: CodexChapter[];
};

export type BookDirection = "next" | "previous";

export type BookState = {
  currentPageIndex: number;
  isOpen: boolean;
  isTurning: boolean;
  turnDirection: BookDirection | null;
};
