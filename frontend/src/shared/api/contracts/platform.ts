export type JsonObject = Record<string, unknown>;

export interface WorkspacePermissions {
  role: 'OWNER' | 'EDITOR' | 'VIEWER' | 'GUEST_VIEWER' | 'GUEST_EDITOR';
  capabilities: string[];
}

export interface WorkspaceMember {
  userId: string;
  displayName: string;
  emailMasked: string;
  avatarUrl: string | null;
  role: 'OWNER' | 'EDITOR' | 'VIEWER';
  status: string;
  joinedAt: string;
  lastSeenAt: string | null;
}

export interface WorkspaceInvitation {
  invitationId: string;
  workspaceId: string;
  invitee: Record<string, string>;
  role: 'EDITOR' | 'VIEWER';
  status: string;
  expiresAt: string | null;
  createdAt: string;
}

export interface WorkspaceShareLink {
  id: string;
  linkId: string;
  url?: string;
  role: 'EDITOR' | 'VIEWER';
  expiresAt: string | null;
  maxUses: number | null;
  useCount: number;
  allowChat: boolean;
  allowComments: boolean;
  capabilities: string[];
  lastUsedAt: string | null;
  createdAt: string;
}

export interface WorkspaceChatMessage {
  id: string;
  clientMessageId: string;
  workspaceId: string;
  author: { id: string; displayName: string; avatarUrl: string | null };
  body: string;
  replyToMessageId: string | null;
  anchor: JsonObject | null;
  createdAt: string;
  editedAt: string | null;
  deletedAt: string | null;
}

export interface WorkspaceChatPage {
  items: WorkspaceChatMessage[];
  nextCursor: string | null;
  unreadCount: number;
}

export interface WorkspaceCommentReply {
  id: string;
  threadId: string;
  author: { id: string; displayName: string; avatarUrl: string | null };
  body: string;
  createdAt: string;
}

export interface WorkspaceCommentThread {
  id: string;
  workspaceId: string;
  author: { id: string; displayName: string; avatarUrl: string | null };
  anchor: JsonObject | null;
  status: 'OPEN' | 'RESOLVED';
  replies: WorkspaceCommentReply[];
  createdAt: string;
  updatedAt: string;
}

export interface WorkspaceMeasurement {
  id: string;
  kind: string;
  value: number;
  unit: string;
  sensorId: string | null;
  targetId: string | null;
  recordedAt: string;
}

export interface LearningLevelSummary {
  id: string;
  trackId?: string;
  levelNumber: number;
  order: number;
  title: string;
  summary?: string;
  difficulty?: string;
  estimatedMinutes?: number;
  status?: string;
  isComingSoon?: boolean;
  isLocked?: boolean;
  publishedVersion?: number | null;
  [key: string]: unknown;
}

export interface LearningTrackMap {
  track: { id: string; code: string; title: string; description?: string; [key: string]: unknown };
  levels: LearningLevelSummary[];
}

export interface BookManifest {
  book: JsonObject & { id: string; slug: string; title?: string; description?: string };
  chapters: Array<JsonObject & { id: string; title?: string; pages?: Array<JsonObject & { id: string; position?: number; title?: string }> }>;
  locale?: string;
  publishedVersion?: number;
  [key: string]: unknown;
}

export interface PageEnvelope<T> {
  items?: T[];
  content?: T[];
  page?: number | JsonObject;
  size?: number;
  total?: number;
  totalElements?: number;
  totalPages?: number;
  facets?: JsonObject;
}
