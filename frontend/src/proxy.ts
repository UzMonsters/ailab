import { NextResponse } from 'next/server';
import createMiddleware from 'next-intl/middleware';
import { locales, defaultLocale } from './i18n/config';
import type { NextRequest } from 'next/server';

const intlProxy = createMiddleware({
  locales,
  defaultLocale,
  localeDetection: true,
});

export default function proxy(request: NextRequest) {
  const { pathname, search } = request.nextUrl;

  const adminMatch = pathname.match(/^\/(ru|uz|en)\/admin(\/.*)?$/);
  if (adminMatch) {
    const currentLocale = adminMatch[1];
    if (currentLocale !== 'en') {
      const rest = adminMatch[2] || '';
      const url = request.nextUrl.clone();
      url.pathname = `/en/admin${rest}`;
      return NextResponse.redirect(url);
    }
  }

  return intlProxy(request);
}

export const config = {
  matcher: ['/', '/(ru|uz|en)/:path*'],
};
