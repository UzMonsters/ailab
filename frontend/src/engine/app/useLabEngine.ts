'use client';

import { useEffect, useState } from 'react';
import { Engine } from '../core/Engine';
import { createDefaultEquipmentRegistry, EquipmentRegistry } from '../registry/EquipmentRegistry';
import { Workspace } from '../workspace/Workspace';
import { LocalSimulationProvider } from '../simulation/LocalSimulationProvider';
import { LocalWorkspaceRepository } from '../workspace/LocalWorkspaceRepository';
import { ApiWorkspaceRepository } from '../workspace/WorkspaceRepository';
import { ApiSimulationProvider } from '../simulation/SimulationProvider';

/**
 * Creates and manages the LabEngine lifecycle.
 * Saved workspaces use backend repositories and simulation sessions.
 * The local provider remains available only for an intentionally unsaved sandbox.
 */
export function useLabEngine(workspaceId?: string, sessionId?: string) {
  const [registry] = useState(() => createDefaultEquipmentRegistry());
  const [engine] = useState(() => {
    const backendMode = Boolean(workspaceId);
    const simulation = backendMode ? new ApiSimulationProvider() : new LocalSimulationProvider();
    const repository = backendMode ? new ApiWorkspaceRepository() : new LocalWorkspaceRepository();
    return new Engine(new Workspace(), simulation, repository);
  });

  useEffect(() => {
    engine.start();
    return () => engine.stop();
  }, [engine]);

  return { engine, registry };
}
