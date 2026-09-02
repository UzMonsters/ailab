import type { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';

/** Slow, visible loss of liquid from cracked glass; never runs while paused. */
export class LeakSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace) {}

  update(deltaSeconds: number): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (const object of this.workspace.scene.objects.values()) {
      const integrity = String(object.properties.integrity ?? 'intact');
      if (integrity !== 'cracked' && integrity !== 'leaking' && integrity !== 'shattered') continue;
      const liquid = object.contents.find((content) => content.phase === 'liquid' || content.phase === 'aqueous');
      if (!liquid || Number(liquid.amount ?? 0) <= 0) continue;
      const rate = integrity === 'shattered' ? 8 : integrity === 'leaking' ? 2 : 0.25;
      const escaped = Math.min(Number(liquid.amount ?? 0), rate * deltaSeconds);
      liquid.amount = Number(liquid.amount ?? 0) - escaped;
      object.properties.escapedMassG = Number(object.properties.escapedMassG ?? 0) + escaped;
      object.properties.volumeMl = Math.max(0, Number(object.properties.volumeMl ?? 0) - escaped);
      const capacity = Number(object.properties.capacityMl ?? object.metadata.capacity ?? 100);
      object.properties.liquidLevel = capacity > 0 ? Number(object.properties.volumeMl) / capacity : 0;
      if (integrity === 'cracked') object.properties.integrity = 'leaking';
      if (!object.history.some((entry) => entry === 'Liquid is leaking from damaged glass')) object.history.push('Liquid is leaking from damaged glass');
      changed = true;
    }
    return changed;
  }
}
