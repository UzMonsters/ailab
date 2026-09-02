import { Workspace } from '../workspace/Workspace';
import type { EngineSystem } from './EngineSystem';
import type { AudioSystem } from './AudioSystem';
import { transferGas, transferLiquid, removeLiquid } from './fluidTransfers';

export type ActivePour = { sourceId: string; targetId: string; targetVolume: number; pouredSoFar: number; spilledSoFar: number; rateMlPerSecond: number };
const isLiquidConduit = (object: { type: string }) => object.type === 'funnel';

export class FluidSystem implements EngineSystem {
  private activePours: ActivePour[] = [];
  constructor(private readonly workspace: Workspace, private readonly audio?: AudioSystem) {}
  startPour(sourceId: string, targetId: string, amount: number, rateMlPerSecond = 25) { this.activePours.push({ sourceId, targetId, targetVolume: amount, pouredSoFar: 0, spilledSoFar: 0, rateMlPerSecond }); }
  pourNow(sourceId: string, targetId: string, amount: number) {
    const source = this.workspace.scene.objects.get(sourceId), target = this.workspace.scene.objects.get(targetId);
    if (!source || !target || amount <= 0 || source.properties.broken || target.properties.broken) return false;
    return transferLiquid(source, target, amount) > 0;
  }
  removeLiquid(sourceId: string, amount: number) {
    const source = this.workspace.scene.objects.get(sourceId);
    if (!source || amount <= 0 || source.properties.broken) return false;
    return removeLiquid(source, amount) > 0;
  }
  private releaseToEnvironment(source: any, target: any, amount: number) {
    const liquid = source.contents.find((content: any) => content.phase === 'liquid' || content.phase === 'aqueous');
    const released = removeLiquid(source, amount);
    if (released <= 0) return 0;
    this.workspace.scene.environment.spills.push({
      id: crypto.randomUUID(), materialId: String(liquid?.materialId ?? 'unknown'), amount: released,
      time: Date.now(), x: target.position.x + target.boundingBox.width / 2, y: target.position.y + target.boundingBox.height,
      color: String(liquid?.color ?? ''), sourceId: target.id,
    });
    target.properties.overflowing = true;
    target.properties.lastOverflowAt = Date.now();
    target.history.push(`Liquid released: ${released.toFixed(1)} mL`);
    return released;
  }
  update(deltaSeconds: number) {
    if (!this.workspace.simulation.running) return false;
    let changed = false;
    for (let i = this.activePours.length - 1; i >= 0; i -= 1) {
      const pour = this.activePours[i], source = this.workspace.scene.objects.get(pour.sourceId), target = this.workspace.scene.objects.get(pour.targetId);
      if (!source || !target) { this.activePours.splice(i, 1); continue; }
      
      const liquidContents = source.contents.filter(c => c.phase === 'liquid' || c.phase === 'aqueous');
      const avgViscosity = liquidContents.length > 0 
        ? liquidContents.reduce((sum, c) => sum + Number((c.metadata as any)?.viscosity ?? (c.properties as any)?.viscosity ?? 1), 0) / liquidContents.length 
        : 1;
      const effectiveRate = pour.rateMlPerSecond / Math.max(0.1, avgViscosity);
      
      // Do not keep a scheduled pour alive after its source has run dry.
      const sourceVolume = Math.max(0, Number(source.properties.volumeMl ?? 0));
      const requested = Math.min(effectiveRate * deltaSeconds, pour.targetVolume - pour.pouredSoFar - pour.spilledSoFar, sourceVolume);
      if (requested <= .001) {
        target.properties.overflowing = false;
        this.activePours.splice(i, 1);
        continue;
      }
      const receiver = isLiquidConduit(target)
        ? this.workspace.scene.connections.values().find((connection) =>
          connection.medium === 'liquid' && connection.from.objectId === target.id && connection.from.portId === 'stem',
        )
        : undefined;
      const receiverObject = receiver ? this.workspace.scene.objects.get(receiver.to.objectId) : target;
      const moved = receiverObject && !isLiquidConduit(receiverObject) ? transferLiquid(source, receiverObject, requested) : 0;
      const overflow = Math.max(0, requested - moved);
      if (overflow > 0) {
        const released = this.releaseToEnvironment(source, receiverObject ?? target, overflow);
        if (released > 0) pour.spilledSoFar += released;
      }
      pour.pouredSoFar += moved;
      if (moved > 0 || overflow > 0) { this.audio?.playPour(); changed = true; }
      if (requested <= 0 || pour.pouredSoFar + pour.spilledSoFar >= pour.targetVolume) { target.properties.overflowing = false; this.activePours.splice(i, 1); }
    }
    for (const funnel of this.workspace.scene.objects.values()) {
      if (!isLiquidConduit(funnel)) continue;
      const inlet = [...this.workspace.scene.connections.values()].find((connection) => connection.medium === 'liquid' && connection.to.objectId === funnel.id && connection.to.portId === 'top');
      const outlet = [...this.workspace.scene.connections.values()].find((connection) => connection.medium === 'liquid' && connection.from.objectId === funnel.id && connection.from.portId === 'stem');
      const source = inlet ? this.workspace.scene.objects.get(inlet.from.objectId) : undefined;
      const receiver = outlet ? this.workspace.scene.objects.get(outlet.to.objectId) : undefined;
      if (!source || source.properties.broken || (receiver && receiver.properties.broken)) continue;
      const amount = 15 * deltaSeconds;
      if (receiver && !isLiquidConduit(receiver)) {
        if (transferLiquid(source, receiver, amount) > 0) { changed = true; this.audio?.playPour(); }
      } else if (source && !receiver) {
        if (this.releaseToEnvironment(source, funnel, amount) > 0) { changed = true; this.audio?.playPour(); }
      }
    }
    for (const connection of this.workspace.scene.connections.values()) {
      if (connection.medium !== 'liquid' && connection.medium !== 'gas') continue;
      const source = this.workspace.scene.objects.get(connection.from.objectId), target = this.workspace.scene.objects.get(connection.to.objectId);
      if (!source || !target || source.properties.broken || target.properties.broken) continue;
      if (isLiquidConduit(source) || isLiquidConduit(target)) continue;
      const sourcePort = source.ports.find((port) => port.id === connection.from.portId), targetPort = target.ports.find((port) => port.id === connection.to.portId);
      if (!sourcePort || !targetPort || sourcePort.direction === 'in' || targetPort.direction === 'out') continue;
      if (connection.medium === 'gas') { if (transferGas(source, target, 15 * deltaSeconds) > 0) changed = true; continue; }
      const valve = source.type === 'burette' || source.type === 'pipette' ? Number(source.properties.valveOpening ?? 0) : 1;
      const pressureDriven = Number(source.properties.pressureBar ?? 1) > Number(target.properties.pressureBar ?? 1) + .03;
      if (valve <= 0 || (source.position.y >= target.position.y && !pressureDriven)) continue;
      
      const liquidContents = source.contents.filter(c => c.phase === 'liquid' || c.phase === 'aqueous');
      const avgViscosity = liquidContents.length > 0 
        ? liquidContents.reduce((sum, c) => sum + Number((c.metadata as any)?.viscosity ?? (c.properties as any)?.viscosity ?? 1), 0) / liquidContents.length 
        : 1;
      const effectiveRate = 15 / Math.max(0.1, avgViscosity);
      
      if (transferLiquid(source, target, effectiveRate * valve * deltaSeconds) > 0) changed = true;
    }
    return changed;
  }
}
