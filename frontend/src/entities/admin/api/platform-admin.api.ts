import { api } from '@/shared/api/client';
import { apiQuery } from '@/shared/api/query';
import type { JsonObject, PageEnvelope } from '@/shared/api/contracts/platform';

type Query = Record<string, string | number | boolean | undefined>;
const resource = (path: string) => ({
  list: (filters: Query = {}) => api.get<PageEnvelope<JsonObject>>(`${path}${apiQuery(filters)}`),
  get: (id: string) => api.get<JsonObject>(`${path}/${id}`),
  create: (request: JsonObject) => api.post<JsonObject>(path, request),
  patch: (id: string, request: JsonObject) => api.patch<JsonObject>(`${path}/${id}`, request),
  publish: (id: string, request?: JsonObject) => api.post<JsonObject>(`${path}/${id}/publish`, request),
});

export const adminPlatformApi = {
  permissions: () => api.get<JsonObject>('/api/v1/admin/me/permissions'),
  dashboard: {
    summary: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/summary${apiQuery(filters)}`),
    activitySeries: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/activity-series${apiQuery(filters)}`),
    scienceDistribution: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/science-distribution${apiQuery(filters)}`),
    learningSummary: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/learning-summary${apiQuery(filters)}`),
    laboratorySummary: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/laboratory-summary${apiQuery(filters)}`),
    activitySummary: (filters: Query = {}) => api.get<JsonObject>(`/api/v1/admin/dashboard/activity-summary${apiQuery(filters)}`),
    createReport: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/reports', request),
    report: (jobId: string) => api.get<JsonObject>(`/api/v1/admin/reports/${jobId}`),
  },
  audit: {
    list: (filters: Query = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/audit-events${apiQuery(filters)}`),
    get: (eventId: string) => api.get<JsonObject>(`/api/v1/admin/audit-events/${eventId}`),
    export: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/audit-exports', request),
    exportStatus: (jobId: string) => api.get<JsonObject>(`/api/v1/admin/audit-exports/${jobId}`),
    retention: () => api.get<JsonObject>('/api/v1/admin/audit-retention'),
  },
  laboratories: {
    list: (filters: Query = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/laboratory-sessions${apiQuery(filters)}`),
    get: (id: string) => api.get<JsonObject>(`/api/v1/admin/laboratory-sessions/${id}`),
    pause: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/laboratory-sessions/${id}/pause`, request),
    terminate: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/laboratory-sessions/${id}/terminate`, request),
  },
  settings: {
    get: () => api.get<JsonObject>('/api/v1/admin/settings'),
    patch: (request: JsonObject) => api.patch<JsonObject>('/api/v1/admin/settings', request),
    schema: () => api.get<JsonObject>('/api/v1/admin/settings/schema'),
    history: (filters: Query = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/settings/history${apiQuery(filters)}`),
    restore: (version: string | number, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/settings/${version}/restore`, request),
    subjects: () => api.get<{ items: JsonObject[] }>('/api/v1/admin/subjects'),
    patchSubject: (id: string, request: JsonObject) => api.patch<JsonObject>(`/api/v1/admin/subjects/${id}`, request),
  },
  assets: {
    uploadUrls: (request: JsonObject) => api.post<JsonObject>('/api/v1/admin/assets/upload-urls', request),
    complete: (assetId: string, request: JsonObject) => api.post<JsonObject>(`/api/v1/admin/assets/${assetId}/complete`, request),
    get: (assetId: string) => api.get<JsonObject>(`/api/v1/admin/assets/${assetId}`),
  },
  chemistry: {
    elements: resource('/api/v1/admin/chemistry/elements'),
    substances: resource('/api/v1/admin/chemistry/substances'),
    reactions: { ...resource('/api/v1/admin/chemistry/reactions'), validate: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/chemistry/reactions/${id}/validate`, request) },
  },
  equipment: { ...resource('/api/v1/admin/equipment'), savePorts: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/equipment/${id}/ports`, request), saveCompatibility: (id: string, request: JsonObject) => api.put<JsonObject>(`/api/v1/admin/equipment/${id}/compatibility`, request) },
  materials: resource('/api/v1/admin/materials'),
  scenarios: { ...resource('/api/v1/admin/scenarios'), validate: (id: string, request?: JsonObject) => api.post<JsonObject>(`/api/v1/admin/scenarios/${id}/validate`, request) },
  safetyRules: resource('/api/v1/admin/safety-rules'),
};
