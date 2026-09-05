import type { JsonObject } from '@/shared/api/contracts/platform';
import type { EquipmentCapability, EquipmentCategory, EquipmentKind, Locale, PortDirection, PortKind } from '@/shared/types/catalog';

export type EquipmentTranslation = {
  name: string;
  shortDescription: string;
  detailedDescription: string;
  usageDescription: string;
  safetyInformation: string;
  educationalNotes: string;
};

export type EquipmentAsset = {
  assetId: string;
  url: string;
  filename: string;
  mimeType: string;
  sizeBytes: number;
  width?: number;
  height?: number;
};

export type EquipmentMedia = {
  thumbnail?: EquipmentAsset;
  imageLight?: EquipmentAsset;
  imageDark?: EquipmentAsset;
  svgLight?: EquipmentAsset;
  svgDark?: EquipmentAsset;
};

export type EquipmentPort = {
  id: string;
  name: string;
  translations: Partial<Record<Locale, { name: string }>>;
  type: PortKind;
  direction: PortDirection;
  medium: string;
  connector: string;
  allowMultiple: boolean;
  position: { x: number; y: number };
  maxTemperature?: number | null;
  maxPressure?: number | null;
  maxFlow?: number | null;
};

export type EquipmentLinks = {
  bookId: string;
  chapterId: string;
  pageId: string;
  wikipedia: Partial<Record<Locale, string>>;
  references: Array<{ id: string; label: string; url: string; locale: Locale | '' }>;
};

export type EquipmentDraft = {
  id?: string;
  code: string;
  internalName: string;
  category: EquipmentCategory;
  kind: EquipmentKind | '';
  rendererKey: string;
  tags: string[];
  status: string;
  version?: number;
  publishedVersion?: number | null;
  translations: Record<Locale, EquipmentTranslation>;
  media: EquipmentMedia;
  capabilities: EquipmentCapability[];
  ports: EquipmentPort[];
  limits: JsonObject;
  links: EquipmentLinks;
  raw: JsonObject;
};

export type EquipmentValidationIssue = {
  id: string;
  severity: 'error' | 'warning';
  tab: string;
  message: string;
};
