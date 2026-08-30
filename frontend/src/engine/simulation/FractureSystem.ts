import type { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';
import type { ParticleSystem } from './ParticleSystem';
import type { AudioSystem } from './AudioSystem';

/** Progresses visual fracture stages independently from the integrity decision. */
export class FractureSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace, private readonly particles?: ParticleSystem, private readonly audio?: AudioSystem) {}
  update(deltaSeconds: number): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (const object of this.workspace.scene.objects.values()) {
      const integrity = String(object.properties.integrity ?? 'intact');
      if (integrity === 'intact') continue;
      if (integrity === 'shattered' && !object.properties.damageCleanupComplete) {
        this.workspace.scene.shatter(object.id);
        changed = true;
      }
      const target = integrity === 'stressed' ? 1 : integrity === 'microcracked' ? 2 : integrity === 'cracked' || integrity === 'leaking' ? 3 : 4;
      const current = Number(object.properties.fractureProgress ?? 0);
      const duration = target === 4 ? .5 : .4;
      const next = Math.min(target, current + deltaSeconds / duration);
      if (next === current) continue;
      object.properties.fractureProgress = next;
      object.properties.fractureStage = next >= 4 ? 'fracture' : next >= 3 ? 'propagation' : next >= 2 ? 'microcrack' : 'stress';
      if (current < 4 && next >= 4) {
        this.audio?.playGlassBreak();
        for (let i = 0; i < 8; i += 1) this.particles?.emit({ x: object.position.x + object.boundingBox.width / 2, y: object.position.y + object.boundingBox.height / 2, vx: (Math.random() - .5) * 120, vy: (Math.random() - .5) * 100, type: 'shatter', color: 'rgba(226,232,240,.9)', life: .7, parentId: object.id, radius: 3, maxRadius: 7, state: 'shatter' });
      }
      changed = true;
    }
    return changed;
  }
}
