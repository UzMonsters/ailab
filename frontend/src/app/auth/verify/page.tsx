import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';
import { LOCALES } from '@/constants';

export default async function RootVerifyPage({
  searchParams,
}: {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>;
}) {
  const cookieStore = await cookies();
  const savedLocale = cookieStore.get('NEXT_LOCALE')?.value;
  const locale = savedLocale && (LOCALES as string[]).includes(savedLocale) ? savedLocale : 'ru';
  const sp = await searchParams;
  const token = typeof sp.token === 'string' ? sp.token : '';
  const query = token ? `?token=${encodeURIComponent(token)}` : '';
  redirect(`/${locale}/auth/verify${query}`);
}