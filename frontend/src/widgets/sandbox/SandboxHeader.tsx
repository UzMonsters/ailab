import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import ThemeToggle from "@/shared/ui/ThemeToggle";
import LanguageSwitcher from "@/shared/ui/LanguageSwitcher";

interface SandboxHeaderProps {
  locale: string;
  workspaceId: string | null;
}

export function SandboxHeader({ locale, workspaceId }: SandboxHeaderProps) {
  return (
    <header className="sandbox-header flex flex-col gap-2 border-b border-[var(--border)] bg-[var(--card)] p-5">
      <div className="flex items-center justify-between">
        <Link href={`/${locale}/dashboard`} aria-label="Back to dashboard" className="text-[var(--muted-foreground)] hover:text-foreground transition-colors">
          <ArrowLeft size={18} />
        </Link>
        <div className="flex gap-2">
          <ThemeToggle />
          <LanguageSwitcher />
        </div>
      </div>
      <span className="text-sm font-semibold tracking-wide text-[var(--primary-bright)]">
        {workspaceId ? "ЛАБОРАТОРНЫЙ ЭКСПЕРИМЕНТ" : "НОВЫЙ ЭКСПЕРИМЕНТ"}
      </span>
    </header>
  );
}
