'use client';
import { NextIntlClientProvider } from 'next-intl';

interface I18nProviderProps {
  children: React.ReactNode;
  locale: string;
  messages: Record<string, unknown>;
}

export default function I18nProvider({ children, locale, messages }: I18nProviderProps) {
  return (
    <NextIntlClientProvider locale={locale} messages={messages}>
      {children}
    </NextIntlClientProvider>
  );
}
