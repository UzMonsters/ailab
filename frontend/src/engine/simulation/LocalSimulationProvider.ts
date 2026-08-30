import type { SimulationProvider } from './SimulationProvider';
import type { WorkspaceSnapshot } from '../workspace/Workspace';
import type { ExecuteOperationRequest, SimulationExecutionResult } from '@/types';

/**
 * Runs simulation entirely in the browser.
 * No backend calls. Suitable for offline use and development.
 */
export class LocalSimulationProvider implements SimulationProvider {
  async run(workspace: WorkspaceSnapshot, deltaSeconds: number): Promise<WorkspaceSnapshot> {
    const boundedDelta = Math.max(0, Math.min(deltaSeconds, 0.1));
    return {
      ...workspace,
      simulation: {
        ...workspace.simulation,
        time: workspace.simulation.time + boundedDelta,
      },
      updatedAt: new Date().toISOString(),
    };
  }

  async executeOperation(
    sessionId: string,
    _data: ExecuteOperationRequest
  ): Promise<SimulationExecutionResult> {
    return { sessionId, commandId: 'local-command', previousVersion: 0, newVersion: 0, stateDelta: {}, executionLog: ['Executed locally in the browser; no backend reaction was called.'], timestamp: new Date().toISOString() };
  }
}
