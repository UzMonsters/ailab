import type { LaboratoryObject } from '../objects/LaboratoryObject';
export type ThermalState = { ambientTemperature: number; surfaceTemperature: number; wallTemperature: number; bottomTemperature: number; contentsTemperature: number; gasTemperature: number; stress: number };
export const readThermalState = (object: LaboratoryObject): ThermalState => {
  const p = object.properties, state = (p.thermalState ?? {}) as Record<string, unknown>, temperature = Number(p.temperature ?? 24.5);
  return { ambientTemperature: Number(state.ambientTemperature ?? 24.5), surfaceTemperature: Number(state.surfaceTemperature ?? (object.capabilities.heater ? temperature : 24.5)), wallTemperature: Number(state.wallTemperature ?? temperature), bottomTemperature: Number(state.bottomTemperature ?? temperature), contentsTemperature: Number(state.contentsTemperature ?? temperature), gasTemperature: Number(state.gasTemperature ?? temperature), stress: Number(state.stress ?? 0) };
};
export const writeThermalState = (object: LaboratoryObject, state: ThermalState) => {
  object.properties.thermalState = state; object.properties.surfaceTemperature = state.surfaceTemperature; object.properties.wallTemperature = state.wallTemperature; object.properties.bottomTemperature = state.bottomTemperature; object.properties.contentsTemperature = state.contentsTemperature; object.properties.gasTemperature = state.gasTemperature; object.properties.temperature = object.capabilities.heater ? state.surfaceTemperature : state.contentsTemperature;
};
