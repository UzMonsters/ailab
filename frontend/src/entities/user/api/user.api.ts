import { api } from '@/shared/api/client';
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

type BackendUser = { id: string; username: string; email: string; avatarUrl?: string | null; createdAt?: string | null; role?: UserMeResponse['role'] };
type BackendPreferences = { language?: string; theme?: string; applicationSettings?: Record<string, unknown> };
type BackendStatistics = { statistics?: Record<string, number>; lastActiveTimestamp?: string | null };
const enumValue = <T extends string>(value: unknown, fallback: T): T => typeof value === 'string' ? value as T : fallback;

export const userApi = {
  getMe: async () => {
    const data = await api.get<BackendUser>('/api/v1/users/me');
    return { ...data, role: data.role || 'ROLE_USER', avatarUrl: data.avatarUrl || null, createdAt: data.createdAt || '' } satisfies UserMeResponse;
  },

  updateMe: (data: UserUpdateRequest) =>
    api.put<AuthSuccessResponse>('/api/v1/users/me', data),

  getPreferences: async () => {
    const data = await api.get<BackendPreferences>('/api/v1/users/me/preferences');
    const settings = data.applicationSettings || {};
    return { theme: enumValue(data.theme, 'SYSTEM') as UserPreferencesResponse['theme'], defaultTemperatureUnit: enumValue(settings.defaultTemperatureUnit, 'CELSIUS') as UserPreferencesResponse['defaultTemperatureUnit'], defaultPressureUnit: enumValue(settings.defaultPressureUnit, 'ATMOSPHERE') as UserPreferencesResponse['defaultPressureUnit'], defaultVolumeUnit: enumValue(settings.defaultVolumeUnit, 'MILLILITER') as UserPreferencesResponse['defaultVolumeUnit'], autoSaveEnabled: settings.autoSaveEnabled !== false };
  },

  updatePreferences: (data: UserPreferencesUpdateRequest) => api.put<AuthSuccessResponse>('/api/v1/users/me/preferences', { language: undefined, theme: data.theme?.toLowerCase(), applicationSettings: { defaultTemperatureUnit: data.defaultTemperatureUnit, defaultPressureUnit: data.defaultPressureUnit, defaultVolumeUnit: data.defaultVolumeUnit, autoSaveEnabled: data.autoSaveEnabled } }),

  getStatistics: async () => {
    const data = await api.get<BackendStatistics>('/api/v1/users/me/statistics'); const values = data.statistics || {};
    return { totalExperimentsRun: values.totalExperimentsRun || 0, totalFormulasParsed: values.totalFormulasParsed || 0, totalEquationsBalanced: values.totalEquationsBalanced || 0, safetyViolationsTriggered: values.safetyViolationsTriggered || 0, lastActiveTimestamp: data.lastActiveTimestamp || '' } satisfies UserStatisticsResponse;
  },

  deleteMe: () =>
    api.delete<AuthSuccessResponse>('/api/v1/users/me'),

  getUser: (id: string) =>
    api.get<UserPublicResponse>(`/api/v1/users/${id}`),

  uploadAvatar: (avatarUrl: string) =>
    api.put<AuthSuccessResponse>('/api/v1/users/avatar', { avatarUrl } satisfies UserAvatarRequest),

  deleteAvatar: () =>
    api.delete<AuthSuccessResponse>('/api/v1/users/avatar'),
};
