import createMiddleware from 'next-intl/middleware';
import { locales, defaultLocale } from './i18n/config';
import type { NextRequest } from 'next/server';

const intlProxy = createMiddleware({
  locales,
  defaultLocale,
  localeDetection: true,
});

export default function proxy(request: NextRequest) {
  return intlProxy(request);
}

export const config = {
  matcher: ['/', '/(ru|uz|en)/:path*'],
};
