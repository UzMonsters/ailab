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
  createExperiment: (data: CreateExperimentRequest) => api.post<SimulationState>('/api/v1/chemistry/experiments', data),

  getExperiment: (sessionId: string) => api.get<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}`),

  executeOperation: (sessionId: string, data: ExecuteOperationRequest) => api.post<SimulationExecutionResult>(`/api/v1/chemistry/experiments/${sessionId}/operations`, data),

  appendEvent: (sessionId: string, data: AppendEventRequest) => api.post<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}/events`, data),

  replayExperiment: (sessionId: string) => api.post<SimulationState>(`/api/v1/chemistry/experiments/${sessionId}/replay`),

  getAudit: (sessionId: string, eventId: string) => api.get<SimulationCalculationAudit>(`/api/v1/chemistry/experiments/${sessionId}/audit/${eventId}`),
  measurements: (sessionId: string, kind?: string) => api.get<Array<Record<string, unknown>>>(`/api/v1/chemistry/experiments/${sessionId}/measurements${kind ? `?kind=${encodeURIComponent(kind)}` : ''}`),
};
