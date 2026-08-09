'use client';

import { createContext, useContext, useState, type ReactNode } from 'react';

interface UIState {
  sidebarOpen: boolean;
  theme: 'dark' | 'light';
  toggleSidebar: () => void;
  setSidebarOpen: (open: boolean) => void;
  setTheme: (theme: 'dark' | 'light') => void;
}

const UIContext = createContext<UIState | null>(null);

export function UIProvider({ children }: { children: ReactNode }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [theme, setTheme] = useState<'dark' | 'light'>('dark');

  return (
    <UIContext.Provider value={{
      sidebarOpen,
      theme,
      toggleSidebar: () => setSidebarOpen((p) => !p),
      setSidebarOpen,
      setTheme,
    }}>
      {children}
    </UIContext.Provider>
  );
}

export function useUIStore() {
  const ctx = useContext(UIContext);
  if (!ctx) throw new Error('useUIStore must be used within UIProvider');
  return ctx;
}
