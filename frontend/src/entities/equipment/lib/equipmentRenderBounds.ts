export type NormalizedPoint = { x: number; y: number };

export type EquipmentRenderBounds = {
  left: number;
  top: number;
  width: number;
  height: number;
};

/** The catalog port coordinates are normalized against the visible equipment art. */
export const EQUIPMENT_RENDER_BOUNDS: EquipmentRenderBounds = {
  left: 0.1,
  top: 0.1,
  width: 0.8,
  height: 0.8,
};

const clamp = (value: number) => Math.max(0, Math.min(1, Number.isFinite(value) ? value : 0.5));

export function resolvePortScreenPosition(point: NormalizedPoint, bounds = EQUIPMENT_RENDER_BOUNDS): NormalizedPoint {
  return {
    x: bounds.left + clamp(point.x) * bounds.width,
    y: bounds.top + clamp(point.y) * bounds.height,
  };
}

export function resolvePortNormalizedPosition(point: NormalizedPoint, bounds = EQUIPMENT_RENDER_BOUNDS): NormalizedPoint {
  return {
    x: clamp((point.x - bounds.left) / bounds.width),
    y: clamp((point.y - bounds.top) / bounds.height),
  };
}

export function resolvePortWorldPosition(
  point: NormalizedPoint,
  frame: { x: number; y: number; width: number; height: number; rotation?: number },
  bounds = EQUIPMENT_RENDER_BOUNDS,
): NormalizedPoint {
  const anchor = resolvePortScreenPosition(point, bounds);
  const localX = anchor.x * frame.width - frame.width / 2;
  const localY = anchor.y * frame.height - frame.height / 2;
  const radians = (frame.rotation ?? 0) * Math.PI / 180;
  return {
    x: frame.x + frame.width / 2 + localX * Math.cos(radians) - localY * Math.sin(radians),
    y: frame.y + frame.height / 2 + localX * Math.sin(radians) + localY * Math.cos(radians),
  };
}
