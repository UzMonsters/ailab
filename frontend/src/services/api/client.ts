import type { ApiError } from '@/types';

const configuredApiBase = process.env.NEXT_PUBLIC_API_URL?.trim().replace(/\/$/, '');
const API_BASE = configuredApiBase || (
  process.env.NODE_ENV === 'production'
    ? 'https://ailab-api-1h23.onrender.com'
    : 'http://localhost:8080'
);

export function getApiBaseUrl(): string {
  return API_BASE;
}

let accessToken: string | null = null;
let refreshPromise: Promise<boolean> | null = null;

export function setAccessToken(token: string | null) {
  accessToken = token;
}

export function getAccessToken(): string | null {
  return accessToken;
}

class ApiErrorImpl extends Error {
  status: number;
  message: string;
  errors?: Record<string, string>;

  constructor(error: ApiError) {
    super(error.message);
    this.name = 'ApiError';
    this.status = error.status;
    this.message = error.message;
    this.errors = error.errors;
    if (Array.isArray(error.fieldViolations) && error.fieldViolations.length > 0) {
      this.errors = error.fieldViolations.reduce<Record<string, string>>((acc, v) => {
        acc[v.field] = v.message;
        return acc;
      }, {});
    }
  }
}

async function tryRefresh(): Promise<boolean> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' },
    });
    if (!res.ok) return false;
    const data = await res.json();
    if (data.accessToken) {
      accessToken = data.accessToken;
      return true;
    }
    return false;
  } catch {
    return false;
  }
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  let res = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  if (res.status === 401) {
    let refreshed = false;
    if (accessToken) {
      if (!refreshPromise) {
        refreshPromise = tryRefresh();
      }
      refreshed = await refreshPromise;
      refreshPromise = null;

      if (refreshed) {
        headers['Authorization'] = `Bearer ${accessToken}`;
        res = await fetch(`${API_BASE}${endpoint}`, {
          ...options,
          headers,
          credentials: 'include',
        });
      }
    }

    if (res.status === 401 && typeof window !== 'undefined' && !endpoint.startsWith('/api/v1/auth/')) {
      accessToken = null;
      window.dispatchEvent(new CustomEvent('auth:unauthorized'));
    }
  }

  if (!res.ok) {
    let errorData: Partial<ApiError>;
    try {
      errorData = await res.json();
    } catch {
      errorData = { message: res.statusText };
    }
    throw new ApiErrorImpl({
      status: res.status,
      message: errorData.message || 'Unknown error',
      errors: errorData.errors,
      fieldViolations: errorData.fieldViolations,
    });
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return res.json();
}

export const api = {
  get: <T>(endpoint: string) => request<T>(endpoint),

  post: <T>(endpoint: string, body?: unknown) =>
    request<T>(endpoint, {
      method: 'POST',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    }),

  put: <T>(endpoint: string, body?: unknown) =>
    request<T>(endpoint, {
      method: 'PUT',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    }),

  patch: <T>(endpoint: string, body?: unknown) =>
    request<T>(endpoint, {
      method: 'PATCH',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    }),

  delete: <T>(endpoint: string) =>
    request<T>(endpoint, { method: 'DELETE' }),
};

export { ApiErrorImpl as ApiError };
