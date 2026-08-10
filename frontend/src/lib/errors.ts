export interface NormalizedError {
  message: string;
  status?: number;
}

export function normalizeError(error: unknown, fallback = 'Something went wrong'): NormalizedError {
  if (error instanceof Error) {
    const candidate = error as Error & { status?: unknown };
    return {
      message: error.message || fallback,
      status: typeof candidate.status === 'number' ? candidate.status : undefined,
    };
  }

  if (typeof error === 'object' && error !== null) {
    const candidate = error as { message?: unknown; status?: unknown };
    return {
      message: typeof candidate.message === 'string' ? candidate.message : fallback,
      status: typeof candidate.status === 'number' ? candidate.status : undefined,
    };
  }

  return { message: fallback };
}
