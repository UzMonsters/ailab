'use client';

import { useEffect, useRef } from 'react';
import { useBookStudioStore } from '../store/useBookStudioStore';

export function useAutoSave(delayMs = 2000) {
  const { dirty, save } = useBookStudioStore();
  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    if (!dirty) return;
    if (timerRef.current) clearTimeout(timerRef.current);
    timerRef.current = setTimeout(() => {
      void save();
    }, delayMs);
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [dirty, delayMs, save]);
}
