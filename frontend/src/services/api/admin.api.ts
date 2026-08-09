import { api } from './client';
import type { AdminUserResponse, AdminUpdateUserRequest, AuthRegisterResponse } from '@/types';

type BackendAdminUser = Omit<AdminUserResponse, 'active' | 'createdAt'> & {
  language?: string;
  theme?: string;
  applicationSettings?: Record<string, unknown>;
  statistics?: Record<string, number>;
  achievements?: string[];
};

const mapUser = (user: BackendAdminUser): AdminUserResponse => ({
  id: user.id,
  username: user.username,
  email: user.email,
  role: user.role,
  avatarUrl: user.avatarUrl ?? null,
  active: user.role !== 'ROLE_BANNED',
  level: user.level ?? 1,
  createdAt: new Date().toISOString(),
});

export const adminApi = {
  getUsers: async () => (await api.get<BackendAdminUser[]>('/api/v1/admin/users')).map(mapUser),

  getUser: async (id: string) => mapUser(await api.get<BackendAdminUser>(`/api/v1/admin/users/${id}`)),

  createUser: async (data: { username: string; email: string; password: string; role?: AdminUserResponse['role'] }) => {
    const created = await api.post<AuthRegisterResponse>('/api/v1/auth/register', { username: data.username, email: data.email, password: data.password });
    if (data.role && data.role !== 'ROLE_USER') await api.put<{ success: boolean }>(`/api/v1/admin/users/${created.id}`, { username: data.username, email: data.email, role: data.role });
    return created;
  },

  updateUser: async (id: string, data: AdminUpdateUserRequest) => {
    await api.put<{ success: boolean }>(`/api/v1/admin/users/${id}`, { username: data.username, email: data.email, role: data.role });
    return adminApi.getUser(id);
  },

  deleteUser: (id: string) => api.delete<{ success: boolean }>(`/api/v1/admin/users/${id}`),
};
