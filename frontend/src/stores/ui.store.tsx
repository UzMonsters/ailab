'use client';

import { createContext, useContext, useEffect, useMemo, useState, useCallback, type ReactNode } from 'react';

interface UIState {
  sidebarOpen: boolean;
  theme: 'dark' | 'light' | 'system';
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  setTheme: (theme: 'dark' | 'light' | 'system') => void;
}

const UIContext = createContext<UIState | null>(null);

export function UIProvider({ children }: { children: ReactNode }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [theme, setTheme] = useState<'dark' | 'light' | 'system'>(() => {
    if (typeof window === 'undefined') return 'system';
    const saved = window.localStorage.getItem('ai-lab-theme');
    return saved === 'dark' || saved === 'light' || saved === 'system' ? saved : 'system';
  });

  useEffect(() => {
    const media = window.matchMedia('(prefers-color-scheme: light)');
    const apply = () => {
      const light = theme === 'light' || (theme === 'system' && media.matches);
      document.documentElement.toggleAttribute('data-theme', light);
      if (light) document.documentElement.dataset.theme = 'light';
    };
    apply();
    const onChange = () => { if (theme === 'system') apply(); };
    media.addEventListener?.('change', onChange);
    return () => media.removeEventListener?.('change', onChange);
  }, [theme]);

  const updateTheme = useCallback((next: 'dark' | 'light' | 'system') => {
    setTheme(next);
    window.localStorage.setItem('ai-lab-theme', next);
  }, []);

  const toggleSidebar = useCallback(() => setSidebarOpen((p) => !p), []);
  const contextValue = useMemo(() => ({
    sidebarOpen,
    theme,
    toggleSidebar,
    setSidebarOpen,
    setTheme: updateTheme,
  }), [sidebarOpen, theme, toggleSidebar, updateTheme]);

  return (
    <UIContext.Provider value={contextValue}>
      {children}
    </UIContext.Provider>
  );
}

export function useUIStore() {
  const ctx = useContext(UIContext);
  if (!ctx) throw new Error('useUIStore must be used within UIProvider');
  return ctx;
}
