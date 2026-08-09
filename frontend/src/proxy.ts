import createMiddleware from 'next-intl/middleware';
import { locales, defaultLocale } from './i18n/config';

const intlProxy = createMiddleware({
  locales,
  defaultLocale,
  localeDetection: true,
});

export default function proxy(request: Request) {
  return intlProxy(request);
}

export const config = {
  matcher: ['/', '/(ru|uz|en)/:path*'],
};
