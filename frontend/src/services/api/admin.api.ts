import { api } from './client';
import type { User, Workspace } from '@/types';

export const adminApi = {
  getUsers: () => api.get<User[]>('/admin/users'),
  getUser: (id: string) => api.get<User>(`/admin/users/${id}`),
  updateUser: (id: string, data: Partial<User>) => api.put<User>(`/admin/users/${id}`, data),
  deleteUser: (id: string) => api.delete(`/admin/users/${id}`),
  getLaboratories: () => api.get<Workspace[]>('/admin/laboratories'),
  getStats: () => api.get<{ totalUsers: number; activeLabs: number; experiments: number; health: string }>('/admin/stats'),
};
