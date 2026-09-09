import { api } from '@/shared/api/client';
import { apiQuery } from '@/shared/api/query';
import type { JsonObject, PageEnvelope } from '@/shared/api/contracts/platform';

export interface AdminUserListItem {
  id: string; displayName: string; email: string; role: string; status: string;
  level: number; xp: number; lastActiveAt: string | null; createdAt: string; version: number;
}

export const adminApi = {
  getUsers: (filters: Record<string, string | number | undefined> = {}) => api.get<PageEnvelope<AdminUserListItem> & { items: AdminUserListItem[] }>(`/api/v1/admin/users${apiQuery(filters)}`),
  getUser: (id: string) => api.get<JsonObject>(`/api/v1/admin/users/${id}`),
  patchUser: (id: string, data: JsonObject) => api.patch<AdminUserListItem>(`/api/v1/admin/users/${id}`, data),
  updateUser: (id: string, data: JsonObject) => api.put<{ success: boolean }>(`/api/v1/admin/users/${id}`, data),
  blockUser: (id: string, reason: string, until?: string) => api.post<JsonObject>(`/api/v1/admin/users/${id}/block`, { reason, until }),
  unblockUser: (id: string, reason = 'Разблокировано администратором') => api.post<JsonObject>(`/api/v1/admin/users/${id}/unblock`, { reason }),
  activity: (id: string, filters: Record<string, string | number | undefined> = {}) => api.get<PageEnvelope<JsonObject>>(`/api/v1/admin/users/${id}/activity${apiQuery(filters)}`),
  learningProgress: (id: string, track = 'chemistry') => api.get<JsonObject>(`/api/v1/admin/users/${id}/learning-progress${apiQuery({ track })}`),
  deleteUser: (id: string, reason = 'Удалено администратором') => api.delete<JsonObject>(`/api/v1/admin/users/${id}`),
};
