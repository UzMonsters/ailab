import { api } from '@/shared/api/client';
import type {
  AutosaveRequest,
  SandboxEventCommand,
  Workspace,
  WorkspaceEventAck,
  WorkspacePageResponse,
  WorkspaceState,
} from '@/types';

export interface WorkspaceListQuery {
  science?: Workspace['science'];
  search?: string;
  sort?: string;
  page?: number;
  size?: number;
  includeDeleted?: boolean;
}

function queryString(query: WorkspaceListQuery = {}): string {
  const params = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== '') params.set(key, String(value));
  });
  const result = params.toString();
  return result ? `?${result}` : '';
}

export const workspacesApi = {
  list: async (query: WorkspaceListQuery = {}): Promise<Workspace[]> => {
    try {
      const response = await api.get<WorkspacePageResponse<Workspace>>(`/api/v1/workspaces${queryString(query)}`);
      return response.items;
    } catch {
      return [{ id: 'mock-ws-1', name: 'Offline Sandbox', science: 'chemistry', isFavorite: false, isDeleted: false, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(), thumbnail: undefined, accessLevel: 'owner', ownerId: '1' } as Workspace];
    }
  },

  get: async (id: string) => {
    try {
      return await api.get<Workspace>(`/api/v1/workspaces/${id}`);
    } catch {
      return { id, name: 'Offline Sandbox', science: 'chemistry', isFavorite: false, isDeleted: false, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(), accessLevel: 'owner', ownerId: '1' } as Workspace;
    }
  },

  create: async (name: string, science: Workspace['science'] = 'chemistry') => {
    try {
      return await api.post<Workspace>('/api/v1/workspaces', { name, science });
    } catch {
      return { id: `mock-${Date.now()}`, name, science, isFavorite: false, isDeleted: false, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(), accessLevel: 'owner', ownerId: '1' } as Workspace;
    }
  },

  update: (
    id: string,
    data: Partial<Pick<Workspace, 'name' | 'isFavorite' | 'isDeleted' | 'thumbnail'>> & { stateVersion?: number },
  ) => api.put<Workspace>(`/api/v1/workspaces/${id}`, data),

  duplicate: (id: string, name?: string) =>
    api.post<Workspace>(`/api/v1/workspaces/${id}/duplicate`, name ? { name } : undefined),

  delete: (id: string) => api.delete<{ message: string }>(`/api/v1/workspaces/${id}`),

  restore: (id: string) => api.post<Workspace>(`/api/v1/workspaces/${id}/restore`),

  saveThumbnail: (id: string, data: { svg?: string; width?: number; height?: number; imageData?: string }) =>
    api.post<{ thumbnailUrl: string; updatedAt: string }>(`/api/v1/workspaces/${id}/thumbnail`, data),

  getState: async (id: string) => {
    try {
      return await api.get<WorkspaceState>(`/api/v1/workspaces/${id}/state`);
    } catch {
      return { version: 1, scene: { objects: {}, connections: {} } } as unknown as WorkspaceState;
    }
  },

  saveState: async (id: string, state: WorkspaceState, expectedVersion?: number) => {
    try {
      return await api.put<WorkspaceState>(
        `/api/v1/workspaces/${id}/state${expectedVersion === undefined ? '' : `?expectedVersion=${expectedVersion}`}`,
        state,
      );
    } catch {
      return state;
    }
  },

  appendEvent: async (id: string, event: SandboxEventCommand) => {
    try {
      return await api.post<WorkspaceEventAck>(`/api/v1/workspaces/${id}/events`, event);
    } catch {
      return { status: 'acknowledged', eventId: crypto.randomUUID() } as unknown as WorkspaceEventAck;
    }
  },

  getEvents: (id: string, afterVersion?: number, limit?: number) => {
    const params = new URLSearchParams();
    if (afterVersion !== undefined) params.set('afterVersion', String(afterVersion));
    if (limit !== undefined) params.set('limit', String(limit));
    const encoded = params.toString();
    const query = encoded ? `?${encoded}` : '';
    return api.get<Array<Record<string, unknown>>>(`/api/v1/workspaces/${id}/events${query}`);
  },

  undo: (id: string, expectedVersion?: number) =>
    api.post<WorkspaceState>(`/api/v1/workspaces/${id}/undo${expectedVersion === undefined ? '' : `?expectedVersion=${expectedVersion}`}`),

  redo: (id: string, expectedVersion?: number) =>
    api.post<WorkspaceState>(`/api/v1/workspaces/${id}/redo${expectedVersion === undefined ? '' : `?expectedVersion=${expectedVersion}`}`),

  publish: (id: string, data?: { title?: string; description?: string }) =>
    api.post<{ workspaceId: string; shareUrl?: string; publishedAt?: string }>(`/api/v1/workspaces/${id}/publish`, data),

  autosave: (id: string, data: AutosaveRequest) =>
    api.post<{ stateVersion: number; savedAt: string }>(`/api/v1/workspaces/${id}/autosave`, data),
};
