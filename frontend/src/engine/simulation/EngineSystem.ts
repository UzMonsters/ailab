import { Workspace } from '../workspace/Workspace';

export interface EngineSystem {
  /** Returns true when the system changed observable workspace state. */
  update(deltaSeconds: number): boolean | void;
}
