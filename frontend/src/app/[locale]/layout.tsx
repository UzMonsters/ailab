import I18nProvider from '@/shared/ui/I18nProvider';
import { ToastProvider } from '@/shared/ui/ToastContainer';

export default async function LocaleLayout({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  let messages;
  try {
    console.log("Loading messages for locale", locale); // Cache buster
    messages = (await import(`../../messages/${locale}.json`)).default;
  } catch {
    messages = (await import('../../messages/en.json')).default;
  }

  return (
    <I18nProvider locale={locale} messages={messages}>
      <ToastProvider>{children}</ToastProvider>
    </I18nProvider>
  );
}
