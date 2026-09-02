import { Workspace } from '../workspace/Workspace';
import type { EngineSystem } from './EngineSystem';
import type { ParticleSystem } from './ParticleSystem';
import type { AudioSystem } from './AudioSystem';
import { defaultReactionRegistry } from './ReactionRegistry';
import { readThermalState, writeThermalState } from './thermalState';
import { applyHeatStep, collectHeaterTargets } from './thermalHeat';
import { applyEvaporation, hasGasOutlet, syncVolume } from './thermalPhase';
import { glassTypes, updateIntegrity } from './thermalIntegrity';

type Content = Record<string, unknown>;
const amount = (content: Content) => Math.max(0, Number(content.amount ?? 0));

export class ThermodynamicThermalProvider {
  private readonly ambientTemperature = 24.5;

  step(workspace: Workspace, deltaSeconds: number, particles?: ParticleSystem, audio?: AudioSystem) {
    let changed = false;
    const heaters = collectHeaterTargets(workspace.scene.objects.values(), workspace.scene.connections.values(), deltaSeconds);
    for (const object of workspace.scene.objects.values()) {
      if (object.properties.broken || object.properties.integrity === 'shattered') continue;
      const { state, liquids, liquidMass, power } = applyHeatStep(object, heaters.get(object.id), deltaSeconds, this.ambientTemperature);
      const gases = object.contents.filter((content) => content.phase === 'gas');
      const heatCapacity = Math.max(20, Number(object.properties.massG ?? object.metadata.massG ?? 100) * .84) + liquids.reduce((sum, content) => sum + amount(content) * Number(content.heatCapacityJPerG ?? defaultReactionRegistry.getMaterialProperties(String(content.materialId ?? '')).specificHeatJPerGC ?? 4.18), 0);
      const closed = String(object.properties.systemType ?? (object.properties.sealed ? 'closed' : 'open')) === 'closed';
      if (applyEvaporation(object, liquids, gases, liquidMass, power, heatCapacity, deltaSeconds, closed, hasGasOutlet(workspace.scene.connections.values(), object.id))) {
        object.history.push(closed ? 'Vapor retained: closed system boundary' : 'Vapor escaped: open system boundary');
        changed = true;
      }
      const trackedMass = object.contents.reduce((sum, content) => sum + amount(content) * (content.unit === 'mL' ? Number(content.densityGPerMl ?? 1) : content.unit === 'mol' ? 0 : 1), 0);
      const escaped = Number(object.properties.escapedMassG ?? 0);
      const initial = Number(object.properties.initialMassG ?? trackedMass + escaped);
      object.properties.initialMassG = initial; object.properties.currentMassG = trackedMass; object.properties.massErrorG = initial - (trackedMass + escaped);
      const gradient = Math.max(Math.abs(state.bottomTemperature - state.contentsTemperature), Math.abs(state.wallTemperature - this.ambientTemperature) * .12);
      state.stress = Math.max(0, state.stress + (gradient > 45 ? (gradient - 45) * .012 : -1.5) * deltaSeconds);
      if (updateIntegrity(object, state.stress)) changed = true;
      writeThermalState(object, state); syncVolume(object);
    }
    return changed;
  }
}

export class ThermalSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace, private readonly particles?: ParticleSystem, private readonly provider = new ThermodynamicThermalProvider(), private readonly audio?: AudioSystem) {}
  update(deltaSeconds: number) { return this.workspace.simulation.running ? this.provider.step(this.workspace, deltaSeconds, this.particles, this.audio) : false; }
}

export { glassTypes };
