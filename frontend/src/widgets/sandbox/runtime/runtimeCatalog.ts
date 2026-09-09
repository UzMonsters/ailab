import type { JsonObject } from '@/shared/api/contracts/platform';
import type { RuntimeCatalog, RuntimeEquipment, RuntimeLocale, RuntimeMaterial } from './runtime.types';

const object = (value: unknown): JsonObject => value && typeof value === 'object' && !Array.isArray(value) ? value as JsonObject : {};
const list = (value: unknown): JsonObject[] => Array.isArray(value) ? value.filter((item): item is JsonObject => Boolean(item) && typeof item === 'object' && !Array.isArray(item)) : [];
const text = (value: unknown, fallback = '') => typeof value === 'string' && value.trim() ? value : fallback;

export const emptyRuntimeCatalog = (): RuntimeCatalog => ({ equipment: [], materials: [], equipmentById: {}, materialsById: {} });

const equipmentDefinition = (raw: JsonObject): RuntimeEquipment => ({
  id: text(raw.id, text(raw.code)), code: text(raw.code, text(raw.id)), rendererKey: text(raw.rendererKey, text(raw.type, 'unsupported')),
  ports: list(raw.ports), capabilities: Array.isArray(raw.capabilities) ? raw.capabilities.map(String) : [], translations: object(raw.translations), raw,
});
const materialDefinition = (raw: JsonObject): RuntimeMaterial => { const appearance = object(raw.appearance); return {
  id: text(raw.id, text(raw.code)), code: text(raw.code, text(raw.id)), formula: text(raw.formula, text(raw.code)), phase: text(raw.phase, text(raw.defaultPhase, 'LIQUID')),
  color: text(appearance.color, text(raw.color, '#94a3b8')), opacity: Number.isFinite(Number(appearance.opacity ?? raw.opacity)) ? Number(appearance.opacity ?? raw.opacity) : 1,
  translations: object(raw.translations), raw,
}; };

export function createRuntimeCatalog(equipmentRows: JsonObject[] = [], materialRows: JsonObject[] = []): RuntimeCatalog {
  const equipment = equipmentRows.map(equipmentDefinition).filter((item) => item.id);
  const materials = materialRows.map(materialDefinition).filter((item) => item.id);
  const equipmentById: Record<string, RuntimeEquipment> = {}, materialsById: Record<string, RuntimeMaterial> = {};
  equipment.forEach((item) => { equipmentById[item.id] = item; if (item.code) equipmentById[item.code] = item; });
  materials.forEach((item) => { materialsById[item.id] = item; if (item.code) materialsById[item.code] = item; });
  return { equipment, materials, equipmentById, materialsById };
}

export function runtimeCatalogFromPayload(payload: JsonObject, scenario: JsonObject): RuntimeCatalog {
  const catalog = object(payload.runtimeCatalog ?? payload.catalog ?? object(payload.runtime).catalog);
  const resources = object(scenario.resources);
  const equipment = list(catalog.equipment ?? payload.equipment ?? payload.equipmentCatalog ?? resources.equipment);
  const materials = list(catalog.materials ?? payload.materials ?? payload.materialCatalog ?? resources.materials);
  return createRuntimeCatalog(equipment, materials);
}

export const getRuntimeEquipment = (catalog: RuntimeCatalog, id: string) => catalog.equipmentById[id];
export const getRuntimeEquipmentRenderer = (catalog: RuntimeCatalog, id: string) => getRuntimeEquipment(catalog, id)?.rendererKey;
export const getRuntimeEquipmentPorts = (catalog: RuntimeCatalog, id: string) => getRuntimeEquipment(catalog, id)?.ports ?? [];
export const getRuntimeMaterial = (catalog: RuntimeCatalog, id: string) => catalog.materialsById[id];
export function getRuntimeEquipmentTranslation(catalog: RuntimeCatalog, id: string, locale: RuntimeLocale) { const item = getRuntimeEquipment(catalog, id); const copy = object(item?.translations[locale] ?? item?.translations.en ?? item?.translations.ru ?? item?.translations.uz); return text(copy.name ?? copy.title, item?.code ?? id); }
export function getRuntimeMaterialTranslation(catalog: RuntimeCatalog, id: string, locale: RuntimeLocale) { const item = getRuntimeMaterial(catalog, id); const copy = object(item?.translations[locale] ?? item?.translations.en ?? item?.translations.ru ?? item?.translations.uz); return text(copy.name ?? copy.title, item?.formula ?? item?.code ?? id); }
