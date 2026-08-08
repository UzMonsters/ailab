'use client';

import Link from 'next/link';

export default function NotFound() {
  return (
    <div className="relative z-10 min-h-screen flex items-center justify-center p-4">
      <div className="text-center max-w-md">
        <div className="text-[120px] font-bold text-[#8b5cf6]/20 leading-none mb-4">404</div>
        <div className="text-5xl mb-6">🔍</div>
        <h1 className="text-3xl md:text-4xl font-bold tracking-tight mb-4">Page Not Found</h1>
        <p className="text-[var(--muted-foreground)] mb-8">The page you&apos;re looking for doesn&apos;t exist or has been moved.</p>
        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
          <Link href="/en" className="button button-primary py-3 px-6">Back to Home</Link>
          <button onClick={() => window.history.back()} className="button button-secondary py-3 px-6">Go Back</button>
        </div>
      </div>
    </div>
  );
}
