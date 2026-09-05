import type { LaboratoryObject } from '../objects/LaboratoryObject';
import { readThermalState, writeThermalState } from './thermalState';
export type HeaterTarget = { surface: number; power: number; contact: number };
export const collectHeaterTargets = (objects: Iterable<LaboratoryObject>, connections: Iterable<any>, dt: number) => {
  const all = [...objects], targets = new Map<string, HeaterTarget>();
  for (const heater of all) {
    if (!heater.capabilities.thermalOutput || !(heater.state === 'heating' || heater.properties.operation === 'heating')) continue;
    const state = readThermalState(heater), setpoint = Math.min(Number(heater.capabilities.heater?.maxTemperature ?? 400), Number(heater.properties.targetTemperature ?? heater.capabilities.heater?.maxTemperature ?? 80));
    state.surfaceTemperature += (setpoint - state.surfaceTemperature) * Math.min(1, dt / 8);
    writeThermalState(heater, state);
    for (const vessel of all) {
      const attached = vessel.properties.attachedTo === heater.id || heater.properties.attachedTo === vessel.id;
      const connected = [...connections].some((connection) => connection.medium === 'thermal' && ((connection.from.objectId === heater.id && connection.to.objectId === vessel.id) || (connection.to.objectId === heater.id && connection.from.objectId === vessel.id)));
      if (attached || connected) targets.set(vessel.id, { surface: state.surfaceTemperature, power: Number(heater.capabilities.thermalOutput.powerW ?? 800), contact: attached ? 1 : .55 });
    }
  }
  return targets;
};
export const applyHeatStep = (object: LaboratoryObject, target: HeaterTarget | undefined, dt: number, ambient: number) => {
  const state = readThermalState(object), wallCapacity = Math.max(20, Number(object.properties.massG ?? object.metadata.massG ?? 100) * .84), liquids = object.contents.filter((content) => content.phase === 'liquid' || content.phase === 'aqueous'), liquidMass = liquids.reduce((sum, content) => sum + Math.max(0, Number(content.amount ?? 0)), 0), power = target ? target.power * target.contact : 0;
  state.bottomTemperature += ((target ? target.surface : ambient) - state.bottomTemperature) * (target ? .08 : .01) * dt;
  // Energy is already integrated over `dt` in the numerator. Multiplying by
  // dt a second time made heating almost motionless at 60 FPS and prevented
  // liquids from reaching their boiling point in a usable simulation time.
  state.bottomTemperature += (power * dt - (state.wallTemperature - ambient) * .8 * dt) / wallCapacity * .05;
  state.wallTemperature += (state.bottomTemperature - state.wallTemperature) * Math.min(1, dt * .18);
  state.contentsTemperature += ((state.wallTemperature - state.contentsTemperature) * (liquidMass ? .035 : .015) - (state.contentsTemperature - ambient) * .004) * dt;
  state.gasTemperature += (state.wallTemperature - state.gasTemperature) * .04 * dt;
  writeThermalState(object, state);
  return { state, liquids, liquidMass, power };
};
