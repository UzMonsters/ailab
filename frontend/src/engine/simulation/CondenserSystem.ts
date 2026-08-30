import type { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';

const gas = (content: Record<string, unknown>) => content.phase === 'gas';
const amount = (content: Record<string, unknown>) => Math.max(0, Number(content.amount ?? 0));

/** Routes vapor through a condenser and deposits condensate in its receiver. */
export class CondenserSystem implements EngineSystem {
  constructor(private readonly workspace: Workspace) {}

  update(deltaSeconds: number): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (const condenser of this.workspace.scene.objects.values()) {
      if (condenser.type !== 'condenser') continue;
      const coolant = [...this.workspace.scene.connections.values()].some((connection) =>
        connection.medium === 'liquid' &&
        (connection.from.objectId === condenser.id || connection.to.objectId === condenser.id) &&
        (connection.from.portId.includes('coolant') || connection.to.portId.includes('coolant'))
      );
      const output = [...this.workspace.scene.connections.values()].find((connection) =>
        connection.medium === 'gas' && connection.from.objectId === condenser.id
      );
      const receiver = output ? this.workspace.scene.objects.get(output.to.objectId) : undefined;
      if (!receiver || receiver.properties.broken) continue;
      const vapor = condenser.contents.find(gas);
      if (!vapor || amount(vapor) <= 0) continue;
      const capacity = Number(receiver.properties.capacityMl ?? receiver.metadata.capacity ?? receiver.capabilities.container?.capacity ?? Infinity);
      const available = Math.max(0, capacity - Number(receiver.properties.volumeMl ?? 0));
      const rate = (coolant ? 0.2 : 0.015) * deltaSeconds;
      const condensed = Math.min(amount(vapor), available, rate);
      if (condensed <= 0) continue;
      vapor.amount = amount(vapor) - condensed;
      const liquid = receiver.contents.find((content) => String(content.materialId) === String(vapor.materialId) && (content.phase === 'liquid' || content.phase === 'aqueous'));
      if (liquid) liquid.amount = amount(liquid) + condensed;
      else receiver.contents.push({ ...structuredClone(vapor), amount: condensed, phase: 'liquid', name: String(vapor.name ?? vapor.materialId).replace(/vapor/i, 'condensate') });
      receiver.properties.volumeMl = Number(receiver.properties.volumeMl ?? 0) + condensed;
      receiver.properties.liquidLevel = capacity > 0 ? Number(receiver.properties.volumeMl) / capacity : 0;
      condenser.properties.condensationRateMlPerSecond = coolant ? 0.2 : 0.015;
      const explanation = coolant ? 'Vapor condensed into receiver' : 'Low condensation efficiency: no coolant flow';
      if (condenser.history.at(-1) !== explanation) condenser.history.push(explanation);
      changed = true;
    }
    return changed;
  }
}
