'use client';

import { Moon, Sun } from 'lucide-react';
import { useUIStore } from '@/stores/ui.store';

export default function ThemeToggle() {
  const { theme, setTheme } = useUIStore();
  const isLight = theme === 'light';

  const toggle = () => {
    setTheme(isLight ? 'dark' : 'light');
  };

  return (
    <button
      onClick={toggle}
      aria-label={isLight ? 'Switch to dark mode' : 'Switch to light mode'}
      title={isLight ? 'Switch to dark mode' : 'Switch to light mode'}
      className="w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--card)] text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all"
    >
      {isLight ? <Moon size={16} /> : <Sun size={16} />}
    </button>
  );
}
