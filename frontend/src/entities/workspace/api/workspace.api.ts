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
    const response = await api.get<WorkspacePageResponse<Workspace>>(`/api/v1/workspaces${queryString(query)}`);
    return response.items;
  },

  get: (id: string) => api.get<Workspace>(`/api/v1/workspaces/${id}`),

  create: (name: string, science: Workspace['science'] = 'chemistry') =>
    api.post<Workspace>('/api/v1/workspaces', { name, science }),

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

  getState: (id: string) => api.get<WorkspaceState>(`/api/v1/workspaces/${id}/state`),

  saveState: (id: string, state: WorkspaceState, expectedVersion?: number) =>
    api.put<WorkspaceState>(
      `/api/v1/workspaces/${id}/state${expectedVersion === undefined ? '' : `?expectedVersion=${expectedVersion}`}`,
      state,
    ),

  appendEvent: (id: string, event: SandboxEventCommand) =>
    api.post<WorkspaceEventAck>(`/api/v1/workspaces/${id}/events`, event),

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
