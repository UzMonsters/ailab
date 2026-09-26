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
  code?: string;
  correlationId?: string;

  constructor(error: ApiError) {
    super(error.message);
    this.name = 'ApiError';
    this.status = error.status;
    this.message = error.message;
    this.errors = error.errors;
    this.code = error.code;
    this.correlationId = error.correlationId || error.traceId;
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

export async function refreshAccessToken(): Promise<boolean> {
  if (!refreshPromise) refreshPromise = tryRefresh();
  const refreshed = await refreshPromise;
  refreshPromise = null;
  return refreshed;
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (!accessToken && !endpoint.startsWith('/api/v1/auth/')) {
    if (!refreshPromise) refreshPromise = tryRefresh();
    const refreshed = await refreshPromise;
    refreshPromise = null;
    if (refreshed && accessToken) {
      headers['Authorization'] = `Bearer ${accessToken}`;
    }
  } else if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  let res = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
    credentials: 'include',
  });

  if (res.status === 401 && !endpoint.startsWith('/api/v1/auth/')) {
    let refreshed = false;
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
    const message = errorData.message || errorData.detail || errorData.error || errorData.title || res.statusText || 'Unknown error';
    throw new ApiErrorImpl({
      status: res.status,
      message,
      code: errorData.code,
      correlationId: errorData.correlationId,
      traceId: errorData.traceId,
      errors: errorData.errors,
      fieldViolations: errorData.fieldViolations,
    });
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return res.json();
}

async function requestBlob(endpoint: string): Promise<Blob> {
  const headers: Record<string, string> = {};
  if (!accessToken && !endpoint.startsWith('/api/v1/auth/')) {
    const refreshed = await refreshAccessToken();
    if (refreshed && accessToken) headers['Authorization'] = `Bearer ${accessToken}`;
  } else if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  let res = await fetch(`${API_BASE}${endpoint}`, { headers, credentials: 'include' });
  if (res.status === 401) {
    const refreshed = await refreshAccessToken();
    if (refreshed && accessToken) {
      headers['Authorization'] = `Bearer ${accessToken}`;
      res = await fetch(`${API_BASE}${endpoint}`, { headers, credentials: 'include' });
    }
  }

  if (!res.ok) {
    let message = res.statusText || 'Download failed';
    try {
      const errorData = await res.json() as Partial<ApiError>;
      message = errorData.message || errorData.detail || errorData.error || errorData.title || message;
    } catch {
      // A failed file endpoint may return an empty response body.
    }
    throw new ApiErrorImpl({ status: res.status, message });
  }

  return res.blob();
}

async function requestBinary<T = void>(
  endpoint: string,
  data: Blob | ArrayBuffer | Uint8Array,
  contentType: string,
  options: RequestInit = {}
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': contentType,
    ...(options.headers as Record<string, string>),
  };

  if (!headers['Authorization']) {
    if (!accessToken && !endpoint.startsWith('/api/v1/auth/')) {
      const refreshed = await refreshAccessToken();
      if (refreshed && accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
      }
    } else if (accessToken) {
      headers['Authorization'] = `Bearer ${accessToken}`;
    }
  }

  const url = endpoint.startsWith('http://') || endpoint.startsWith('https://')
    ? endpoint
    : `${API_BASE}${endpoint.startsWith('/') ? '' : '/'}${endpoint}`;

  let res = await fetch(url, {
    ...options,
    method: options.method || 'PUT',
    headers,
    body: data as BodyInit,
    credentials: 'include',
  });

  if (res.status === 401 && !endpoint.startsWith('/api/v1/auth/')) {
    const refreshed = await refreshAccessToken();
    if (refreshed && accessToken) {
      headers['Authorization'] = `Bearer ${accessToken}`;
      res = await fetch(url, {
        ...options,
        method: options.method || 'PUT',
        headers,
        body: data as BodyInit,
        credentials: 'include',
      });
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
    const message = errorData.message || errorData.detail || errorData.error || errorData.title || res.statusText || 'Binary upload failed';
    throw new ApiErrorImpl({
      status: res.status,
      message,
      code: errorData.code,
      correlationId: errorData.correlationId,
      traceId: errorData.traceId,
      errors: errorData.errors,
      fieldViolations: errorData.fieldViolations,
    });
  }

  if (res.status === 204 || res.headers.get('content-length') === '0') {
    return undefined as T;
  }

  const ct = res.headers.get('content-type');
  if (ct && ct.includes('application/json')) {
    return res.json();
  }
  return undefined as T;
}

export const api = {
  get: <T>(endpoint: string, options?: RequestInit) => request<T>(endpoint, options),
  getBlob: (endpoint: string) => requestBlob(endpoint),
  putBinary: <T = void>(
    endpoint: string,
    data: Blob | ArrayBuffer | Uint8Array,
    contentType: string,
    options?: RequestInit
  ) => requestBinary<T>(endpoint, data, contentType, options),

  post: <T>(endpoint: string, body?: unknown, options?: RequestInit) =>
    request<T>(endpoint, {
      method: 'POST',
      body: body !== undefined ? JSON.stringify(body) : undefined,
      ...options,
    }),

  put: <T>(endpoint: string, body?: unknown, options?: RequestInit) =>
    request<T>(endpoint, {
      method: 'PUT',
      body: body !== undefined ? JSON.stringify(body) : undefined,
      ...options,
    }),

  patch: <T>(endpoint: string, body?: unknown, options?: RequestInit) =>
    request<T>(endpoint, {
      method: 'PATCH',
      body: body !== undefined ? JSON.stringify(body) : undefined,
      ...options,
    }),

  delete: <T>(endpoint: string, body?: unknown, options?: RequestInit) =>
    request<T>(endpoint, {
      method: 'DELETE',
      body: body !== undefined ? JSON.stringify(body) : undefined,
      ...options,
    }),
};

export { ApiErrorImpl as ApiError };
