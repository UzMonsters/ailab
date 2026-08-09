'use client';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { Check, Globe } from 'lucide-react';
import { LOCALES, LOCALE_NAMES } from '@/constants';

interface LanguageSwitcherProps {
  variant?: 'nav' | 'ghost';
  className?: string;
}

export default function LanguageSwitcher({ variant = 'ghost', className = '' }: LanguageSwitcherProps) {
  const pathname = usePathname();
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  const current = (pathname.split('/')[1] as (typeof LOCALES)[number]) || LOCALES[0];
  const locale = LOCALES.includes(current) ? current : LOCALES[0];

  useEffect(() => {
    const onClick = (e: MouseEvent) => {
      if (ref.current && !ref.current.contains(e.target as Node)) setOpen(false);
    };
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setOpen(false);
    };
    document.addEventListener('mousedown', onClick);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('mousedown', onClick);
      document.removeEventListener('keydown', onKey);
    };
  }, []);

  const switchTo = (next: string) => {
    const rest = pathname.split('/').slice(2).join('/');
    router.push(`/${next}${rest ? `/${rest}` : ''}`);
    setOpen(false);
  };

  return (
    <div className={`relative ${className}`} ref={ref}>
      <button
        type="button"
        aria-label="Change language"
        aria-haspopup="menu"
        aria-expanded={open}
        onClick={() => setOpen((v) => !v)}
        className="flex items-center gap-1.5 rounded-md border border-[var(--border)] bg-[var(--card)] px-2.5 h-9 text-xs text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:border-[#8B5CF6]/40 transition-all cursor-pointer"
      >
        <Globe size={14} />
        <span className="uppercase font-semibold tracking-wide">{locale}</span>
      </button>

      {open && (
        <div
          role="menu"
          className="absolute right-0 top-full mt-1.5 min-w-[140px] bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-sm)] shadow-[0_15px_35px_rgba(0,0,0,.6)] py-1 z-[120]"
        >
          {LOCALES.map((loc) => (
            <button
              key={loc}
              role="menuitem"
              onClick={() => switchTo(loc)}
              className={`w-full flex items-center gap-2 px-3 py-2 text-xs text-left cursor-pointer transition-colors ${loc === locale ? 'text-[#C084FC] bg-[#8b5cf6]/10' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/[0.04]'}`}
            >
              <span className={`uppercase font-semibold w-6 ${loc === locale ? 'text-[#C084FC]' : 'text-[var(--muted-foreground)]/70'}`}>{loc}</span>
              <span className="flex-1">{LOCALE_NAMES[loc]}</span>
              {loc === locale && <Check size={13} className="text-[#14F195]" />}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
