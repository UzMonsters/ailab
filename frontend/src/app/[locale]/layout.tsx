import I18nProvider from '@/components/common/I18nProvider';
import { ToastProvider } from '@/components/common/ToastContainer';

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
