'use client';
import { useState, useCallback, createContext, useContext, type ReactNode } from 'react';
import Toast from '@/shared/ui/Toast';

interface Toast { id: number; message: string; type: 'success' | 'error' | 'info'; }
interface ToastContextType { addToast: (message: string, type?: Toast['type']) => void; }

const ToastContext = createContext<ToastContextType>({ addToast: () => {} });

export function useToast() { return useContext(ToastContext); }

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<Toast[]>([]);
  const addToast = useCallback((message: string, type: Toast['type'] = 'info') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);
  }, []);
  const removeToast = useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);
  return (
    <ToastContext.Provider value={{ addToast }}>
      {children}
      {toasts.map((t) => <Toast key={t.id} message={t.message} type={t.type} onClose={() => removeToast(t.id)} />)}
    </ToastContext.Provider>
  );
}
