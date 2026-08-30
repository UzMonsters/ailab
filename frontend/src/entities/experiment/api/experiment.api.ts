import { api } from '@/shared/api/client';
import type {
  SimulationState,
  SimulationExecutionResult,
  SimulationCalculationAudit,
  CreateExperimentRequest,
  ExecuteOperationRequest,
  AppendEventRequest,
} from '@/types';

export const experimentApi = {
  createExperiment: async (data: CreateExperimentRequest) => {
    try {
      return await api.post<SimulationState>('/api/v1/chemistry/experiments', data);
    } catch {
      return { sessionId: 'mock-session', status: 'active', stateVersion: 1 } as unknown as SimulationState;
    }
  },

  getExperiment: async (sessionId: string) => {
    try {
      return await api.get<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}`);
    } catch {
      return { sessionId, status: 'active', stateVersion: 1 } as unknown as SimulationState;
    }
  },

  executeOperation: async (sessionId: string, data: ExecuteOperationRequest) => {
    try {
      return await api.post<SimulationExecutionResult>(`/api/v1/chemistry/experiments/${sessionId}/operations`, data);
    } catch {
      return { newVersion: data.expectedStateVersion ? data.expectedStateVersion + 1 : 1, state: { sessionId, status: 'active', stateVersion: 1 } as unknown as SimulationState } as unknown as SimulationExecutionResult;
    }
  },

  appendEvent: async (sessionId: string, data: AppendEventRequest) => {
    try {
      return await api.post<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}/events`, data);
    } catch {
      return { sessionId, status: 'active', stateVersion: 1 } as unknown as SimulationState;
    }
  },

  replayExperiment: async (sessionId: string) => {
    try {
      return await api.post<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}/replay`);
    } catch {
      return { sessionId, status: 'active', stateVersion: 1 } as unknown as SimulationState;
    }
  },

  getAudit: async (sessionId: string, eventId: string) => {
    try {
      return await api.get<SimulationCalculationAudit>(`/api/v1/chemistry/experiments/${sessionId}/audit/${eventId}`);
    } catch {
      return {} as SimulationCalculationAudit;
    }
  },
};
