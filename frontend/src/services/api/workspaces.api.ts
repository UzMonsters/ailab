import { api } from './client';
import type { Workspace } from '@/types';

const MOCK_MODE = true;

const mockStore = {
  getWorkspaces: (): Workspace[] => {
    if (typeof window === 'undefined') return [];
    try {
      return JSON.parse(localStorage.getItem('ailab_workspaces') || '[]');
    } catch { return []; }
  },
  saveWorkspaces: (workspaces: Workspace[]) => {
    if (typeof window === 'undefined') return;
    localStorage.setItem('ailab_workspaces', JSON.stringify(workspaces));
  },
};

function generateId(): string {
  return `ws_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;
}

export const workspacesApi = {
  list: async (): Promise<Workspace[]> => {
    if (MOCK_MODE) return mockStore.getWorkspaces();
    return api.get<Workspace[]>('/api/v1/workspaces');
  },

  get: async (id: string): Promise<Workspace> => {
    if (MOCK_MODE) {
      const ws = mockStore.getWorkspaces().find(w => w.id === id);
      if (!ws) throw new Error('Workspace not found');
      return ws;
    }
    return api.get<Workspace>(`/api/v1/workspaces/${id}`);
  },

  create: async (name: string, science: Workspace['science'] = 'chemistry'): Promise<Workspace> => {
    if (MOCK_MODE) {
      const now = new Date().toISOString();
      const ws: Workspace = { id: generateId(), name, science, createdAt: now, updatedAt: now, isFavorite: false, isDeleted: false };
      const all = mockStore.getWorkspaces();
      all.unshift(ws);
      mockStore.saveWorkspaces(all);
      return ws;
    }
    return api.post<Workspace>('/api/v1/workspaces', { name, science });
  },

  update: async (id: string, data: Partial<Pick<Workspace, 'name' | 'isFavorite' | 'isDeleted'>>): Promise<Workspace> => {
    if (MOCK_MODE) {
      const all = mockStore.getWorkspaces();
      const idx = all.findIndex(w => w.id === id);
      if (idx === -1) throw new Error('Workspace not found');
      all[idx] = { ...all[idx], ...data, updatedAt: new Date().toISOString() };
      mockStore.saveWorkspaces(all);
      return all[idx];
    }
    return api.put<Workspace>(`/api/v1/workspaces/${id}`, data);
  },

  duplicate: async (id: string): Promise<Workspace> => {
    if (MOCK_MODE) {
      const original = mockStore.getWorkspaces().find(w => w.id === id);
      if (!original) throw new Error('Workspace not found');
      const copy: Workspace = { ...original, id: generateId(), name: `${original.name} (copy)`, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      const all = mockStore.getWorkspaces();
      all.unshift(copy);
      mockStore.saveWorkspaces(all);
      return copy;
    }
    return api.post<Workspace>(`/api/v1/workspaces/${id}/duplicate`);
  },

  delete: async (id: string): Promise<{ message: string }> => {
    if (MOCK_MODE) {
      const all = mockStore.getWorkspaces().filter(w => w.id !== id);
      mockStore.saveWorkspaces(all);
      return { message: 'Deleted' };
    }
    return api.delete<{ message: string }>(`/api/v1/workspaces/${id}`);
  },
};
