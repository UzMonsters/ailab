export type RuntimeUnitDimension = 'volume' | 'mass' | 'amount' | 'temperature' | 'pressure';

const units: Record<string, { dimension: RuntimeUnitDimension; factor: number; offset?: number }> = {
  'µL': { dimension: 'volume', factor: .001 }, uL: { dimension: 'volume', factor: .001 }, mL: { dimension: 'volume', factor: 1 }, L: { dimension: 'volume', factor: 1000 },
  mg: { dimension: 'mass', factor: .001 }, g: { dimension: 'mass', factor: 1 }, kg: { dimension: 'mass', factor: 1000 },
  mmol: { dimension: 'amount', factor: .001 }, mol: { dimension: 'amount', factor: 1 },
  '°C': { dimension: 'temperature', factor: 1 }, C: { dimension: 'temperature', factor: 1 }, K: { dimension: 'temperature', factor: 1, offset: -273.15 },
  bar: { dimension: 'pressure', factor: 1 }, kPa: { dimension: 'pressure', factor: .01 }, Pa: { dimension: 'pressure', factor: .00001 },
};

export const runtimeUnitDimension = (unit: string) => units[unit]?.dimension ?? null;
export const areRuntimeUnitsCompatible = (from: string, to: string) => Boolean(units[from] && units[to] && units[from].dimension === units[to].dimension);
export function convertRuntimeUnit(value: number, from: string, to: string): number | null {
  const source = units[from], target = units[to];
  if (!Number.isFinite(value) || !source || !target || source.dimension !== target.dimension) return null;
  const base = value * source.factor + (source.offset ?? 0);
  return (base - (target.offset ?? 0)) / target.factor;
}
