type Vessel = { properties: Record<string, unknown>; contents: Record<string, unknown>[]; material?: Record<string, unknown>; metadata?: Record<string, unknown>; capabilities?: Record<string, any> };
const liquid = (content: Record<string, unknown>) => content.phase === 'liquid' || content.phase === 'aqueous';
const capacity = (vessel: Vessel) => Number(vessel.properties.capacityMl ?? vessel.metadata?.capacity ?? vessel.capabilities?.container?.capacity ?? Infinity);
const syncContents = (vessel: Vessel) => {
  vessel.contents = vessel.contents.filter((content) => Number(content.amount ?? 0) > 0.001);
  vessel.material = vessel.contents[0] as Record<string, unknown> | undefined;
};
export function transferLiquid(source: Vessel, target: Vessel, wanted: number) {
  const sourceVolume = Math.max(0, Number(source.properties.volumeMl ?? 0));
  const targetVolume = Math.max(0, Number(target.properties.volumeMl ?? 0));
  const moved = Math.min(wanted, sourceVolume, Math.max(0, capacity(target) - targetVolume));
  const parts = source.contents.filter(liquid);
  const total = parts.reduce((sum, content) => sum + Math.max(0, Number(content.amount ?? 0)), 0);
  if (moved <= 0 || total <= 0) return 0;
  for (const part of parts) {
    const portion = moved * Math.max(0, Number(part.amount ?? 0)) / total;
    part.amount = Number(part.amount ?? 0) - portion;
    const existing = target.contents.find((content) => String(content.materialId) === String(part.materialId) && content.phase === part.phase);
    if (existing) existing.amount = Number(existing.amount ?? 0) + portion;
    else target.contents.push({ ...structuredClone(part), amount: portion });
  }
  source.properties.volumeMl = sourceVolume - moved;
  target.properties.volumeMl = targetVolume + moved;
  source.properties.liquidLevel = Number(source.properties.volumeMl) / capacity(source);
  target.properties.liquidLevel = Number(target.properties.volumeMl) / capacity(target);
  syncContents(source);
  syncContents(target);
  return moved;
}
export function transferGas(source: Vessel, target: Vessel, wanted: number) {
  const sourceVolume = Math.max(0, Number(source.properties.volumeMl ?? 0));
  const targetVolume = Math.max(0, Number(target.properties.volumeMl ?? 0));
  const moved = Math.min(wanted, sourceVolume, Math.max(0, capacity(target) - targetVolume));
  const parts = source.contents.filter((content) => content.phase === 'gas');
  const total = parts.reduce((sum, content) => sum + Math.max(0, Number(content.amount ?? 0)), 0);
  if (moved <= 0 || total <= 0) return 0;
  for (const part of parts) {
    const portion = moved * Math.max(0, Number(part.amount ?? 0)) / total;
    part.amount = Number(part.amount ?? 0) - portion;
    const existing = target.contents.find((content) => String(content.materialId) === String(part.materialId) && content.phase === 'gas');
    if (existing) existing.amount = Number(existing.amount ?? 0) + portion;
    else target.contents.push({ ...structuredClone(part), amount: portion });
  }
  source.properties.volumeMl = sourceVolume - moved;
  target.properties.volumeMl = targetVolume + moved;
  syncContents(source);
  syncContents(target);
  return moved;
}

export function removeLiquid(source: Vessel, wanted: number) {
  const sourceVolume = Math.max(0, Number(source.properties.volumeMl ?? 0));
  const moved = Math.min(wanted, sourceVolume);
  const parts = source.contents.filter(liquid);
  const total = parts.reduce((sum, content) => sum + Math.max(0, Number(content.amount ?? 0)), 0);
  if (moved <= 0 || total <= 0) return 0;
  for (const part of parts) {
    const portion = moved * Math.max(0, Number(part.amount ?? 0)) / total;
    part.amount = Number(part.amount ?? 0) - portion;
  }
  source.properties.volumeMl = sourceVolume - moved;
  source.properties.liquidLevel = Number(source.properties.volumeMl) / capacity(source);
  syncContents(source);
  return moved;
}

