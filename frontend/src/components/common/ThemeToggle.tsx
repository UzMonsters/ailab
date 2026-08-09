'use client';

import { Moon, Sun } from 'lucide-react';
import { useUIStore } from '@/stores/ui.store';

export default function ThemeToggle() {
  const { theme, setTheme } = useUIStore();
  const isDark = theme === 'dark' || (theme === 'system' && typeof window !== 'undefined' && !window.matchMedia('(prefers-color-scheme: light)').matches);

  return (
    <button
      onClick={() => setTheme(isDark ? 'light' : 'dark')}
      aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
      title={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
      className="w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--card)] text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all"
    >
      {isDark ? <Sun size={16} /> : <Moon size={16} />}
    </button>
  );
}
