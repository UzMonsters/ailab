"use client";

import { X } from "lucide-react";
import { useTranslations } from "next-intl";
import { useEffect } from "react";
import CodexExperience from "./CodexExperience";
export type CodexLabContext = {
  equipmentId?: string;
  experimentId?: string;
  type?: 'equipment' | 'material' | 'scenario';
  id?: string;
  level?: string;
};

export function CodexModal({ onClose, onOpenLab, initialContext }: { onClose: () => void; onOpenLab?: (context: CodexLabContext) => void; initialContext?: CodexLabContext }) {
  const t = useTranslations("adventure");
  
  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();
    };
    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, [onClose]);

  return (
    <div 
      className="fixed inset-0 z-[240]" 
      role="dialog" 
      aria-modal="true" 
      aria-label={t("title")}
    >
      <div className="absolute inset-0 bg-black/65 backdrop-blur-[2px]" aria-hidden="true" />
      <CodexExperience onOpenLab={onOpenLab} onClose={onClose} initialContext={initialContext} />
    </div>
  );
}
