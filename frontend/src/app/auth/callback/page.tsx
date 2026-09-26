import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { LOCALES } from '@/constants';

export default async function RootCallbackPage({
  searchParams,
}: {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}) {
  const cookieStore = await cookies();
  const savedLocale = cookieStore.get('NEXT_LOCALE')?.value;
  const locale = savedLocale && (LOCALES as string[]).includes(savedLocale) ? savedLocale : 'ru';
  const sp = await searchParams;
  const queryParts = Object.entries(sp)
    .filter(([_, val]) => val !== undefined)
    .map(([key, val]) => `${encodeURIComponent(key)}=${encodeURIComponent(Array.isArray(val) ? val[0] : (val as string))}`);
  const query = queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
  redirect(`/${locale}/auth/callback${query}`);
}