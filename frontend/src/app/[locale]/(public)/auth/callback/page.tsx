import { Suspense } from 'react';
import OAuthCallbackPage from '@/views/auth/OAuthCallbackPage';

export default async function CallbackPage({
  params,
}: {
  params: Promise<{ locale: string }>;
}) {
  const { locale } = await params;
  return (
    <Suspense fallback={<div className="min-h-screen flex items-center justify-center text-sm text-[var(--muted-foreground)]">Loading...</div>}>
      <OAuthCallbackPage locale={locale} />
    </Suspense>
  );
}