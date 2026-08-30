'use client';

import { usePathname, useRouter } from 'next/navigation';
import { useCallback } from 'react';
import type { Locale } from '@/types';
import { LOCALES } from '@/constants';

const LOCALE_KEY = 'ai-lab-locale';

export function useLocaleSwitch() {
  const pathname = usePathname();
  const router = useRouter();
  const currentLocale = (pathname.split('/')[1] || 'en') as Locale;

  const switchLocale = useCallback(
    (locale: Locale) => {
      if (!LOCALES.includes(locale)) return;
      try {
        localStorage.setItem(LOCALE_KEY, locale);
        document.cookie = `NEXT_LOCALE=${locale};path=/;max-age=31536000`;
      } catch {}
      const segments = pathname.split('/');
      segments[1] = locale;
      router.push(segments.join('/'));
    },
    [pathname, router]
  );

  return { currentLocale, switchLocale };
}

export function getSavedLocale(): Locale | null {
  if (typeof window === 'undefined') return null;
  try {
    const saved = localStorage.getItem(LOCALE_KEY);
    if (saved && LOCALES.includes(saved as Locale)) return saved as Locale;
  } catch {}
  return null;
}
