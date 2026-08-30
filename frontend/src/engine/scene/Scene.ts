import { LaboratoryObject } from '../objects/LaboratoryObject';
import type { Vector2 } from '../core/types';
import { validatePortCompatibility } from '../ports/PortSystem';

export type SceneSnapshot = { objects: ReturnType<LaboratoryObject['serialize']>[]; connections: ConnectionSnapshot[]; camera: { position: Vector2; zoom: number }; selection: string[] };
export type ConnectionSnapshot = { id: string; from: { objectId: string; portId: string }; to: { objectId: string; portId: string }; type: string; medium?: 'liquid' | 'gas' | 'thermal' | 'electrical' | 'sensor' | 'mechanical'; connector?: 'glass-tube' | 'rubber-hose' | 'wire' | 'direct' | 'adapter'; style: string; routePoints?: Vector2[]; validation?: { reasonKey: string } };
export type SpillSnapshot = { id: string; materialId: string; amount: number; time: number; x?: number; y?: number; color?: string; sourceId?: string };

export class Scene {
  readonly objects = new Map<string, LaboratoryObject>();
  environment: { spills: SpillSnapshot[] } = { spills: [] };
  readonly connections = new Map<string, ConnectionSnapshot>();
  camera = { position: { x: 0, y: 0 }, zoom: 1 };
  selection = new Set<string>();
  onUpdate?: () => void;
  add(object: LaboratoryObject) { this.objects.set(object.id, object); this.onUpdate?.(); return object; }
  remove(id: string) { this.objects.delete(id); this.selection.delete(id); for (const [key, connection] of this.connections) if (connection.from.objectId === id || connection.to.objectId === id) this.connections.delete(key); this.onUpdate?.(); }
  shatter(id: string) {
    const object = this.objects.get(id);
    if (!object || object.properties.damageCleanupComplete) return { disconnected: 0, spilled: 0 };
    const disconnected = [...this.connections.values()].filter((connection) => connection.from.objectId === id || connection.to.objectId === id);
    disconnected.forEach((connection) => this.connections.delete(connection.id));
    const spilled = object.contents.reduce((total, content) => total + Math.max(0, Number(content.amount ?? 0)), 0);
    for (const content of object.contents) {
      const amount = Math.max(0, Number(content.amount ?? 0));
      if (amount > 0) this.environment.spills.push({
        id: crypto.randomUUID(), materialId: String(content.materialId ?? 'unknown'), amount, time: Date.now(),
        x: object.position.x + object.boundingBox.width / 2, y: object.position.y + object.boundingBox.height,
        color: String(content['color'] ?? (content.metadata as Record<string, unknown> | undefined)?.color ?? ''), sourceId: object.id,
      });
      content.amount = 0;
    }
    object.contents = [];
    object.material = undefined;
    object.properties.volumeMl = 0;
    object.properties.liquidLevel = 0;
    object.properties.integrity = 'shattered';
    object.properties.broken = true;
    object.properties.damageCleanupComplete = true;
    object.properties.contentsStatus = 'spilled';
    object.state = 'idle';
    object.history.push(disconnected.length ? `${disconnected.length} connections disconnected after vessel damage` : 'Vessel damaged; no active connections');
    if (spilled > 0) object.history.push('Contents released into virtual workspace');
    this.onUpdate?.();
    return { disconnected: disconnected.length, spilled };
  }
  select(ids: string[]) { this.selection = new Set(ids); for (const object of this.objects.values()) object.selected = this.selection.has(object.id); this.onUpdate?.(); }
  validateConnection(connection: ConnectionSnapshot) {
    const from = this.objects.get(connection.from.objectId);
    const to = this.objects.get(connection.to.objectId);
    if (!from || !to) throw new Error('Connection endpoints must exist in the scene');
    if (from.id === to.id) throw new Error('An object cannot connect to itself');
    const fromPort = from.ports.find((port) => port.id === connection.from.portId);
    const toPort = to.ports.find((port) => port.id === connection.to.portId);
    if (!fromPort || !toPort) throw new Error('Connection ports must exist in the scene');
    const result = validatePortCompatibility(fromPort, toPort);
    if (result.status !== 'compatible') throw new Error(result.reasonKey);
    const duplicate = [...this.connections.values()].some((existing) =>
      (existing.from.objectId === connection.from.objectId && existing.from.portId === connection.from.portId && existing.to.objectId === connection.to.objectId && existing.to.portId === connection.to.portId) ||
      (existing.from.objectId === connection.to.objectId && existing.from.portId === connection.to.portId && existing.to.objectId === connection.from.objectId && existing.to.portId === connection.from.portId));
    if (duplicate) throw new Error('ports.duplicate');
    
    const duplicateType = [...this.connections.values()].some((existing) => {
      const sameObjects = (existing.from.objectId === connection.from.objectId && existing.to.objectId === connection.to.objectId) ||
                          (existing.from.objectId === connection.to.objectId && existing.to.objectId === connection.from.objectId);
      return sameObjects && existing.type === connection.type;
    });
    if (duplicateType) throw new Error('ports.duplicate');

    const occupied = (objectId: string, portId: string) => [...this.connections.values()].filter((existing) =>
      (existing.from.objectId === objectId && existing.from.portId === portId) || (existing.to.objectId === objectId && existing.to.portId === portId)).length;
    if (fromPort.capacity !== undefined && occupied(from.id, fromPort.id) >= fromPort.capacity) throw new Error('ports.capacity');
    if (toPort.capacity !== undefined && occupied(to.id, toPort.id) >= toPort.capacity) throw new Error('ports.capacity');
  }
  connect(connection: ConnectionSnapshot) { this.validateConnection(connection); this.connections.set(connection.id, connection); this.onUpdate?.(); return connection; }
  serialize(): SceneSnapshot { return { objects: [...this.objects.values()].map((object) => object.serialize()), connections: [...this.connections.values()], camera: this.camera, selection: [...this.selection] }; }
  static deserialize(snapshot: SceneSnapshot) { const scene = new Scene(); snapshot.objects.forEach((object) => scene.add(LaboratoryObject.deserialize(object))); 
    snapshot.connections.forEach((connection) => {
      // Filter out duplicate connections of the same type between the same objects
      const isDuplicate = [...scene.connections.values()].some(existing => {
        const sameObjects = (existing.from.objectId === connection.from.objectId && existing.to.objectId === connection.to.objectId) ||
                            (existing.from.objectId === connection.to.objectId && existing.to.objectId === connection.from.objectId);
        return sameObjects && existing.type === connection.type;
      });
      if (!isDuplicate) {
        scene.connections.set(connection.id, connection);
      }
    });
 scene.camera = snapshot.camera; scene.select(snapshot.selection); return scene; }
}

  
