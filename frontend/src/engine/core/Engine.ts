import { MockSimulationProvider, type SimulationProvider } from '../simulation/SimulationProvider';
import { Workspace } from '../workspace/Workspace';
import type { WorkspaceRepository } from '../workspace/WorkspaceRepository';
import { FluidSystem } from '../simulation/FluidSystem';
import type { EngineSystem } from '../simulation/EngineSystem';
import { ThermalSystem } from '../simulation/ThermalSystem';
import { ReactionSystem } from '../simulation/ReactionSystem';
import { ParticleSystem } from '../simulation/ParticleSystem';
import { AudioSystem } from '../simulation/AudioSystem';
import { PressureSystem } from '../simulation/PressureSystem';
import { LeakSystem } from '../simulation/LeakSystem';
import { CondenserSystem } from '../simulation/CondenserSystem';
import { SensorSystem } from '../simulation/SensorSystem';
import { FractureSystem } from '../simulation/FractureSystem';
import { LaboratoryObject } from '../objects/LaboratoryObject';
import type { SceneSnapshot } from '../scene/Scene';

export class Engine {
  private frame: number | null = null;
  private lastTime = 0;
  private lastUiNotifyTime = 0;
  readonly workspace: Workspace;
  readonly repository?: WorkspaceRepository;
  readonly simulation: SimulationProvider;
  
  readonly fluid: FluidSystem;
  readonly particles: ParticleSystem;
  readonly audio: AudioSystem;
  private systems: EngineSystem[] = [];

  private listeners = new Set<() => void>();
  
  constructor(
    workspace = new Workspace(), 
    simulation: SimulationProvider = new MockSimulationProvider(),
    repository?: WorkspaceRepository
  ) { 
    this.workspace = workspace; 
    this.simulation = simulation;
    this.repository = repository;
    
    this.audio = new AudioSystem();
    this.fluid = new FluidSystem(this.workspace, this.audio);
    this.particles = new ParticleSystem(this.workspace);
    
    this.systems.push(this.audio);
    this.systems.push(this.fluid);
    this.systems.push(new ThermalSystem(this.workspace, this.particles, undefined, this.audio));
    this.systems.push(new PressureSystem(this.workspace));
    this.systems.push(new LeakSystem(this.workspace));
    this.systems.push(new CondenserSystem(this.workspace));
    this.systems.push(new SensorSystem(this.workspace));
    this.systems.push(new FractureSystem(this.workspace, this.particles, this.audio));
    this.systems.push(new ReactionSystem(this.workspace));
    this.systems.push(this.particles);
    
    this.workspace.scene.onUpdate = () => this.notifyUpdate();
  }

  subscribe(listener: () => void) {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  notifyUpdate() {
    this.listeners.forEach(l => l());
  }

  setSimulationRunning(running: boolean) {
    this.workspace.simulation.running = running;
    this.notifyUpdate();
  }

  setSimulationSpeed(speed: number) {
    this.workspace.simulation.speed = speed;
    this.notifyUpdate();
  }

  resetSimulation(snapshot?: SceneSnapshot) {
    this.stop();
    if (snapshot) {
      this.workspace.scene.objects.clear();
      this.workspace.scene.connections.clear();
      for (const object of snapshot.objects) this.workspace.scene.objects.set(object.id, LaboratoryObject.deserialize(object));
      for (const connection of snapshot.connections) this.workspace.scene.connections.set(connection.id, structuredClone(connection));
      this.workspace.scene.select(snapshot.selection);
    }
    this.workspace.simulation = { running: false, time: 0 };
    for (const object of this.workspace.scene.objects.values()) {
      if (object.state === 'heating' || object.state === 'cooling' || object.state === 'stirring' || object.state === 'mixing') object.state = 'idle';
      delete object.properties.heatEnergyJ;
    }
    this.notifyUpdate();
  }

  start() { if (this.frame !== null || typeof window === 'undefined') return; this.lastTime = performance.now(); const tick = (time: number) => { const delta = Math.min(.1, (time - this.lastTime) / 1000); this.lastTime = time; void this.update(delta); this.render(); this.frame = window.requestAnimationFrame(tick); }; this.frame = window.requestAnimationFrame(tick); }
  stop() { if (this.frame !== null) window.cancelAnimationFrame(this.frame); this.frame = null; }
  
  async update(deltaSeconds: number) { 
    // The frame loop is local-only. Scientific backend operations are submitted
    // explicitly by user actions and reconciled once, never once per frame.
    if (this.workspace.simulation.running) {
      this.workspace.simulation.time += deltaSeconds;
    }
    const simulationDelta = deltaSeconds * Math.max(0.05, Number(this.workspace.simulation.speed ?? 1));
    const changed = this.systems.reduce((acc, system) => system.update(simulationDelta) === true || acc, false);
    if (changed || this.workspace.simulation.running) {
      const now = typeof performance !== 'undefined' ? performance.now() : Date.now();
      if (!this.workspace.simulation.running || now - this.lastUiNotifyTime >= 80) {
        this.lastUiNotifyTime = now;
        this.notifyUpdate();
      }
    }
  }
  
  render() { /* Rendering is intentionally supplied by Canvas adapters, never by the engine. */ }
  tick(deltaSeconds: number) { return this.update(deltaSeconds); }
}
