const ADMIN_LOCALE = 'en';

export function adminHref(path: string): string {
  const clean = path.startsWith('/') ? path : `/${path}`;
  return `/${ADMIN_LOCALE}/admin${clean}`;
}

export function isAdminRoute(pathname: string): boolean {
  const segments = pathname.split('/');
  return segments.length >= 3 && segments[2] === 'admin';
}

export function canonicalAdminPath(pathname: string): string | null {
  if (!isAdminRoute(pathname)) return null;
  const segments = pathname.split('/');
  const rest = segments.slice(3).join('/');
  const queryIndex = rest.indexOf('?');
  const path = queryIndex >= 0 ? rest.substring(0, queryIndex) : rest;
  const query = queryIndex >= 0 ? rest.substring(queryIndex) : '';
  return `/${ADMIN_LOCALE}/admin/${path}${query}`;
}
