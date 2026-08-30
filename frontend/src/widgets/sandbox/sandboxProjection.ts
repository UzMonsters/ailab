import type { Engine } from '@/engine/core/Engine';
import type { EquipmentRegistry } from '@/engine/registry/EquipmentRegistry';
import type { Connection, ContentComponent, EquipmentType, Item, Material } from './types';
import { isLiquidConduit } from './types';

import { featuredMaterials } from '@/features/sandbox/add-item/ui/Library';

export const vesselCapacities: Record<string, number> = {
  beaker: 250, beaker50: 50, beaker100: 100, beaker250: 250, beaker500: 500, testtube: 50,
  graduated: 100, graduated_cylinder: 100, erlenmeyer: 250, roundflask: 500, volumetric: 100, pipette: 10, burette: 50,
  separatory_funnel: 500, volumetric_flask: 250, distillation_flask: 250
};

export const capacityFor = (item: Pick<Item, 'type' | 'capacityMl'>) => item.capacityMl ?? vesselCapacities[item.type] ?? 100;

export function projectSandboxItems(engine: Engine, registry: EquipmentRegistry): Item[] {
  return Array.from(engine.workspace.scene.objects.values()).map((object) => {
    const liquidConduit = isLiquidConduit({ type: object.type as EquipmentType });
    const legacyMaterial = object.material as Material | undefined;
    const contents = liquidConduit ? [] : ((object.contents.length > 0 ? object.contents : legacyMaterial ? [{
      materialId: legacyMaterial.id, name: legacyMaterial.name, formula: legacyMaterial.formula,
      amount: Number(object.properties.volumeMl ?? 0), unit: legacyMaterial.state === 'solid' ? 'g' : 'mL',
      phase: legacyMaterial.state, color: legacyMaterial.color,
    }] : []) as ContentComponent[]).filter((content) =>
      (object.type === 'pipette' || object.type === 'burette')
        ? content.phase === 'liquid' || content.phase === 'aqueous'
        : true
    );

    // Inject missing metadata (like color) from Library if available
    const enrichedContents = contents.map(c => {
      if (c.color) return c;
      const libMat = featuredMaterials.find(m => m.id === c.materialId || m.formula === c.materialId);
      return { ...c, color: libMat?.color ?? c.color, name: libMat?.name ?? c.name, formula: libMat?.formula ?? c.formula };
    });

    const primary = enrichedContents.find(c => c.phase === 'aqueous') 
                 ?? enrichedContents.find(c => c.phase === 'liquid') 
                 ?? enrichedContents[0];
    const scaleBounds = registry.getScaleBounds(object.type);
    return {
      id: object.id,
      type: object.type as EquipmentType,
      name: (object.metadata.displayName as string) ?? (object.metadata.name as string) ?? object.type,
      x: object.position.x,
      y: object.position.y,
      w: object.boundingBox?.width ?? 100,
      h: object.boundingBox?.height ?? 100,
      scale: typeof object.scale === 'number' ? object.scale : object.scale.x,
      scaleX: typeof object.scale === 'number' ? object.scale : object.scale.x,
      scaleY: typeof object.scale === 'number' ? object.scale : object.scale.y,
      rotation: object.rotation,
      material: legacyMaterial ?? (primary ? { id: primary.materialId, name: primary.name ?? primary.materialId, formula: primary.formula ?? primary.materialId, color: primary.color, state: primary.phase } : undefined),
      contents: enrichedContents,
      volumeMl: liquidConduit ? 0 : Number(object.properties.volumeMl ?? primary?.amount ?? 0),
      capacityMl: liquidConduit ? undefined : Number(object.properties.capacityMl ?? object.metadata.capacity ?? vesselCapacities[object.type] ?? 100),
      liquidLevel: liquidConduit ? 0 : Number(object.properties.liquidLevel ?? 0),
      temperature: Number(object.properties.temperature ?? 24.5),
      pressureBar: Number(object.properties.pressureBar ?? 1),
      massG: object.type === 'pipette' || object.type === 'burette' ? 0 : Number(object.properties.massG ?? 0),
      moles: Number(object.properties.moles ?? contents.reduce((sum, content) => sum + Number(content.molarAmount ?? 0), 0)),
      targetTemperature: object.properties.targetTemperature as number | undefined,
      operation: object.state as Item['operation'],
      attachedTo: object.properties.attachedTo as string | undefined,
      broken: Boolean(object.properties.broken),
      systemType: (object.properties.systemType ?? (object.properties.sealed ? 'closed' : 'open')) as Item['systemType'],
      valveOpening: Number(object.properties.valveOpening ?? 0),
      integrity: object.properties.integrity as Item['integrity'],
      unsafeConfiguration: Boolean(object.properties.unsafeConfiguration),
      measuredTemperatureC: typeof object.properties.measuredTemperatureC === 'number' ? object.properties.measuredTemperatureC : undefined,
      measurementStatus: object.properties.measurementStatus as Item['measurementStatus'],
      measurementTarget: object.properties.measurementTarget as Item['measurementTarget'],
      measuredValue: typeof object.properties.measuredValue === 'number' ? object.properties.measuredValue : undefined,
      overflowing: Boolean(object.properties.overflowing),
      lastOverflowAt: typeof object.properties.lastOverflowAt === 'number' ? object.properties.lastOverflowAt : undefined,
      // Funnels are pass-through equipment, including for older snapshots
      // that still stored the historical container capability.
      capabilities: liquidConduit ? { pourable: true, liquidConduit: true } : object.capabilities,
      metadata: object.metadata,
      minScale: scaleBounds.min,
      maxScale: scaleBounds.max,
      portTypes: object.ports.map((port) => port.type),
      ports: object.ports.map((port) => ({ id: port.id, name: port.name, type: port.type, x: port.position.x, y: port.position.y, direction: port.direction })),
      history: object.history,
    };
  });
}

export function projectSandboxConnections(engine: Engine): Connection[] {
  return Array.from(engine.workspace.scene.connections.values()).filter((connection) => {
    const from = engine.workspace.scene.objects.get(connection.from.objectId);
    const to = engine.workspace.scene.objects.get(connection.to.objectId);
    const fromPort = from?.ports.find((port) => port.id === connection.from.portId);
    const toPort = to?.ports.find((port) => port.id === connection.to.portId);
    // Do not draw stale/corrupt links from older workspace snapshots. They
    // make a vessel appear to have power connections and hide the real ports.
    if (!from || !to || !fromPort || !toPort) return false;
    if (fromPort.type !== toPort.type) return false;
    if (!((fromPort.direction === 'out' || fromPort.direction === 'bidirectional') && (toPort.direction === 'in' || toPort.direction === 'bidirectional'))) return false;
    return true;
  }).map((connection) => {
    const from = engine.workspace.scene.objects.get(connection.from.objectId);
    const to = engine.workspace.scene.objects.get(connection.to.objectId);
    const fromPort = from?.ports.find((port) => port.id === connection.from.portId);
    const toPort = to?.ports.find((port) => port.id === connection.to.portId);
    return ({
    id: connection.id,
    from: connection.from.objectId,
    to: connection.to.objectId,
    fromPort: connection.from.portId,
    toPort: connection.to.portId,
    fromName: String(from?.metadata.displayName ?? from?.metadata.name ?? from?.type ?? connection.from.objectId),
    toName: String(to?.metadata.displayName ?? to?.metadata.name ?? to?.type ?? connection.to.objectId),
    fromPortName: fromPort?.name ?? connection.from.portId,
    toPortName: toPort?.name ?? connection.to.portId,
    routePoints: connection.routePoints ?? [],
    port: connection.type as Connection['port'],
    medium: connection.medium ?? (connection.type === 'Liquid' ? 'liquid' : connection.type === 'Gas' ? 'gas' : connection.type === 'Thermal' ? 'thermal' : connection.type === 'Electric' ? 'electrical' : 'mechanical'),
    connector: connection.connector ?? 'glass-tube',
    direction: 'source-to-target',
    });
  });
}
