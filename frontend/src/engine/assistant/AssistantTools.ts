import { Engine } from '../core/Engine';
import { EquipmentRegistry } from '../registry/EquipmentRegistry';

export class AssistantTools {
  constructor(private engine: Engine, private registry: EquipmentRegistry) {}

  getLabState() {
    const scene = this.engine.workspace.scene;
    const objects = Array.from(scene.objects.values()).map(obj => ({
      id: obj.id,
      type: obj.type,
      properties: obj.properties,
      contents: obj.contents,
      capabilities: obj.capabilities,
      state: obj.state,
    }));
    
    return {
      objects,
      simulationRunning: this.engine.workspace.simulation.running,
      simulationTime: this.engine.workspace.simulation.time,
    };
  }

  executeAction(action: string, payload: Record<string, unknown>) {
    const scene = this.engine.workspace.scene;
    const objectId = typeof payload.id === 'string' ? payload.id : null;
    
    switch (action) {
      case 'spawn':
        try {
          const position = payload.position && typeof payload.position === 'object' ? payload.position as { x: number; y: number } : { x: 400, y: 300 };
          const equipment = this.registry.create(String(payload.type), {
            position,
          });
          scene.add(equipment);
          return { success: true, id: equipment.id };
        } catch (error: unknown) {
          return { success: false, error: error instanceof Error ? error.message : 'Spawn failed' };
        }

      case 'remove':
        if (objectId) {
          scene.remove(objectId);
          return { success: true };
        }
        return { success: false, error: 'Object not found' };

      case 'update':
        const obj = objectId ? scene.objects.get(objectId) : undefined;
        if (obj) {
          obj.update(payload.patch as Parameters<typeof obj.update>[0]);
          scene.onUpdate?.();
          return { success: true };
        }
        return { success: false, error: 'Object not found' };

      case 'clear':
        const ids = Array.from(scene.objects.keys());
        ids.forEach(id => scene.remove(id));
        return { success: true };

      case 'add_material':
        const target = objectId ? scene.objects.get(objectId) : undefined;
        if (target && target.capabilities?.container) {
          const material = payload.material && typeof payload.material === 'object' ? payload.material as Record<string, unknown> : null;
          if (!material) return { success: false, error: 'Material payload is invalid' };
          target.contents.push(material);
          if (material.state === "solid") {
             target.properties.massG = (target.properties.massG as number || 0) + 1;
          } else {
             target.properties.volumeMl = (target.properties.volumeMl as number || 0) + 25;
          }
          scene.onUpdate?.();
          return { success: true };
        }
        return { success: false, error: 'Target not found or not a container' };

      default:
        return { success: false, error: 'Unknown action' };
    }
  }
}
