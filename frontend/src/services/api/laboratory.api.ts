import { api } from './client';
import type { Workspace } from '@/types';

export const laboratoryApi = {
  getWorkspaces: () => api.get<Workspace[]>('/laboratories'),
  getWorkspace: (id: string) => api.get<Workspace>(`/laboratories/${id}`),
  createWorkspace: (data: { name: string; science: string }) =>
    api.post<Workspace>('/laboratories', data),
  updateWorkspace: (id: string, data: Partial<Workspace>) =>
    api.put<Workspace>(`/laboratories/${id}`, data),
  deleteWorkspace: (id: string) => api.delete(`/laboratories/${id}`),
};
