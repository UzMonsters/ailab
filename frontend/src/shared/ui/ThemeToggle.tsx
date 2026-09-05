'use client';

import { useSyncExternalStore } from 'react';
import { Moon, Sun } from 'lucide-react';
import { useTheme } from 'next-themes';

const THEME_PREFERENCE_KEY = 'chemistry-theme-preference';

export default function ThemeToggle() {
  const { setTheme, resolvedTheme } = useTheme();
  const mounted = useSyncExternalStore(() => () => undefined, () => true, () => false);

  const isDark = resolvedTheme === 'dark';

  if (!mounted) {
    return (
      <button className="w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-transparent text-[var(--muted-foreground)] opacity-50" aria-label="Theme toggle placeholder">
        <span className="w-4 h-4" />
      </button>
    );
  }

  return (
    <button
      onClick={() => {
        const nextTheme = isDark ? 'light' : 'dark';
        setTheme(nextTheme);
        window.localStorage.setItem(THEME_PREFERENCE_KEY, nextTheme);
        window.localStorage.setItem('ai-lab-theme', nextTheme);
        window.dispatchEvent(new CustomEvent('chemistry-theme-change', { detail: nextTheme }));
      }}
      aria-label={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
      title={isDark ? 'Switch to light mode' : 'Switch to dark mode'}
      className="w-9 h-9 flex items-center justify-center rounded-lg border border-[var(--border)] bg-[var(--muted)]/70 text-[var(--muted-foreground)] hover:bg-[var(--accent)] hover:text-[var(--foreground)] transition-all backdrop-blur-sm"
    >
      {isDark ? <Sun size={16} /> : <Moon size={16} />}
    </button>
  );
}
