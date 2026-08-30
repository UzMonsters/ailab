'use client';

import { useState, useEffect, useRef } from 'react';
import type { Engine } from '../core/Engine';

export function useEngineState(engine: Engine | null) {
  const [tick, setTick] = useState(0);
  const frameRef = useRef<number | null>(null);

  useEffect(() => {
    if (!engine) return;
    
    // Subscribe to explicit engine updates rather than 60fps render calls
    const unsubscribe = engine.subscribe(() => {
      if (frameRef.current !== null) return;
      frameRef.current = window.requestAnimationFrame(() => {
        frameRef.current = null;
        setTick((t) => t + 1);
      });
    });
    
    return () => {
      unsubscribe();
      if (frameRef.current !== null) window.cancelAnimationFrame(frameRef.current);
      frameRef.current = null;
    };
  }, [engine]);

  return { engine, tick };
}
