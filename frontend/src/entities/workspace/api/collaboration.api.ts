import { api } from '@/shared/api/client';
import { apiQuery } from '@/shared/api/query';
import type {
  JsonObject, WorkspaceChatMessage, WorkspaceChatPage, WorkspaceCommentThread,
  WorkspaceInvitation, WorkspaceMeasurement, WorkspaceMember, WorkspacePermissions,
  WorkspaceShareLink,
} from '@/shared/api/contracts/platform';

export interface ResolvedWorkspaceShare {
  workspaceId: string;
  name: string;
  science: string;
  preview?: { status?: string; variants?: Record<string, { url?: string; width?: number; height?: number; mimeType?: string }>; fallback?: Record<string, unknown> };
  role: 'VIEWER' | 'EDITOR';
  capabilities: string[];
  requiresAuth: boolean;
  expiresAt?: string | null;
  shareSessionToken?: string;
}

export const workspaceCollaborationApi = {
  permissions: (workspaceId: string) => api.get<WorkspacePermissions>(`/api/v1/workspaces/${workspaceId}/permissions`),
  members: (workspaceId: string) => api.get<WorkspaceMember[]>(`/api/v1/workspaces/${workspaceId}/members`),
  invite: (workspaceId: string, request: { emailOrUserId: string; role: 'EDITOR' | 'VIEWER'; expiresAt?: string; message?: string }) =>
    api.post<JsonObject>(`/api/v1/workspaces/${workspaceId}/invitations`, request),
  invitations: (workspaceId: string) => api.get<WorkspaceInvitation[]>(`/api/v1/workspaces/${workspaceId}/invitations`),
  revokeInvitation: (workspaceId: string, invitationId: string) => api.delete<{ message: string }>(`/api/v1/workspaces/${workspaceId}/invitations/${invitationId}`),
  acceptInvitation: (token: string, displayName?: string) => api.post<JsonObject>(`/api/v1/workspace-invitations/${encodeURIComponent(token)}/accept`, displayName ? { displayName } : undefined),
  updateMember: (workspaceId: string, userId: string, role: 'EDITOR' | 'VIEWER') => api.patch<WorkspaceMember>(`/api/v1/workspaces/${workspaceId}/members/${userId}`, { role }),
  removeMember: (workspaceId: string, userId: string) => api.delete<{ message: string }>(`/api/v1/workspaces/${workspaceId}/members/${userId}`),
  createShareLink: (workspaceId: string, request: { role: 'EDITOR' | 'VIEWER'; expiresAt?: string; password?: string; maxUses?: number; allowChat?: boolean; allowComments?: boolean }) =>
    api.post<WorkspaceShareLink & { token?: string }>(`/api/v1/workspaces/${workspaceId}/share-links`, request),
  shareLinks: (workspaceId: string) => api.get<WorkspaceShareLink[]>(`/api/v1/workspaces/${workspaceId}/share-links`),
  updateShareLink: (workspaceId: string, linkId: string, request: Partial<Pick<WorkspaceShareLink, 'role' | 'expiresAt' | 'maxUses' | 'allowChat' | 'allowComments'>>) =>
    api.patch<WorkspaceShareLink>(`/api/v1/workspaces/${workspaceId}/share-links/${linkId}`, request),
  revokeShareLink: (workspaceId: string, linkId: string) => api.delete<{ message: string }>(`/api/v1/workspaces/${workspaceId}/share-links/${linkId}`),
  resolveShareLink: (token: string, password?: string) => api.post<ResolvedWorkspaceShare>('/api/v1/shared-workspaces/resolve', { token, password }),
  chat: (workspaceId: string, before?: string, limit = 50) => api.get<WorkspaceChatPage>(`/api/v1/workspaces/${workspaceId}/chat/messages${apiQuery({ before, limit })}`),
  sendMessage: (workspaceId: string, body: string, replyToMessageId?: string, anchor?: JsonObject) => api.post<WorkspaceChatMessage>(`/api/v1/workspaces/${workspaceId}/chat/messages`, { clientMessageId: crypto.randomUUID(), body, replyToMessageId, anchor }),
  editMessage: (workspaceId: string, messageId: string, body: string) => api.patch<WorkspaceChatMessage>(`/api/v1/workspaces/${workspaceId}/chat/messages/${messageId}`, { body }),
  deleteMessage: (workspaceId: string, messageId: string) => api.delete<{ message: string }>(`/api/v1/workspaces/${workspaceId}/chat/messages/${messageId}`),
  markChatRead: (workspaceId: string, messageId: string) => api.post<{ status: string }>(`/api/v1/workspaces/${workspaceId}/chat/read`, { messageId }),
  comments: (workspaceId: string) => api.get<WorkspaceCommentThread[]>(`/api/v1/workspaces/${workspaceId}/comments`),
  createComment: (workspaceId: string, body: string, anchor?: JsonObject) => api.post<WorkspaceCommentThread>(`/api/v1/workspaces/${workspaceId}/comments`, { body, anchor }),
  replyComment: (workspaceId: string, threadId: string, body: string) => api.post<WorkspaceCommentThread>(`/api/v1/workspaces/${workspaceId}/comments/${threadId}/replies`, { clientMessageId: crypto.randomUUID(), body }),
  setCommentStatus: (workspaceId: string, threadId: string, status: 'OPEN' | 'RESOLVED') => api.patch<WorkspaceCommentThread>(`/api/v1/workspaces/${workspaceId}/comments/${threadId}`, { status }),
  measurements: (workspaceId: string, filters: { kind?: string; from?: string; to?: string; limit?: number } = {}) => api.get<WorkspaceMeasurement[]>(`/api/v1/workspaces/${workspaceId}/measurements${apiQuery(filters)}`),
};
