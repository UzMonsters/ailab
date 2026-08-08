import type { Locale } from '@/types';

export const LOCALES: Locale[] = ['en', 'ru', 'uz'];
export const DEFAULT_LOCALE: Locale = 'en';

export const LOCALE_NAMES: Record<Locale, string> = {
  en: 'English',
  ru: 'Русский',
  uz: "O'zbek",
};

export const SITE_NAME = 'AI Laboratory';
export const SITE_DESCRIPTION = 'AI-powered virtual laboratory platform for scientific research and education.';
