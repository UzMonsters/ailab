import type { Item } from './types';

export type TransferAnimationKind = 'beaker' | 'flask' | 'pipette' | 'funnel' | 'cylinder';

export interface TransferAnimationProfile {
  kind: TransferAnimationKind;
  durationMs: number;
  arcLift: number;
  streamWidth: number;
}

const PROFILES: Record<TransferAnimationKind, Omit<TransferAnimationProfile, 'durationMs'>> = {
  beaker: { kind: 'beaker', arcLift: 64, streamWidth: 6 },
  flask: { kind: 'flask', arcLift: 82, streamWidth: 5 },
  pipette: { kind: 'pipette', arcLift: 30, streamWidth: 3 },
  funnel: { kind: 'funnel', arcLift: 18, streamWidth: 4 },
  cylinder: { kind: 'cylinder', arcLift: 72, streamWidth: 4 },
};

export function getTransferAnimationProfile(item: Pick<Item, 'type'> | undefined, amountMl: number): TransferAnimationProfile {
  const type = String(item?.type ?? '').toLowerCase();
  const kind: TransferAnimationKind = type.includes('pipette')
    ? 'pipette'
    : type.includes('funnel')
      ? 'funnel'
      : type.includes('cylinder') || type.includes('burette')
        ? 'cylinder'
        : type.includes('flask') || type.includes('erlenmeyer') || type.includes('volumetric')
          ? 'flask'
          : 'beaker';
  const base = PROFILES[kind];
  const speed = kind === 'pipette' ? 48 : kind === 'funnel' ? 34 : 40;
  return { ...base, durationMs: Math.max(900, Math.min(3200, 700 + (amountMl / speed) * 1000)) };
}
