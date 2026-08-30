import type { ReactNode } from "react";
import { useTranslations } from "next-intl";

type BookPageProps = {
  children?: ReactNode;
  side: "left" | "right";
  pageNumber?: number;
  chapterLabel?: string;
};

export function BookPage({ children, side, pageNumber, chapterLabel }: BookPageProps) {
  const t = useTranslations("adventure");
  return (
    <article className={`codex-page codex-page--${side}`} data-page-number={pageNumber}>
      <div className="codex-page__inner">
        <div className="codex-page__content">{children ?? (side === "right" ? <div className="codex-field-notes"><span className="codex-field-notes__eyebrow">{t("openFieldNotes")}</span><div className="codex-field-notes__line"/><h2>{chapterLabel}</h2><p>{t("guideText")}</p><div className="codex-field-notes__diagram" aria-hidden="true"><span/><span/><span/><span/></div><small>{t("completion")}</small></div> : null)}</div>
        {pageNumber !== undefined && <footer className="codex-page__footer">{String(pageNumber).padStart(3, "0")}</footer>}
      </div>
    </article>
  );
}
