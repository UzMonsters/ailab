import { api } from '@/shared/api/client';
import { apiQuery } from '@/shared/api/query';
import type { JsonObject, LearningTrackMap, PageEnvelope } from '@/shared/api/contracts/platform';

export const learningApi = {
  track: (code = 'chemistry', locale = 'ru') => api.get<LearningTrackMap>(`/api/v1/learning/tracks/${encodeURIComponent(code)}${apiQuery({ locale })}`),
  level: (id: string, locale = 'ru') => api.get<JsonObject>(`/api/v1/learning/levels/${id}${apiQuery({ locale })}`),
  startAttempt: (levelId: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/learning/levels/${levelId}/attempts`, request),
  attempt: (attemptId: string) => api.get<JsonObject>(`/api/v1/learning/attempts/${attemptId}`),
  sendEvent: (attemptId: string, event: JsonObject) => api.post<JsonObject>(`/api/v1/learning/attempts/${attemptId}/events`, event),
  evaluate: (attemptId: string, checkpointId: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/learning/attempts/${attemptId}/checkpoints/${checkpointId}/evaluate`, request),
  guide: (attemptId: string, locale = 'ru', mode = 'hint') => api.get<JsonObject>(`/api/v1/learning/attempts/${attemptId}/guide${apiQuery({ locale, mode })}`),
  requestHint: (attemptId: string, request: JsonObject, locale = 'ru') => api.post<JsonObject>(`/api/v1/learning/attempts/${attemptId}/hint-requests${apiQuery({ locale })}`, request),
  complete: (attemptId: string, request?: JsonObject, locale = 'ru') => api.post<JsonObject>(`/api/v1/learning/attempts/${attemptId}/complete${apiQuery({ locale })}`, request),
  progress: (track = 'chemistry') => api.get<JsonObject>(`/api/v1/users/me/learning-progress${apiQuery({ track })}`),
};

export const adminLearningApi = {
  overview: (filters: JsonObject = {}) => api.get<JsonObject>(`/api/v1/admin/learning/overview${apiQuery(filters as Record<string, string>)}`),
  tracks: (filters: JsonObject = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/learning/tracks${apiQuery(filters as Record<string, string>)}`),
  createTrack: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/learning/tracks', request),
  levels: (filters: JsonObject = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/learning/levels${apiQuery(filters as Record<string, string>)}`),
  createLevel: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/learning/levels', request),
  level: (id: string) => api.get<JsonObject>(`/api/v1/admin/learning/levels/${id}?include=steps,scenario,requirements,rewards,translations`),
  patchLevel: (id: string, request: JsonObject) => api.patch<JsonObject>(`/api/v1/admin/learning/levels/${id}`, request),
  saveSteps: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/learning/levels/${id}/steps`, request),
  saveScenario: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/learning/levels/${id}/scenario`, request),
  saveRequirements: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/learning/levels/${id}/requirements`, request),
  saveRewards: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/learning/levels/${id}/rewards`, request),
  saveTranslations: (id: string, locale: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/learning/levels/${id}/translations/${locale}`, request),
  validate: (id: string, version?: number) => api.post<JsonObject>(`/api/v1/admin/learning/levels/${id}/validate`, version === undefined ? undefined : { version }),
  preview: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/learning/levels/${id}/preview-attempts`, request),
  publish: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/learning/levels/${id}/publish`, request),
  archive: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/learning/levels/${id}/archive`, request),
  analytics: (id: string) => api.get<JsonObject>(`/api/v1/admin/learning/levels/${id}/analytics`),
  chapters: (trackId?: string) => api.get<JsonObject[]>(`/api/v1/admin/learning/chapters${apiQuery({ trackId })}`),
  createChapter: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/learning/chapters', request),
  tasks: () => api.get<JsonObject[]>('/api/v1/admin/learning/tasks'),
  createTask: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/learning/tasks', request),
  rewards: () => api.get<JsonObject[]>('/api/v1/admin/learning/rewards'),
  createReward: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/learning/rewards', request),
  progress: (filters: JsonObject = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/learning/progress${apiQuery(filters as Record<string, string>)}`),
  resetProgress: (userId: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/learning/progress/${userId}/reset`, request),
  localization: (filters: JsonObject = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/learning/localization${apiQuery(filters as Record<string, string>)}`),
};
