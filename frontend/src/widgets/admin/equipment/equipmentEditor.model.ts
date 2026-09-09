import type { JsonObject } from '@/shared/api/contracts/platform';
import { CONTENT_LOCALES, type EquipmentCapability, type EquipmentCategory, type EquipmentKind, type Locale, type PortDirection, type PortKind } from '@/shared/types/catalog';
import { canonicalRendererId, hasEquipmentRenderer } from '@/entities/equipment/ui/EquipmentRendererRegistry';
import type { EquipmentDraft, EquipmentPort, EquipmentTranslation, EquipmentValidationIssue } from './equipmentEditor.types';

const protectedKeys = new Set(['id', 'entityType', 'status', 'version', 'publishedVersion', 'publishedAt', 'createdAt', 'updatedAt']);
const emptyTranslation = (): EquipmentTranslation => ({ name: '', shortDescription: '', detailedDescription: '', usageDescription: '', safetyInformation: '', educationalNotes: '' });
const object = (value: unknown): JsonObject => value && typeof value === 'object' && !Array.isArray(value) ? value as JsonObject : {};
const string = (value: unknown, fallback = '') => typeof value === 'string' ? value : fallback;
const numberOrNull = (value: unknown) => typeof value === 'number' && Number.isFinite(value) ? value : null;
const clamp = (value: unknown, fallback: number) => Math.max(0, Math.min(1, typeof value === 'number' ? value : fallback));

function translation(value: unknown): EquipmentTranslation {
  if (typeof value === 'string') return { ...emptyTranslation(), name: value };
  const source = object(value);
  return {
    name: string(source.name ?? source.title),
    shortDescription: string(source.shortDescription ?? source.summary),
    detailedDescription: string(source.detailedDescription ?? source.description),
    usageDescription: string(source.usageDescription ?? source.usage),
    safetyInformation: string(source.safetyInformation ?? source.safety),
    educationalNotes: string(source.educationalNotes),
  };
}

function port(value: unknown, index: number): EquipmentPort {
  const source = object(value);
  const position = object(source.position ?? source.normalizedPosition);
  const portTranslations = object(source.translations);
  return {
    id: string(source.id ?? source.code, `PORT_${index + 1}`),
    name: string(source.name, `Port ${index + 1}`),
    translations: Object.fromEntries(CONTENT_LOCALES.map(locale => [locale, { name: string(object(portTranslations[locale]).name) }])) as EquipmentPort['translations'],
    type: string(source.type, 'FLUID') as PortKind,
    direction: string(source.direction, 'INPUT') as PortDirection,
    medium: string(source.medium, 'LIQUID'),
    connector: string(source.connector ?? source.connectorType, 'standard-open-mouth'),
    allowMultiple: Boolean(source.allowMultiple ?? source.allowMultipleConnections),
    position: { x: clamp(position.x, .5), y: clamp(position.y, .5) },
    maxTemperature: numberOrNull(source.maxTemperature),
    maxPressure: numberOrNull(source.maxPressure),
    maxFlow: numberOrNull(source.maxFlow),
  };
}

export function createEquipmentDraft(record: JsonObject = {}): EquipmentDraft {
  const translations = object(record.translations);
  const raw = Object.fromEntries(Object.entries(record).filter(([key]) => !protectedKeys.has(key)));
  const rendererKey = canonicalRendererId(string(record.rendererKey, 'beaker'));
  const links = object(record.links);
  const wikipedia = object(links.wikipedia);
  return {
    id: string(record.id) || undefined,
    code: string(record.code),
    internalName: string(record.name),
    category: string(record.category, 'CONTAINER') as EquipmentCategory,
    kind: string(record.kind ?? record.equipmentKind) as EquipmentKind | '',
    rendererKey,
    tags: Array.isArray(record.tags) ? record.tags.map(String) : [],
    status: string(record.status, 'DRAFT'),
    version: typeof record.version === 'number' ? record.version : undefined,
    publishedVersion: typeof record.publishedVersion === 'number' ? record.publishedVersion : null,
    translations: Object.fromEntries(CONTENT_LOCALES.map(locale => [locale, translation(translations[locale])])) as Record<Locale, EquipmentTranslation>,
    media: object(record.media) as EquipmentDraft['media'],
    capabilities: Array.isArray(record.capabilities) ? record.capabilities.map(String) as EquipmentCapability[] : [],
    ports: Array.isArray(record.ports) ? record.ports.map(port) : [],
    limits: object(record.limits ?? record.technicalSpecs),
    links: {
      bookId: string(links.bookId), chapterId: string(links.chapterId), pageId: string(links.pageId),
      wikipedia: Object.fromEntries(CONTENT_LOCALES.map(locale => [locale, string(wikipedia[locale])])),
      references: Array.isArray(links.references) ? links.references.map((entry, index) => { const ref = object(entry); return { id: string(ref.id, `reference-${index}`), label: string(ref.label), url: string(ref.url), locale: string(ref.locale) as Locale | '' }; }) : [],
    },
    raw,
  };
}

export function equipmentPayload(draft: EquipmentDraft): JsonObject {
  return {
    ...draft.raw,
    code: draft.code.trim(),
    name: draft.internalName.trim(),
    category: draft.category,
    ...(draft.kind ? { kind: draft.kind } : {}),
    rendererKey: canonicalRendererId(draft.rendererKey),
    tags: draft.tags,
    translations: draft.translations,
    media: draft.media,
    capabilities: draft.capabilities,
    ports: draft.ports.map(item => ({
      id: item.id.trim(), name: item.name.trim(), translations: item.translations,
      type: item.type, direction: item.direction, medium: item.medium,
      connector: item.connector.trim(), allowMultiple: item.allowMultiple,
      position: { x: clamp(item.position.x, .5), y: clamp(item.position.y, .5) },
      ...(item.maxTemperature != null ? { maxTemperature: item.maxTemperature } : {}),
      ...(item.maxPressure != null ? { maxPressure: item.maxPressure } : {}),
      ...(item.maxFlow != null ? { maxFlow: item.maxFlow } : {}),
    })),
    limits: draft.limits,
    links: draft.links,
  };
}

export function validateEquipment(draft: EquipmentDraft): EquipmentValidationIssue[] {
  const issues: EquipmentValidationIssue[] = [];
  if (!draft.code.trim()) issues.push({ id: 'equipment-code', severity: 'error', tab: 'details', message: 'Stable equipment code is required.' });
  if (!draft.category) issues.push({ id: 'equipment-category', severity: 'error', tab: 'details', message: 'Category is required.' });
  if (!draft.rendererKey) issues.push({ id: 'equipment-renderer', severity: 'error', tab: 'details', message: 'Renderer is required.' });
  else if (!hasEquipmentRenderer(draft.rendererKey)) issues.push({ id: 'equipment-renderer', severity: 'error', tab: 'details', message: `Renderer “${draft.rendererKey}” is unavailable in this frontend version.` });
  CONTENT_LOCALES.forEach(locale => {
    const value = draft.translations[locale];
    if (!value.name.trim()) issues.push({ id: `translation-${locale}-name`, severity: 'error', tab: 'localization', message: `${locale.toUpperCase()} name is missing.` });
    if (!value.shortDescription.trim()) issues.push({ id: `translation-${locale}-short`, severity: 'warning', tab: 'localization', message: `${locale.toUpperCase()} short description is missing.` });
  });
  const ids = new Set<string>();
  draft.ports.forEach((item, index) => {
    if (!item.id.trim()) issues.push({ id: `port-${index}-id`, severity: 'error', tab: 'ports', message: `Port ${index + 1} needs a code.` });
    if (ids.has(item.id.trim())) issues.push({ id: `port-${index}-duplicate`, severity: 'error', tab: 'ports', message: `Port code “${item.id}” is duplicated.` });
    ids.add(item.id.trim());
    if (!item.connector.trim()) issues.push({ id: `port-${index}-connector`, severity: 'error', tab: 'ports', message: `Port ${item.id || index + 1} needs a connector.` });
  });
  draft.links.references.forEach((reference, index) => {
    if (reference.url && !isValidHttpUrl(reference.url)) issues.push({ id: `link-${index}`, severity: 'error', tab: 'links', message: `Reference “${reference.label || index + 1}” has an invalid URL.` });
  });
  return issues;
}

export function isValidHttpUrl(value: string) {
  if (!value.trim()) return true;
  try { const url = new URL(value); return url.protocol === 'http:' || url.protocol === 'https:'; } catch { return false; }
}
