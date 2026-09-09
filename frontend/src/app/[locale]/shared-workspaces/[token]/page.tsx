import SharedWorkspaceAccess from '@/views/shared-workspace/SharedWorkspaceAccess';

export default async function SharedWorkspacePage({ params }: { params: Promise<{ locale: string; token: string }> }) {
  const { locale, token } = await params;
  return <SharedWorkspaceAccess locale={locale} token={decodeURIComponent(token)} />;
}
