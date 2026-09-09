import { workspacesApi } from '@/entities/workspace/api/workspace.api';
import type { WorkspaceState, WorkspaceEventAck, AutosaveRequest, SandboxEventCommand } from '@/types';
import type { WorkspaceSnapshot } from './Workspace';

export interface WorkspaceRepository {
  getState(workspaceId: string): Promise<WorkspaceState>;
  saveState(workspaceId: string, state: WorkspaceSnapshot): Promise<WorkspaceState>;
  appendEvent(workspaceId: string, event: SandboxEventCommand): Promise<WorkspaceEventAck>;
  autosave?(workspaceId: string, data: AutosaveRequest): Promise<{ stateVersion: number; savedAt: string }>;
  undo?(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState>;
  redo?(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState>;
}

export class ApiWorkspaceRepository implements WorkspaceRepository {
  constructor(private readonly sessionId?: string) {}

  async getState(workspaceId: string): Promise<WorkspaceState> {
    return await workspacesApi.getState(workspaceId, this.sessionId);
  }

  async saveState(workspaceId: string, snapshot: WorkspaceSnapshot): Promise<WorkspaceState> {
    const state: WorkspaceState = this.mapToState(workspaceId, snapshot);
    return await workspacesApi.saveState(workspaceId, state, undefined, this.sessionId);
  }

  async appendEvent(workspaceId: string, event: SandboxEventCommand): Promise<WorkspaceEventAck> {
    return workspacesApi.appendEvent(workspaceId, event, this.sessionId);
  }

  async autosave(workspaceId: string, data: AutosaveRequest): Promise<{ stateVersion: number; savedAt: string }> {
    return await workspacesApi.autosave(workspaceId, data, this.sessionId);
  }

  async undo(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState> {
    return workspacesApi.undo(workspaceId, expectedVersion, this.sessionId);
  }

  async redo(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState> {
    return workspacesApi.redo(workspaceId, expectedVersion, this.sessionId);
  }

  mapToState(workspaceId: string, snapshot: WorkspaceSnapshot, stateVersion = 1): WorkspaceState {
    return {
      workspaceId,
      stateVersion,
      viewport: snapshot.scene.camera,
      grid: { enabled: true },
      items: snapshot.scene.objects as unknown as Record<string, unknown>[],
      connections: snapshot.scene.connections as unknown as Record<string, unknown>[],
      log: [],
      updatedAt: snapshot.updatedAt,
    };
  }
}
