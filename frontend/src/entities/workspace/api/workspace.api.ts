import { api } from '@/shared/api/client';
import type {
  AutosaveRequest,
  CompletePreviewRequest,
  PreviewUploadUrlsRequest,
  PreviewUploadUrlsResponse,
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

  listPage: async (query: WorkspaceListQuery = {}): Promise<WorkspacePageResponse<Workspace>> => {
    return api.get<WorkspacePageResponse<Workspace>>(`/api/v1/workspaces${queryString(query)}`);
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

  createPreviewUploadUrls: (id: string, data: PreviewUploadUrlsRequest) =>
    api.post<PreviewUploadUrlsResponse>(`/api/v1/workspaces/${id}/preview-upload-urls`, data),

  completePreview: (id: string, previewId: string, data: CompletePreviewRequest) =>
    api.post<Workspace['preview']>(`/api/v1/workspaces/${id}/previews/${previewId}/complete`, data),

  getState: (id: string, sessionToken?: string) => api.get<WorkspaceState>(`/api/v1/workspaces/${id}/state`, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined),

  saveState: (id: string, state: WorkspaceState, expectedVersion?: number, sessionToken?: string) => {
    const params = new URLSearchParams();
    if (expectedVersion !== undefined) params.set('expectedVersion', String(expectedVersion));
    const query = params.toString() ? `?${params.toString()}` : '';
    return api.put<WorkspaceState>(`/api/v1/workspaces/${id}/state${query}`, state, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined);
  },

  appendEvent: (id: string, event: SandboxEventCommand, sessionToken?: string) =>
    api.post<WorkspaceEventAck>(`/api/v1/workspaces/${id}/events`, event, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined),

  getEvents: (id: string, afterVersion?: number, limit?: number, sessionToken?: string) => {
    const params = new URLSearchParams();
    if (afterVersion !== undefined) params.set('afterVersion', String(afterVersion));
    if (limit !== undefined) params.set('limit', String(limit));
    const encoded = params.toString();
    const query = encoded ? `?${encoded}` : '';
    return api.get<Array<Record<string, unknown>>>(`/api/v1/workspaces/${id}/events${query}`, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined);
  },

  undo: (id: string, expectedVersion?: number, sessionToken?: string) => {
    const params = new URLSearchParams();
    if (expectedVersion !== undefined) params.set('expectedVersion', String(expectedVersion));
    const encoded = params.toString();
    const query = encoded ? `?${encoded}` : '';
    return api.post<WorkspaceState>(`/api/v1/workspaces/${id}/undo${query}`, undefined, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined);
  },

  redo: (id: string, expectedVersion?: number, sessionToken?: string) => {
    const params = new URLSearchParams();
    if (expectedVersion !== undefined) params.set('expectedVersion', String(expectedVersion));
    const encoded = params.toString();
    const query = encoded ? `?${encoded}` : '';
    return api.post<WorkspaceState>(`/api/v1/workspaces/${id}/redo${query}`, undefined, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined);
  },

  publish: (id: string, data?: { title?: string; description?: string }) =>
    api.post<{ workspaceId: string; shareUrl?: string; publishedAt?: string }>(`/api/v1/workspaces/${id}/publish`, data),

  autosave: (id: string, data: AutosaveRequest, sessionToken?: string) =>
    api.post<{ stateVersion: number; savedAt: string }>(`/api/v1/workspaces/${id}/autosave`, data, sessionToken ? { headers: { 'X-Share-Session': sessionToken } } : undefined),
};
