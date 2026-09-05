import AdminLayout from '@/widgets/layout/AdminLayout';
import './admin.css';
import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from 'next-intl/server';

export default async function AdminRouteLayout({ children, params }: { children: React.ReactNode; params: Promise<{ locale: string }> }) {
  const { locale } = await params;
  const messages = await getMessages({ locale });
  return <NextIntlClientProvider locale={locale} messages={messages}><AdminLayout>{children}</AdminLayout></NextIntlClientProvider>;
}
