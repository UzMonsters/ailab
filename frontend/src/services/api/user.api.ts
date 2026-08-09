import { api } from './client';
import type {
  UserMeResponse,
  UserPublicResponse,
  UserUpdateRequest,
  UserPreferencesResponse,
  UserPreferencesUpdateRequest,
  UserStatisticsResponse,
  UserAvatarRequest,
  AuthSuccessResponse,
} from '@/types';

export const userApi = {
  getMe: () =>
    api.get<UserMeResponse>('/api/v1/users/me'),

  updateMe: (data: UserUpdateRequest) =>
    api.put<AuthSuccessResponse>('/api/v1/users/me', data),

  getPreferences: () =>
    api.get<UserPreferencesResponse>('/api/v1/users/me/preferences'),

  updatePreferences: (data: UserPreferencesUpdateRequest) =>
    api.put<AuthSuccessResponse>('/api/v1/users/me/preferences', data),

  getStatistics: () =>
    api.get<UserStatisticsResponse>('/api/v1/users/me/statistics'),

  deleteMe: () =>
    api.delete<AuthSuccessResponse>('/api/v1/users/me'),

  getUser: (id: string) =>
    api.get<UserPublicResponse>(`/api/v1/users/${id}`),

  uploadAvatar: (avatarUrl: string) =>
    api.put<AuthSuccessResponse>('/api/v1/users/avatar', { avatarUrl } satisfies UserAvatarRequest),

  deleteAvatar: () =>
    api.delete<AuthSuccessResponse>('/api/v1/users/avatar'),
};
