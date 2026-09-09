import { redirect } from 'next/navigation';

export default async function SharedWorkspaceRedirect({ params }: { params: Promise<{ token: string }> }) {
  const { token } = await params;
  redirect(`/ru/shared-workspaces/${encodeURIComponent(token)}`);
}
