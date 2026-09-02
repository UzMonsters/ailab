import { validatePortCompatibility, portPosition, type PortCompatibility } from '../ports/PortSystem';
import type { ConnectionSnapshot } from '../scene/Scene';
import type { LaboratoryObject } from '../objects/LaboratoryObject';
export class ConnectionEngine {
  validate(from: LaboratoryObject, fromPortId: string, to: LaboratoryObject, toPortId: string, connections: ConnectionSnapshot[] = []): PortCompatibility {
    const source = from.ports.find((port) => port.id === fromPortId);
    const target = to.ports.find((port) => port.id === toPortId);
    if (!source || !target) return { status: 'incompatible', reasonKey: 'ports.missing' };
    if (from.id === to.id) return { status: 'incompatible', reasonKey: 'ports.selfConnection' };
    const compatibility = validatePortCompatibility(source, target);
    if (compatibility.status !== 'compatible') return compatibility;
    if (source.type === 'Thermal' && !from.capabilities.thermalOutput && source.role !== 'thermal-output') return { status: 'incompatible', reasonKey: 'ports.thermalSourceRequired' };
    if (target.type === 'Thermal' && !to.capabilities.heatTarget && !to.capabilities.temperatureSensor) return { status: 'incompatible', reasonKey: 'ports.thermalTargetRequired' };
    if ((source.type === 'Liquid' || source.type === 'Gas') && source.role !== 'coolant-outlet' && !from.capabilities.pourable && !from.capabilities.container) return { status: 'incompatible', reasonKey: 'ports.transferSourceRequired' };
    if ((target.type === 'Liquid' || target.type === 'Gas') && !to.capabilities.container && !to.capabilities.liquidConduit && target.role !== 'coolant-inlet') return { status: 'incompatible', reasonKey: 'ports.containmentRequired' };
    const sameEndpoints = connections.some((connection) =>
      (connection.from.objectId === from.id && connection.from.portId === fromPortId && connection.to.objectId === to.id && connection.to.portId === toPortId) ||
      (connection.from.objectId === to.id && connection.from.portId === toPortId && connection.to.objectId === from.id && connection.to.portId === fromPortId));
    if (sameEndpoints) return { status: 'incompatible', reasonKey: 'ports.duplicate' };
    
    const duplicateType = connections.some((connection) => {
      const sameObjects = (connection.from.objectId === from.id && connection.to.objectId === to.id) ||
                          (connection.from.objectId === to.id && connection.to.objectId === from.id);
      return sameObjects && connection.type === source.type;
    });
    if (duplicateType) return { status: 'incompatible', reasonKey: 'ports.duplicate' };

    const occupied = (objectId: string, portId: string) => connections.filter((connection) =>
      (connection.from.objectId === objectId && connection.from.portId === portId) || (connection.to.objectId === objectId && connection.to.portId === portId)).length;
    if (source.capacity !== undefined && occupied(from.id, fromPortId) >= source.capacity) return { status: 'incompatible', reasonKey: 'ports.capacity' };
    if (target.capacity !== undefined && occupied(to.id, toPortId) >= target.capacity) return { status: 'incompatible', reasonKey: 'ports.capacity' };
    return compatibility;
  }
  canConnect(from: LaboratoryObject, fromPortId: string, to: LaboratoryObject, toPortId: string, connections: ConnectionSnapshot[] = []) {
    return this.validate(from, fromPortId, to, toPortId, connections).status === 'compatible';
  }
  create(from: LaboratoryObject, fromPortId: string, to: LaboratoryObject, toPortId: string, connections: ConnectionSnapshot[] = []): ConnectionSnapshot {
    if (!this.canConnect(from, fromPortId, to, toPortId, connections)) throw new Error('Ports are not compatible');
    const type = from.ports.find((port) => port.id === fromPortId)?.type ?? 'Glass';
    const medium = type === 'Liquid' ? 'liquid' : type === 'Gas' ? 'gas' : type === 'Thermal' ? 'thermal' : type === 'Electric' ? 'electrical' : type === 'Sensor' ? 'sensor' : 'mechanical';
    const connector = (medium === 'electrical' || medium === 'sensor') ? 'wire' : medium === 'thermal' ? 'direct' : 'glass-tube';
    const start = portPosition(from, from.ports.find((port) => port.id === fromPortId)!);
    const end = portPosition(to, to.ports.find((port) => port.id === toPortId)!);
    const middleX = (start.x + end.x) / 2;
    return { id: crypto.randomUUID(), from: { objectId: from.id, portId: fromPortId }, to: { objectId: to.id, portId: toPortId }, type, medium, connector, style: 'orthogonal-tube', routePoints: [{ x: middleX, y: start.y }, { x: middleX, y: end.y }] };
  }

  createWithAdapter(from: LaboratoryObject, fromPortId: string, to: LaboratoryObject, toPortId: string): ConnectionSnapshot {
    void from; void fromPortId; void to; void toPortId;
    throw new Error('An explicit adapter equipment is required for this connection');
  }
}
