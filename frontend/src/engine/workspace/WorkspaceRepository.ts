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
  async getState(workspaceId: string): Promise<WorkspaceState> {
    return await workspacesApi.getState(workspaceId);
  }

  async saveState(workspaceId: string, snapshot: WorkspaceSnapshot): Promise<WorkspaceState> {
    const state: WorkspaceState = this.mapToState(workspaceId, snapshot);
    return await workspacesApi.saveState(workspaceId, state);
  }

  async appendEvent(workspaceId: string, event: SandboxEventCommand): Promise<WorkspaceEventAck> {
    return workspacesApi.appendEvent(workspaceId, event);
  }

  async autosave(workspaceId: string, data: AutosaveRequest): Promise<{ stateVersion: number; savedAt: string }> {
    return await workspacesApi.autosave(workspaceId, data);
  }

  async undo(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState> {
    return workspacesApi.undo(workspaceId, expectedVersion);
  }

  async redo(workspaceId: string, expectedVersion?: number): Promise<WorkspaceState> {
    return workspacesApi.redo(workspaceId, expectedVersion);
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
