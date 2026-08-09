'use client';

import { useState, useEffect } from 'react';
import { X } from 'lucide-react';

interface OnboardingHintProps {
  storageKey: string;
  locale: string;
  kind: string;
}

export default function OnboardingHint({ storageKey }: OnboardingHintProps) {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    try {
      const dismissed = localStorage.getItem(storageKey);
      if (!dismissed) setVisible(true);
    } catch {}
  }, [storageKey]);

  if (!visible) return null;

  const dismiss = () => {
    try { localStorage.setItem(storageKey, 'dismissed'); } catch {}
    setVisible(false);
  };

  return (
    <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 px-5 py-3 bg-[var(--card)] border border-[var(--border)] rounded-xl shadow-[0_10px_40px_rgba(0,0,0,.3)] flex items-center gap-3 text-sm text-[var(--foreground)]">
      <span>👋</span>
      <span>Welcome! Create your first workspace to get started.</span>
      <button onClick={dismiss} className="ml-2 p-1 rounded hover:bg-[var(--accent)] text-[var(--muted-foreground)]">
        <X size={14} />
      </button>
    </div>
  );
}
