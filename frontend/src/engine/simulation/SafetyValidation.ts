import type { LaboratoryObject } from '../objects/LaboratoryObject';

export type SafetyIssueKey =
  | 'validation.safetyContainment'
  | 'validation.gasContainment'
  | 'validation.condenserContainment'
  | 'validation.pressureContainment'
  | 'validation.materialIncompatible';

/** Frontend-only containment rules used before local simulation starts. */
export function validateEquipmentContainment(object: LaboratoryObject): SafetyIssueKey[] {
  const issues = new Set<SafetyIssueKey>();
  const contents = object.contents ?? [];
  const hasGas = contents.some((content) => content.phase === 'gas');
  const hazardous = contents.some((content) =>
    (Array.isArray(content.safety) ? content.safety.length : 0) > 0 || (content.metadata as Record<string, unknown> | undefined)?.hazardous === true,
  );
  const closed = object.properties.isClosed === true;
  const hasContainer = Boolean(object.capabilities.container);
  const hasGasPort = object.ports.some((port) => port.type === 'Gas');

  if (hazardous && (!hasContainer || !closed)) issues.add('validation.safetyContainment');
  if (hasGas && (!hasContainer || !closed || !hasGasPort)) issues.add('validation.gasContainment');

  if (object.type === 'condenser' && hasGas) {
    const gasPorts = object.ports.filter((port) => port.type === 'Gas');
    if (gasPorts.length < 2) issues.add('validation.condenserContainment');
  }

  const pressure = Number(object.properties.pressureBar ?? 1);
  const maxPressure = Number(object.properties.maxPressureBar ?? 2.5);
  if (pressure > maxPressure * 0.9 && (!hasContainer || !closed)) issues.add('validation.pressureContainment');
  
  // Container Compatibility Validation
  const containerMaterial = String(object.properties.containerMaterial ?? 'Borosilicate Glass');
  for (const content of contents) {
    const incompatible = (content.metadata as any)?.incompatibleMaterials as string[] | undefined;
    if (incompatible && incompatible.includes(containerMaterial)) {
      issues.add('validation.materialIncompatible' as SafetyIssueKey);
      break;
    }
  }

  return [...issues];
}
