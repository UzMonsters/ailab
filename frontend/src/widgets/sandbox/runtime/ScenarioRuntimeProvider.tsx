'use client';
import { createContext, useContext, type ReactNode } from 'react';
import type { RuntimeScenario } from './runtime.types';

const ScenarioRuntimeContext = createContext<RuntimeScenario | null>(null);

export function ScenarioRuntimeProvider({ scenario, children }: { scenario: RuntimeScenario | null; children: ReactNode }) {
  return <ScenarioRuntimeContext.Provider value={scenario}>{children}</ScenarioRuntimeContext.Provider>;
}

export const useScenarioRuntimeContext = () => useContext(ScenarioRuntimeContext);
