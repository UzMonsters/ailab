import type { WorkspaceSnapshot } from '../workspace/Workspace';
import { experimentApi } from '@/entities/experiment/api/experiment.api';
import type { SimulationState, SimulationExecutionResult, ExecuteOperationRequest } from '@/types';

export interface SimulationProvider { 
  run(workspace: WorkspaceSnapshot, deltaSeconds: number): Promise<WorkspaceSnapshot>; 
  getExperiment?(sessionId: string): Promise<SimulationState>;
  executeOperation?(sessionId: string, data: ExecuteOperationRequest): Promise<SimulationExecutionResult>;
}

export class MockSimulationProvider implements SimulationProvider { 
  async run(workspace: WorkspaceSnapshot, deltaSeconds: number) { 
    return { ...workspace, simulation: { ...workspace.simulation, time: workspace.simulation.time + deltaSeconds }, updatedAt: new Date().toISOString() }; 
  } 
}

export class ApiSimulationProvider implements SimulationProvider {
  async run(workspace: WorkspaceSnapshot, deltaSeconds: number) {
    // Usually handled server side, returning current state to fulfill interface
    return workspace;
  }

  async getExperiment(sessionId: string): Promise<SimulationState> {
    return await experimentApi.getExperiment(sessionId);
  }

  async executeOperation(sessionId: string, data: ExecuteOperationRequest): Promise<SimulationExecutionResult> {
    return await experimentApi.executeOperation(sessionId, data);
  }
}
