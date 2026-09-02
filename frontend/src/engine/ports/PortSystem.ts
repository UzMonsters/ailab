import type { LaboratoryObjectSnapshot, PortDefinition, PortType, Vector2 } from '../core/types';

export type PortCompatibility = {
  status: 'compatible' | 'adapterRequired' | 'incompatible';
  reasonKey: string;
};

export const portPosition = (object: LaboratoryObjectSnapshot, port: PortDefinition): Vector2 => {
  const sx = object.scale?.x ?? 1;
  const sy = object.scale?.y ?? 1;
  const width = object.boundingBox.width * sx;
  const height = object.boundingBox.height * sy;
  const local = { x: width * port.position.x, y: height * port.position.y };
  const radians = (object.rotation ?? 0) * Math.PI / 180;
  return {
    x: object.position.x + local.x * Math.cos(radians) - local.y * Math.sin(radians),
    y: object.position.y + local.x * Math.sin(radians) + local.y * Math.cos(radians),
  };
};

const typePairIsValid = (source: PortDefinition, target: PortDefinition) =>
  source.type === target.type;

export const arePortsCompatible = (source: PortDefinition, target: PortDefinition): boolean =>
  validatePortCompatibility(source, target).status === 'compatible';

export function validatePortCompatibility(source: PortDefinition, target: PortDefinition): PortCompatibility {
  if (!source || !target) return { status: 'incompatible', reasonKey: 'ports.missing' };
  if (!typePairIsValid(source, target)) {
    return { status: 'incompatible', reasonKey: 'ports.typeMismatch' };
  }
  const sourceCooling = source.role === 'coolant-outlet';
  const targetCooling = target.role === 'coolant-inlet';
  if (sourceCooling !== targetCooling && (sourceCooling || targetCooling)) {
    return { status: 'incompatible', reasonKey: 'ports.coolantMismatch' };
  }
  if (source.role === 'condensate-outlet' && target.role === 'coolant-inlet') {
    return { status: 'incompatible', reasonKey: 'ports.condensateCoolingMismatch' };
  }
  const directionOk = (source.direction === 'out' || source.direction === 'bidirectional') &&
    (target.direction === 'in' || target.direction === 'bidirectional');
  if (!directionOk) return { status: 'incompatible', reasonKey: 'ports.directionMismatch' };
  if (source.isOpen === false || target.isOpen === false) {
    return { status: 'incompatible', reasonKey: 'ports.closed' };
  }
  if (source.requiredConnector && target.requiredConnector && source.requiredConnector !== target.requiredConnector) {
    return { status: 'adapterRequired', reasonKey: 'ports.connectorMismatch' };
  }
  return { status: 'compatible', reasonKey: 'ports.compatible' };
}

export const portTypeLabel = (type: PortType) => type;
