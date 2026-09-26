'use client';

import { useEffect, useRef } from 'react';
import { getApiBaseUrl } from '@/shared/api/client';

const PING_INTERVAL_MS = 270_000; // 4.5 minutes (Render sleeps after 15 min)

export default function BackendKeepAlive() {
  const lastPingRef = useRef<number>(0);

  useEffect(() => {
    const ping = async () => {
      try {
        const baseUrl = getApiBaseUrl();
        lastPingRef.current = Date.now();
        await fetch(`${baseUrl}/actuator/health`, {
          method: 'GET',
          cache: 'no-store',
          mode: 'cors',
        });
      } catch {
        // Silent catch: background keep-alive should never break UI
      }
    };

    // Ping soon after mount (1.5s delay to let first render settle)
    const initialTimer = setTimeout(() => {
      void ping();
    }, 1500);

    // Periodic ping every 4.5 minutes
    const intervalTimer = setInterval(() => {
      void ping();
    }, PING_INTERVAL_MS);

    // Ping on tab re-focus if more than 4 minutes elapsed
    const handleVisibility = () => {
      if (document.visibilityState === 'visible') {
        const elapsed = Date.now() - lastPingRef.current;
        if (elapsed > 240_000) {
          void ping();
        }
      }
    };

    document.addEventListener('visibilitychange', handleVisibility);

    return () => {
      clearTimeout(initialTimer);
      clearInterval(intervalTimer);
      document.removeEventListener('visibilitychange', handleVisibility);
    };
  }, []);

  return null;
}
