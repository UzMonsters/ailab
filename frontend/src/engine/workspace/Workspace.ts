import { Scene, type SceneSnapshot } from '../scene/Scene';
export type WorkspaceSnapshot = { version: 1; scene: SceneSnapshot; simulation: { running: boolean; time: number; speed?: number }; updatedAt: string };
export type SimulationState = { running: boolean; time: number; speed?: number };
export class Workspace {
  readonly scene: Scene;
  simulation: SimulationState = { running: false, time: 0, speed: 1 };
  constructor(scene = new Scene()) { this.scene = scene; }
  serialize(): WorkspaceSnapshot { return { version: 1, scene: this.scene.serialize(), simulation: { ...this.simulation }, updatedAt: new Date().toISOString() }; }
  toJSON() { return JSON.stringify(this.serialize()); }
  static fromJSON(value: string) { const snapshot = JSON.parse(value) as WorkspaceSnapshot; const workspace = new Workspace(Scene.deserialize(snapshot.scene)); workspace.simulation = { running: snapshot.simulation.running, time: snapshot.simulation.time, speed: snapshot.simulation.speed ?? 1 }; return workspace; }
}
