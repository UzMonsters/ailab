import { api } from '@/shared/api/client';
import { apiQuery } from '@/shared/api/query';
import type { BookManifest, JsonObject, PageEnvelope } from '@/shared/api/contracts/platform';

export const bookApi = {
  manifest: (slug: string, locale = 'ru') => api.get<BookManifest>(`/api/v1/books/${encodeURIComponent(slug)}/manifest${apiQuery({ locale })}`),
  chapter: (slug: string, chapterId: string, locale = 'ru') => api.get<JsonObject>(`/api/v1/books/${encodeURIComponent(slug)}/chapters/${chapterId}${apiQuery({ locale })}`),
  progress: (bookId: string) => api.get<JsonObject>(`/api/v1/users/me/book-progress/${bookId}`),
  saveProgress: (bookId: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/users/me/book-progress/${bookId}`, request),
};

export const adminBookApi = {
  list: (filters: JsonObject = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/books${apiQuery(filters as Record<string, string>)}`),
  create: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/books', request),
  get: (bookId: string) => api.get<JsonObject>(`/api/v1/admin/books/${bookId}`),
  patch: (bookId: string, request: JsonObject) => api.patch<JsonObject>(`/api/v1/admin/books/${bookId}`, request),
  createChapter: (bookId: string, request: JsonObject) => api.post<JsonObject>(`/api/v1/admin/books/${bookId}/chapters`, request),
  patchChapter: (bookId: string, chapterId: string, request: JsonObject) => api.patch<JsonObject>(`/api/v1/admin/books/${bookId}/chapters/${chapterId}`, request),
  deleteChapter: (bookId: string, chapterId: string) => api.delete<JsonObject>(`/api/v1/admin/books/${bookId}/chapters/${chapterId}`),
  createPage: (bookId: string, request: JsonObject) => api.post<JsonObject>(`/api/v1/admin/books/${bookId}/pages`, request),
  patchPage: (bookId: string, pageId: string, request: JsonObject) => api.patch<JsonObject>(`/api/v1/admin/books/${bookId}/pages/${pageId}`, request),
  saveBlocks: (bookId: string, pageId: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/books/${bookId}/pages/${pageId}/blocks`, request),
  validate: (bookId: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/books/${bookId}/validate`, request),
  publish: (bookId: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/books/${bookId}/publish`, request),
  rollback: (bookId: string, request: JsonObject) => api.post<JsonObject>(`/api/v1/admin/books/${bookId}/rollback`, request),
};
