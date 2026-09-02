import type { LaboratoryObject } from '@/engine/objects/LaboratoryObject';
import type { Scene } from '@/engine/scene/Scene';
import type { EquipmentRegistry } from '@/engine/registry/EquipmentRegistry';
import type { EquipmentOperation, Item } from './types';

export function applyItemPatch(object: LaboratoryObject, patch: Partial<Item>) {
  if (patch.x !== undefined) object.position.x = patch.x;
  if (patch.y !== undefined) object.position.y = patch.y;
  if (patch.scale !== undefined) object.scale.x = object.scale.y = patch.scale;
  if (patch.scaleX !== undefined) object.scale.x = patch.scaleX;
  if (patch.scaleY !== undefined) object.scale.y = patch.scaleY;
  if (patch.rotation !== undefined) object.rotation = patch.rotation;
  const properties: Record<string, unknown> = {
    volumeMl: patch.volumeMl,
    liquidLevel: patch.liquidLevel,
    temperature: patch.temperature,
    pressureBar: patch.pressureBar,
    massG: patch.massG,
    moles: patch.moles,
    targetTemperature: patch.targetTemperature,
    attachedTo: patch.attachedTo,
    broken: patch.broken,
    sealed: patch.sealed,
    systemType: patch.systemType,
    valveOpening: patch.valveOpening,
    integrity: patch.integrity,
    unsafeConfiguration: patch.unsafeConfiguration,
  };
  for (const [key, value] of Object.entries(properties)) if (value !== undefined) object.properties[key] = value;
  if (patch.operation !== undefined) object.state = patch.operation;
  if (patch.material !== undefined) object.material = patch.material;
  if (patch.contents !== undefined) object.contents = patch.contents;
}

export function applyOperationToScene(objects: Iterable<LaboratoryObject>, itemId: string, operation: EquipmentOperation, targetTemperature: number) {
  const target = Array.from(objects).find((object) => object.id === itemId);
  if (target) {
    target.state = operation;
    if (operation !== 'idle' && operation !== 'stirring') target.properties.targetTemperature = targetTemperature;
  }
  for (const object of objects) {
    if ((object.type === 'burner' || object.type === 'hotplate') && object.properties.attachedTo === itemId) object.state = operation;
  }
}

export function moveObjectWithAttachedChildren(scene: Scene, itemId: string, x: number, y: number) {
  const object = scene.objects.get(itemId);
  if (!object) return;
  const deltaX = x - object.position.x;
  const deltaY = y - object.position.y;
  const group = new Set<string>([itemId]);
  let changed = true;
  while (changed) {
    changed = false;
    for (const connection of scene.connections.values()) {
      if (connection.medium !== 'mechanical') continue;
      if (group.has(connection.from.objectId) && !group.has(connection.to.objectId)) {
        group.add(connection.to.objectId);
        changed = true;
      }
      if (group.has(connection.to.objectId) && !group.has(connection.from.objectId)) {
        group.add(connection.from.objectId);
        changed = true;
      }
    }
    for (const child of scene.objects.values()) {
      if (group.has(child.properties.attachedTo as string) && !group.has(child.id)) {
        group.add(child.id);
        changed = true;
      }
    }
  }
  for (const groupId of group) {
    const member = scene.objects.get(groupId);
    if (member) {
      member.position.x += deltaX;
      member.position.y += deltaY;
    }
  }
  
  // Nuke routePoints for attached connections to force recalculation of the route
  for (const connection of scene.connections.values()) {
    if (connection.from.objectId === itemId || connection.to.objectId === itemId) {
      connection.routePoints = [];
    }
  }
  
  for (const child of scene.objects.values()) {
    if ((child.type === 'burner' || child.type === 'hotplate' || child.type === 'magnetic_stirrer') && child.properties.attachedTo === object.id) {
      child.position.x = object.position.x + (object.boundingBox.width * object.scale.x) / 2 - (child.boundingBox.width * child.scale.x) / 2;
      child.position.y = object.position.y + object.boundingBox.height * object.scale.y - child.boundingBox.height * child.scale.y * 0.1;
      
      // Also nuke connections for child objects since they moved
      for (const connection of scene.connections.values()) {
        if (connection.from.objectId === child.id || connection.to.objectId === child.id) {
          connection.routePoints = [];
        }
      }
    }
  }
}

export function applyAcidBaseTemplate(scene: Scene, registry: EquipmentRegistry) {
  const flaskId = 'template-flask';
  const burner = registry.create('burner', { id: 'template-burner', position: { x: 322, y: 314 } });
  burner.properties.attachedTo = flaskId;
  const flask = registry.create('erlenmeyer', { id: flaskId, position: { x: 300, y: 220 } });
  const thermometer = registry.create('thermometer', { id: 'template-thermometer', position: { x: 440, y: 215 } });
  scene.add(flask);
  scene.add(burner);
  scene.add(thermometer);
  return flaskId;
}
