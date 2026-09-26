import { Suspense } from 'react';
import VerifyEmailPage from '@/views/auth/VerifyEmailPage';

export default async function VerifyPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  return (
    <Suspense fallback={<div className="min-h-screen flex items-center justify-center text-sm text-[var(--muted-foreground)]">Loading...</div>}>
      <VerifyEmailPage locale={locale} />
    </Suspense>
  );
}