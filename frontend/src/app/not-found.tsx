'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { SearchX, ArrowLeft } from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';

export default function NotFound() {
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';

  return (
    <div className="relative min-h-screen flex items-center justify-center p-4" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />
      <div className="relative z-10 text-center max-w-md">
        <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-[#8b5cf6]/10 border border-[#8b5cf6]/30 rounded-full text-xs font-mono text-[#C084FC] mb-6 tracking-wider uppercase">
          <SearchX size={12} /> 404 Error
        </div>
        <h1 className="text-5xl md:text-6xl font-bold tracking-tight mb-4">
          Page <span className="text-[#8B5CF6]">Not Found</span>
        </h1>
        <p className="text-[var(--muted-foreground)] mb-8">The page you are looking for does not exist or has been moved.</p>
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link href={`/${locale}`} className="bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white py-3 px-6 rounded-[var(--radius-md)] font-semibold no-underline shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:-translate-y-0.5 transition-all">
            Back to Home
          </Link>
          <button onClick={() => window.history.back()} className="inline-flex items-center gap-2 py-3 px-6 rounded-[var(--radius-md)] font-semibold no-underline border border-[var(--border)] bg-[var(--card)] hover:border-[#8B5CF6]/40 transition-all">
            <ArrowLeft size={14} /> Go Back
          </button>
        </div>
      </div>
    </div>
  );
}
