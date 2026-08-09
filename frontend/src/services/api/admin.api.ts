import { api } from './client';
import type {
  AdminUserResponse,
  AdminUpdateUserRequest,
  AuthSuccessResponse,
} from '@/types';

export const adminApi = {
  getUsers: () =>
    api.get<AdminUserResponse[]>('/api/v1/admin/users'),

  getUser: (id: string) =>
    api.get<AdminUserResponse>(`/api/v1/admin/users/${id}`),

  updateUser: (id: string, data: AdminUpdateUserRequest) =>
    api.put<AdminUserResponse>(`/api/v1/admin/users/${id}`, data),

  deleteUser: (id: string) =>
    api.delete<AuthSuccessResponse>(`/api/v1/admin/users/${id}`),
};
