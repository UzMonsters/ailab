import { api } from './client';
import type { User } from '@/types';

export const userApi = {
  getProfile: () => api.get<User>('/user/profile'),
  updateProfile: (data: Partial<User>) => api.put<User>('/user/profile', data),
};
