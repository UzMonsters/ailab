import type { Engine } from '@/engine/core/Engine';
import type { SimulationExecutionResult } from '@/types';

type RecordValue = Record<string, unknown>;
const record = (value: unknown): RecordValue => value && typeof value === 'object' && !Array.isArray(value) ? value as RecordValue : {};
const rows = (value: unknown): RecordValue[] => Array.isArray(value) ? value.map(record) : [];
const number = (value: unknown): number | undefined => typeof value === 'number' && Number.isFinite(value) ? value : undefined;
const strings = (value: unknown): string[] => Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];

export type ReconciledSimulationFacts = { reactionIds: string[]; formedMaterialIds: string[] };

/** Applies backend-authoritative scientific fields without replacing the visual scene graph. */
export function reconcileSimulationResult(engine: Engine, result: SimulationExecutionResult): ReconciledSimulationFacts {
  const delta = record(result.stateDelta);
  const reactionIds = new Set(strings(delta.reactionIds));
  const formedMaterialIds = new Set(strings(delta.formedMaterialIds));
  const objects = [...rows(delta.objects), ...rows(delta.items), ...rows(delta.containers), ...rows(delta.apparatus)];
  let changed = false;
  objects.forEach((change) => {
    const id = String(change.id ?? change.objectId ?? change.vesselId ?? '');
    const object = id ? engine.workspace.scene.objects.get(id) : undefined;
    if (!object) return;
    const properties = record(change.properties);
    const temperature = number(change.temperatureC ?? change.temperature ?? properties.temperatureC ?? properties.temperature);
    const pressure = number(change.pressureBar ?? change.pressure ?? properties.pressureBar ?? properties.pressure);
    const volume = number(change.volumeMl ?? change.volume ?? properties.volumeMl ?? properties.volume);
    const ph = number(change.ph ?? change.pH ?? properties.ph ?? properties.pH);
    if (temperature !== undefined) { object.properties.temperature = temperature; changed = true; }
    if (pressure !== undefined) { object.properties.pressureBar = pressure; changed = true; }
    if (volume !== undefined) { object.properties.volumeMl = volume; changed = true; }
    if (ph !== undefined) { object.properties.pH = ph; changed = true; }
    const contents = rows(change.contents ?? change.materials);
    if (contents.length) {
      object.contents = contents.map((content) => {
        const materialId = String(content.materialId ?? content.id ?? content.code ?? '');
        const reactionId = typeof content.reactionId === 'string' ? content.reactionId : typeof delta.reactionId === 'string' ? delta.reactionId : undefined;
        if (reactionId) reactionIds.add(reactionId);
        if (content.formed === true || content.source === 'reaction' || reactionId) formedMaterialIds.add(materialId);
        return { ...content, materialId, amount: number(content.amount ?? content.quantity) ?? 0, unit: String(content.unit ?? 'mL'), phase: String(content.phase ?? 'liquid'), metadata: { ...record(content.metadata), ...(reactionId ? { reactionId, source: 'backend-reaction' } : {}) } };
      });
      changed = true;
    }
  });
  const reactionId = typeof delta.reactionId === 'string' ? delta.reactionId : '';
  if (reactionId) reactionIds.add(reactionId);
  if (changed) engine.notifyUpdate();
  return { reactionIds: [...reactionIds], formedMaterialIds: [...formedMaterialIds] };
}
