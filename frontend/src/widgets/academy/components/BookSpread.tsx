import type { ReactNode } from "react";
import type { CodexPage } from "../model/codexTypes";
import { BookPage } from "./BookPage";

type BookSpreadProps = {
  leftPage: CodexPage;
  rightPage?: CodexPage;
  children: ReactNode;
};

export function BookSpread({ leftPage, rightPage, children }: BookSpreadProps) {
  return (
    <div className="codex-spread" data-left-page={leftPage.id} data-right-page={rightPage?.id}>
      <BookPage side="left" pageNumber={leftPage.pageNumber} chapterLabel={leftPage.chapter}>
        {children}
      </BookPage>
      {rightPage && <BookPage side="right" pageNumber={rightPage.pageNumber} chapterLabel={rightPage.chapter} />}
    </div>
  );
}
