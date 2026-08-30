import type { WorkspaceRepository } from './WorkspaceRepository';
import type { WorkspaceSnapshot } from './Workspace';
import type { WorkspaceState, WorkspaceEventAck, SandboxEventCommand } from '@/types';

const STORAGE_KEY_PREFIX = 'ailab_workspace_';
const SNAPSHOT_VERSION = 2;

interface StoredSnapshot {
  version: number;
  stateVersion: number;
  snapshot: WorkspaceSnapshot;
  savedAt: string;
}

/**
 * Persists workspace state to localStorage.
 * Enables scene restoration after page reload without backend.
 */
export class LocalWorkspaceRepository implements WorkspaceRepository {
  private stateVersion = 0;

  async getState(workspaceId: string): Promise<WorkspaceState> {
    try {
      const raw = localStorage.getItem(STORAGE_KEY_PREFIX + workspaceId);
      if (!raw) return this.emptyState(workspaceId);
      const stored: StoredSnapshot = JSON.parse(raw);
      // Handle version migration
      if (stored.version !== SNAPSHOT_VERSION) return this.emptyState(workspaceId);
      this.stateVersion = stored.stateVersion ?? 0;
      return {
        stateVersion: this.stateVersion,
        sessionId: null,
        viewport: stored.snapshot.scene?.camera ?? { zoom: 1, x: 0, y: 0 },
        items: (stored.snapshot.scene?.objects ?? []) as unknown as Record<string, unknown>[],
        connections: (stored.snapshot.scene?.connections ?? []) as unknown as Record<string, unknown>[],
        workspaceId,
        grid: { enabled: true },
        log: [],
        updatedAt: stored.savedAt,
      };
    } catch {
      return this.emptyState(workspaceId);
    }
  }

  async saveState(workspaceId: string, snapshot: WorkspaceSnapshot): Promise<WorkspaceState> {
    try {
      this.stateVersion += 1;
      const stored: StoredSnapshot = {
        version: SNAPSHOT_VERSION,
        stateVersion: this.stateVersion,
        snapshot,
        savedAt: new Date().toISOString(),
      };
      localStorage.setItem(STORAGE_KEY_PREFIX + workspaceId, JSON.stringify(stored));
    } catch {
      // localStorage might be full or unavailable
    }
    return { ...this.emptyState(workspaceId), stateVersion: this.stateVersion, viewport: snapshot.scene.camera, items: snapshot.scene.objects as unknown as Record<string, unknown>[], connections: snapshot.scene.connections as unknown as Record<string, unknown>[], updatedAt: snapshot.updatedAt };
  }

  async appendEvent(workspaceId: string, event: SandboxEventCommand): Promise<WorkspaceEventAck> {
    // For local storage, events just bump the version
    // Full event log could be added later
    this.stateVersion += 1;
    return {
      clientEventId: event.clientEventId,
      eventId: `local-${this.stateVersion}`,
      eventType: event.eventType,
      workspaceId,
      sessionId: null,
      stateVersion: this.stateVersion,
      stateDelta: {},
      safetyWarnings: [],
      occurredAt: new Date().toISOString(),
    };
  }

  private emptyState(workspaceId = 'local'): WorkspaceState {
    return {
      workspaceId,
      stateVersion: 0,
      sessionId: null,
      viewport: { zoom: 1, x: 0, y: 0 },
      grid: { enabled: true },
      items: [],
      connections: [],
      log: [],
      updatedAt: new Date(0).toISOString(),
    };
  }
}
