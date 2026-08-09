'use client';

import { Atom } from 'lucide-react';

export default function LoadingScreen() {
  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-[var(--background)]">
      <div className="flex flex-col items-center gap-4">
        <div className="w-12 h-12 rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center animate-pulse">
          <Atom size={24} className="text-white" />
        </div>
        <div className="text-sm text-[var(--muted-foreground)]">Loading...</div>
      </div>
    </div>
  );
}
