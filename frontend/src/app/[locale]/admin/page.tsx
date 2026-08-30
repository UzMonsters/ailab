import { redirect } from 'next/navigation';

export default async function AdminPage({ params }: { params: { locale: string } | Promise<{ locale: string }> }) {
  const resolvedParams = await params;
  const locale = resolvedParams.locale || 'ru';
  redirect(`/${locale}/admin/dashboard`);
}
