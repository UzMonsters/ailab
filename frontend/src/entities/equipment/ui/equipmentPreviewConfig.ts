export type EquipmentPreviewNormalization = {
  scale?: number;
  offsetX?: number;
  offsetY?: number;
  objectPosition?: string;
};

export const equipmentPreviewConfig: Record<string, EquipmentPreviewNormalization> = {
  beaker: { scale: 0.86, offsetX: 0, offsetY: 1 },
  erlenmeyer: { scale: 0.82, offsetX: -1, offsetY: 1 },
  roundflask: { scale: 1.08, offsetX: 0, offsetY: -3 },
};

