'use client';

import { createContext, useContext, useMemo, useState, useCallback, type ReactNode } from 'react';
import { useTheme } from 'next-themes';

interface UIState {
  sidebarOpen: boolean;
  theme: 'dark' | 'light' | 'system';
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  setTheme: (theme: 'dark' | 'light' | 'system') => void;
}

const UIContext = createContext<UIState | null>(null);

export function UIProvider({ children }: { children: ReactNode }) {
  const { theme: activeTheme, setTheme: setActiveTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const theme: UIState['theme'] = activeTheme === 'light' || activeTheme === 'dark' || activeTheme === 'system' ? activeTheme : 'system';

  const updateTheme = useCallback((next: 'dark' | 'light' | 'system') => {
    setActiveTheme(next);
    window.localStorage.setItem('ai-lab-theme', next);
  }, [setActiveTheme]);

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
